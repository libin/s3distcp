/*    */ package com.amazonaws.handlers;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class HandlerChainFactory
/*    */ {
/*    */   public List<RequestHandler> newRequestHandlerChain(String resource)
/*    */   {
/* 42 */     List handlers = new ArrayList();
/*    */     try
/*    */     {
/* 45 */       InputStream input = getClass().getResourceAsStream(resource);
/* 46 */       if (input == null) return handlers;
/*    */ 
/* 48 */       BufferedReader reader = new BufferedReader(new InputStreamReader(input));
/*    */       while (true) {
/* 50 */         String requestHandlerClassName = reader.readLine();
/* 51 */         if (requestHandlerClassName == null) break;
/* 52 */         requestHandlerClassName = requestHandlerClassName.trim();
/* 53 */         if (!requestHandlerClassName.equals(""))
/*    */         {
/* 55 */           Class requestHandlerClass = getClass().getClassLoader().loadClass(requestHandlerClassName);
/* 56 */           Object requestHandlerObject = requestHandlerClass.newInstance();
/* 57 */           if ((requestHandlerObject instanceof RequestHandler))
/* 58 */             handlers.add((RequestHandler)requestHandlerObject);
/*    */           else
/* 60 */             throw new AmazonClientException("Unable to instantiate request handler chain for client.  Listed request handler ('" + requestHandlerClassName + "') " + "does not implement the RequestHandler interface.");
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 66 */       throw new AmazonClientException("Unable to instantiate request handler chain for client: " + e.getMessage(), e);
/*    */     }
/*    */ 
/* 70 */     return handlers;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.handlers.HandlerChainFactory
 * JD-Core Version:    0.6.2
 */