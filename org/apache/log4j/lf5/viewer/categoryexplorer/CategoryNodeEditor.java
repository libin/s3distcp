/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryNodeEditor extends CategoryAbstractCellEditor
/*     */ {
/*     */   protected CategoryNodeEditorRenderer _renderer;
/*     */   protected CategoryNode _lastEditedNode;
/*     */   protected JCheckBox _checkBox;
/*     */   protected CategoryExplorerModel _categoryModel;
/*     */   protected JTree _tree;
/*     */ 
/*     */   public CategoryNodeEditor(CategoryExplorerModel model)
/*     */   {
/*  66 */     this._renderer = new CategoryNodeEditorRenderer();
/*  67 */     this._checkBox = this._renderer.getCheckBox();
/*  68 */     this._categoryModel = model;
/*     */ 
/*  70 */     this._checkBox.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  72 */         CategoryNodeEditor.this._categoryModel.update(CategoryNodeEditor.this._lastEditedNode, CategoryNodeEditor.this._checkBox.isSelected());
/*  73 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*  77 */     this._renderer.addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/*  79 */         if ((e.getModifiers() & 0x4) != 0) {
/*  80 */           CategoryNodeEditor.this.showPopup(CategoryNodeEditor.this._lastEditedNode, e.getX(), e.getY());
/*     */         }
/*  82 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
/*     */   {
/*  94 */     this._lastEditedNode = ((CategoryNode)value);
/*  95 */     this._tree = tree;
/*     */ 
/*  97 */     return this._renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/* 104 */     return this._lastEditedNode.getUserObject();
/*     */   }
/*     */ 
/*     */   protected JMenuItem createPropertiesMenuItem(final CategoryNode node)
/*     */   {
/* 111 */     JMenuItem result = new JMenuItem("Properties");
/* 112 */     result.addActionListener(new ActionListener() { private final CategoryNode val$node;
/*     */ 
/* 114 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.showPropertiesDialog(node); }
/*     */ 
/*     */     });
/* 117 */     return result;
/*     */   }
/*     */ 
/*     */   protected void showPropertiesDialog(CategoryNode node) {
/* 121 */     JOptionPane.showMessageDialog(this._tree, getDisplayedProperties(node), "Category Properties: " + node.getTitle(), -1);
/*     */   }
/*     */ 
/*     */   protected Object getDisplayedProperties(CategoryNode node)
/*     */   {
/* 130 */     ArrayList result = new ArrayList();
/* 131 */     result.add("Category: " + node.getTitle());
/* 132 */     if (node.hasFatalRecords()) {
/* 133 */       result.add("Contains at least one fatal LogRecord.");
/*     */     }
/* 135 */     if (node.hasFatalChildren()) {
/* 136 */       result.add("Contains descendants with a fatal LogRecord.");
/*     */     }
/* 138 */     result.add("LogRecords in this category alone: " + node.getNumberOfContainedRecords());
/*     */ 
/* 140 */     result.add("LogRecords in descendant categories: " + node.getNumberOfRecordsFromChildren());
/*     */ 
/* 142 */     result.add("LogRecords in this category including descendants: " + node.getTotalNumberOfRecords());
/*     */ 
/* 144 */     return result.toArray();
/*     */   }
/*     */ 
/*     */   protected void showPopup(CategoryNode node, int x, int y) {
/* 148 */     JPopupMenu popup = new JPopupMenu();
/* 149 */     popup.setSize(150, 400);
/*     */ 
/* 153 */     if (node.getParent() == null) {
/* 154 */       popup.add(createRemoveMenuItem());
/* 155 */       popup.addSeparator();
/*     */     }
/* 157 */     popup.add(createSelectDescendantsMenuItem(node));
/* 158 */     popup.add(createUnselectDescendantsMenuItem(node));
/* 159 */     popup.addSeparator();
/* 160 */     popup.add(createExpandMenuItem(node));
/* 161 */     popup.add(createCollapseMenuItem(node));
/* 162 */     popup.addSeparator();
/* 163 */     popup.add(createPropertiesMenuItem(node));
/* 164 */     popup.show(this._renderer, x, y);
/*     */   }
/*     */ 
/*     */   protected JMenuItem createSelectDescendantsMenuItem(final CategoryNode node) {
/* 168 */     JMenuItem selectDescendants = new JMenuItem("Select All Descendant Categories");
/*     */ 
/* 170 */     selectDescendants.addActionListener(new ActionListener() {
/*     */       private final CategoryNode val$node;
/*     */ 
/* 173 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, true); }
/*     */ 
/*     */     });
/* 177 */     return selectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createUnselectDescendantsMenuItem(final CategoryNode node) {
/* 181 */     JMenuItem unselectDescendants = new JMenuItem("Deselect All Descendant Categories");
/*     */ 
/* 183 */     unselectDescendants.addActionListener(new ActionListener() {
/*     */       private final CategoryNode val$node;
/*     */ 
/*     */       public void actionPerformed(ActionEvent e) {
/* 187 */         CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, false);
/*     */       }
/*     */     });
/* 192 */     return unselectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createExpandMenuItem(final CategoryNode node) {
/* 196 */     JMenuItem result = new JMenuItem("Expand All Descendant Categories");
/* 197 */     result.addActionListener(new ActionListener() { private final CategoryNode val$node;
/*     */ 
/* 199 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.expandDescendants(node); }
/*     */ 
/*     */     });
/* 202 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createCollapseMenuItem(final CategoryNode node) {
/* 206 */     JMenuItem result = new JMenuItem("Collapse All Descendant Categories");
/* 207 */     result.addActionListener(new ActionListener() { private final CategoryNode val$node;
/*     */ 
/* 209 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.collapseDescendants(node); }
/*     */ 
/*     */     });
/* 212 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createRemoveMenuItem()
/*     */   {
/* 225 */     JMenuItem result = new JMenuItem("Remove All Empty Categories");
/* 226 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 228 */         while (CategoryNodeEditor.this.removeUnusedNodes() > 0);
/*     */       }
/*     */     });
/* 231 */     return result;
/*     */   }
/*     */ 
/*     */   protected void expandDescendants(CategoryNode node) {
/* 235 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 237 */     while (descendants.hasMoreElements()) {
/* 238 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 239 */       expand(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void collapseDescendants(CategoryNode node) {
/* 244 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 246 */     while (descendants.hasMoreElements()) {
/* 247 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 248 */       collapse(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int removeUnusedNodes()
/*     */   {
/* 256 */     int count = 0;
/* 257 */     CategoryNode root = this._categoryModel.getRootCategoryNode();
/* 258 */     Enumeration enumeration = root.depthFirstEnumeration();
/* 259 */     while (enumeration.hasMoreElements()) {
/* 260 */       CategoryNode node = (CategoryNode)enumeration.nextElement();
/* 261 */       if ((node.isLeaf()) && (node.getNumberOfContainedRecords() == 0) && (node.getParent() != null))
/*     */       {
/* 263 */         this._categoryModel.removeNodeFromParent(node);
/* 264 */         count++;
/*     */       }
/*     */     }
/*     */ 
/* 268 */     return count;
/*     */   }
/*     */ 
/*     */   protected void expand(CategoryNode node) {
/* 272 */     this._tree.expandPath(getTreePath(node));
/*     */   }
/*     */ 
/*     */   protected TreePath getTreePath(CategoryNode node) {
/* 276 */     return new TreePath(node.getPath());
/*     */   }
/*     */ 
/*     */   protected void collapse(CategoryNode node) {
/* 280 */     this._tree.collapsePath(getTreePath(node));
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor
 * JD-Core Version:    0.6.2
 */