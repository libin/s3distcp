/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ 
/*     */ @Beta
/*     */ public final class Resources
/*     */ {
/*     */   public static InputSupplier<InputStream> newInputStreamSupplier(URL url)
/*     */   {
/*  57 */     Preconditions.checkNotNull(url);
/*  58 */     return new InputSupplier()
/*     */     {
/*     */       public InputStream getInput() throws IOException {
/*  61 */         return this.val$url.openStream();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static InputSupplier<InputStreamReader> newReaderSupplier(URL url, Charset charset)
/*     */   {
/*  77 */     return CharStreams.newReaderSupplier(newInputStreamSupplier(url), charset);
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(URL url)
/*     */     throws IOException
/*     */   {
/*  88 */     return ByteStreams.toByteArray(newInputStreamSupplier(url));
/*     */   }
/*     */ 
/*     */   public static String toString(URL url, Charset charset)
/*     */     throws IOException
/*     */   {
/* 102 */     return CharStreams.toString(newReaderSupplier(url, charset));
/*     */   }
/*     */ 
/*     */   public static <T> T readLines(URL url, Charset charset, LineProcessor<T> callback)
/*     */     throws IOException
/*     */   {
/* 118 */     return CharStreams.readLines(newReaderSupplier(url, charset), callback);
/*     */   }
/*     */ 
/*     */   public static List<String> readLines(URL url, Charset charset)
/*     */     throws IOException
/*     */   {
/* 134 */     return CharStreams.readLines(newReaderSupplier(url, charset));
/*     */   }
/*     */ 
/*     */   public static void copy(URL from, OutputStream to)
/*     */     throws IOException
/*     */   {
/* 145 */     ByteStreams.copy(newInputStreamSupplier(from), to);
/*     */   }
/*     */ 
/*     */   public static URL getResource(String resourceName)
/*     */   {
/* 156 */     URL url = Resources.class.getClassLoader().getResource(resourceName);
/* 157 */     Preconditions.checkArgument(url != null, "resource %s not found.", new Object[] { resourceName });
/* 158 */     return url;
/*     */   }
/*     */ 
/*     */   public static URL getResource(Class<?> contextClass, String resourceName)
/*     */   {
/* 168 */     URL url = contextClass.getResource(resourceName);
/* 169 */     Preconditions.checkArgument(url != null, "resource %s relative to %s not found.", new Object[] { resourceName, contextClass.getName() });
/*     */ 
/* 171 */     return url;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.Resources
 * JD-Core Version:    0.6.2
 */