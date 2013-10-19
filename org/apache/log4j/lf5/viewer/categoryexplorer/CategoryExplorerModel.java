/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.AWTEventMulticaster;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ 
/*     */ public class CategoryExplorerModel extends DefaultTreeModel
/*     */ {
/*     */   private static final long serialVersionUID = -3413887384316015901L;
/*  53 */   protected boolean _renderFatal = true;
/*  54 */   protected ActionListener _listener = null;
/*  55 */   protected ActionEvent _event = new ActionEvent(this, 1001, "Nodes Selection changed");
/*     */ 
/*     */   public CategoryExplorerModel(CategoryNode node)
/*     */   {
/*  68 */     super(node);
/*     */   }
/*     */ 
/*     */   public void addLogRecord(LogRecord lr)
/*     */   {
/*  75 */     CategoryPath path = new CategoryPath(lr.getCategory());
/*  76 */     addCategory(path);
/*  77 */     CategoryNode node = getCategoryNode(path);
/*  78 */     node.addRecord();
/*  79 */     if ((this._renderFatal) && (lr.isFatal())) {
/*  80 */       TreeNode[] nodes = getPathToRoot(node);
/*  81 */       int len = nodes.length;
/*     */ 
/*  86 */       for (int i = 1; i < len - 1; i++) {
/*  87 */         CategoryNode parent = (CategoryNode)nodes[i];
/*  88 */         parent.setHasFatalChildren(true);
/*  89 */         nodeChanged(parent);
/*     */       }
/*  91 */       node.setHasFatalRecords(true);
/*  92 */       nodeChanged(node);
/*     */     }
/*     */   }
/*     */ 
/*     */   public CategoryNode getRootCategoryNode() {
/*  97 */     return (CategoryNode)getRoot();
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(String category) {
/* 101 */     CategoryPath path = new CategoryPath(category);
/* 102 */     return getCategoryNode(path);
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(CategoryPath path)
/*     */   {
/* 109 */     CategoryNode root = (CategoryNode)getRoot();
/* 110 */     CategoryNode parent = root;
/*     */ 
/* 112 */     for (int i = 0; i < path.size(); i++) {
/* 113 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 116 */       Enumeration children = parent.children();
/*     */ 
/* 118 */       boolean categoryAlreadyExists = false;
/* 119 */       while (children.hasMoreElements()) {
/* 120 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 121 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 123 */         String pathLC = element.getTitle().toLowerCase();
/* 124 */         if (title.equals(pathLC)) {
/* 125 */           categoryAlreadyExists = true;
/*     */ 
/* 127 */           parent = node;
/* 128 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 132 */       if (!categoryAlreadyExists) {
/* 133 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 137 */     return parent;
/*     */   }
/*     */ 
/*     */   public boolean isCategoryPathActive(CategoryPath path)
/*     */   {
/* 145 */     CategoryNode root = (CategoryNode)getRoot();
/* 146 */     CategoryNode parent = root;
/* 147 */     boolean active = false;
/*     */ 
/* 149 */     for (int i = 0; i < path.size(); i++) {
/* 150 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 153 */       Enumeration children = parent.children();
/*     */ 
/* 155 */       boolean categoryAlreadyExists = false;
/* 156 */       active = false;
/*     */ 
/* 158 */       while (children.hasMoreElements()) {
/* 159 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 160 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 162 */         String pathLC = element.getTitle().toLowerCase();
/* 163 */         if (title.equals(pathLC)) {
/* 164 */           categoryAlreadyExists = true;
/*     */ 
/* 166 */           parent = node;
/*     */ 
/* 168 */           if (!parent.isSelected()) break;
/* 169 */           active = true; break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 176 */       if ((!active) || (!categoryAlreadyExists)) {
/* 177 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 181 */     return active;
/*     */   }
/*     */ 
/*     */   public CategoryNode addCategory(CategoryPath path)
/*     */   {
/* 193 */     CategoryNode root = (CategoryNode)getRoot();
/* 194 */     CategoryNode parent = root;
/*     */ 
/* 196 */     for (int i = 0; i < path.size(); i++) {
/* 197 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 200 */       Enumeration children = parent.children();
/*     */ 
/* 202 */       boolean categoryAlreadyExists = false;
/* 203 */       while (children.hasMoreElements()) {
/* 204 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 205 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 207 */         String pathLC = element.getTitle().toLowerCase();
/* 208 */         if (title.equals(pathLC)) {
/* 209 */           categoryAlreadyExists = true;
/*     */ 
/* 211 */           parent = node;
/* 212 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 216 */       if (!categoryAlreadyExists)
/*     */       {
/* 218 */         CategoryNode newNode = new CategoryNode(element.getTitle());
/*     */ 
/* 226 */         insertNodeInto(newNode, parent, parent.getChildCount());
/* 227 */         refresh(newNode);
/*     */ 
/* 230 */         parent = newNode;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 235 */     return parent;
/*     */   }
/*     */ 
/*     */   public void update(CategoryNode node, boolean selected) {
/* 239 */     if (node.isSelected() == selected) {
/* 240 */       return;
/*     */     }
/*     */ 
/* 243 */     if (selected)
/* 244 */       setParentSelection(node, true);
/*     */     else
/* 246 */       setDescendantSelection(node, false);
/*     */   }
/*     */ 
/*     */   public void setDescendantSelection(CategoryNode node, boolean selected)
/*     */   {
/* 251 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 253 */     while (descendants.hasMoreElements()) {
/* 254 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/*     */ 
/* 256 */       if (current.isSelected() != selected) {
/* 257 */         current.setSelected(selected);
/* 258 */         nodeChanged(current);
/*     */       }
/*     */     }
/* 261 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public void setParentSelection(CategoryNode node, boolean selected) {
/* 265 */     TreeNode[] nodes = getPathToRoot(node);
/* 266 */     int len = nodes.length;
/*     */ 
/* 271 */     for (int i = 1; i < len; i++) {
/* 272 */       CategoryNode parent = (CategoryNode)nodes[i];
/* 273 */       if (parent.isSelected() != selected) {
/* 274 */         parent.setSelected(selected);
/* 275 */         nodeChanged(parent);
/*     */       }
/*     */     }
/* 278 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public synchronized void addActionListener(ActionListener l)
/*     */   {
/* 283 */     this._listener = AWTEventMulticaster.add(this._listener, l);
/*     */   }
/*     */ 
/*     */   public synchronized void removeActionListener(ActionListener l) {
/* 287 */     this._listener = AWTEventMulticaster.remove(this._listener, l);
/*     */   }
/*     */ 
/*     */   public void resetAllNodeCounts() {
/* 291 */     Enumeration nodes = getRootCategoryNode().depthFirstEnumeration();
/*     */ 
/* 293 */     while (nodes.hasMoreElements()) {
/* 294 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 295 */       current.resetNumberOfContainedRecords();
/* 296 */       nodeChanged(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TreePath getTreePathToRoot(CategoryNode node)
/*     */   {
/* 307 */     if (node == null) {
/* 308 */       return null;
/*     */     }
/* 310 */     return new TreePath(getPathToRoot(node));
/*     */   }
/*     */ 
/*     */   protected void notifyActionListeners()
/*     */   {
/* 317 */     if (this._listener != null)
/* 318 */       this._listener.actionPerformed(this._event);
/*     */   }
/*     */ 
/*     */   protected void refresh(final CategoryNode node)
/*     */   {
/* 326 */     SwingUtilities.invokeLater(new Runnable() { private final CategoryNode val$node;
/*     */ 
/* 328 */       public void run() { CategoryExplorerModel.this.nodeChanged(node); }
/*     */ 
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel
 * JD-Core Version:    0.6.2
 */