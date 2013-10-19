/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Adjustable;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class LF5SwingUtils
/*     */ {
/*     */   public static void selectRow(int row, JTable table, JScrollPane pane)
/*     */   {
/*  67 */     if ((table == null) || (pane == null)) {
/*  68 */       return;
/*     */     }
/*  70 */     if (!contains(row, table.getModel())) {
/*  71 */       return;
/*     */     }
/*  73 */     moveAdjustable(row * table.getRowHeight(), pane.getVerticalScrollBar());
/*  74 */     selectRow(row, table.getSelectionModel());
/*     */ 
/*  78 */     repaintLater(table);
/*     */   }
/*     */ 
/*     */   public static void makeScrollBarTrack(Adjustable scrollBar)
/*     */   {
/*  86 */     if (scrollBar == null) {
/*  87 */       return;
/*     */     }
/*  89 */     scrollBar.addAdjustmentListener(new TrackingAdjustmentListener());
/*     */   }
/*     */ 
/*     */   public static void makeVerticalScrollBarTrack(JScrollPane pane)
/*     */   {
/*  98 */     if (pane == null) {
/*  99 */       return;
/*     */     }
/* 101 */     makeScrollBarTrack(pane.getVerticalScrollBar());
/*     */   }
/*     */ 
/*     */   protected static boolean contains(int row, TableModel model)
/*     */   {
/* 108 */     if (model == null) {
/* 109 */       return false;
/*     */     }
/* 111 */     if (row < 0) {
/* 112 */       return false;
/*     */     }
/* 114 */     if (row >= model.getRowCount()) {
/* 115 */       return false;
/*     */     }
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   protected static void selectRow(int row, ListSelectionModel model) {
/* 121 */     if (model == null) {
/* 122 */       return;
/*     */     }
/* 124 */     model.setSelectionInterval(row, row);
/*     */   }
/*     */ 
/*     */   protected static void moveAdjustable(int location, Adjustable scrollBar) {
/* 128 */     if (scrollBar == null) {
/* 129 */       return;
/*     */     }
/* 131 */     scrollBar.setValue(location);
/*     */   }
/*     */ 
/*     */   protected static void repaintLater(JComponent component)
/*     */   {
/* 139 */     SwingUtilities.invokeLater(new Runnable() { private final JComponent val$component;
/*     */ 
/* 141 */       public void run() { this.val$component.repaint(); }
/*     */ 
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LF5SwingUtils
 * JD-Core Version:    0.6.2
 */