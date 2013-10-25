/*     */ package org.apache.log4j.lf5.viewer.configure;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.tree.TreePath;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogLevelFormatException;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ import org.apache.log4j.lf5.viewer.LogTable;
/*     */ import org.apache.log4j.lf5.viewer.LogTableColumn;
/*     */ import org.apache.log4j.lf5.viewer.LogTableColumnFormatException;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNode;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class ConfigurationManager
/*     */ {
/*     */   private static final String CONFIG_FILE_NAME = "lf5_configuration.xml";
/*     */   private static final String NAME = "name";
/*     */   private static final String PATH = "path";
/*     */   private static final String SELECTED = "selected";
/*     */   private static final String EXPANDED = "expanded";
/*     */   private static final String CATEGORY = "category";
/*     */   private static final String FIRST_CATEGORY_NAME = "Categories";
/*     */   private static final String LEVEL = "level";
/*     */   private static final String COLORLEVEL = "colorlevel";
/*     */   private static final String RED = "red";
/*     */   private static final String GREEN = "green";
/*     */   private static final String BLUE = "blue";
/*     */   private static final String COLUMN = "column";
/*     */   private static final String NDCTEXTFILTER = "searchtext";
/*  85 */   private LogBrokerMonitor _monitor = null;
/*  86 */   private LogTable _table = null;
/*     */ 
/*     */   public ConfigurationManager(LogBrokerMonitor monitor, LogTable table)
/*     */   {
/*  93 */     this._monitor = monitor;
/*  94 */     this._table = table;
/*  95 */     load();
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/* 102 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/* 103 */     CategoryNode root = model.getRootCategoryNode();
/*     */ 
/* 105 */     StringBuffer xml = new StringBuffer(2048);
/* 106 */     openXMLDocument(xml);
/* 107 */     openConfigurationXML(xml);
/* 108 */     processLogRecordFilter(this._monitor.getNDCTextFilter(), xml);
/* 109 */     processLogLevels(this._monitor.getLogLevelMenuItems(), xml);
/* 110 */     processLogLevelColors(this._monitor.getLogLevelMenuItems(), LogLevel.getLogLevelColorMap(), xml);
/*     */ 
/* 112 */     processLogTableColumns(LogTableColumn.getLogTableColumns(), xml);
/* 113 */     processConfigurationNode(root, xml);
/* 114 */     closeConfigurationXML(xml);
/* 115 */     store(xml.toString());
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 119 */     deleteConfigurationFile();
/* 120 */     collapseTree();
/* 121 */     selectAllNodes();
/*     */   }
/*     */ 
/*     */   public static String treePathToString(TreePath path)
/*     */   {
/* 126 */     StringBuffer sb = new StringBuffer();
/* 127 */     CategoryNode n = null;
/* 128 */     Object[] objects = path.getPath();
/* 129 */     for (int i = 1; i < objects.length; i++) {
/* 130 */       n = (CategoryNode)objects[i];
/* 131 */       if (i > 1) {
/* 132 */         sb.append(".");
/*     */       }
/* 134 */       sb.append(n.getTitle());
/*     */     }
/* 136 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 143 */     File file = new File(getFilename());
/* 144 */     if (file.exists())
/*     */       try {
/* 146 */         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 148 */         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/* 149 */         Document doc = docBuilder.parse(file);
/* 150 */         processRecordFilter(doc);
/* 151 */         processCategories(doc);
/* 152 */         processLogLevels(doc);
/* 153 */         processLogLevelColors(doc);
/* 154 */         processLogTableColumns(doc);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 158 */         System.err.println("Unable process configuration file at " + getFilename() + ". Error Message=" + e.getMessage());
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void processRecordFilter(Document doc)
/*     */   {
/* 171 */     NodeList nodeList = doc.getElementsByTagName("searchtext");
/*     */ 
/* 174 */     Node n = nodeList.item(0);
/*     */ 
/* 177 */     if (n == null) {
/* 178 */       return;
/*     */     }
/*     */ 
/* 181 */     NamedNodeMap map = n.getAttributes();
/* 182 */     String text = getValue(map, "name");
/*     */ 
/* 184 */     if ((text == null) || (text.equals(""))) {
/* 185 */       return;
/*     */     }
/* 187 */     this._monitor.setNDCLogRecordFilter(text);
/*     */   }
/*     */ 
/*     */   protected void processCategories(Document doc) {
/* 191 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/* 192 */     CategoryExplorerModel model = tree.getExplorerModel();
/* 193 */     NodeList nodeList = doc.getElementsByTagName("category");
/*     */ 
/* 196 */     NamedNodeMap map = nodeList.item(0).getAttributes();
/* 197 */     int j = getValue(map, "name").equalsIgnoreCase("Categories") ? 1 : 0;
/*     */ 
/* 200 */     for (int i = nodeList.getLength() - 1; i >= j; i--) {
/* 201 */       Node n = nodeList.item(i);
/* 202 */       map = n.getAttributes();
/* 203 */       CategoryNode chnode = model.addCategory(new CategoryPath(getValue(map, "path")));
/* 204 */       chnode.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/* 205 */       if (getValue(map, "expanded").equalsIgnoreCase("true"));
/* 206 */       tree.expandPath(model.getTreePathToRoot(chnode));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevels(Document doc)
/*     */   {
/* 212 */     NodeList nodeList = doc.getElementsByTagName("level");
/* 213 */     Map menuItems = this._monitor.getLogLevelMenuItems();
/*     */ 
/* 215 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 216 */       Node n = nodeList.item(i);
/* 217 */       NamedNodeMap map = n.getAttributes();
/* 218 */       String name = getValue(map, "name");
/*     */       try {
/* 220 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(LogLevel.valueOf(name));
/*     */ 
/* 222 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */       }
/*     */       catch (LogLevelFormatException e) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevelColors(Document doc) {
/* 230 */     NodeList nodeList = doc.getElementsByTagName("colorlevel");
/* 231 */     LogLevel.getLogLevelColorMap();
/*     */ 
/* 233 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 234 */       Node n = nodeList.item(i);
/*     */ 
/* 237 */       if (n == null) {
/* 238 */         return;
/*     */       }
/*     */ 
/* 241 */       NamedNodeMap map = n.getAttributes();
/* 242 */       String name = getValue(map, "name");
/*     */       try {
/* 244 */         LogLevel level = LogLevel.valueOf(name);
/* 245 */         int red = Integer.parseInt(getValue(map, "red"));
/* 246 */         int green = Integer.parseInt(getValue(map, "green"));
/* 247 */         int blue = Integer.parseInt(getValue(map, "blue"));
/* 248 */         Color c = new Color(red, green, blue);
/* 249 */         if (level != null)
/* 250 */           level.setLogLevelColorMap(level, c);
/*     */       }
/*     */       catch (LogLevelFormatException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogTableColumns(Document doc)
/*     */   {
/* 260 */     NodeList nodeList = doc.getElementsByTagName("column");
/* 261 */     Map menuItems = this._monitor.getLogTableColumnMenuItems();
/* 262 */     List selectedColumns = new ArrayList();
/* 263 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 264 */       Node n = nodeList.item(i);
/*     */ 
/* 267 */       if (n == null) {
/* 268 */         return;
/*     */       }
/* 270 */       NamedNodeMap map = n.getAttributes();
/* 271 */       String name = getValue(map, "name");
/*     */       try {
/* 273 */         LogTableColumn column = LogTableColumn.valueOf(name);
/* 274 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(column);
/*     */ 
/* 276 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */ 
/* 278 */         if (item.isSelected()) {
/* 279 */           selectedColumns.add(column);
/*     */         }
/*     */       }
/*     */       catch (LogTableColumnFormatException e)
/*     */       {
/*     */       }
/* 285 */       if (selectedColumns.isEmpty())
/* 286 */         this._table.setDetailedView();
/*     */       else
/* 288 */         this._table.setView(selectedColumns);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getValue(NamedNodeMap map, String attr)
/*     */   {
/* 295 */     Node n = map.getNamedItem(attr);
/* 296 */     return n.getNodeValue();
/*     */   }
/*     */ 
/*     */   protected void collapseTree()
/*     */   {
/* 301 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/* 302 */     for (int i = tree.getRowCount() - 1; i > 0; i--)
/* 303 */       tree.collapseRow(i);
/*     */   }
/*     */ 
/*     */   protected void selectAllNodes()
/*     */   {
/* 308 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/* 309 */     CategoryNode root = model.getRootCategoryNode();
/* 310 */     Enumeration all = root.breadthFirstEnumeration();
/* 311 */     CategoryNode n = null;
/* 312 */     while (all.hasMoreElements()) {
/* 313 */       n = (CategoryNode)all.nextElement();
/* 314 */       n.setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void store(String s)
/*     */   {
/*     */     try {
/* 321 */       PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));
/* 322 */       writer.print(s);
/* 323 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 326 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void deleteConfigurationFile()
/*     */   {
/*     */     try {
/* 333 */       File f = new File(getFilename());
/* 334 */       if (f.exists())
/* 335 */         f.delete();
/*     */     }
/*     */     catch (SecurityException e) {
/* 338 */       System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 344 */     String home = System.getProperty("user.home");
/* 345 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 347 */     return home + sep + "lf5" + sep + "lf5_configuration.xml";
/*     */   }
/*     */ 
/*     */   private void processConfigurationNode(CategoryNode node, StringBuffer xml)
/*     */   {
/* 354 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/*     */ 
/* 356 */     Enumeration all = node.breadthFirstEnumeration();
/* 357 */     CategoryNode n = null;
/* 358 */     while (all.hasMoreElements()) {
/* 359 */       n = (CategoryNode)all.nextElement();
/* 360 */       exportXMLElement(n, model.getTreePathToRoot(n), xml);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processLogLevels(Map logLevelMenuItems, StringBuffer xml)
/*     */   {
/* 366 */     xml.append("\t<loglevels>\r\n");
/* 367 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 368 */     while (it.hasNext()) {
/* 369 */       LogLevel level = (LogLevel)it.next();
/* 370 */       JCheckBoxMenuItem item = (JCheckBoxMenuItem)logLevelMenuItems.get(level);
/* 371 */       exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 374 */     xml.append("\t</loglevels>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogLevelColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
/* 378 */     xml.append("\t<loglevelcolors>\r\n");
/*     */ 
/* 380 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 381 */     while (it.hasNext()) {
/* 382 */       LogLevel level = (LogLevel)it.next();
/*     */ 
/* 384 */       Color color = (Color)logLevelColors.get(level);
/* 385 */       exportLogLevelColorXMLElement(level.getLabel(), color, xml);
/*     */     }
/*     */ 
/* 388 */     xml.append("\t</loglevelcolors>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml)
/*     */   {
/* 393 */     xml.append("\t<logtablecolumns>\r\n");
/* 394 */     Iterator it = logTableColumnMenuItems.iterator();
/* 395 */     while (it.hasNext()) {
/* 396 */       LogTableColumn column = (LogTableColumn)it.next();
/* 397 */       JCheckBoxMenuItem item = this._monitor.getTableColumnMenuItem(column);
/* 398 */       exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 401 */     xml.append("\t</logtablecolumns>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogRecordFilter(String text, StringBuffer xml)
/*     */   {
/* 407 */     xml.append("\t<").append("searchtext").append(" ");
/* 408 */     xml.append("name").append("=\"").append(text).append("\"");
/* 409 */     xml.append("/>\r\n");
/*     */   }
/*     */ 
/*     */   private void openXMLDocument(StringBuffer xml) {
/* 413 */     xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
/*     */   }
/*     */ 
/*     */   private void openConfigurationXML(StringBuffer xml) {
/* 417 */     xml.append("<configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void closeConfigurationXML(StringBuffer xml) {
/* 421 */     xml.append("</configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportXMLElement(CategoryNode node, TreePath path, StringBuffer xml) {
/* 425 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/*     */ 
/* 427 */     xml.append("\t<").append("category").append(" ");
/* 428 */     xml.append("name").append("=\"").append(node.getTitle()).append("\" ");
/* 429 */     xml.append("path").append("=\"").append(treePathToString(path)).append("\" ");
/* 430 */     xml.append("expanded").append("=\"").append(tree.isExpanded(path)).append("\" ");
/* 431 */     xml.append("selected").append("=\"").append(node.isSelected()).append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelXMLElement(String label, boolean selected, StringBuffer xml) {
/* 435 */     xml.append("\t\t<").append("level").append(" ").append("name");
/* 436 */     xml.append("=\"").append(label).append("\" ");
/* 437 */     xml.append("selected").append("=\"").append(selected);
/* 438 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelColorXMLElement(String label, Color color, StringBuffer xml) {
/* 442 */     xml.append("\t\t<").append("colorlevel").append(" ").append("name");
/* 443 */     xml.append("=\"").append(label).append("\" ");
/* 444 */     xml.append("red").append("=\"").append(color.getRed()).append("\" ");
/* 445 */     xml.append("green").append("=\"").append(color.getGreen()).append("\" ");
/* 446 */     xml.append("blue").append("=\"").append(color.getBlue());
/* 447 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogTableColumnXMLElement(String label, boolean selected, StringBuffer xml) {
/* 451 */     xml.append("\t\t<").append("column").append(" ").append("name");
/* 452 */     xml.append("=\"").append(label).append("\" ");
/* 453 */     xml.append("selected").append("=\"").append(selected);
/* 454 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.configure.ConfigurationManager
 * JD-Core Version:    0.6.2
 */