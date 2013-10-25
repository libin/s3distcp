/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeModelEvent;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryExplorerTree extends JTree
/*     */ {
/*     */   private static final long serialVersionUID = 8066257446951323576L;
/*     */   protected CategoryExplorerModel _model;
/*  46 */   protected boolean _rootAlreadyExpanded = false;
/*     */ 
/*     */   public CategoryExplorerTree(CategoryExplorerModel model)
/*     */   {
/*  60 */     super(model);
/*     */ 
/*  62 */     this._model = model;
/*  63 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerTree()
/*     */   {
/*  72 */     CategoryNode rootNode = new CategoryNode("Categories");
/*     */ 
/*  74 */     this._model = new CategoryExplorerModel(rootNode);
/*     */ 
/*  76 */     setModel(this._model);
/*     */ 
/*  78 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerModel getExplorerModel()
/*     */   {
/*  86 */     return this._model;
/*     */   }
/*     */ 
/*     */   public String getToolTipText(MouseEvent e)
/*     */   {
/*     */     try {
/*  92 */       return super.getToolTipText(e); } catch (Exception ex) {
/*     */     }
/*  94 */     return "";
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 105 */     putClientProperty("JTree.lineStyle", "Angled");
/*     */ 
/* 109 */     CategoryNodeRenderer renderer = new CategoryNodeRenderer();
/* 110 */     setEditable(true);
/* 111 */     setCellRenderer(renderer);
/*     */ 
/* 113 */     CategoryNodeEditor editor = new CategoryNodeEditor(this._model);
/*     */ 
/* 115 */     setCellEditor(new CategoryImmediateEditor(this, new CategoryNodeRenderer(), editor));
/*     */ 
/* 118 */     setShowsRootHandles(true);
/*     */ 
/* 120 */     setToolTipText("");
/*     */ 
/* 122 */     ensureRootExpansion();
/*     */   }
/*     */ 
/*     */   protected void expandRootNode()
/*     */   {
/* 127 */     if (this._rootAlreadyExpanded) {
/* 128 */       return;
/*     */     }
/* 130 */     this._rootAlreadyExpanded = true;
/* 131 */     TreePath path = new TreePath(this._model.getRootCategoryNode().getPath());
/* 132 */     expandPath(path);
/*     */   }
/*     */ 
/*     */   protected void ensureRootExpansion() {
/* 136 */     this._model.addTreeModelListener(new TreeModelAdapter() {
/*     */       public void treeNodesInserted(TreeModelEvent e) {
/* 138 */         CategoryExplorerTree.this.expandRootNode();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree
 * JD-Core Version:    0.6.2
 */