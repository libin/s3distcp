/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal..Gson.Preconditions;
/*     */ import com.google.gson.internal.LazilyParsedNumber;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public final class JsonPrimitive extends JsonElement
/*     */ {
/*  35 */   private static final Class<?>[] PRIMITIVE_TYPES = { Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };
/*     */   private Object value;
/*     */ 
/*     */   public JsonPrimitive(Boolean bool)
/*     */   {
/*  47 */     setValue(bool);
/*     */   }
/*     */ 
/*     */   public JsonPrimitive(Number number)
/*     */   {
/*  56 */     setValue(number);
/*     */   }
/*     */ 
/*     */   public JsonPrimitive(String string)
/*     */   {
/*  65 */     setValue(string);
/*     */   }
/*     */ 
/*     */   public JsonPrimitive(Character c)
/*     */   {
/*  75 */     setValue(c);
/*     */   }
/*     */ 
/*     */   JsonPrimitive(Object primitive)
/*     */   {
/*  85 */     setValue(primitive);
/*     */   }
/*     */ 
/*     */   void setValue(Object primitive) {
/*  89 */     if ((primitive instanceof Character))
/*     */     {
/*  92 */       char c = ((Character)primitive).charValue();
/*  93 */       this.value = String.valueOf(c);
/*     */     } else {
/*  95 */       .Gson.Preconditions.checkArgument(((primitive instanceof Number)) || (isPrimitiveOrString(primitive)));
/*     */ 
/*  97 */       this.value = primitive;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isBoolean()
/*     */   {
/* 107 */     return this.value instanceof Boolean;
/*     */   }
/*     */ 
/*     */   Boolean getAsBooleanWrapper()
/*     */   {
/* 117 */     return (Boolean)this.value;
/*     */   }
/*     */ 
/*     */   public boolean getAsBoolean()
/*     */   {
/* 127 */     if (isBoolean()) {
/* 128 */       return getAsBooleanWrapper().booleanValue();
/*     */     }
/*     */ 
/* 131 */     return Boolean.parseBoolean(getAsString());
/*     */   }
/*     */ 
/*     */   public boolean isNumber()
/*     */   {
/* 141 */     return this.value instanceof Number;
/*     */   }
/*     */ 
/*     */   public Number getAsNumber()
/*     */   {
/* 152 */     return (this.value instanceof String) ? new LazilyParsedNumber((String)this.value) : (Number)this.value;
/*     */   }
/*     */ 
/*     */   public boolean isString()
/*     */   {
/* 161 */     return this.value instanceof String;
/*     */   }
/*     */ 
/*     */   public String getAsString()
/*     */   {
/* 171 */     if (isNumber())
/* 172 */       return getAsNumber().toString();
/* 173 */     if (isBoolean()) {
/* 174 */       return getAsBooleanWrapper().toString();
/*     */     }
/* 176 */     return (String)this.value;
/*     */   }
/*     */ 
/*     */   public double getAsDouble()
/*     */   {
/* 188 */     return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getAsBigDecimal()
/*     */   {
/* 199 */     return (this.value instanceof BigDecimal) ? (BigDecimal)this.value : new BigDecimal(this.value.toString());
/*     */   }
/*     */ 
/*     */   public BigInteger getAsBigInteger()
/*     */   {
/* 210 */     return (this.value instanceof BigInteger) ? (BigInteger)this.value : new BigInteger(this.value.toString());
/*     */   }
/*     */ 
/*     */   public float getAsFloat()
/*     */   {
/* 222 */     return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
/*     */   }
/*     */ 
/*     */   public long getAsLong()
/*     */   {
/* 233 */     return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
/*     */   }
/*     */ 
/*     */   public short getAsShort()
/*     */   {
/* 244 */     return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
/*     */   }
/*     */ 
/*     */   public int getAsInt()
/*     */   {
/* 255 */     return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
/*     */   }
/*     */ 
/*     */   public byte getAsByte()
/*     */   {
/* 260 */     return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
/*     */   }
/*     */ 
/*     */   public char getAsCharacter()
/*     */   {
/* 265 */     return getAsString().charAt(0);
/*     */   }
/*     */ 
/*     */   private static boolean isPrimitiveOrString(Object target) {
/* 269 */     if ((target instanceof String)) {
/* 270 */       return true;
/*     */     }
/*     */ 
/* 273 */     Class classOfPrimitive = target.getClass();
/* 274 */     for (Class standardPrimitive : PRIMITIVE_TYPES) {
/* 275 */       if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
/* 276 */         return true;
/*     */       }
/*     */     }
/* 279 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 284 */     if (this.value == null) {
/* 285 */       return 31;
/*     */     }
/*     */ 
/* 288 */     if (isIntegral(this)) {
/* 289 */       long value = getAsNumber().longValue();
/* 290 */       return (int)(value ^ value >>> 32);
/*     */     }
/* 292 */     if ((this.value instanceof Number)) {
/* 293 */       long value = Double.doubleToLongBits(getAsNumber().doubleValue());
/* 294 */       return (int)(value ^ value >>> 32);
/*     */     }
/* 296 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 301 */     if (this == obj) {
/* 302 */       return true;
/*     */     }
/* 304 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 305 */       return false;
/*     */     }
/* 307 */     JsonPrimitive other = (JsonPrimitive)obj;
/* 308 */     if (this.value == null) {
/* 309 */       return other.value == null;
/*     */     }
/* 311 */     if ((isIntegral(this)) && (isIntegral(other))) {
/* 312 */       return getAsNumber().longValue() == other.getAsNumber().longValue();
/*     */     }
/* 314 */     if (((this.value instanceof Number)) && ((other.value instanceof Number))) {
/* 315 */       double a = getAsNumber().doubleValue();
/*     */ 
/* 318 */       double b = other.getAsNumber().doubleValue();
/* 319 */       return (a == b) || ((Double.isNaN(a)) && (Double.isNaN(b)));
/*     */     }
/* 321 */     return this.value.equals(other.value);
/*     */   }
/*     */ 
/*     */   private static boolean isIntegral(JsonPrimitive primitive)
/*     */   {
/* 329 */     if ((primitive.value instanceof Number)) {
/* 330 */       Number number = (Number)primitive.value;
/* 331 */       return ((number instanceof BigInteger)) || ((number instanceof Long)) || ((number instanceof Integer)) || ((number instanceof Short)) || ((number instanceof Byte));
/*     */     }
/*     */ 
/* 334 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonPrimitive
 * JD-Core Version:    0.6.2
 */