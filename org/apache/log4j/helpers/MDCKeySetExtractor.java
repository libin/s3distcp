/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Set;
/*    */ import org.apache.log4j.pattern.LogEvent;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class MDCKeySetExtractor
/*    */ {
/*    */   private final Method getKeySetMethod;
/* 32 */   public static final MDCKeySetExtractor INSTANCE = new MDCKeySetExtractor();
/*    */ 
/*    */   private MDCKeySetExtractor()
/*    */   {
/* 41 */     Method getMethod = null;
/*    */     try
/*    */     {
/* 44 */       getMethod = LoggingEvent.class.getMethod("getPropertyKeySet", null);
/*    */     }
/*    */     catch (Exception ex) {
/* 47 */       getMethod = null;
/*    */     }
/* 49 */     this.getKeySetMethod = getMethod;
/*    */   }
/*    */ 
/*    */   public Set getPropertyKeySet(LoggingEvent event)
/*    */     throws Exception
/*    */   {
/* 57 */     Set keySet = null;
/* 58 */     if (this.getKeySetMethod != null) {
/* 59 */       keySet = (Set)this.getKeySetMethod.invoke(event, null);
/*    */     }
/*    */     else
/*    */     {
/* 64 */       ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
/* 65 */       ObjectOutputStream os = new ObjectOutputStream(outBytes);
/* 66 */       os.writeObject(event);
/* 67 */       os.close();
/*    */ 
/* 69 */       byte[] raw = outBytes.toByteArray();
/*    */ 
/* 73 */       String subClassName = LogEvent.class.getName();
/* 74 */       if ((raw[6] == 0) || (raw[7] == subClassName.length()))
/*    */       {
/* 78 */         for (int i = 0; i < subClassName.length(); i++) {
/* 79 */           raw[(8 + i)] = ((byte)subClassName.charAt(i));
/*    */         }
/* 81 */         ByteArrayInputStream inBytes = new ByteArrayInputStream(raw);
/* 82 */         ObjectInputStream is = new ObjectInputStream(inBytes);
/* 83 */         Object cracked = is.readObject();
/* 84 */         if ((cracked instanceof LogEvent)) {
/* 85 */           keySet = ((LogEvent)cracked).getPropertyKeySet();
/*    */         }
/* 87 */         is.close();
/*    */       }
/*    */     }
/* 90 */     return keySet;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.MDCKeySetExtractor
 * JD-Core Version:    0.6.2
 */