package com.amazon.external.elasticmapreduce.s3distcp;

import com.google.common.collect.Lists;
//import com.hadoop.compression.lzo.LzopCodec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;

public class CopyFilesReducer implements Reducer<Text, FileInfo, Text, Text> {
  private static final Log LOG = LogFactory.getLog(CopyFilesReducer.class);
  private static final List<String> validCodecs = Lists
      .newArrayList(new String[] { "snappy", "gz", "lzo", "lzop", "gzip" });
  private OutputCollector<Text, Text> collector;
  private Reporter reporter;
  private SimpleExecutor transferQueue;
  private Set<FileInfo> uncommitedFiles;
  private String tempDir;
  private long targetSize;
  private int bufferSize;
  private int numTransferRetries;
  private int multipartSize;
  private String outputCodec;
  private boolean deleteOnSuccess;
  private boolean useMultipartUpload;
  private boolean numberFiles;
  private boolean groupWithNewLine;
  private int numberDeletePartition;
  private JobConf conf;

  public void close() throws IOException {
    this.transferQueue.close();
    synchronized (this) {
      LOG.info("CopyFilesReducer uncommitted file " + this.uncommitedFiles.size());
      for (FileInfo fileInfo : this.uncommitedFiles) {
        LOG.warn("failed to upload " + fileInfo.inputFileName);
        this.collector.collect(fileInfo.outputFileName, fileInfo.inputFileName);
      }

      if (this.uncommitedFiles.size() > 0) {
        String message = String.format("Reducer task failed to copy %d files: %s etc",
            new Object[] { Integer.valueOf(this.uncommitedFiles.size()),
                ((FileInfo) this.uncommitedFiles.iterator().next()).inputFileName });

        throw new RuntimeException(message);
      }
    }
  }

  public JobConf getConf() {
    return this.conf;
  }

  public boolean shouldDeleteOnSuccess() {
    return this.deleteOnSuccess;
  }

  public boolean shouldUseMutlipartUpload() {
    return this.useMultipartUpload;
  }

  public int getMultipartSize() {
    return this.multipartSize;
  }

  public void configure(JobConf conf) {
    this.conf = conf;
    int queueSize = conf.getInt("s3DistCp.copyfiles.mapper.queueSize", 10);
    int numWorkers = conf.getInt("s3DistCp.copyfiles.mapper.numWorkers", 5);
    this.tempDir = (conf.get("s3DistCp.copyfiles.reducer.tempDir", "hdfs:///tmp") + "/" + "tempspace");
    this.bufferSize = conf.getInt("s3DistCp.copyfiles.mapper.bufferSize", 1048576);
    this.targetSize = conf.getLong("s3DistCp.copyfiles.reducer.targetSize", 9223372036854775807L);
    this.outputCodec = conf.get("s3DistCp.copyfiles.reducer.outputCodec").toLowerCase();
    this.numberFiles = conf.getBoolean("s3DistCp.copyfiles.reducer.numberFiles", false);
    this.transferQueue = new SimpleExecutor(queueSize, numWorkers);
    this.multipartSize = conf.getInt("s3DistCp.copyFiles.multipartUploadPartSize", 16777216);
    this.uncommitedFiles = new HashSet();
    this.deleteOnSuccess = conf.getBoolean("s3DistCp.copyFiles.deleteFilesOnSuccess", false);
    this.numTransferRetries = conf.getInt("s3DistCp.copyfiles.mapper.numRetries", 10);
    this.useMultipartUpload = conf.getBoolean("s3DistCp.copyFiles.useMultipartUploads", true);
    this.groupWithNewLine = conf.getBoolean("s3DistCp.groupWithNewLine", false);
    this.numberDeletePartition = conf.getInt("s3DistCp.numberDeletePartition", 0);
  }

  public int getNumTransferRetries() {
    return this.numTransferRetries;
  }

  public int getBufferSize() {
    return this.bufferSize;
  }

  public boolean shouldReencodeFiles() {
    return validCodecs.contains(this.outputCodec);
  }

  private String makeFinalPath(long fileUid, String finalDir, String groupId, String groupIndex) {
    String[] groupIds = groupId.split("/");
    groupId = groupIds[(groupIds.length - 1)];

    groupIndex = "";

    if (this.numberFiles) {
      groupId = fileUid + groupId;
    }

    if (!this.outputCodec.equalsIgnoreCase("keep")) {
      String suffix;
      if (this.outputCodec.equalsIgnoreCase("gzip")) {
        suffix = groupIndex + ".gz";
      } else {
        if (this.outputCodec.equalsIgnoreCase("none"))
          suffix = groupIndex;
        else
          suffix = groupIndex + "." + this.outputCodec;
      }

      return deleteDir(finalDir, this.numberDeletePartition) + "/" + Utils.replaceSuffix(groupId, suffix);
    }
    String suffix = Utils.getSuffix(groupId);
    if (this.outputCodec.equalsIgnoreCase("gzip") || this.outputCodec.equalsIgnoreCase("gz")) {
      suffix = suffix + ".gz";
    }

    String name = groupId;
    if (groupIndex.length() > 0) {
      name = Utils.replaceSuffix(name, groupIndex);
      if (suffix.length() > 0) {
        name = name + "." + suffix;
      }
    }

    return deleteDir(finalDir, this.numberDeletePartition) + "/" + name;
  }

