package com.amazon.external.elasticmapreduce.s3distcp;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;

public class FileInfoListing {
  private static final Log LOG = LogFactory.getLog(FileInfoListing.class);
  private FileSystem fs;
  private SequenceFile.Writer writer;
  private Long fileIndex = Long.valueOf(0L);
  private long recordIndex = 0L;
  private Long recordsInThisFile = Long.valueOf(0L);
  private Long recordsPerFile;
  private Path tmpDir;
  private Configuration conf;
  private Path outputDir;
  private Path defaultSrcDir;
  private Pattern srcPattern;
  private Pattern groupBy;
  private OutputStream manifestStream;
  private Map<String, ManifestEntry> previousManifest;
  private final Gson gson = new Gson();

  public FileInfoListing(Configuration conf, Path srcDir, Path tmpDir, Path outputDir, long startingIndex,
      File manifestFile, Map<String, ManifestEntry> previousManifest) throws IOException {
    this.conf = conf;
    this.defaultSrcDir = srcDir;
    this.tmpDir = tmpDir;
    this.outputDir = outputDir;
    this.recordsPerFile = Long.valueOf(500000L);
    this.recordIndex = startingIndex;
    this.previousManifest = previousManifest;
    if (manifestFile != null)
      this.manifestStream = new GZIPOutputStream(new FileOutputStream(manifestFile));
  }

  public void openNewFile() {
    try {
      if (this.writer != null) {
        this.writer.close();
      }
      this.fileIndex = Long.valueOf(this.fileIndex.longValue() + 1L);
      this.recordsInThisFile = Long.valueOf(0L);
      this.fs = FileSystem.get(this.tmpDir.toUri(), this.conf);
      Path path = new Path(this.tmpDir, this.fileIndex.toString());
      LOG.info(new StringBuilder().append("Opening new file: ").append(path.toString()).toString());
      this.writer = SequenceFile.createWriter(this.fs, this.conf, path, LongWritable.class, FileInfo.class,
          SequenceFile.CompressionType.NONE);
    } catch (IOException e) {
      throw new RuntimeException(new StringBuilder().append("Unable to open new file for writing")
          .append(new Path(this.tmpDir, this.fileIndex.toString()).toString()).toString(), e);
    }
  }

  public void add(Path filePath, long fileSize) {
    add(filePath, this.defaultSrcDir, fileSize);
  }

  public void add(Path filePath, Path srcDir, long fileSize) {
    String filePathString = filePath.toString();
    if (this.srcPattern != null) {
      Matcher matcher = this.srcPattern.matcher(filePathString);
      if (!matcher.matches()) {
        return;
      }
    }

    if (this.groupBy != null) {
      Matcher matcher = this.groupBy.matcher(filePathString);
      if (!matcher.matches()) {
        return;
      }
      int numGroups = matcher.groupCount();
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < numGroups; i++) {
        builder.append(matcher.group(i + 1));
      }
      if (builder.toString().length() == 0) {
        return;
      }

    }

    if ((this.writer == null) || (this.recordsInThisFile.longValue() > this.recordsPerFile.longValue())) {
      openNewFile();
    }
    this.recordIndex += 1L;
    this.recordsInThisFile = Long.valueOf(this.recordsInThisFile.longValue() + 1L);
    String outputFilePath = getOutputFilePath(filePath, srcDir);
    String basePath = getBaseName(filePath, srcDir);
    String manifestSrcDir = this.outputDir.toString();
    try {
      FileInfo fileInfo = new FileInfo(Long.valueOf(this.recordIndex), filePathString, outputFilePath, fileSize);

      LOG.debug(new StringBuilder().append("Adding ").append(fileInfo).toString());
      if ((this.previousManifest != null) && (this.previousManifest.containsKey(basePath))
          && (((ManifestEntry) this.previousManifest.get(basePath)).size == fileSize)) {
        outputFilePath = ((ManifestEntry) this.previousManifest.get(basePath)).path;
        manifestSrcDir = ((ManifestEntry) this.previousManifest.get(basePath)).srcDir;
      } else {
        this.writer.append(new LongWritable(this.recordIndex), fileInfo);
      }
      if (this.manifestStream != null) {
        ManifestEntry entry = new ManifestEntry(URLDecoder.decode(outputFilePath, "UTF-8"),
            URLDecoder.decode(basePath, "UTF-8"), manifestSrcDir, fileSize);

        String outLine = new StringBuilder().append(this.gson.toJson(entry)).append("\n").toString();
        this.manifestStream.write(outLine.getBytes("utf-8"));
      }
    } catch (IOException e) {
      throw new RuntimeException(
          new StringBuilder().append("Unable to write file copy entry ").append(filePathString).toString(), e);
    }
  }

  private String getBaseName(Path filePath, Path srcDir) {
    String filePathString = filePath.toString();
    String suffix = filePathString;
    String srcDirString = srcDir.toString();
    if (filePathString.startsWith(srcDirString)) {
      suffix = filePathString.substring(srcDirString.length());
      if (suffix.startsWith("/")) {
        suffix = suffix.substring(1);
      }
    }
    return suffix;
  }

  private String getOutputFilePath(Path filePath, Path srcDir) {
    String suffix = getBaseName(filePath, srcDir);
    LOG.debug(new StringBuilder().append("outputDir: '").append(this.outputDir).append("'").toString());
    LOG.debug(new StringBuilder().append("suffix: '").append(suffix).append("'").toString());
    LOG.debug(
        new StringBuilder().append("Output path: '").append(new Path(this.outputDir, suffix).toString()).toString());
    return new Path(this.outputDir, suffix).toString();
  }

  public void close() {
    try {
      if (this.writer != null) {
        this.writer.close();
      }
      if (this.manifestStream != null)
        this.manifestStream.close();
    } catch (IOException e) {
      throw new RuntimeException("Unable to close fileInfo writer", e);
    }
  }

  public Long getRecordsPerFile() {
    return this.recordsPerFile;
  }

  public void setRecordsPerFile(Long recordsPerFile) {
    this.recordsPerFile = recordsPerFile;
  }

  public Pattern getSrcPattern() {
    return this.srcPattern;
  }

  public void setSrcPattern(Pattern srcPattern) {
    this.srcPattern = srcPattern;
  }

  public Pattern getGroupBy() {
    return this.groupBy;
  }

  public void setGroupBy(Pattern groupBy) {
    this.groupBy = groupBy;
  }

  public Long getFileIndex() {
    return this.fileIndex;
  }

  public Long getRecordIndex() {
    return Long.valueOf(this.recordIndex);
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.FileInfoListing JD-Core
 * Version: 0.6.2
 */