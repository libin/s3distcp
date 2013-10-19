/*     */ package com.amazonaws.auth.policy.internal;
/*     */ 
/*     */ import com.amazonaws.auth.policy.Action;
/*     */ import com.amazonaws.auth.policy.Condition;
/*     */ import com.amazonaws.auth.policy.Policy;
/*     */ import com.amazonaws.auth.policy.Principal;
/*     */ import com.amazonaws.auth.policy.Resource;
/*     */ import com.amazonaws.auth.policy.Statement;
/*     */ import com.amazonaws.auth.policy.Statement.Effect;
/*     */ import com.amazonaws.util.json.JSONException;
/*     */ import com.amazonaws.util.json.JSONWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class JsonPolicyWriter
/*     */ {
/*     */   public String writePolicyToString(Policy policy)
/*     */   {
/*  53 */     if (policy == null) {
/*  54 */       throw new IllegalArgumentException("Policy cannot be null");
/*     */     }
/*     */ 
/*  57 */     StringWriter writer = new StringWriter();
/*     */     try {
/*  59 */       JSONWriter generator = new JSONWriter(writer);
/*  60 */       writePolicy(policy, generator);
/*  61 */       return writer.toString();
/*     */     } catch (Exception e) {
/*  63 */       String message = "Unable to serialize policy to JSON string: " + e.getMessage();
/*  64 */       throw new IllegalArgumentException(message, e); } finally {
/*     */       try {
/*  66 */         writer.close(); } catch (Exception e) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writePolicy(Policy policy, JSONWriter generator) throws JSONException, IOException {
/*  72 */     generator.object();
/*  73 */     generator.key("Version").value(policy.getVersion());
/*     */ 
/*  75 */     if (policy.getId() != null) {
/*  76 */       generator.key("Id").value(policy.getId());
/*     */     }
/*     */ 
/*  79 */     generator.key("Statement").array();
/*  80 */     for (Statement statement : policy.getStatements()) {
/*  81 */       generator.object();
/*  82 */       if (statement.getId() != null) {
/*  83 */         generator.key("Sid").value(statement.getId());
/*     */       }
/*  85 */       generator.key("Effect").value(statement.getEffect().toString());
/*     */ 
/*  87 */       writePrincipals(statement, generator);
/*  88 */       writeActions(statement, generator);
/*  89 */       writeResources(statement, generator);
/*  90 */       writeConditions(statement, generator);
/*     */ 
/*  92 */       generator.endObject();
/*     */     }
/*  94 */     generator.endArray();
/*     */ 
/*  96 */     generator.endObject();
/*     */   }
/*     */ 
/*     */   private void writeConditions(Statement statement, JSONWriter generator) throws IOException, JSONException
/*     */   {
/* 101 */     List conditions = statement.getConditions();
/* 102 */     if ((conditions == null) || (conditions.isEmpty())) return;
/*     */ 
/* 109 */     Map conditionsByType = sortConditionsByType(conditions);
/*     */ 
/* 111 */     generator.key("Condition").object();
/* 112 */     for (String conditionType : conditionsByType.keySet()) {
/* 113 */       generator.key(conditionType).object();
/*     */ 
/* 120 */       Map conditionValuesByKey = sortConditionsByKey((List)conditionsByType.get(conditionType));
/* 121 */       for (String conditionKey : conditionValuesByKey.keySet()) {
/* 122 */         generator.key(conditionKey).array();
/* 123 */         for (String value : (List)conditionValuesByKey.get(conditionKey)) {
/* 124 */           generator.value(value);
/*     */         }
/* 126 */         generator.endArray();
/*     */       }
/* 128 */       generator.endObject();
/*     */     }
/* 130 */     generator.endObject();
/*     */   }
/*     */ 
/*     */   private Map<String, List<String>> sortConditionsByKey(List<Condition> conditions) {
/* 134 */     Map conditionValuesByConditionKey = new HashMap();
/*     */ 
/* 136 */     for (Condition condition : conditions) {
/* 137 */       String key = condition.getConditionKey();
/* 138 */       List values = condition.getValues();
/*     */ 
/* 140 */       if (!conditionValuesByConditionKey.containsKey(key)) {
/* 141 */         conditionValuesByConditionKey.put(key, new ArrayList());
/*     */       }
/* 143 */       ((List)conditionValuesByConditionKey.get(key)).addAll(values);
/*     */     }
/*     */ 
/* 146 */     return conditionValuesByConditionKey;
/*     */   }
/*     */ 
/*     */   private Map<String, List<Condition>> sortConditionsByType(List<Condition> conditions) {
/* 150 */     Map conditionsByType = new HashMap();
/* 151 */     for (Condition condition : conditions) {
/* 152 */       String conditionType = condition.getType();
/* 153 */       if (conditionsByType.get(conditionType) == null) {
/* 154 */         conditionsByType.put(conditionType, new ArrayList());
/*     */       }
/* 156 */       ((List)conditionsByType.get(conditionType)).add(condition);
/*     */     }
/*     */ 
/* 159 */     return conditionsByType;
/*     */   }
/*     */ 
/*     */   private void writeResources(Statement statement, JSONWriter generator) throws IOException, JSONException
/*     */   {
/* 164 */     List resources = statement.getResources();
/* 165 */     if ((resources == null) || (resources.isEmpty())) return;
/*     */ 
/* 167 */     generator.key("Resource").array();
/* 168 */     for (Resource resource : resources) {
/* 169 */       generator.value(resource.getId());
/*     */     }
/* 171 */     generator.endArray();
/*     */   }
/*     */ 
/*     */   private void writeActions(Statement statement, JSONWriter generator) throws IOException, JSONException
/*     */   {
/* 176 */     List actions = statement.getActions();
/* 177 */     if ((actions == null) || (actions.isEmpty())) return;
/*     */ 
/* 179 */     generator.key("Action").array();
/* 180 */     for (Action action : actions) {
/* 181 */       generator.value(action.getActionName());
/*     */     }
/* 183 */     generator.endArray();
/*     */   }
/*     */ 
/*     */   private void writePrincipals(Statement statement, JSONWriter generator)
/*     */     throws IOException, JSONException
/*     */   {
/* 192 */     List principals = statement.getPrincipals();
/* 193 */     if ((principals == null) || (principals.isEmpty())) return;
/*     */ 
/* 195 */     generator.key("Principal").object();
/* 196 */     Map principalIdsByScheme = new HashMap();
/* 197 */     for (Principal p : principals) {
/* 198 */       List principalIds = (List)principalIdsByScheme.get(p.getProvider());
/* 199 */       if (principalIds == null) {
/* 200 */         principalIds = new ArrayList();
/* 201 */         principalIdsByScheme.put(p.getProvider(), principalIds);
/*     */       }
/* 203 */       principalIds.add(p.getId());
/*     */     }
/* 205 */     for (String scheme : principalIdsByScheme.keySet()) {
/* 206 */       generator.key(scheme).array();
/* 207 */       for (String principalId : (List)principalIdsByScheme.get(scheme)) {
/* 208 */         generator.value(principalId);
/*     */       }
/* 210 */       generator.endArray();
/*     */     }
/* 212 */     generator.endObject();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.internal.JsonPolicyWriter
 * JD-Core Version:    0.6.2
 */