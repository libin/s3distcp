/*    */ package com.amazonaws.transform;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.codehaus.jackson.JsonToken;
/*    */ 
/*    */ public class ListUnmarshaller<T>
/*    */   implements Unmarshaller<List<T>, JsonUnmarshallerContext>
/*    */ {
/*    */   private final Unmarshaller<T, JsonUnmarshallerContext> itemUnmarshaller;
/*    */ 
/*    */   public ListUnmarshaller(Unmarshaller<T, JsonUnmarshallerContext> itemUnmarshaller)
/*    */   {
/* 30 */     this.itemUnmarshaller = itemUnmarshaller;
/*    */   }
/*    */ 
/*    */   public List<T> unmarshall(JsonUnmarshallerContext context) throws Exception {
/* 34 */     List list = new ArrayList();
/* 35 */     int originalDepth = context.getCurrentDepth();
/* 36 */     int targetDepth = originalDepth + 1;
/*    */     while (true)
/*    */     {
/* 39 */       JsonToken token = context.nextToken();
/* 40 */       if (token == null) return list;
/*    */ 
/* 42 */       if (token != JsonToken.START_ARRAY)
/*    */       {
/* 44 */         if ((token == JsonToken.END_ARRAY) || (token == JsonToken.END_OBJECT)) {
/* 45 */           if (context.getCurrentDepth() < originalDepth) return list; 
/*    */         }
/*    */         else
/* 47 */           list.add(this.itemUnmarshaller.unmarshall(context));
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.ListUnmarshaller
 * JD-Core Version:    0.6.2
 */