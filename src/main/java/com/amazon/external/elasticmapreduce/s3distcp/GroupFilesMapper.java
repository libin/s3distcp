package com.amazon.external.elasticmapreduce.s3distcp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class GroupFilesMapper implements Mapper<LongWritable, FileInfo, Text, FileInfo> {
  private static final Log log = LogFactory.getLog(GroupFilesMapper.class);
  protected JobConf conf;
  protected Pattern pattern = null;
  private String destDir;

  public void configure(JobConf conf) {
    this.conf = conf;
    String patternString = conf.get("s3DistCp.listfiles.gropubypattern");
    if (patternString != null) {
      this.pattern = Pattern.compile(patternString);
    }
    this.destDir = conf.get("s3DistCp.copyfiles.destDir");
  }

  public void close() throws IOException {
  }

  public void map(LongWritable fileUID, FileInfo fileInfo, OutputCollector<Text, FileInfo> collector, Reporter reporter)
      throws IOException {
    Text key;
    try {
      String path = new URI(fileInfo.inputFileName.toString()).getPath();
      if (path.startsWith(this.destDir)) {
        path = path.substring(this.destDir.length());
      }
      key = new Text(path);
    } catch (URISyntaxException e) {
      throw new RuntimeException(
          new StringBuilder().append("Bad URI: ").append(fileInfo.inputFileName.toString()).toString(), e);
    }

    if (this.pattern != null) {
      Matcher matcher = this.pattern.matcher(fileInfo.inputFileName.toString());
      if (matcher.matches()) {
        int numGroups = matcher.groupCount();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numGroups; i++) {
          builder.append(matcher.group(i + 1)).append("__").append(numGroups);
        }
        key = new Text(builder.toString());
      }
    }
    
    log.debug(new StringBuilder().append("Adding ").append(key.toString()).append(": ")
        .append(fileInfo.inputFileName.toString()).toString());
    collector.collect(key, fileInfo);
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.GroupFilesMapper JD-Core
 * Version: 0.6.2
 */