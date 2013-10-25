/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class LocationInfo
/*     */   implements Serializable
/*     */ {
/*     */   transient String lineNumber;
/*     */   transient String fileName;
/*     */   transient String className;
/*     */   transient String methodName;
/*     */   public String fullInfo;
/*  60 */   private static StringWriter sw = new StringWriter();
/*  61 */   private static PrintWriter pw = new PrintWriter(sw);
/*     */   private static Method getStackTraceMethod;
/*     */   private static Method getClassNameMethod;
/*     */   private static Method getMethodNameMethod;
/*     */   private static Method getFileNameMethod;
/*     */   private static Method getLineNumberMethod;
/*     */   public static final String NA = "?";
/*     */   static final long serialVersionUID = -1325822038990805636L;
/*  82 */   public static final LocationInfo NA_LOCATION_INFO = new LocationInfo("?", "?", "?", "?");
/*     */ 
/*  88 */   static boolean inVisualAge = false;
/*     */ 
/*     */   public LocationInfo(Throwable t, String fqnOfCallingClass)
/*     */   {
/* 134 */     if ((t == null) || (fqnOfCallingClass == null))
/* 135 */       return;
/* 136 */     if (getLineNumberMethod != null)
/*     */       try {
/* 138 */         Object[] noArgs = null;
/* 139 */         Object[] elements = (Object[])getStackTraceMethod.invoke(t, noArgs);
/* 140 */         String prevClass = "?";
/* 141 */         for (int i = elements.length - 1; i >= 0; i--) {
/* 142 */           String thisClass = (String)getClassNameMethod.invoke(elements[i], noArgs);
/* 143 */           if (fqnOfCallingClass.equals(thisClass)) {
/* 144 */             int caller = i + 1;
/* 145 */             if (caller < elements.length) {
/* 146 */               this.className = prevClass;
/* 147 */               this.methodName = ((String)getMethodNameMethod.invoke(elements[caller], noArgs));
/* 148 */               this.fileName = ((String)getFileNameMethod.invoke(elements[caller], noArgs));
/* 149 */               if (this.fileName == null) {
/* 150 */                 this.fileName = "?";
/*     */               }
/* 152 */               int line = ((Integer)getLineNumberMethod.invoke(elements[caller], noArgs)).intValue();
/* 153 */               if (line < 0)
/* 154 */                 this.lineNumber = "?";
/*     */               else {
/* 156 */                 this.lineNumber = String.valueOf(line);
/*     */               }
/* 158 */               StringBuffer buf = new StringBuffer();
/* 159 */               buf.append(this.className);
/* 160 */               buf.append(".");
/* 161 */               buf.append(this.methodName);
/* 162 */               buf.append("(");
/* 163 */               buf.append(this.fileName);
/* 164 */               buf.append(":");
/* 165 */               buf.append(this.lineNumber);
/* 166 */               buf.append(")");
/* 167 */               this.fullInfo = buf.toString();
/*     */             }
/* 169 */             return;
/*     */           }
/* 171 */           prevClass = thisClass;
/*     */         }
/* 173 */         return;
/*     */       } catch (IllegalAccessException ex) {
/* 175 */         LogLog.debug("LocationInfo failed using JDK 1.4 methods", ex);
/*     */       } catch (InvocationTargetException ex) {
/* 177 */         if (((ex.getTargetException() instanceof InterruptedException)) || ((ex.getTargetException() instanceof InterruptedIOException)))
/*     */         {
/* 179 */           Thread.currentThread().interrupt();
/*     */         }
/* 181 */         LogLog.debug("LocationInfo failed using JDK 1.4 methods", ex);
/*     */       } catch (RuntimeException ex) {
/* 183 */         LogLog.debug("LocationInfo failed using JDK 1.4 methods", ex);
/*     */       }
/*     */     String s;
/* 189 */     synchronized (sw) {
/* 190 */       t.printStackTrace(pw);
/* 191 */       s = sw.toString();
/* 192 */       sw.getBuffer().setLength(0);
/*     */     }
/*     */ 
/* 204 */     int ibegin = s.lastIndexOf(fqnOfCallingClass);
/* 205 */     if (ibegin == -1) {
/* 206 */       return;
/*     */     }
/*     */ 
/* 215 */     if ((ibegin + fqnOfCallingClass.length() < s.length()) && (s.charAt(ibegin + fqnOfCallingClass.length()) != '.'))
/*     */     {
/* 217 */       int i = s.lastIndexOf(fqnOfCallingClass + ".");
/* 218 */       if (i != -1) {
/* 219 */         ibegin = i;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 224 */     ibegin = s.indexOf(Layout.LINE_SEP, ibegin);
/* 225 */     if (ibegin == -1)
/* 226 */       return;
/* 227 */     ibegin += Layout.LINE_SEP_LEN;
/*     */ 
/* 230 */     int iend = s.indexOf(Layout.LINE_SEP, ibegin);
/* 231 */     if (iend == -1) {
/* 232 */       return;
/*     */     }
/*     */ 
/* 236 */     if (!inVisualAge)
/*     */     {
/* 238 */       ibegin = s.lastIndexOf("at ", iend);
/* 239 */       if (ibegin == -1) {
/* 240 */         return;
/*     */       }
/* 242 */       ibegin += 3;
/*     */     }
/*     */ 
/* 245 */     this.fullInfo = s.substring(ibegin, iend);
/*     */   }
/*     */ 
/*     */   private static final void appendFragment(StringBuffer buf, String fragment)
/*     */   {
/* 258 */     if (fragment == null)
/* 259 */       buf.append("?");
/*     */     else
/* 261 */       buf.append(fragment);
/*     */   }
/*     */ 
/*     */   public LocationInfo(String file, String classname, String method, String line)
/*     */   {
/* 279 */     this.fileName = file;
/* 280 */     this.className = classname;
/* 281 */     this.methodName = method;
/* 282 */     this.lineNumber = line;
/* 283 */     StringBuffer buf = new StringBuffer();
/* 284 */     appendFragment(buf, classname);
/* 285 */     buf.append(".");
/* 286 */     appendFragment(buf, method);
/* 287 */     buf.append("(");
/* 288 */     appendFragment(buf, file);
/* 289 */     buf.append(":");
/* 290 */     appendFragment(buf, line);
/* 291 */     buf.append(")");
/* 292 */     this.fullInfo = buf.toString();
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/* 301 */     if (this.fullInfo == null) return "?";
/* 302 */     if (this.className == null)
/*     */     {
/* 305 */       int iend = this.fullInfo.lastIndexOf('(');
/* 306 */       if (iend == -1) {
/* 307 */         this.className = "?";
/*     */       } else {
/* 309 */         iend = this.fullInfo.lastIndexOf('.', iend);
/*     */ 
/* 320 */         int ibegin = 0;
/* 321 */         if (inVisualAge) {
/* 322 */           ibegin = this.fullInfo.lastIndexOf(' ', iend) + 1;
/*     */         }
/*     */ 
/* 325 */         if (iend == -1)
/* 326 */           this.className = "?";
/*     */         else
/* 328 */           this.className = this.fullInfo.substring(ibegin, iend);
/*     */       }
/*     */     }
/* 331 */     return this.className;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 341 */     if (this.fullInfo == null) return "?";
/*     */ 
/* 343 */     if (this.fileName == null) {
/* 344 */       int iend = this.fullInfo.lastIndexOf(':');
/* 345 */       if (iend == -1) {
/* 346 */         this.fileName = "?";
/*     */       } else {
/* 348 */         int ibegin = this.fullInfo.lastIndexOf('(', iend - 1);
/* 349 */         this.fileName = this.fullInfo.substring(ibegin + 1, iend);
/*     */       }
/*     */     }
/* 352 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public String getLineNumber()
/*     */   {
/* 362 */     if (this.fullInfo == null) return "?";
/*     */ 
/* 364 */     if (this.lineNumber == null) {
/* 365 */       int iend = this.fullInfo.lastIndexOf(')');
/* 366 */       int ibegin = this.fullInfo.lastIndexOf(':', iend - 1);
/* 367 */       if (ibegin == -1)
/* 368 */         this.lineNumber = "?";
/*     */       else
/* 370 */         this.lineNumber = this.fullInfo.substring(ibegin + 1, iend);
/*     */     }
/* 372 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 380 */     if (this.fullInfo == null) return "?";
/* 381 */     if (this.methodName == null) {
/* 382 */       int iend = this.fullInfo.lastIndexOf('(');
/* 383 */       int ibegin = this.fullInfo.lastIndexOf('.', iend);
/* 384 */       if (ibegin == -1)
/* 385 */         this.methodName = "?";
/*     */       else
/* 387 */         this.methodName = this.fullInfo.substring(ibegin + 1, iend);
/*     */     }
/* 389 */     return this.methodName;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  91 */       inVisualAge = Class.forName("com.ibm.uvm.tools.DebugSupport") != null;
/*  92 */       LogLog.debug("Detected IBM VisualAge environment.");
/*     */     }
/*     */     catch (Throwable e) {
/*     */     }
/*     */     try {
/*  97 */       Class[] noArgs = null;
/*  98 */       getStackTraceMethod = Throwable.class.getMethod("getStackTrace", noArgs);
/*  99 */       Class stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
/* 100 */       getClassNameMethod = stackTraceElementClass.getMethod("getClassName", noArgs);
/* 101 */       getMethodNameMethod = stackTraceElementClass.getMethod("getMethodName", noArgs);
/* 102 */       getFileNameMethod = stackTraceElementClass.getMethod("getFileName", noArgs);
/* 103 */       getLineNumberMethod = stackTraceElementClass.getMethod("getLineNumber", noArgs);
/*     */     } catch (ClassNotFoundException ex) {
/* 105 */       LogLog.debug("LocationInfo will use pre-JDK 1.4 methods to determine location.");
/*     */     } catch (NoSuchMethodException ex) {
/* 107 */       LogLog.debug("LocationInfo will use pre-JDK 1.4 methods to determine location.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.LocationInfo
 * JD-Core Version:    0.6.2
 */