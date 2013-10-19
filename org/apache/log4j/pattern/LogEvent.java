/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.MDC;
/*     */ import org.apache.log4j.NDC;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ import org.apache.log4j.spi.ThrowableInformation;
/*     */ 
/*     */ public class LogEvent
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
/*     */   public LogEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message, Throwable throwable)
/*     */   {
/* 159 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 160 */     this.logger = logger;
/* 161 */     this.categoryName = logger.getName();
/* 162 */     this.level = level;
/* 163 */     this.message = message;
/* 164 */     if (throwable != null) {
/* 165 */       this.throwableInfo = new ThrowableInformation(throwable);
/*     */     }
/* 167 */     this.timeStamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public LogEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message, Throwable throwable)
/*     */   {
/* 184 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 185 */     this.logger = logger;
/* 186 */     this.categoryName = logger.getName();
/* 187 */     this.level = level;
/* 188 */     this.message = message;
/* 189 */     if (throwable != null) {
/* 190 */       this.throwableInfo = new ThrowableInformation(throwable);
/*     */     }
/*     */ 
/* 193 */     this.timeStamp = timeStamp;
/*     */   }
/*     */ 
/*     */   public LogEvent(String fqnOfCategoryClass, Logger logger, long timeStamp, Level level, Object message, String threadName, ThrowableInformation throwable, String ndc, LocationInfo info, Map properties)
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
/* 251 */     if (this.locationInfo == null) {
/* 252 */       this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
/*     */     }
/* 254 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public Level getLevel()
/*     */   {
/* 261 */     return (Level)this.level;
/*     */   }
/*     */ 
/*     */   public String getLoggerName()
/*     */   {
/* 269 */     return this.categoryName;
/*     */   }
/*     */ 
/*     */   public Object getMessage()
/*     */   {
/* 283 */     if (this.message != null) {
/* 284 */       return this.message;
/*     */     }
/* 286 */     return getRenderedMessage();
/*     */   }
/*     */ 
/*     */   public String getNDC()
/*     */   {
/* 297 */     if (this.ndcLookupRequired) {
/* 298 */       this.ndcLookupRequired = false;
/* 299 */       this.ndc = NDC.get();
/*     */     }
/* 301 */     return this.ndc;
/*     */   }
/*     */ 
/*     */   public Object getMDC(String key)
/*     */   {
/* 322 */     if (this.mdcCopy != null) {
/* 323 */       Object r = this.mdcCopy.get(key);
/* 324 */       if (r != null) {
/* 325 */         return r;
/*     */       }
/*     */     }
/* 328 */     return MDC.get(key);
/*     */   }
/*     */ 
/*     */   public void getMDCCopy()
/*     */   {
/* 337 */     if (this.mdcCopyLookupRequired) {
/* 338 */       this.mdcCopyLookupRequired = false;
/*     */ 
/* 341 */       Hashtable t = MDC.getContext();
/* 342 */       if (t != null)
/* 343 */         this.mdcCopy = ((Hashtable)t.clone());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getRenderedMessage()
/*     */   {
/* 350 */     if ((this.renderedMessage == null) && (this.message != null)) {
/* 351 */       if ((this.message instanceof String)) {
/* 352 */         this.renderedMessage = ((String)this.message);
/*     */       } else {
/* 354 */         LoggerRepository repository = this.logger.getLoggerRepository();
/*     */ 
/* 356 */         if ((repository instanceof RendererSupport)) {
/* 357 */           RendererSupport rs = (RendererSupport)repository;
/* 358 */           this.renderedMessage = rs.getRendererMap().findAndRender(this.message);
/*     */         } else {
/* 360 */           this.renderedMessage = this.message.toString();
/*     */         }
/*     */       }
/*     */     }
/* 364 */     return this.renderedMessage;
/*     */   }
/*     */ 
/*     */   public static long getStartTime()
/*     */   {
/* 371 */     return startTime;
/*     */   }
/*     */ 
/*     */   public String getThreadName()
/*     */   {
/* 376 */     if (this.threadName == null)
/* 377 */       this.threadName = Thread.currentThread().getName();
/* 378 */     return this.threadName;
/*     */   }
/*     */ 
/*     */   public ThrowableInformation getThrowableInformation()
/*     */   {
/* 391 */     return this.throwableInfo;
/*     */   }
/*     */ 
/*     */   public String[] getThrowableStrRep()
/*     */   {
/* 400 */     if (this.throwableInfo == null) {
/* 401 */       return null;
/*     */     }
/* 403 */     return this.throwableInfo.getThrowableStrRep();
/*     */   }
/*     */ 
/*     */   private void readLevel(ObjectInputStream ois)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 411 */     int p = ois.readInt();
/*     */     try {
/* 413 */       String className = (String)ois.readObject();
/* 414 */       if (className == null) {
/* 415 */         this.level = Level.toLevel(p);
/*     */       } else {
/* 417 */         Method m = (Method)methodCache.get(className);
/* 418 */         if (m == null) {
/* 419 */           Class clazz = Loader.loadClass(className);
/*     */ 
/* 426 */           m = clazz.getDeclaredMethod("toLevel", TO_LEVEL_PARAMS);
/* 427 */           methodCache.put(className, m);
/*     */         }
/* 429 */         PARAM_ARRAY[0] = new Integer(p);
/* 430 */         this.level = ((Level)m.invoke(null, PARAM_ARRAY));
/*     */       }
/*     */     } catch (Exception e) {
/* 433 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 434 */       this.level = Level.toLevel(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 440 */     ois.defaultReadObject();
/* 441 */     readLevel(ois);
/*     */ 
/* 444 */     if (this.locationInfo == null)
/* 445 */       this.locationInfo = new LocationInfo(null, null);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 452 */     getThreadName();
/*     */ 
/* 455 */     getRenderedMessage();
/*     */ 
/* 459 */     getNDC();
/*     */ 
/* 463 */     getMDCCopy();
/*     */ 
/* 466 */     getThrowableStrRep();
/*     */ 
/* 468 */     oos.defaultWriteObject();
/*     */ 
/* 471 */     writeLevel(oos);
/*     */   }
/*     */ 
/*     */   private void writeLevel(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 477 */     oos.writeInt(this.level.toInt());
/*     */ 
/* 479 */     Class clazz = this.level.getClass();
/* 480 */     if (clazz == Level.class) {
/* 481 */       oos.writeObject(null);
/*     */     }
/*     */     else
/*     */     {
/* 486 */       oos.writeObject(clazz.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void setProperty(String propName, String propValue)
/*     */   {
/* 502 */     if (this.mdcCopy == null) {
/* 503 */       getMDCCopy();
/*     */     }
/* 505 */     if (this.mdcCopy == null) {
/* 506 */       this.mdcCopy = new Hashtable();
/*     */     }
/* 508 */     this.mdcCopy.put(propName, propValue);
/*     */   }
/*     */ 
/*     */   public final String getProperty(String key)
/*     */   {
/* 522 */     Object value = getMDC(key);
/* 523 */     String retval = null;
/* 524 */     if (value != null) {
/* 525 */       retval = value.toString();
/*     */     }
/* 527 */     return retval;
/*     */   }
/*     */ 
/*     */   public final boolean locationInformationExists()
/*     */   {
/* 537 */     return this.locationInfo != null;
/*     */   }
/*     */ 
/*     */   public final long getTimeStamp()
/*     */   {
/* 548 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   public Set getPropertyKeySet()
/*     */   {
/* 563 */     return getProperties().keySet();
/*     */   }
/*     */ 
/*     */   public Map getProperties()
/*     */   {
/* 578 */     getMDCCopy();
/*     */     Map properties;
/*     */     Map properties;
/* 580 */     if (this.mdcCopy == null)
/* 581 */       properties = new HashMap();
/*     */     else {
/* 583 */       properties = this.mdcCopy;
/*     */     }
/* 585 */     return Collections.unmodifiableMap(properties);
/*     */   }
/*     */ 
/*     */   public String getFQNOfLoggerClass()
/*     */   {
/* 595 */     return this.fqnOfCategoryClass;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.LogEvent
 * JD-Core Version:    0.6.2
 */