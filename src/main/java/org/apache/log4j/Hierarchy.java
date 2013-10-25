/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.or.ObjectRenderer;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ import org.apache.log4j.spi.HierarchyEventListener;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ import org.apache.log4j.spi.ThrowableRenderer;
/*     */ import org.apache.log4j.spi.ThrowableRendererSupport;
/*     */ 
/*     */ public class Hierarchy
/*     */   implements LoggerRepository, RendererSupport, ThrowableRendererSupport
/*     */ {
/*     */   private LoggerFactory defaultFactory;
/*     */   private Vector listeners;
/*     */   Hashtable ht;
/*     */   Logger root;
/*     */   RendererMap rendererMap;
/*     */   int thresholdInt;
/*     */   Level threshold;
/*  78 */   boolean emittedNoAppenderWarning = false;
/*  79 */   boolean emittedNoResourceBundleWarning = false;
/*     */ 
/*  81 */   private ThrowableRenderer throwableRenderer = null;
/*     */ 
/*     */   public Hierarchy(Logger root)
/*     */   {
/*  91 */     this.ht = new Hashtable();
/*  92 */     this.listeners = new Vector(1);
/*  93 */     this.root = root;
/*     */ 
/*  95 */     setThreshold(Level.ALL);
/*  96 */     this.root.setHierarchy(this);
/*  97 */     this.rendererMap = new RendererMap();
/*  98 */     this.defaultFactory = new DefaultCategoryFactory();
/*     */   }
/*     */ 
/*     */   public void addRenderer(Class classToRender, ObjectRenderer or)
/*     */   {
/* 106 */     this.rendererMap.put(classToRender, or);
/*     */   }
/*     */ 
/*     */   public void addHierarchyEventListener(HierarchyEventListener listener)
/*     */   {
/* 111 */     if (this.listeners.contains(listener))
/* 112 */       LogLog.warn("Ignoring attempt to add an existent listener.");
/*     */     else
/* 114 */       this.listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 130 */     this.ht.clear();
/*     */   }
/*     */ 
/*     */   public void emitNoAppenderWarning(Category cat)
/*     */   {
/* 136 */     if (!this.emittedNoAppenderWarning) {
/* 137 */       LogLog.warn("No appenders could be found for logger (" + cat.getName() + ").");
/*     */ 
/* 139 */       LogLog.warn("Please initialize the log4j system properly.");
/* 140 */       LogLog.warn("See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.");
/* 141 */       this.emittedNoAppenderWarning = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Logger exists(String name)
/*     */   {
/* 154 */     Object o = this.ht.get(new CategoryKey(name));
/* 155 */     if ((o instanceof Logger)) {
/* 156 */       return (Logger)o;
/*     */     }
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   public void setThreshold(String levelStr)
/*     */   {
/* 167 */     Level l = Level.toLevel(levelStr, null);
/* 168 */     if (l != null)
/* 169 */       setThreshold(l);
/*     */     else
/* 171 */       LogLog.warn("Could not convert [" + levelStr + "] to Level.");
/*     */   }
/*     */ 
/*     */   public void setThreshold(Level l)
/*     */   {
/* 184 */     if (l != null) {
/* 185 */       this.thresholdInt = l.level;
/* 186 */       this.threshold = l;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireAddAppenderEvent(Category logger, Appender appender)
/*     */   {
/* 192 */     if (this.listeners != null) {
/* 193 */       int size = this.listeners.size();
/*     */ 
/* 195 */       for (int i = 0; i < size; i++) {
/* 196 */         HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
/* 197 */         listener.addAppenderEvent(logger, appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void fireRemoveAppenderEvent(Category logger, Appender appender) {
/* 203 */     if (this.listeners != null) {
/* 204 */       int size = this.listeners.size();
/*     */ 
/* 206 */       for (int i = 0; i < size; i++) {
/* 207 */         HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
/* 208 */         listener.removeAppenderEvent(logger, appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Level getThreshold()
/*     */   {
/* 220 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name)
/*     */   {
/* 247 */     return getLogger(name, this.defaultFactory);
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 266 */     CategoryKey key = new CategoryKey(name);
/*     */ 
/* 272 */     synchronized (this.ht) {
/* 273 */       Object o = this.ht.get(key);
/* 274 */       if (o == null) {
/* 275 */         Logger logger = factory.makeNewLoggerInstance(name);
/* 276 */         logger.setHierarchy(this);
/* 277 */         this.ht.put(key, logger);
/* 278 */         updateParents(logger);
/* 279 */         return logger;
/* 280 */       }if ((o instanceof Logger))
/* 281 */         return (Logger)o;
/* 282 */       if ((o instanceof ProvisionNode))
/*     */       {
/* 284 */         Logger logger = factory.makeNewLoggerInstance(name);
/* 285 */         logger.setHierarchy(this);
/* 286 */         this.ht.put(key, logger);
/* 287 */         updateChildren((ProvisionNode)o, logger);
/* 288 */         updateParents(logger);
/* 289 */         return logger;
/*     */       }
/*     */ 
/* 293 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Enumeration getCurrentLoggers()
/*     */   {
/* 309 */     Vector v = new Vector(this.ht.size());
/*     */ 
/* 311 */     Enumeration elems = this.ht.elements();
/* 312 */     while (elems.hasMoreElements()) {
/* 313 */       Object o = elems.nextElement();
/* 314 */       if ((o instanceof Logger)) {
/* 315 */         v.addElement(o);
/*     */       }
/*     */     }
/* 318 */     return v.elements();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Enumeration getCurrentCategories()
/*     */   {
/* 326 */     return getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public RendererMap getRendererMap()
/*     */   {
/* 335 */     return this.rendererMap;
/*     */   }
/*     */ 
/*     */   public Logger getRootLogger()
/*     */   {
/* 346 */     return this.root;
/*     */   }
/*     */ 
/*     */   public boolean isDisabled(int level)
/*     */   {
/* 356 */     return this.thresholdInt > level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void overrideAsNeeded(String override)
/*     */   {
/* 364 */     LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
/*     */   }
/*     */ 
/*     */   public void resetConfiguration()
/*     */   {
/* 384 */     getRootLogger().setLevel(Level.DEBUG);
/* 385 */     this.root.setResourceBundle(null);
/* 386 */     setThreshold(Level.ALL);
/*     */ 
/* 390 */     synchronized (this.ht) {
/* 391 */       shutdown();
/*     */ 
/* 393 */       Enumeration cats = getCurrentLoggers();
/* 394 */       while (cats.hasMoreElements()) {
/* 395 */         Logger c = (Logger)cats.nextElement();
/* 396 */         c.setLevel(null);
/* 397 */         c.setAdditivity(true);
/* 398 */         c.setResourceBundle(null);
/*     */       }
/*     */     }
/* 401 */     this.rendererMap.clear();
/* 402 */     this.throwableRenderer = null;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setDisableOverride(String override)
/*     */   {
/* 412 */     LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
/*     */   }
/*     */ 
/*     */   public void setRenderer(Class renderedClass, ObjectRenderer renderer)
/*     */   {
/* 422 */     this.rendererMap.put(renderedClass, renderer);
/*     */   }
/*     */ 
/*     */   public void setThrowableRenderer(ThrowableRenderer renderer)
/*     */   {
/* 429 */     this.throwableRenderer = renderer;
/*     */   }
/*     */ 
/*     */   public ThrowableRenderer getThrowableRenderer()
/*     */   {
/* 436 */     return this.throwableRenderer;
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 458 */     Logger root = getRootLogger();
/*     */ 
/* 461 */     root.closeNestedAppenders();
/*     */ 
/* 463 */     synchronized (this.ht) {
/* 464 */       Enumeration cats = getCurrentLoggers();
/* 465 */       while (cats.hasMoreElements()) {
/* 466 */         Logger c = (Logger)cats.nextElement();
/* 467 */         c.closeNestedAppenders();
/*     */       }
/*     */ 
/* 471 */       root.removeAllAppenders();
/* 472 */       cats = getCurrentLoggers();
/* 473 */       while (cats.hasMoreElements()) {
/* 474 */         Logger c = (Logger)cats.nextElement();
/* 475 */         c.removeAllAppenders();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void updateParents(Logger cat)
/*     */   {
/* 504 */     String name = cat.name;
/* 505 */     int length = name.length();
/* 506 */     boolean parentFound = false;
/*     */ 
/* 511 */     for (int i = name.lastIndexOf('.', length - 1); i >= 0; 
/* 512 */       i = name.lastIndexOf('.', i - 1)) {
/* 513 */       String substr = name.substring(0, i);
/*     */ 
/* 516 */       CategoryKey key = new CategoryKey(substr);
/* 517 */       Object o = this.ht.get(key);
/*     */ 
/* 519 */       if (o == null)
/*     */       {
/* 521 */         ProvisionNode pn = new ProvisionNode(cat);
/* 522 */         this.ht.put(key, pn); } else {
/* 523 */         if ((o instanceof Category)) {
/* 524 */           parentFound = true;
/* 525 */           cat.parent = ((Category)o);
/*     */ 
/* 527 */           break;
/* 528 */         }if ((o instanceof ProvisionNode)) {
/* 529 */           ((ProvisionNode)o).addElement(cat);
/*     */         } else {
/* 531 */           Exception e = new IllegalStateException("unexpected object type " + o.getClass() + " in ht.");
/*     */ 
/* 533 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 537 */     if (!parentFound)
/* 538 */       cat.parent = this.root;
/*     */   }
/*     */ 
/*     */   private final void updateChildren(ProvisionNode pn, Logger logger)
/*     */   {
/* 560 */     int last = pn.size();
/*     */ 
/* 562 */     for (int i = 0; i < last; i++) {
/* 563 */       Logger l = (Logger)pn.elementAt(i);
/*     */ 
/* 568 */       if (!l.parent.name.startsWith(logger.name)) {
/* 569 */         logger.parent = l.parent;
/* 570 */         l.parent = logger;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Hierarchy
 * JD-Core Version:    0.6.2
 */