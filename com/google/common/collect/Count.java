/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class Count
/*    */   implements Serializable
/*    */ {
/*    */   private int value;
/*    */ 
/*    */   Count(int value)
/*    */   {
/* 33 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public int get() {
/* 37 */     return this.value;
/*    */   }
/*    */ 
/*    */   public int getAndAdd(int delta) {
/* 41 */     int result = this.value;
/* 42 */     this.value = (result + delta);
/* 43 */     return result;
/*    */   }
/*    */ 
/*    */   public int addAndGet(int delta) {
/* 47 */     return this.value += delta;
/*    */   }
/*    */ 
/*    */   public void set(int newValue) {
/* 51 */     this.value = newValue;
/*    */   }
/*    */ 
/*    */   public int getAndSet(int newValue) {
/* 55 */     int result = this.value;
/* 56 */     this.value = newValue;
/* 57 */     return result;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 62 */     return this.value;
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object obj)
/*    */   {
/* 67 */     return ((obj instanceof Count)) && (((Count)obj).value == this.value);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     return Integer.toString(this.value);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Count
 * JD-Core Version:    0.6.2
 */