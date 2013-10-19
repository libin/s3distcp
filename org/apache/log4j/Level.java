/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Level extends Priority
/*     */   implements Serializable
/*     */ {
/*     */   public static final int TRACE_INT = 5000;
/*  51 */   public static final Level OFF = new Level(2147483647, "OFF", 0);
/*     */ 
/*  57 */   public static final Level FATAL = new Level(50000, "FATAL", 0);
/*     */ 
/*  62 */   public static final Level ERROR = new Level(40000, "ERROR", 3);
/*     */ 
/*  67 */   public static final Level WARN = new Level(30000, "WARN", 4);
/*     */ 
/*  73 */   public static final Level INFO = new Level(20000, "INFO", 6);
/*     */ 
/*  79 */   public static final Level DEBUG = new Level(10000, "DEBUG", 7);
/*     */ 
/*  86 */   public static final Level TRACE = new Level(5000, "TRACE", 7);
/*     */ 
/*  92 */   public static final Level ALL = new Level(-2147483648, "ALL", 7);
/*     */   static final long serialVersionUID = 3491141966387921974L;
/*     */ 
/*     */   protected Level(int level, String levelStr, int syslogEquivalent)
/*     */   {
/* 104 */     super(level, levelStr, syslogEquivalent);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String sArg)
/*     */   {
/* 115 */     return toLevel(sArg, DEBUG);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(int val)
/*     */   {
/* 126 */     return toLevel(val, DEBUG);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(int val, Level defaultLevel)
/*     */   {
/* 136 */     switch (val) { case -2147483648:
/* 137 */       return ALL;
/*     */     case 10000:
/* 138 */       return DEBUG;
/*     */     case 20000:
/* 139 */       return INFO;
/*     */     case 30000:
/* 140 */       return WARN;
/*     */     case 40000:
/* 141 */       return ERROR;
/*     */     case 50000:
/* 142 */       return FATAL;
/*     */     case 2147483647:
/* 143 */       return OFF;
/*     */     case 5000:
/* 144 */       return TRACE; }
/* 145 */     return defaultLevel;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String sArg, Level defaultLevel)
/*     */   {
/* 157 */     if (sArg == null) {
/* 158 */       return defaultLevel;
/*     */     }
/* 160 */     String s = sArg.toUpperCase();
/*     */ 
/* 162 */     if (s.equals("ALL")) return ALL;
/* 163 */     if (s.equals("DEBUG")) return DEBUG;
/* 164 */     if (s.equals("INFO")) return INFO;
/* 165 */     if (s.equals("WARN")) return WARN;
/* 166 */     if (s.equals("ERROR")) return ERROR;
/* 167 */     if (s.equals("FATAL")) return FATAL;
/* 168 */     if (s.equals("OFF")) return OFF;
/* 169 */     if (s.equals("TRACE")) return TRACE;
/*     */ 
/* 173 */     if (s.equals("İNFO")) return INFO;
/* 174 */     return defaultLevel;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 184 */     s.defaultReadObject();
/* 185 */     this.level = s.readInt();
/* 186 */     this.syslogEquivalent = s.readInt();
/* 187 */     this.levelStr = s.readUTF();
/* 188 */     if (this.levelStr == null)
/* 189 */       this.levelStr = "";
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream s)
/*     */     throws IOException
/*     */   {
/* 199 */     s.defaultWriteObject();
/* 200 */     s.writeInt(this.level);
/* 201 */     s.writeInt(this.syslogEquivalent);
/* 202 */     s.writeUTF(this.levelStr);
/*     */   }
/*     */ 
/*     */   private Object readResolve()
/*     */     throws ObjectStreamException
/*     */   {
/* 215 */     if (getClass() == Level.class) {
/* 216 */       return toLevel(this.level);
/*     */     }
/*     */ 
/* 221 */     return this;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Level
 * JD-Core Version:    0.6.2
 */