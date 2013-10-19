/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Date;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ class DetailPanel extends JPanel
/*     */   implements ListSelectionListener
/*     */ {
/*  42 */   private static final Logger LOG = Logger.getLogger(DetailPanel.class);
/*     */ 
/*  46 */   private static final MessageFormat FORMATTER = new MessageFormat("<b>Time:</b> <code>{0,time,medium}</code>&nbsp;&nbsp;<b>Priority:</b> <code>{1}</code>&nbsp;&nbsp;<b>Thread:</b> <code>{2}</code>&nbsp;&nbsp;<b>NDC:</b> <code>{3}</code><br><b>Logger:</b> <code>{4}</code><br><b>Location:</b> <code>{5}</code><br><b>Message:</b><pre>{6}</pre><b>Throwable:</b><pre>{7}</pre>");
/*     */   private final MyTableModel mModel;
/*     */   private final JEditorPane mDetails;
/*     */ 
/*     */   DetailPanel(JTable aTable, MyTableModel aModel)
/*     */   {
/*  70 */     this.mModel = aModel;
/*  71 */     setLayout(new BorderLayout());
/*  72 */     setBorder(BorderFactory.createTitledBorder("Details: "));
/*     */ 
/*  74 */     this.mDetails = new JEditorPane();
/*  75 */     this.mDetails.setEditable(false);
/*  76 */     this.mDetails.setContentType("text/html");
/*  77 */     add(new JScrollPane(this.mDetails), "Center");
/*     */ 
/*  79 */     ListSelectionModel rowSM = aTable.getSelectionModel();
/*  80 */     rowSM.addListSelectionListener(this);
/*     */   }
/*     */ 
/*     */   public void valueChanged(ListSelectionEvent aEvent)
/*     */   {
/*  86 */     if (aEvent.getValueIsAdjusting()) {
/*  87 */       return;
/*     */     }
/*     */ 
/*  90 */     ListSelectionModel lsm = (ListSelectionModel)aEvent.getSource();
/*  91 */     if (lsm.isSelectionEmpty()) {
/*  92 */       this.mDetails.setText("Nothing selected");
/*     */     } else {
/*  94 */       int selectedRow = lsm.getMinSelectionIndex();
/*  95 */       EventDetails e = this.mModel.getEventDetails(selectedRow);
/*  96 */       Object[] args = { new Date(e.getTimeStamp()), e.getPriority(), escape(e.getThreadName()), escape(e.getNDC()), escape(e.getCategoryName()), escape(e.getLocationDetails()), escape(e.getMessage()), escape(getThrowableStrRep(e)) };
/*     */ 
/* 107 */       this.mDetails.setText(FORMATTER.format(args));
/* 108 */       this.mDetails.setCaretPosition(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getThrowableStrRep(EventDetails aEvent)
/*     */   {
/* 123 */     String[] strs = aEvent.getThrowableStrRep();
/* 124 */     if (strs == null) {
/* 125 */       return null;
/*     */     }
/*     */ 
/* 128 */     StringBuffer sb = new StringBuffer();
/* 129 */     for (int i = 0; i < strs.length; i++) {
/* 130 */       sb.append(strs[i]).append("\n");
/*     */     }
/*     */ 
/* 133 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String escape(String aStr)
/*     */   {
/* 143 */     if (aStr == null) {
/* 144 */       return null;
/*     */     }
/*     */ 
/* 147 */     StringBuffer buf = new StringBuffer();
/* 148 */     for (int i = 0; i < aStr.length(); i++) {
/* 149 */       char c = aStr.charAt(i);
/* 150 */       switch (c) {
/*     */       case '<':
/* 152 */         buf.append("&lt;");
/* 153 */         break;
/*     */       case '>':
/* 155 */         buf.append("&gt;");
/* 156 */         break;
/*     */       case '"':
/* 158 */         buf.append("&quot;");
/* 159 */         break;
/*     */       case '&':
/* 161 */         buf.append("&amp;");
/* 162 */         break;
/*     */       default:
/* 164 */         buf.append(c);
/*     */       }
/*     */     }
/*     */ 
/* 168 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.chainsaw.DetailPanel
 * JD-Core Version:    0.6.2
 */