/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class ConsoleAppender extends WriterAppender
/*     */ {
/*     */   public static final String SYSTEM_OUT = "System.out";
/*     */   public static final String SYSTEM_ERR = "System.err";
/*  37 */   protected String target = "System.out";
/*     */ 
/*  43 */   private boolean follow = false;
/*     */ 
/*     */   public ConsoleAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ConsoleAppender(Layout layout)
/*     */   {
/*  57 */     this(layout, "System.out");
/*     */   }
/*     */ 
/*     */   public ConsoleAppender(Layout layout, String target)
/*     */   {
/*  66 */     setLayout(layout);
/*  67 */     setTarget(target);
/*  68 */     activateOptions();
/*     */   }
/*     */ 
/*     */   public void setTarget(String value)
/*     */   {
/*  78 */     String v = value.trim();
/*     */ 
/*  80 */     if ("System.out".equalsIgnoreCase(v))
/*  81 */       this.target = "System.out";
/*  82 */     else if ("System.err".equalsIgnoreCase(v))
/*  83 */       this.target = "System.err";
/*     */     else
/*  85 */       targetWarn(value);
/*     */   }
/*     */ 
/*     */   public String getTarget()
/*     */   {
/*  97 */     return this.target;
/*     */   }
/*     */ 
/*     */   public final void setFollow(boolean newValue)
/*     */   {
/* 108 */     this.follow = newValue;
/*     */   }
/*     */ 
/*     */   public final boolean getFollow()
/*     */   {
/* 119 */     return this.follow;
/*     */   }
/*     */ 
/*     */   void targetWarn(String val) {
/* 123 */     LogLog.warn("[" + val + "] should be System.out or System.err.");
/* 124 */     LogLog.warn("Using previously set target, System.out by default.");
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 131 */     if (this.follow) {
/* 132 */       if (this.target.equals("System.err"))
/* 133 */         setWriter(createWriter(new SystemErrStream()));
/*     */       else {
/* 135 */         setWriter(createWriter(new SystemOutStream()));
/*     */       }
/*     */     }
/* 138 */     else if (this.target.equals("System.err"))
/* 139 */       setWriter(createWriter(System.err));
/*     */     else {
/* 141 */       setWriter(createWriter(System.out));
/*     */     }
/*     */ 
/* 145 */     super.activateOptions();
/*     */   }
/*     */ 
/*     */   protected final void closeWriter()
/*     */   {
/* 154 */     if (this.follow)
/* 155 */       super.closeWriter();
/*     */   }
/*     */ 
/*     */   private static class SystemOutStream extends OutputStream
/*     */   {
/*     */     public void close()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void flush()
/*     */     {
/* 203 */       System.out.flush();
/*     */     }
/*     */ 
/*     */     public void write(byte[] b) throws IOException {
/* 207 */       System.out.write(b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len) throws IOException
/*     */     {
/* 212 */       System.out.write(b, off, len);
/*     */     }
/*     */ 
/*     */     public void write(int b) throws IOException {
/* 216 */       System.out.write(b);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SystemErrStream extends OutputStream
/*     */   {
/*     */     public void close()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void flush()
/*     */     {
/* 173 */       System.err.flush();
/*     */     }
/*     */ 
/*     */     public void write(byte[] b) throws IOException {
/* 177 */       System.err.write(b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len) throws IOException
/*     */     {
/* 182 */       System.err.write(b, off, len);
/*     */     }
/*     */ 
/*     */     public void write(int b) throws IOException {
/* 186 */       System.err.write(b);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.ConsoleAppender
 * JD-Core Version:    0.6.2
 */