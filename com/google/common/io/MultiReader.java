/*    */ package com.google.common.io;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ class MultiReader extends Reader
/*    */ {
/*    */   private final Iterator<? extends InputSupplier<? extends Reader>> it;
/*    */   private Reader current;
/*    */ 
/*    */   MultiReader(Iterator<? extends InputSupplier<? extends Reader>> readers)
/*    */     throws IOException
/*    */   {
/* 37 */     this.it = readers;
/* 38 */     advance();
/*    */   }
/*    */ 
/*    */   private void advance()
/*    */     throws IOException
/*    */   {
/* 45 */     close();
/* 46 */     if (this.it.hasNext())
/* 47 */       this.current = ((Reader)((InputSupplier)this.it.next()).getInput());
/*    */   }
/*    */ 
/*    */   public int read(char[] cbuf, int off, int len) throws IOException
/*    */   {
/* 52 */     if (this.current == null) {
/* 53 */       return -1;
/*    */     }
/* 55 */     int result = this.current.read(cbuf, off, len);
/* 56 */     if (result == -1) {
/* 57 */       advance();
/* 58 */       return read(cbuf, off, len);
/*    */     }
/* 60 */     return result;
/*    */   }
/*    */ 
/*    */   public long skip(long n) throws IOException {
/* 64 */     Preconditions.checkArgument(n >= 0L, "n is negative");
/* 65 */     if (n > 0L) {
/* 66 */       while (this.current != null) {
/* 67 */         long result = this.current.skip(n);
/* 68 */         if (result > 0L) {
/* 69 */           return result;
/*    */         }
/* 71 */         advance();
/*    */       }
/*    */     }
/* 74 */     return 0L;
/*    */   }
/*    */ 
/*    */   public boolean ready() throws IOException {
/* 78 */     return (this.current != null) && (this.current.ready());
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 82 */     if (this.current != null)
/*    */       try {
/* 84 */         this.current.close();
/*    */       } finally {
/* 86 */         this.current = null;
/*    */       }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.MultiReader
 * JD-Core Version:    0.6.2
 */