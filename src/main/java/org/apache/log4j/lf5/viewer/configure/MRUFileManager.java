/*     */ package org.apache.log4j.lf5.viewer.configure;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class MRUFileManager
/*     */ {
/*     */   private static final String CONFIG_FILE_NAME = "mru_file_manager";
/*     */   private static final int DEFAULT_MAX_SIZE = 3;
/*  57 */   private int _maxSize = 0;
/*     */   private LinkedList _mruFileList;
/*     */ 
/*     */   public MRUFileManager()
/*     */   {
/*  64 */     load();
/*  65 */     setMaxSize(3);
/*     */   }
/*     */ 
/*     */   public MRUFileManager(int maxSize) {
/*  69 */     load();
/*  70 */     setMaxSize(maxSize);
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*  80 */     File file = new File(getFilename());
/*     */     try
/*     */     {
/*  83 */       ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
/*     */ 
/*  85 */       oos.writeObject(this._mruFileList);
/*  86 */       oos.flush();
/*  87 */       oos.close();
/*     */     }
/*     */     catch (Exception e) {
/*  90 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  98 */     return this._mruFileList.size();
/*     */   }
/*     */ 
/*     */   public Object getFile(int index)
/*     */   {
/* 106 */     if (index < size()) {
/* 107 */       return this._mruFileList.get(index);
/*     */     }
/*     */ 
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream(int index)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 118 */     if (index < size()) {
/* 119 */       Object o = getFile(index);
/* 120 */       if ((o instanceof File)) {
/* 121 */         return getInputStream((File)o);
/*     */       }
/* 123 */       return getInputStream((URL)o);
/*     */     }
/*     */ 
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public void set(File file)
/*     */   {
/* 133 */     setMRU(file);
/*     */   }
/*     */ 
/*     */   public void set(URL url)
/*     */   {
/* 140 */     setMRU(url);
/*     */   }
/*     */ 
/*     */   public String[] getMRUFileList()
/*     */   {
/* 147 */     if (size() == 0) {
/* 148 */       return null;
/*     */     }
/*     */ 
/* 151 */     String[] ss = new String[size()];
/*     */ 
/* 153 */     for (int i = 0; i < size(); i++) {
/* 154 */       Object o = getFile(i);
/* 155 */       if ((o instanceof File)) {
/* 156 */         ss[i] = ((File)o).getAbsolutePath();
/*     */       }
/*     */       else {
/* 159 */         ss[i] = o.toString();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 164 */     return ss;
/*     */   }
/*     */ 
/*     */   public void moveToTop(int index)
/*     */   {
/* 173 */     this._mruFileList.add(0, this._mruFileList.remove(index));
/*     */   }
/*     */ 
/*     */   public static void createConfigurationDirectory()
/*     */   {
/* 183 */     String home = System.getProperty("user.home");
/* 184 */     String sep = System.getProperty("file.separator");
/* 185 */     File f = new File(home + sep + "lf5");
/* 186 */     if (!f.exists())
/*     */       try {
/* 188 */         f.mkdir();
/*     */       } catch (SecurityException e) {
/* 190 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 206 */     BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
/*     */ 
/* 209 */     return reader;
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(URL url)
/*     */     throws IOException
/*     */   {
/* 219 */     return url.openStream();
/*     */   }
/*     */ 
/*     */   protected void setMRU(Object o)
/*     */   {
/* 226 */     int index = this._mruFileList.indexOf(o);
/*     */ 
/* 228 */     if (index == -1) {
/* 229 */       this._mruFileList.add(0, o);
/* 230 */       setMaxSize(this._maxSize);
/*     */     } else {
/* 232 */       moveToTop(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 241 */     createConfigurationDirectory();
/* 242 */     File file = new File(getFilename());
/* 243 */     if (file.exists())
/*     */       try {
/* 245 */         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
/*     */ 
/* 247 */         this._mruFileList = ((LinkedList)ois.readObject());
/* 248 */         ois.close();
/*     */ 
/* 251 */         Iterator it = this._mruFileList.iterator();
/* 252 */         while (it.hasNext()) {
/* 253 */           Object o = it.next();
/* 254 */           if ((!(o instanceof File)) && (!(o instanceof URL)))
/* 255 */             it.remove();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 259 */         this._mruFileList = new LinkedList();
/*     */       }
/*     */     else
/* 262 */       this._mruFileList = new LinkedList();
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 268 */     String home = System.getProperty("user.home");
/* 269 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 271 */     return home + sep + "lf5" + sep + "mru_file_manager";
/*     */   }
/*     */ 
/*     */   protected void setMaxSize(int maxSize)
/*     */   {
/* 278 */     if (maxSize < this._mruFileList.size()) {
/* 279 */       for (int i = 0; i < this._mruFileList.size() - maxSize; i++) {
/* 280 */         this._mruFileList.removeLast();
/*     */       }
/*     */     }
/*     */ 
/* 284 */     this._maxSize = maxSize;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.configure.MRUFileManager
 * JD-Core Version:    0.6.2
 */