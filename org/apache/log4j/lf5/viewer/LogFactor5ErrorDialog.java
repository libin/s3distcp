/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class LogFactor5ErrorDialog extends LogFactor5Dialog
/*    */ {
/*    */   public LogFactor5ErrorDialog(JFrame jframe, String message)
/*    */   {
/* 55 */     super(jframe, "Error", true);
/*    */ 
/* 57 */     JButton ok = new JButton("Ok");
/* 58 */     ok.addActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 60 */         LogFactor5ErrorDialog.this.hide();
/*    */       }
/*    */     });
/* 64 */     JPanel bottom = new JPanel();
/* 65 */     bottom.setLayout(new FlowLayout());
/* 66 */     bottom.add(ok);
/*    */ 
/* 68 */     JPanel main = new JPanel();
/* 69 */     main.setLayout(new GridBagLayout());
/* 70 */     wrapStringOnPanel(message, main);
/*    */ 
/* 72 */     getContentPane().add(main, "Center");
/* 73 */     getContentPane().add(bottom, "South");
/* 74 */     show();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5ErrorDialog
 * JD-Core Version:    0.6.2
 */