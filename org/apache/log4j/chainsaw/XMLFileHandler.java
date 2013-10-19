/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.log4j.Level;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ class XMLFileHandler extends DefaultHandler
/*     */ {
/*     */   private static final String TAG_EVENT = "log4j:event";
/*     */   private static final String TAG_MESSAGE = "log4j:message";
/*     */   private static final String TAG_NDC = "log4j:NDC";
/*     */   private static final String TAG_THROWABLE = "log4j:throwable";
/*     */   private static final String TAG_LOCATION_INFO = "log4j:locationInfo";
/*     */   private final MyTableModel mModel;
/*     */   private int mNumEvents;
/*     */   private long mTimeStamp;
/*     */   private Level mLevel;
/*     */   private String mCategoryName;
/*     */   private String mNDC;
/*     */   private String mThreadName;
/*     */   private String mMessage;
/*     */   private String[] mThrowableStrRep;
/*     */   private String mLocationDetails;
/*  68 */   private final StringBuffer mBuf = new StringBuffer();
/*     */ 
/*     */   XMLFileHandler(MyTableModel aModel)
/*     */   {
/*  76 */     this.mModel = aModel;
/*     */   }
/*     */ 
/*     */   public void startDocument()
/*     */     throws SAXException
/*     */   {
/*  83 */     this.mNumEvents = 0;
/*     */   }
/*     */ 
/*     */   public void characters(char[] aChars, int aStart, int aLength)
/*     */   {
/*  88 */     this.mBuf.append(String.valueOf(aChars, aStart, aLength));
/*     */   }
/*     */ 
/*     */   public void endElement(String aNamespaceURI, String aLocalName, String aQName)
/*     */   {
/*  96 */     if ("log4j:event".equals(aQName)) {
/*  97 */       addEvent();
/*  98 */       resetData();
/*  99 */     } else if ("log4j:NDC".equals(aQName)) {
/* 100 */       this.mNDC = this.mBuf.toString();
/* 101 */     } else if ("log4j:message".equals(aQName)) {
/* 102 */       this.mMessage = this.mBuf.toString();
/* 103 */     } else if ("log4j:throwable".equals(aQName)) {
/* 104 */       StringTokenizer st = new StringTokenizer(this.mBuf.toString(), "\n\t");
/*     */ 
/* 106 */       this.mThrowableStrRep = new String[st.countTokens()];
/* 107 */       if (this.mThrowableStrRep.length > 0) {
/* 108 */         this.mThrowableStrRep[0] = st.nextToken();
/* 109 */         for (int i = 1; i < this.mThrowableStrRep.length; i++)
/* 110 */           this.mThrowableStrRep[i] = ("\t" + st.nextToken());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startElement(String aNamespaceURI, String aLocalName, String aQName, Attributes aAtts)
/*     */   {
/* 122 */     this.mBuf.setLength(0);
/*     */ 
/* 124 */     if ("log4j:event".equals(aQName)) {
/* 125 */       this.mThreadName = aAtts.getValue("thread");
/* 126 */       this.mTimeStamp = Long.parseLong(aAtts.getValue("timestamp"));
/* 127 */       this.mCategoryName = aAtts.getValue("logger");
/* 128 */       this.mLevel = Level.toLevel(aAtts.getValue("level"));
/* 129 */     } else if ("log4j:locationInfo".equals(aQName)) {
/* 130 */       this.mLocationDetails = (aAtts.getValue("class") + "." + aAtts.getValue("method") + "(" + aAtts.getValue("file") + ":" + aAtts.getValue("line") + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   int getNumEvents()
/*     */   {
/* 139 */     return this.mNumEvents;
/*     */   }
/*     */ 
/*     */   private void addEvent()
/*     */   {
/* 148 */     this.mModel.addEvent(new EventDetails(this.mTimeStamp, this.mLevel, this.mCategoryName, this.mNDC, this.mThreadName, this.mMessage, this.mThrowableStrRep, this.mLocationDetails));
/*     */ 
/* 156 */     this.mNumEvents += 1;
/*     */   }
/*     */ 
/*     */   private void resetData()
/*     */   {
/* 161 */     this.mTimeStamp = 0L;
/* 162 */     this.mLevel = null;
/* 163 */     this.mCategoryName = null;
/* 164 */     this.mNDC = null;
/* 165 */     this.mThreadName = null;
/* 166 */     this.mMessage = null;
/* 167 */     this.mThrowableStrRep = null;
/* 168 */     this.mLocationDetails = null;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.chainsaw.XMLFileHandler
 * JD-Core Version:    0.6.2
 */