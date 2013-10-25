/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class Resource
/*     */ {
/*     */   protected String _name;
/*     */ 
/*     */   public Resource()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Resource(String name)
/*     */   {
/*  63 */     this._name = name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  84 */     this._name = name;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  94 */     return this._name;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 105 */     InputStream in = ResourceUtils.getResourceAsStream(this, this);
/*     */ 
/* 107 */     return in;
/*     */   }
/*     */ 
/*     */   public InputStreamReader getInputStreamReader()
/*     */   {
/* 118 */     InputStream in = ResourceUtils.getResourceAsStream(this, this);
/*     */ 
/* 120 */     if (in == null) {
/* 121 */       return null;
/*     */     }
/*     */ 
/* 124 */     InputStreamReader reader = new InputStreamReader(in);
/*     */ 
/* 126 */     return reader;
/*     */   }
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 135 */     return ResourceUtils.getResourceAsURL(this, this);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.util.Resource
 * JD-Core Version:    0.6.2
 */