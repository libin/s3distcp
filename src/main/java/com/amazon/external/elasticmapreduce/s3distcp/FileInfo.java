package com.amazon.external.elasticmapreduce.s3distcp;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

class FileInfo extends WritableStruct implements Cloneable {
  public LongWritable fileUID = new LongWritable(0L);
  public Text inputFileName = new Text();
  public Text outputFileName = new Text();
  public LongWritable fileSize = new LongWritable(0L);

  public FileInfo() {
  }

  public FileInfo(Long fileUID, String inputUri, String outputUri, long fileSize) {
    this.fileUID = new LongWritable(fileUID.longValue());
    this.inputFileName = new Text(inputUri);
    this.outputFileName = new Text(outputUri);
    this.fileSize = new LongWritable(fileSize);
  }

  public FileInfo clone() {
    return new FileInfo(Long.valueOf(this.fileUID.get()), this.inputFileName.toString(), this.outputFileName.toString(),
        this.fileSize.get());
  }

  public Writable[] getFields() {
    return new Writable[] { this.fileUID, this.inputFileName, this.outputFileName, this.fileSize };
  }

  public String toString() {
    return "{" + this.fileUID + ", '" + this.inputFileName + "', '" + this.outputFileName + "', " + this.fileSize + "}";
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.FileInfo JD-Core Version: 0.6.2
 */