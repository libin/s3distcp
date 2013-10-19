/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class LogFactor5InputDialog extends LogFactor5Dialog
/*     */ {
/*     */   public static final int SIZE = 30;
/*     */   private JTextField _textField;
/*     */ 
/*     */   public LogFactor5InputDialog(JFrame jframe, String title, String label)
/*     */   {
/*  62 */     this(jframe, title, label, 30);
/*     */   }
/*     */ 
/*     */   public LogFactor5InputDialog(JFrame jframe, String title, String label, int size)
/*     */   {
/*  74 */     super(jframe, title, true);
/*     */ 
/*  76 */     JPanel bottom = new JPanel();
/*  77 */     bottom.setLayout(new FlowLayout());
/*     */ 
/*  79 */     JPanel main = new JPanel();
/*  80 */     main.setLayout(new FlowLayout());
/*  81 */     main.add(new JLabel(label));
/*  82 */     this._textField = new JTextField(size);
/*  83 */     main.add(this._textField);
/*     */ 
/*  85 */     addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/*  87 */         if (e.getKeyCode() == 10)
/*  88 */           LogFactor5InputDialog.this.hide();
/*     */       }
/*     */     });
/*  93 */     JButton ok = new JButton("Ok");
/*  94 */     ok.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  96 */         LogFactor5InputDialog.this.hide();
/*     */       }
/*     */     });
/* 100 */     JButton cancel = new JButton("Cancel");
/* 101 */     cancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 103 */         LogFactor5InputDialog.this.hide();
/*     */ 
/* 107 */         LogFactor5InputDialog.this._textField.setText("");
/*     */       }
/*     */     });
/* 111 */     bottom.add(ok);
/* 112 */     bottom.add(cancel);
/* 113 */     getContentPane().add(main, "Center");
/* 114 */     getContentPane().add(bottom, "South");
/* 115 */     pack();
/* 116 */     centerWindow(this);
/* 117 */     show();
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 124 */     String s = this._textField.getText();
/*     */ 
/* 126 */     if ((s != null) && (s.trim().length() == 0)) {
/* 127 */       return null;
/*     */     }
/*     */ 
/* 130 */     return s;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5InputDialog
 * JD-Core Version:    0.6.2
 */