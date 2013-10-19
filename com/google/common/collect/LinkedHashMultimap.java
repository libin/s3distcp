/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public final class LinkedHashMultimap<K, V> extends AbstractSetMultimap<K, V>
/*     */ {
/*     */   private static final int DEFAULT_VALUES_PER_KEY = 8;
/*     */ 
/*     */   @VisibleForTesting
/*  79 */   transient int expectedValuesPerKey = 8;
/*     */   transient Collection<Map.Entry<K, V>> linkedEntries;
/*     */ 
/*     */   @GwtIncompatible("java serialization not supported")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> LinkedHashMultimap<K, V> create()
/*     */   {
/*  94 */     return new LinkedHashMultimap();
/*     */   }
/*     */ 
/*     */   public static <K, V> LinkedHashMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey)
/*     */   {
/* 108 */     return new LinkedHashMultimap(expectedKeys, expectedValuesPerKey);
/*     */   }
/*     */ 
/*     */   public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 122 */     return new LinkedHashMultimap(multimap);
/*     */   }
/*     */ 
/*     */   private LinkedHashMultimap() {
/* 126 */     super(new LinkedHashMap());
/* 127 */     this.linkedEntries = Sets.newLinkedHashSet();
/*     */   }
/*     */ 
/*     */   private LinkedHashMultimap(int expectedKeys, int expectedValuesPerKey) {
/* 131 */     super(new LinkedHashMap(expectedKeys));
/* 132 */     Preconditions.checkArgument(expectedValuesPerKey >= 0);
/* 133 */     this.expectedValuesPerKey = expectedValuesPerKey;
/* 134 */     this.linkedEntries = new LinkedHashSet((int)Math.min(1073741824L, expectedKeys * expectedValuesPerKey));
/*     */   }
/*     */ 
/*     */   private LinkedHashMultimap(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 140 */     super(new LinkedHashMap(Maps.capacity(multimap.keySet().size())));
/*     */ 
/* 142 */     this.linkedEntries = new LinkedHashSet(Maps.capacity(multimap.size()));
/*     */ 
/* 144 */     putAll(multimap);
/*     */   }
/*     */ 
/*     */   Set<V> createCollection()
/*     */   {
/* 157 */     return new LinkedHashSet(Maps.capacity(this.expectedValuesPerKey));
/*     */   }
/*     */ 
/*     */   Collection<V> createCollection(@Nullable K key)
/*     */   {
/* 171 */     return new SetDecorator(key, createCollection());
/*     */   }
/*     */ 
/*     */   Iterator<Map.Entry<K, V>> createEntryIterator()
/*     */   {
/* 294 */     final Iterator delegateIterator = this.linkedEntries.iterator();
/*     */ 
/* 296 */     return new Iterator()
/*     */     {
/*     */       Map.Entry<K, V> entry;
/*     */ 
/*     */       public boolean hasNext() {
/* 301 */         return delegateIterator.hasNext();
/*     */       }
/*     */ 
/*     */       public Map.Entry<K, V> next()
/*     */       {
/* 306 */         this.entry = ((Map.Entry)delegateIterator.next());
/* 307 */         return this.entry;
/*     */       }
/*     */ 
/*     */       public void remove()
/*     */       {
/* 313 */         delegateIterator.remove();
/* 314 */         LinkedHashMultimap.this.remove(this.entry.getKey(), this.entry.getValue());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Set<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/* 329 */     return super.replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entries()
/*     */   {
/* 345 */     return super.entries();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 356 */     return super.values();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 369 */     stream.defaultWriteObject();
/* 370 */     stream.writeInt(this.expectedValuesPerKey);
/* 371 */     Serialization.writeMultimap(this, stream);
/* 372 */     for (Map.Entry entry : this.linkedEntries) {
/* 373 */       stream.writeObject(entry.getKey());
/* 374 */       stream.writeObject(entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 381 */     stream.defaultReadObject();
/* 382 */     this.expectedValuesPerKey = stream.readInt();
/* 383 */     int distinctKeys = Serialization.readCount(stream);
/* 384 */     setMap(new LinkedHashMap(Maps.capacity(distinctKeys)));
/* 385 */     this.linkedEntries = new LinkedHashSet(distinctKeys * this.expectedValuesPerKey);
/*     */ 
/* 387 */     Serialization.populateMultimap(this, stream, distinctKeys);
/* 388 */     this.linkedEntries.clear();
/* 389 */     for (int i = 0; i < size(); i++)
/*     */     {
/* 391 */       Object key = stream.readObject();
/*     */ 
/* 393 */       Object value = stream.readObject();
/* 394 */       this.linkedEntries.add(Maps.immutableEntry(key, value));
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SetDecorator extends ForwardingSet<V>
/*     */   {
/*     */     final Set<V> delegate;
/*     */     final K key;
/*     */ 
/*     */     SetDecorator(Set<V> key)
/*     */     {
/* 179 */       this.delegate = delegate;
/* 180 */       this.key = key;
/*     */     }
/*     */ 
/*     */     protected Set<V> delegate() {
/* 184 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     <E> Map.Entry<K, E> createEntry(@Nullable E value) {
/* 188 */       return Maps.immutableEntry(this.key, value);
/*     */     }
/*     */ 
/*     */     <E> Collection<Map.Entry<K, E>> createEntries(Collection<E> values)
/*     */     {
/* 193 */       Collection entries = Lists.newArrayListWithExpectedSize(values.size());
/*     */ 
/* 195 */       for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 196 */         entries.add(createEntry(value));
/*     */       }
/* 198 */       return entries;
/*     */     }
/*     */ 
/*     */     public boolean add(@Nullable V value) {
/* 202 */       boolean changed = this.delegate.add(value);
/* 203 */       if (changed) {
/* 204 */         LinkedHashMultimap.this.linkedEntries.add(createEntry(value));
/*     */       }
/* 206 */       return changed;
/*     */     }
/*     */ 
/*     */     public boolean addAll(Collection<? extends V> values) {
/* 210 */       boolean changed = this.delegate.addAll(values);
/* 211 */       if (changed) {
/* 212 */         LinkedHashMultimap.this.linkedEntries.addAll(createEntries(delegate()));
/*     */       }
/* 214 */       return changed;
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 218 */       for (Iterator i$ = this.delegate.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 219 */         LinkedHashMultimap.this.linkedEntries.remove(createEntry(value));
/*     */       }
/* 221 */       this.delegate.clear();
/*     */     }
/*     */ 
/*     */     public Iterator<V> iterator() {
/* 225 */       final Iterator delegateIterator = this.delegate.iterator();
/* 226 */       return new Iterator()
/*     */       {
/*     */         V value;
/*     */ 
/*     */         public boolean hasNext() {
/* 231 */           return delegateIterator.hasNext();
/*     */         }
/*     */ 
/*     */         public V next() {
/* 235 */           this.value = delegateIterator.next();
/* 236 */           return this.value;
/*     */         }
/*     */ 
/*     */         public void remove() {
/* 240 */           delegateIterator.remove();
/* 241 */           LinkedHashMultimap.this.linkedEntries.remove(LinkedHashMultimap.SetDecorator.this.createEntry(this.value));
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public boolean remove(@Nullable Object value) {
/* 247 */       boolean changed = this.delegate.remove(value);
/* 248 */       if (changed)
/*     */       {
/* 253 */         LinkedHashMultimap.this.linkedEntries.remove(createEntry(value));
/*     */       }
/* 255 */       return changed;
/*     */     }
/*     */ 
/*     */     public boolean removeAll(Collection<?> values) {
/* 259 */       boolean changed = this.delegate.removeAll(values);
/* 260 */       if (changed) {
/* 261 */         LinkedHashMultimap.this.linkedEntries.removeAll(createEntries(values));
/*     */       }
/* 263 */       return changed;
/*     */     }
/*     */ 
/*     */     public boolean retainAll(Collection<?> values)
/*     */     {
/* 271 */       boolean changed = false;
/* 272 */       Iterator iterator = this.delegate.iterator();
/* 273 */       while (iterator.hasNext()) {
/* 274 */         Object value = iterator.next();
/* 275 */         if (!values.contains(value)) {
/* 276 */           iterator.remove();
/* 277 */           LinkedHashMultimap.this.linkedEntries.remove(Maps.immutableEntry(this.key, value));
/* 278 */           changed = true;
/*     */         }
/*     */       }
/* 281 */       return changed;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.LinkedHashMultimap
 * JD-Core Version:    0.6.2
 */