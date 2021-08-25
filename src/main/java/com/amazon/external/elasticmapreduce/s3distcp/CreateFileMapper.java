package com.amazon.external.elasticmapreduce.s3distcp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class CreateFileMapper implements Mapper<LongWritable, CreateFileInfo, LongWritable, CreateFileInfo> {
  protected JobConf conf;

  public void close() throws IOException {
  }

  public void configure(JobConf conf) {
    this.conf = conf;
  }

  public void map(LongWritable key, CreateFileInfo value, OutputCollector<LongWritable, CreateFileInfo> output,
      Reporter reporter) throws IOException {
    try {
      FileSystem fs = FileSystem.get(new URI(value.fileName.toString()), this.conf);
      FSDataOutputStream outputFile = fs.create(new Path(value.fileName.toString()));
      long bytesLeftToWrite = value.fileSize.get();
      byte[] buffer = new byte[12582912];
      for (int i = 0; (i < buffer.length) && (i < bytesLeftToWrite); i++) {
        buffer[i] = ((byte) (i % 127));
      }
      while (bytesLeftToWrite > buffer.length) {
        outputFile.write(buffer);
        bytesLeftToWrite -= buffer.length;
        reporter.progress();
      }
      if (bytesLeftToWrite > 0L) {
        outputFile.write(buffer, 0, (int) bytesLeftToWrite);
        bytesLeftToWrite = 0L;
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.CreateFileMapper JD-Core
 * Version: 0.6.2
 */