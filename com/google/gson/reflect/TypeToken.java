/*     */ package com.google.gson.reflect;
/*     */ 
/*     */ import com.google.gson.internal..Gson.Preconditions;
/*     */ import com.google.gson.internal..Gson.Types;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TypeToken<T>
/*     */ {
/*     */   final Class<? super T> rawType;
/*     */   final Type type;
/*     */   final int hashCode;
/*     */ 
/*     */   protected TypeToken()
/*     */   {
/*  62 */     this.type = getSuperclassTypeParameter(getClass());
/*  63 */     this.rawType = .Gson.Types.getRawType(this.type);
/*  64 */     this.hashCode = this.type.hashCode();
/*     */   }
/*     */ 
/*     */   TypeToken(Type type)
/*     */   {
/*  72 */     this.type = .Gson.Types.canonicalize((Type).Gson.Preconditions.checkNotNull(type));
/*  73 */     this.rawType = .Gson.Types.getRawType(this.type);
/*  74 */     this.hashCode = this.type.hashCode();
/*     */   }
/*     */ 
/*     */   static Type getSuperclassTypeParameter(Class<?> subclass)
/*     */   {
/*  82 */     Type superclass = subclass.getGenericSuperclass();
/*  83 */     if ((superclass instanceof Class)) {
/*  84 */       throw new RuntimeException("Missing type parameter.");
/*     */     }
/*  86 */     ParameterizedType parameterized = (ParameterizedType)superclass;
/*  87 */     return .Gson.Types.canonicalize(parameterized.getActualTypeArguments()[0]);
/*     */   }
/*     */ 
/*     */   public final Class<? super T> getRawType()
/*     */   {
/*  94 */     return this.rawType;
/*     */   }
/*     */ 
/*     */   public final Type getType()
/*     */   {
/* 101 */     return this.type;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isAssignableFrom(Class<?> cls)
/*     */   {
/* 112 */     return isAssignableFrom(cls);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isAssignableFrom(Type from)
/*     */   {
/* 123 */     if (from == null) {
/* 124 */       return false;
/*     */     }
/*     */ 
/* 127 */     if (this.type.equals(from)) {
/* 128 */       return true;
/*     */     }
/*     */ 
/* 131 */     if ((this.type instanceof Class))
/* 132 */       return this.rawType.isAssignableFrom(.Gson.Types.getRawType(from));
/* 133 */     if ((this.type instanceof ParameterizedType)) {
/* 134 */       return isAssignableFrom(from, (ParameterizedType)this.type, new HashMap());
/*     */     }
/* 136 */     if ((this.type instanceof GenericArrayType)) {
/* 137 */       return (this.rawType.isAssignableFrom(.Gson.Types.getRawType(from))) && (isAssignableFrom(from, (GenericArrayType)this.type));
/*     */     }
/*     */ 
/* 140 */     throw buildUnexpectedTypeError(this.type, new Class[] { Class.class, ParameterizedType.class, GenericArrayType.class });
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isAssignableFrom(TypeToken<?> token)
/*     */   {
/* 153 */     return isAssignableFrom(token.getType());
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableFrom(Type from, GenericArrayType to)
/*     */   {
/* 161 */     Type toGenericComponentType = to.getGenericComponentType();
/* 162 */     if ((toGenericComponentType instanceof ParameterizedType)) {
/* 163 */       Type t = from;
/* 164 */       if ((from instanceof GenericArrayType)) {
/* 165 */         t = ((GenericArrayType)from).getGenericComponentType();
/* 166 */       } else if ((from instanceof Class)) {
/* 167 */         Class classType = (Class)from;
/* 168 */         while (classType.isArray()) {
/* 169 */           classType = classType.getComponentType();
/*     */         }
/* 171 */         t = classType;
/*     */       }
/* 173 */       return isAssignableFrom(t, (ParameterizedType)toGenericComponentType, new HashMap());
/*     */     }
/*     */ 
/* 178 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableFrom(Type from, ParameterizedType to, Map<String, Type> typeVarMap)
/*     */   {
/* 188 */     if (from == null) {
/* 189 */       return false;
/*     */     }
/*     */ 
/* 192 */     if (to.equals(from)) {
/* 193 */       return true;
/*     */     }
/*     */ 
/* 197 */     Class clazz = .Gson.Types.getRawType(from);
/* 198 */     ParameterizedType ptype = null;
/* 199 */     if ((from instanceof ParameterizedType)) {
/* 200 */       ptype = (ParameterizedType)from;
/*     */     }
/*     */ 
/* 204 */     if (ptype != null) {
/* 205 */       Type[] tArgs = ptype.getActualTypeArguments();
/* 206 */       TypeVariable[] tParams = clazz.getTypeParameters();
/* 207 */       for (int i = 0; i < tArgs.length; i++) {
/* 208 */         Type arg = tArgs[i];
/* 209 */         TypeVariable var = tParams[i];
/* 210 */         while ((arg instanceof TypeVariable)) {
/* 211 */           TypeVariable v = (TypeVariable)arg;
/* 212 */           arg = (Type)typeVarMap.get(v.getName());
/*     */         }
/* 214 */         typeVarMap.put(var.getName(), arg);
/*     */       }
/*     */ 
/* 218 */       if (typeEquals(ptype, to, typeVarMap)) {
/* 219 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 223 */     for (Type itype : clazz.getGenericInterfaces()) {
/* 224 */       if (isAssignableFrom(itype, to, new HashMap(typeVarMap))) {
/* 225 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 230 */     Type sType = clazz.getGenericSuperclass();
/* 231 */     return isAssignableFrom(sType, to, new HashMap(typeVarMap));
/*     */   }
/*     */ 
/*     */   private static boolean typeEquals(ParameterizedType from, ParameterizedType to, Map<String, Type> typeVarMap)
/*     */   {
/* 240 */     if (from.getRawType().equals(to.getRawType())) {
/* 241 */       Type[] fromArgs = from.getActualTypeArguments();
/* 242 */       Type[] toArgs = to.getActualTypeArguments();
/* 243 */       for (int i = 0; i < fromArgs.length; i++) {
/* 244 */         if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
/* 245 */           return false;
/*     */         }
/*     */       }
/* 248 */       return true;
/*     */     }
/* 250 */     return false;
/*     */   }
/*     */ 
/*     */   private static AssertionError buildUnexpectedTypeError(Type token, Class<?>[] expected)
/*     */   {
/* 257 */     StringBuilder exceptionMessage = new StringBuilder("Unexpected type. Expected one of: ");
/*     */ 
/* 259 */     for (Class clazz : expected) {
/* 260 */       exceptionMessage.append(clazz.getName()).append(", ");
/*     */     }
/* 262 */     exceptionMessage.append("but got: ").append(token.getClass().getName()).append(", for type token: ").append(token.toString()).append('.');
/*     */ 
/* 265 */     return new AssertionError(exceptionMessage.toString());
/*     */   }
/*     */ 
/*     */   private static boolean matches(Type from, Type to, Map<String, Type> typeMap)
/*     */   {
/* 273 */     return (to.equals(from)) || (((from instanceof TypeVariable)) && (to.equals(typeMap.get(((TypeVariable)from).getName()))));
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 280 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */   public final boolean equals(Object o) {
/* 284 */     return ((o instanceof TypeToken)) && (.Gson.Types.equals(this.type, ((TypeToken)o).type));
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 289 */     return .Gson.Types.typeToString(this.type);
/*     */   }
/*     */ 
/*     */   public static TypeToken<?> get(Type type)
/*     */   {
/* 296 */     return new TypeToken(type);
/*     */   }
/*     */ 
/*     */   public static <T> TypeToken<T> get(Class<T> type)
/*     */   {
/* 303 */     return new TypeToken(type);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.reflect.TypeToken
 * JD-Core Version:    0.6.2
 */