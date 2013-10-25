/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import javax.swing.table.DefaultTableModel;
/*    */ 
/*    */ public class LogTableModel extends DefaultTableModel
/*    */ {
/*    */   private static final long serialVersionUID = 3593300685868700894L;
/*    */ 
/*    */   public LogTableModel(Object[] colNames, int numRows)
/*    */   {
/* 49 */     super(colNames, numRows);
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(int row, int column)
/*    */   {
/* 57 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTableModel
 * JD-Core Version:    0.6.2
 */