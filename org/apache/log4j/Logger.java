/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class Logger extends Category
/*     */ {
/*  35 */   private static final String FQCN = Logger.class.getName();
/*     */ 
/*     */   protected Logger(String name)
/*     */   {
/*  40 */     super(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 104 */     return LogManager.getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 117 */     return LogManager.getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 135 */     return LogManager.getRootLogger();
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 155 */     return LogManager.getLogger(name, factory);
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 166 */     if (this.repository.isDisabled(5000)) {
/* 167 */       return;
/*     */     }
/*     */ 
/* 170 */     if (Level.TRACE.isGreaterOrEqual(getEffectiveLevel()))
/* 171 */       forcedLog(FQCN, Level.TRACE, message, null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/* 188 */     if (this.repository.isDisabled(5000)) {
/* 189 */       return;
/*     */     }
/*     */ 
/* 192 */     if (Level.TRACE.isGreaterOrEqual(getEffectiveLevel()))
/* 193 */       forcedLog(FQCN, Level.TRACE, message, t);
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 205 */     if (this.repository.isDisabled(5000)) {
/* 206 */       return false;
/*     */     }
/*     */ 
/* 209 */     return Level.TRACE.isGreaterOrEqual(getEffectiveLevel());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Logger
 * JD-Core Version:    0.6.2
 */