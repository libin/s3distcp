/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.apache.log4j.lf5.util.DateFormatManager;
/*     */ 
/*     */ public class LogTable extends JTable
/*     */ {
/*     */   private static final long serialVersionUID = 4867085140195148458L;
/*  57 */   protected int _rowHeight = 30;
/*     */   protected JTextArea _detailTextArea;
/*  61 */   protected int _numCols = 9;
/*  62 */   protected TableColumn[] _tableColumns = new TableColumn[this._numCols];
/*  63 */   protected int[] _colWidths = { 40, 40, 40, 70, 70, 360, 440, 200, 60 };
/*  64 */   protected LogTableColumn[] _colNames = LogTableColumn.getLogTableColumnArray();
/*  65 */   protected int _colDate = 0;
/*  66 */   protected int _colThread = 1;
/*  67 */   protected int _colMessageNum = 2;
/*  68 */   protected int _colLevel = 3;
/*  69 */   protected int _colNDC = 4;
/*  70 */   protected int _colCategory = 5;
/*  71 */   protected int _colMessage = 6;
/*  72 */   protected int _colLocation = 7;
/*  73 */   protected int _colThrown = 8;
/*     */ 
/*  75 */   protected DateFormatManager _dateFormatManager = null;
/*     */ 
/*     */   public LogTable(JTextArea detailTextArea)
/*     */   {
/*  88 */     init();
/*     */ 
/*  90 */     this._detailTextArea = detailTextArea;
/*     */ 
/*  92 */     setModel(new FilteredLogTableModel());
/*     */ 
/*  94 */     Enumeration columns = getColumnModel().getColumns();
/*  95 */     int i = 0;
/*  96 */     while (columns.hasMoreElements()) {
/*  97 */       TableColumn col = (TableColumn)columns.nextElement();
/*  98 */       col.setCellRenderer(new LogTableRowRenderer());
/*  99 */       col.setPreferredWidth(this._colWidths[i]);
/*     */ 
/* 101 */       this._tableColumns[i] = col;
/* 102 */       i++;
/*     */     }
/*     */ 
/* 105 */     ListSelectionModel rowSM = getSelectionModel();
/* 106 */     rowSM.addListSelectionListener(new LogTableListSelectionListener(this));
/*     */   }
/*     */ 
/*     */   public DateFormatManager getDateFormatManager()
/*     */   {
/* 119 */     return this._dateFormatManager;
/*     */   }
/*     */ 
/*     */   public void setDateFormatManager(DateFormatManager dfm)
/*     */   {
/* 126 */     this._dateFormatManager = dfm;
/*     */   }
/*     */ 
/*     */   public synchronized void clearLogRecords()
/*     */   {
/* 134 */     getFilteredLogTableModel().clear();
/*     */   }
/*     */ 
/*     */   public FilteredLogTableModel getFilteredLogTableModel() {
/* 138 */     return (FilteredLogTableModel)getModel();
/*     */   }
/*     */ 
/*     */   public void setDetailedView()
/*     */   {
/* 144 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 146 */     for (int f = 0; f < this._numCols; f++) {
/* 147 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/*     */ 
/* 150 */     for (int i = 0; i < this._numCols; i++) {
/* 151 */       model.addColumn(this._tableColumns[i]);
/*     */     }
/*     */ 
/* 154 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setView(List columns) {
/* 158 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 161 */     for (int f = 0; f < this._numCols; f++) {
/* 162 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/* 164 */     Iterator selectedColumns = columns.iterator();
/* 165 */     Vector columnNameAndNumber = getColumnNameAndNumber();
/* 166 */     while (selectedColumns.hasNext())
/*     */     {
/* 168 */       model.addColumn(this._tableColumns[columnNameAndNumber.indexOf(selectedColumns.next())]);
/*     */     }
/*     */ 
/* 172 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setFont(Font font) {
/* 176 */     super.setFont(font);
/* 177 */     Graphics g = getGraphics();
/* 178 */     if (g != null) {
/* 179 */       FontMetrics fm = g.getFontMetrics(font);
/* 180 */       int height = fm.getHeight();
/* 181 */       this._rowHeight = (height + height / 3);
/* 182 */       setRowHeight(this._rowHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 194 */     setRowHeight(this._rowHeight);
/* 195 */     setSelectionMode(0);
/*     */   }
/*     */ 
/*     */   protected Vector getColumnNameAndNumber()
/*     */   {
/* 200 */     Vector columnNameAndNumber = new Vector();
/* 201 */     for (int i = 0; i < this._colNames.length; i++) {
/* 202 */       columnNameAndNumber.add(i, this._colNames[i]);
/*     */     }
/* 204 */     return columnNameAndNumber;
/*     */   }
/*     */ 
/*     */   class LogTableListSelectionListener
/*     */     implements ListSelectionListener
/*     */   {
/*     */     protected JTable _table;
/*     */ 
/*     */     public LogTableListSelectionListener(JTable table)
/*     */     {
/* 219 */       this._table = table;
/*     */     }
/*     */ 
/*     */     public void valueChanged(ListSelectionEvent e)
/*     */     {
/* 224 */       if (e.getValueIsAdjusting()) {
/* 225 */         return;
/*     */       }
/*     */ 
/* 228 */       ListSelectionModel lsm = (ListSelectionModel)e.getSource();
/* 229 */       if (!lsm.isSelectionEmpty())
/*     */       {
/* 232 */         StringBuffer buf = new StringBuffer();
/* 233 */         int selectedRow = lsm.getMinSelectionIndex();
/*     */ 
/* 235 */         for (int i = 0; i < LogTable.this._numCols - 1; i++) {
/* 236 */           String value = "";
/* 237 */           Object obj = this._table.getModel().getValueAt(selectedRow, i);
/* 238 */           if (obj != null) {
/* 239 */             value = obj.toString();
/*     */           }
/*     */ 
/* 242 */           buf.append(LogTable.this._colNames[i] + ":");
/* 243 */           buf.append("\t");
/*     */ 
/* 245 */           if ((i == LogTable.this._colThread) || (i == LogTable.this._colMessage) || (i == LogTable.this._colLevel)) {
/* 246 */             buf.append("\t");
/*     */           }
/*     */ 
/* 249 */           if ((i == LogTable.this._colDate) || (i == LogTable.this._colNDC)) {
/* 250 */             buf.append("\t\t");
/*     */           }
/*     */ 
/* 258 */           buf.append(value);
/* 259 */           buf.append("\n");
/*     */         }
/* 261 */         buf.append(LogTable.this._colNames[(LogTable.this._numCols - 1)] + ":\n");
/* 262 */         Object obj = this._table.getModel().getValueAt(selectedRow, LogTable.this._numCols - 1);
/* 263 */         if (obj != null) {
/* 264 */           buf.append(obj.toString());
/*     */         }
/*     */ 
/* 267 */         LogTable.this._detailTextArea.setText(buf.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTable
 * JD-Core Version:    0.6.2
 */