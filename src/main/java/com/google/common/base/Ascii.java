/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Ascii
/*     */ {
/*     */   public static final byte NUL = 0;
/*     */   public static final byte SOH = 1;
/*     */   public static final byte STX = 2;
/*     */   public static final byte ETX = 3;
/*     */   public static final byte EOT = 4;
/*     */   public static final byte ENQ = 5;
/*     */   public static final byte ACK = 6;
/*     */   public static final byte BEL = 7;
/*     */   public static final byte BS = 8;
/*     */   public static final byte HT = 9;
/*     */   public static final byte LF = 10;
/*     */   public static final byte NL = 10;
/*     */   public static final byte VT = 11;
/*     */   public static final byte FF = 12;
/*     */   public static final byte CR = 13;
/*     */   public static final byte SO = 14;
/*     */   public static final byte SI = 15;
/*     */   public static final byte DLE = 16;
/*     */   public static final byte DC1 = 17;
/*     */   public static final byte XON = 17;
/*     */   public static final byte DC2 = 18;
/*     */   public static final byte DC3 = 19;
/*     */   public static final byte XOFF = 19;
/*     */   public static final byte DC4 = 20;
/*     */   public static final byte NAK = 21;
/*     */   public static final byte SYN = 22;
/*     */   public static final byte ETB = 23;
/*     */   public static final byte CAN = 24;
/*     */   public static final byte EM = 25;
/*     */   public static final byte SUB = 26;
/*     */   public static final byte ESC = 27;
/*     */   public static final byte FS = 28;
/*     */   public static final byte GS = 29;
/*     */   public static final byte RS = 30;
/*     */   public static final byte US = 31;
/*     */   public static final byte SP = 32;
/*     */   public static final byte SPACE = 32;
/*     */   public static final byte DEL = 127;
/*     */ 
/*     */   @Beta
/*     */   public static final char MIN = '\000';
/*     */ 
/*     */   @Beta
/*     */   public static final char MAX = '';
/*     */ 
/*     */   public static String toLowerCase(String string)
/*     */   {
/* 411 */     int length = string.length();
/* 412 */     StringBuilder builder = new StringBuilder(length);
/* 413 */     for (int i = 0; i < length; i++) {
/* 414 */       builder.append(toLowerCase(string.charAt(i)));
/*     */     }
/* 416 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static char toLowerCase(char c)
/*     */   {
/* 424 */     return isUpperCase(c) ? (char)(c ^ 0x20) : c;
/*     */   }
/*     */ 
/*     */   public static String toUpperCase(String string)
/*     */   {
/* 433 */     int length = string.length();
/* 434 */     StringBuilder builder = new StringBuilder(length);
/* 435 */     for (int i = 0; i < length; i++) {
/* 436 */       builder.append(toUpperCase(string.charAt(i)));
/*     */     }
/* 438 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static char toUpperCase(char c)
/*     */   {
/* 446 */     return isLowerCase(c) ? (char)(c & 0x5F) : c;
/*     */   }
/*     */ 
/*     */   public static boolean isLowerCase(char c)
/*     */   {
/* 455 */     return (c >= 'a') && (c <= 'z');
/*     */   }
/*     */ 
/*     */   public static boolean isUpperCase(char c)
/*     */   {
/* 464 */     return (c >= 'A') && (c <= 'Z');
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Ascii
 * JD-Core Version:    0.6.2
 */