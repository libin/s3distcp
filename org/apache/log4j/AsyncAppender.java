/*       */ package org.apache.log4j;
/*       */ 
/*       */ import java.text.MessageFormat;
/*       */ import java.util.ArrayList;
/*       */ import java.util.Collection;
/*       */ import java.util.Enumeration;
/*       */ import java.util.HashMap;
/*       */ import java.util.Iterator;
/*       */ import java.util.List;
/*       */ import java.util.Map;
/*       */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*       */ import org.apache.log4j.helpers.LogLog;
/*       */ import org.apache.log4j.spi.AppenderAttachable;
/*       */ import org.apache.log4j.spi.LoggingEvent;
/*       */ 
/*       */ public class AsyncAppender extends AppenderSkeleton
/*       */   implements AppenderAttachable
/*       */ {
/*       */   public static final int DEFAULT_BUFFER_SIZE = 128;
/*    67 */   private final List buffer = new ArrayList();
/*       */ 
/*    72 */   private final Map discardMap = new HashMap();
/*       */ 
/*    77 */   private int bufferSize = 128;
/*       */   AppenderAttachableImpl aai;
/*       */   private final AppenderAttachableImpl appenders;
/*       */   private final Thread dispatcher;
/*    95 */   private boolean locationInfo = false;
/*       */ 
/*   100 */   private boolean blocking = true;
/*       */ 
/*       */   public AsyncAppender()
/*       */   {
/*   106 */     this.appenders = new AppenderAttachableImpl();
/*       */ 
/*   110 */     this.aai = this.appenders;
/*       */ 
/*   112 */     this.dispatcher = new Thread(new Dispatcher(this, this.buffer, this.discardMap, this.appenders));
/*       */ 
/*   117 */     this.dispatcher.setDaemon(true);
/*       */ 
/*   121 */     this.dispatcher.setName("AsyncAppender-Dispatcher-" + this.dispatcher.getName());
/*   122 */     this.dispatcher.start();
/*       */   }
/*       */ 
/*       */   public void addAppender(Appender newAppender)
/*       */   {
/*   131 */     synchronized (this.appenders) {
/*   132 */       this.appenders.addAppender(newAppender);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void append(LoggingEvent event)
/*       */   {
/*   144 */     if ((this.dispatcher == null) || (!this.dispatcher.isAlive()) || (this.bufferSize <= 0)) {
/*   145 */       synchronized (this.appenders) {
/*   146 */         this.appenders.appendLoopOnAppenders(event);
/*       */       }
/*       */ 
/*   149 */       return;
/*       */     }
/*       */ 
/*   154 */     event.getNDC();
/*   155 */     event.getThreadName();
/*       */ 
/*   157 */     event.getMDCCopy();
/*   158 */     if (this.locationInfo) {
/*   159 */       event.getLocationInformation();
/*       */     }
/*   161 */     event.getRenderedMessage();
/*   162 */     event.getThrowableStrRep();
/*       */ 
/*   164 */     synchronized (this.buffer) {
/*       */       while (true) {
/*   166 */         int previousSize = this.buffer.size();
/*       */ 
/*   168 */         if (previousSize < this.bufferSize) {
/*   169 */           this.buffer.add(event);
/*       */ 
/*   176 */           if (previousSize != 0) break;
/*   177 */           this.buffer.notifyAll(); break;
/*       */         }
/*       */ 
/*   190 */         boolean discard = true;
/*   191 */         if ((this.blocking) && (!Thread.interrupted()) && (Thread.currentThread() != this.dispatcher))
/*       */         {
/*       */           try
/*       */           {
/*   195 */             this.buffer.wait();
/*   196 */             discard = false;
/*       */           }
/*       */           catch (InterruptedException e)
/*       */           {
/*   202 */             Thread.currentThread().interrupt();
/*       */           }
/*       */ 
/*       */         }
/*       */ 
/*   210 */         if (discard) {
/*   211 */           String loggerName = event.getLoggerName();
/*   212 */           DiscardSummary summary = (DiscardSummary)this.discardMap.get(loggerName);
/*       */ 
/*   214 */           if (summary == null) {
/*   215 */             summary = new DiscardSummary(event);
/*   216 */             this.discardMap.put(loggerName, summary); break;
/*       */           }
/*   218 */           summary.add(event);
/*       */ 
/*   221 */           break;
/*       */         }
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public void close()
/*       */   {
/*   236 */     synchronized (this.buffer) {
/*   237 */       this.closed = true;
/*   238 */       this.buffer.notifyAll();
/*       */     }
/*       */     try
/*       */     {
/*   242 */       this.dispatcher.join();
/*       */     } catch (InterruptedException e) {
/*   244 */       Thread.currentThread().interrupt();
/*   245 */       LogLog.error("Got an InterruptedException while waiting for the dispatcher to finish.", e);
/*       */     }
/*       */ 
/*   253 */     synchronized (this.appenders) {
/*   254 */       Enumeration iter = this.appenders.getAllAppenders();
/*       */ 
/*   256 */       if (iter != null)
/*   257 */         while (iter.hasMoreElements()) {
/*   258 */           Object next = iter.nextElement();
/*       */ 
/*   260 */           if ((next instanceof Appender))
/*   261 */             ((Appender)next).close();
/*       */         }
/*       */     }
/*       */   }
/*       */ 
/*       */   public Enumeration getAllAppenders()
/*       */   {
/*   273 */     synchronized (this.appenders) {
/*   274 */       return this.appenders.getAllAppenders();
/*       */     }
/*       */   }
/*       */ 
/*       */   public Appender getAppender(String name)
/*       */   {
/*   285 */     synchronized (this.appenders) {
/*   286 */       return this.appenders.getAppender(name);
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean getLocationInfo()
/*       */   {
/*   297 */     return this.locationInfo;
/*       */   }
/*       */ 
/*       */   public boolean isAttached(Appender appender)
/*       */   {
/*   306 */     synchronized (this.appenders) {
/*   307 */       return this.appenders.isAttached(appender);
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean requiresLayout()
/*       */   {
/*   315 */     return false;
/*       */   }
/*       */ 
/*       */   public void removeAllAppenders()
/*       */   {
/*   322 */     synchronized (this.appenders) {
/*   323 */       this.appenders.removeAllAppenders();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removeAppender(Appender appender)
/*       */   {
/*   332 */     synchronized (this.appenders) {
/*   333 */       this.appenders.removeAppender(appender);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removeAppender(String name)
/*       */   {
/*   342 */     synchronized (this.appenders) {
/*   343 */       this.appenders.removeAppender(name);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setLocationInfo(boolean flag)
/*       */   {
/*   361 */     this.locationInfo = flag;
/*       */   }
/*       */ 
/*       */   public void setBufferSize(int size)
/*       */   {
/*   377 */     if (size < 0) {
/*   378 */       throw new NegativeArraySizeException("size");
/*       */     }
/*       */ 
/*   381 */     synchronized (this.buffer)
/*       */     {
/*   385 */       this.bufferSize = (size < 1 ? 1 : size);
/*   386 */       this.buffer.notifyAll();
/*       */     }
/*       */   }
/*       */ 
/*       */   public int getBufferSize()
/*       */   {
/*   395 */     return this.bufferSize;
/*       */   }
/*       */ 
/*       */   public void setBlocking(boolean value)
/*       */   {
/*   406 */     synchronized (this.buffer) {
/*   407 */       this.blocking = value;
/*   408 */       this.buffer.notifyAll();
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean getBlocking()
/*       */   {
/*   421 */     return this.blocking;
/*       */   }
/*       */ 
/*       */   private static class Dispatcher
/*       */     implements Runnable
/*       */   {
/*       */     private final AsyncAppender parent;
/*       */     private final List buffer;
/*       */     private final Map discardMap;
/*       */     private final AppenderAttachableImpl appenders;
/*       */ 
/*       */     public Dispatcher(AsyncAppender parent, List buffer, Map discardMap, AppenderAttachableImpl appenders)
/*       */     {
/*   517 */       this.parent = parent;
/*   518 */       this.buffer = buffer;
/*   519 */       this.appenders = appenders;
/*   520 */       this.discardMap = discardMap;
/*       */     }
/*       */ 
/*       */     public void run()
/*       */     {
/*   527 */       boolean isActive = true;
/*       */       try
/*       */       {
/*   536 */         while (isActive) {
/*   537 */           LoggingEvent[] events = null;
/*       */ 
/*   543 */           synchronized (this.buffer) {
/*   544 */             int bufferSize = this.buffer.size();
/*   545 */             isActive = !this.parent.closed;
/*       */ 
/*   547 */             while ((bufferSize == 0) && (isActive)) {
/*   548 */               this.buffer.wait();
/*   549 */               bufferSize = this.buffer.size();
/*   550 */               isActive = !this.parent.closed;
/*       */             }
/*       */ 
/*   553 */             if (bufferSize > 0) {
/*   554 */               events = new LoggingEvent[bufferSize + this.discardMap.size()];
/*   555 */               this.buffer.toArray(events);
/*       */ 
/*   560 */               int index = bufferSize;
/*       */ 
/*   563 */               Iterator iter = this.discardMap.values().iterator();
/*   564 */               while (iter.hasNext()) {
/*   565 */                 events[(index++)] = ((AsyncAppender.DiscardSummary)iter.next()).createEvent();
/*       */               }
/*       */ 
/*   571 */               this.buffer.clear();
/*   572 */               this.discardMap.clear();
/*       */ 
/*   576 */               this.buffer.notifyAll();
/*       */             }
/*       */ 
/*       */           }
/*       */ 
/*   583 */           if (events != null)
/*   584 */             for (int i = 0; i < events.length; i++)
/*   585 */               synchronized (this.appenders) {
/*   586 */                 this.appenders.appendLoopOnAppenders(events[i]);
/*       */               }
/*       */         }
/*       */       }
/*       */       catch (InterruptedException ex)
/*       */       {
/*   592 */         Thread.currentThread().interrupt();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static final class DiscardSummary
/*       */   {
/*       */     private LoggingEvent maxEvent;
/*       */     private int count;
/*       */ 
/*       */     public DiscardSummary(LoggingEvent event)
/*       */     {
/*   444 */       this.maxEvent = event;
/*   445 */       this.count = 1;
/*       */     }
/*       */ 
/*       */     public void add(LoggingEvent event)
/*       */     {
/*   454 */       if (event.getLevel().toInt() > this.maxEvent.getLevel().toInt()) {
/*   455 */         this.maxEvent = event;
/*       */       }
/*       */ 
/*   458 */       this.count += 1;
/*       */     }
/*       */ 
/*       */     public LoggingEvent createEvent()
/*       */     {
/*   467 */       String msg = MessageFormat.format("Discarded {0} messages due to full event buffer including: {1}", new Object[] { new Integer(this.count), this.maxEvent.getMessage() });
/*       */ 
/*   472 */       return new LoggingEvent("org.apache.log4j.AsyncAppender.DONT_REPORT_LOCATION", Logger.getLogger(this.maxEvent.getLoggerName()), this.maxEvent.getLevel(), msg, null);
/*       */     }
/*       */   }
/*       */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.AsyncAppender
 * JD-Core Version:    0.6.2
 */