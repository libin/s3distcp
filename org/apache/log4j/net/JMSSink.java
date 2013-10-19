/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.jms.TopicSubscriber;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NameNotFoundException;
/*     */ import javax.naming.NamingException;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.xml.DOMConfigurator;
/*     */ 
/*     */ public class JMSSink
/*     */   implements MessageListener
/*     */ {
/*  49 */   static Logger logger = Logger.getLogger(JMSSink.class);
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/*  52 */     if (args.length != 5) {
/*  53 */       usage("Wrong number of arguments.");
/*     */     }
/*     */ 
/*  56 */     String tcfBindingName = args[0];
/*  57 */     String topicBindingName = args[1];
/*  58 */     String username = args[2];
/*  59 */     String password = args[3];
/*     */ 
/*  62 */     String configFile = args[4];
/*     */ 
/*  64 */     if (configFile.endsWith(".xml"))
/*  65 */       DOMConfigurator.configure(configFile);
/*     */     else {
/*  67 */       PropertyConfigurator.configure(configFile);
/*     */     }
/*     */ 
/*  70 */     new JMSSink(tcfBindingName, topicBindingName, username, password);
/*     */ 
/*  72 */     BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
/*     */ 
/*  74 */     System.out.println("Type \"exit\" to quit JMSSink.");
/*     */     while (true) {
/*  76 */       String s = stdin.readLine();
/*  77 */       if (s.equalsIgnoreCase("exit")) {
/*  78 */         System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
/*     */ 
/*  80 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public JMSSink(String tcfBindingName, String topicBindingName, String username, String password)
/*     */   {
/*     */     try
/*     */     {
/*  89 */       Context ctx = new InitialContext();
/*     */ 
/*  91 */       TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)lookup(ctx, tcfBindingName);
/*     */ 
/*  94 */       TopicConnection topicConnection = topicConnectionFactory.createTopicConnection(username, password);
/*     */ 
/*  97 */       topicConnection.start();
/*     */ 
/*  99 */       TopicSession topicSession = topicConnection.createTopicSession(false, 1);
/*     */ 
/* 102 */       Topic topic = (Topic)ctx.lookup(topicBindingName);
/*     */ 
/* 104 */       TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
/*     */ 
/* 106 */       topicSubscriber.setMessageListener(this);
/*     */     }
/*     */     catch (JMSException e) {
/* 109 */       logger.error("Could not read JMS message.", e);
/*     */     } catch (NamingException e) {
/* 111 */       logger.error("Could not read JMS message.", e);
/*     */     } catch (RuntimeException e) {
/* 113 */       logger.error("Could not read JMS message.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onMessage(Message message)
/*     */   {
/*     */     try
/*     */     {
/* 122 */       if ((message instanceof ObjectMessage)) {
/* 123 */         ObjectMessage objectMessage = (ObjectMessage)message;
/* 124 */         LoggingEvent event = (LoggingEvent)objectMessage.getObject();
/* 125 */         Logger remoteLogger = Logger.getLogger(event.getLoggerName());
/* 126 */         remoteLogger.callAppenders(event);
/*     */       } else {
/* 128 */         logger.warn("Received message is of type " + message.getJMSType() + ", was expecting ObjectMessage.");
/*     */       }
/*     */     }
/*     */     catch (JMSException jmse) {
/* 132 */       logger.error("Exception thrown while processing incoming message.", jmse);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static Object lookup(Context ctx, String name) throws NamingException
/*     */   {
/*     */     try
/*     */     {
/* 140 */       return ctx.lookup(name);
/*     */     } catch (NameNotFoundException e) {
/* 142 */       logger.error("Could not find name [" + name + "].");
/* 143 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void usage(String msg) {
/* 148 */     System.err.println(msg);
/* 149 */     System.err.println("Usage: java " + JMSSink.class.getName() + " TopicConnectionFactoryBindingName TopicBindingName username password configFile");
/*     */ 
/* 151 */     System.exit(1);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.JMSSink
 * JD-Core Version:    0.6.2
 */