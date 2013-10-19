/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Stack;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class NDC
/*     */ {
/* 114 */   static Hashtable ht = new Hashtable();
/*     */ 
/* 116 */   static int pushCounter = 0;
/*     */   static final int REAP_THRESHOLD = 5;
/*     */ 
/*     */   private static Stack getCurrentStack()
/*     */   {
/* 134 */     if (ht != null) {
/* 135 */       return (Stack)ht.get(Thread.currentThread());
/*     */     }
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 153 */     Stack stack = getCurrentStack();
/* 154 */     if (stack != null)
/* 155 */       stack.setSize(0);
/*     */   }
/*     */ 
/*     */   public static Stack cloneStack()
/*     */   {
/* 176 */     Stack stack = getCurrentStack();
/* 177 */     if (stack == null) {
/* 178 */       return null;
/*     */     }
/* 180 */     return (Stack)stack.clone();
/*     */   }
/*     */ 
/*     */   public static void inherit(Stack stack)
/*     */   {
/* 208 */     if (stack != null)
/* 209 */       ht.put(Thread.currentThread(), stack);
/*     */   }
/*     */ 
/*     */   public static String get()
/*     */   {
/* 220 */     Stack s = getCurrentStack();
/* 221 */     if ((s != null) && (!s.isEmpty())) {
/* 222 */       return ((DiagnosticContext)s.peek()).fullMessage;
/*     */     }
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getDepth()
/*     */   {
/* 236 */     Stack stack = getCurrentStack();
/* 237 */     if (stack == null) {
/* 238 */       return 0;
/*     */     }
/* 240 */     return stack.size();
/*     */   }
/*     */ 
/*     */   private static void lazyRemove()
/*     */   {
/* 246 */     if (ht == null)
/*     */       return;
/*     */     Vector v;
/* 253 */     synchronized (ht)
/*     */     {
/* 255 */       if (++pushCounter <= 5) {
/* 256 */         return;
/*     */       }
/* 258 */       pushCounter = 0;
/*     */ 
/* 261 */       int misses = 0;
/* 262 */       v = new Vector();
/* 263 */       Enumeration enumeration = ht.keys();
/*     */ 
/* 268 */       while ((enumeration.hasMoreElements()) && (misses <= 4)) {
/* 269 */         Thread t = (Thread)enumeration.nextElement();
/* 270 */         if (t.isAlive()) {
/* 271 */           misses++;
/*     */         } else {
/* 273 */           misses = 0;
/* 274 */           v.addElement(t);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 279 */     int size = v.size();
/* 280 */     for (int i = 0; i < size; i++) {
/* 281 */       Thread t = (Thread)v.elementAt(i);
/* 282 */       LogLog.debug("Lazy NDC removal for thread [" + t.getName() + "] (" + ht.size() + ").");
/*     */ 
/* 284 */       ht.remove(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String pop()
/*     */   {
/* 301 */     Stack stack = getCurrentStack();
/* 302 */     if ((stack != null) && (!stack.isEmpty())) {
/* 303 */       return ((DiagnosticContext)stack.pop()).message;
/*     */     }
/* 305 */     return "";
/*     */   }
/*     */ 
/*     */   public static String peek()
/*     */   {
/* 321 */     Stack stack = getCurrentStack();
/* 322 */     if ((stack != null) && (!stack.isEmpty())) {
/* 323 */       return ((DiagnosticContext)stack.peek()).message;
/*     */     }
/* 325 */     return "";
/*     */   }
/*     */ 
/*     */   public static void push(String message)
/*     */   {
/* 338 */     Stack stack = getCurrentStack();
/*     */ 
/* 340 */     if (stack == null) {
/* 341 */       DiagnosticContext dc = new DiagnosticContext(message, null);
/* 342 */       stack = new Stack();
/* 343 */       Thread key = Thread.currentThread();
/* 344 */       ht.put(key, stack);
/* 345 */       stack.push(dc);
/* 346 */     } else if (stack.isEmpty()) {
/* 347 */       DiagnosticContext dc = new DiagnosticContext(message, null);
/* 348 */       stack.push(dc);
/*     */     } else {
/* 350 */       DiagnosticContext parent = (DiagnosticContext)stack.peek();
/* 351 */       stack.push(new DiagnosticContext(message, parent));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void remove()
/*     */   {
/* 377 */     if (ht != null) {
/* 378 */       ht.remove(Thread.currentThread());
/*     */ 
/* 381 */       lazyRemove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setMaxDepth(int maxDepth)
/*     */   {
/* 415 */     Stack stack = getCurrentStack();
/* 416 */     if ((stack != null) && (maxDepth < stack.size()))
/* 417 */       stack.setSize(maxDepth);
/*     */   }
/*     */ 
/*     */   private static class DiagnosticContext
/*     */   {
/*     */     String fullMessage;
/*     */     String message;
/*     */ 
/*     */     DiagnosticContext(String message, DiagnosticContext parent) {
/* 427 */       this.message = message;
/* 428 */       if (parent != null)
/* 429 */         this.fullMessage = (parent.fullMessage + ' ' + message);
/*     */       else
/* 431 */         this.fullMessage = message;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.NDC
 * JD-Core Version:    0.6.2
 */