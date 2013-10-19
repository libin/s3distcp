/*     */ package com.amazonaws.auth.policy;
/*     */ 
/*     */ import com.amazonaws.auth.policy.internal.JsonPolicyWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Policy
/*     */ {
/*     */   private static final String DEFAULT_POLICY_VERSION = "2008-10-17";
/*     */   private String id;
/*  60 */   private String version = "2008-10-17";
/*  61 */   private List<Statement> statements = new ArrayList();
/*     */ 
/*     */   public Policy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Policy(String id)
/*     */   {
/*  81 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public Policy(String id, Collection<Statement> statements)
/*     */   {
/* 101 */     this(id);
/* 102 */     setStatements(statements);
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 113 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 125 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public Policy withId(String id)
/*     */   {
/* 142 */     setId(id);
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 152 */     return this.version;
/*     */   }
/*     */ 
/*     */   public Collection<Statement> getStatements()
/*     */   {
/* 163 */     return this.statements;
/*     */   }
/*     */ 
/*     */   public void setStatements(Collection<Statement> statements)
/*     */   {
/* 178 */     this.statements = new ArrayList(statements);
/* 179 */     assignUniqueStatementIds();
/*     */   }
/*     */ 
/*     */   public Policy withStatements(Statement[] statements)
/*     */   {
/* 200 */     setStatements(Arrays.asList(statements));
/* 201 */     return this;
/*     */   }
/*     */ 
/*     */   public String toJson()
/*     */   {
/* 212 */     return new JsonPolicyWriter().writePolicyToString(this);
/*     */   }
/*     */ 
/*     */   private void assignUniqueStatementIds() {
/* 216 */     Set usedStatementIds = new HashSet();
/* 217 */     for (Statement statement : this.statements) {
/* 218 */       if (statement.getId() != null) usedStatementIds.add(statement.getId());
/*     */     }
/*     */ 
/* 221 */     int counter = 0;
/* 222 */     for (Statement statement : this.statements)
/* 223 */       if (statement.getId() == null)
/*     */       {
/* 225 */         while (usedStatementIds.contains(Integer.toString(++counter)));
/* 226 */         statement.setId(Integer.toString(counter));
/*     */       }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.Policy
 * JD-Core Version:    0.6.2
 */