/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagLayout;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class LogFactor5LoadingDialog extends LogFactor5Dialog
/*    */ {
/*    */   public LogFactor5LoadingDialog(JFrame jframe, String message)
/*    */   {
/* 53 */     super(jframe, "LogFactor5", false);
/*    */ 
/* 55 */     JPanel bottom = new JPanel();
/* 56 */     bottom.setLayout(new FlowLayout());
/*    */ 
/* 58 */     JPanel main = new JPanel();
/* 59 */     main.setLayout(new GridBagLayout());
/* 60 */     wrapStringOnPanel(message, main);
/*    */ 
/* 62 */     getContentPane().add(main, "Center");
/* 63 */     getContentPane().add(bottom, "South");
/* 64 */     show();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5LoadingDialog
 * JD-Core Version:    0.6.2
 */