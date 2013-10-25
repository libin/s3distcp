/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.apache.log4j.lf5.Log4JLogRecord;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogLevelFormatException;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ import org.apache.log4j.lf5.viewer.LogFactor5ErrorDialog;
/*     */ import org.apache.log4j.lf5.viewer.LogFactor5LoadingDialog;
/*     */ 
/*     */ public class LogFileParser
/*     */   implements Runnable
/*     */ {
/*     */   public static final String RECORD_DELIMITER = "[slf5s.start]";
/*     */   public static final String ATTRIBUTE_DELIMITER = "[slf5s.";
/*     */   public static final String DATE_DELIMITER = "[slf5s.DATE]";
/*     */   public static final String THREAD_DELIMITER = "[slf5s.THREAD]";
/*     */   public static final String CATEGORY_DELIMITER = "[slf5s.CATEGORY]";
/*     */   public static final String LOCATION_DELIMITER = "[slf5s.LOCATION]";
/*     */   public static final String MESSAGE_DELIMITER = "[slf5s.MESSAGE]";
/*     */   public static final String PRIORITY_DELIMITER = "[slf5s.PRIORITY]";
/*     */   public static final String NDC_DELIMITER = "[slf5s.NDC]";
/*  69 */   private static SimpleDateFormat _sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss,S");
/*     */   private LogBrokerMonitor _monitor;
/*     */   LogFactor5LoadingDialog _loadDialog;
/*  72 */   private InputStream _in = null;
/*     */ 
/*     */   public LogFileParser(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/*  79 */     this(new FileInputStream(file));
/*     */   }
/*     */ 
/*     */   public LogFileParser(InputStream stream) throws IOException {
/*  83 */     this._in = stream;
/*     */   }
/*     */ 
/*     */   public void parse(LogBrokerMonitor monitor)
/*     */     throws RuntimeException
/*     */   {
/*  95 */     this._monitor = monitor;
/*  96 */     Thread t = new Thread(this);
/*  97 */     t.start();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 106 */     int index = 0;
/* 107 */     int counter = 0;
/*     */ 
/* 109 */     boolean isLogFile = false;
/*     */ 
/* 111 */     this._loadDialog = new LogFactor5LoadingDialog(this._monitor.getBaseFrame(), "Loading file...");
/*     */     try
/*     */     {
/* 116 */       String logRecords = loadLogFile(this._in);
/*     */ 
/* 118 */       while ((counter = logRecords.indexOf("[slf5s.start]", index)) != -1) {
/* 119 */         LogRecord temp = createLogRecord(logRecords.substring(index, counter));
/* 120 */         isLogFile = true;
/*     */ 
/* 122 */         if (temp != null) {
/* 123 */           this._monitor.addMessage(temp);
/*     */         }
/*     */ 
/* 126 */         index = counter + "[slf5s.start]".length();
/*     */       }
/*     */ 
/* 129 */       if ((index < logRecords.length()) && (isLogFile)) {
/* 130 */         LogRecord temp = createLogRecord(logRecords.substring(index));
/*     */ 
/* 132 */         if (temp != null) {
/* 133 */           this._monitor.addMessage(temp);
/*     */         }
/*     */       }
/*     */ 
/* 137 */       if (!isLogFile) {
/* 138 */         throw new RuntimeException("Invalid log file format");
/*     */       }
/* 140 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 142 */           LogFileParser.this.destroyDialog();
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (RuntimeException e) {
/* 147 */       destroyDialog();
/* 148 */       displayError("Error - Invalid log file format.\nPlease see documentation on how to load log files.");
/*     */     }
/*     */     catch (IOException e) {
/* 151 */       destroyDialog();
/* 152 */       displayError("Error - Unable to load log file!");
/*     */     }
/*     */ 
/* 155 */     this._in = null;
/*     */   }
/*     */ 
/*     */   protected void displayError(String message)
/*     */   {
/* 162 */     LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(this._monitor.getBaseFrame(), message);
/*     */   }
/*     */ 
/*     */   private void destroyDialog()
/*     */   {
/* 171 */     this._loadDialog.hide();
/* 172 */     this._loadDialog.dispose();
/*     */   }
/*     */ 
/*     */   private String loadLogFile(InputStream stream)
/*     */     throws IOException
/*     */   {
/* 179 */     BufferedInputStream br = new BufferedInputStream(stream);
/*     */ 
/* 181 */     int count = 0;
/* 182 */     int size = br.available();
/*     */ 
/* 184 */     StringBuffer sb = null;
/* 185 */     if (size > 0)
/* 186 */       sb = new StringBuffer(size);
/*     */     else {
/* 188 */       sb = new StringBuffer(1024);
/*     */     }
/*     */ 
/* 191 */     while ((count = br.read()) != -1) {
/* 192 */       sb.append((char)count);
/*     */     }
/*     */ 
/* 195 */     br.close();
/* 196 */     br = null;
/* 197 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String parseAttribute(String name, String record)
/*     */   {
/* 203 */     int index = record.indexOf(name);
/*     */ 
/* 205 */     if (index == -1) {
/* 206 */       return null;
/*     */     }
/*     */ 
/* 209 */     return getAttribute(index, record);
/*     */   }
/*     */ 
/*     */   private long parseDate(String record) {
/*     */     try {
/* 214 */       String s = parseAttribute("[slf5s.DATE]", record);
/*     */ 
/* 216 */       if (s == null) {
/* 217 */         return 0L;
/*     */       }
/*     */ 
/* 220 */       Date d = _sdf.parse(s);
/*     */ 
/* 222 */       return d.getTime(); } catch (ParseException e) {
/*     */     }
/* 224 */     return 0L;
/*     */   }
/*     */ 
/*     */   private LogLevel parsePriority(String record)
/*     */   {
/* 229 */     String temp = parseAttribute("[slf5s.PRIORITY]", record);
/*     */ 
/* 231 */     if (temp != null) {
/*     */       try {
/* 233 */         return LogLevel.valueOf(temp);
/*     */       } catch (LogLevelFormatException e) {
/* 235 */         return LogLevel.DEBUG;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 240 */     return LogLevel.DEBUG;
/*     */   }
/*     */ 
/*     */   private String parseThread(String record) {
/* 244 */     return parseAttribute("[slf5s.THREAD]", record);
/*     */   }
/*     */ 
/*     */   private String parseCategory(String record) {
/* 248 */     return parseAttribute("[slf5s.CATEGORY]", record);
/*     */   }
/*     */ 
/*     */   private String parseLocation(String record) {
/* 252 */     return parseAttribute("[slf5s.LOCATION]", record);
/*     */   }
/*     */ 
/*     */   private String parseMessage(String record) {
/* 256 */     return parseAttribute("[slf5s.MESSAGE]", record);
/*     */   }
/*     */ 
/*     */   private String parseNDC(String record) {
/* 260 */     return parseAttribute("[slf5s.NDC]", record);
/*     */   }
/*     */ 
/*     */   private String parseThrowable(String record) {
/* 264 */     return getAttribute(record.length(), record);
/*     */   }
/*     */ 
/*     */   private LogRecord createLogRecord(String record) {
/* 268 */     if ((record == null) || (record.trim().length() == 0)) {
/* 269 */       return null;
/*     */     }
/*     */ 
/* 272 */     LogRecord lr = new Log4JLogRecord();
/* 273 */     lr.setMillis(parseDate(record));
/* 274 */     lr.setLevel(parsePriority(record));
/* 275 */     lr.setCategory(parseCategory(record));
/* 276 */     lr.setLocation(parseLocation(record));
/* 277 */     lr.setThreadDescription(parseThread(record));
/* 278 */     lr.setNDC(parseNDC(record));
/* 279 */     lr.setMessage(parseMessage(record));
/* 280 */     lr.setThrownStackTrace(parseThrowable(record));
/*     */ 
/* 282 */     return lr;
/*     */   }
/*     */ 
/*     */   private String getAttribute(int index, String record)
/*     */   {
/* 287 */     int start = record.lastIndexOf("[slf5s.", index - 1);
/*     */ 
/* 289 */     if (start == -1) {
/* 290 */       return record.substring(0, index);
/*     */     }
/*     */ 
/* 293 */     start = record.indexOf("]", start);
/*     */ 
/* 295 */     return record.substring(start + 1, index).trim();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.util.LogFileParser
 * JD-Core Version:    0.6.2
 */