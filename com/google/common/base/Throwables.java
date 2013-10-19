/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public final class Throwables
/*     */ {
/*     */   public static <X extends Throwable> void propagateIfInstanceOf(@Nullable Throwable throwable, Class<X> declaredType)
/*     */     throws Throwable
/*     */   {
/*  63 */     if ((throwable != null) && (declaredType.isInstance(throwable)))
/*  64 */       throw ((Throwable)declaredType.cast(throwable));
/*     */   }
/*     */ 
/*     */   public static void propagateIfPossible(@Nullable Throwable throwable)
/*     */   {
/*  83 */     propagateIfInstanceOf(throwable, Error.class);
/*  84 */     propagateIfInstanceOf(throwable, RuntimeException.class);
/*     */   }
/*     */ 
/*     */   public static <X extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X> declaredType)
/*     */     throws Throwable
/*     */   {
/* 108 */     propagateIfInstanceOf(throwable, declaredType);
/* 109 */     propagateIfPossible(throwable);
/*     */   }
/*     */ 
/*     */   public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2)
/*     */     throws Throwable, Throwable
/*     */   {
/* 129 */     Preconditions.checkNotNull(declaredType2);
/* 130 */     propagateIfInstanceOf(throwable, declaredType1);
/* 131 */     propagateIfPossible(throwable, declaredType2);
/*     */   }
/*     */ 
/*     */   public static RuntimeException propagate(Throwable throwable)
/*     */   {
/* 159 */     propagateIfPossible((Throwable)Preconditions.checkNotNull(throwable));
/* 160 */     throw new RuntimeException(throwable);
/*     */   }
/*     */ 
/*     */   public static Throwable getRootCause(Throwable throwable)
/*     */   {
/*     */     Throwable cause;
/* 174 */     while ((cause = throwable.getCause()) != null) {
/* 175 */       throwable = cause;
/*     */     }
/* 177 */     return throwable;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static List<Throwable> getCausalChain(Throwable throwable)
/*     */   {
/* 199 */     Preconditions.checkNotNull(throwable);
/* 200 */     List causes = new ArrayList(4);
/* 201 */     while (throwable != null) {
/* 202 */       causes.add(throwable);
/* 203 */       throwable = throwable.getCause();
/*     */     }
/* 205 */     return Collections.unmodifiableList(causes);
/*     */   }
/*     */ 
/*     */   public static String getStackTraceAsString(Throwable throwable)
/*     */   {
/* 216 */     StringWriter stringWriter = new StringWriter();
/* 217 */     throwable.printStackTrace(new PrintWriter(stringWriter));
/* 218 */     return stringWriter.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Throwables
 * JD-Core Version:    0.6.2
 */