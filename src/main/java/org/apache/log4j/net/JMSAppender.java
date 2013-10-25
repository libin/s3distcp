/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicPublisher;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NameNotFoundException;
/*     */ import javax.naming.NamingException;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class JMSAppender extends AppenderSkeleton
/*     */ {
/*     */   String securityPrincipalName;
/*     */   String securityCredentials;
/*     */   String initialContextFactoryName;
/*     */   String urlPkgPrefixes;
/*     */   String providerURL;
/*     */   String topicBindingName;
/*     */   String tcfBindingName;
/*     */   String userName;
/*     */   String password;
/*     */   boolean locationInfo;
/*     */   TopicConnection topicConnection;
/*     */   TopicSession topicSession;
/*     */   TopicPublisher topicPublisher;
/*     */ 
/*     */   public void setTopicConnectionFactoryBindingName(String tcfBindingName)
/*     */   {
/* 130 */     this.tcfBindingName = tcfBindingName;
/*     */   }
/*     */ 
/*     */   public String getTopicConnectionFactoryBindingName()
/*     */   {
/* 138 */     return this.tcfBindingName;
/*     */   }
/*     */ 
/*     */   public void setTopicBindingName(String topicBindingName)
/*     */   {
/* 148 */     this.topicBindingName = topicBindingName;
/*     */   }
/*     */ 
/*     */   public String getTopicBindingName()
/*     */   {
/* 156 */     return this.topicBindingName;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 166 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */     try
/*     */     {
/* 178 */       LogLog.debug("Getting initial context.");
/*     */       Context jndi;
/*     */       Context jndi;
/* 179 */       if (this.initialContextFactoryName != null) {
/* 180 */         Properties env = new Properties();
/* 181 */         env.put("java.naming.factory.initial", this.initialContextFactoryName);
/* 182 */         if (this.providerURL != null)
/* 183 */           env.put("java.naming.provider.url", this.providerURL);
/*     */         else {
/* 185 */           LogLog.warn("You have set InitialContextFactoryName option but not the ProviderURL. This is likely to cause problems.");
/*     */         }
/*     */ 
/* 188 */         if (this.urlPkgPrefixes != null) {
/* 189 */           env.put("java.naming.factory.url.pkgs", this.urlPkgPrefixes);
/*     */         }
/*     */ 
/* 192 */         if (this.securityPrincipalName != null) {
/* 193 */           env.put("java.naming.security.principal", this.securityPrincipalName);
/* 194 */           if (this.securityCredentials != null)
/* 195 */             env.put("java.naming.security.credentials", this.securityCredentials);
/*     */           else {
/* 197 */             LogLog.warn("You have set SecurityPrincipalName option but not the SecurityCredentials. This is likely to cause problems.");
/*     */           }
/*     */         }
/*     */ 
/* 201 */         jndi = new InitialContext(env);
/*     */       } else {
/* 203 */         jndi = new InitialContext();
/*     */       }
/*     */ 
/* 206 */       LogLog.debug("Looking up [" + this.tcfBindingName + "]");
/* 207 */       TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)lookup(jndi, this.tcfBindingName);
/* 208 */       LogLog.debug("About to create TopicConnection.");
/* 209 */       if (this.userName != null) {
/* 210 */         this.topicConnection = topicConnectionFactory.createTopicConnection(this.userName, this.password);
/*     */       }
/*     */       else {
/* 213 */         this.topicConnection = topicConnectionFactory.createTopicConnection();
/*     */       }
/*     */ 
/* 216 */       LogLog.debug("Creating TopicSession, non-transactional, in AUTO_ACKNOWLEDGE mode.");
/*     */ 
/* 218 */       this.topicSession = this.topicConnection.createTopicSession(false, 1);
/*     */ 
/* 221 */       LogLog.debug("Looking up topic name [" + this.topicBindingName + "].");
/* 222 */       Topic topic = (Topic)lookup(jndi, this.topicBindingName);
/*     */ 
/* 224 */       LogLog.debug("Creating TopicPublisher.");
/* 225 */       this.topicPublisher = this.topicSession.createPublisher(topic);
/*     */ 
/* 227 */       LogLog.debug("Starting TopicConnection.");
/* 228 */       this.topicConnection.start();
/*     */ 
/* 230 */       jndi.close();
/*     */     } catch (JMSException e) {
/* 232 */       this.errorHandler.error("Error while activating options for appender named [" + this.name + "].", e, 0);
/*     */     }
/*     */     catch (NamingException e) {
/* 235 */       this.errorHandler.error("Error while activating options for appender named [" + this.name + "].", e, 0);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 238 */       this.errorHandler.error("Error while activating options for appender named [" + this.name + "].", e, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object lookup(Context ctx, String name) throws NamingException
/*     */   {
/*     */     try {
/* 245 */       return ctx.lookup(name);
/*     */     } catch (NameNotFoundException e) {
/* 247 */       LogLog.error("Could not find name [" + name + "].");
/* 248 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions() {
/* 253 */     String fail = null;
/*     */ 
/* 255 */     if (this.topicConnection == null)
/* 256 */       fail = "No TopicConnection";
/* 257 */     else if (this.topicSession == null)
/* 258 */       fail = "No TopicSession";
/* 259 */     else if (this.topicPublisher == null) {
/* 260 */       fail = "No TopicPublisher";
/*     */     }
/*     */ 
/* 263 */     if (fail != null) {
/* 264 */       this.errorHandler.error(fail + " for JMSAppender named [" + this.name + "].");
/* 265 */       return false;
/*     */     }
/* 267 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 277 */     if (this.closed) {
/* 278 */       return;
/*     */     }
/* 280 */     LogLog.debug("Closing appender [" + this.name + "].");
/* 281 */     this.closed = true;
/*     */     try
/*     */     {
/* 284 */       if (this.topicSession != null)
/* 285 */         this.topicSession.close();
/* 286 */       if (this.topicConnection != null)
/* 287 */         this.topicConnection.close();
/*     */     } catch (JMSException e) {
/* 289 */       LogLog.error("Error while closing JMSAppender [" + this.name + "].", e);
/*     */     } catch (RuntimeException e) {
/* 291 */       LogLog.error("Error while closing JMSAppender [" + this.name + "].", e);
/*     */     }
/*     */ 
/* 294 */     this.topicPublisher = null;
/* 295 */     this.topicSession = null;
/* 296 */     this.topicConnection = null;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 303 */     if (!checkEntryConditions()) {
/* 304 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 308 */       ObjectMessage msg = this.topicSession.createObjectMessage();
/* 309 */       if (this.locationInfo) {
/* 310 */         event.getLocationInformation();
/*     */       }
/* 312 */       msg.setObject(event);
/* 313 */       this.topicPublisher.publish(msg);
/*     */     } catch (JMSException e) {
/* 315 */       this.errorHandler.error("Could not publish message in JMSAppender [" + this.name + "].", e, 0);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 318 */       this.errorHandler.error("Could not publish message in JMSAppender [" + this.name + "].", e, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getInitialContextFactoryName()
/*     */   {
/* 329 */     return this.initialContextFactoryName;
/*     */   }
/*     */ 
/*     */   public void setInitialContextFactoryName(String initialContextFactoryName)
/*     */   {
/* 342 */     this.initialContextFactoryName = initialContextFactoryName;
/*     */   }
/*     */ 
/*     */   public String getProviderURL() {
/* 346 */     return this.providerURL;
/*     */   }
/*     */ 
/*     */   public void setProviderURL(String providerURL) {
/* 350 */     this.providerURL = providerURL;
/*     */   }
/*     */ 
/*     */   String getURLPkgPrefixes() {
/* 354 */     return this.urlPkgPrefixes;
/*     */   }
/*     */ 
/*     */   public void setURLPkgPrefixes(String urlPkgPrefixes) {
/* 358 */     this.urlPkgPrefixes = urlPkgPrefixes;
/*     */   }
/*     */ 
/*     */   public String getSecurityCredentials() {
/* 362 */     return this.securityCredentials;
/*     */   }
/*     */ 
/*     */   public void setSecurityCredentials(String securityCredentials) {
/* 366 */     this.securityCredentials = securityCredentials;
/*     */   }
/*     */ 
/*     */   public String getSecurityPrincipalName()
/*     */   {
/* 371 */     return this.securityPrincipalName;
/*     */   }
/*     */ 
/*     */   public void setSecurityPrincipalName(String securityPrincipalName) {
/* 375 */     this.securityPrincipalName = securityPrincipalName;
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/* 379 */     return this.userName;
/*     */   }
/*     */ 
/*     */   public void setUserName(String userName)
/*     */   {
/* 390 */     this.userName = userName;
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 394 */     return this.password;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 401 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 410 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   protected TopicConnection getTopicConnection()
/*     */   {
/* 418 */     return this.topicConnection;
/*     */   }
/*     */ 
/*     */   protected TopicSession getTopicSession()
/*     */   {
/* 426 */     return this.topicSession;
/*     */   }
/*     */ 
/*     */   protected TopicPublisher getTopicPublisher()
/*     */   {
/* 434 */     return this.topicPublisher;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 442 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.JMSAppender
 * JD-Core Version:    0.6.2
 */