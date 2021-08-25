package com.amazon.external.elasticmapreduce.s3distcp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

abstract class WritableStruct implements Writable {
  public abstract Writable[] getFields();

  public void readFields(DataInput input) throws IOException {
    for (Writable field : getFields())
      field.readFields(input);
  }

  public void write(DataOutput output) throws IOException {
    for (Writable field : getFields())
      field.write(output);
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.WritableStruct JD-Core Version:
 * 0.6.2
 */