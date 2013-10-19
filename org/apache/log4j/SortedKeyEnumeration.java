/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class SortedKeyEnumeration
/*     */   implements Enumeration
/*     */ {
/*     */   private Enumeration e;
/*     */ 
/*     */   public SortedKeyEnumeration(Hashtable ht)
/*     */   {
/* 976 */     Enumeration f = ht.keys();
/* 977 */     Vector keys = new Vector(ht.size());
/* 978 */     for (int last = 0; f.hasMoreElements(); last++) {
/* 979 */       String key = (String)f.nextElement();
/* 980 */       for (int i = 0; i < last; i++) {
/* 981 */         String s = (String)keys.get(i);
/* 982 */         if (key.compareTo(s) <= 0) break;
/*     */       }
/* 984 */       keys.add(i, key);
/*     */     }
/* 986 */     this.e = keys.elements();
/*     */   }
/*     */ 
/*     */   public boolean hasMoreElements() {
/* 990 */     return this.e.hasMoreElements();
/*     */   }
/*     */ 
/*     */   public Object nextElement() {
/* 994 */     return this.e.nextElement();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.SortedKeyEnumeration
 * JD-Core Version:    0.6.2
 */