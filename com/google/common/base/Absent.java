/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class Absent extends Optional<Object>
/*    */ {
/* 33 */   static final Absent INSTANCE = new Absent();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public boolean isPresent()
/*    */   {
/* 36 */     return false;
/*    */   }
/*    */ 
/*    */   public Object get() {
/* 40 */     throw new IllegalStateException("value is absent");
/*    */   }
/*    */ 
/*    */   public Object or(Object defaultValue) {
/* 44 */     return Preconditions.checkNotNull(defaultValue, "use orNull() instead of or(null)");
/*    */   }
/*    */ 
/*    */   public Optional<Object> or(Optional<?> secondChoice)
/*    */   {
/* 49 */     return (Optional)Preconditions.checkNotNull(secondChoice);
/*    */   }
/*    */ 
/*    */   public Object or(Supplier<?> supplier) {
/* 53 */     return Preconditions.checkNotNull(supplier.get(), "use orNull() instead of a Supplier that returns null");
/*    */   }
/*    */ 
/*    */   @Nullable
/*    */   public Object orNull() {
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   public Set<Object> asSet() {
/* 62 */     return Collections.emptySet();
/*    */   }
/*    */ 
/*    */   public <V> Optional<V> transform(Function<Object, V> function) {
/* 66 */     Preconditions.checkNotNull(function);
/* 67 */     return Optional.absent();
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 71 */     return object == this;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 75 */     return 1502476572;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 79 */     return "Optional.absent()";
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 83 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Absent
 * JD-Core Version:    0.6.2
 */