package com.amazon.external.elasticmapreduce.s3distcp;

import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleExecutor implements Executor {
  private static final Log LOG = LogFactory.getLog(Worker.class);
  protected boolean closed;
  protected int tail;
  protected int head;
  protected Exception lastException;
  protected Runnable[] queue;
  protected Thread[] workers;

  public SimpleExecutor(int queueSize, int workerSize) {
    this.queue = new Runnable[queueSize + 1];
    this.workers = new Thread[workerSize];
    this.head = 0;
    this.tail = 0;
    this.closed = false;
    this.lastException = null;
    startWorkers();
  }

  public synchronized void registerException(Exception e) {
    this.lastException = e;
  }

  public synchronized void assertNoExceptions() {
    if (this.lastException != null)
      throw new RuntimeException("Some tasks in remote executor failed", this.lastException);
  }

  private void startWorkers() {
    for (int i = 0; i < this.workers.length; i++) {
      this.workers[i] = new Thread(new Worker(this));
      this.workers[i].start();
    }
  }

  public void close() {
    synchronized (this) {
      this.closed = true;
      notifyAll();
    }

    for (int i = 0; i < this.workers.length; i++)
      try {
        this.workers[i].join();
      } catch (InterruptedException e) {
        LOG.error("Interrupted while waiting for workers", e);
      }
  }

  public synchronized boolean closed() {
    return this.closed;
  }

  public synchronized void execute(Runnable command) {
    try {
      while (isFull())
        wait();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    this.queue[this.head] = command;
    this.head = ((this.head + 1) % this.queue.length);
    notifyAll();
  }

  synchronized boolean isEmpty() {
    return this.head == this.tail;
  }

  synchronized boolean isFull() {
    return (this.head + 1) % this.queue.length == this.tail;
  }

  synchronized int size() {
    int result = this.head - this.tail;
    if (result < 0) {
      return result + this.queue.length;
    }
    return result;
  }

  public synchronized Runnable take() throws InterruptedException {
    while ((isEmpty()) && (!this.closed)) {
      wait(15000L);
    }
    if (!isEmpty()) {
      Runnable returnItem = this.queue[this.tail];
      this.tail = ((this.tail + 1) % this.queue.length);
      notifyAll();
      return returnItem;
    }
    return null;
  }

  static class Worker implements Runnable {
    private final SimpleExecutor executor;

    Worker(SimpleExecutor executor) {
      this.executor = executor;
    }

    public void run() {
      try {
        Runnable job;
        while ((job = this.executor.take()) != null)
          try {
            job.run();
          } catch (RuntimeException e) {
            this.executor.registerException(e);
            SimpleExecutor.LOG.error("Worker task threw exception", e);
          }
      } catch (InterruptedException e) {
      }
    }
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.SimpleExecutor JD-Core Version:
 * 0.6.2
 */