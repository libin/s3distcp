/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public class TreeMultimap<K, V> extends AbstractSortedSetMultimap<K, V>
/*     */ {
/*     */   private transient Comparator<? super K> keyComparator;
/*     */   private transient Comparator<? super V> valueComparator;
/*     */ 
/*     */   @GwtIncompatible("not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create()
/*     */   {
/*  85 */     return new TreeMultimap(Ordering.natural(), Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <K, V> TreeMultimap<K, V> create(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator)
/*     */   {
/*  99 */     return new TreeMultimap((Comparator)Preconditions.checkNotNull(keyComparator), (Comparator)Preconditions.checkNotNull(valueComparator));
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 111 */     return new TreeMultimap(Ordering.natural(), Ordering.natural(), multimap);
/*     */   }
/*     */ 
/*     */   TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator)
/*     */   {
/* 117 */     super(new TreeMap(keyComparator));
/* 118 */     this.keyComparator = keyComparator;
/* 119 */     this.valueComparator = valueComparator;
/*     */   }
/*     */ 
/*     */   private TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator, Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 125 */     this(keyComparator, valueComparator);
/* 126 */     putAll(multimap);
/*     */   }
/*     */ 
/*     */   SortedSet<V> createCollection()
/*     */   {
/* 138 */     return new TreeSet(this.valueComparator);
/*     */   }
/*     */ 
/*     */   public Comparator<? super K> keyComparator()
/*     */   {
/* 145 */     return this.keyComparator;
/*     */   }
/*     */ 
/*     */   public Comparator<? super V> valueComparator()
/*     */   {
/* 150 */     return this.valueComparator;
/*     */   }
/*     */ 
/*     */   public SortedSet<K> keySet()
/*     */   {
/* 161 */     return (SortedSet)super.keySet();
/*     */   }
/*     */ 
/*     */   public SortedMap<K, Collection<V>> asMap()
/*     */   {
/* 172 */     return (SortedMap)super.asMap();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 182 */     stream.defaultWriteObject();
/* 183 */     stream.writeObject(keyComparator());
/* 184 */     stream.writeObject(valueComparator());
/* 185 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 192 */     stream.defaultReadObject();
/* 193 */     this.keyComparator = ((Comparator)Preconditions.checkNotNull((Comparator)stream.readObject()));
/* 194 */     this.valueComparator = ((Comparator)Preconditions.checkNotNull((Comparator)stream.readObject()));
/* 195 */     setMap(new TreeMap(this.keyComparator));
/* 196 */     Serialization.populateMultimap(this, stream);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.TreeMultimap
 * JD-Core Version:    0.6.2
 */