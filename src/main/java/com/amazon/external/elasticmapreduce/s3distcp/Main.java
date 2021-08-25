package com.amazon.external.elasticmapreduce.s3distcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.ToolRunner;
import java.util.Arrays;

public class Main {
  private static final Log log = LogFactory.getLog(S3DistCp.class);

  public static void main(String[] args) throws Exception {
    log.info("Running with args: " + Arrays.toString(args));

	System.exit(ToolRunner.run(new S3DistCp(), args));
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.Main JD-Core Version: 0.6.2
 */