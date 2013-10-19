/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.util.LinkedList;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class CategoryPath
/*     */ {
/*  40 */   protected LinkedList _categoryElements = new LinkedList();
/*     */ 
/*     */   public CategoryPath()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CategoryPath(String category)
/*     */   {
/*  58 */     String processedCategory = category;
/*     */ 
/*  60 */     if (processedCategory == null) {
/*  61 */       processedCategory = "Debug";
/*     */     }
/*     */ 
/*  64 */     processedCategory = processedCategory.replace('/', '.');
/*  65 */     processedCategory = processedCategory.replace('\\', '.');
/*     */ 
/*  67 */     StringTokenizer st = new StringTokenizer(processedCategory, ".");
/*  68 */     while (st.hasMoreTokens()) {
/*  69 */       String element = st.nextToken();
/*  70 */       addCategoryElement(new CategoryElement(element));
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  82 */     int count = this._categoryElements.size();
/*     */ 
/*  84 */     return count;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  88 */     boolean empty = false;
/*     */ 
/*  90 */     if (this._categoryElements.size() == 0) {
/*  91 */       empty = true;
/*     */     }
/*     */ 
/*  94 */     return empty;
/*     */   }
/*     */ 
/*     */   public void removeAllCategoryElements()
/*     */   {
/* 102 */     this._categoryElements.clear();
/*     */   }
/*     */ 
/*     */   public void addCategoryElement(CategoryElement categoryElement)
/*     */   {
/* 109 */     this._categoryElements.addLast(categoryElement);
/*     */   }
/*     */ 
/*     */   public CategoryElement categoryElementAt(int index)
/*     */   {
/* 116 */     return (CategoryElement)this._categoryElements.get(index);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 121 */     StringBuffer out = new StringBuffer(100);
/*     */ 
/* 123 */     out.append("\n");
/* 124 */     out.append("===========================\n");
/* 125 */     out.append("CategoryPath:                   \n");
/* 126 */     out.append("---------------------------\n");
/*     */ 
/* 128 */     out.append("\nCategoryPath:\n\t");
/*     */ 
/* 130 */     if (size() > 0)
/* 131 */       for (int i = 0; i < size(); i++) {
/* 132 */         out.append(categoryElementAt(i).toString());
/* 133 */         out.append("\n\t");
/*     */       }
/*     */     else {
/* 136 */       out.append("<<NONE>>");
/*     */     }
/*     */ 
/* 139 */     out.append("\n");
/* 140 */     out.append("===========================\n");
/*     */ 
/* 142 */     return out.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath
 * JD-Core Version:    0.6.2
 */