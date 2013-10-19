/*     */ package org.apache.log4j.xml;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.Transform;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class XMLLayout extends Layout
/*     */ {
/*  70 */   private final int DEFAULT_SIZE = 256;
/*  71 */   private final int UPPER_LIMIT = 2048;
/*     */ 
/*  73 */   private StringBuffer buf = new StringBuffer(256);
/*  74 */   private boolean locationInfo = false;
/*  75 */   private boolean properties = false;
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/*  89 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/*  96 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setProperties(boolean flag)
/*     */   {
/* 105 */     this.properties = flag;
/*     */   }
/*     */ 
/*     */   public boolean getProperties()
/*     */   {
/* 114 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 129 */     if (this.buf.capacity() > 2048)
/* 130 */       this.buf = new StringBuffer(256);
/*     */     else {
/* 132 */       this.buf.setLength(0);
/*     */     }
/*     */ 
/* 137 */     this.buf.append("<log4j:event logger=\"");
/* 138 */     this.buf.append(Transform.escapeTags(event.getLoggerName()));
/* 139 */     this.buf.append("\" timestamp=\"");
/* 140 */     this.buf.append(event.timeStamp);
/* 141 */     this.buf.append("\" level=\"");
/* 142 */     this.buf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
/* 143 */     this.buf.append("\" thread=\"");
/* 144 */     this.buf.append(Transform.escapeTags(event.getThreadName()));
/* 145 */     this.buf.append("\">\r\n");
/*     */ 
/* 147 */     this.buf.append("<log4j:message><![CDATA[");
/*     */ 
/* 150 */     Transform.appendEscapingCDATA(this.buf, event.getRenderedMessage());
/* 151 */     this.buf.append("]]></log4j:message>\r\n");
/*     */ 
/* 153 */     String ndc = event.getNDC();
/* 154 */     if (ndc != null) {
/* 155 */       this.buf.append("<log4j:NDC><![CDATA[");
/* 156 */       Transform.appendEscapingCDATA(this.buf, ndc);
/* 157 */       this.buf.append("]]></log4j:NDC>\r\n");
/*     */     }
/*     */ 
/* 160 */     String[] s = event.getThrowableStrRep();
/* 161 */     if (s != null) {
/* 162 */       this.buf.append("<log4j:throwable><![CDATA[");
/* 163 */       for (int i = 0; i < s.length; i++) {
/* 164 */         Transform.appendEscapingCDATA(this.buf, s[i]);
/* 165 */         this.buf.append("\r\n");
/*     */       }
/* 167 */       this.buf.append("]]></log4j:throwable>\r\n");
/*     */     }
/*     */ 
/* 170 */     if (this.locationInfo) {
/* 171 */       LocationInfo locationInfo = event.getLocationInformation();
/* 172 */       this.buf.append("<log4j:locationInfo class=\"");
/* 173 */       this.buf.append(Transform.escapeTags(locationInfo.getClassName()));
/* 174 */       this.buf.append("\" method=\"");
/* 175 */       this.buf.append(Transform.escapeTags(locationInfo.getMethodName()));
/* 176 */       this.buf.append("\" file=\"");
/* 177 */       this.buf.append(Transform.escapeTags(locationInfo.getFileName()));
/* 178 */       this.buf.append("\" line=\"");
/* 179 */       this.buf.append(locationInfo.getLineNumber());
/* 180 */       this.buf.append("\"/>\r\n");
/*     */     }
/*     */ 
/* 183 */     if (this.properties) {
/* 184 */       Set keySet = event.getPropertyKeySet();
/* 185 */       if (keySet.size() > 0) {
/* 186 */         this.buf.append("<log4j:properties>\r\n");
/* 187 */         Object[] keys = keySet.toArray();
/* 188 */         Arrays.sort(keys);
/* 189 */         for (int i = 0; i < keys.length; i++) {
/* 190 */           String key = keys[i].toString();
/* 191 */           Object val = event.getMDC(key);
/* 192 */           if (val != null) {
/* 193 */             this.buf.append("<log4j:data name=\"");
/* 194 */             this.buf.append(Transform.escapeTags(key));
/* 195 */             this.buf.append("\" value=\"");
/* 196 */             this.buf.append(Transform.escapeTags(String.valueOf(val)));
/* 197 */             this.buf.append("\"/>\r\n");
/*     */           }
/*     */         }
/* 200 */         this.buf.append("</log4j:properties>\r\n");
/*     */       }
/*     */     }
/*     */ 
/* 204 */     this.buf.append("</log4j:event>\r\n\r\n");
/*     */ 
/* 206 */     return this.buf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 214 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.xml.XMLLayout
 * JD-Core Version:    0.6.2
 */