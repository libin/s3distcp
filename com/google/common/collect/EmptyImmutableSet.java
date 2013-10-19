/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Collection;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ final class EmptyImmutableSet extends ImmutableSet<Object>
/*    */ {
/* 33 */   static final EmptyImmutableSet INSTANCE = new EmptyImmutableSet();
/*    */ 
/* 58 */   private static final Object[] EMPTY_ARRAY = new Object[0];
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int size()
/*    */   {
/* 39 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean isEmpty() {
/* 43 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean contains(Object target) {
/* 47 */     return false;
/*    */   }
/*    */ 
/*    */   public UnmodifiableIterator<Object> iterator() {
/* 51 */     return Iterators.emptyIterator();
/*    */   }
/*    */ 
/*    */   boolean isPartialView() {
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */   public Object[] toArray()
/*    */   {
/* 61 */     return EMPTY_ARRAY;
/*    */   }
/*    */ 
/*    */   public <T> T[] toArray(T[] a) {
/* 65 */     if (a.length > 0) {
/* 66 */       a[0] = null;
/*    */     }
/* 68 */     return a;
/*    */   }
/*    */ 
/*    */   public boolean containsAll(Collection<?> targets) {
/* 72 */     return targets.isEmpty();
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 76 */     if ((object instanceof Set)) {
/* 77 */       Set that = (Set)object;
/* 78 */       return that.isEmpty();
/*    */     }
/* 80 */     return false;
/*    */   }
/*    */ 
/*    */   public final int hashCode() {
/* 84 */     return 0;
/*    */   }
/*    */ 
/*    */   boolean isHashCodeFast() {
/* 88 */     return true;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 92 */     return "[]";
/*    */   }
/*    */ 
/*    */   Object readResolve() {
/* 96 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableSet
 * JD-Core Version:    0.6.2
 */