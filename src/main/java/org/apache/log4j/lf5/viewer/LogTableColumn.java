/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LogTableColumn
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4275827753626456547L;
/*  37 */   public static final LogTableColumn DATE = new LogTableColumn("Date");
/*  38 */   public static final LogTableColumn THREAD = new LogTableColumn("Thread");
/*  39 */   public static final LogTableColumn MESSAGE_NUM = new LogTableColumn("Message #");
/*  40 */   public static final LogTableColumn LEVEL = new LogTableColumn("Level");
/*  41 */   public static final LogTableColumn NDC = new LogTableColumn("NDC");
/*  42 */   public static final LogTableColumn CATEGORY = new LogTableColumn("Category");
/*  43 */   public static final LogTableColumn MESSAGE = new LogTableColumn("Message");
/*  44 */   public static final LogTableColumn LOCATION = new LogTableColumn("Location");
/*  45 */   public static final LogTableColumn THROWN = new LogTableColumn("Thrown");
/*     */   protected String _label;
/*  63 */   private static LogTableColumn[] _log4JColumns = { DATE, THREAD, MESSAGE_NUM, LEVEL, NDC, CATEGORY, MESSAGE, LOCATION, THROWN };
/*     */ 
/*  66 */   private static Map _logTableColumnMap = new HashMap();
/*     */ 
/*     */   public LogTableColumn(String label)
/*     */   {
/*  75 */     this._label = label;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/*  86 */     return this._label;
/*     */   }
/*     */ 
/*     */   public static LogTableColumn valueOf(String column)
/*     */     throws LogTableColumnFormatException
/*     */   {
/*  99 */     LogTableColumn tableColumn = null;
/* 100 */     if (column != null) {
/* 101 */       column = column.trim();
/* 102 */       tableColumn = (LogTableColumn)_logTableColumnMap.get(column);
/*     */     }
/*     */ 
/* 105 */     if (tableColumn == null) {
/* 106 */       StringBuffer buf = new StringBuffer();
/* 107 */       buf.append("Error while trying to parse (" + column + ") into");
/* 108 */       buf.append(" a LogTableColumn.");
/* 109 */       throw new LogTableColumnFormatException(buf.toString());
/*     */     }
/* 111 */     return tableColumn;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 116 */     boolean equals = false;
/*     */ 
/* 118 */     if (((o instanceof LogTableColumn)) && 
/* 119 */       (getLabel() == ((LogTableColumn)o).getLabel()))
/*     */     {
/* 121 */       equals = true;
/*     */     }
/*     */ 
/* 125 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 129 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 133 */     return this._label;
/*     */   }
/*     */ 
/*     */   public static List getLogTableColumns()
/*     */   {
/* 141 */     return Arrays.asList(_log4JColumns);
/*     */   }
/*     */ 
/*     */   public static LogTableColumn[] getLogTableColumnArray() {
/* 145 */     return _log4JColumns;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  68 */     for (int i = 0; i < _log4JColumns.length; i++)
/*  69 */       _logTableColumnMap.put(_log4JColumns[i].getLabel(), _log4JColumns[i]);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTableColumn
 * JD-Core Version:    0.6.2
 */