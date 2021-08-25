package com.amazon.external.elasticmapreduce.s3distcp;

public class ManifestEntry {
  public String path;
  public String baseName;
  public String srcDir;
  public long size;

  public ManifestEntry() {
  }

  public ManifestEntry(String path, String baseName, String srcDir, long size) {
    this.path = path;
    this.baseName = baseName;
    this.srcDir = srcDir;
    this.size = size;
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.ManifestEntry JD-Core Version:
 * 0.6.2
 */