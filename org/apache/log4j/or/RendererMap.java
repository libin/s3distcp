/*     */ package org.apache.log4j.or;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ 
/*     */ public class RendererMap
/*     */ {
/*     */   Hashtable map;
/*  35 */   static ObjectRenderer defaultRenderer = new DefaultRenderer();
/*     */ 
/*     */   public RendererMap()
/*     */   {
/*  39 */     this.map = new Hashtable();
/*     */   }
/*     */ 
/*     */   public static void addRenderer(RendererSupport repository, String renderedClassName, String renderingClassName)
/*     */   {
/*  49 */     LogLog.debug("Rendering class: [" + renderingClassName + "], Rendered class: [" + renderedClassName + "].");
/*     */ 
/*  51 */     ObjectRenderer renderer = (ObjectRenderer)OptionConverter.instantiateByClassName(renderingClassName, ObjectRenderer.class, null);
/*     */ 
/*  55 */     if (renderer == null) {
/*  56 */       LogLog.error("Could not instantiate renderer [" + renderingClassName + "].");
/*  57 */       return;
/*     */     }
/*     */     try {
/*  60 */       Class renderedClass = Loader.loadClass(renderedClassName);
/*  61 */       repository.setRenderer(renderedClass, renderer);
/*     */     } catch (ClassNotFoundException e) {
/*  63 */       LogLog.error("Could not find class [" + renderedClassName + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String findAndRender(Object o)
/*     */   {
/*  77 */     if (o == null) {
/*  78 */       return null;
/*     */     }
/*  80 */     return get(o.getClass()).doRender(o);
/*     */   }
/*     */ 
/*     */   public ObjectRenderer get(Object o)
/*     */   {
/*  89 */     if (o == null) {
/*  90 */       return null;
/*     */     }
/*  92 */     return get(o.getClass());
/*     */   }
/*     */ 
/*     */   public ObjectRenderer get(Class clazz)
/*     */   {
/* 148 */     ObjectRenderer r = null;
/* 149 */     for (Class c = clazz; c != null; c = c.getSuperclass())
/*     */     {
/* 151 */       r = (ObjectRenderer)this.map.get(c);
/* 152 */       if (r != null) {
/* 153 */         return r;
/*     */       }
/* 155 */       r = searchInterfaces(c);
/* 156 */       if (r != null)
/* 157 */         return r;
/*     */     }
/* 159 */     return defaultRenderer;
/*     */   }
/*     */ 
/*     */   ObjectRenderer searchInterfaces(Class c)
/*     */   {
/* 165 */     ObjectRenderer r = (ObjectRenderer)this.map.get(c);
/* 166 */     if (r != null) {
/* 167 */       return r;
/*     */     }
/* 169 */     Class[] ia = c.getInterfaces();
/* 170 */     for (int i = 0; i < ia.length; i++) {
/* 171 */       r = searchInterfaces(ia[i]);
/* 172 */       if (r != null) {
/* 173 */         return r;
/*     */       }
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   public ObjectRenderer getDefaultRenderer()
/*     */   {
/* 182 */     return defaultRenderer;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 188 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   public void put(Class clazz, ObjectRenderer or)
/*     */   {
/* 196 */     this.map.put(clazz, or);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.or.RendererMap
 * JD-Core Version:    0.6.2
 */