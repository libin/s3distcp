/*    */ package org.apache.log4j.or.sax;
/*    */ 
/*    */ import org.apache.log4j.or.ObjectRenderer;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class AttributesRenderer
/*    */   implements ObjectRenderer
/*    */ {
/*    */   public String doRender(Object o)
/*    */   {
/* 41 */     if ((o instanceof Attributes)) {
/* 42 */       StringBuffer sbuf = new StringBuffer();
/* 43 */       Attributes a = (Attributes)o;
/* 44 */       int len = a.getLength();
/* 45 */       boolean first = true;
/* 46 */       for (int i = 0; i < len; i++) {
/* 47 */         if (first)
/* 48 */           first = false;
/*    */         else {
/* 50 */           sbuf.append(", ");
/*    */         }
/* 52 */         sbuf.append(a.getQName(i));
/* 53 */         sbuf.append('=');
/* 54 */         sbuf.append(a.getValue(i));
/*    */       }
/* 56 */       return sbuf.toString();
/*    */     }
/*    */     try {
/* 59 */       return o.toString();
/*    */     } catch (Exception ex) {
/* 61 */       return ex.toString();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.or.sax.AttributesRenderer
 * JD-Core Version:    0.6.2
 */