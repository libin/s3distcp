/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ public class Transform
/*    */ {
/*    */   private static final String CDATA_START = "<![CDATA[";
/*    */   private static final String CDATA_END = "]]>";
/*    */   private static final String CDATA_PSEUDO_END = "]]&gt;";
/*    */   private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
/* 32 */   private static final int CDATA_END_LEN = "]]>".length();
/*    */ 
/*    */   public static String escapeTags(String input)
/*    */   {
/* 47 */     if ((input == null) || (input.length() == 0) || ((input.indexOf('"') == -1) && (input.indexOf('&') == -1) && (input.indexOf('<') == -1) && (input.indexOf('>') == -1)))
/*    */     {
/* 53 */       return input;
/*    */     }
/*    */ 
/* 59 */     StringBuffer buf = new StringBuffer(input.length() + 6);
/* 60 */     char ch = ' ';
/*    */ 
/* 62 */     int len = input.length();
/* 63 */     for (int i = 0; i < len; i++) {
/* 64 */       ch = input.charAt(i);
/* 65 */       if (ch > '>')
/* 66 */         buf.append(ch);
/* 67 */       else if (ch == '<')
/* 68 */         buf.append("&lt;");
/* 69 */       else if (ch == '>')
/* 70 */         buf.append("&gt;");
/* 71 */       else if (ch == '&')
/* 72 */         buf.append("&amp;");
/* 73 */       else if (ch == '"')
/* 74 */         buf.append("&quot;");
/*    */       else {
/* 76 */         buf.append(ch);
/*    */       }
/*    */     }
/* 79 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public static void appendEscapingCDATA(StringBuffer buf, String str)
/*    */   {
/* 93 */     if (str != null) {
/* 94 */       int end = str.indexOf("]]>");
/* 95 */       if (end < 0) {
/* 96 */         buf.append(str);
/*    */       } else {
/* 98 */         int start = 0;
/* 99 */         while (end > -1) {
/* 100 */           buf.append(str.substring(start, end));
/* 101 */           buf.append("]]>]]&gt;<![CDATA[");
/* 102 */           start = end + CDATA_END_LEN;
/* 103 */           if (start < str.length())
/* 104 */             end = str.indexOf("]]>", start);
/*    */           else {
/* 106 */             return;
/*    */           }
/*    */         }
/* 109 */         buf.append(str.substring(start));
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.Transform
 * JD-Core Version:    0.6.2
 */