/*     */ package org.apache.log4j.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.PatternLayout;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class JDBCAppender extends AppenderSkeleton
/*     */   implements Appender
/*     */ {
/*  84 */   protected String databaseURL = "jdbc:odbc:myDB";
/*     */ 
/*  89 */   protected String databaseUser = "me";
/*     */ 
/*  94 */   protected String databasePassword = "mypassword";
/*     */ 
/* 103 */   protected Connection connection = null;
/*     */ 
/* 114 */   protected String sqlStatement = "";
/*     */ 
/* 120 */   protected int bufferSize = 1;
/*     */   protected ArrayList buffer;
/*     */   protected ArrayList removes;
/* 132 */   private boolean locationInfo = false;
/*     */ 
/*     */   public JDBCAppender()
/*     */   {
/* 136 */     this.buffer = new ArrayList(this.bufferSize);
/* 137 */     this.removes = new ArrayList(this.bufferSize);
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 148 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/* 166 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 174 */     event.getNDC();
/* 175 */     event.getThreadName();
/*     */ 
/* 177 */     event.getMDCCopy();
/* 178 */     if (this.locationInfo) {
/* 179 */       event.getLocationInformation();
/*     */     }
/* 181 */     event.getRenderedMessage();
/* 182 */     event.getThrowableStrRep();
/* 183 */     this.buffer.add(event);
/*     */ 
/* 185 */     if (this.buffer.size() >= this.bufferSize)
/* 186 */       flushBuffer();
/*     */   }
/*     */ 
/*     */   protected String getLogStatement(LoggingEvent event)
/*     */   {
/* 198 */     return getLayout().format(event);
/*     */   }
/*     */ 
/*     */   protected void execute(String sql)
/*     */     throws SQLException
/*     */   {
/* 211 */     Connection con = null;
/* 212 */     Statement stmt = null;
/*     */     try
/*     */     {
/* 215 */       con = getConnection();
/*     */ 
/* 217 */       stmt = con.createStatement();
/* 218 */       stmt.executeUpdate(sql);
/*     */     } finally {
/* 220 */       if (stmt != null) {
/* 221 */         stmt.close();
/*     */       }
/* 223 */       closeConnection(con);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void closeConnection(Connection con)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 247 */     if (!DriverManager.getDrivers().hasMoreElements()) {
/* 248 */       setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
/*     */     }
/* 250 */     if (this.connection == null) {
/* 251 */       this.connection = DriverManager.getConnection(this.databaseURL, this.databaseUser, this.databasePassword);
/*     */     }
/*     */ 
/* 255 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 264 */     flushBuffer();
/*     */     try
/*     */     {
/* 267 */       if ((this.connection != null) && (!this.connection.isClosed()))
/* 268 */         this.connection.close();
/*     */     } catch (SQLException e) {
/* 270 */       this.errorHandler.error("Error closing connection", e, 0);
/*     */     }
/* 272 */     this.closed = true;
/*     */   }
/*     */ 
/*     */   public void flushBuffer()
/*     */   {
/* 284 */     this.removes.ensureCapacity(this.buffer.size());
/* 285 */     for (Iterator i = this.buffer.iterator(); i.hasNext(); ) {
/* 286 */       LoggingEvent logEvent = (LoggingEvent)i.next();
/*     */       try {
/* 288 */         String sql = getLogStatement(logEvent);
/* 289 */         execute(sql);
/*     */       }
/*     */       catch (SQLException e) {
/* 292 */         this.errorHandler.error("Failed to excute sql", e, 2);
/*     */       }
/*     */       finally {
/* 295 */         this.removes.add(logEvent);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 300 */     this.buffer.removeAll(this.removes);
/*     */ 
/* 303 */     this.removes.clear();
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 309 */     close();
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 317 */     return true;
/*     */   }
/*     */ 
/*     */   public void setSql(String s)
/*     */   {
/* 325 */     this.sqlStatement = s;
/* 326 */     if (getLayout() == null) {
/* 327 */       setLayout(new PatternLayout(s));
/*     */     }
/*     */     else
/* 330 */       ((PatternLayout)getLayout()).setConversionPattern(s);
/*     */   }
/*     */ 
/*     */   public String getSql()
/*     */   {
/* 339 */     return this.sqlStatement;
/*     */   }
/*     */ 
/*     */   public void setUser(String user)
/*     */   {
/* 344 */     this.databaseUser = user;
/*     */   }
/*     */ 
/*     */   public void setURL(String url)
/*     */   {
/* 349 */     this.databaseURL = url;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 354 */     this.databasePassword = password;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int newBufferSize)
/*     */   {
/* 359 */     this.bufferSize = newBufferSize;
/* 360 */     this.buffer.ensureCapacity(this.bufferSize);
/* 361 */     this.removes.ensureCapacity(this.bufferSize);
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 366 */     return this.databaseUser;
/*     */   }
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 371 */     return this.databaseURL;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 376 */     return this.databasePassword;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 381 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setDriver(String driverClass)
/*     */   {
/*     */     try
/*     */     {
/* 391 */       Class.forName(driverClass);
/*     */     } catch (Exception e) {
/* 393 */       this.errorHandler.error("Failed to load driver", e, 0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jdbc.JDBCAppender
 * JD-Core Version:    0.6.2
 */