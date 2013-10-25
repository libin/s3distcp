/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.ThreadLocalMap;
/*     */ 
/*     */ public class MDC
/*     */ {
/*  48 */   static final MDC mdc = new MDC();
/*     */   static final int HT_SIZE = 7;
/*     */   boolean java1;
/*     */   Object tlm;
/*     */   private Method removeMethod;
/*     */ 
/*     */   private MDC()
/*     */   {
/*  60 */     this.java1 = Loader.isJava1();
/*  61 */     if (!this.java1) {
/*  62 */       this.tlm = new ThreadLocalMap();
/*     */     }
/*     */     try
/*     */     {
/*  66 */       this.removeMethod = ThreadLocal.class.getMethod("remove", null);
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void put(String key, Object o)
/*     */   {
/*  84 */     if (mdc != null)
/*  85 */       mdc.put0(key, o);
/*     */   }
/*     */ 
/*     */   public static Object get(String key)
/*     */   {
/*  97 */     if (mdc != null) {
/*  98 */       return mdc.get0(key);
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public static void remove(String key)
/*     */   {
/* 111 */     if (mdc != null)
/* 112 */       mdc.remove0(key);
/*     */   }
/*     */ 
/*     */   public static Hashtable getContext()
/*     */   {
/* 122 */     if (mdc != null) {
/* 123 */       return mdc.getContext0();
/*     */     }
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 134 */     if (mdc != null)
/* 135 */       mdc.clear0();
/*     */   }
/*     */ 
/*     */   private void put0(String key, Object o)
/*     */   {
/* 142 */     if ((this.java1) || (this.tlm == null)) {
/* 143 */       return;
/*     */     }
/* 145 */     Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 146 */     if (ht == null) {
/* 147 */       ht = new Hashtable(7);
/* 148 */       ((ThreadLocalMap)this.tlm).set(ht);
/*     */     }
/* 150 */     ht.put(key, o);
/*     */   }
/*     */ 
/*     */   private Object get0(String key)
/*     */   {
/* 156 */     if ((this.java1) || (this.tlm == null)) {
/* 157 */       return null;
/*     */     }
/* 159 */     Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 160 */     if ((ht != null) && (key != null)) {
/* 161 */       return ht.get(key);
/*     */     }
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */   private void remove0(String key)
/*     */   {
/* 170 */     if ((!this.java1) && (this.tlm != null)) {
/* 171 */       Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 172 */       if (ht != null) {
/* 173 */         ht.remove(key);
/*     */ 
/* 175 */         if (ht.isEmpty())
/* 176 */           clear0();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Hashtable getContext0()
/*     */   {
/* 185 */     if ((this.java1) || (this.tlm == null)) {
/* 186 */       return null;
/*     */     }
/* 188 */     return (Hashtable)((ThreadLocalMap)this.tlm).get();
/*     */   }
/*     */ 
/*     */   private void clear0()
/*     */   {
/* 194 */     if ((!this.java1) && (this.tlm != null)) {
/* 195 */       Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 196 */       if (ht != null) {
/* 197 */         ht.clear();
/*     */       }
/* 199 */       if (this.removeMethod != null)
/*     */         try
/*     */         {
/* 202 */           this.removeMethod.invoke(this.tlm, null);
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/*     */         }
/*     */         catch (InvocationTargetException e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.MDC
 * JD-Core Version:    0.6.2
 */