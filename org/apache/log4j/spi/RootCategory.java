/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ 
/*    */ /** @deprecated */
/*    */ public final class RootCategory extends Logger
/*    */ {
/*    */   public RootCategory(Level level)
/*    */   {
/* 37 */     super("root");
/* 38 */     setLevel(level);
/*    */   }
/*    */ 
/*    */   public final Level getChainedLevel()
/*    */   {
/* 49 */     return this.level;
/*    */   }
/*    */ 
/*    */   public final void setLevel(Level level)
/*    */   {
/* 60 */     if (level == null) {
/* 61 */       LogLog.error("You have tried to set a null level to root.", new Throwable());
/*    */     }
/*    */     else
/*    */     {
/* 65 */       this.level = level;
/*    */     }
/*    */   }
/*    */ 
/*    */   public final void setPriority(Level level)
/*    */   {
/* 72 */     setLevel(level);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.RootCategory
 * JD-Core Version:    0.6.2
 */