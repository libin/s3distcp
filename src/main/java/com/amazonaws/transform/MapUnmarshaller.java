/*    */ package com.amazonaws.transform;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.codehaus.jackson.JsonToken;
/*    */ 
/*    */ public class MapUnmarshaller<K, V>
/*    */   implements Unmarshaller<Map<K, V>, JsonUnmarshallerContext>
/*    */ {
/*    */   private final Unmarshaller<K, JsonUnmarshallerContext> keyUnmarshaller;
/*    */   private final Unmarshaller<V, JsonUnmarshallerContext> valueUnmarshaller;
/*    */ 
/*    */   public MapUnmarshaller(Unmarshaller<K, JsonUnmarshallerContext> keyUnmarshaller, Unmarshaller<V, JsonUnmarshallerContext> valueUnmarshaller)
/*    */   {
/* 33 */     this.keyUnmarshaller = keyUnmarshaller;
/* 34 */     this.valueUnmarshaller = valueUnmarshaller;
/*    */   }
/*    */ 
/*    */   public Map<K, V> unmarshall(JsonUnmarshallerContext context) throws Exception {
/* 38 */     Map map = new HashMap();
/* 39 */     int originalDepth = context.getCurrentDepth();
/*    */     while (true)
/*    */     {
/* 42 */       JsonToken token = context.nextToken();
/* 43 */       if (token == null) return map;
/*    */ 
/* 45 */       if (token == JsonToken.FIELD_NAME) {
/* 46 */         Object k = this.keyUnmarshaller.unmarshall(context);
/* 47 */         token = context.nextToken();
/* 48 */         Object v = this.valueUnmarshaller.unmarshall(context);
/* 49 */         map.put(k, v);
/* 50 */       } else if (((token == JsonToken.END_ARRAY) || (token == JsonToken.END_OBJECT)) && 
/* 51 */         (context.getCurrentDepth() <= originalDepth)) { return map; }
/*    */ 
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.MapUnmarshaller
 * JD-Core Version:    0.6.2
 */