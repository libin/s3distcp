/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.annotations.GwtIncompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.util.EnumMap;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ public final class EnumMultiset<E extends Enum<E>> extends AbstractMapBasedMultiset<E>
/*    */ {
/*    */   private transient Class<E> type;
/*    */ 
/*    */   @GwtIncompatible("Not needed in emulated source")
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public static <E extends Enum<E>> EnumMultiset<E> create(Class<E> type)
/*    */   {
/* 42 */     return new EnumMultiset(type);
/*    */   }
/*    */ 
/*    */   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> elements)
/*    */   {
/* 55 */     Iterator iterator = elements.iterator();
/* 56 */     Preconditions.checkArgument(iterator.hasNext(), "EnumMultiset constructor passed empty Iterable");
/* 57 */     EnumMultiset multiset = new EnumMultiset(((Enum)iterator.next()).getDeclaringClass());
/* 58 */     Iterables.addAll(multiset, elements);
/* 59 */     return multiset;
/*    */   }
/*    */ 
/*    */   private EnumMultiset(Class<E> type)
/*    */   {
/* 66 */     super(WellBehavedMap.wrap(new EnumMap(type)));
/* 67 */     this.type = type;
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("java.io.ObjectOutputStream")
/*    */   private void writeObject(ObjectOutputStream stream) throws IOException {
/* 72 */     stream.defaultWriteObject();
/* 73 */     stream.writeObject(this.type);
/* 74 */     Serialization.writeMultiset(this, stream);
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("java.io.ObjectInputStream")
/*    */   private void readObject(ObjectInputStream stream)
/*    */     throws IOException, ClassNotFoundException
/*    */   {
/* 84 */     stream.defaultReadObject();
/*    */ 
/* 86 */     Class localType = (Class)stream.readObject();
/* 87 */     this.type = localType;
/* 88 */     setBackingMap(WellBehavedMap.wrap(new EnumMap(this.type)));
/* 89 */     Serialization.populateMultiset(this, stream);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EnumMultiset
 * JD-Core Version:    0.6.2
 */