package com.amazon.external.elasticmapreduce.s3distcp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.util.Progressable;

public class ProgressableResettableBufferedFileInputStream extends InputStream {
  protected File file;
  protected Progressable progressable;
  private BufferedInputStream inputStream;
  private long mark = 0L;
  private long pos = 0L;

  public ProgressableResettableBufferedFileInputStream(File file, Progressable progressable) throws IOException {
    this.file = file;
    this.progressable = progressable;
    this.inputStream = new BufferedInputStream(new FileInputStream(file));
  }

  public int available() throws IOException {
    return this.inputStream.available();
  }

  public void close() throws IOException {
    this.inputStream.close();
  }

  public synchronized void mark(int readlimit) {
    if (this.progressable != null)
      this.progressable.progress();
    this.mark = this.pos;
  }

  public boolean markSupported() {
    if (this.progressable != null)
      this.progressable.progress();
    return true;
  }

  public int read() throws IOException {
    if (this.progressable != null)
      this.progressable.progress();

    int read = this.inputStream.read();
    if (read != -1) {
      this.pos += 1L;
    }
    return read;
  }

  public int read(byte[] b, int off, int len) throws IOException {
    if (this.progressable != null)
      this.progressable.progress();

    int read = this.inputStream.read(b, off, len);
    if (read != -1) {
      this.pos += read;
    }
    return read;
  }

  public int read(byte[] b) throws IOException {
    if (this.progressable != null)
      this.progressable.progress();

    int read = this.inputStream.read(b);
    if (read != -1) {
      this.pos += read;
    }
    return read;
  }

  public synchronized void reset() throws IOException {
    if (this.progressable != null)
      this.progressable.progress();

    this.inputStream.close();
    this.inputStream = new BufferedInputStream(new FileInputStream(this.file));
    this.pos = this.inputStream.skip(this.mark);
  }

  public long skip(long n) throws IOException {
    if (this.progressable != null)
      this.progressable.progress();

    long skipped = this.inputStream.skip(n);
    this.pos += skipped;
    return skipped;
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.
 * ProgressableResettableBufferedFileInputStream JD-Core Version: 0.6.2
 */