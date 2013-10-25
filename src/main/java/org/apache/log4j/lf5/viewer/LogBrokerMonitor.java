/*      */ package org.apache.log4j.lf5.viewer;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBoxMenuItem;
/*      */ import javax.swing.JColorChooser;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import org.apache.log4j.lf5.LogLevel;
/*      */ import org.apache.log4j.lf5.LogRecord;
/*      */ import org.apache.log4j.lf5.LogRecordFilter;
/*      */ import org.apache.log4j.lf5.util.DateFormatManager;
/*      */ import org.apache.log4j.lf5.util.LogFileParser;
/*      */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel;
/*      */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree;
/*      */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath;
/*      */ import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
/*      */ import org.apache.log4j.lf5.viewer.configure.MRUFileManager;
/*      */ 
/*      */ public class LogBrokerMonitor
/*      */ {
/*      */   public static final String DETAILED_VIEW = "Detailed";
/*      */   protected JFrame _logMonitorFrame;
/*  100 */   protected int _logMonitorFrameWidth = 550;
/*  101 */   protected int _logMonitorFrameHeight = 500;
/*      */   protected LogTable _table;
/*      */   protected CategoryExplorerTree _categoryExplorerTree;
/*      */   protected String _searchText;
/*  105 */   protected String _NDCTextFilter = "";
/*  106 */   protected LogLevel _leastSevereDisplayedLogLevel = LogLevel.DEBUG;
/*      */   protected JScrollPane _logTableScrollPane;
/*      */   protected JLabel _statusLabel;
/*  110 */   protected Object _lock = new Object();
/*      */   protected JComboBox _fontSizeCombo;
/*  113 */   protected int _fontSize = 10;
/*  114 */   protected String _fontName = "Dialog";
/*  115 */   protected String _currentView = "Detailed";
/*      */ 
/*  117 */   protected boolean _loadSystemFonts = false;
/*  118 */   protected boolean _trackTableScrollPane = true;
/*      */   protected Dimension _lastTableViewportSize;
/*  120 */   protected boolean _callSystemExitOnClose = false;
/*  121 */   protected List _displayedLogBrokerProperties = new Vector();
/*      */ 
/*  123 */   protected Map _logLevelMenuItems = new HashMap();
/*  124 */   protected Map _logTableColumnMenuItems = new HashMap();
/*      */ 
/*  126 */   protected List _levels = null;
/*  127 */   protected List _columns = null;
/*  128 */   protected boolean _isDisposed = false;
/*      */ 
/*  130 */   protected ConfigurationManager _configurationManager = null;
/*  131 */   protected MRUFileManager _mruFileManager = null;
/*  132 */   protected File _fileLocation = null;
/*      */ 
/*      */   public LogBrokerMonitor(List logLevels)
/*      */   {
/*  147 */     this._levels = logLevels;
/*  148 */     this._columns = LogTableColumn.getLogTableColumns();
/*      */ 
/*  152 */     String callSystemExitOnClose = System.getProperty("monitor.exit");
/*      */ 
/*  154 */     if (callSystemExitOnClose == null) {
/*  155 */       callSystemExitOnClose = "false";
/*      */     }
/*  157 */     callSystemExitOnClose = callSystemExitOnClose.trim().toLowerCase();
/*      */ 
/*  159 */     if (callSystemExitOnClose.equals("true")) {
/*  160 */       this._callSystemExitOnClose = true;
/*      */     }
/*      */ 
/*  163 */     initComponents();
/*      */ 
/*  166 */     this._logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));
/*      */   }
/*      */ 
/*      */   public void show(final int delay)
/*      */   {
/*  180 */     if (this._logMonitorFrame.isVisible()) {
/*  181 */       return;
/*      */     }
/*      */ 
/*  184 */     SwingUtilities.invokeLater(new Runnable() { private final int val$delay;
/*      */ 
/*  186 */       public void run() { Thread.yield();
/*  187 */         LogBrokerMonitor.this.pause(delay);
/*  188 */         LogBrokerMonitor.this._logMonitorFrame.setVisible(true); }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void show()
/*      */   {
/*  194 */     show(0);
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  201 */     this._logMonitorFrame.dispose();
/*  202 */     this._isDisposed = true;
/*      */ 
/*  204 */     if (this._callSystemExitOnClose == true)
/*  205 */       System.exit(0);
/*      */   }
/*      */ 
/*      */   public void hide()
/*      */   {
/*  213 */     this._logMonitorFrame.setVisible(false);
/*      */   }
/*      */ 
/*      */   public DateFormatManager getDateFormatManager()
/*      */   {
/*  220 */     return this._table.getDateFormatManager();
/*      */   }
/*      */ 
/*      */   public void setDateFormatManager(DateFormatManager dfm)
/*      */   {
/*  227 */     this._table.setDateFormatManager(dfm);
/*      */   }
/*      */ 
/*      */   public boolean getCallSystemExitOnClose()
/*      */   {
/*  235 */     return this._callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*      */   {
/*  243 */     this._callSystemExitOnClose = callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void addMessage(final LogRecord lr)
/*      */   {
/*  252 */     if (this._isDisposed == true)
/*      */     {
/*  255 */       return;
/*      */     }
/*      */ 
/*  258 */     SwingUtilities.invokeLater(new Runnable() { private final LogRecord val$lr;
/*      */ 
/*  260 */       public void run() { LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().addLogRecord(lr);
/*  261 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().addLogRecord(lr);
/*  262 */         LogBrokerMonitor.this.updateStatusLabel(); }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords)
/*      */   {
/*  268 */     this._table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
/*      */   }
/*      */ 
/*      */   public JFrame getBaseFrame() {
/*  272 */     return this._logMonitorFrame;
/*      */   }
/*      */ 
/*      */   public void setTitle(String title) {
/*  276 */     this._logMonitorFrame.setTitle(title + " - LogFactor5");
/*      */   }
/*      */ 
/*      */   public void setFrameSize(int width, int height) {
/*  280 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/*  281 */     if ((0 < width) && (width < screen.width)) {
/*  282 */       this._logMonitorFrameWidth = width;
/*      */     }
/*  284 */     if ((0 < height) && (height < screen.height)) {
/*  285 */       this._logMonitorFrameHeight = height;
/*      */     }
/*  287 */     updateFrameSize();
/*      */   }
/*      */ 
/*      */   public void setFontSize(int fontSize) {
/*  291 */     changeFontSizeCombo(this._fontSizeCombo, fontSize);
/*      */   }
/*      */ 
/*      */   public void addDisplayedProperty(Object messageLine)
/*      */   {
/*  297 */     this._displayedLogBrokerProperties.add(messageLine);
/*      */   }
/*      */ 
/*      */   public Map getLogLevelMenuItems() {
/*  301 */     return this._logLevelMenuItems;
/*      */   }
/*      */ 
/*      */   public Map getLogTableColumnMenuItems() {
/*  305 */     return this._logTableColumnMenuItems;
/*      */   }
/*      */ 
/*      */   public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
/*  309 */     return getLogTableColumnMenuItem(column);
/*      */   }
/*      */ 
/*      */   public CategoryExplorerTree getCategoryExplorerTree() {
/*  313 */     return this._categoryExplorerTree;
/*      */   }
/*      */ 
/*      */   public String getNDCTextFilter()
/*      */   {
/*  319 */     return this._NDCTextFilter;
/*      */   }
/*      */ 
/*      */   public void setNDCLogRecordFilter(String textFilter)
/*      */   {
/*  326 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(textFilter));
/*      */   }
/*      */ 
/*      */   protected void setSearchText(String text)
/*      */   {
/*  334 */     this._searchText = text;
/*      */   }
/*      */ 
/*      */   protected void setNDCTextFilter(String text)
/*      */   {
/*  341 */     if (text == null)
/*  342 */       this._NDCTextFilter = "";
/*      */     else
/*  344 */       this._NDCTextFilter = text;
/*      */   }
/*      */ 
/*      */   protected void sortByNDC()
/*      */   {
/*  352 */     String text = this._NDCTextFilter;
/*  353 */     if ((text == null) || (text.length() == 0)) {
/*  354 */       return;
/*      */     }
/*      */ 
/*  358 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(text));
/*      */   }
/*      */ 
/*      */   protected void findSearchText()
/*      */   {
/*  363 */     String text = this._searchText;
/*  364 */     if ((text == null) || (text.length() == 0)) {
/*  365 */       return;
/*      */     }
/*  367 */     int startRow = getFirstSelectedRow();
/*  368 */     int foundRow = findRecord(startRow, text, this._table.getFilteredLogTableModel().getFilteredRecords());
/*      */ 
/*  373 */     selectRow(foundRow);
/*      */   }
/*      */ 
/*      */   protected int getFirstSelectedRow() {
/*  377 */     return this._table.getSelectionModel().getMinSelectionIndex();
/*      */   }
/*      */ 
/*      */   protected void selectRow(int foundRow) {
/*  381 */     if (foundRow == -1) {
/*  382 */       String message = this._searchText + " not found.";
/*  383 */       JOptionPane.showMessageDialog(this._logMonitorFrame, message, "Text not found", 1);
/*      */ 
/*  389 */       return;
/*      */     }
/*  391 */     LF5SwingUtils.selectRow(foundRow, this._table, this._logTableScrollPane);
/*      */   }
/*      */ 
/*      */   protected int findRecord(int startRow, String searchText, List records)
/*      */   {
/*  399 */     if (startRow < 0)
/*  400 */       startRow = 0;
/*      */     else {
/*  402 */       startRow++;
/*      */     }
/*  404 */     int len = records.size();
/*      */ 
/*  406 */     for (int i = startRow; i < len; i++) {
/*  407 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  408 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  412 */     len = startRow;
/*  413 */     for (int i = 0; i < len; i++) {
/*  414 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  415 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  419 */     return -1;
/*      */   }
/*      */ 
/*      */   protected boolean matches(LogRecord record, String text)
/*      */   {
/*  427 */     String message = record.getMessage();
/*  428 */     String NDC = record.getNDC();
/*      */ 
/*  430 */     if (((message == null) && (NDC == null)) || (text == null)) {
/*  431 */       return false;
/*      */     }
/*  433 */     if ((message.toLowerCase().indexOf(text.toLowerCase()) == -1) && (NDC.toLowerCase().indexOf(text.toLowerCase()) == -1))
/*      */     {
/*  435 */       return false;
/*      */     }
/*      */ 
/*  438 */     return true;
/*      */   }
/*      */ 
/*      */   protected void refresh(JTextArea textArea)
/*      */   {
/*  447 */     String text = textArea.getText();
/*  448 */     textArea.setText("");
/*  449 */     textArea.setText(text);
/*      */   }
/*      */ 
/*      */   protected void refreshDetailTextArea() {
/*  453 */     refresh(this._table._detailTextArea);
/*      */   }
/*      */ 
/*      */   protected void clearDetailTextArea() {
/*  457 */     this._table._detailTextArea.setText("");
/*      */   }
/*      */ 
/*      */   protected int changeFontSizeCombo(JComboBox box, int requestedSize)
/*      */   {
/*  466 */     int len = box.getItemCount();
/*      */ 
/*  469 */     Object selectedObject = box.getItemAt(0);
/*  470 */     int selectedValue = Integer.parseInt(String.valueOf(selectedObject));
/*  471 */     for (int i = 0; i < len; i++) {
/*  472 */       Object currentObject = box.getItemAt(i);
/*  473 */       int currentValue = Integer.parseInt(String.valueOf(currentObject));
/*  474 */       if ((selectedValue < currentValue) && (currentValue <= requestedSize)) {
/*  475 */         selectedValue = currentValue;
/*  476 */         selectedObject = currentObject;
/*      */       }
/*      */     }
/*  479 */     box.setSelectedItem(selectedObject);
/*  480 */     return selectedValue;
/*      */   }
/*      */ 
/*      */   protected void setFontSizeSilently(int fontSize)
/*      */   {
/*  487 */     this._fontSize = fontSize;
/*  488 */     setFontSize(this._table._detailTextArea, fontSize);
/*  489 */     selectRow(0);
/*  490 */     setFontSize(this._table, fontSize);
/*      */   }
/*      */ 
/*      */   protected void setFontSize(Component component, int fontSize) {
/*  494 */     Font oldFont = component.getFont();
/*  495 */     Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), fontSize);
/*      */ 
/*  497 */     component.setFont(newFont);
/*      */   }
/*      */ 
/*      */   protected void updateFrameSize() {
/*  501 */     this._logMonitorFrame.setSize(this._logMonitorFrameWidth, this._logMonitorFrameHeight);
/*  502 */     centerFrame(this._logMonitorFrame);
/*      */   }
/*      */ 
/*      */   protected void pause(int millis) {
/*      */     try {
/*  507 */       Thread.sleep(millis);
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void initComponents()
/*      */   {
/*  517 */     this._logMonitorFrame = new JFrame("LogFactor5");
/*      */ 
/*  519 */     this._logMonitorFrame.setDefaultCloseOperation(0);
/*      */ 
/*  521 */     String resource = "/org/apache/log4j/lf5/viewer/images/lf5_small_icon.gif";
/*      */ 
/*  523 */     URL lf5IconURL = getClass().getResource(resource);
/*      */ 
/*  525 */     if (lf5IconURL != null) {
/*  526 */       this._logMonitorFrame.setIconImage(new ImageIcon(lf5IconURL).getImage());
/*      */     }
/*  528 */     updateFrameSize();
/*      */ 
/*  533 */     JTextArea detailTA = createDetailTextArea();
/*  534 */     JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
/*  535 */     this._table = new LogTable(detailTA);
/*  536 */     setView(this._currentView, this._table);
/*  537 */     this._table.setFont(new Font(this._fontName, 0, this._fontSize));
/*  538 */     this._logTableScrollPane = new JScrollPane(this._table);
/*      */ 
/*  540 */     if (this._trackTableScrollPane) {
/*  541 */       this._logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
/*      */     }
/*      */ 
/*  550 */     JSplitPane tableViewerSplitPane = new JSplitPane();
/*  551 */     tableViewerSplitPane.setOneTouchExpandable(true);
/*  552 */     tableViewerSplitPane.setOrientation(0);
/*  553 */     tableViewerSplitPane.setLeftComponent(this._logTableScrollPane);
/*  554 */     tableViewerSplitPane.setRightComponent(detailTAScrollPane);
/*      */ 
/*  562 */     tableViewerSplitPane.setDividerLocation(350);
/*      */ 
/*  568 */     this._categoryExplorerTree = new CategoryExplorerTree();
/*      */ 
/*  570 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());
/*      */ 
/*  572 */     JScrollPane categoryExplorerTreeScrollPane = new JScrollPane(this._categoryExplorerTree);
/*      */ 
/*  574 */     categoryExplorerTreeScrollPane.setPreferredSize(new Dimension(130, 400));
/*      */ 
/*  577 */     this._mruFileManager = new MRUFileManager();
/*      */ 
/*  583 */     JSplitPane splitPane = new JSplitPane();
/*  584 */     splitPane.setOneTouchExpandable(true);
/*  585 */     splitPane.setRightComponent(tableViewerSplitPane);
/*  586 */     splitPane.setLeftComponent(categoryExplorerTreeScrollPane);
/*      */ 
/*  588 */     splitPane.setDividerLocation(130);
/*      */ 
/*  593 */     this._logMonitorFrame.getRootPane().setJMenuBar(createMenuBar());
/*  594 */     this._logMonitorFrame.getContentPane().add(splitPane, "Center");
/*  595 */     this._logMonitorFrame.getContentPane().add(createToolBar(), "North");
/*      */ 
/*  597 */     this._logMonitorFrame.getContentPane().add(createStatusArea(), "South");
/*      */ 
/*  600 */     makeLogTableListenToCategoryExplorer();
/*  601 */     addTableModelProperties();
/*      */ 
/*  606 */     this._configurationManager = new ConfigurationManager(this, this._table);
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createLogRecordFilter()
/*      */   {
/*  611 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  613 */         CategoryPath path = new CategoryPath(record.getCategory());
/*  614 */         return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected()) && (LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  619 */     return result;
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createNDCLogRecordFilter(String text)
/*      */   {
/*  625 */     this._NDCTextFilter = text;
/*  626 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  628 */         String NDC = record.getNDC();
/*  629 */         CategoryPath path = new CategoryPath(record.getCategory());
/*  630 */         if ((NDC == null) || (LogBrokerMonitor.this._NDCTextFilter == null))
/*  631 */           return false;
/*  632 */         if (NDC.toLowerCase().indexOf(LogBrokerMonitor.this._NDCTextFilter.toLowerCase()) == -1) {
/*  633 */           return false;
/*      */         }
/*  635 */         return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected()) && (LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  641 */     return result;
/*      */   }
/*      */ 
/*      */   protected void updateStatusLabel()
/*      */   {
/*  646 */     this._statusLabel.setText(getRecordsDisplayedMessage());
/*      */   }
/*      */ 
/*      */   protected String getRecordsDisplayedMessage() {
/*  650 */     FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*  651 */     return getStatusText(model.getRowCount(), model.getTotalRowCount());
/*      */   }
/*      */ 
/*      */   protected void addTableModelProperties() {
/*  655 */     final FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*      */ 
/*  657 */     addDisplayedProperty(new Object() {
/*      */       public String toString() {
/*  659 */         return LogBrokerMonitor.this.getRecordsDisplayedMessage();
/*      */       }
/*      */     });
/*  662 */     addDisplayedProperty(new Object() { private final FilteredLogTableModel val$model;
/*      */ 
/*  664 */       public String toString() { return "Maximum number of displayed LogRecords: " + model._maxNumberOfLogRecords; }
/*      */ 
/*      */     });
/*      */   }
/*      */ 
/*      */   protected String getStatusText(int displayedRows, int totalRows)
/*      */   {
/*  671 */     StringBuffer result = new StringBuffer();
/*  672 */     result.append("Displaying: ");
/*  673 */     result.append(displayedRows);
/*  674 */     result.append(" records out of a total of: ");
/*  675 */     result.append(totalRows);
/*  676 */     result.append(" records.");
/*  677 */     return result.toString();
/*      */   }
/*      */ 
/*      */   protected void makeLogTableListenToCategoryExplorer() {
/*  681 */     ActionListener listener = new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  683 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  684 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     };
/*  687 */     this._categoryExplorerTree.getExplorerModel().addActionListener(listener);
/*      */   }
/*      */ 
/*      */   protected JPanel createStatusArea() {
/*  691 */     JPanel statusArea = new JPanel();
/*  692 */     JLabel status = new JLabel("No log records to display.");
/*      */ 
/*  694 */     this._statusLabel = status;
/*  695 */     status.setHorizontalAlignment(2);
/*      */ 
/*  697 */     statusArea.setBorder(BorderFactory.createEtchedBorder());
/*  698 */     statusArea.setLayout(new FlowLayout(0, 0, 0));
/*  699 */     statusArea.add(status);
/*      */ 
/*  701 */     return statusArea;
/*      */   }
/*      */ 
/*      */   protected JTextArea createDetailTextArea() {
/*  705 */     JTextArea detailTA = new JTextArea();
/*  706 */     detailTA.setFont(new Font("Monospaced", 0, 14));
/*  707 */     detailTA.setTabSize(3);
/*  708 */     detailTA.setLineWrap(true);
/*  709 */     detailTA.setWrapStyleWord(false);
/*  710 */     return detailTA;
/*      */   }
/*      */ 
/*      */   protected JMenuBar createMenuBar() {
/*  714 */     JMenuBar menuBar = new JMenuBar();
/*  715 */     menuBar.add(createFileMenu());
/*  716 */     menuBar.add(createEditMenu());
/*  717 */     menuBar.add(createLogLevelMenu());
/*  718 */     menuBar.add(createViewMenu());
/*  719 */     menuBar.add(createConfigureMenu());
/*  720 */     menuBar.add(createHelpMenu());
/*      */ 
/*  722 */     return menuBar;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelMenu() {
/*  726 */     JMenu result = new JMenu("Log Level");
/*  727 */     result.setMnemonic('l');
/*  728 */     Iterator levels = getLogLevels();
/*  729 */     while (levels.hasNext()) {
/*  730 */       result.add(getMenuItem((LogLevel)levels.next()));
/*      */     }
/*      */ 
/*  733 */     result.addSeparator();
/*  734 */     result.add(createAllLogLevelsMenuItem());
/*  735 */     result.add(createNoLogLevelsMenuItem());
/*  736 */     result.addSeparator();
/*  737 */     result.add(createLogLevelColorMenu());
/*  738 */     result.add(createResetLogLevelColorMenuItem());
/*      */ 
/*  740 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogLevelsMenuItem() {
/*  744 */     JMenuItem result = new JMenuItem("Show all LogLevels");
/*  745 */     result.setMnemonic('s');
/*  746 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  748 */         LogBrokerMonitor.this.selectAllLogLevels(true);
/*  749 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  750 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  753 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogLevelsMenuItem() {
/*  757 */     JMenuItem result = new JMenuItem("Hide all LogLevels");
/*  758 */     result.setMnemonic('h');
/*  759 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  761 */         LogBrokerMonitor.this.selectAllLogLevels(false);
/*  762 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  763 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  766 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelColorMenu() {
/*  770 */     JMenu colorMenu = new JMenu("Configure LogLevel Colors");
/*  771 */     colorMenu.setMnemonic('c');
/*  772 */     Iterator levels = getLogLevels();
/*  773 */     while (levels.hasNext()) {
/*  774 */       colorMenu.add(createSubMenuItem((LogLevel)levels.next()));
/*      */     }
/*      */ 
/*  777 */     return colorMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createResetLogLevelColorMenuItem() {
/*  781 */     JMenuItem result = new JMenuItem("Reset LogLevel Colors");
/*  782 */     result.setMnemonic('r');
/*  783 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  786 */         LogLevel.resetLogLevelColorMap();
/*      */ 
/*  789 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*      */       }
/*      */     });
/*  792 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogLevels(boolean selected) {
/*  796 */     Iterator levels = getLogLevels();
/*  797 */     while (levels.hasNext())
/*  798 */       getMenuItem((LogLevel)levels.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getMenuItem(LogLevel level)
/*      */   {
/*  803 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logLevelMenuItems.get(level);
/*  804 */     if (result == null) {
/*  805 */       result = createMenuItem(level);
/*  806 */       this._logLevelMenuItems.put(level, result);
/*      */     }
/*  808 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSubMenuItem(LogLevel level) {
/*  812 */     final JMenuItem result = new JMenuItem(level.toString());
/*  813 */     final LogLevel logLevel = level;
/*  814 */     result.setMnemonic(level.toString().charAt(0));
/*  815 */     result.addActionListener(new ActionListener() { private final JMenuItem val$result;
/*      */       private final LogLevel val$logLevel;
/*      */ 
/*  817 */       public void actionPerformed(ActionEvent e) { LogBrokerMonitor.this.showLogLevelColorChangeDialog(result, logLevel); }
/*      */ 
/*      */     });
/*  821 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showLogLevelColorChangeDialog(JMenuItem result, LogLevel level)
/*      */   {
/*  826 */     JMenuItem menuItem = result;
/*  827 */     Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose LogLevel Color", result.getForeground());
/*      */ 
/*  832 */     if (newColor != null)
/*      */     {
/*  834 */       level.setLogLevelColorMap(level, newColor);
/*  835 */       this._table.getFilteredLogTableModel().refresh();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createMenuItem(LogLevel level)
/*      */   {
/*  841 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
/*  842 */     result.setSelected(true);
/*  843 */     result.setMnemonic(level.toString().charAt(0));
/*  844 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  846 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  847 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  850 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createViewMenu()
/*      */   {
/*  855 */     JMenu result = new JMenu("View");
/*  856 */     result.setMnemonic('v');
/*  857 */     Iterator columns = getLogTableColumns();
/*  858 */     while (columns.hasNext()) {
/*  859 */       result.add(getLogTableColumnMenuItem((LogTableColumn)columns.next()));
/*      */     }
/*      */ 
/*  862 */     result.addSeparator();
/*  863 */     result.add(createAllLogTableColumnsMenuItem());
/*  864 */     result.add(createNoLogTableColumnsMenuItem());
/*  865 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
/*  869 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logTableColumnMenuItems.get(column);
/*  870 */     if (result == null) {
/*  871 */       result = createLogTableColumnMenuItem(column);
/*  872 */       this._logTableColumnMenuItems.put(column, result);
/*      */     }
/*  874 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
/*  878 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());
/*      */ 
/*  880 */     result.setSelected(true);
/*  881 */     result.setMnemonic(column.toString().charAt(0));
/*  882 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  885 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  886 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  889 */     return result;
/*      */   }
/*      */ 
/*      */   protected List updateView() {
/*  893 */     ArrayList updatedList = new ArrayList();
/*  894 */     Iterator columnIterator = this._columns.iterator();
/*  895 */     while (columnIterator.hasNext()) {
/*  896 */       LogTableColumn column = (LogTableColumn)columnIterator.next();
/*  897 */       JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);
/*      */ 
/*  899 */       if (result.isSelected()) {
/*  900 */         updatedList.add(column);
/*      */       }
/*      */     }
/*      */ 
/*  904 */     return updatedList;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogTableColumnsMenuItem() {
/*  908 */     JMenuItem result = new JMenuItem("Show all Columns");
/*  909 */     result.setMnemonic('s');
/*  910 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  912 */         LogBrokerMonitor.this.selectAllLogTableColumns(true);
/*      */ 
/*  914 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  915 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  918 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogTableColumnsMenuItem() {
/*  922 */     JMenuItem result = new JMenuItem("Hide all Columns");
/*  923 */     result.setMnemonic('h');
/*  924 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  926 */         LogBrokerMonitor.this.selectAllLogTableColumns(false);
/*      */ 
/*  928 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  929 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  932 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogTableColumns(boolean selected) {
/*  936 */     Iterator columns = getLogTableColumns();
/*  937 */     while (columns.hasNext())
/*  938 */       getLogTableColumnMenuItem((LogTableColumn)columns.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JMenu createFileMenu()
/*      */   {
/*  943 */     JMenu fileMenu = new JMenu("File");
/*  944 */     fileMenu.setMnemonic('f');
/*      */ 
/*  946 */     fileMenu.add(createOpenMI());
/*  947 */     fileMenu.add(createOpenURLMI());
/*  948 */     fileMenu.addSeparator();
/*  949 */     fileMenu.add(createCloseMI());
/*  950 */     createMRUFileListMI(fileMenu);
/*  951 */     fileMenu.addSeparator();
/*  952 */     fileMenu.add(createExitMI());
/*  953 */     return fileMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenMI()
/*      */   {
/*  961 */     JMenuItem result = new JMenuItem("Open...");
/*  962 */     result.setMnemonic('o');
/*  963 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  965 */         LogBrokerMonitor.this.requestOpen();
/*      */       }
/*      */     });
/*  968 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenURLMI()
/*      */   {
/*  976 */     JMenuItem result = new JMenuItem("Open URL...");
/*  977 */     result.setMnemonic('u');
/*  978 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  980 */         LogBrokerMonitor.this.requestOpenURL();
/*      */       }
/*      */     });
/*  983 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createCloseMI() {
/*  987 */     JMenuItem result = new JMenuItem("Close");
/*  988 */     result.setMnemonic('c');
/*  989 */     result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
/*  990 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  992 */         LogBrokerMonitor.this.requestClose();
/*      */       }
/*      */     });
/*  995 */     return result;
/*      */   }
/*      */ 
/*      */   protected void createMRUFileListMI(JMenu menu)
/*      */   {
/* 1004 */     String[] files = this._mruFileManager.getMRUFileList();
/*      */ 
/* 1006 */     if (files != null) {
/* 1007 */       menu.addSeparator();
/* 1008 */       for (int i = 0; i < files.length; i++) {
/* 1009 */         JMenuItem result = new JMenuItem(i + 1 + " " + files[i]);
/* 1010 */         result.setMnemonic(i + 1);
/* 1011 */         result.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1013 */             LogBrokerMonitor.this.requestOpenMRU(e);
/*      */           }
/*      */         });
/* 1016 */         menu.add(result);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JMenuItem createExitMI() {
/* 1022 */     JMenuItem result = new JMenuItem("Exit");
/* 1023 */     result.setMnemonic('x');
/* 1024 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1026 */         LogBrokerMonitor.this.requestExit();
/*      */       }
/*      */     });
/* 1029 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createConfigureMenu() {
/* 1033 */     JMenu configureMenu = new JMenu("Configure");
/* 1034 */     configureMenu.setMnemonic('c');
/* 1035 */     configureMenu.add(createConfigureSave());
/* 1036 */     configureMenu.add(createConfigureReset());
/* 1037 */     configureMenu.add(createConfigureMaxRecords());
/*      */ 
/* 1039 */     return configureMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureSave() {
/* 1043 */     JMenuItem result = new JMenuItem("Save");
/* 1044 */     result.setMnemonic('s');
/* 1045 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1047 */         LogBrokerMonitor.this.saveConfiguration();
/*      */       }
/*      */     });
/* 1051 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureReset() {
/* 1055 */     JMenuItem result = new JMenuItem("Reset");
/* 1056 */     result.setMnemonic('r');
/* 1057 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1059 */         LogBrokerMonitor.this.resetConfiguration();
/*      */       }
/*      */     });
/* 1063 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureMaxRecords() {
/* 1067 */     JMenuItem result = new JMenuItem("Set Max Number of Records");
/* 1068 */     result.setMnemonic('m');
/* 1069 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1071 */         LogBrokerMonitor.this.setMaxRecordConfiguration();
/*      */       }
/*      */     });
/* 1075 */     return result;
/*      */   }
/*      */ 
/*      */   protected void saveConfiguration()
/*      */   {
/* 1080 */     this._configurationManager.save();
/*      */   }
/*      */ 
/*      */   protected void resetConfiguration() {
/* 1084 */     this._configurationManager.reset();
/*      */   }
/*      */ 
/*      */   protected void setMaxRecordConfiguration() {
/* 1088 */     LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);
/*      */ 
/* 1091 */     String temp = inputDialog.getText();
/*      */ 
/* 1093 */     if (temp != null)
/*      */       try {
/* 1095 */         setMaxNumberOfLogRecords(Integer.parseInt(temp));
/*      */       } catch (NumberFormatException e) {
/* 1097 */         LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");
/*      */ 
/* 1100 */         setMaxRecordConfiguration();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected JMenu createHelpMenu()
/*      */   {
/* 1107 */     JMenu helpMenu = new JMenu("Help");
/* 1108 */     helpMenu.setMnemonic('h');
/* 1109 */     helpMenu.add(createHelpProperties());
/* 1110 */     return helpMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpProperties() {
/* 1114 */     String title = "LogFactor5 Properties";
/* 1115 */     JMenuItem result = new JMenuItem("LogFactor5 Properties");
/* 1116 */     result.setMnemonic('l');
/* 1117 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1119 */         LogBrokerMonitor.this.showPropertiesDialog("LogFactor5 Properties");
/*      */       }
/*      */     });
/* 1122 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showPropertiesDialog(String title) {
/* 1126 */     JOptionPane.showMessageDialog(this._logMonitorFrame, this._displayedLogBrokerProperties.toArray(), title, -1);
/*      */   }
/*      */ 
/*      */   protected JMenu createEditMenu()
/*      */   {
/* 1135 */     JMenu editMenu = new JMenu("Edit");
/* 1136 */     editMenu.setMnemonic('e');
/* 1137 */     editMenu.add(createEditFindMI());
/* 1138 */     editMenu.add(createEditFindNextMI());
/* 1139 */     editMenu.addSeparator();
/* 1140 */     editMenu.add(createEditSortNDCMI());
/* 1141 */     editMenu.add(createEditRestoreAllNDCMI());
/* 1142 */     return editMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindNextMI() {
/* 1146 */     JMenuItem editFindNextMI = new JMenuItem("Find Next");
/* 1147 */     editFindNextMI.setMnemonic('n');
/* 1148 */     editFindNextMI.setAccelerator(KeyStroke.getKeyStroke("F3"));
/* 1149 */     editFindNextMI.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1151 */         LogBrokerMonitor.this.findSearchText();
/*      */       }
/*      */     });
/* 1154 */     return editFindNextMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindMI() {
/* 1158 */     JMenuItem editFindMI = new JMenuItem("Find");
/* 1159 */     editFindMI.setMnemonic('f');
/* 1160 */     editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));
/*      */ 
/* 1162 */     editFindMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1165 */         String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Find text: ", "Search Record Messages", 3);
/*      */ 
/* 1172 */         LogBrokerMonitor.this.setSearchText(inputValue);
/* 1173 */         LogBrokerMonitor.this.findSearchText();
/*      */       }
/*      */     });
/* 1178 */     return editFindMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditSortNDCMI()
/*      */   {
/* 1185 */     JMenuItem editSortNDCMI = new JMenuItem("Sort by NDC");
/* 1186 */     editSortNDCMI.setMnemonic('s');
/* 1187 */     editSortNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1190 */         String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Sort by this NDC: ", "Sort Log Records by NDC", 3);
/*      */ 
/* 1197 */         LogBrokerMonitor.this.setNDCTextFilter(inputValue);
/* 1198 */         LogBrokerMonitor.this.sortByNDC();
/* 1199 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/* 1200 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1205 */     return editSortNDCMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditRestoreAllNDCMI()
/*      */   {
/* 1211 */     JMenuItem editRestoreAllNDCMI = new JMenuItem("Restore all NDCs");
/* 1212 */     editRestoreAllNDCMI.setMnemonic('r');
/* 1213 */     editRestoreAllNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1216 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().setLogRecordFilter(LogBrokerMonitor.this.createLogRecordFilter());
/*      */ 
/* 1218 */         LogBrokerMonitor.this.setNDCTextFilter("");
/* 1219 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/* 1220 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1224 */     return editRestoreAllNDCMI;
/*      */   }
/*      */ 
/*      */   protected JToolBar createToolBar() {
/* 1228 */     JToolBar tb = new JToolBar();
/* 1229 */     tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/* 1230 */     JComboBox fontCombo = new JComboBox();
/* 1231 */     JComboBox fontSizeCombo = new JComboBox();
/* 1232 */     this._fontSizeCombo = fontSizeCombo;
/*      */ 
/* 1234 */     ClassLoader cl = getClass().getClassLoader();
/* 1235 */     if (cl == null) {
/* 1236 */       cl = ClassLoader.getSystemClassLoader();
/*      */     }
/* 1238 */     URL newIconURL = cl.getResource("org/apache/log4j/lf5/viewer/images/channelexplorer_new.gif");
/*      */ 
/* 1241 */     ImageIcon newIcon = null;
/*      */ 
/* 1243 */     if (newIconURL != null) {
/* 1244 */       newIcon = new ImageIcon(newIconURL);
/*      */     }
/*      */ 
/* 1247 */     JButton newButton = new JButton("Clear Log Table");
/*      */ 
/* 1249 */     if (newIcon != null) {
/* 1250 */       newButton.setIcon(newIcon);
/*      */     }
/*      */ 
/* 1253 */     newButton.setToolTipText("Clear Log Table.");
/*      */ 
/* 1256 */     newButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1259 */         LogBrokerMonitor.this._table.clearLogRecords();
/* 1260 */         LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().resetAllNodeCounts();
/* 1261 */         LogBrokerMonitor.this.updateStatusLabel();
/* 1262 */         LogBrokerMonitor.this.clearDetailTextArea();
/* 1263 */         LogRecord.resetSequenceNumber();
/*      */       }
/*      */     });
/* 1268 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*      */     String[] fonts;
/*      */     String[] fonts;
/* 1273 */     if (this._loadSystemFonts) {
/* 1274 */       fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
/*      */     }
/*      */     else {
/* 1277 */       fonts = tk.getFontList();
/*      */     }
/*      */ 
/* 1280 */     for (int j = 0; j < fonts.length; j++) {
/* 1281 */       fontCombo.addItem(fonts[j]);
/*      */     }
/*      */ 
/* 1284 */     fontCombo.setSelectedItem(this._fontName);
/*      */ 
/* 1286 */     fontCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1290 */         JComboBox box = (JComboBox)e.getSource();
/* 1291 */         String font = (String)box.getSelectedItem();
/* 1292 */         LogBrokerMonitor.this._table.setFont(new Font(font, 0, LogBrokerMonitor.this._fontSize));
/* 1293 */         LogBrokerMonitor.this._fontName = font;
/*      */       }
/*      */     });
/* 1298 */     fontSizeCombo.addItem("8");
/* 1299 */     fontSizeCombo.addItem("9");
/* 1300 */     fontSizeCombo.addItem("10");
/* 1301 */     fontSizeCombo.addItem("12");
/* 1302 */     fontSizeCombo.addItem("14");
/* 1303 */     fontSizeCombo.addItem("16");
/* 1304 */     fontSizeCombo.addItem("18");
/* 1305 */     fontSizeCombo.addItem("24");
/*      */ 
/* 1307 */     fontSizeCombo.setSelectedItem(String.valueOf(this._fontSize));
/* 1308 */     fontSizeCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1311 */         JComboBox box = (JComboBox)e.getSource();
/* 1312 */         String size = (String)box.getSelectedItem();
/* 1313 */         int s = Integer.valueOf(size).intValue();
/*      */ 
/* 1315 */         LogBrokerMonitor.this.setFontSizeSilently(s);
/* 1316 */         LogBrokerMonitor.this.refreshDetailTextArea();
/* 1317 */         LogBrokerMonitor.this._fontSize = s;
/*      */       }
/*      */     });
/* 1322 */     tb.add(new JLabel(" Font: "));
/* 1323 */     tb.add(fontCombo);
/* 1324 */     tb.add(fontSizeCombo);
/* 1325 */     tb.addSeparator();
/* 1326 */     tb.addSeparator();
/* 1327 */     tb.add(newButton);
/*      */ 
/* 1329 */     newButton.setAlignmentY(0.5F);
/* 1330 */     newButton.setAlignmentX(0.5F);
/*      */ 
/* 1332 */     fontCombo.setMaximumSize(fontCombo.getPreferredSize());
/* 1333 */     fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());
/*      */ 
/* 1336 */     return tb;
/*      */   }
/*      */ 
/*      */   protected void setView(String viewString, LogTable table)
/*      */   {
/* 1354 */     if ("Detailed".equals(viewString)) {
/* 1355 */       table.setDetailedView();
/*      */     } else {
/* 1357 */       String message = viewString + "does not match a supported view.";
/* 1358 */       throw new IllegalArgumentException(message);
/*      */     }
/* 1360 */     this._currentView = viewString;
/*      */   }
/*      */ 
/*      */   protected JComboBox createLogLevelCombo() {
/* 1364 */     JComboBox result = new JComboBox();
/* 1365 */     Iterator levels = getLogLevels();
/* 1366 */     while (levels.hasNext()) {
/* 1367 */       result.addItem(levels.next());
/*      */     }
/* 1369 */     result.setSelectedItem(this._leastSevereDisplayedLogLevel);
/*      */ 
/* 1371 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1373 */         JComboBox box = (JComboBox)e.getSource();
/* 1374 */         LogLevel level = (LogLevel)box.getSelectedItem();
/* 1375 */         LogBrokerMonitor.this.setLeastSevereDisplayedLogLevel(level);
/*      */       }
/*      */     });
/* 1378 */     result.setMaximumSize(result.getPreferredSize());
/* 1379 */     return result;
/*      */   }
/*      */ 
/*      */   protected void setLeastSevereDisplayedLogLevel(LogLevel level) {
/* 1383 */     if ((level == null) || (this._leastSevereDisplayedLogLevel == level)) {
/* 1384 */       return;
/*      */     }
/* 1386 */     this._leastSevereDisplayedLogLevel = level;
/* 1387 */     this._table.getFilteredLogTableModel().refresh();
/* 1388 */     updateStatusLabel();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   protected void trackTableScrollPane()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void centerFrame(JFrame frame)
/*      */   {
/* 1406 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/* 1407 */     Dimension comp = frame.getSize();
/*      */ 
/* 1409 */     frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
/*      */   }
/*      */ 
/*      */   protected void requestOpen()
/*      */   {
/*      */     JFileChooser chooser;
/*      */     JFileChooser chooser;
/* 1421 */     if (this._fileLocation == null)
/* 1422 */       chooser = new JFileChooser();
/*      */     else {
/* 1424 */       chooser = new JFileChooser(this._fileLocation);
/*      */     }
/*      */ 
/* 1427 */     int returnVal = chooser.showOpenDialog(this._logMonitorFrame);
/* 1428 */     if (returnVal == 0) {
/* 1429 */       File f = chooser.getSelectedFile();
/* 1430 */       if (loadLogFile(f)) {
/* 1431 */         this._fileLocation = chooser.getSelectedFile();
/* 1432 */         this._mruFileManager.set(f);
/* 1433 */         updateMRUList();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestOpenURL()
/*      */   {
/* 1443 */     LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Open URL", "URL:");
/*      */ 
/* 1445 */     String temp = inputDialog.getText();
/*      */     LogFactor5ErrorDialog error;
/* 1447 */     if (temp != null) {
/* 1448 */       if (temp.indexOf("://") == -1) {
/* 1449 */         temp = "http://" + temp;
/*      */       }
/*      */       try
/*      */       {
/* 1453 */         URL url = new URL(temp);
/* 1454 */         if (loadLogFile(url)) {
/* 1455 */           this._mruFileManager.set(url);
/* 1456 */           updateMRUList();
/*      */         }
/*      */       } catch (MalformedURLException e) {
/* 1459 */         error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL.");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateMRUList()
/*      */   {
/* 1470 */     JMenu menu = this._logMonitorFrame.getJMenuBar().getMenu(0);
/* 1471 */     menu.removeAll();
/* 1472 */     menu.add(createOpenMI());
/* 1473 */     menu.add(createOpenURLMI());
/* 1474 */     menu.addSeparator();
/* 1475 */     menu.add(createCloseMI());
/* 1476 */     createMRUFileListMI(menu);
/* 1477 */     menu.addSeparator();
/* 1478 */     menu.add(createExitMI());
/*      */   }
/*      */ 
/*      */   protected void requestClose() {
/* 1482 */     setCallSystemExitOnClose(false);
/* 1483 */     closeAfterConfirm();
/*      */   }
/*      */   protected void requestOpenMRU(ActionEvent e) {
/* 1490 */     String file = e.getActionCommand();
/* 1491 */     StringTokenizer st = new StringTokenizer(file);
/* 1492 */     String num = st.nextToken().trim();
/* 1493 */     file = st.nextToken("\n");
/*      */     LogFactor5ErrorDialog error;
/*      */     try {
/* 1496 */       int index = Integer.parseInt(num) - 1;
/*      */ 
/* 1498 */       InputStream in = this._mruFileManager.getInputStream(index);
/* 1499 */       LogFileParser lfp = new LogFileParser(in);
/* 1500 */       lfp.parse(this);
/*      */ 
/* 1502 */       this._mruFileManager.moveToTop(index);
/* 1503 */       updateMRUList();
/*      */     }
/*      */     catch (Exception me) {
/* 1506 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Unable to load file " + file);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestExit()
/*      */   {
/* 1513 */     this._mruFileManager.save();
/* 1514 */     setCallSystemExitOnClose(true);
/* 1515 */     closeAfterConfirm();
/*      */   }
/*      */ 
/*      */   protected void closeAfterConfirm() {
/* 1519 */     StringBuffer message = new StringBuffer();
/*      */ 
/* 1521 */     if (!this._callSystemExitOnClose) {
/* 1522 */       message.append("Are you sure you want to close the logging ");
/* 1523 */       message.append("console?\n");
/* 1524 */       message.append("(Note: This will not shut down the Virtual Machine,\n");
/* 1525 */       message.append("or the Swing event thread.)");
/*      */     } else {
/* 1527 */       message.append("Are you sure you want to exit?\n");
/* 1528 */       message.append("This will shut down the Virtual Machine.\n");
/*      */     }
/*      */ 
/* 1531 */     String title = "Are you sure you want to dispose of the Logging Console?";
/*      */ 
/* 1534 */     if (this._callSystemExitOnClose == true) {
/* 1535 */       title = "Are you sure you want to exit?";
/*      */     }
/* 1537 */     int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(), title, 2, 3, null);
/*      */ 
/* 1546 */     if (value == 0)
/* 1547 */       dispose();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogLevels()
/*      */   {
/* 1552 */     return this._levels.iterator();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogTableColumns() {
/* 1556 */     return this._columns.iterator();
/*      */   }
/*      */ 
/*      */   protected boolean loadLogFile(File file)
/*      */   {
/* 1563 */     boolean ok = false;
/*      */     LogFactor5ErrorDialog error;
/*      */     try {
/* 1565 */       LogFileParser lfp = new LogFileParser(file);
/* 1566 */       lfp.parse(this);
/* 1567 */       ok = true;
/*      */     } catch (IOException e) {
/* 1569 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading " + file.getName());
/*      */     }
/*      */ 
/* 1573 */     return ok;
/*      */   }
/*      */ 
/*      */   protected boolean loadLogFile(URL url)
/*      */   {
/* 1580 */     boolean ok = false;
/*      */     LogFactor5ErrorDialog error;
/*      */     try {
/* 1582 */       LogFileParser lfp = new LogFileParser(url.openStream());
/* 1583 */       lfp.parse(this);
/* 1584 */       ok = true;
/*      */     } catch (IOException e) {
/* 1586 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL:" + url.getFile());
/*      */     }
/*      */ 
/* 1589 */     return ok;
/*      */   }
/*      */ 
/*      */   class LogBrokerMonitorWindowAdaptor extends WindowAdapter
/*      */   {
/*      */     protected LogBrokerMonitor _monitor;
/*      */ 
/*      */     public LogBrokerMonitorWindowAdaptor(LogBrokerMonitor monitor)
/*      */     {
/* 1603 */       this._monitor = monitor;
/*      */     }
/*      */ 
/*      */     public void windowClosing(WindowEvent ev) {
/* 1607 */       this._monitor.requestClose();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogBrokerMonitor
 * JD-Core Version:    0.6.2
 */