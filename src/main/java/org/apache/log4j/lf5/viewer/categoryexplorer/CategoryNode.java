/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ public class CategoryNode extends DefaultMutableTreeNode
/*     */ {
/*     */   private static final long serialVersionUID = 5958994817693177319L;
/*  41 */   protected boolean _selected = true;
/*  42 */   protected int _numberOfContainedRecords = 0;
/*  43 */   protected int _numberOfRecordsFromChildren = 0;
/*  44 */   protected boolean _hasFatalChildren = false;
/*  45 */   protected boolean _hasFatalRecords = false;
/*     */ 
/*     */   public CategoryNode(String title)
/*     */   {
/*  59 */     setUserObject(title);
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  66 */     return (String)getUserObject();
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean s) {
/*  70 */     if (s != this._selected)
/*  71 */       this._selected = s;
/*     */   }
/*     */ 
/*     */   public boolean isSelected()
/*     */   {
/*  76 */     return this._selected;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setAllDescendantsSelected()
/*     */   {
/*  83 */     Enumeration children = children();
/*  84 */     while (children.hasMoreElements()) {
/*  85 */       CategoryNode node = (CategoryNode)children.nextElement();
/*  86 */       node.setSelected(true);
/*  87 */       node.setAllDescendantsSelected();
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setAllDescendantsDeSelected()
/*     */   {
/*  95 */     Enumeration children = children();
/*  96 */     while (children.hasMoreElements()) {
/*  97 */       CategoryNode node = (CategoryNode)children.nextElement();
/*  98 */       node.setSelected(false);
/*  99 */       node.setAllDescendantsDeSelected();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 104 */     return getTitle();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 108 */     if ((obj instanceof CategoryNode)) {
/* 109 */       CategoryNode node = (CategoryNode)obj;
/* 110 */       String tit1 = getTitle().toLowerCase();
/* 111 */       String tit2 = node.getTitle().toLowerCase();
/*     */ 
/* 113 */       if (tit1.equals(tit2)) {
/* 114 */         return true;
/*     */       }
/*     */     }
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 121 */     return getTitle().hashCode();
/*     */   }
/*     */ 
/*     */   public void addRecord() {
/* 125 */     this._numberOfContainedRecords += 1;
/* 126 */     addRecordToParent();
/*     */   }
/*     */ 
/*     */   public int getNumberOfContainedRecords() {
/* 130 */     return this._numberOfContainedRecords;
/*     */   }
/*     */ 
/*     */   public void resetNumberOfContainedRecords() {
/* 134 */     this._numberOfContainedRecords = 0;
/* 135 */     this._numberOfRecordsFromChildren = 0;
/* 136 */     this._hasFatalRecords = false;
/* 137 */     this._hasFatalChildren = false;
/*     */   }
/*     */ 
/*     */   public boolean hasFatalRecords() {
/* 141 */     return this._hasFatalRecords;
/*     */   }
/*     */ 
/*     */   public boolean hasFatalChildren() {
/* 145 */     return this._hasFatalChildren;
/*     */   }
/*     */ 
/*     */   public void setHasFatalRecords(boolean flag) {
/* 149 */     this._hasFatalRecords = flag;
/*     */   }
/*     */ 
/*     */   public void setHasFatalChildren(boolean flag) {
/* 153 */     this._hasFatalChildren = flag;
/*     */   }
/*     */ 
/*     */   protected int getTotalNumberOfRecords()
/*     */   {
/* 161 */     return getNumberOfRecordsFromChildren() + getNumberOfContainedRecords();
/*     */   }
/*     */ 
/*     */   protected void addRecordFromChild()
/*     */   {
/* 168 */     this._numberOfRecordsFromChildren += 1;
/* 169 */     addRecordToParent();
/*     */   }
/*     */ 
/*     */   protected int getNumberOfRecordsFromChildren() {
/* 173 */     return this._numberOfRecordsFromChildren;
/*     */   }
/*     */ 
/*     */   protected void addRecordToParent() {
/* 177 */     TreeNode parent = getParent();
/* 178 */     if (parent == null) {
/* 179 */       return;
/*     */     }
/* 181 */     ((CategoryNode)parent).addRecordFromChild();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNode
 * JD-Core Version:    0.6.2
 */