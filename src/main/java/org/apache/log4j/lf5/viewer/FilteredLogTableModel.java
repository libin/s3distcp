/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ import org.apache.log4j.lf5.LogRecordFilter;
/*     */ import org.apache.log4j.lf5.PassingLogRecordFilter;
/*     */ 
/*     */ public class FilteredLogTableModel extends AbstractTableModel
/*     */ {
/*  49 */   protected LogRecordFilter _filter = new PassingLogRecordFilter();
/*  50 */   protected List _allRecords = new ArrayList();
/*     */   protected List _filteredRecords;
/*  52 */   protected int _maxNumberOfLogRecords = 5000;
/*  53 */   protected String[] _colNames = { "Date", "Thread", "Message #", "Level", "NDC", "Category", "Message", "Location", "Thrown" };
/*     */ 
/*     */   public void setLogRecordFilter(LogRecordFilter filter)
/*     */   {
/*  80 */     this._filter = filter;
/*     */   }
/*     */ 
/*     */   public LogRecordFilter getLogRecordFilter() {
/*  84 */     return this._filter;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int i) {
/*  88 */     return this._colNames[i];
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/*  92 */     return this._colNames.length;
/*     */   }
/*     */ 
/*     */   public int getRowCount() {
/*  96 */     return getFilteredRecords().size();
/*     */   }
/*     */ 
/*     */   public int getTotalRowCount() {
/* 100 */     return this._allRecords.size();
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int col) {
/* 104 */     LogRecord record = getFilteredRecord(row);
/* 105 */     return getColumn(col, record);
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfLogRecords(int maxNumRecords) {
/* 109 */     if (maxNumRecords > 0)
/* 110 */       this._maxNumberOfLogRecords = maxNumRecords;
/*     */   }
/*     */ 
/*     */   public synchronized boolean addLogRecord(LogRecord record)
/*     */   {
/* 117 */     this._allRecords.add(record);
/*     */ 
/* 119 */     if (!this._filter.passes(record)) {
/* 120 */       return false;
/*     */     }
/* 122 */     getFilteredRecords().add(record);
/* 123 */     fireTableRowsInserted(getRowCount(), getRowCount());
/* 124 */     trimRecords();
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void refresh()
/*     */   {
/* 133 */     this._filteredRecords = createFilteredRecordsList();
/* 134 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public synchronized void fastRefresh() {
/* 138 */     this._filteredRecords.remove(0);
/* 139 */     fireTableRowsDeleted(0, 0);
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 147 */     this._allRecords.clear();
/* 148 */     this._filteredRecords.clear();
/* 149 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   protected List getFilteredRecords()
/*     */   {
/* 157 */     if (this._filteredRecords == null) {
/* 158 */       refresh();
/*     */     }
/* 160 */     return this._filteredRecords;
/*     */   }
/*     */ 
/*     */   protected List createFilteredRecordsList() {
/* 164 */     List result = new ArrayList();
/* 165 */     Iterator records = this._allRecords.iterator();
/*     */ 
/* 167 */     while (records.hasNext()) {
/* 168 */       LogRecord current = (LogRecord)records.next();
/* 169 */       if (this._filter.passes(current)) {
/* 170 */         result.add(current);
/*     */       }
/*     */     }
/* 173 */     return result;
/*     */   }
/*     */ 
/*     */   protected LogRecord getFilteredRecord(int row) {
/* 177 */     List records = getFilteredRecords();
/* 178 */     int size = records.size();
/* 179 */     if (row < size) {
/* 180 */       return (LogRecord)records.get(row);
/*     */     }
/*     */ 
/* 186 */     return (LogRecord)records.get(size - 1);
/*     */   }
/*     */ 
/*     */   protected Object getColumn(int col, LogRecord lr)
/*     */   {
/* 191 */     if (lr == null) {
/* 192 */       return "NULL Column";
/*     */     }
/* 194 */     String date = new Date(lr.getMillis()).toString();
/* 195 */     switch (col) {
/*     */     case 0:
/* 197 */       return date + " (" + lr.getMillis() + ")";
/*     */     case 1:
/* 199 */       return lr.getThreadDescription();
/*     */     case 2:
/* 201 */       return new Long(lr.getSequenceNumber());
/*     */     case 3:
/* 203 */       return lr.getLevel();
/*     */     case 4:
/* 205 */       return lr.getNDC();
/*     */     case 5:
/* 207 */       return lr.getCategory();
/*     */     case 6:
/* 209 */       return lr.getMessage();
/*     */     case 7:
/* 211 */       return lr.getLocation();
/*     */     case 8:
/* 213 */       return lr.getThrownStackTrace();
/*     */     }
/* 215 */     String message = "The column number " + col + "must be between 0 and 8";
/* 216 */     throw new IllegalArgumentException(message);
/*     */   }
/*     */ 
/*     */   protected void trimRecords()
/*     */   {
/* 227 */     if (needsTrimming())
/* 228 */       trimOldestRecords();
/*     */   }
/*     */ 
/*     */   protected boolean needsTrimming()
/*     */   {
/* 233 */     return this._allRecords.size() > this._maxNumberOfLogRecords;
/*     */   }
/*     */ 
/*     */   protected void trimOldestRecords() {
/* 237 */     synchronized (this._allRecords) {
/* 238 */       int trim = numberOfRecordsToTrim();
/* 239 */       if (trim > 1) {
/* 240 */         List oldRecords = this._allRecords.subList(0, trim);
/*     */ 
/* 242 */         oldRecords.clear();
/* 243 */         refresh();
/*     */       } else {
/* 245 */         this._allRecords.remove(0);
/* 246 */         fastRefresh();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int numberOfRecordsToTrim()
/*     */   {
/* 256 */     return this._allRecords.size() - this._maxNumberOfLogRecords;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.FilteredLogTableModel
 * JD-Core Version:    0.6.2
 */