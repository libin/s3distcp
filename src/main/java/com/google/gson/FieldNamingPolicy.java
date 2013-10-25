/*     */ package com.google.gson;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public enum FieldNamingPolicy
/*     */   implements FieldNamingStrategy
/*     */ {
/*  36 */   IDENTITY, 
/*     */ 
/*  52 */   UPPER_CAMEL_CASE, 
/*     */ 
/*  71 */   UPPER_CAMEL_CASE_WITH_SPACES, 
/*     */ 
/*  89 */   LOWER_CASE_WITH_UNDERSCORES, 
/*     */ 
/* 112 */   LOWER_CASE_WITH_DASHES;
/*     */ 
/*     */   private static String separateCamelCase(String name, String separator)
/*     */   {
/* 123 */     StringBuilder translation = new StringBuilder();
/* 124 */     for (int i = 0; i < name.length(); i++) {
/* 125 */       char character = name.charAt(i);
/* 126 */       if ((Character.isUpperCase(character)) && (translation.length() != 0)) {
/* 127 */         translation.append(separator);
/*     */       }
/* 129 */       translation.append(character);
/*     */     }
/* 131 */     return translation.toString();
/*     */   }
/*     */ 
/*     */   private static String upperCaseFirstLetter(String name)
/*     */   {
/* 138 */     StringBuilder fieldNameBuilder = new StringBuilder();
/* 139 */     int index = 0;
/* 140 */     char firstCharacter = name.charAt(index);
/*     */ 
/* 142 */     while ((index < name.length() - 1) && 
/* 143 */       (!Character.isLetter(firstCharacter)))
/*     */     {
/* 147 */       fieldNameBuilder.append(firstCharacter);
/* 148 */       firstCharacter = name.charAt(++index);
/*     */     }
/*     */ 
/* 151 */     if (index == name.length()) {
/* 152 */       return fieldNameBuilder.toString();
/*     */     }
/*     */ 
/* 155 */     if (!Character.isUpperCase(firstCharacter)) {
/* 156 */       String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), name, ++index);
/* 157 */       return modifiedTarget;
/*     */     }
/* 159 */     return name;
/*     */   }
/*     */ 
/*     */   private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring)
/*     */   {
/* 164 */     return indexOfSubstring < srcString.length() ? firstCharacter + srcString.substring(indexOfSubstring) : String.valueOf(firstCharacter);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.FieldNamingPolicy
 * JD-Core Version:    0.6.2
 */