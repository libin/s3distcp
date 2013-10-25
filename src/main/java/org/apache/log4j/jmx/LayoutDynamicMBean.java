/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public class LayoutDynamicMBean extends AbstractDynamicMBean
/*     */ {
/*  53 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  54 */   private Vector dAttributes = new Vector();
/*  55 */   private String dClassName = getClass().getName();
/*     */ 
/*  57 */   private Hashtable dynamicProps = new Hashtable(5);
/*  58 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
/*  59 */   private String dDescription = "This MBean acts as a management facade for log4j layouts.";
/*     */ 
/*  63 */   private static Logger cat = Logger.getLogger(LayoutDynamicMBean.class);
/*     */   private Layout layout;
/*     */ 
/*     */   public LayoutDynamicMBean(Layout layout)
/*     */     throws IntrospectionException
/*     */   {
/*  69 */     this.layout = layout;
/*  70 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo() throws IntrospectionException
/*     */   {
/*  75 */     Constructor[] constructors = getClass().getConstructors();
/*  76 */     this.dConstructors[0] = new MBeanConstructorInfo("LayoutDynamicMBean(): Constructs a LayoutDynamicMBean instance", constructors[0]);
/*     */ 
/*  81 */     BeanInfo bi = Introspector.getBeanInfo(this.layout.getClass());
/*  82 */     PropertyDescriptor[] pd = bi.getPropertyDescriptors();
/*     */ 
/*  84 */     int size = pd.length;
/*     */ 
/*  86 */     for (int i = 0; i < size; i++) {
/*  87 */       String name = pd[i].getName();
/*  88 */       Method readMethod = pd[i].getReadMethod();
/*  89 */       Method writeMethod = pd[i].getWriteMethod();
/*  90 */       if (readMethod != null) {
/*  91 */         Class returnClass = readMethod.getReturnType();
/*  92 */         if (isSupportedType(returnClass))
/*     */         {
/*     */           String returnClassName;
/*     */           String returnClassName;
/*  94 */           if (returnClass.isAssignableFrom(Level.class))
/*  95 */             returnClassName = "java.lang.String";
/*     */           else {
/*  97 */             returnClassName = returnClass.getName();
/*     */           }
/*     */ 
/* 100 */           this.dAttributes.add(new MBeanAttributeInfo(name, returnClassName, "Dynamic", true, writeMethod != null, false));
/*     */ 
/* 106 */           this.dynamicProps.put(name, new MethodUnion(readMethod, writeMethod));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 111 */     MBeanParameterInfo[] params = new MBeanParameterInfo[0];
/*     */ 
/* 113 */     this.dOperations[0] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an layout", params, "void", 1);
/*     */   }
/*     */ 
/*     */   private boolean isSupportedType(Class clazz)
/*     */   {
/* 122 */     if (clazz.isPrimitive()) {
/* 123 */       return true;
/*     */     }
/*     */ 
/* 126 */     if (clazz == String.class) {
/* 127 */       return true;
/*     */     }
/* 129 */     if (clazz.isAssignableFrom(Level.class)) {
/* 130 */       return true;
/*     */     }
/*     */ 
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 140 */     cat.debug("getMBeanInfo called.");
/*     */ 
/* 142 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.dAttributes.size()];
/* 143 */     this.dAttributes.toArray(attribs);
/*     */ 
/* 145 */     return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 158 */     if ((operationName.equals("activateOptions")) && ((this.layout instanceof OptionHandler)))
/*     */     {
/* 160 */       OptionHandler oh = this.layout;
/* 161 */       oh.activateOptions();
/* 162 */       return "Options activated.";
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 169 */     return cat;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 179 */     if (attributeName == null) {
/* 180 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 186 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(attributeName);
/*     */ 
/* 188 */     cat.debug("----name=" + attributeName + ", mu=" + mu);
/*     */ 
/* 190 */     if ((mu != null) && (mu.readMethod != null)) {
/*     */       try {
/* 192 */         return mu.readMethod.invoke(this.layout, null);
/*     */       } catch (InvocationTargetException e) {
/* 194 */         if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */         {
/* 196 */           Thread.currentThread().interrupt();
/*     */         }
/* 198 */         return null;
/*     */       } catch (IllegalAccessException e) {
/* 200 */         return null;
/*     */       } catch (RuntimeException e) {
/* 202 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 209 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 222 */     if (attribute == null) {
/* 223 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 228 */     String name = attribute.getName();
/* 229 */     Object value = attribute.getValue();
/*     */ 
/* 231 */     if (name == null) {
/* 232 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 240 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(name);
/*     */ 
/* 242 */     if ((mu != null) && (mu.writeMethod != null)) {
/* 243 */       Object[] o = new Object[1];
/*     */ 
/* 245 */       Class[] params = mu.writeMethod.getParameterTypes();
/* 246 */       if (params[0] == Priority.class) {
/* 247 */         value = OptionConverter.toLevel((String)value, (Level)getAttribute(name));
/*     */       }
/*     */ 
/* 250 */       o[0] = value;
/*     */       try
/*     */       {
/* 253 */         mu.writeMethod.invoke(this.layout, o);
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 256 */         if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */         {
/* 258 */           Thread.currentThread().interrupt();
/*     */         }
/* 260 */         cat.error("FIXME", e);
/*     */       } catch (IllegalAccessException e) {
/* 262 */         cat.error("FIXME", e);
/*     */       } catch (RuntimeException e) {
/* 264 */         cat.error("FIXME", e);
/*     */       }
/*     */     } else {
/* 267 */       throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jmx.LayoutDynamicMBean
 * JD-Core Version:    0.6.2
 */