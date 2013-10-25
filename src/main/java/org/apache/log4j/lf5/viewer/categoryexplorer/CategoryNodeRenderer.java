/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.net.URL;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ 
/*     */ public class CategoryNodeRenderer extends DefaultTreeCellRenderer
/*     */ {
/*     */   private static final long serialVersionUID = -6046702673278595048L;
/*  40 */   public static final Color FATAL_CHILDREN = new Color(189, 113, 0);
/*     */ 
/*  45 */   protected JCheckBox _checkBox = new JCheckBox();
/*  46 */   protected JPanel _panel = new JPanel();
/*  47 */   protected static ImageIcon _sat = null;
/*     */ 
/*     */   public CategoryNodeRenderer()
/*     */   {
/*  58 */     this._panel.setBackground(UIManager.getColor("Tree.textBackground"));
/*     */ 
/*  60 */     if (_sat == null)
/*     */     {
/*  62 */       String resource = "/org/apache/log4j/lf5/viewer/images/channelexplorer_satellite.gif";
/*     */ 
/*  64 */       URL satURL = getClass().getResource(resource);
/*     */ 
/*  66 */       _sat = new ImageIcon(satURL);
/*     */     }
/*     */ 
/*  69 */     setOpaque(false);
/*  70 */     this._checkBox.setOpaque(false);
/*  71 */     this._panel.setOpaque(false);
/*     */ 
/*  75 */     this._panel.setLayout(new FlowLayout(0, 0, 0));
/*  76 */     this._panel.add(this._checkBox);
/*  77 */     this._panel.add(this);
/*     */ 
/*  79 */     setOpenIcon(_sat);
/*  80 */     setClosedIcon(_sat);
/*  81 */     setLeafIcon(_sat);
/*     */   }
/*     */ 
/*     */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */   {
/*  93 */     CategoryNode node = (CategoryNode)value;
/*     */ 
/*  98 */     super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
/*     */ 
/* 102 */     if (row == 0)
/*     */     {
/* 104 */       this._checkBox.setVisible(false);
/*     */     } else {
/* 106 */       this._checkBox.setVisible(true);
/* 107 */       this._checkBox.setSelected(node.isSelected());
/*     */     }
/* 109 */     String toolTip = buildToolTip(node);
/* 110 */     this._panel.setToolTipText(toolTip);
/* 111 */     if (node.hasFatalChildren()) {
/* 112 */       setForeground(FATAL_CHILDREN);
/*     */     }
/* 114 */     if (node.hasFatalRecords()) {
/* 115 */       setForeground(Color.red);
/*     */     }
/*     */ 
/* 118 */     return this._panel;
/*     */   }
/*     */ 
/*     */   public Dimension getCheckBoxOffset() {
/* 122 */     return new Dimension(0, 0);
/*     */   }
/*     */ 
/*     */   protected String buildToolTip(CategoryNode node)
/*     */   {
/* 130 */     StringBuffer result = new StringBuffer();
/* 131 */     result.append(node.getTitle()).append(" contains a total of ");
/* 132 */     result.append(node.getTotalNumberOfRecords());
/* 133 */     result.append(" LogRecords.");
/* 134 */     result.append(" Right-click for more info.");
/* 135 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeRenderer
 * JD-Core Version:    0.6.2
 */