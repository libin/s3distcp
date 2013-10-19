/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.EventObject;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultTreeCellEditor;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryImmediateEditor extends DefaultTreeCellEditor
/*     */ {
/*     */   private CategoryNodeRenderer renderer;
/*  47 */   protected Icon editingIcon = null;
/*     */ 
/*     */   public CategoryImmediateEditor(JTree tree, CategoryNodeRenderer renderer, CategoryNodeEditor editor)
/*     */   {
/*  59 */     super(tree, renderer, editor);
/*  60 */     this.renderer = renderer;
/*  61 */     renderer.setIcon(null);
/*  62 */     renderer.setLeafIcon(null);
/*  63 */     renderer.setOpenIcon(null);
/*  64 */     renderer.setClosedIcon(null);
/*     */ 
/*  66 */     this.editingIcon = null;
/*     */   }
/*     */ 
/*     */   public boolean shouldSelectCell(EventObject e)
/*     */   {
/*  73 */     boolean rv = false;
/*     */ 
/*  75 */     if ((e instanceof MouseEvent)) {
/*  76 */       MouseEvent me = (MouseEvent)e;
/*  77 */       TreePath path = this.tree.getPathForLocation(me.getX(), me.getY());
/*     */ 
/*  79 */       CategoryNode node = (CategoryNode)path.getLastPathComponent();
/*     */ 
/*  82 */       rv = node.isLeaf();
/*     */     }
/*  84 */     return rv;
/*     */   }
/*     */ 
/*     */   public boolean inCheckBoxHitRegion(MouseEvent e) {
/*  88 */     TreePath path = this.tree.getPathForLocation(e.getX(), e.getY());
/*     */ 
/*  90 */     if (path == null) {
/*  91 */       return false;
/*     */     }
/*  93 */     CategoryNode node = (CategoryNode)path.getLastPathComponent();
/*  94 */     boolean rv = false;
/*     */ 
/* 100 */     Rectangle bounds = this.tree.getRowBounds(this.lastRow);
/* 101 */     Dimension checkBoxOffset = this.renderer.getCheckBoxOffset();
/*     */ 
/* 104 */     bounds.translate(this.offset + checkBoxOffset.width, checkBoxOffset.height);
/*     */ 
/* 107 */     rv = bounds.contains(e.getPoint());
/*     */ 
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean canEditImmediately(EventObject e)
/*     */   {
/* 117 */     boolean rv = false;
/*     */ 
/* 119 */     if ((e instanceof MouseEvent)) {
/* 120 */       MouseEvent me = (MouseEvent)e;
/* 121 */       rv = inCheckBoxHitRegion(me);
/*     */     }
/*     */ 
/* 124 */     return rv;
/*     */   }
/*     */ 
/*     */   protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
/*     */   {
/* 131 */     this.offset = 0;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryImmediateEditor
 * JD-Core Version:    0.6.2
 */