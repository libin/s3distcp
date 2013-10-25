/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ class MyTableModel extends AbstractTableModel
/*     */ {
/*  42 */   private static final Logger LOG = Logger.getLogger(MyTableModel.class);
/*     */ 
/*  45 */   private static final Comparator MY_COMP = new Comparator()
/*     */   {
/*     */     public int compare(Object aObj1, Object aObj2)
/*     */     {
/*  49 */       if ((aObj1 == null) && (aObj2 == null))
/*  50 */         return 0;
/*  51 */       if (aObj1 == null)
/*  52 */         return -1;
/*  53 */       if (aObj2 == null) {
/*  54 */         return 1;
/*     */       }
/*     */ 
/*  58 */       EventDetails le1 = (EventDetails)aObj1;
/*  59 */       EventDetails le2 = (EventDetails)aObj2;
/*     */ 
/*  61 */       if (le1.getTimeStamp() < le2.getTimeStamp()) {
/*  62 */         return 1;
/*     */       }
/*     */ 
/*  65 */       return -1;
/*     */     }
/*  45 */   };
/*     */ 
/* 112 */   private static final String[] COL_NAMES = { "Time", "Priority", "Trace", "Category", "NDC", "Message" };
/*     */ 
/* 116 */   private static final EventDetails[] EMPTY_LIST = new EventDetails[0];
/*     */ 
/* 119 */   private static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(3, 2);
/*     */ 
/* 123 */   private final Object mLock = new Object();
/*     */ 
/* 125 */   private final SortedSet mAllEvents = new TreeSet(MY_COMP);
/*     */ 
/* 127 */   private EventDetails[] mFilteredEvents = EMPTY_LIST;
/*     */ 
/* 129 */   private final List mPendingEvents = new ArrayList();
/*     */ 
/* 131 */   private boolean mPaused = false;
/*     */ 
/* 134 */   private String mThreadFilter = "";
/*     */ 
/* 136 */   private String mMessageFilter = "";
/*     */ 
/* 138 */   private String mNDCFilter = "";
/*     */ 
/* 140 */   private String mCategoryFilter = "";
/*     */ 
/* 142 */   private Priority mPriorityFilter = Priority.DEBUG;
/*     */ 
/*     */   MyTableModel()
/*     */   {
/* 150 */     Thread t = new Thread(new Processor(null));
/* 151 */     t.setDaemon(true);
/* 152 */     t.start();
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 162 */     synchronized (this.mLock) {
/* 163 */       return this.mFilteredEvents.length;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/* 170 */     return COL_NAMES.length;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int aCol)
/*     */   {
/* 176 */     return COL_NAMES[aCol];
/*     */   }
/*     */ 
/*     */   public Class getColumnClass(int aCol)
/*     */   {
/* 182 */     return Object.class;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int aRow, int aCol)
/*     */   {
/* 187 */     synchronized (this.mLock) {
/* 188 */       EventDetails event = this.mFilteredEvents[aRow];
/*     */ 
/* 190 */       if (aCol == 0)
/* 191 */         return DATE_FORMATTER.format(new Date(event.getTimeStamp()));
/* 192 */       if (aCol == 1)
/* 193 */         return event.getPriority();
/* 194 */       if (aCol == 2) {
/* 195 */         return event.getThrowableStrRep() == null ? Boolean.FALSE : Boolean.TRUE;
/*     */       }
/* 197 */       if (aCol == 3)
/* 198 */         return event.getCategoryName();
/* 199 */       if (aCol == 4) {
/* 200 */         return event.getNDC();
/*     */       }
/* 202 */       return event.getMessage();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPriorityFilter(Priority aPriority)
/*     */   {
/* 217 */     synchronized (this.mLock) {
/* 218 */       this.mPriorityFilter = aPriority;
/* 219 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setThreadFilter(String aStr)
/*     */   {
/* 229 */     synchronized (this.mLock) {
/* 230 */       this.mThreadFilter = aStr.trim();
/* 231 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMessageFilter(String aStr)
/*     */   {
/* 241 */     synchronized (this.mLock) {
/* 242 */       this.mMessageFilter = aStr.trim();
/* 243 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNDCFilter(String aStr)
/*     */   {
/* 253 */     synchronized (this.mLock) {
/* 254 */       this.mNDCFilter = aStr.trim();
/* 255 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCategoryFilter(String aStr)
/*     */   {
/* 265 */     synchronized (this.mLock) {
/* 266 */       this.mCategoryFilter = aStr.trim();
/* 267 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addEvent(EventDetails aEvent)
/*     */   {
/* 277 */     synchronized (this.mLock) {
/* 278 */       this.mPendingEvents.add(aEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 286 */     synchronized (this.mLock) {
/* 287 */       this.mAllEvents.clear();
/* 288 */       this.mFilteredEvents = new EventDetails[0];
/* 289 */       this.mPendingEvents.clear();
/* 290 */       fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void toggle()
/*     */   {
/* 296 */     synchronized (this.mLock) {
/* 297 */       this.mPaused = (!this.mPaused);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isPaused()
/*     */   {
/* 303 */     synchronized (this.mLock) {
/* 304 */       return this.mPaused;
/*     */     }
/*     */   }
/*     */ 
/*     */   public EventDetails getEventDetails(int aRow)
/*     */   {
/* 315 */     synchronized (this.mLock) {
/* 316 */       return this.mFilteredEvents[aRow];
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateFilteredEvents(boolean aInsertedToFront)
/*     */   {
/* 331 */     long start = System.currentTimeMillis();
/* 332 */     List filtered = new ArrayList();
/* 333 */     int size = this.mAllEvents.size();
/* 334 */     Iterator it = this.mAllEvents.iterator();
/*     */ 
/* 336 */     while (it.hasNext()) {
/* 337 */       EventDetails event = (EventDetails)it.next();
/* 338 */       if (matchFilter(event)) {
/* 339 */         filtered.add(event);
/*     */       }
/*     */     }
/*     */ 
/* 343 */     EventDetails lastFirst = this.mFilteredEvents.length == 0 ? null : this.mFilteredEvents[0];
/*     */ 
/* 346 */     this.mFilteredEvents = ((EventDetails[])filtered.toArray(EMPTY_LIST));
/*     */ 
/* 348 */     if ((aInsertedToFront) && (lastFirst != null)) {
/* 349 */       int index = filtered.indexOf(lastFirst);
/* 350 */       if (index < 1) {
/* 351 */         LOG.warn("In strange state");
/* 352 */         fireTableDataChanged();
/*     */       } else {
/* 354 */         fireTableRowsInserted(0, index - 1);
/*     */       }
/*     */     } else {
/* 357 */       fireTableDataChanged();
/*     */     }
/*     */ 
/* 360 */     long end = System.currentTimeMillis();
/* 361 */     LOG.debug("Total time [ms]: " + (end - start) + " in update, size: " + size);
/*     */   }
/*     */ 
/*     */   private boolean matchFilter(EventDetails aEvent)
/*     */   {
/* 372 */     if ((aEvent.getPriority().isGreaterOrEqual(this.mPriorityFilter)) && (aEvent.getThreadName().indexOf(this.mThreadFilter) >= 0) && (aEvent.getCategoryName().indexOf(this.mCategoryFilter) >= 0) && ((this.mNDCFilter.length() == 0) || ((aEvent.getNDC() != null) && (aEvent.getNDC().indexOf(this.mNDCFilter) >= 0))))
/*     */     {
/* 379 */       String rm = aEvent.getMessage();
/* 380 */       if (rm == null)
/*     */       {
/* 382 */         return this.mMessageFilter.length() == 0;
/*     */       }
/* 384 */       return rm.indexOf(this.mMessageFilter) >= 0;
/*     */     }
/*     */ 
/* 388 */     return false;
/*     */   }
/*     */ 
/*     */   private class Processor
/*     */     implements Runnable
/*     */   {
/*     */     private Processor()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true)
/*     */       {
/*     */         try
/*     */         {
/*  80 */           Thread.sleep(1000L);
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/*  85 */         synchronized (MyTableModel.this.mLock) {
/*  86 */           if (!MyTableModel.this.mPaused)
/*     */           {
/*  90 */             boolean toHead = true;
/*  91 */             boolean needUpdate = false;
/*  92 */             Iterator it = MyTableModel.this.mPendingEvents.iterator();
/*  93 */             while (it.hasNext()) {
/*  94 */               EventDetails event = (EventDetails)it.next();
/*  95 */               MyTableModel.this.mAllEvents.add(event);
/*  96 */               toHead = (toHead) && (event == MyTableModel.this.mAllEvents.first());
/*  97 */               needUpdate = (needUpdate) || (MyTableModel.this.matchFilter(event));
/*     */             }
/*  99 */             MyTableModel.this.mPendingEvents.clear();
/*     */ 
/* 101 */             if (needUpdate)
/* 102 */               MyTableModel.this.updateFilteredEvents(toHead);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     Processor(MyTableModel.1 x1)
/*     */     {
/*  73 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.chainsaw.MyTableModel
 * JD-Core Version:    0.6.2
 */