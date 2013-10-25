/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.util.Date;
/*     */ import java.util.Properties;
/*     */ import javax.mail.Authenticator;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.Message.RecipientType;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.PasswordAuthentication;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.Transport;
/*     */ import javax.mail.internet.AddressException;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.InternetHeaders;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.CyclicBuffer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ import org.apache.log4j.spi.TriggeringEventEvaluator;
/*     */ import org.apache.log4j.xml.DOMConfigurator;
/*     */ import org.apache.log4j.xml.UnrecognizedElementHandler;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class SMTPAppender extends AppenderSkeleton
/*     */   implements UnrecognizedElementHandler
/*     */ {
/*     */   private String to;
/*     */   private String cc;
/*     */   private String bcc;
/*     */   private String from;
/*     */   private String replyTo;
/*     */   private String subject;
/*     */   private String smtpHost;
/*     */   private String smtpUsername;
/*     */   private String smtpPassword;
/*     */   private String smtpProtocol;
/* 100 */   private int smtpPort = -1;
/* 101 */   private boolean smtpDebug = false;
/* 102 */   private int bufferSize = 512;
/* 103 */   private boolean locationInfo = false;
/* 104 */   private boolean sendOnClose = false;
/*     */ 
/* 106 */   protected CyclicBuffer cb = new CyclicBuffer(this.bufferSize);
/*     */   protected Message msg;
/*     */   protected TriggeringEventEvaluator evaluator;
/*     */ 
/*     */   public SMTPAppender()
/*     */   {
/* 119 */     this(new DefaultEvaluator());
/*     */   }
/*     */ 
/*     */   public SMTPAppender(TriggeringEventEvaluator evaluator)
/*     */   {
/* 128 */     this.evaluator = evaluator;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 137 */     Session session = createSession();
/* 138 */     this.msg = new MimeMessage(session);
/*     */     try
/*     */     {
/* 141 */       addressMessage(this.msg);
/* 142 */       if (this.subject != null)
/*     */         try {
/* 144 */           this.msg.setSubject(MimeUtility.encodeText(this.subject, "UTF-8", null));
/*     */         } catch (UnsupportedEncodingException ex) {
/* 146 */           LogLog.error("Unable to encode SMTP subject", ex);
/*     */         }
/*     */     }
/*     */     catch (MessagingException e) {
/* 150 */       LogLog.error("Could not activate SMTPAppender options.", e);
/*     */     }
/*     */ 
/* 153 */     if ((this.evaluator instanceof OptionHandler))
/* 154 */       ((OptionHandler)this.evaluator).activateOptions();
/*     */   }
/*     */ 
/*     */   protected void addressMessage(Message msg)
/*     */     throws MessagingException
/*     */   {
/* 165 */     if (this.from != null)
/* 166 */       msg.setFrom(getAddress(this.from));
/*     */     else {
/* 168 */       msg.setFrom();
/*     */     }
/*     */ 
/* 172 */     if ((this.replyTo != null) && (this.replyTo.length() > 0)) {
/* 173 */       msg.setReplyTo(parseAddress(this.replyTo));
/*     */     }
/*     */ 
/* 176 */     if ((this.to != null) && (this.to.length() > 0)) {
/* 177 */       msg.setRecipients(Message.RecipientType.TO, parseAddress(this.to));
/*     */     }
/*     */ 
/* 181 */     if ((this.cc != null) && (this.cc.length() > 0)) {
/* 182 */       msg.setRecipients(Message.RecipientType.CC, parseAddress(this.cc));
/*     */     }
/*     */ 
/* 186 */     if ((this.bcc != null) && (this.bcc.length() > 0))
/* 187 */       msg.setRecipients(Message.RecipientType.BCC, parseAddress(this.bcc));
/*     */   }
/*     */ 
/*     */   protected Session createSession()
/*     */   {
/* 197 */     Properties props = null;
/*     */     try {
/* 199 */       props = new Properties(System.getProperties());
/*     */     } catch (SecurityException ex) {
/* 201 */       props = new Properties();
/*     */     }
/*     */ 
/* 204 */     String prefix = "mail.smtp";
/* 205 */     if (this.smtpProtocol != null) {
/* 206 */       props.put("mail.transport.protocol", this.smtpProtocol);
/* 207 */       prefix = "mail." + this.smtpProtocol;
/*     */     }
/* 209 */     if (this.smtpHost != null) {
/* 210 */       props.put(prefix + ".host", this.smtpHost);
/*     */     }
/* 212 */     if (this.smtpPort > 0) {
/* 213 */       props.put(prefix + ".port", String.valueOf(this.smtpPort));
/*     */     }
/*     */ 
/* 216 */     Authenticator auth = null;
/* 217 */     if ((this.smtpPassword != null) && (this.smtpUsername != null)) {
/* 218 */       props.put(prefix + ".auth", "true");
/* 219 */       auth = new Authenticator() {
/*     */         protected PasswordAuthentication getPasswordAuthentication() {
/* 221 */           return new PasswordAuthentication(SMTPAppender.this.smtpUsername, SMTPAppender.this.smtpPassword);
/*     */         }
/*     */       };
/*     */     }
/* 225 */     Session session = Session.getInstance(props, auth);
/* 226 */     if (this.smtpProtocol != null) {
/* 227 */       session.setProtocolForAddress("rfc822", this.smtpProtocol);
/*     */     }
/* 229 */     if (this.smtpDebug) {
/* 230 */       session.setDebug(this.smtpDebug);
/*     */     }
/* 232 */     return session;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 242 */     if (!checkEntryConditions()) {
/* 243 */       return;
/*     */     }
/*     */ 
/* 246 */     event.getThreadName();
/* 247 */     event.getNDC();
/* 248 */     event.getMDCCopy();
/* 249 */     if (this.locationInfo) {
/* 250 */       event.getLocationInformation();
/*     */     }
/* 252 */     event.getRenderedMessage();
/* 253 */     event.getThrowableStrRep();
/* 254 */     this.cb.add(event);
/* 255 */     if (this.evaluator.isTriggeringEvent(event))
/* 256 */       sendBuffer();
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions()
/*     */   {
/* 268 */     if (this.msg == null) {
/* 269 */       this.errorHandler.error("Message object not configured.");
/* 270 */       return false;
/*     */     }
/*     */ 
/* 273 */     if (this.evaluator == null) {
/* 274 */       this.errorHandler.error("No TriggeringEventEvaluator is set for appender [" + this.name + "].");
/*     */ 
/* 276 */       return false;
/*     */     }
/*     */ 
/* 280 */     if (this.layout == null) {
/* 281 */       this.errorHandler.error("No layout set for appender named [" + this.name + "].");
/* 282 */       return false;
/*     */     }
/* 284 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 291 */     this.closed = true;
/* 292 */     if ((this.sendOnClose) && (this.cb.length() > 0))
/* 293 */       sendBuffer();
/*     */   }
/*     */ 
/*     */   InternetAddress getAddress(String addressStr)
/*     */   {
/*     */     try {
/* 299 */       return new InternetAddress(addressStr);
/*     */     } catch (AddressException e) {
/* 301 */       this.errorHandler.error("Could not parse address [" + addressStr + "].", e, 6);
/*     */     }
/* 303 */     return null;
/*     */   }
/*     */ 
/*     */   InternetAddress[] parseAddress(String addressStr)
/*     */   {
/*     */     try {
/* 309 */       return InternetAddress.parse(addressStr, true);
/*     */     } catch (AddressException e) {
/* 311 */       this.errorHandler.error("Could not parse address [" + addressStr + "].", e, 6);
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public String getTo()
/*     */   {
/* 322 */     return this.to;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 331 */     return true;
/*     */   }
/*     */ 
/*     */   protected String formatBody()
/*     */   {
/* 343 */     StringBuffer sbuf = new StringBuffer();
/* 344 */     String t = this.layout.getHeader();
/* 345 */     if (t != null)
/* 346 */       sbuf.append(t);
/* 347 */     int len = this.cb.length();
/* 348 */     for (int i = 0; i < len; i++)
/*     */     {
/* 350 */       LoggingEvent event = this.cb.get();
/* 351 */       sbuf.append(this.layout.format(event));
/* 352 */       if (this.layout.ignoresThrowable()) {
/* 353 */         String[] s = event.getThrowableStrRep();
/* 354 */         if (s != null) {
/* 355 */           for (int j = 0; j < s.length; j++) {
/* 356 */             sbuf.append(s[j]);
/* 357 */             sbuf.append(Layout.LINE_SEP);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 362 */     t = this.layout.getFooter();
/* 363 */     if (t != null) {
/* 364 */       sbuf.append(t);
/*     */     }
/*     */ 
/* 367 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   protected void sendBuffer()
/*     */   {
/*     */     try
/*     */     {
/* 377 */       String s = formatBody();
/* 378 */       boolean allAscii = true;
/* 379 */       for (int i = 0; (i < s.length()) && (allAscii); i++)
/* 380 */         allAscii = s.charAt(i) <= '';
/*     */       MimeBodyPart part;
/* 383 */       if (allAscii) {
/* 384 */         MimeBodyPart part = new MimeBodyPart();
/* 385 */         part.setContent(s, this.layout.getContentType());
/*     */       } else {
/*     */         try {
/* 388 */           ByteArrayOutputStream os = new ByteArrayOutputStream();
/* 389 */           Writer writer = new OutputStreamWriter(MimeUtility.encode(os, "quoted-printable"), "UTF-8");
/*     */ 
/* 391 */           writer.write(s);
/* 392 */           writer.close();
/* 393 */           InternetHeaders headers = new InternetHeaders();
/* 394 */           headers.setHeader("Content-Type", this.layout.getContentType() + "; charset=UTF-8");
/* 395 */           headers.setHeader("Content-Transfer-Encoding", "quoted-printable");
/* 396 */           part = new MimeBodyPart(headers, os.toByteArray());
/*     */         } catch (Exception ex) {
/* 398 */           StringBuffer sbuf = new StringBuffer(s);
/* 399 */           for (int i = 0; i < sbuf.length(); i++) {
/* 400 */             if (sbuf.charAt(i) >= '') {
/* 401 */               sbuf.setCharAt(i, '?');
/*     */             }
/*     */           }
/* 404 */           part = new MimeBodyPart();
/* 405 */           part.setContent(sbuf.toString(), this.layout.getContentType());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 411 */       Multipart mp = new MimeMultipart();
/* 412 */       mp.addBodyPart(part);
/* 413 */       this.msg.setContent(mp);
/*     */ 
/* 415 */       this.msg.setSentDate(new Date());
/* 416 */       Transport.send(this.msg);
/*     */     } catch (MessagingException e) {
/* 418 */       LogLog.error("Error occured while sending e-mail notification.", e);
/*     */     } catch (RuntimeException e) {
/* 420 */       LogLog.error("Error occured while sending e-mail notification.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getEvaluatorClass()
/*     */   {
/* 431 */     return this.evaluator == null ? null : this.evaluator.getClass().getName();
/*     */   }
/*     */ 
/*     */   public String getFrom()
/*     */   {
/* 439 */     return this.from;
/*     */   }
/*     */ 
/*     */   public String getReplyTo()
/*     */   {
/* 449 */     return this.replyTo;
/*     */   }
/*     */ 
/*     */   public String getSubject()
/*     */   {
/* 457 */     return this.subject;
/*     */   }
/*     */ 
/*     */   public void setFrom(String from)
/*     */   {
/* 466 */     this.from = from;
/*     */   }
/*     */ 
/*     */   public void setReplyTo(String addresses)
/*     */   {
/* 476 */     this.replyTo = addresses;
/*     */   }
/*     */ 
/*     */   public void setSubject(String subject)
/*     */   {
/* 486 */     this.subject = subject;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize)
/*     */   {
/* 499 */     this.bufferSize = bufferSize;
/* 500 */     this.cb.resize(bufferSize);
/*     */   }
/*     */ 
/*     */   public void setSMTPHost(String smtpHost)
/*     */   {
/* 509 */     this.smtpHost = smtpHost;
/*     */   }
/*     */ 
/*     */   public String getSMTPHost()
/*     */   {
/* 517 */     return this.smtpHost;
/*     */   }
/*     */ 
/*     */   public void setTo(String to)
/*     */   {
/* 526 */     this.to = to;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 536 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setEvaluatorClass(String value)
/*     */   {
/* 548 */     this.evaluator = ((TriggeringEventEvaluator)OptionConverter.instantiateByClassName(value, TriggeringEventEvaluator.class, this.evaluator));
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 568 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 576 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setCc(String addresses)
/*     */   {
/* 585 */     this.cc = addresses;
/*     */   }
/*     */ 
/*     */   public String getCc()
/*     */   {
/* 594 */     return this.cc;
/*     */   }
/*     */ 
/*     */   public void setBcc(String addresses)
/*     */   {
/* 603 */     this.bcc = addresses;
/*     */   }
/*     */ 
/*     */   public String getBcc()
/*     */   {
/* 612 */     return this.bcc;
/*     */   }
/*     */ 
/*     */   public void setSMTPPassword(String password)
/*     */   {
/* 622 */     this.smtpPassword = password;
/*     */   }
/*     */ 
/*     */   public void setSMTPUsername(String username)
/*     */   {
/* 632 */     this.smtpUsername = username;
/*     */   }
/*     */ 
/*     */   public void setSMTPDebug(boolean debug)
/*     */   {
/* 643 */     this.smtpDebug = debug;
/*     */   }
/*     */ 
/*     */   public String getSMTPPassword()
/*     */   {
/* 652 */     return this.smtpPassword;
/*     */   }
/*     */ 
/*     */   public String getSMTPUsername()
/*     */   {
/* 661 */     return this.smtpUsername;
/*     */   }
/*     */ 
/*     */   public boolean getSMTPDebug()
/*     */   {
/* 670 */     return this.smtpDebug;
/*     */   }
/*     */ 
/*     */   public final void setEvaluator(TriggeringEventEvaluator trigger)
/*     */   {
/* 679 */     if (trigger == null) {
/* 680 */       throw new NullPointerException("trigger");
/*     */     }
/* 682 */     this.evaluator = trigger;
/*     */   }
/*     */ 
/*     */   public final TriggeringEventEvaluator getEvaluator()
/*     */   {
/* 691 */     return this.evaluator;
/*     */   }
/*     */ 
/*     */   public boolean parseUnrecognizedElement(Element element, Properties props)
/*     */     throws Exception
/*     */   {
/* 699 */     if ("triggeringPolicy".equals(element.getNodeName())) {
/* 700 */       Object triggerPolicy = DOMConfigurator.parseElement(element, props, TriggeringEventEvaluator.class);
/*     */ 
/* 703 */       if ((triggerPolicy instanceof TriggeringEventEvaluator)) {
/* 704 */         setEvaluator((TriggeringEventEvaluator)triggerPolicy);
/*     */       }
/* 706 */       return true;
/*     */     }
/*     */ 
/* 709 */     return false;
/*     */   }
/*     */ 
/*     */   public final String getSMTPProtocol()
/*     */   {
/* 720 */     return this.smtpProtocol;
/*     */   }
/*     */ 
/*     */   public final void setSMTPProtocol(String val)
/*     */   {
/* 731 */     this.smtpProtocol = val;
/*     */   }
/*     */ 
/*     */   public final int getSMTPPort()
/*     */   {
/* 741 */     return this.smtpPort;
/*     */   }
/*     */ 
/*     */   public final void setSMTPPort(int val)
/*     */   {
/* 751 */     this.smtpPort = val;
/*     */   }
/*     */ 
/*     */   public final boolean getSendOnClose()
/*     */   {
/* 761 */     return this.sendOnClose;
/*     */   }
/*     */ 
/*     */   public final void setSendOnClose(boolean val)
/*     */   {
/* 771 */     this.sendOnClose = val;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.SMTPAppender
 * JD-Core Version:    0.6.2
 */