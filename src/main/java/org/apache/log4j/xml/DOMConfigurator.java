/*      */ package org.apache.log4j.xml;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.FactoryConfigurationError;
/*      */ import org.apache.log4j.Appender;
/*      */ import org.apache.log4j.Layout;
/*      */ import org.apache.log4j.Level;
/*      */ import org.apache.log4j.LogManager;
/*      */ import org.apache.log4j.Logger;
/*      */ import org.apache.log4j.config.PropertySetter;
/*      */ import org.apache.log4j.helpers.Loader;
/*      */ import org.apache.log4j.helpers.LogLog;
/*      */ import org.apache.log4j.helpers.OptionConverter;
/*      */ import org.apache.log4j.or.RendererMap;
/*      */ import org.apache.log4j.spi.AppenderAttachable;
/*      */ import org.apache.log4j.spi.Configurator;
/*      */ import org.apache.log4j.spi.ErrorHandler;
/*      */ import org.apache.log4j.spi.Filter;
/*      */ import org.apache.log4j.spi.LoggerFactory;
/*      */ import org.apache.log4j.spi.LoggerRepository;
/*      */ import org.apache.log4j.spi.RendererSupport;
/*      */ import org.apache.log4j.spi.ThrowableRenderer;
/*      */ import org.apache.log4j.spi.ThrowableRendererSupport;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class DOMConfigurator
/*      */   implements Configurator
/*      */ {
/*      */   static final String CONFIGURATION_TAG = "log4j:configuration";
/*      */   static final String OLD_CONFIGURATION_TAG = "configuration";
/*      */   static final String RENDERER_TAG = "renderer";
/*      */   private static final String THROWABLE_RENDERER_TAG = "throwableRenderer";
/*      */   static final String APPENDER_TAG = "appender";
/*      */   static final String APPENDER_REF_TAG = "appender-ref";
/*      */   static final String PARAM_TAG = "param";
/*      */   static final String LAYOUT_TAG = "layout";
/*      */   static final String CATEGORY = "category";
/*      */   static final String LOGGER = "logger";
/*      */   static final String LOGGER_REF = "logger-ref";
/*      */   static final String CATEGORY_FACTORY_TAG = "categoryFactory";
/*      */   static final String LOGGER_FACTORY_TAG = "loggerFactory";
/*      */   static final String NAME_ATTR = "name";
/*      */   static final String CLASS_ATTR = "class";
/*      */   static final String VALUE_ATTR = "value";
/*      */   static final String ROOT_TAG = "root";
/*      */   static final String ROOT_REF = "root-ref";
/*      */   static final String LEVEL_TAG = "level";
/*      */   static final String PRIORITY_TAG = "priority";
/*      */   static final String FILTER_TAG = "filter";
/*      */   static final String ERROR_HANDLER_TAG = "errorHandler";
/*      */   static final String REF_ATTR = "ref";
/*      */   static final String ADDITIVITY_ATTR = "additivity";
/*      */   static final String THRESHOLD_ATTR = "threshold";
/*      */   static final String CONFIG_DEBUG_ATTR = "configDebug";
/*      */   static final String INTERNAL_DEBUG_ATTR = "debug";
/*      */   private static final String RESET_ATTR = "reset";
/*      */   static final String RENDERING_CLASS_ATTR = "renderingClass";
/*      */   static final String RENDERED_CLASS_ATTR = "renderedClass";
/*      */   static final String EMPTY_STR = "";
/*  124 */   static final Class[] ONE_STRING_PARAM = { String.class };
/*      */   static final String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";
/*      */   Hashtable appenderBag;
/*      */   Properties props;
/*      */   LoggerRepository repository;
/*  135 */   protected LoggerFactory catFactory = null;
/*      */ 
/*      */   public DOMConfigurator()
/*      */   {
/*  142 */     this.appenderBag = new Hashtable();
/*      */   }
/*      */ 
/*      */   protected Appender findAppenderByName(Document doc, String appenderName)
/*      */   {
/*  150 */     Appender appender = (Appender)this.appenderBag.get(appenderName);
/*      */ 
/*  152 */     if (appender != null) {
/*  153 */       return appender;
/*      */     }
/*      */ 
/*  159 */     Element element = null;
/*  160 */     NodeList list = doc.getElementsByTagName("appender");
/*  161 */     for (int t = 0; t < list.getLength(); t++) {
/*  162 */       Node node = list.item(t);
/*  163 */       NamedNodeMap map = node.getAttributes();
/*  164 */       Node attrNode = map.getNamedItem("name");
/*  165 */       if (appenderName.equals(attrNode.getNodeValue())) {
/*  166 */         element = (Element)node;
/*  167 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  172 */     if (element == null) {
/*  173 */       LogLog.error("No appender named [" + appenderName + "] could be found.");
/*  174 */       return null;
/*      */     }
/*  176 */     appender = parseAppender(element);
/*  177 */     if (appender != null) {
/*  178 */       this.appenderBag.put(appenderName, appender);
/*      */     }
/*  180 */     return appender;
/*      */   }
/*      */ 
/*      */   protected Appender findAppenderByReference(Element appenderRef)
/*      */   {
/*  189 */     String appenderName = subst(appenderRef.getAttribute("ref"));
/*  190 */     Document doc = appenderRef.getOwnerDocument();
/*  191 */     return findAppenderByName(doc, appenderName);
/*      */   }
/*      */ 
/*      */   private static void parseUnrecognizedElement(Object instance, Element element, Properties props)
/*      */     throws Exception
/*      */   {
/*  207 */     boolean recognized = false;
/*  208 */     if ((instance instanceof UnrecognizedElementHandler)) {
/*  209 */       recognized = ((UnrecognizedElementHandler)instance).parseUnrecognizedElement(element, props);
/*      */     }
/*      */ 
/*  212 */     if (!recognized)
/*  213 */       LogLog.warn("Unrecognized element " + element.getNodeName());
/*      */   }
/*      */ 
/*      */   private static void quietParseUnrecognizedElement(Object instance, Element element, Properties props)
/*      */   {
/*      */     try
/*      */     {
/*  230 */       parseUnrecognizedElement(instance, element, props);
/*      */     } catch (Exception ex) {
/*  232 */       if (((ex instanceof InterruptedException)) || ((ex instanceof InterruptedIOException))) {
/*  233 */         Thread.currentThread().interrupt();
/*      */       }
/*  235 */       LogLog.error("Error in extension content: ", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Appender parseAppender(Element appenderElement)
/*      */   {
/*  244 */     String className = subst(appenderElement.getAttribute("class"));
/*  245 */     LogLog.debug("Class name: [" + className + ']');
/*      */     try {
/*  247 */       Object instance = Loader.loadClass(className).newInstance();
/*  248 */       Appender appender = (Appender)instance;
/*  249 */       PropertySetter propSetter = new PropertySetter(appender);
/*      */ 
/*  251 */       appender.setName(subst(appenderElement.getAttribute("name")));
/*      */ 
/*  253 */       NodeList children = appenderElement.getChildNodes();
/*  254 */       int length = children.getLength();
/*      */ 
/*  256 */       for (int loop = 0; loop < length; loop++) {
/*  257 */         Node currentNode = children.item(loop);
/*      */ 
/*  260 */         if (currentNode.getNodeType() == 1) {
/*  261 */           Element currentElement = (Element)currentNode;
/*      */ 
/*  264 */           if (currentElement.getTagName().equals("param")) {
/*  265 */             setParameter(currentElement, propSetter);
/*      */           }
/*  268 */           else if (currentElement.getTagName().equals("layout")) {
/*  269 */             appender.setLayout(parseLayout(currentElement));
/*      */           }
/*  272 */           else if (currentElement.getTagName().equals("filter")) {
/*  273 */             parseFilters(currentElement, appender);
/*      */           }
/*  275 */           else if (currentElement.getTagName().equals("errorHandler")) {
/*  276 */             parseErrorHandler(currentElement, appender);
/*      */           }
/*  278 */           else if (currentElement.getTagName().equals("appender-ref")) {
/*  279 */             String refName = subst(currentElement.getAttribute("ref"));
/*  280 */             if ((appender instanceof AppenderAttachable)) {
/*  281 */               AppenderAttachable aa = (AppenderAttachable)appender;
/*  282 */               LogLog.debug("Attaching appender named [" + refName + "] to appender named [" + appender.getName() + "].");
/*      */ 
/*  284 */               aa.addAppender(findAppenderByReference(currentElement));
/*      */             } else {
/*  286 */               LogLog.error("Requesting attachment of appender named [" + refName + "] to appender named [" + appender.getName() + "] which does not implement org.apache.log4j.spi.AppenderAttachable.");
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  291 */             parseUnrecognizedElement(instance, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*  295 */       propSetter.activate();
/*  296 */       return appender;
/*      */     }
/*      */     catch (Exception oops)
/*      */     {
/*  301 */       if (((oops instanceof InterruptedException)) || ((oops instanceof InterruptedIOException))) {
/*  302 */         Thread.currentThread().interrupt();
/*      */       }
/*  304 */       LogLog.error("Could not create an Appender. Reported error follows.", oops);
/*      */     }
/*  306 */     return null;
/*      */   }
/*      */ 
/*      */   protected void parseErrorHandler(Element element, Appender appender)
/*      */   {
/*  315 */     ErrorHandler eh = (ErrorHandler)OptionConverter.instantiateByClassName(subst(element.getAttribute("class")), ErrorHandler.class, null);
/*      */ 
/*  320 */     if (eh != null) {
/*  321 */       eh.setAppender(appender);
/*      */ 
/*  323 */       PropertySetter propSetter = new PropertySetter(eh);
/*  324 */       NodeList children = element.getChildNodes();
/*  325 */       int length = children.getLength();
/*      */ 
/*  327 */       for (int loop = 0; loop < length; loop++) {
/*  328 */         Node currentNode = children.item(loop);
/*  329 */         if (currentNode.getNodeType() == 1) {
/*  330 */           Element currentElement = (Element)currentNode;
/*  331 */           String tagName = currentElement.getTagName();
/*  332 */           if (tagName.equals("param")) {
/*  333 */             setParameter(currentElement, propSetter);
/*  334 */           } else if (tagName.equals("appender-ref")) {
/*  335 */             eh.setBackupAppender(findAppenderByReference(currentElement));
/*  336 */           } else if (tagName.equals("logger-ref")) {
/*  337 */             String loggerName = currentElement.getAttribute("ref");
/*  338 */             Logger logger = this.catFactory == null ? this.repository.getLogger(loggerName) : this.repository.getLogger(loggerName, this.catFactory);
/*      */ 
/*  340 */             eh.setLogger(logger);
/*  341 */           } else if (tagName.equals("root-ref")) {
/*  342 */             Logger root = this.repository.getRootLogger();
/*  343 */             eh.setLogger(root);
/*      */           } else {
/*  345 */             quietParseUnrecognizedElement(eh, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*  349 */       propSetter.activate();
/*  350 */       appender.setErrorHandler(eh);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseFilters(Element element, Appender appender)
/*      */   {
/*  359 */     String clazz = subst(element.getAttribute("class"));
/*  360 */     Filter filter = (Filter)OptionConverter.instantiateByClassName(clazz, Filter.class, null);
/*      */ 
/*  363 */     if (filter != null) {
/*  364 */       PropertySetter propSetter = new PropertySetter(filter);
/*  365 */       NodeList children = element.getChildNodes();
/*  366 */       int length = children.getLength();
/*      */ 
/*  368 */       for (int loop = 0; loop < length; loop++) {
/*  369 */         Node currentNode = children.item(loop);
/*  370 */         if (currentNode.getNodeType() == 1) {
/*  371 */           Element currentElement = (Element)currentNode;
/*  372 */           String tagName = currentElement.getTagName();
/*  373 */           if (tagName.equals("param"))
/*  374 */             setParameter(currentElement, propSetter);
/*      */           else {
/*  376 */             quietParseUnrecognizedElement(filter, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*  380 */       propSetter.activate();
/*  381 */       LogLog.debug("Adding filter of type [" + filter.getClass() + "] to appender named [" + appender.getName() + "].");
/*      */ 
/*  383 */       appender.addFilter(filter);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseCategory(Element loggerElement)
/*      */   {
/*  393 */     String catName = subst(loggerElement.getAttribute("name"));
/*      */ 
/*  397 */     String className = subst(loggerElement.getAttribute("class"));
/*      */     Logger cat;
/*      */     Logger cat;
/*  400 */     if ("".equals(className)) {
/*  401 */       LogLog.debug("Retreiving an instance of org.apache.log4j.Logger.");
/*  402 */       cat = this.catFactory == null ? this.repository.getLogger(catName) : this.repository.getLogger(catName, this.catFactory);
/*      */     }
/*      */     else {
/*  405 */       LogLog.debug("Desired logger sub-class: [" + className + ']');
/*      */       try {
/*  407 */         Class clazz = Loader.loadClass(className);
/*  408 */         Method getInstanceMethod = clazz.getMethod("getLogger", ONE_STRING_PARAM);
/*      */ 
/*  410 */         cat = (Logger)getInstanceMethod.invoke(null, new Object[] { catName });
/*      */       } catch (InvocationTargetException oops) {
/*  412 */         if (((oops.getTargetException() instanceof InterruptedException)) || ((oops.getTargetException() instanceof InterruptedIOException)))
/*      */         {
/*  414 */           Thread.currentThread().interrupt();
/*      */         }
/*  416 */         LogLog.error("Could not retrieve category [" + catName + "]. Reported error follows.", oops);
/*      */ 
/*  418 */         return;
/*      */       } catch (Exception oops) {
/*  420 */         LogLog.error("Could not retrieve category [" + catName + "]. Reported error follows.", oops);
/*      */ 
/*  422 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  429 */     synchronized (cat) {
/*  430 */       boolean additivity = OptionConverter.toBoolean(subst(loggerElement.getAttribute("additivity")), true);
/*      */ 
/*  434 */       LogLog.debug("Setting [" + cat.getName() + "] additivity to [" + additivity + "].");
/*  435 */       cat.setAdditivity(additivity);
/*  436 */       parseChildrenOfLoggerElement(loggerElement, cat, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseCategoryFactory(Element factoryElement)
/*      */   {
/*  446 */     String className = subst(factoryElement.getAttribute("class"));
/*      */ 
/*  448 */     if ("".equals(className)) {
/*  449 */       LogLog.error("Category Factory tag class attribute not found.");
/*  450 */       LogLog.debug("No Category Factory configured.");
/*      */     }
/*      */     else {
/*  453 */       LogLog.debug("Desired category factory: [" + className + ']');
/*  454 */       Object factory = OptionConverter.instantiateByClassName(className, LoggerFactory.class, null);
/*      */ 
/*  457 */       if ((factory instanceof LoggerFactory))
/*  458 */         this.catFactory = ((LoggerFactory)factory);
/*      */       else {
/*  460 */         LogLog.error("Category Factory class " + className + " does not implement org.apache.log4j.LoggerFactory");
/*      */       }
/*  462 */       PropertySetter propSetter = new PropertySetter(factory);
/*      */ 
/*  464 */       Element currentElement = null;
/*  465 */       Node currentNode = null;
/*  466 */       NodeList children = factoryElement.getChildNodes();
/*  467 */       int length = children.getLength();
/*      */ 
/*  469 */       for (int loop = 0; loop < length; loop++) {
/*  470 */         currentNode = children.item(loop);
/*  471 */         if (currentNode.getNodeType() == 1) {
/*  472 */           currentElement = (Element)currentNode;
/*  473 */           if (currentElement.getTagName().equals("param"))
/*  474 */             setParameter(currentElement, propSetter);
/*      */           else
/*  476 */             quietParseUnrecognizedElement(factory, currentElement, this.props);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseRoot(Element rootElement)
/*      */   {
/*  489 */     Logger root = this.repository.getRootLogger();
/*      */ 
/*  491 */     synchronized (root) {
/*  492 */       parseChildrenOfLoggerElement(rootElement, root, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseChildrenOfLoggerElement(Element catElement, Logger cat, boolean isRoot)
/*      */   {
/*  504 */     PropertySetter propSetter = new PropertySetter(cat);
/*      */ 
/*  508 */     cat.removeAllAppenders();
/*      */ 
/*  511 */     NodeList children = catElement.getChildNodes();
/*  512 */     int length = children.getLength();
/*      */ 
/*  514 */     for (int loop = 0; loop < length; loop++) {
/*  515 */       Node currentNode = children.item(loop);
/*      */ 
/*  517 */       if (currentNode.getNodeType() == 1) {
/*  518 */         Element currentElement = (Element)currentNode;
/*  519 */         String tagName = currentElement.getTagName();
/*      */ 
/*  521 */         if (tagName.equals("appender-ref")) {
/*  522 */           Element appenderRef = (Element)currentNode;
/*  523 */           Appender appender = findAppenderByReference(appenderRef);
/*  524 */           String refName = subst(appenderRef.getAttribute("ref"));
/*  525 */           if (appender != null) {
/*  526 */             LogLog.debug("Adding appender named [" + refName + "] to category [" + cat.getName() + "].");
/*      */           }
/*      */           else {
/*  529 */             LogLog.debug("Appender named [" + refName + "] not found.");
/*      */           }
/*  531 */           cat.addAppender(appender);
/*      */         }
/*  533 */         else if (tagName.equals("level")) {
/*  534 */           parseLevel(currentElement, cat, isRoot);
/*  535 */         } else if (tagName.equals("priority")) {
/*  536 */           parseLevel(currentElement, cat, isRoot);
/*  537 */         } else if (tagName.equals("param")) {
/*  538 */           setParameter(currentElement, propSetter);
/*      */         } else {
/*  540 */           quietParseUnrecognizedElement(cat, currentElement, this.props);
/*      */         }
/*      */       }
/*      */     }
/*  544 */     propSetter.activate();
/*      */   }
/*      */ 
/*      */   protected Layout parseLayout(Element layout_element)
/*      */   {
/*  552 */     String className = subst(layout_element.getAttribute("class"));
/*  553 */     LogLog.debug("Parsing layout of class: \"" + className + "\"");
/*      */     try {
/*  555 */       Object instance = Loader.loadClass(className).newInstance();
/*  556 */       Layout layout = (Layout)instance;
/*  557 */       PropertySetter propSetter = new PropertySetter(layout);
/*      */ 
/*  559 */       NodeList params = layout_element.getChildNodes();
/*  560 */       int length = params.getLength();
/*      */ 
/*  562 */       for (int loop = 0; loop < length; loop++) {
/*  563 */         Node currentNode = params.item(loop);
/*  564 */         if (currentNode.getNodeType() == 1) {
/*  565 */           Element currentElement = (Element)currentNode;
/*  566 */           String tagName = currentElement.getTagName();
/*  567 */           if (tagName.equals("param"))
/*  568 */             setParameter(currentElement, propSetter);
/*      */           else {
/*  570 */             parseUnrecognizedElement(instance, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  575 */       propSetter.activate();
/*  576 */       return layout;
/*      */     }
/*      */     catch (Exception oops) {
/*  579 */       if (((oops instanceof InterruptedException)) || ((oops instanceof InterruptedIOException))) {
/*  580 */         Thread.currentThread().interrupt();
/*      */       }
/*  582 */       LogLog.error("Could not create the Layout. Reported error follows.", oops);
/*      */     }
/*  584 */     return null;
/*      */   }
/*      */ 
/*      */   protected void parseRenderer(Element element)
/*      */   {
/*  590 */     String renderingClass = subst(element.getAttribute("renderingClass"));
/*  591 */     String renderedClass = subst(element.getAttribute("renderedClass"));
/*  592 */     if ((this.repository instanceof RendererSupport))
/*  593 */       RendererMap.addRenderer((RendererSupport)this.repository, renderedClass, renderingClass);
/*      */   }
/*      */ 
/*      */   protected ThrowableRenderer parseThrowableRenderer(Element element)
/*      */   {
/*  605 */     String className = subst(element.getAttribute("class"));
/*  606 */     LogLog.debug("Parsing throwableRenderer of class: \"" + className + "\"");
/*      */     try {
/*  608 */       Object instance = Loader.loadClass(className).newInstance();
/*  609 */       ThrowableRenderer tr = (ThrowableRenderer)instance;
/*  610 */       PropertySetter propSetter = new PropertySetter(tr);
/*      */ 
/*  612 */       NodeList params = element.getChildNodes();
/*  613 */       int length = params.getLength();
/*      */ 
/*  615 */       for (int loop = 0; loop < length; loop++) {
/*  616 */         Node currentNode = params.item(loop);
/*  617 */         if (currentNode.getNodeType() == 1) {
/*  618 */           Element currentElement = (Element)currentNode;
/*  619 */           String tagName = currentElement.getTagName();
/*  620 */           if (tagName.equals("param"))
/*  621 */             setParameter(currentElement, propSetter);
/*      */           else {
/*  623 */             parseUnrecognizedElement(instance, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  628 */       propSetter.activate();
/*  629 */       return tr;
/*      */     }
/*      */     catch (Exception oops) {
/*  632 */       if (((oops instanceof InterruptedException)) || ((oops instanceof InterruptedIOException))) {
/*  633 */         Thread.currentThread().interrupt();
/*      */       }
/*  635 */       LogLog.error("Could not create the ThrowableRenderer. Reported error follows.", oops);
/*      */     }
/*  637 */     return null;
/*      */   }
/*      */ 
/*      */   protected void parseLevel(Element element, Logger logger, boolean isRoot)
/*      */   {
/*  646 */     String catName = logger.getName();
/*  647 */     if (isRoot) {
/*  648 */       catName = "root";
/*      */     }
/*      */ 
/*  651 */     String priStr = subst(element.getAttribute("value"));
/*  652 */     LogLog.debug("Level value for " + catName + " is  [" + priStr + "].");
/*      */ 
/*  654 */     if (("inherited".equalsIgnoreCase(priStr)) || ("null".equalsIgnoreCase(priStr))) {
/*  655 */       if (isRoot)
/*  656 */         LogLog.error("Root level cannot be inherited. Ignoring directive.");
/*      */       else
/*  658 */         logger.setLevel(null);
/*      */     }
/*      */     else {
/*  661 */       String className = subst(element.getAttribute("class"));
/*  662 */       if ("".equals(className)) {
/*  663 */         logger.setLevel(OptionConverter.toLevel(priStr, Level.DEBUG));
/*      */       } else {
/*  665 */         LogLog.debug("Desired Level sub-class: [" + className + ']');
/*      */         try {
/*  667 */           Class clazz = Loader.loadClass(className);
/*  668 */           Method toLevelMethod = clazz.getMethod("toLevel", ONE_STRING_PARAM);
/*      */ 
/*  670 */           Level pri = (Level)toLevelMethod.invoke(null, new Object[] { priStr });
/*      */ 
/*  672 */           logger.setLevel(pri);
/*      */         } catch (Exception oops) {
/*  674 */           if (((oops instanceof InterruptedException)) || ((oops instanceof InterruptedIOException))) {
/*  675 */             Thread.currentThread().interrupt();
/*      */           }
/*  677 */           LogLog.error("Could not create level [" + priStr + "]. Reported error follows.", oops);
/*      */ 
/*  679 */           return;
/*      */         }
/*      */       }
/*      */     }
/*  683 */     LogLog.debug(catName + " level set to " + logger.getLevel());
/*      */   }
/*      */ 
/*      */   protected void setParameter(Element elem, PropertySetter propSetter)
/*      */   {
/*  688 */     String name = subst(elem.getAttribute("name"));
/*  689 */     String value = elem.getAttribute("value");
/*  690 */     value = subst(OptionConverter.convertSpecialChars(value));
/*  691 */     propSetter.setProperty(name, value);
/*      */   }
/*      */ 
/*      */   public static void configure(Element element)
/*      */   {
/*  703 */     DOMConfigurator configurator = new DOMConfigurator();
/*  704 */     configurator.doConfigure(element, LogManager.getLoggerRepository());
/*      */   }
/*      */ 
/*      */   public static void configureAndWatch(String configFilename)
/*      */   {
/*  718 */     configureAndWatch(configFilename, 60000L);
/*      */   }
/*      */ 
/*      */   public static void configureAndWatch(String configFilename, long delay)
/*      */   {
/*  735 */     XMLWatchdog xdog = new XMLWatchdog(configFilename);
/*  736 */     xdog.setDelay(delay);
/*  737 */     xdog.start();
/*      */   }
/*      */ 
/*      */   public void doConfigure(final String filename, LoggerRepository repository)
/*      */   {
/*  747 */     ParseAction action = new ParseAction() { private final String val$filename;
/*      */ 
/*  749 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { return parser.parse(new File(filename)); }
/*      */ 
/*      */       public String toString() {
/*  752 */         return "file [" + filename + "]";
/*      */       }
/*      */     };
/*  755 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   public void doConfigure(final URL url, LoggerRepository repository)
/*      */   {
/*  761 */     ParseAction action = new ParseAction() { private final URL val$url;
/*      */ 
/*  763 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { URLConnection uConn = url.openConnection();
/*  764 */         uConn.setUseCaches(false);
/*  765 */         InputStream stream = uConn.getInputStream();
/*      */         try {
/*  767 */           InputSource src = new InputSource(stream);
/*  768 */           src.setSystemId(url.toString());
/*  769 */           return parser.parse(src);
/*      */         } finally {
/*  771 */           stream.close();
/*      */         } }
/*      */ 
/*      */       public String toString() {
/*  775 */         return "url [" + url.toString() + "]";
/*      */       }
/*      */     };
/*  778 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   public void doConfigure(final InputStream inputStream, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  789 */     ParseAction action = new ParseAction() { private final InputStream val$inputStream;
/*      */ 
/*  791 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { InputSource inputSource = new InputSource(inputStream);
/*  792 */         inputSource.setSystemId("dummy://log4j.dtd");
/*  793 */         return parser.parse(inputSource); }
/*      */ 
/*      */       public String toString() {
/*  796 */         return "input stream [" + inputStream.toString() + "]";
/*      */       }
/*      */     };
/*  799 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   public void doConfigure(final Reader reader, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  810 */     ParseAction action = new ParseAction() { private final Reader val$reader;
/*      */ 
/*  812 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { InputSource inputSource = new InputSource(reader);
/*  813 */         inputSource.setSystemId("dummy://log4j.dtd");
/*  814 */         return parser.parse(inputSource); }
/*      */ 
/*      */       public String toString() {
/*  817 */         return "reader [" + reader.toString() + "]";
/*      */       }
/*      */     };
/*  820 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   protected void doConfigure(final InputSource inputSource, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  831 */     if (inputSource.getSystemId() == null) {
/*  832 */       inputSource.setSystemId("dummy://log4j.dtd");
/*      */     }
/*  834 */     ParseAction action = new ParseAction() { private final InputSource val$inputSource;
/*      */ 
/*  836 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { return parser.parse(inputSource); }
/*      */ 
/*      */       public String toString() {
/*  839 */         return "input source [" + inputSource.toString() + "]";
/*      */       }
/*      */     };
/*  842 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   private final void doConfigure(ParseAction action, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  848 */     DocumentBuilderFactory dbf = null;
/*  849 */     this.repository = repository;
/*      */     try {
/*  851 */       LogLog.debug("System property is :" + OptionConverter.getSystemProperty("javax.xml.parsers.DocumentBuilderFactory", null));
/*      */ 
/*  854 */       dbf = DocumentBuilderFactory.newInstance();
/*  855 */       LogLog.debug("Standard DocumentBuilderFactory search succeded.");
/*  856 */       LogLog.debug("DocumentBuilderFactory is: " + dbf.getClass().getName());
/*      */     } catch (FactoryConfigurationError fce) {
/*  858 */       Exception e = fce.getException();
/*  859 */       LogLog.debug("Could not instantiate a DocumentBuilderFactory.", e);
/*  860 */       throw fce;
/*      */     }
/*      */     try
/*      */     {
/*  864 */       dbf.setValidating(true);
/*      */ 
/*  866 */       DocumentBuilder docBuilder = dbf.newDocumentBuilder();
/*      */ 
/*  868 */       docBuilder.setErrorHandler(new SAXErrorHandler());
/*  869 */       docBuilder.setEntityResolver(new Log4jEntityResolver());
/*      */ 
/*  871 */       Document doc = action.parse(docBuilder);
/*  872 */       parse(doc.getDocumentElement());
/*      */     } catch (Exception e) {
/*  874 */       if (((e instanceof InterruptedException)) || ((e instanceof InterruptedIOException))) {
/*  875 */         Thread.currentThread().interrupt();
/*      */       }
/*      */ 
/*  878 */       LogLog.error("Could not parse " + action.toString() + ".", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void doConfigure(Element element, LoggerRepository repository)
/*      */   {
/*  886 */     this.repository = repository;
/*  887 */     parse(element);
/*      */   }
/*      */ 
/*      */   public static void configure(String filename)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  896 */     new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
/*      */   }
/*      */ 
/*      */   public static void configure(URL url)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  906 */     new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
/*      */   }
/*      */ 
/*      */   protected void parse(Element element)
/*      */   {
/*  918 */     String rootElementName = element.getTagName();
/*      */ 
/*  920 */     if (!rootElementName.equals("log4j:configuration")) {
/*  921 */       if (rootElementName.equals("configuration")) {
/*  922 */         LogLog.warn("The <configuration> element has been deprecated.");
/*      */ 
/*  924 */         LogLog.warn("Use the <log4j:configuration> element instead.");
/*      */       } else {
/*  926 */         LogLog.error("DOM element is - not a <log4j:configuration> element.");
/*  927 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  932 */     String debugAttrib = subst(element.getAttribute("debug"));
/*      */ 
/*  934 */     LogLog.debug("debug attribute= \"" + debugAttrib + "\".");
/*      */ 
/*  937 */     if ((!debugAttrib.equals("")) && (!debugAttrib.equals("null")))
/*  938 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(debugAttrib, true));
/*      */     else {
/*  940 */       LogLog.debug("Ignoring debug attribute.");
/*      */     }
/*      */ 
/*  947 */     String resetAttrib = subst(element.getAttribute("reset"));
/*  948 */     LogLog.debug("reset attribute= \"" + resetAttrib + "\".");
/*  949 */     if ((!"".equals(resetAttrib)) && 
/*  950 */       (OptionConverter.toBoolean(resetAttrib, false))) {
/*  951 */       this.repository.resetConfiguration();
/*      */     }
/*      */ 
/*  957 */     String confDebug = subst(element.getAttribute("configDebug"));
/*  958 */     if ((!confDebug.equals("")) && (!confDebug.equals("null"))) {
/*  959 */       LogLog.warn("The \"configDebug\" attribute is deprecated.");
/*  960 */       LogLog.warn("Use the \"debug\" attribute instead.");
/*  961 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(confDebug, true));
/*      */     }
/*      */ 
/*  964 */     String thresholdStr = subst(element.getAttribute("threshold"));
/*  965 */     LogLog.debug("Threshold =\"" + thresholdStr + "\".");
/*  966 */     if ((!"".equals(thresholdStr)) && (!"null".equals(thresholdStr))) {
/*  967 */       this.repository.setThreshold(thresholdStr);
/*      */     }
/*      */ 
/*  979 */     String tagName = null;
/*  980 */     Element currentElement = null;
/*  981 */     Node currentNode = null;
/*  982 */     NodeList children = element.getChildNodes();
/*  983 */     int length = children.getLength();
/*      */ 
/*  985 */     for (int loop = 0; loop < length; loop++) {
/*  986 */       currentNode = children.item(loop);
/*  987 */       if (currentNode.getNodeType() == 1) {
/*  988 */         currentElement = (Element)currentNode;
/*  989 */         tagName = currentElement.getTagName();
/*      */ 
/*  991 */         if ((tagName.equals("categoryFactory")) || (tagName.equals("loggerFactory"))) {
/*  992 */           parseCategoryFactory(currentElement);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  997 */     for (int loop = 0; loop < length; loop++) {
/*  998 */       currentNode = children.item(loop);
/*  999 */       if (currentNode.getNodeType() == 1) {
/* 1000 */         currentElement = (Element)currentNode;
/* 1001 */         tagName = currentElement.getTagName();
/*      */ 
/* 1003 */         if ((tagName.equals("category")) || (tagName.equals("logger")))
/* 1004 */           parseCategory(currentElement);
/* 1005 */         else if (tagName.equals("root"))
/* 1006 */           parseRoot(currentElement);
/* 1007 */         else if (tagName.equals("renderer"))
/* 1008 */           parseRenderer(currentElement);
/* 1009 */         else if (tagName.equals("throwableRenderer")) {
/* 1010 */           if ((this.repository instanceof ThrowableRendererSupport)) {
/* 1011 */             ThrowableRenderer tr = parseThrowableRenderer(currentElement);
/* 1012 */             if (tr != null)
/* 1013 */               ((ThrowableRendererSupport)this.repository).setThrowableRenderer(tr);
/*      */           }
/*      */         }
/* 1016 */         else if ((!tagName.equals("appender")) && (!tagName.equals("categoryFactory")) && (!tagName.equals("loggerFactory")))
/*      */         {
/* 1019 */           quietParseUnrecognizedElement(this.repository, currentElement, this.props);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String subst(String value)
/*      */   {
/* 1028 */     return subst(value, this.props);
/*      */   }
/*      */ 
/*      */   public static String subst(String value, Properties props)
/*      */   {
/*      */     try
/*      */     {
/* 1043 */       return OptionConverter.substVars(value, props);
/*      */     } catch (IllegalArgumentException e) {
/* 1045 */       LogLog.warn("Could not perform variable substitution.", e);
/* 1046 */     }return value;
/*      */   }
/*      */ 
/*      */   public static void setParameter(Element elem, PropertySetter propSetter, Properties props)
/*      */   {
/* 1062 */     String name = subst(elem.getAttribute("name"), props);
/* 1063 */     String value = elem.getAttribute("value");
/* 1064 */     value = subst(OptionConverter.convertSpecialChars(value), props);
/* 1065 */     propSetter.setProperty(name, value);
/*      */   }
/*      */ 
/*      */   public static Object parseElement(Element element, Properties props, Class expectedClass)
/*      */     throws Exception
/*      */   {
/* 1085 */     String clazz = subst(element.getAttribute("class"), props);
/* 1086 */     Object instance = OptionConverter.instantiateByClassName(clazz, expectedClass, null);
/*      */ 
/* 1089 */     if (instance != null) {
/* 1090 */       PropertySetter propSetter = new PropertySetter(instance);
/* 1091 */       NodeList children = element.getChildNodes();
/* 1092 */       int length = children.getLength();
/*      */ 
/* 1094 */       for (int loop = 0; loop < length; loop++) {
/* 1095 */         Node currentNode = children.item(loop);
/* 1096 */         if (currentNode.getNodeType() == 1) {
/* 1097 */           Element currentElement = (Element)currentNode;
/* 1098 */           String tagName = currentElement.getTagName();
/* 1099 */           if (tagName.equals("param"))
/* 1100 */             setParameter(currentElement, propSetter, props);
/*      */           else {
/* 1102 */             parseUnrecognizedElement(instance, currentElement, props);
/*      */           }
/*      */         }
/*      */       }
/* 1106 */       return instance;
/*      */     }
/* 1108 */     return null;
/*      */   }
/*      */ 
/*      */   private static abstract interface ParseAction
/*      */   {
/*      */     public abstract Document parse(DocumentBuilder paramDocumentBuilder)
/*      */       throws SAXException, IOException;
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.xml.DOMConfigurator
 * JD-Core Version:    0.6.2
 */