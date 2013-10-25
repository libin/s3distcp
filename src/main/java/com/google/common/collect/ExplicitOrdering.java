/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class ExplicitOrdering<T> extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final ImmutableMap<T, Integer> rankMap;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   ExplicitOrdering(List<T> valuesInOrder)
/*    */   {
/* 32 */     this(buildRankMap(valuesInOrder));
/*    */   }
/*    */ 
/*    */   ExplicitOrdering(ImmutableMap<T, Integer> rankMap) {
/* 36 */     this.rankMap = rankMap;
/*    */   }
/*    */ 
/*    */   public int compare(T left, T right) {
/* 40 */     return rank(left) - rank(right);
/*    */   }
/*    */ 
/*    */   private int rank(T value) {
/* 44 */     Integer rank = (Integer)this.rankMap.get(value);
/* 45 */     if (rank == null) {
/* 46 */       throw new Ordering.IncomparableValueException(value);
/*    */     }
/* 48 */     return rank.intValue();
/*    */   }
/*    */ 
/*    */   private static <T> ImmutableMap<T, Integer> buildRankMap(List<T> valuesInOrder)
/*    */   {
/* 53 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/* 54 */     int rank = 0;
/* 55 */     for (Iterator i$ = valuesInOrder.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 56 */       builder.put(value, Integer.valueOf(rank++));
/*    */     }
/* 58 */     return builder.build();
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 62 */     if ((object instanceof ExplicitOrdering)) {
/* 63 */       ExplicitOrdering that = (ExplicitOrdering)object;
/* 64 */       return this.rankMap.equals(that.rankMap);
/*    */     }
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 70 */     return this.rankMap.hashCode();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 74 */     return "Ordering.explicit(" + this.rankMap.keySet() + ")";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ExplicitOrdering
 * JD-Core Version:    0.6.2
 */