  public void reduce(Text groupKey, Iterator<FileInfo> fileInfos, OutputCollector<Text, Text> collector,
      Reporter reporter) throws IOException {
    this.collector = collector;
    this.reporter = reporter;
    long curSize = 0L;
    int groupNum = 0;
    int numFiles = 0;
    List curFiles = new ArrayList();
    while (fileInfos.hasNext()) {
      FileInfo fileInfo = ((FileInfo) fileInfos.next()).clone();
      numFiles++;
      curSize += fileInfo.fileSize.get();
      curFiles.add(fileInfo);
      if (curSize >= this.targetSize) {
        String groupId = groupKey.toString();
        Path tempPath = new Path(this.tempDir + "/" + groupId);
        Path finalPath = new Path(fileInfo.outputFileName.toString()).getParent();
        String groupIndex = Integer.toString(groupNum);
        if ((numFiles == 1) && (!fileInfos.hasNext())) {
          groupIndex = "";
        }

        finalPath = new Path(makeFinalPath(fileInfo.fileUID.get(), finalPath.toString(), groupId, groupIndex));
        LOG.info("tempPath:" + tempPath + " finalPath:" + finalPath);
        executeDownloads(this, curFiles, tempPath, finalPath);
        groupNum++;
        curFiles = new ArrayList();
        curSize = 0L;
      }
    }
    if (!curFiles.isEmpty()) {
      String groupId = groupKey.toString();
      Path tempPath = new Path(this.tempDir + "/" + UUID.randomUUID());
      Path intermediateFinal = new Path(((FileInfo) curFiles.get(0)).outputFileName.toString()).getParent();
      LOG.info("tempPath:" + tempPath + " interPath:" + intermediateFinal);
      String groupIndex = Integer.toString(groupNum);
      if (numFiles == 1) {
        groupIndex = "";
      }
      Path finalPath = new Path(
          makeFinalPath(((FileInfo) curFiles.get(0)).fileUID.get(), intermediateFinal.toString(), groupId, groupIndex));
      executeDownloads(this, curFiles, tempPath, finalPath);
    }
  }

  private String deleteDir(String pathStr, int l) {
    for (int i = 0; i < l; i++) {
      pathStr = pathStr.substring(0, pathStr.lastIndexOf("/"));
    }
    return pathStr;
  }

  private void executeDownloads(CopyFilesReducer reducer, List<FileInfo> fileInfos, Path tempPath, Path finalPath) {
    synchronized (this) {
      for (FileInfo fileInfo : fileInfos) {
        this.uncommitedFiles.add(fileInfo);
        LOG.info("Processing object: " + fileInfo.inputFileName.toString());
      }
    }
    if (fileInfos.size() > 0) {
      LOG.info("Processing " + fileInfos.size() + " files");
      this.transferQueue.execute(new CopyFilesRunable(reducer, fileInfos, tempPath, finalPath, this.groupWithNewLine));
    } else {
      LOG.info("No files to process");
    }
  }

  public void markFileAsCommited(FileInfo fileInfo) {
    LOG.info("commit " + fileInfo.inputFileName);
    synchronized (this) {
      this.uncommitedFiles.remove(fileInfo);
      progress();
    }
  }

  public InputStream openInputStream(Path inputFilePath) throws IOException {
    FileSystem inputFs = inputFilePath.getFileSystem(this.conf);
    InputStream inputStream = inputFs.open(inputFilePath);

    if (!this.outputCodec.equalsIgnoreCase("keep")) {
      String suffix = Utils.getSuffix(inputFilePath.getName());
      if (suffix.equalsIgnoreCase("gz"))
        return new GZIPInputStream(inputStream);
      if (suffix.equalsIgnoreCase("snappy")) {
        SnappyCodec codec = new SnappyCodec();
        codec.setConf(getConf());
        return codec.createInputStream(inputStream);
      }

      // if ((suffix.equalsIgnoreCase("lzop")) || (suffix.equalsIgnoreCase("lzo"))) {
      // LzopCodec codec = new LzopCodec();
      // codec.setConf(getConf());
      // return codec.createInputStream(inputStream);
      // }
    }
    return inputStream;
  }

  public OutputStream openOutputStream(Path outputFilePath) throws IOException {
    FileSystem outputFs = outputFilePath.getFileSystem(this.conf);
    OutputStream outputStream = outputFs.create(outputFilePath, this.reporter);
    if ((this.outputCodec.equalsIgnoreCase("gzip")) || (this.outputCodec.equalsIgnoreCase("gz")))
      return new GZIPOutputStream(outputStream);
    // if (this.outputCodec.equalsIgnoreCase("lzo")) {
    // LzopCodec codec = new LzopCodec();
    // codec.setConf(getConf());
    // return codec.createOutputStream(outputStream);
    // }
    if (this.outputCodec.equalsIgnoreCase("snappy")) {
      SnappyCodec codec = new SnappyCodec();
      codec.setConf(getConf());
      return codec.createOutputStream(outputStream);
    }
    return outputStream;
  }

  public Progressable getProgressable() {
    return this.reporter;
  }

  public void progress() {
    this.reporter.progress();
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.CopyFilesReducer JD-Core
 * Version: 0.6.2
 */