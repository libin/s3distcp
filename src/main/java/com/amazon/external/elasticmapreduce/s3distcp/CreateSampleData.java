package com.amazon.external.elasticmapreduce.s3distcp;

import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CreateSampleData implements Tool {
  private static final Log LOG = LogFactory.getLog(CreateSampleData.class);
  protected JobConf conf;

  public static void main(String[] args) throws Exception {
    JobConf job = new JobConf(CreateSampleData.class);
    CreateSampleData distcp = new CreateSampleData(job);
    int result = ToolRunner.run(distcp, args);
    System.exit(result);
  }

  public CreateSampleData(JobConf conf) {
    this.conf = new JobConf(conf);
  }

  void createFileList(Path inputFileListPath, String inputLocation, URI inputUri, URI outputUri) throws IOException {
    FileSystem inputFS = FileSystem.get(inputUri, this.conf);
    FileSystem inputFileListFS = FileSystem.get(inputFileListPath.toUri(), this.conf);
    Path inputPath = new Path(inputLocation);

    LongWritable uid = new LongWritable(1L);

    inputFileListFS.delete(inputFileListPath, true);
    inputFileListFS.mkdirs(inputFileListPath);
    SequenceFile.Writer fileInfoWriter = SequenceFile.createWriter(inputFileListFS, this.conf, inputFileListPath,
        LongWritable.class, FileInfo.class, SequenceFile.CompressionType.NONE);
    try {
      FileStatus[] contents = inputFS.listStatus(inputPath);
      for (FileStatus child : contents) {
        String inputFilePath = child.getPath().toString();
        String outputFilePath = join(outputUri.toString(), child.getPath().getName());
        FileInfo info = new FileInfo(Long.valueOf(uid.get()), inputFilePath, outputFilePath, child.getLen());
        fileInfoWriter.append(uid, info);
        uid.set(uid.get() + 1L);
      }
    } finally {
      fileInfoWriter.close();
    }

    FileStatus[] fileListContents = inputFileListFS.listStatus(inputFileListPath);
    for (FileStatus status : fileListContents)
      LOG.info("fileListContents: " + status.getPath());
  }

  private void createInputFiles(String inputPathString, long numFiles, long fileSize, String outputPath) {
    try {
      FileSystem fs = FileSystem.get(new URI(inputPathString), this.conf);
      fs.mkdirs(new Path(inputPathString));
      for (int fileNumber = 1; fileNumber <= numFiles; fileNumber++) {
        String inputFileName = join(inputPathString, Integer.valueOf(fileNumber));
        Path inputFilePath = new Path(inputFileName);
        fs.delete(inputFilePath, true);
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, this.conf, inputFilePath, LongWritable.class,
            CreateFileInfo.class, SequenceFile.CompressionType.NONE);
        try {
          writer.append(new LongWritable(fileNumber),
              new CreateFileInfo(join(outputPath, Integer.valueOf(fileNumber)), fileSize));
        } finally {
          writer.close();
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Configuration getConf() {
    return this.conf;
  }

  private String join(String s, Integer t) {
    return join(s, t.toString());
  }

  private String join(String s, String t) {
    if ((s.length() != 0) && (s.charAt(s.length() - 1) == '/')) {
      return s + t;
    }
    return s + "/" + t;
  }

  public int run(String[] args) throws Exception {
    String outputLocation = args[0];

    long numFiles = this.conf.getLong("createSampleData.numFiles", 5L);
    long fileSize = this.conf.getLong("createSampleData.fileSize", 104857600L);
    String jobName = this.conf.get("createSampleData.baseJobName", "CreateSampleData");
    String tmpPathString = this.conf.get("createSampleData.tmpDir", "hdfs:///tmp/createSampleData");
    String inputPathString = this.conf.get("createSampleData.workingInputDir", join(tmpPathString, "input"));
    String outputPathString = this.conf.get("createSampleData.workingOutputDir", join(tmpPathString, "output"));

    FileSystem.get(new URI(outputPathString), this.conf).delete(new Path(outputPathString), true);

    createInputFiles(inputPathString, numFiles, fileSize, outputLocation);
    return runCreateJob(inputPathString, outputPathString, jobName);
  }

  int runCreateJob(String inputPathString, String outputPathString, String jobName) throws IOException {
    JobConf jobConf = new JobConf(this.conf);
    jobConf.setJobName(jobName);
    jobConf.setMapSpeculativeExecution(false);

    FileInputFormat.addInputPath(jobConf, new Path(inputPathString));
    FileOutputFormat.setOutputPath(jobConf, new Path(outputPathString));

    jobConf.setInputFormat(SequenceFileInputFormat.class);
    jobConf.setOutputKeyClass(LongWritable.class);
    jobConf.setOutputValueClass(CreateFileInfo.class);
    jobConf.setMapperClass(CreateFileMapper.class);
    jobConf.setReducerClass(IdentityReducer.class);
    jobConf.setOutputFormat(SequenceFileOutputFormat.class);

    RunningJob result = JobClient.runJob(jobConf);
    return result.isSuccessful() ? 0 : -1;
  }

  public void setConf(Configuration conf) {
    this.conf = new JobConf(conf);
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.CreateSampleData JD-Core
 * Version: 0.6.2
 */