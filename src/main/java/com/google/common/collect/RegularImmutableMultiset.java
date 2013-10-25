/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true)
/*     */ class RegularImmutableMultiset<E> extends ImmutableMultiset<E>
/*     */ {
/*     */   private final transient ImmutableMap<E, Integer> map;
/*     */   private final transient int size;
/*     */ 
/*     */   RegularImmutableMultiset(ImmutableMap<E, Integer> map, int size)
/*     */   {
/*  39 */     this.map = map;
/*  40 */     this.size = size;
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/*  45 */     return this.map.isPartialView();
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/*  50 */     Integer value = (Integer)this.map.get(element);
/*  51 */     return value == null ? 0 : value.intValue();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  56 */     return this.size;
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object element)
/*     */   {
/*  61 */     return this.map.containsKey(element);
/*     */   }
/*     */ 
/*     */   public ImmutableSet<E> elementSet()
/*     */   {
/*  66 */     return this.map.keySet();
/*     */   }
/*     */ 
/*     */   private static <E> Multiset.Entry<E> entryFromMapEntry(Map.Entry<E, Integer> entry) {
/*  70 */     return Multisets.immutableEntry(entry.getKey(), ((Integer)entry.getValue()).intValue());
/*     */   }
/*     */ 
/*     */   ImmutableSet<Multiset.Entry<E>> createEntrySet()
/*     */   {
/*  75 */     return new EntrySet(null);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 108 */     return this.map.hashCode();
/*     */   }
/*     */ 
/*     */   private class EntrySet extends ImmutableMultiset.EntrySet
/*     */   {
/*     */     private EntrySet()
/*     */     {
/*  78 */       super();
/*     */     }
/*     */     public int size() {
/*  81 */       return RegularImmutableMultiset.this.map.size();
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<Multiset.Entry<E>> iterator()
/*     */     {
/*  86 */       return asList().iterator();
/*     */     }
/*     */ 
/*     */     ImmutableList<Multiset.Entry<E>> createAsList()
/*     */     {
/*  91 */       final ImmutableList entryList = RegularImmutableMultiset.this.map.entrySet().asList();
/*  92 */       return new ImmutableAsList()
/*     */       {
/*     */         public Multiset.Entry<E> get(int index) {
/*  95 */           return RegularImmutableMultiset.entryFromMapEntry((Map.Entry)entryList.get(index));
/*     */         }
/*     */ 
/*     */         ImmutableCollection<Multiset.Entry<E>> delegateCollection()
/*     */         {
/* 100 */           return RegularImmutableMultiset.EntrySet.this;
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularImmutableMultiset
 * JD-Core Version:    0.6.2
 */