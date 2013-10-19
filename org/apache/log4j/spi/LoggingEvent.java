/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.MDC;
/*     */ import org.apache.log4j.NDC;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ 
/*     */ public class LoggingEvent
/*     */   implements Serializable
/*     */ {
/*  57 */   private static long startTime = System.currentTimeMillis();
/*     */   public final transient String fqnOfCategoryClass;
/*     */ 
/*     */   /** @deprecated */
/*     */   private transient Category logger;
/*     */ 
/*     */   /** @deprecated */
/*     */   public final String categoryName;
/*     */ 
/*     */   /** @deprecated */
/*     */   public transient Priority level;
/*     */   private String ndc;
/*     */   private Hashtable mdcCopy;
/* 109 */   private boolean ndcLookupRequired = true;
/*     */ 
/* 115 */   private boolean mdcCopyLookupRequired = true;
/*     */   private transient Object message;
/*     */   private String renderedMessage;
/*     */   private String threadName;
/*     */   private ThrowableInformation throwableInfo;
/*     */   public final long timeStamp;
/*     */   private LocationInfo locationInfo;
/*     */   static final long serialVersionUID = -868428216207166145L;
/* 142 */   static final Integer[] PARAM_ARRAY = new Integer[1];
/*     */   static final String TO_LEVEL = "toLevel";
/* 144 */   static final Class[] TO_LEVEL_PARAMS = { Integer.TYPE };
/* 145 */   static final Hashtable methodCache = new Hashtable(3);
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message, Throwable throwable)
/*     */   {
/* 159 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 160 */     this.logger = logger;
/* 161 */     this.categoryName = logger.getName();
/* 162 */     this.level = level;
/* 163 */     this.message = message;
/* 164 */     if (throwable != null) {
/* 165 */       this.throwableInfo = new ThrowableInformation(throwable, logger);
/*     */     }
/* 167 */     this.timeStamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message, Throwable throwable)
/*     */   {
/* 184 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 185 */     this.logger = logger;
/* 186 */     this.categoryName = logger.getName();
/* 187 */     this.level = level;
/* 188 */     this.message = message;
/* 189 */     if (throwable != null) {
/* 190 */       this.throwableInfo = new ThrowableInformation(throwable, logger);
/*     */     }
/*     */ 
/* 193 */     this.timeStamp = timeStamp;
/*     */   }
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Level level, Object message, String threadName, ThrowableInformation throwable, String ndc, LocationInfo info, Map properties)
/*     */   {
/* 222 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 223 */     this.logger = logger;
/* 224 */     if (logger != null)
/* 225 */       this.categoryName = logger.getName();
/*     */     else {
/* 227 */       this.categoryName = null;
/*     */     }
/* 229 */     this.level = level;
/* 230 */     this.message = message;
/* 231 */     if (throwable != null) {
/* 232 */       this.throwableInfo = throwable;
/*     */     }
/*     */ 
/* 235 */     this.timeStamp = timeStamp;
/* 236 */     this.threadName = threadName;
/* 237 */     this.ndcLookupRequired = false;
/* 238 */     this.ndc = ndc;
/* 239 */     this.locationInfo = info;
/* 240 */     this.mdcCopyLookupRequired = false;
/* 241 */     if (properties != null)
/* 242 */       this.mdcCopy = new Hashtable(properties);
/*     */   }
/*     */ 
/*     */   public LocationInfo getLocationInformation()
/*     */   {
/* 252 */     if (this.locationInfo == null) {
/* 253 */       this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
/*     */     }
/* 255 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public Level getLevel()
/*     */   {
/* 262 */     return (Level)this.level;
/*     */   }
/*     */ 
/*     */   public String getLoggerName()
/*     */   {
/* 270 */     return this.categoryName;
/*     */   }
/*     */ 
/*     */   public Category getLogger()
/*     */   {
/* 279 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public Object getMessage()
/*     */   {
/* 293 */     if (this.message != null) {
/* 294 */       return this.message;
/*     */     }
/* 296 */     return getRenderedMessage();
/*     */   }
/*     */ 
/*     */   public String getNDC()
/*     */   {
/* 307 */     if (this.ndcLookupRequired) {
/* 308 */       this.ndcLookupRequired = false;
/* 309 */       this.ndc = NDC.get();
/*     */     }
/* 311 */     return this.ndc;
/*     */   }
/*     */ 
/*     */   public Object getMDC(String key)
/*     */   {
/* 332 */     if (this.mdcCopy != null) {
/* 333 */       Object r = this.mdcCopy.get(key);
/* 334 */       if (r != null) {
/* 335 */         return r;
/*     */       }
/*     */     }
/* 338 */     return MDC.get(key);
/*     */   }
/*     */ 
/*     */   public void getMDCCopy()
/*     */   {
/* 347 */     if (this.mdcCopyLookupRequired) {
/* 348 */       this.mdcCopyLookupRequired = false;
/*     */ 
/* 351 */       Hashtable t = MDC.getContext();
/* 352 */       if (t != null)
/* 353 */         this.mdcCopy = ((Hashtable)t.clone());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getRenderedMessage()
/*     */   {
/* 360 */     if ((this.renderedMessage == null) && (this.message != null)) {
/* 361 */       if ((this.message instanceof String)) {
/* 362 */         this.renderedMessage = ((String)this.message);
/*     */       } else {
/* 364 */         LoggerRepository repository = this.logger.getLoggerRepository();
/*     */ 
/* 366 */         if ((repository instanceof RendererSupport)) {
/* 367 */           RendererSupport rs = (RendererSupport)repository;
/* 368 */           this.renderedMessage = rs.getRendererMap().findAndRender(this.message);
/*     */         } else {
/* 370 */           this.renderedMessage = this.message.toString();
/*     */         }
/*     */       }
/*     */     }
/* 374 */     return this.renderedMessage;
/*     */   }
/*     */ 
/*     */   public static long getStartTime()
/*     */   {
/* 381 */     return startTime;
/*     */   }
/*     */ 
/*     */   public String getThreadName()
/*     */   {
/* 386 */     if (this.threadName == null)
/* 387 */       this.threadName = Thread.currentThread().getName();
/* 388 */     return this.threadName;
/*     */   }
/*     */ 
/*     */   public ThrowableInformation getThrowableInformation()
/*     */   {
/* 401 */     return this.throwableInfo;
/*     */   }
/*     */ 
/*     */   public String[] getThrowableStrRep()
/*     */   {
/* 410 */     if (this.throwableInfo == null) {
/* 411 */       return null;
/*     */     }
/* 413 */     return this.throwableInfo.getThrowableStrRep();
/*     */   }
/*     */ 
/*     */   private void readLevel(ObjectInputStream ois)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 421 */     int p = ois.readInt();
/*     */     try {
/* 423 */       String className = (String)ois.readObject();
/* 424 */       if (className == null) {
/* 425 */         this.level = Level.toLevel(p);
/*     */       } else {
/* 427 */         Method m = (Method)methodCache.get(className);
/* 428 */         if (m == null) {
/* 429 */           Class clazz = Loader.loadClass(className);
/*     */ 
/* 436 */           m = clazz.getDeclaredMethod("toLevel", TO_LEVEL_PARAMS);
/* 437 */           methodCache.put(className, m);
/*     */         }
/* 439 */         this.level = ((Level)m.invoke(null, new Integer[] { new Integer(p) }));
/*     */       }
/*     */     } catch (InvocationTargetException e) {
/* 442 */       if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */       {
/* 444 */         Thread.currentThread().interrupt();
/*     */       }
/* 446 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 447 */       this.level = Level.toLevel(p);
/*     */     } catch (NoSuchMethodException e) {
/* 449 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 450 */       this.level = Level.toLevel(p);
/*     */     } catch (IllegalAccessException e) {
/* 452 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 453 */       this.level = Level.toLevel(p);
/*     */     } catch (RuntimeException e) {
/* 455 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 456 */       this.level = Level.toLevel(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 462 */     ois.defaultReadObject();
/* 463 */     readLevel(ois);
/*     */ 
/* 466 */     if (this.locationInfo == null)
/* 467 */       this.locationInfo = new LocationInfo(null, null);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 474 */     getThreadName();
/*     */ 
/* 477 */     getRenderedMessage();
/*     */ 
/* 481 */     getNDC();
/*     */ 
/* 485 */     getMDCCopy();
/*     */ 
/* 488 */     getThrowableStrRep();
/*     */ 
/* 490 */     oos.defaultWriteObject();
/*     */ 
/* 493 */     writeLevel(oos);
/*     */   }
/*     */ 
/*     */   private void writeLevel(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 499 */     oos.writeInt(this.level.toInt());
/*     */ 
/* 501 */     Class clazz = this.level.getClass();
/* 502 */     if (clazz == Level.class) {
/* 503 */       oos.writeObject(null);
/*     */     }
/*     */     else
/*     */     {
/* 508 */       oos.writeObject(clazz.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void setProperty(String propName, String propValue)
/*     */   {
/* 524 */     if (this.mdcCopy == null) {
/* 525 */       getMDCCopy();
/*     */     }
/* 527 */     if (this.mdcCopy == null) {
/* 528 */       this.mdcCopy = new Hashtable();
/*     */     }
/* 530 */     this.mdcCopy.put(propName, propValue);
/*     */   }
/*     */ 
/*     */   public final String getProperty(String key)
/*     */   {
/* 544 */     Object value = getMDC(key);
/* 545 */     String retval = null;
/* 546 */     if (value != null) {
/* 547 */       retval = value.toString();
/*     */     }
/* 549 */     return retval;
/*     */   }
/*     */ 
/*     */   public final boolean locationInformationExists()
/*     */   {
/* 559 */     return this.locationInfo != null;
/*     */   }
/*     */ 
/*     */   public final long getTimeStamp()
/*     */   {
/* 570 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   public Set getPropertyKeySet()
/*     */   {
/* 585 */     return getProperties().keySet();
/*     */   }
/*     */ 
/*     */   public Map getProperties()
/*     */   {
/* 600 */     getMDCCopy();
/*     */     Map properties;
/*     */     Map properties;
/* 602 */     if (this.mdcCopy == null)
/* 603 */       properties = new HashMap();
/*     */     else {
/* 605 */       properties = this.mdcCopy;
/*     */     }
/* 607 */     return Collections.unmodifiableMap(properties);
/*     */   }
/*     */ 
/*     */   public String getFQNOfLoggerClass()
/*     */   {
/* 617 */     return this.fqnOfCategoryClass;
/*     */   }
/*     */ 
/*     */   public Object removeProperty(String propName)
/*     */   {
/* 630 */     if (this.mdcCopy == null) {
/* 631 */       getMDCCopy();
/*     */     }
/* 633 */     if (this.mdcCopy == null) {
/* 634 */       this.mdcCopy = new Hashtable();
/*     */     }
/* 636 */     return this.mdcCopy.remove(propName);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.LoggingEvent
 * JD-Core Version:    0.6.2
 */