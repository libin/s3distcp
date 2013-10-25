/*    */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.apache.log4j.lf5.LogRecord;
/*    */ import org.apache.log4j.lf5.LogRecordFilter;
/*    */ 
/*    */ public class CategoryExplorerLogRecordFilter
/*    */   implements LogRecordFilter
/*    */ {
/*    */   protected CategoryExplorerModel _model;
/*    */ 
/*    */   public CategoryExplorerLogRecordFilter(CategoryExplorerModel model)
/*    */   {
/* 52 */     this._model = model;
/*    */   }
/*    */ 
/*    */   public boolean passes(LogRecord record)
/*    */   {
/* 66 */     CategoryPath path = new CategoryPath(record.getCategory());
/* 67 */     return this._model.isCategoryPathActive(path);
/*    */   }
/*    */ 
/*    */   public void reset()
/*    */   {
/* 74 */     resetAllNodes();
/*    */   }
/*    */ 
/*    */   protected void resetAllNodes()
/*    */   {
/* 82 */     Enumeration nodes = this._model.getRootCategoryNode().depthFirstEnumeration();
/*    */ 
/* 84 */     while (nodes.hasMoreElements()) {
/* 85 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 86 */       current.resetNumberOfContainedRecords();
/* 87 */       this._model.nodeChanged(current);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerLogRecordFilter
 * JD-Core Version:    0.6.2
 */