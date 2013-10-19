/*     */ package com.amazonaws.auth.policy;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Statement
/*     */ {
/*     */   private String id;
/*     */   private Effect effect;
/*  73 */   private List<Principal> principals = new ArrayList();
/*  74 */   private List<Action> actions = new ArrayList();
/*     */   private List<Resource> resources;
/*  76 */   private List<Condition> conditions = new ArrayList();
/*     */ 
/*     */   public Statement(Effect effect)
/*     */   {
/*  92 */     this.effect = effect;
/*  93 */     this.id = null;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 114 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 136 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public Statement withId(String id)
/*     */   {
/* 157 */     setId(id);
/* 158 */     return this;
/*     */   }
/*     */ 
/*     */   public Effect getEffect()
/*     */   {
/* 168 */     return this.effect;
/*     */   }
/*     */ 
/*     */   public void setEffect(Effect effect)
/*     */   {
/* 179 */     this.effect = effect;
/*     */   }
/*     */ 
/*     */   public List<Action> getActions()
/*     */   {
/* 193 */     return this.actions;
/*     */   }
/*     */ 
/*     */   public void setActions(Collection<Action> actions)
/*     */   {
/* 208 */     this.actions = new ArrayList(actions);
/*     */   }
/*     */ 
/*     */   public Statement withActions(Action[] actions)
/*     */   {
/* 229 */     setActions(Arrays.asList(actions));
/* 230 */     return this;
/*     */   }
/*     */ 
/*     */   public List<Resource> getResources()
/*     */   {
/* 244 */     return this.resources;
/*     */   }
/*     */ 
/*     */   public void setResources(Collection<Resource> resources)
/*     */   {
/* 259 */     this.resources = new ArrayList(resources);
/*     */   }
/*     */ 
/*     */   public Statement withResources(Resource[] resources)
/*     */   {
/* 280 */     setResources(Arrays.asList(resources));
/* 281 */     return this;
/*     */   }
/*     */ 
/*     */   public List<Condition> getConditions()
/*     */   {
/* 301 */     return this.conditions;
/*     */   }
/*     */ 
/*     */   public void setConditions(List<Condition> conditions)
/*     */   {
/* 322 */     this.conditions = conditions;
/*     */   }
/*     */ 
/*     */   public Statement withConditions(Condition[] conditions)
/*     */   {
/* 349 */     setConditions(Arrays.asList(conditions));
/* 350 */     return this;
/*     */   }
/*     */ 
/*     */   public List<Principal> getPrincipals()
/*     */   {
/* 360 */     return this.principals;
/*     */   }
/*     */ 
/*     */   public void setPrincipals(Collection<Principal> principals)
/*     */   {
/* 375 */     this.principals = new ArrayList(principals);
/*     */   }
/*     */ 
/*     */   public Statement withPrincipals(Principal[] principals)
/*     */   {
/* 394 */     setPrincipals(Arrays.asList(principals));
/* 395 */     return this;
/*     */   }
/*     */ 
/*     */   public static enum Effect
/*     */   {
/*  68 */     Allow, Deny;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.Statement
 * JD-Core Version:    0.6.2
 */