/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.spi.AppenderAttachable;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class AppenderAttachableImpl
/*     */   implements AppenderAttachable
/*     */ {
/*     */   protected Vector appenderList;
/*     */ 
/*     */   public void addAppender(Appender newAppender)
/*     */   {
/*  45 */     if (newAppender == null) {
/*  46 */       return;
/*     */     }
/*  48 */     if (this.appenderList == null) {
/*  49 */       this.appenderList = new Vector(1);
/*     */     }
/*  51 */     if (!this.appenderList.contains(newAppender))
/*  52 */       this.appenderList.addElement(newAppender);
/*     */   }
/*     */ 
/*     */   public int appendLoopOnAppenders(LoggingEvent event)
/*     */   {
/*  59 */     int size = 0;
/*     */ 
/*  62 */     if (this.appenderList != null) {
/*  63 */       size = this.appenderList.size();
/*  64 */       for (int i = 0; i < size; i++) {
/*  65 */         Appender appender = (Appender)this.appenderList.elementAt(i);
/*  66 */         appender.doAppend(event);
/*     */       }
/*     */     }
/*  69 */     return size;
/*     */   }
/*     */ 
/*     */   public Enumeration getAllAppenders()
/*     */   {
/*  81 */     if (this.appenderList == null) {
/*  82 */       return null;
/*     */     }
/*  84 */     return this.appenderList.elements();
/*     */   }
/*     */ 
/*     */   public Appender getAppender(String name)
/*     */   {
/*  96 */     if ((this.appenderList == null) || (name == null)) {
/*  97 */       return null;
/*     */     }
/*  99 */     int size = this.appenderList.size();
/*     */ 
/* 101 */     for (int i = 0; i < size; i++) {
/* 102 */       Appender appender = (Appender)this.appenderList.elementAt(i);
/* 103 */       if (name.equals(appender.getName()))
/* 104 */         return appender;
/*     */     }
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isAttached(Appender appender)
/*     */   {
/* 117 */     if ((this.appenderList == null) || (appender == null)) {
/* 118 */       return false;
/*     */     }
/* 120 */     int size = this.appenderList.size();
/*     */ 
/* 122 */     for (int i = 0; i < size; i++) {
/* 123 */       Appender a = (Appender)this.appenderList.elementAt(i);
/* 124 */       if (a == appender)
/* 125 */         return true;
/*     */     }
/* 127 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAllAppenders()
/*     */   {
/* 137 */     if (this.appenderList != null) {
/* 138 */       int len = this.appenderList.size();
/* 139 */       for (int i = 0; i < len; i++) {
/* 140 */         Appender a = (Appender)this.appenderList.elementAt(i);
/* 141 */         a.close();
/*     */       }
/* 143 */       this.appenderList.removeAllElements();
/* 144 */       this.appenderList = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAppender(Appender appender)
/*     */   {
/* 154 */     if ((appender == null) || (this.appenderList == null))
/* 155 */       return;
/* 156 */     this.appenderList.removeElement(appender);
/*     */   }
/*     */ 
/*     */   public void removeAppender(String name)
/*     */   {
/* 166 */     if ((name == null) || (this.appenderList == null)) return;
/* 167 */     int size = this.appenderList.size();
/* 168 */     for (int i = 0; i < size; i++)
/* 169 */       if (name.equals(((Appender)this.appenderList.elementAt(i)).getName())) {
/* 170 */         this.appenderList.removeElementAt(i);
/* 171 */         break;
/*     */       }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.AppenderAttachableImpl
 * JD-Core Version:    0.6.2
 */