/*     */ package com.amazonaws.auth.policy;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class Condition
/*     */ {
/*     */   protected String type;
/*     */   protected String conditionKey;
/*     */   protected List<String> values;
/*     */ 
/*     */   public String getType()
/*     */   {
/*  74 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(String type)
/*     */   {
/*  84 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public String getConditionKey()
/*     */   {
/* 100 */     return this.conditionKey;
/*     */   }
/*     */ 
/*     */   public void setConditionKey(String conditionKey)
/*     */   {
/* 117 */     this.conditionKey = conditionKey;
/*     */   }
/*     */ 
/*     */   public List<String> getValues()
/*     */   {
/* 131 */     return this.values;
/*     */   }
/*     */ 
/*     */   public void setValues(List<String> values)
/*     */   {
/* 146 */     this.values = values;
/*     */   }
/*     */ 
/*     */   public Condition withType(String type)
/*     */   {
/* 154 */     setType(type);
/* 155 */     return this;
/*     */   }
/*     */ 
/*     */   public Condition withConditionKey(String key)
/*     */   {
/* 163 */     setConditionKey(key);
/* 164 */     return this;
/*     */   }
/*     */ 
/*     */   public Condition withValues(String[] values)
/*     */   {
/* 172 */     setValues(Arrays.asList(values));
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   public Condition withValues(List<String> values)
/*     */   {
/* 182 */     setValues(values);
/* 183 */     return this;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.Condition
 * JD-Core Version:    0.6.2
 */