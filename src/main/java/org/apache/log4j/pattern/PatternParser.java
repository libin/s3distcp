/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public final class PatternParser
/*     */ {
/*     */   private static final char ESCAPE_CHAR = '%';
/*     */   private static final int LITERAL_STATE = 0;
/*     */   private static final int CONVERTER_STATE = 1;
/*     */   private static final int DOT_STATE = 3;
/*     */   private static final int MIN_STATE = 4;
/*     */   private static final int MAX_STATE = 5;
/*     */   private static final Map PATTERN_LAYOUT_RULES;
/* 144 */   private static final Map FILENAME_PATTERN_RULES = new ReadOnlyMap(fnameRules);
/*     */ 
/*     */   public static Map getPatternLayoutRules()
/*     */   {
/* 158 */     return PATTERN_LAYOUT_RULES;
/*     */   }
/*     */ 
/*     */   public static Map getFileNamePatternRules()
/*     */   {
/* 166 */     return FILENAME_PATTERN_RULES;
/*     */   }
/*     */ 
/*     */   private static int extractConverter(char lastChar, String pattern, int i, StringBuffer convBuf, StringBuffer currentLiteral)
/*     */   {
/* 187 */     convBuf.setLength(0);
/*     */ 
/* 194 */     if (!Character.isUnicodeIdentifierStart(lastChar)) {
/* 195 */       return i;
/*     */     }
/*     */ 
/* 198 */     convBuf.append(lastChar);
/*     */ 
/* 202 */     while ((i < pattern.length()) && (Character.isUnicodeIdentifierPart(pattern.charAt(i)))) {
/* 203 */       convBuf.append(pattern.charAt(i));
/* 204 */       currentLiteral.append(pattern.charAt(i));
/*     */ 
/* 207 */       i++;
/*     */     }
/*     */ 
/* 210 */     return i;
/*     */   }
/*     */ 
/*     */   private static int extractOptions(String pattern, int i, List options)
/*     */   {
/* 221 */     while ((i < pattern.length()) && (pattern.charAt(i) == '{')) {
/* 222 */       int end = pattern.indexOf('}', i);
/*     */ 
/* 224 */       if (end == -1)
/*     */       {
/*     */         break;
/*     */       }
/* 228 */       String r = pattern.substring(i + 1, end);
/* 229 */       options.add(r);
/* 230 */       i = end + 1;
/*     */     }
/*     */ 
/* 233 */     return i;
/*     */   }
/*     */ 
/*     */   public static void parse(String pattern, List patternConverters, List formattingInfos, Map converterRegistry, Map rules)
/*     */   {
/* 247 */     if (pattern == null) {
/* 248 */       throw new NullPointerException("pattern");
/*     */     }
/*     */ 
/* 251 */     StringBuffer currentLiteral = new StringBuffer(32);
/*     */ 
/* 253 */     int patternLength = pattern.length();
/* 254 */     int state = 0;
/*     */ 
/* 256 */     int i = 0;
/* 257 */     FormattingInfo formattingInfo = FormattingInfo.getDefault();
/*     */ 
/* 259 */     while (i < patternLength) {
/* 260 */       char c = pattern.charAt(i++);
/*     */ 
/* 262 */       switch (state)
/*     */       {
/*     */       case 0:
/* 266 */         if (i == patternLength) {
/* 267 */           currentLiteral.append(c);
/*     */         }
/* 272 */         else if (c == '%')
/*     */         {
/* 274 */           switch (pattern.charAt(i)) {
/*     */           case '%':
/* 276 */             currentLiteral.append(c);
/* 277 */             i++;
/*     */ 
/* 279 */             break;
/*     */           default:
/* 283 */             if (currentLiteral.length() != 0) {
/* 284 */               patternConverters.add(new LiteralPatternConverter(currentLiteral.toString()));
/*     */ 
/* 286 */               formattingInfos.add(FormattingInfo.getDefault());
/*     */             }
/*     */ 
/* 289 */             currentLiteral.setLength(0);
/* 290 */             currentLiteral.append(c);
/* 291 */             state = 1;
/* 292 */             formattingInfo = FormattingInfo.getDefault(); break;
/*     */           }
/*     */         }
/* 295 */         else currentLiteral.append(c);
/*     */ 
/* 298 */         break;
/*     */       case 1:
/* 301 */         currentLiteral.append(c);
/*     */ 
/* 303 */         switch (c) {
/*     */         case '-':
/* 305 */           formattingInfo = new FormattingInfo(true, formattingInfo.getMinLength(), formattingInfo.getMaxLength());
/*     */ 
/* 310 */           break;
/*     */         case '.':
/* 313 */           state = 3;
/*     */ 
/* 315 */           break;
/*     */         default:
/* 319 */           if ((c >= '0') && (c <= '9')) {
/* 320 */             formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), c - '0', formattingInfo.getMaxLength());
/*     */ 
/* 324 */             state = 4;
/*     */           } else {
/* 326 */             i = finalizeConverter(c, pattern, i, currentLiteral, formattingInfo, converterRegistry, rules, patternConverters, formattingInfos);
/*     */ 
/* 331 */             state = 0;
/* 332 */             formattingInfo = FormattingInfo.getDefault();
/* 333 */             currentLiteral.setLength(0);
/*     */           }
/*     */           break;
/*     */         }
/* 337 */         break;
/*     */       case 4:
/* 340 */         currentLiteral.append(c);
/*     */ 
/* 342 */         if ((c >= '0') && (c <= '9')) {
/* 343 */           formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength() * 10 + (c - '0'), formattingInfo.getMaxLength());
/*     */         }
/* 348 */         else if (c == '.') {
/* 349 */           state = 3;
/*     */         } else {
/* 351 */           i = finalizeConverter(c, pattern, i, currentLiteral, formattingInfo, converterRegistry, rules, patternConverters, formattingInfos);
/*     */ 
/* 354 */           state = 0;
/* 355 */           formattingInfo = FormattingInfo.getDefault();
/* 356 */           currentLiteral.setLength(0);
/*     */         }
/*     */ 
/* 359 */         break;
/*     */       case 3:
/* 362 */         currentLiteral.append(c);
/*     */ 
/* 364 */         if ((c >= '0') && (c <= '9')) {
/* 365 */           formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength(), c - '0');
/*     */ 
/* 369 */           state = 5;
/*     */         } else {
/* 371 */           LogLog.error("Error occured in position " + i + ".\n Was expecting digit, instead got char \"" + c + "\".");
/*     */ 
/* 375 */           state = 0;
/*     */         }
/*     */ 
/* 378 */         break;
/*     */       case 5:
/* 381 */         currentLiteral.append(c);
/*     */ 
/* 383 */         if ((c >= '0') && (c <= '9')) {
/* 384 */           formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength(), formattingInfo.getMaxLength() * 10 + (c - '0'));
/*     */         }
/*     */         else
/*     */         {
/* 389 */           i = finalizeConverter(c, pattern, i, currentLiteral, formattingInfo, converterRegistry, rules, patternConverters, formattingInfos);
/*     */ 
/* 392 */           state = 0;
/* 393 */           formattingInfo = FormattingInfo.getDefault();
/* 394 */           currentLiteral.setLength(0);
/*     */         }
/*     */         break;
/*     */       case 2:
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 402 */     if (currentLiteral.length() != 0) {
/* 403 */       patternConverters.add(new LiteralPatternConverter(currentLiteral.toString()));
/*     */ 
/* 405 */       formattingInfos.add(FormattingInfo.getDefault());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static PatternConverter createConverter(String converterId, StringBuffer currentLiteral, Map converterRegistry, Map rules, List options)
/*     */   {
/* 424 */     String converterName = converterId;
/* 425 */     Object converterObj = null;
/*     */ 
/* 427 */     for (int i = converterId.length(); (i > 0) && (converterObj == null); 
/* 428 */       i--) {
/* 429 */       converterName = converterName.substring(0, i);
/*     */ 
/* 431 */       if (converterRegistry != null) {
/* 432 */         converterObj = converterRegistry.get(converterName);
/*     */       }
/*     */ 
/* 435 */       if ((converterObj == null) && (rules != null)) {
/* 436 */         converterObj = rules.get(converterName);
/*     */       }
/*     */     }
/*     */ 
/* 440 */     if (converterObj == null) {
/* 441 */       LogLog.error("Unrecognized format specifier [" + converterId + "]");
/*     */ 
/* 443 */       return null;
/*     */     }
/*     */ 
/* 446 */     Class converterClass = null;
/*     */ 
/* 448 */     if ((converterObj instanceof Class)) {
/* 449 */       converterClass = (Class)converterObj;
/*     */     }
/* 451 */     else if ((converterObj instanceof String)) {
/*     */       try {
/* 453 */         converterClass = Loader.loadClass((String)converterObj);
/*     */       } catch (ClassNotFoundException ex) {
/* 455 */         LogLog.warn("Class for conversion pattern %" + converterName + " not found", ex);
/*     */ 
/* 459 */         return null;
/*     */       }
/*     */     } else {
/* 462 */       LogLog.warn("Bad map entry for conversion pattern %" + converterName + ".");
/*     */ 
/* 465 */       return null;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 470 */       Method factory = converterClass.getMethod("newInstance", new Class[] { Class.forName("[Ljava.lang.String;") });
/*     */ 
/* 476 */       String[] optionsArray = new String[options.size()];
/* 477 */       optionsArray = (String[])options.toArray(optionsArray);
/*     */ 
/* 479 */       Object newObj = factory.invoke(null, new Object[] { optionsArray });
/*     */ 
/* 482 */       if ((newObj instanceof PatternConverter)) {
/* 483 */         currentLiteral.delete(0, currentLiteral.length() - (converterId.length() - converterName.length()));
/*     */ 
/* 488 */         return (PatternConverter)newObj;
/*     */       }
/* 490 */       LogLog.warn("Class " + converterClass.getName() + " does not extend PatternConverter.");
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 495 */       LogLog.error("Error creating converter for " + converterId, ex);
/*     */       try
/*     */       {
/* 500 */         PatternConverter pc = (PatternConverter)converterClass.newInstance();
/* 501 */         currentLiteral.delete(0, currentLiteral.length() - (converterId.length() - converterName.length()));
/*     */ 
/* 506 */         return pc;
/*     */       } catch (Exception ex2) {
/* 508 */         LogLog.error("Error creating converter for " + converterId, ex2);
/*     */       }
/*     */     }
/*     */ 
/* 512 */     return null;
/*     */   }
/*     */ 
/*     */   private static int finalizeConverter(char c, String pattern, int i, StringBuffer currentLiteral, FormattingInfo formattingInfo, Map converterRegistry, Map rules, List patternConverters, List formattingInfos)
/*     */   {
/* 534 */     StringBuffer convBuf = new StringBuffer();
/* 535 */     i = extractConverter(c, pattern, i, convBuf, currentLiteral);
/*     */ 
/* 537 */     String converterId = convBuf.toString();
/*     */ 
/* 539 */     List options = new ArrayList();
/* 540 */     i = extractOptions(pattern, i, options);
/*     */ 
/* 542 */     PatternConverter pc = createConverter(converterId, currentLiteral, converterRegistry, rules, options);
/*     */ 
/* 546 */     if (pc == null)
/*     */     {
/*     */       StringBuffer msg;
/*     */       StringBuffer msg;
/* 549 */       if ((converterId == null) || (converterId.length() == 0)) {
/* 550 */         msg = new StringBuffer("Empty conversion specifier starting at position ");
/*     */       }
/*     */       else {
/* 553 */         msg = new StringBuffer("Unrecognized conversion specifier [");
/* 554 */         msg.append(converterId);
/* 555 */         msg.append("] starting at position ");
/*     */       }
/*     */ 
/* 558 */       msg.append(Integer.toString(i));
/* 559 */       msg.append(" in conversion pattern.");
/*     */ 
/* 561 */       LogLog.error(msg.toString());
/*     */ 
/* 563 */       patternConverters.add(new LiteralPatternConverter(currentLiteral.toString()));
/*     */ 
/* 565 */       formattingInfos.add(FormattingInfo.getDefault());
/*     */     } else {
/* 567 */       patternConverters.add(pc);
/* 568 */       formattingInfos.add(formattingInfo);
/*     */ 
/* 570 */       if (currentLiteral.length() > 0) {
/* 571 */         patternConverters.add(new LiteralPatternConverter(currentLiteral.toString()));
/*     */ 
/* 573 */         formattingInfos.add(FormattingInfo.getDefault());
/*     */       }
/*     */     }
/*     */ 
/* 577 */     currentLiteral.setLength(0);
/*     */ 
/* 579 */     return i;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  91 */     Map rules = new HashMap(17);
/*  92 */     rules.put("c", LoggerPatternConverter.class);
/*  93 */     rules.put("logger", LoggerPatternConverter.class);
/*     */ 
/*  95 */     rules.put("C", ClassNamePatternConverter.class);
/*  96 */     rules.put("class", ClassNamePatternConverter.class);
/*     */ 
/*  98 */     rules.put("d", DatePatternConverter.class);
/*  99 */     rules.put("date", DatePatternConverter.class);
/*     */ 
/* 101 */     rules.put("F", FileLocationPatternConverter.class);
/* 102 */     rules.put("file", FileLocationPatternConverter.class);
/*     */ 
/* 104 */     rules.put("l", FullLocationPatternConverter.class);
/*     */ 
/* 106 */     rules.put("L", LineLocationPatternConverter.class);
/* 107 */     rules.put("line", LineLocationPatternConverter.class);
/*     */ 
/* 109 */     rules.put("m", MessagePatternConverter.class);
/* 110 */     rules.put("message", MessagePatternConverter.class);
/*     */ 
/* 112 */     rules.put("n", LineSeparatorPatternConverter.class);
/*     */ 
/* 114 */     rules.put("M", MethodLocationPatternConverter.class);
/* 115 */     rules.put("method", MethodLocationPatternConverter.class);
/*     */ 
/* 117 */     rules.put("p", LevelPatternConverter.class);
/* 118 */     rules.put("level", LevelPatternConverter.class);
/*     */ 
/* 120 */     rules.put("r", RelativeTimePatternConverter.class);
/* 121 */     rules.put("relative", RelativeTimePatternConverter.class);
/*     */ 
/* 123 */     rules.put("t", ThreadPatternConverter.class);
/* 124 */     rules.put("thread", ThreadPatternConverter.class);
/*     */ 
/* 126 */     rules.put("x", NDCPatternConverter.class);
/* 127 */     rules.put("ndc", NDCPatternConverter.class);
/*     */ 
/* 129 */     rules.put("X", PropertiesPatternConverter.class);
/* 130 */     rules.put("properties", PropertiesPatternConverter.class);
/*     */ 
/* 132 */     rules.put("sn", SequenceNumberPatternConverter.class);
/* 133 */     rules.put("sequenceNumber", SequenceNumberPatternConverter.class);
/*     */ 
/* 135 */     rules.put("throwable", ThrowableInformationPatternConverter.class);
/* 136 */     PATTERN_LAYOUT_RULES = new ReadOnlyMap(rules);
/*     */ 
/* 138 */     Map fnameRules = new HashMap(4);
/* 139 */     fnameRules.put("d", FileDatePatternConverter.class);
/* 140 */     fnameRules.put("date", FileDatePatternConverter.class);
/* 141 */     fnameRules.put("i", IntegerPatternConverter.class);
/* 142 */     fnameRules.put("index", IntegerPatternConverter.class);
/*     */   }
/*     */ 
/*     */   private static class ReadOnlyMap
/*     */     implements Map
/*     */   {
/*     */     private final Map map;
/*     */ 
/*     */     public ReadOnlyMap(Map src)
/*     */     {
/* 596 */       this.map = src;
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 603 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean containsKey(Object key)
/*     */     {
/* 610 */       return this.map.containsKey(key);
/*     */     }
/*     */ 
/*     */     public boolean containsValue(Object value)
/*     */     {
/* 617 */       return this.map.containsValue(value);
/*     */     }
/*     */ 
/*     */     public Set entrySet()
/*     */     {
/* 624 */       return this.map.entrySet();
/*     */     }
/*     */ 
/*     */     public Object get(Object key)
/*     */     {
/* 631 */       return this.map.get(key);
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 638 */       return this.map.isEmpty();
/*     */     }
/*     */ 
/*     */     public Set keySet()
/*     */     {
/* 645 */       return this.map.keySet();
/*     */     }
/*     */ 
/*     */     public Object put(Object key, Object value)
/*     */     {
/* 652 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void putAll(Map t)
/*     */     {
/* 659 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Object remove(Object key)
/*     */     {
/* 666 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 673 */       return this.map.size();
/*     */     }
/*     */ 
/*     */     public Collection values()
/*     */     {
/* 680 */       return this.map.values();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.PatternParser
 * JD-Core Version:    0.6.2
 */