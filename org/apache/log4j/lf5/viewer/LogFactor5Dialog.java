/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ 
/*     */ public abstract class LogFactor5Dialog extends JDialog
/*     */ {
/*  46 */   protected static final Font DISPLAY_FONT = new Font("Arial", 1, 12);
/*     */ 
/*     */   protected LogFactor5Dialog(JFrame jframe, String message, boolean modal)
/*     */   {
/*  59 */     super(jframe, message, modal);
/*     */   }
/*     */ 
/*     */   public void show()
/*     */   {
/*  66 */     pack();
/*  67 */     minimumSizeDialog(this, 200, 100);
/*  68 */     centerWindow(this);
/*  69 */     super.show();
/*     */   }
/*     */ 
/*     */   protected void centerWindow(Window win)
/*     */   {
/*  80 */     Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/*  83 */     if (screenDim.width < win.getSize().width) {
/*  84 */       win.setSize(screenDim.width, win.getSize().height);
/*     */     }
/*     */ 
/*  87 */     if (screenDim.height < win.getSize().height) {
/*  88 */       win.setSize(win.getSize().width, screenDim.height);
/*     */     }
/*     */ 
/*  92 */     int x = (screenDim.width - win.getSize().width) / 2;
/*  93 */     int y = (screenDim.height - win.getSize().height) / 2;
/*  94 */     win.setLocation(x, y);
/*     */   }
/*     */ 
/*     */   protected void wrapStringOnPanel(String message, Container container)
/*     */   {
/*  99 */     GridBagConstraints c = getDefaultConstraints();
/* 100 */     c.gridwidth = 0;
/*     */ 
/* 102 */     c.insets = new Insets(0, 0, 0, 0);
/* 103 */     GridBagLayout gbLayout = (GridBagLayout)container.getLayout();
/*     */ 
/* 106 */     while (message.length() > 0) {
/* 107 */       int newLineIndex = message.indexOf('\n');
/*     */       String line;
/* 109 */       if (newLineIndex >= 0) {
/* 110 */         String line = message.substring(0, newLineIndex);
/* 111 */         message = message.substring(newLineIndex + 1);
/*     */       } else {
/* 113 */         line = message;
/* 114 */         message = "";
/*     */       }
/* 116 */       Label label = new Label(line);
/* 117 */       label.setFont(DISPLAY_FONT);
/* 118 */       gbLayout.setConstraints(label, c);
/* 119 */       container.add(label);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected GridBagConstraints getDefaultConstraints() {
/* 124 */     GridBagConstraints constraints = new GridBagConstraints();
/* 125 */     constraints.weightx = 1.0D;
/* 126 */     constraints.weighty = 1.0D;
/* 127 */     constraints.gridheight = 1;
/*     */ 
/* 129 */     constraints.insets = new Insets(4, 4, 4, 4);
/*     */ 
/* 131 */     constraints.fill = 0;
/*     */ 
/* 133 */     constraints.anchor = 17;
/*     */ 
/* 135 */     return constraints;
/*     */   }
/*     */ 
/*     */   protected void minimumSizeDialog(Component component, int minWidth, int minHeight)
/*     */   {
/* 142 */     if (component.getSize().width < minWidth) {
/* 143 */       component.setSize(minWidth, component.getSize().height);
/*     */     }
/* 145 */     if (component.getSize().height < minHeight)
/* 146 */       component.setSize(component.getSize().width, minHeight);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5Dialog
 * JD-Core Version:    0.6.2
 */