/*       */ package org.apache.log4j.rewrite;
/*       */ 
/*       */ import java.util.Enumeration;
/*       */ import java.util.Properties;
/*       */ import org.apache.log4j.Appender;
/*       */ import org.apache.log4j.AppenderSkeleton;
/*       */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*       */ import org.apache.log4j.spi.AppenderAttachable;
/*       */ import org.apache.log4j.spi.LoggingEvent;
/*       */ import org.apache.log4j.spi.OptionHandler;
/*       */ import org.apache.log4j.xml.DOMConfigurator;
/*       */ import org.apache.log4j.xml.UnrecognizedElementHandler;
/*       */ import org.w3c.dom.Element;
/*       */ 
/*       */ public class RewriteAppender extends AppenderSkeleton
/*       */   implements AppenderAttachable, UnrecognizedElementHandler
/*       */ {
/*       */   private RewritePolicy policy;
/*       */   private final AppenderAttachableImpl appenders;
/*       */ 
/*       */   public RewriteAppender()
/*       */   {
/*    51 */     this.appenders = new AppenderAttachableImpl();
/*       */   }
/*       */ 
/*       */   protected void append(LoggingEvent event)
/*       */   {
/*    58 */     LoggingEvent rewritten = event;
/*    59 */     if (this.policy != null) {
/*    60 */       rewritten = this.policy.rewrite(event);
/*       */     }
/*    62 */     if (rewritten != null)
/*    63 */       synchronized (this.appenders) {
/*    64 */         this.appenders.appendLoopOnAppenders(rewritten);
/*       */       }
/*       */   }
/*       */ 
/*       */   public void addAppender(Appender newAppender)
/*       */   {
/*    75 */     synchronized (this.appenders) {
/*    76 */       this.appenders.addAppender(newAppender);
/*       */     }
/*       */   }
/*       */ 
/*       */   public Enumeration getAllAppenders()
/*       */   {
/*    85 */     synchronized (this.appenders) {
/*    86 */       return this.appenders.getAllAppenders();
/*       */     }
/*       */   }
/*       */ 
/*       */   public Appender getAppender(String name)
/*       */   {
/*    97 */     synchronized (this.appenders) {
/*    98 */       return this.appenders.getAppender(name);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void close()
/*       */   {
/*   108 */     this.closed = true;
/*       */ 
/*   112 */     synchronized (this.appenders) {
/*   113 */       Enumeration iter = this.appenders.getAllAppenders();
/*       */ 
/*   115 */       if (iter != null)
/*   116 */         while (iter.hasMoreElements()) {
/*   117 */           Object next = iter.nextElement();
/*       */ 
/*   119 */           if ((next instanceof Appender))
/*   120 */             ((Appender)next).close();
/*       */         }
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean isAttached(Appender appender)
/*       */   {
/*   133 */     synchronized (this.appenders) {
/*   134 */       return this.appenders.isAttached(appender);
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean requiresLayout()
/*       */   {
/*   142 */     return false;
/*       */   }
/*       */ 
/*       */   public void removeAllAppenders()
/*       */   {
/*   149 */     synchronized (this.appenders) {
/*   150 */       this.appenders.removeAllAppenders();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removeAppender(Appender appender)
/*       */   {
/*   159 */     synchronized (this.appenders) {
/*   160 */       this.appenders.removeAppender(appender);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removeAppender(String name)
/*       */   {
/*   169 */     synchronized (this.appenders) {
/*   170 */       this.appenders.removeAppender(name);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setRewritePolicy(RewritePolicy rewritePolicy)
/*       */   {
/*   176 */     this.policy = rewritePolicy;
/*       */   }
/*       */ 
/*       */   public boolean parseUnrecognizedElement(Element element, Properties props)
/*       */     throws Exception
/*       */   {
/*   183 */     String nodeName = element.getNodeName();
/*   184 */     if ("rewritePolicy".equals(nodeName)) {
/*   185 */       Object rewritePolicy = DOMConfigurator.parseElement(element, props, RewritePolicy.class);
/*       */ 
/*   188 */       if (rewritePolicy != null) {
/*   189 */         if ((rewritePolicy instanceof OptionHandler)) {
/*   190 */           ((OptionHandler)rewritePolicy).activateOptions();
/*       */         }
/*   192 */         setRewritePolicy((RewritePolicy)rewritePolicy);
/*       */       }
/*   194 */       return true;
/*       */     }
/*   196 */     return false;
/*       */   }
/*       */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.rewrite.RewriteAppender
 * JD-Core Version:    0.6.2
 */