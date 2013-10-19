/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Date;
/*     */ import org.apache.log4j.helpers.Transform;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class HTMLLayout extends Layout
/*     */ {
/*  36 */   protected final int BUF_SIZE = 256;
/*  37 */   protected final int MAX_CAPACITY = 1024;
/*     */ 
/*  39 */   static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";
/*     */ 
/*  42 */   private StringBuffer sbuf = new StringBuffer(256);
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String LOCATION_INFO_OPTION = "LocationInfo";
/*     */   public static final String TITLE_OPTION = "Title";
/*  66 */   boolean locationInfo = false;
/*     */ 
/*  68 */   String title = "Log4J Log Messages";
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/*  83 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/*  91 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/* 102 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/* 110 */     return this.title;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 118 */     return "text/html";
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 131 */     if (this.sbuf.capacity() > 1024)
/* 132 */       this.sbuf = new StringBuffer(256);
/*     */     else {
/* 134 */       this.sbuf.setLength(0);
/*     */     }
/*     */ 
/* 137 */     this.sbuf.append(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);
/*     */ 
/* 139 */     this.sbuf.append("<td>");
/* 140 */     this.sbuf.append(event.timeStamp - LoggingEvent.getStartTime());
/* 141 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 143 */     String escapedThread = Transform.escapeTags(event.getThreadName());
/* 144 */     this.sbuf.append("<td title=\"" + escapedThread + " thread\">");
/* 145 */     this.sbuf.append(escapedThread);
/* 146 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 148 */     this.sbuf.append("<td title=\"Level\">");
/* 149 */     if (event.getLevel().equals(Level.DEBUG)) {
/* 150 */       this.sbuf.append("<font color=\"#339933\">");
/* 151 */       this.sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
/* 152 */       this.sbuf.append("</font>");
/*     */     }
/* 154 */     else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
/* 155 */       this.sbuf.append("<font color=\"#993300\"><strong>");
/* 156 */       this.sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
/* 157 */       this.sbuf.append("</strong></font>");
/*     */     } else {
/* 159 */       this.sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
/*     */     }
/* 161 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 163 */     String escapedLogger = Transform.escapeTags(event.getLoggerName());
/* 164 */     this.sbuf.append("<td title=\"" + escapedLogger + " category\">");
/* 165 */     this.sbuf.append(escapedLogger);
/* 166 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 168 */     if (this.locationInfo) {
/* 169 */       LocationInfo locInfo = event.getLocationInformation();
/* 170 */       this.sbuf.append("<td>");
/* 171 */       this.sbuf.append(Transform.escapeTags(locInfo.getFileName()));
/* 172 */       this.sbuf.append(':');
/* 173 */       this.sbuf.append(locInfo.getLineNumber());
/* 174 */       this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */     }
/*     */ 
/* 177 */     this.sbuf.append("<td title=\"Message\">");
/* 178 */     this.sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
/* 179 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/* 180 */     this.sbuf.append("</tr>" + Layout.LINE_SEP);
/*     */ 
/* 182 */     if (event.getNDC() != null) {
/* 183 */       this.sbuf.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
/* 184 */       this.sbuf.append("NDC: " + Transform.escapeTags(event.getNDC()));
/* 185 */       this.sbuf.append("</td></tr>" + Layout.LINE_SEP);
/*     */     }
/*     */ 
/* 188 */     String[] s = event.getThrowableStrRep();
/* 189 */     if (s != null) {
/* 190 */       this.sbuf.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : xx-small;\" colspan=\"6\">");
/* 191 */       appendThrowableAsHTML(s, this.sbuf);
/* 192 */       this.sbuf.append("</td></tr>" + Layout.LINE_SEP);
/*     */     }
/*     */ 
/* 195 */     return this.sbuf.toString();
/*     */   }
/*     */ 
/*     */   void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
/* 199 */     if (s != null) {
/* 200 */       int len = s.length;
/* 201 */       if (len == 0)
/* 202 */         return;
/* 203 */       sbuf.append(Transform.escapeTags(s[0]));
/* 204 */       sbuf.append(Layout.LINE_SEP);
/* 205 */       for (int i = 1; i < len; i++) {
/* 206 */         sbuf.append(TRACE_PREFIX);
/* 207 */         sbuf.append(Transform.escapeTags(s[i]));
/* 208 */         sbuf.append(Layout.LINE_SEP);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getHeader()
/*     */   {
/* 218 */     StringBuffer sbuf = new StringBuffer();
/* 219 */     sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Layout.LINE_SEP);
/* 220 */     sbuf.append("<html>" + Layout.LINE_SEP);
/* 221 */     sbuf.append("<head>" + Layout.LINE_SEP);
/* 222 */     sbuf.append("<title>" + this.title + "</title>" + Layout.LINE_SEP);
/* 223 */     sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
/* 224 */     sbuf.append("<!--" + Layout.LINE_SEP);
/* 225 */     sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}" + Layout.LINE_SEP);
/* 226 */     sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}" + Layout.LINE_SEP);
/* 227 */     sbuf.append("-->" + Layout.LINE_SEP);
/* 228 */     sbuf.append("</style>" + Layout.LINE_SEP);
/* 229 */     sbuf.append("</head>" + Layout.LINE_SEP);
/* 230 */     sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">" + Layout.LINE_SEP);
/* 231 */     sbuf.append("<hr size=\"1\" noshade>" + Layout.LINE_SEP);
/* 232 */     sbuf.append("Log session start time " + new Date() + "<br>" + Layout.LINE_SEP);
/* 233 */     sbuf.append("<br>" + Layout.LINE_SEP);
/* 234 */     sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">" + Layout.LINE_SEP);
/* 235 */     sbuf.append("<tr>" + Layout.LINE_SEP);
/* 236 */     sbuf.append("<th>Time</th>" + Layout.LINE_SEP);
/* 237 */     sbuf.append("<th>Thread</th>" + Layout.LINE_SEP);
/* 238 */     sbuf.append("<th>Level</th>" + Layout.LINE_SEP);
/* 239 */     sbuf.append("<th>Category</th>" + Layout.LINE_SEP);
/* 240 */     if (this.locationInfo) {
/* 241 */       sbuf.append("<th>File:Line</th>" + Layout.LINE_SEP);
/*     */     }
/* 243 */     sbuf.append("<th>Message</th>" + Layout.LINE_SEP);
/* 244 */     sbuf.append("</tr>" + Layout.LINE_SEP);
/* 245 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public String getFooter()
/*     */   {
/* 253 */     StringBuffer sbuf = new StringBuffer();
/* 254 */     sbuf.append("</table>" + Layout.LINE_SEP);
/* 255 */     sbuf.append("<br>" + Layout.LINE_SEP);
/* 256 */     sbuf.append("</body></html>");
/* 257 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 265 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.HTMLLayout
 * JD-Core Version:    0.6.2
 */