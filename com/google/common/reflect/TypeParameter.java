/*    */ package com.google.common.reflect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.TypeVariable;
/*    */ 
/*    */ @Beta
/*    */ public abstract class TypeParameter<T> extends TypeCapture<T>
/*    */ {
/*    */   final TypeVariable<?> typeVariable;
/*    */ 
/*    */   private TypeParameter(TypeVariable<?> typeVariable)
/*    */   {
/* 46 */     this.typeVariable = ((TypeVariable)Preconditions.checkNotNull(typeVariable));
/*    */   }
/*    */ 
/*    */   protected TypeParameter() {
/* 50 */     Type type = capture();
/* 51 */     Preconditions.checkArgument(type instanceof TypeVariable, "%s should be a type variable.", new Object[] { type });
/* 52 */     this.typeVariable = ((TypeVariable)type);
/*    */   }
/*    */ 
/*    */   public final int hashCode() {
/* 56 */     return this.typeVariable.hashCode();
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object o) {
/* 60 */     if ((o instanceof TypeParameter)) {
/* 61 */       TypeParameter that = (TypeParameter)o;
/* 62 */       return this.typeVariable.equals(that.typeVariable);
/*    */     }
/* 64 */     return false;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 68 */     return this.typeVariable.toString();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.TypeParameter
 * JD-Core Version:    0.6.2
 */