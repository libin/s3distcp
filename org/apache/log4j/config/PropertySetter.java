/*     */ package org.apache.log4j.config;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public class PropertySetter
/*     */ {
/*     */   protected Object obj;
/*     */   protected PropertyDescriptor[] props;
/*     */ 
/*     */   public PropertySetter(Object obj)
/*     */   {
/*  73 */     this.obj = obj;
/*     */   }
/*     */ 
/*     */   protected void introspect()
/*     */   {
/*     */     try
/*     */     {
/*  83 */       BeanInfo bi = Introspector.getBeanInfo(this.obj.getClass());
/*  84 */       this.props = bi.getPropertyDescriptors();
/*     */     } catch (IntrospectionException ex) {
/*  86 */       LogLog.error("Failed to introspect " + this.obj + ": " + ex.getMessage());
/*  87 */       this.props = new PropertyDescriptor[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setProperties(Object obj, Properties properties, String prefix)
/*     */   {
/* 104 */     new PropertySetter(obj).setProperties(properties, prefix);
/*     */   }
/*     */ 
/*     */   public void setProperties(Properties properties, String prefix)
/*     */   {
/* 116 */     int len = prefix.length();
/*     */ 
/* 118 */     for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
/* 119 */       String key = (String)e.nextElement();
/*     */ 
/* 122 */       if (key.startsWith(prefix))
/*     */       {
/* 126 */         if (key.indexOf('.', len + 1) <= 0)
/*     */         {
/* 132 */           String value = OptionConverter.findAndSubst(key, properties);
/* 133 */           key = key.substring(len);
/* 134 */           if (((!"layout".equals(key)) && (!"errorhandler".equals(key))) || (!(this.obj instanceof Appender)))
/*     */           {
/* 140 */             PropertyDescriptor prop = getPropertyDescriptor(Introspector.decapitalize(key));
/* 141 */             if ((prop != null) && (OptionHandler.class.isAssignableFrom(prop.getPropertyType())) && (prop.getWriteMethod() != null))
/*     */             {
/* 144 */               OptionHandler opt = (OptionHandler)OptionConverter.instantiateByKey(properties, prefix + key, prop.getPropertyType(), null);
/*     */ 
/* 148 */               PropertySetter setter = new PropertySetter(opt);
/* 149 */               setter.setProperties(properties, prefix + key + ".");
/*     */               try {
/* 151 */                 prop.getWriteMethod().invoke(this.obj, new Object[] { opt });
/*     */               } catch (IllegalAccessException ex) {
/* 153 */                 LogLog.warn("Failed to set property [" + key + "] to value \"" + value + "\". ", ex);
/*     */               }
/*     */               catch (InvocationTargetException ex) {
/* 156 */                 if (((ex.getTargetException() instanceof InterruptedException)) || ((ex.getTargetException() instanceof InterruptedIOException)))
/*     */                 {
/* 158 */                   Thread.currentThread().interrupt();
/*     */                 }
/* 160 */                 LogLog.warn("Failed to set property [" + key + "] to value \"" + value + "\". ", ex);
/*     */               }
/*     */               catch (RuntimeException ex) {
/* 163 */                 LogLog.warn("Failed to set property [" + key + "] to value \"" + value + "\". ", ex);
/*     */               }
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 169 */               setProperty(key, value);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 172 */     activate();
/*     */   }
/*     */ 
/*     */   public void setProperty(String name, String value)
/*     */   {
/* 192 */     if (value == null) return;
/*     */ 
/* 194 */     name = Introspector.decapitalize(name);
/* 195 */     PropertyDescriptor prop = getPropertyDescriptor(name);
/*     */ 
/* 199 */     if (prop == null)
/* 200 */       LogLog.warn("No such property [" + name + "] in " + this.obj.getClass().getName() + ".");
/*     */     else
/*     */       try
/*     */       {
/* 204 */         setProperty(prop, name, value);
/*     */       } catch (PropertySetterException ex) {
/* 206 */         LogLog.warn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex.rootCause);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setProperty(PropertyDescriptor prop, String name, String value)
/*     */     throws PropertySetterException
/*     */   {
/* 223 */     Method setter = prop.getWriteMethod();
/* 224 */     if (setter == null) {
/* 225 */       throw new PropertySetterException("No setter for property [" + name + "].");
/*     */     }
/* 227 */     Class[] paramTypes = setter.getParameterTypes();
/* 228 */     if (paramTypes.length != 1) {
/* 229 */       throw new PropertySetterException("#params for setter != 1");
/*     */     }
/*     */     Object arg;
/*     */     try
/*     */     {
/* 234 */       arg = convertArg(value, paramTypes[0]);
/*     */     } catch (Throwable t) {
/* 236 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. Reason: " + t);
/*     */     }
/*     */ 
/* 239 */     if (arg == null) {
/* 240 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
/*     */     }
/*     */ 
/* 243 */     LogLog.debug("Setting property [" + name + "] to [" + arg + "].");
/*     */     try {
/* 245 */       setter.invoke(this.obj, new Object[] { arg });
/*     */     } catch (IllegalAccessException ex) {
/* 247 */       throw new PropertySetterException(ex);
/*     */     } catch (InvocationTargetException ex) {
/* 249 */       if (((ex.getTargetException() instanceof InterruptedException)) || ((ex.getTargetException() instanceof InterruptedIOException)))
/*     */       {
/* 251 */         Thread.currentThread().interrupt();
/*     */       }
/* 253 */       throw new PropertySetterException(ex);
/*     */     } catch (RuntimeException ex) {
/* 255 */       throw new PropertySetterException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object convertArg(String val, Class type)
/*     */   {
/* 266 */     if (val == null) {
/* 267 */       return null;
/*     */     }
/* 269 */     String v = val.trim();
/* 270 */     if (String.class.isAssignableFrom(type))
/* 271 */       return val;
/* 272 */     if (Integer.TYPE.isAssignableFrom(type))
/* 273 */       return new Integer(v);
/* 274 */     if (Long.TYPE.isAssignableFrom(type))
/* 275 */       return new Long(v);
/* 276 */     if (Boolean.TYPE.isAssignableFrom(type)) {
/* 277 */       if ("true".equalsIgnoreCase(v))
/* 278 */         return Boolean.TRUE;
/* 279 */       if ("false".equalsIgnoreCase(v))
/* 280 */         return Boolean.FALSE;
/*     */     } else {
/* 282 */       if (Priority.class.isAssignableFrom(type))
/* 283 */         return OptionConverter.toLevel(v, Level.DEBUG);
/* 284 */       if (ErrorHandler.class.isAssignableFrom(type)) {
/* 285 */         return OptionConverter.instantiateByClassName(v, ErrorHandler.class, null);
/*     */       }
/*     */     }
/* 288 */     return null;
/*     */   }
/*     */ 
/*     */   protected PropertyDescriptor getPropertyDescriptor(String name)
/*     */   {
/* 294 */     if (this.props == null) introspect();
/*     */ 
/* 296 */     for (int i = 0; i < this.props.length; i++) {
/* 297 */       if (name.equals(this.props[i].getName())) {
/* 298 */         return this.props[i];
/*     */       }
/*     */     }
/* 301 */     return null;
/*     */   }
/*     */ 
/*     */   public void activate()
/*     */   {
/* 306 */     if ((this.obj instanceof OptionHandler))
/* 307 */       ((OptionHandler)this.obj).activateOptions();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.config.PropertySetter
 * JD-Core Version:    0.6.2
 */