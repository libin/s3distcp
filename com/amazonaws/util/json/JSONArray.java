/*     */ package com.amazonaws.util.json;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class JSONArray
/*     */ {
/*     */   private ArrayList myArrayList;
/*     */ 
/*     */   public JSONArray()
/*     */   {
/*  96 */     this.myArrayList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public JSONArray(JSONTokener x)
/*     */     throws JSONException
/*     */   {
/* 105 */     this();
/* 106 */     char c = x.nextClean();
/*     */     char q;
/* 108 */     if (c == '[') {
/* 109 */       q = ']';
/*     */     }
/*     */     else
/*     */     {
/*     */       char q;
/* 110 */       if (c == '(')
/* 111 */         q = ')';
/*     */       else
/* 113 */         throw x.syntaxError("A JSONArray text must start with '['");
/*     */     }
/*     */     char q;
/* 115 */     if (x.nextClean() == ']') {
/* 116 */       return;
/*     */     }
/* 118 */     x.back();
/*     */     while (true) {
/* 120 */       if (x.nextClean() == ',') {
/* 121 */         x.back();
/* 122 */         this.myArrayList.add(null);
/*     */       } else {
/* 124 */         x.back();
/* 125 */         this.myArrayList.add(x.nextValue());
/*     */       }
/* 127 */       c = x.nextClean();
/* 128 */       switch (c) {
/*     */       case ',':
/*     */       case ';':
/* 131 */         if (x.nextClean() == ']') {
/* 132 */           return;
/*     */         }
/* 134 */         x.back();
/*     */       case ')':
/*     */       case ']':
/*     */       }
/*     */     }
/* 138 */     if (q != c) {
/* 139 */       throw x.syntaxError("Expected a '" + new Character(q) + "'");
/*     */     }
/* 141 */     return;
/*     */ 
/* 143 */     throw x.syntaxError("Expected a ',' or ']'");
/*     */   }
/*     */ 
/*     */   public JSONArray(String source)
/*     */     throws JSONException
/*     */   {
/* 157 */     this(new JSONTokener(source));
/*     */   }
/*     */ 
/*     */   public JSONArray(Collection collection)
/*     */   {
/* 166 */     this.myArrayList = new ArrayList();
/* 167 */     if (collection != null) {
/* 168 */       Iterator iter = collection.iterator();
/* 169 */       while (iter.hasNext()) {
/* 170 */         Object o = iter.next();
/* 171 */         this.myArrayList.add(JSONObject.wrap(o));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public JSONArray(Object array)
/*     */     throws JSONException
/*     */   {
/* 182 */     this();
/* 183 */     if (array.getClass().isArray()) {
/* 184 */       int length = Array.getLength(array);
/* 185 */       for (int i = 0; i < length; i++)
/* 186 */         put(JSONObject.wrap(Array.get(array, i)));
/*     */     }
/*     */     else {
/* 189 */       throw new JSONException("JSONArray initial value should be a string or collection or array.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object get(int index)
/*     */     throws JSONException
/*     */   {
/* 203 */     Object o = opt(index);
/* 204 */     if (o == null) {
/* 205 */       throw new JSONException("JSONArray[" + index + "] not found.");
/*     */     }
/* 207 */     return o;
/*     */   }
/*     */ 
/*     */   public boolean getBoolean(int index)
/*     */     throws JSONException
/*     */   {
/* 221 */     Object o = get(index);
/* 222 */     if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
/*     */     {
/* 225 */       return false;
/* 226 */     }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
/*     */     {
/* 229 */       return true;
/*     */     }
/* 231 */     throw new JSONException("JSONArray[" + index + "] is not a Boolean.");
/*     */   }
/*     */ 
/*     */   public double getDouble(int index)
/*     */     throws JSONException
/*     */   {
/* 244 */     Object o = get(index);
/*     */     try {
/* 246 */       return (o instanceof Number) ? ((Number)o).doubleValue() : Double.valueOf((String)o).doubleValue();
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 250 */     throw new JSONException("JSONArray[" + index + "] is not a number.");
/*     */   }
/*     */ 
/*     */   public int getInt(int index)
/*     */     throws JSONException
/*     */   {
/* 266 */     Object o = get(index);
/* 267 */     return (o instanceof Number) ? ((Number)o).intValue() : (int)getDouble(index);
/*     */   }
/*     */ 
/*     */   public JSONArray getJSONArray(int index)
/*     */     throws JSONException
/*     */   {
/* 280 */     Object o = get(index);
/* 281 */     if ((o instanceof JSONArray)) {
/* 282 */       return (JSONArray)o;
/*     */     }
/* 284 */     throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
/*     */   }
/*     */ 
/*     */   public JSONObject getJSONObject(int index)
/*     */     throws JSONException
/*     */   {
/* 297 */     Object o = get(index);
/* 298 */     if ((o instanceof JSONObject)) {
/* 299 */       return (JSONObject)o;
/*     */     }
/* 301 */     throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
/*     */   }
/*     */ 
/*     */   public long getLong(int index)
/*     */     throws JSONException
/*     */   {
/* 315 */     Object o = get(index);
/* 316 */     return (o instanceof Number) ? ((Number)o).longValue() : ()getDouble(index);
/*     */   }
/*     */ 
/*     */   public String getString(int index)
/*     */     throws JSONException
/*     */   {
/* 328 */     return get(index).toString();
/*     */   }
/*     */ 
/*     */   public boolean isNull(int index)
/*     */   {
/* 338 */     return JSONObject.NULL.equals(opt(index));
/*     */   }
/*     */ 
/*     */   public String join(String separator)
/*     */     throws JSONException
/*     */   {
/* 351 */     int len = length();
/* 352 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 354 */     for (int i = 0; i < len; i++) {
/* 355 */       if (i > 0) {
/* 356 */         sb.append(separator);
/*     */       }
/* 358 */       sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
/*     */     }
/* 360 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 370 */     return this.myArrayList.size();
/*     */   }
/*     */ 
/*     */   public Object opt(int index)
/*     */   {
/* 381 */     return (index < 0) || (index >= length()) ? null : this.myArrayList.get(index);
/*     */   }
/*     */ 
/*     */   public boolean optBoolean(int index)
/*     */   {
/* 395 */     return optBoolean(index, false);
/*     */   }
/*     */ 
/*     */   public boolean optBoolean(int index, boolean defaultValue)
/*     */   {
/*     */     try
/*     */     {
/* 410 */       return getBoolean(index); } catch (Exception e) {
/*     */     }
/* 412 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public double optDouble(int index)
/*     */   {
/* 426 */     return optDouble(index, (0.0D / 0.0D));
/*     */   }
/*     */ 
/*     */   public double optDouble(int index, double defaultValue)
/*     */   {
/*     */     try
/*     */     {
/* 441 */       return getDouble(index); } catch (Exception e) {
/*     */     }
/* 443 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public int optInt(int index)
/*     */   {
/* 457 */     return optInt(index, 0);
/*     */   }
/*     */ 
/*     */   public int optInt(int index, int defaultValue)
/*     */   {
/*     */     try
/*     */     {
/* 471 */       return getInt(index); } catch (Exception e) {
/*     */     }
/* 473 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public JSONArray optJSONArray(int index)
/*     */   {
/* 485 */     Object o = opt(index);
/* 486 */     return (o instanceof JSONArray) ? (JSONArray)o : null;
/*     */   }
/*     */ 
/*     */   public JSONObject optJSONObject(int index)
/*     */   {
/* 499 */     Object o = opt(index);
/* 500 */     return (o instanceof JSONObject) ? (JSONObject)o : null;
/*     */   }
/*     */ 
/*     */   public long optLong(int index)
/*     */   {
/* 513 */     return optLong(index, 0L);
/*     */   }
/*     */ 
/*     */   public long optLong(int index, long defaultValue)
/*     */   {
/*     */     try
/*     */     {
/* 527 */       return getLong(index); } catch (Exception e) {
/*     */     }
/* 529 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public String optString(int index)
/*     */   {
/* 543 */     return optString(index, "");
/*     */   }
/*     */ 
/*     */   public String optString(int index, String defaultValue)
/*     */   {
/* 556 */     Object o = opt(index);
/* 557 */     return o != null ? o.toString() : defaultValue;
/*     */   }
/*     */ 
/*     */   public JSONArray put(boolean value)
/*     */   {
/* 568 */     put(value ? Boolean.TRUE : Boolean.FALSE);
/* 569 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(Collection value)
/*     */   {
/* 580 */     put(new JSONArray(value));
/* 581 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(double value)
/*     */     throws JSONException
/*     */   {
/* 593 */     Double d = new Double(value);
/* 594 */     JSONObject.testValidity(d);
/* 595 */     put(d);
/* 596 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int value)
/*     */   {
/* 607 */     put(new Integer(value));
/* 608 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(long value)
/*     */   {
/* 619 */     put(new Long(value));
/* 620 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(Map value)
/*     */   {
/* 631 */     put(new JSONObject(value));
/* 632 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(Object value)
/*     */   {
/* 644 */     this.myArrayList.add(value);
/* 645 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, boolean value)
/*     */     throws JSONException
/*     */   {
/* 659 */     put(index, value ? Boolean.TRUE : Boolean.FALSE);
/* 660 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, Collection value)
/*     */     throws JSONException
/*     */   {
/* 674 */     put(index, new JSONArray(value));
/* 675 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, double value)
/*     */     throws JSONException
/*     */   {
/* 690 */     put(index, new Double(value));
/* 691 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, int value)
/*     */     throws JSONException
/*     */   {
/* 705 */     put(index, new Integer(value));
/* 706 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, long value)
/*     */     throws JSONException
/*     */   {
/* 720 */     put(index, new Long(value));
/* 721 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, Map value)
/*     */     throws JSONException
/*     */   {
/* 735 */     put(index, new JSONObject(value));
/* 736 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, Object value)
/*     */     throws JSONException
/*     */   {
/* 753 */     JSONObject.testValidity(value);
/* 754 */     if (index < 0) {
/* 755 */       throw new JSONException("JSONArray[" + index + "] not found.");
/*     */     }
/* 757 */     if (index < length()) {
/* 758 */       this.myArrayList.set(index, value);
/*     */     } else {
/* 760 */       while (index != length()) {
/* 761 */         put(JSONObject.NULL);
/*     */       }
/* 763 */       put(value);
/*     */     }
/* 765 */     return this;
/*     */   }
/*     */ 
/*     */   public Object remove(int index)
/*     */   {
/* 776 */     Object o = opt(index);
/* 777 */     this.myArrayList.remove(index);
/* 778 */     return o;
/*     */   }
/*     */ 
/*     */   public JSONObject toJSONObject(JSONArray names)
/*     */     throws JSONException
/*     */   {
/* 792 */     if ((names == null) || (names.length() == 0) || (length() == 0)) {
/* 793 */       return null;
/*     */     }
/* 795 */     JSONObject jo = new JSONObject();
/* 796 */     for (int i = 0; i < names.length(); i++) {
/* 797 */       jo.put(names.getString(i), opt(i));
/*     */     }
/* 799 */     return jo;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 816 */       return '[' + join(",") + ']'; } catch (Exception e) {
/*     */     }
/* 818 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString(int indentFactor)
/*     */     throws JSONException
/*     */   {
/* 835 */     return toString(indentFactor, 0);
/*     */   }
/*     */ 
/*     */   String toString(int indentFactor, int indent)
/*     */     throws JSONException
/*     */   {
/* 850 */     int len = length();
/* 851 */     if (len == 0) {
/* 852 */       return "[]";
/*     */     }
/*     */ 
/* 855 */     StringBuffer sb = new StringBuffer("[");
/* 856 */     if (len == 1) {
/* 857 */       sb.append(JSONObject.valueToString(this.myArrayList.get(0), indentFactor, indent));
/*     */     }
/*     */     else {
/* 860 */       int newindent = indent + indentFactor;
/* 861 */       sb.append('\n');
/* 862 */       for (int i = 0; i < len; i++) {
/* 863 */         if (i > 0) {
/* 864 */           sb.append(",\n");
/*     */         }
/* 866 */         for (int j = 0; j < newindent; j++) {
/* 867 */           sb.append(' ');
/*     */         }
/* 869 */         sb.append(JSONObject.valueToString(this.myArrayList.get(i), indentFactor, newindent));
/*     */       }
/*     */ 
/* 872 */       sb.append('\n');
/* 873 */       for (i = 0; i < indent; i++) {
/* 874 */         sb.append(' ');
/*     */       }
/*     */     }
/* 877 */     sb.append(']');
/* 878 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public Writer write(Writer writer)
/*     */     throws JSONException
/*     */   {
/*     */     try
/*     */     {
/* 893 */       boolean b = false;
/* 894 */       int len = length();
/*     */ 
/* 896 */       writer.write(91);
/*     */ 
/* 898 */       for (int i = 0; i < len; i++) {
/* 899 */         if (b) {
/* 900 */           writer.write(44);
/*     */         }
/* 902 */         Object v = this.myArrayList.get(i);
/* 903 */         if ((v instanceof JSONObject))
/* 904 */           ((JSONObject)v).write(writer);
/* 905 */         else if ((v instanceof JSONArray))
/* 906 */           ((JSONArray)v).write(writer);
/*     */         else {
/* 908 */           writer.write(JSONObject.valueToString(v));
/*     */         }
/* 910 */         b = true;
/*     */       }
/* 912 */       writer.write(93);
/* 913 */       return writer;
/*     */     } catch (IOException e) {
/* 915 */       throw new JSONException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.json.JSONArray
 * JD-Core Version:    0.6.2
 */