/*      */ package com.amazonaws.util.json;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ 
/*      */ public class JSONObject
/*      */ {
/*      */   private Map map;
/*  140 */   public static final Object NULL = new Null(null);
/*      */ 
/*      */   public JSONObject()
/*      */   {
/*  147 */     this.map = new HashMap();
/*      */   }
/*      */ 
/*      */   public JSONObject(JSONObject jo, String[] names)
/*      */   {
/*  161 */     this();
/*  162 */     for (int i = 0; i < names.length; i++)
/*      */       try {
/*  164 */         putOnce(names[i], jo.opt(names[i]));
/*      */       }
/*      */       catch (Exception ignore)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public JSONObject(JSONTokener x)
/*      */     throws JSONException
/*      */   {
/*  178 */     this();
/*      */ 
/*  182 */     if (x.nextClean() != '{')
/*  183 */       throw x.syntaxError("A JSONObject text must begin with '{'");
/*      */     while (true)
/*      */     {
/*  186 */       char c = x.nextClean();
/*  187 */       switch (c) {
/*      */       case '\000':
/*  189 */         throw x.syntaxError("A JSONObject text must end with '}'");
/*      */       case '}':
/*  191 */         return;
/*      */       }
/*  193 */       x.back();
/*  194 */       String key = x.nextValue().toString();
/*      */ 
/*  201 */       c = x.nextClean();
/*  202 */       if (c == '=') {
/*  203 */         if (x.next() != '>')
/*  204 */           x.back();
/*      */       }
/*  206 */       else if (c != ':') {
/*  207 */         throw x.syntaxError("Expected a ':' after a key");
/*      */       }
/*  209 */       putOnce(key, x.nextValue());
/*      */ 
/*  215 */       switch (x.nextClean()) {
/*      */       case ',':
/*      */       case ';':
/*  218 */         if (x.nextClean() == '}') {
/*  219 */           return;
/*      */         }
/*  221 */         x.back();
/*      */       case '}':
/*      */       }
/*      */     }
/*  224 */     return;
/*      */ 
/*  226 */     throw x.syntaxError("Expected a ',' or '}'");
/*      */   }
/*      */ 
/*      */   public JSONObject(Map map)
/*      */   {
/*  240 */     this.map = new HashMap();
/*  241 */     if (map != null) {
/*  242 */       Iterator i = map.entrySet().iterator();
/*  243 */       while (i.hasNext()) {
/*  244 */         Map.Entry e = (Map.Entry)i.next();
/*  245 */         this.map.put(e.getKey(), wrap(e.getValue()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public JSONObject(Object bean)
/*      */   {
/*  271 */     this();
/*  272 */     populateMap(bean);
/*      */   }
/*      */ 
/*      */   public JSONObject(Object object, String[] names)
/*      */   {
/*  288 */     this();
/*  289 */     Class c = object.getClass();
/*  290 */     for (int i = 0; i < names.length; i++) {
/*  291 */       String name = names[i];
/*      */       try {
/*  293 */         putOpt(name, c.getField(name).get(object));
/*      */       }
/*      */       catch (Exception ignore)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public JSONObject(String source)
/*      */     throws JSONException
/*      */   {
/*  310 */     this(new JSONTokener(source));
/*      */   }
/*      */ 
/*      */   public JSONObject accumulate(String key, Object value)
/*      */     throws JSONException
/*      */   {
/*  328 */     testValidity(value);
/*  329 */     Object o = opt(key);
/*  330 */     if (o == null) {
/*  331 */       put(key, (value instanceof JSONArray) ? new JSONArray().put(value) : value);
/*      */     }
/*  334 */     else if ((o instanceof JSONArray))
/*  335 */       ((JSONArray)o).put(value);
/*      */     else {
/*  337 */       put(key, new JSONArray().put(o).put(value));
/*      */     }
/*  339 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject append(String key, Object value)
/*      */     throws JSONException
/*      */   {
/*  355 */     testValidity(value);
/*  356 */     Object o = opt(key);
/*  357 */     if (o == null)
/*  358 */       put(key, new JSONArray().put(value));
/*  359 */     else if ((o instanceof JSONArray))
/*  360 */       put(key, ((JSONArray)o).put(value));
/*      */     else {
/*  362 */       throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
/*      */     }
/*      */ 
/*  365 */     return this;
/*      */   }
/*      */ 
/*      */   public static String doubleToString(double d)
/*      */   {
/*  376 */     if ((Double.isInfinite(d)) || (Double.isNaN(d))) {
/*  377 */       return "null";
/*      */     }
/*      */ 
/*  382 */     String s = Double.toString(d);
/*  383 */     if ((s.indexOf(46) > 0) && (s.indexOf(101) < 0) && (s.indexOf(69) < 0)) {
/*  384 */       while (s.endsWith("0")) {
/*  385 */         s = s.substring(0, s.length() - 1);
/*      */       }
/*  387 */       if (s.endsWith(".")) {
/*  388 */         s = s.substring(0, s.length() - 1);
/*      */       }
/*      */     }
/*  391 */     return s;
/*      */   }
/*      */ 
/*      */   public Object get(String key)
/*      */     throws JSONException
/*      */   {
/*  403 */     Object o = opt(key);
/*  404 */     if (o == null) {
/*  405 */       throw new JSONException("JSONObject[" + quote(key) + "] not found.");
/*      */     }
/*      */ 
/*  408 */     return o;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String key)
/*      */     throws JSONException
/*      */   {
/*  421 */     Object o = get(key);
/*  422 */     if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
/*      */     {
/*  425 */       return false;
/*  426 */     }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
/*      */     {
/*  429 */       return true;
/*      */     }
/*  431 */     throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean.");
/*      */   }
/*      */ 
/*      */   public double getDouble(String key)
/*      */     throws JSONException
/*      */   {
/*  444 */     Object o = get(key);
/*      */     try {
/*  446 */       return (o instanceof Number) ? ((Number)o).doubleValue() : Double.valueOf((String)o).doubleValue();
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/*  450 */     throw new JSONException("JSONObject[" + quote(key) + "] is not a number.");
/*      */   }
/*      */ 
/*      */   public int getInt(String key)
/*      */     throws JSONException
/*      */   {
/*  465 */     Object o = get(key);
/*      */     try {
/*  467 */       return (o instanceof Number) ? ((Number)o).intValue() : Integer.parseInt((String)o);
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/*  471 */     throw new JSONException("JSONObject[" + quote(key) + "] is not an int.");
/*      */   }
/*      */ 
/*      */   public JSONArray getJSONArray(String key)
/*      */     throws JSONException
/*      */   {
/*  486 */     Object o = get(key);
/*  487 */     if ((o instanceof JSONArray)) {
/*  488 */       return (JSONArray)o;
/*      */     }
/*  490 */     throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONArray.");
/*      */   }
/*      */ 
/*      */   public JSONObject getJSONObject(String key)
/*      */     throws JSONException
/*      */   {
/*  504 */     Object o = get(key);
/*  505 */     if ((o instanceof JSONObject)) {
/*  506 */       return (JSONObject)o;
/*      */     }
/*  508 */     throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONObject.");
/*      */   }
/*      */ 
/*      */   public long getLong(String key)
/*      */     throws JSONException
/*      */   {
/*  522 */     Object o = get(key);
/*      */     try {
/*  524 */       return (o instanceof Number) ? ((Number)o).longValue() : Long.parseLong((String)o);
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/*  528 */     throw new JSONException("JSONObject[" + quote(key) + "] is not a long.");
/*      */   }
/*      */ 
/*      */   public static String[] getNames(JSONObject jo)
/*      */   {
/*  540 */     int length = jo.length();
/*  541 */     if (length == 0) {
/*  542 */       return null;
/*      */     }
/*  544 */     Iterator i = jo.keys();
/*  545 */     String[] names = new String[length];
/*  546 */     int j = 0;
/*  547 */     while (i.hasNext()) {
/*  548 */       names[j] = ((String)i.next());
/*  549 */       j++;
/*      */     }
/*  551 */     return names;
/*      */   }
/*      */ 
/*      */   public static String[] getNames(Object object)
/*      */   {
/*  561 */     if (object == null) {
/*  562 */       return null;
/*      */     }
/*  564 */     Class klass = object.getClass();
/*  565 */     Field[] fields = klass.getFields();
/*  566 */     int length = fields.length;
/*  567 */     if (length == 0) {
/*  568 */       return null;
/*      */     }
/*  570 */     String[] names = new String[length];
/*  571 */     for (int i = 0; i < length; i++) {
/*  572 */       names[i] = fields[i].getName();
/*      */     }
/*  574 */     return names;
/*      */   }
/*      */ 
/*      */   public String getString(String key)
/*      */     throws JSONException
/*      */   {
/*  586 */     return get(key).toString();
/*      */   }
/*      */ 
/*      */   public boolean has(String key)
/*      */   {
/*  596 */     return this.map.containsKey(key);
/*      */   }
/*      */ 
/*      */   public JSONObject increment(String key)
/*      */     throws JSONException
/*      */   {
/*  610 */     Object value = opt(key);
/*  611 */     if (value == null) {
/*  612 */       put(key, 1);
/*      */     }
/*  614 */     else if ((value instanceof Integer))
/*  615 */       put(key, ((Integer)value).intValue() + 1);
/*  616 */     else if ((value instanceof Long))
/*  617 */       put(key, ((Long)value).longValue() + 1L);
/*  618 */     else if ((value instanceof Double))
/*  619 */       put(key, ((Double)value).doubleValue() + 1.0D);
/*  620 */     else if ((value instanceof Float))
/*  621 */       put(key, ((Float)value).floatValue() + 1.0F);
/*      */     else {
/*  623 */       throw new JSONException("Unable to increment [" + key + "].");
/*      */     }
/*      */ 
/*  626 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean isNull(String key)
/*      */   {
/*  638 */     return NULL.equals(opt(key));
/*      */   }
/*      */ 
/*      */   public Iterator keys()
/*      */   {
/*  648 */     return this.map.keySet().iterator();
/*      */   }
/*      */ 
/*      */   public int length()
/*      */   {
/*  658 */     return this.map.size();
/*      */   }
/*      */ 
/*      */   public JSONArray names()
/*      */   {
/*  669 */     JSONArray ja = new JSONArray();
/*  670 */     Iterator keys = keys();
/*  671 */     while (keys.hasNext()) {
/*  672 */       ja.put(keys.next());
/*      */     }
/*  674 */     return ja.length() == 0 ? null : ja;
/*      */   }
/*      */ 
/*      */   public static String numberToString(Number n)
/*      */     throws JSONException
/*      */   {
/*  685 */     if (n == null) {
/*  686 */       throw new JSONException("Null pointer");
/*      */     }
/*  688 */     testValidity(n);
/*      */ 
/*  692 */     String s = n.toString();
/*  693 */     if ((s.indexOf(46) > 0) && (s.indexOf(101) < 0) && (s.indexOf(69) < 0)) {
/*  694 */       while (s.endsWith("0")) {
/*  695 */         s = s.substring(0, s.length() - 1);
/*      */       }
/*  697 */       if (s.endsWith(".")) {
/*  698 */         s = s.substring(0, s.length() - 1);
/*      */       }
/*      */     }
/*  701 */     return s;
/*      */   }
/*      */ 
/*      */   public Object opt(String key)
/*      */   {
/*  711 */     return key == null ? null : this.map.get(key);
/*      */   }
/*      */ 
/*      */   public boolean optBoolean(String key)
/*      */   {
/*  724 */     return optBoolean(key, false);
/*      */   }
/*      */ 
/*      */   public boolean optBoolean(String key, boolean defaultValue)
/*      */   {
/*      */     try
/*      */     {
/*  739 */       return getBoolean(key); } catch (Exception e) {
/*      */     }
/*  741 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public double optDouble(String key)
/*      */   {
/*  756 */     return optDouble(key, (0.0D / 0.0D));
/*      */   }
/*      */ 
/*      */   public double optDouble(String key, double defaultValue)
/*      */   {
/*      */     try
/*      */     {
/*  772 */       Object o = opt(key);
/*  773 */       return (o instanceof Number) ? ((Number)o).doubleValue() : new Double((String)o).doubleValue();
/*      */     } catch (Exception e) {
/*      */     }
/*  776 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public int optInt(String key)
/*      */   {
/*  791 */     return optInt(key, 0);
/*      */   }
/*      */ 
/*      */   public int optInt(String key, int defaultValue)
/*      */   {
/*      */     try
/*      */     {
/*  807 */       return getInt(key); } catch (Exception e) {
/*      */     }
/*  809 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public JSONArray optJSONArray(String key)
/*      */   {
/*  823 */     Object o = opt(key);
/*  824 */     return (o instanceof JSONArray) ? (JSONArray)o : null;
/*      */   }
/*      */ 
/*      */   public JSONObject optJSONObject(String key)
/*      */   {
/*  837 */     Object o = opt(key);
/*  838 */     return (o instanceof JSONObject) ? (JSONObject)o : null;
/*      */   }
/*      */ 
/*      */   public long optLong(String key)
/*      */   {
/*  852 */     return optLong(key, 0L);
/*      */   }
/*      */ 
/*      */   public long optLong(String key, long defaultValue)
/*      */   {
/*      */     try
/*      */     {
/*  868 */       return getLong(key); } catch (Exception e) {
/*      */     }
/*  870 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public String optString(String key)
/*      */   {
/*  884 */     return optString(key, "");
/*      */   }
/*      */ 
/*      */   public String optString(String key, String defaultValue)
/*      */   {
/*  897 */     Object o = opt(key);
/*  898 */     return o != null ? o.toString() : defaultValue;
/*      */   }
/*      */ 
/*      */   private void populateMap(Object bean)
/*      */   {
/*  903 */     Class klass = bean.getClass();
/*      */ 
/*  907 */     boolean includeSuperClass = klass.getClassLoader() != null;
/*      */ 
/*  909 */     Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
/*      */ 
/*  911 */     for (int i = 0; i < methods.length; i++)
/*      */       try {
/*  913 */         Method method = methods[i];
/*  914 */         if (Modifier.isPublic(method.getModifiers())) {
/*  915 */           String name = method.getName();
/*  916 */           String key = "";
/*  917 */           if (name.startsWith("get")) {
/*  918 */             if ((name.equals("getClass")) || (name.equals("getDeclaringClass")))
/*      */             {
/*  920 */               key = "";
/*      */             }
/*  922 */             else key = name.substring(3);
/*      */           }
/*  924 */           else if (name.startsWith("is")) {
/*  925 */             key = name.substring(2);
/*      */           }
/*  927 */           if ((key.length() > 0) && (Character.isUpperCase(key.charAt(0))) && (method.getParameterTypes().length == 0))
/*      */           {
/*  930 */             if (key.length() == 1)
/*  931 */               key = key.toLowerCase();
/*  932 */             else if (!Character.isUpperCase(key.charAt(1))) {
/*  933 */               key = key.substring(0, 1).toLowerCase() + key.substring(1);
/*      */             }
/*      */ 
/*  937 */             Object result = method.invoke(bean, (Object[])null);
/*      */ 
/*  939 */             this.map.put(key, wrap(result));
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Exception ignore)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, boolean value)
/*      */     throws JSONException
/*      */   {
/*  957 */     put(key, value ? Boolean.TRUE : Boolean.FALSE);
/*  958 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, Collection value)
/*      */     throws JSONException
/*      */   {
/*  971 */     put(key, new JSONArray(value));
/*  972 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, double value)
/*      */     throws JSONException
/*      */   {
/*  985 */     put(key, new Double(value));
/*  986 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, int value)
/*      */     throws JSONException
/*      */   {
/*  999 */     put(key, new Integer(value));
/* 1000 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, long value)
/*      */     throws JSONException
/*      */   {
/* 1013 */     put(key, new Long(value));
/* 1014 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, Map value)
/*      */     throws JSONException
/*      */   {
/* 1027 */     put(key, new JSONObject(value));
/* 1028 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, Object value)
/*      */     throws JSONException
/*      */   {
/* 1044 */     if (key == null) {
/* 1045 */       throw new JSONException("Null key.");
/*      */     }
/* 1047 */     if (value != null) {
/* 1048 */       testValidity(value);
/* 1049 */       this.map.put(key, value);
/*      */     } else {
/* 1051 */       remove(key);
/*      */     }
/* 1053 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject putOnce(String key, Object value)
/*      */     throws JSONException
/*      */   {
/* 1067 */     if ((key != null) && (value != null)) {
/* 1068 */       if (opt(key) != null) {
/* 1069 */         throw new JSONException("Duplicate key \"" + key + "\"");
/*      */       }
/* 1071 */       put(key, value);
/*      */     }
/* 1073 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject putOpt(String key, Object value)
/*      */     throws JSONException
/*      */   {
/* 1088 */     if ((key != null) && (value != null)) {
/* 1089 */       put(key, value);
/*      */     }
/* 1091 */     return this;
/*      */   }
/*      */ 
/*      */   public static String quote(String string)
/*      */   {
/* 1104 */     if ((string == null) || (string.length() == 0)) {
/* 1105 */       return "\"\"";
/*      */     }
/*      */ 
/* 1109 */     char c = '\000';
/*      */ 
/* 1111 */     int len = string.length();
/* 1112 */     StringBuffer sb = new StringBuffer(len + 4);
/*      */ 
/* 1115 */     sb.append('"');
/* 1116 */     for (int i = 0; i < len; i++) {
/* 1117 */       char b = c;
/* 1118 */       c = string.charAt(i);
/* 1119 */       switch (c) {
/*      */       case '"':
/*      */       case '\\':
/* 1122 */         sb.append('\\');
/* 1123 */         sb.append(c);
/* 1124 */         break;
/*      */       case '/':
/* 1126 */         if (b == '<') {
/* 1127 */           sb.append('\\');
/*      */         }
/* 1129 */         sb.append(c);
/* 1130 */         break;
/*      */       case '\b':
/* 1132 */         sb.append("\\b");
/* 1133 */         break;
/*      */       case '\t':
/* 1135 */         sb.append("\\t");
/* 1136 */         break;
/*      */       case '\n':
/* 1138 */         sb.append("\\n");
/* 1139 */         break;
/*      */       case '\f':
/* 1141 */         sb.append("\\f");
/* 1142 */         break;
/*      */       case '\r':
/* 1144 */         sb.append("\\r");
/* 1145 */         break;
/*      */       default:
/* 1147 */         if ((c < ' ') || ((c >= '') && (c < ' '))) {
/* 1148 */           String t = "000" + Integer.toHexString(c);
/* 1149 */           sb.append("\\u" + t.substring(t.length() - 4));
/*      */         } else {
/* 1151 */           sb.append(c);
/*      */         }break;
/*      */       }
/*      */     }
/* 1155 */     sb.append('"');
/* 1156 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public Object remove(String key)
/*      */   {
/* 1166 */     return this.map.remove(key);
/*      */   }
/*      */ 
/*      */   public Iterator sortedKeys()
/*      */   {
/* 1176 */     return new TreeSet(this.map.keySet()).iterator();
/*      */   }
/*      */ 
/*      */   public static Object stringToValue(String s)
/*      */   {
/* 1186 */     if (s.equals("")) {
/* 1187 */       return s;
/*      */     }
/* 1189 */     if (s.equalsIgnoreCase("true")) {
/* 1190 */       return Boolean.TRUE;
/*      */     }
/* 1192 */     if (s.equalsIgnoreCase("false")) {
/* 1193 */       return Boolean.FALSE;
/*      */     }
/* 1195 */     if (s.equalsIgnoreCase("null")) {
/* 1196 */       return NULL;
/*      */     }
/*      */ 
/* 1208 */     char b = s.charAt(0);
/* 1209 */     if (((b >= '0') && (b <= '9')) || (b == '.') || (b == '-') || (b == '+')) {
/* 1210 */       if ((b == '0') && (s.length() > 2) && ((s.charAt(1) == 'x') || (s.charAt(1) == 'X')))
/*      */         try
/*      */         {
/* 1213 */           return new Integer(Integer.parseInt(s.substring(2), 16));
/*      */         }
/*      */         catch (Exception ignore) {
/*      */         }
/*      */       try {
/* 1218 */         if ((s.indexOf(46) > -1) || (s.indexOf(101) > -1) || (s.indexOf(69) > -1))
/*      */         {
/* 1220 */           return Double.valueOf(s);
/*      */         }
/* 1222 */         Long myLong = new Long(s);
/* 1223 */         if (myLong.longValue() == myLong.intValue()) {
/* 1224 */           return new Integer(myLong.intValue());
/*      */         }
/* 1226 */         return myLong;
/*      */       }
/*      */       catch (Exception ignore)
/*      */       {
/*      */       }
/*      */     }
/* 1232 */     return s;
/*      */   }
/*      */ 
/*      */   static void testValidity(Object o)
/*      */     throws JSONException
/*      */   {
/* 1242 */     if (o != null)
/* 1243 */       if ((o instanceof Double)) {
/* 1244 */         if ((((Double)o).isInfinite()) || (((Double)o).isNaN())) {
/* 1245 */           throw new JSONException("JSON does not allow non-finite numbers.");
/*      */         }
/*      */       }
/* 1248 */       else if (((o instanceof Float)) && (
/* 1249 */         (((Float)o).isInfinite()) || (((Float)o).isNaN())))
/* 1250 */         throw new JSONException("JSON does not allow non-finite numbers.");
/*      */   }
/*      */ 
/*      */   public JSONArray toJSONArray(JSONArray names)
/*      */     throws JSONException
/*      */   {
/* 1267 */     if ((names == null) || (names.length() == 0)) {
/* 1268 */       return null;
/*      */     }
/* 1270 */     JSONArray ja = new JSONArray();
/* 1271 */     for (int i = 0; i < names.length(); i++) {
/* 1272 */       ja.put(opt(names.getString(i)));
/*      */     }
/* 1274 */     return ja;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*      */     try
/*      */     {
/* 1291 */       Iterator keys = keys();
/* 1292 */       StringBuffer sb = new StringBuffer("{");
/*      */ 
/* 1294 */       while (keys.hasNext()) {
/* 1295 */         if (sb.length() > 1) {
/* 1296 */           sb.append(',');
/*      */         }
/* 1298 */         Object o = keys.next();
/* 1299 */         sb.append(quote(o.toString()));
/* 1300 */         sb.append(':');
/* 1301 */         sb.append(valueToString(this.map.get(o)));
/*      */       }
/* 1303 */       sb.append('}');
/* 1304 */       return sb.toString(); } catch (Exception e) {
/*      */     }
/* 1306 */     return null;
/*      */   }
/*      */ 
/*      */   public String toString(int indentFactor)
/*      */     throws JSONException
/*      */   {
/* 1324 */     return toString(indentFactor, 0);
/*      */   }
/*      */ 
/*      */   String toString(int indentFactor, int indent)
/*      */     throws JSONException
/*      */   {
/* 1343 */     int n = length();
/* 1344 */     if (n == 0) {
/* 1345 */       return "{}";
/*      */     }
/* 1347 */     Iterator keys = sortedKeys();
/* 1348 */     StringBuffer sb = new StringBuffer("{");
/* 1349 */     int newindent = indent + indentFactor;
/*      */ 
/* 1351 */     if (n == 1) {
/* 1352 */       Object o = keys.next();
/* 1353 */       sb.append(quote(o.toString()));
/* 1354 */       sb.append(": ");
/* 1355 */       sb.append(valueToString(this.map.get(o), indentFactor, indent));
/*      */     }
/*      */     else {
/* 1358 */       while (keys.hasNext()) {
/* 1359 */         Object o = keys.next();
/* 1360 */         if (sb.length() > 1)
/* 1361 */           sb.append(",\n");
/*      */         else {
/* 1363 */           sb.append('\n');
/*      */         }
/* 1365 */         for (int j = 0; j < newindent; j++) {
/* 1366 */           sb.append(' ');
/*      */         }
/* 1368 */         sb.append(quote(o.toString()));
/* 1369 */         sb.append(": ");
/* 1370 */         sb.append(valueToString(this.map.get(o), indentFactor, newindent));
/*      */       }
/*      */ 
/* 1373 */       if (sb.length() > 1) {
/* 1374 */         sb.append('\n');
/* 1375 */         for (int j = 0; j < indent; j++) {
/* 1376 */           sb.append(' ');
/*      */         }
/*      */       }
/*      */     }
/* 1380 */     sb.append('}');
/* 1381 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   static String valueToString(Object value)
/*      */     throws JSONException
/*      */   {
/* 1407 */     if ((value == null) || (value.equals(null))) {
/* 1408 */       return "null";
/*      */     }
/* 1410 */     if ((value instanceof Number)) {
/* 1411 */       return numberToString((Number)value);
/*      */     }
/* 1413 */     if (((value instanceof Boolean)) || ((value instanceof JSONObject)) || ((value instanceof JSONArray)))
/*      */     {
/* 1415 */       return value.toString();
/*      */     }
/* 1417 */     if ((value instanceof Map)) {
/* 1418 */       return new JSONObject((Map)value).toString();
/*      */     }
/* 1420 */     if ((value instanceof Collection)) {
/* 1421 */       return new JSONArray((Collection)value).toString();
/*      */     }
/* 1423 */     if (value.getClass().isArray()) {
/* 1424 */       return new JSONArray(value).toString();
/*      */     }
/* 1426 */     return quote(value.toString());
/*      */   }
/*      */ 
/*      */   static String valueToString(Object value, int indentFactor, int indent)
/*      */     throws JSONException
/*      */   {
/* 1446 */     if ((value == null) || (value.equals(null))) {
/* 1447 */       return "null";
/*      */     }
/* 1449 */     if ((value instanceof Number)) {
/* 1450 */       return numberToString((Number)value);
/*      */     }
/* 1452 */     if ((value instanceof Boolean)) {
/* 1453 */       return value.toString();
/*      */     }
/* 1455 */     if ((value instanceof JSONObject)) {
/* 1456 */       return ((JSONObject)value).toString(indentFactor, indent);
/*      */     }
/* 1458 */     if ((value instanceof JSONArray)) {
/* 1459 */       return ((JSONArray)value).toString(indentFactor, indent);
/*      */     }
/* 1461 */     if ((value instanceof Map)) {
/* 1462 */       return new JSONObject((Map)value).toString(indentFactor, indent);
/*      */     }
/* 1464 */     if ((value instanceof Collection)) {
/* 1465 */       return new JSONArray((Collection)value).toString(indentFactor, indent);
/*      */     }
/* 1467 */     if (value.getClass().isArray()) {
/* 1468 */       return new JSONArray(value).toString(indentFactor, indent);
/*      */     }
/* 1470 */     return quote(value.toString());
/*      */   }
/*      */ 
/*      */   static Object wrap(Object object)
/*      */   {
/*      */     try
/*      */     {
/* 1488 */       if (object == null) {
/* 1489 */         return NULL;
/*      */       }
/* 1491 */       if (((object instanceof JSONObject)) || ((object instanceof JSONArray)) || (NULL.equals(object)) || ((object instanceof String)) || ((object instanceof Byte)) || ((object instanceof Character)) || ((object instanceof Short)) || ((object instanceof Integer)) || ((object instanceof Long)) || ((object instanceof Boolean)) || ((object instanceof Float)) || ((object instanceof Double)))
/*      */       {
/* 1497 */         return object;
/*      */       }
/*      */ 
/* 1500 */       if ((object instanceof Collection)) {
/* 1501 */         return new JSONArray((Collection)object);
/*      */       }
/* 1503 */       if (object.getClass().isArray()) {
/* 1504 */         return new JSONArray(object);
/*      */       }
/* 1506 */       if ((object instanceof Map)) {
/* 1507 */         return new JSONObject((Map)object);
/*      */       }
/* 1509 */       Package objectPackage = object.getClass().getPackage();
/* 1510 */       String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
/* 1511 */       if ((objectPackageName.startsWith("java.")) || (objectPackageName.startsWith("javax.")) || (object.getClass().getClassLoader() == null))
/*      */       {
/* 1514 */         return object.toString();
/*      */       }
/* 1516 */       return new JSONObject(object); } catch (Exception exception) {
/*      */     }
/* 1518 */     return null;
/*      */   }
/*      */ 
/*      */   public Writer write(Writer writer)
/*      */     throws JSONException
/*      */   {
/*      */     try
/*      */     {
/* 1534 */       boolean b = false;
/* 1535 */       Iterator keys = keys();
/* 1536 */       writer.write(123);
/*      */ 
/* 1538 */       while (keys.hasNext()) {
/* 1539 */         if (b) {
/* 1540 */           writer.write(44);
/*      */         }
/* 1542 */         Object k = keys.next();
/* 1543 */         writer.write(quote(k.toString()));
/* 1544 */         writer.write(58);
/* 1545 */         Object v = this.map.get(k);
/* 1546 */         if ((v instanceof JSONObject))
/* 1547 */           ((JSONObject)v).write(writer);
/* 1548 */         else if ((v instanceof JSONArray))
/* 1549 */           ((JSONArray)v).write(writer);
/*      */         else {
/* 1551 */           writer.write(valueToString(v));
/*      */         }
/* 1553 */         b = true;
/*      */       }
/* 1555 */       writer.write(125);
/* 1556 */       return writer;
/*      */     } catch (IOException exception) {
/* 1558 */       throw new JSONException(exception);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class Null
/*      */   {
/*      */     protected final Object clone()
/*      */     {
/*  103 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object object)
/*      */     {
/*  114 */       return (object == null) || (object == this);
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  123 */       return "null";
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.json.JSONObject
 * JD-Core Version:    0.6.2
 */