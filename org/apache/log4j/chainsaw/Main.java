/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTable;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ 
/*     */ public class Main extends JFrame
/*     */ {
/*     */   private static final int DEFAULT_PORT = 4445;
/*     */   public static final String PORT_PROP_NAME = "chainsaw.port";
/*  54 */   private static final Logger LOG = Logger.getLogger(Main.class);
/*     */ 
/*     */   private Main()
/*     */   {
/*  61 */     super("CHAINSAW - Log4J Log Viewer");
/*     */ 
/*  63 */     MyTableModel model = new MyTableModel();
/*     */ 
/*  66 */     JMenuBar menuBar = new JMenuBar();
/*  67 */     setJMenuBar(menuBar);
/*  68 */     JMenu menu = new JMenu("File");
/*  69 */     menuBar.add(menu);
/*     */     try
/*     */     {
/*  72 */       LoadXMLAction lxa = new LoadXMLAction(this, model);
/*  73 */       JMenuItem loadMenuItem = new JMenuItem("Load file...");
/*  74 */       menu.add(loadMenuItem);
/*  75 */       loadMenuItem.addActionListener(lxa);
/*     */     } catch (NoClassDefFoundError e) {
/*  77 */       LOG.info("Missing classes for XML parser", e);
/*  78 */       JOptionPane.showMessageDialog(this, "XML parser not in classpath - unable to load XML events.", "CHAINSAW", 0);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  84 */       LOG.info("Unable to create the action to load XML files", e);
/*  85 */       JOptionPane.showMessageDialog(this, "Unable to create a XML parser - unable to load XML events.", "CHAINSAW", 0);
/*     */     }
/*     */ 
/*  92 */     JMenuItem exitMenuItem = new JMenuItem("Exit");
/*  93 */     menu.add(exitMenuItem);
/*  94 */     exitMenuItem.addActionListener(ExitAction.INSTANCE);
/*     */ 
/*  97 */     ControlPanel cp = new ControlPanel(model);
/*  98 */     getContentPane().add(cp, "North");
/*     */ 
/* 101 */     JTable table = new JTable(model);
/* 102 */     table.setSelectionMode(0);
/* 103 */     JScrollPane scrollPane = new JScrollPane(table);
/* 104 */     scrollPane.setBorder(BorderFactory.createTitledBorder("Events: "));
/* 105 */     scrollPane.setPreferredSize(new Dimension(900, 300));
/*     */ 
/* 108 */     JPanel details = new DetailPanel(table, model);
/* 109 */     details.setPreferredSize(new Dimension(900, 300));
/*     */ 
/* 112 */     JSplitPane jsp = new JSplitPane(0, scrollPane, details);
/*     */ 
/* 114 */     getContentPane().add(jsp, "Center");
/*     */ 
/* 116 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent aEvent) {
/* 118 */         ExitAction.INSTANCE.actionPerformed(null);
/*     */       }
/*     */     });
/* 122 */     pack();
/* 123 */     setVisible(true);
/*     */ 
/* 125 */     setupReceiver(model);
/*     */   }
/*     */ 
/*     */   private void setupReceiver(MyTableModel aModel)
/*     */   {
/* 134 */     int port = 4445;
/* 135 */     String strRep = System.getProperty("chainsaw.port");
/* 136 */     if (strRep != null) {
/*     */       try {
/* 138 */         port = Integer.parseInt(strRep);
/*     */       } catch (NumberFormatException nfe) {
/* 140 */         LOG.fatal("Unable to parse chainsaw.port property with value " + strRep + ".");
/*     */ 
/* 142 */         JOptionPane.showMessageDialog(this, "Unable to parse port number from '" + strRep + "', quitting.", "CHAINSAW", 0);
/*     */ 
/* 148 */         System.exit(1);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 153 */       LoggingReceiver lr = new LoggingReceiver(aModel, port);
/* 154 */       lr.start();
/*     */     } catch (IOException e) {
/* 156 */       LOG.fatal("Unable to connect to socket server, quiting", e);
/* 157 */       JOptionPane.showMessageDialog(this, "Unable to create socket on port " + port + ", quitting.", "CHAINSAW", 0);
/*     */ 
/* 162 */       System.exit(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initLog4J()
/*     */   {
/* 174 */     Properties props = new Properties();
/* 175 */     props.setProperty("log4j.rootLogger", "DEBUG, A1");
/* 176 */     props.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
/*     */ 
/* 178 */     props.setProperty("log4j.appender.A1.layout", "org.apache.log4j.TTCCLayout");
/*     */ 
/* 180 */     PropertyConfigurator.configure(props);
/*     */   }
/*     */ 
/*     */   public static void main(String[] aArgs)
/*     */   {
/* 189 */     initLog4J();
/* 190 */     new Main();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.chainsaw.Main
 * JD-Core Version:    0.6.2
 */