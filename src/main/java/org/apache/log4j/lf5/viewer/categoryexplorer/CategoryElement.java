/*    */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*    */ 
/*    */ public class CategoryElement
/*    */ {
/*    */   protected String _categoryTitle;
/*    */ 
/*    */   public CategoryElement()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CategoryElement(String title)
/*    */   {
/* 51 */     this._categoryTitle = title;
/*    */   }
/*    */ 
/*    */   public String getTitle()
/*    */   {
/* 59 */     return this._categoryTitle;
/*    */   }
/*    */ 
/*    */   public void setTitle(String title) {
/* 63 */     this._categoryTitle = title;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryElement
 * JD-Core Version:    0.6.2
 */