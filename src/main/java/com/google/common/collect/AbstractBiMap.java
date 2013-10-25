/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ abstract class AbstractBiMap<K, V> extends ForwardingMap<K, V>
/*     */   implements BiMap<K, V>, Serializable
/*     */ {
/*     */   private transient Map<K, V> delegate;
/*     */   transient AbstractBiMap<V, K> inverse;
/*     */   private transient Set<K> keySet;
/*     */   private transient Set<V> valueSet;
/*     */   private transient Set<Map.Entry<K, V>> entrySet;
/*     */ 
/*     */   @GwtIncompatible("Not needed in emulated source.")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   AbstractBiMap(Map<K, V> forward, Map<V, K> backward)
/*     */   {
/*  56 */     setDelegates(forward, backward);
/*     */   }
/*     */ 
/*     */   private AbstractBiMap(Map<K, V> backward, AbstractBiMap<V, K> forward)
/*     */   {
/*  61 */     this.delegate = backward;
/*  62 */     this.inverse = forward;
/*     */   }
/*     */ 
/*     */   protected Map<K, V> delegate() {
/*  66 */     return this.delegate;
/*     */   }
/*     */ 
/*     */   K checkKey(@Nullable K key)
/*     */   {
/*  73 */     return key;
/*     */   }
/*     */ 
/*     */   V checkValue(@Nullable V value)
/*     */   {
/*  80 */     return value;
/*     */   }
/*     */ 
/*     */   void setDelegates(Map<K, V> forward, Map<V, K> backward)
/*     */   {
/*  88 */     Preconditions.checkState(this.delegate == null);
/*  89 */     Preconditions.checkState(this.inverse == null);
/*  90 */     Preconditions.checkArgument(forward.isEmpty());
/*  91 */     Preconditions.checkArgument(backward.isEmpty());
/*  92 */     Preconditions.checkArgument(forward != backward);
/*  93 */     this.delegate = forward;
/*  94 */     this.inverse = new Inverse(backward, this, null);
/*     */   }
/*     */ 
/*     */   void setInverse(AbstractBiMap<V, K> inverse) {
/*  98 */     this.inverse = inverse;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value)
/*     */   {
/* 104 */     return this.inverse.containsKey(value);
/*     */   }
/*     */ 
/*     */   public V put(K key, V value)
/*     */   {
/* 110 */     return putInBothMaps(key, value, false);
/*     */   }
/*     */ 
/*     */   public V forcePut(K key, V value)
/*     */   {
/* 115 */     return putInBothMaps(key, value, true);
/*     */   }
/*     */ 
/*     */   private V putInBothMaps(@Nullable K key, @Nullable V value, boolean force) {
/* 119 */     checkKey(key);
/* 120 */     checkValue(value);
/* 121 */     boolean containedKey = containsKey(key);
/* 122 */     if ((containedKey) && (Objects.equal(value, get(key)))) {
/* 123 */       return value;
/*     */     }
/* 125 */     if (force)
/* 126 */       inverse().remove(value);
/*     */     else {
/* 128 */       Preconditions.checkArgument(!containsValue(value), "value already present: %s", new Object[] { value });
/*     */     }
/* 130 */     Object oldValue = this.delegate.put(key, value);
/* 131 */     updateInverseMap(key, containedKey, oldValue, value);
/* 132 */     return oldValue;
/*     */   }
/*     */ 
/*     */   private void updateInverseMap(K key, boolean containedKey, V oldValue, V newValue)
/*     */   {
/* 137 */     if (containedKey) {
/* 138 */       removeFromInverseMap(oldValue);
/*     */     }
/* 140 */     this.inverse.delegate.put(newValue, key);
/*     */   }
/*     */ 
/*     */   public V remove(Object key) {
/* 144 */     return containsKey(key) ? removeFromBothMaps(key) : null;
/*     */   }
/*     */ 
/*     */   private V removeFromBothMaps(Object key) {
/* 148 */     Object oldValue = this.delegate.remove(key);
/* 149 */     removeFromInverseMap(oldValue);
/* 150 */     return oldValue;
/*     */   }
/*     */ 
/*     */   private void removeFromInverseMap(V oldValue) {
/* 154 */     this.inverse.delegate.remove(oldValue);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> map)
/*     */   {
/* 160 */     for (Map.Entry entry : map.entrySet())
/* 161 */       put(entry.getKey(), entry.getValue());
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 166 */     this.delegate.clear();
/* 167 */     this.inverse.delegate.clear();
/*     */   }
/*     */ 
/*     */   public BiMap<V, K> inverse()
/*     */   {
/* 174 */     return this.inverse;
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 180 */     Set result = this.keySet;
/* 181 */     return result == null ? (this.keySet = new KeySet(null)) : result;
/*     */   }
/*     */ 
/*     */   public Set<V> values()
/*     */   {
/* 221 */     Set result = this.valueSet;
/* 222 */     return result == null ? (this.valueSet = new ValueSet(null)) : result;
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/* 252 */     Set result = this.entrySet;
/* 253 */     return result == null ? (this.entrySet = new EntrySet(null)) : result;
/*     */   }
/*     */ 
/*     */   private static class Inverse<K, V> extends AbstractBiMap<K, V>
/*     */   {
/*     */ 
/*     */     @GwtIncompatible("Not needed in emulated source.")
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private Inverse(Map<K, V> backward, AbstractBiMap<V, K> forward)
/*     */     {
/* 354 */       super(forward, null);
/*     */     }
/*     */ 
/*     */     K checkKey(K key)
/*     */     {
/* 368 */       return this.inverse.checkValue(key);
/*     */     }
/*     */ 
/*     */     V checkValue(V value)
/*     */     {
/* 373 */       return this.inverse.checkKey(value);
/*     */     }
/*     */ 
/*     */     @GwtIncompatible("java.io.ObjectOuputStream")
/*     */     private void writeObject(ObjectOutputStream stream)
/*     */       throws IOException
/*     */     {
/* 381 */       stream.defaultWriteObject();
/* 382 */       stream.writeObject(inverse());
/*     */     }
/*     */ 
/*     */     @GwtIncompatible("java.io.ObjectInputStream")
/*     */     private void readObject(ObjectInputStream stream)
/*     */       throws IOException, ClassNotFoundException
/*     */     {
/* 389 */       stream.defaultReadObject();
/* 390 */       setInverse((AbstractBiMap)stream.readObject());
/*     */     }
/*     */ 
/*     */     @GwtIncompatible("Not needed in the emulated source.")
/*     */     Object readResolve() {
/* 395 */       return inverse().inverse();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntrySet extends ForwardingSet<Map.Entry<K, V>>
/*     */   {
/* 257 */     final Set<Map.Entry<K, V>> esDelegate = AbstractBiMap.this.delegate.entrySet();
/*     */ 
/*     */     private EntrySet() {  } 
/* 260 */     protected Set<Map.Entry<K, V>> delegate() { return this.esDelegate; }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 264 */       AbstractBiMap.this.clear();
/*     */     }
/*     */ 
/*     */     public boolean remove(Object object) {
/* 268 */       if (!this.esDelegate.contains(object)) {
/* 269 */         return false;
/*     */       }
/*     */ 
/* 273 */       Map.Entry entry = (Map.Entry)object;
/* 274 */       AbstractBiMap.this.inverse.delegate.remove(entry.getValue());
/*     */ 
/* 280 */       this.esDelegate.remove(entry);
/* 281 */       return true;
/*     */     }
/*     */ 
/*     */     public Iterator<Map.Entry<K, V>> iterator() {
/* 285 */       final Iterator iterator = this.esDelegate.iterator();
/* 286 */       return new Iterator() {
/*     */         Map.Entry<K, V> entry;
/*     */ 
/*     */         public boolean hasNext() {
/* 290 */           return iterator.hasNext();
/*     */         }
/*     */ 
/*     */         public Map.Entry<K, V> next() {
/* 294 */           this.entry = ((Map.Entry)iterator.next());
/* 295 */           final Map.Entry finalEntry = this.entry;
/*     */ 
/* 297 */           return new ForwardingMapEntry() {
/*     */             protected Map.Entry<K, V> delegate() {
/* 299 */               return finalEntry;
/*     */             }
/*     */ 
/*     */             public V setValue(V value)
/*     */             {
/* 304 */               Preconditions.checkState(AbstractBiMap.EntrySet.this.contains(this), "entry no longer in map");
/*     */ 
/* 306 */               if (Objects.equal(value, getValue())) {
/* 307 */                 return value;
/*     */               }
/* 309 */               Preconditions.checkArgument(!AbstractBiMap.this.containsValue(value), "value already present: %s", new Object[] { value });
/*     */ 
/* 311 */               Object oldValue = finalEntry.setValue(value);
/* 312 */               Preconditions.checkState(Objects.equal(value, AbstractBiMap.this.get(getKey())), "entry no longer in map");
/*     */ 
/* 314 */               AbstractBiMap.this.updateInverseMap(getKey(), true, oldValue, value);
/* 315 */               return oldValue;
/*     */             }
/*     */           };
/*     */         }
/*     */ 
/*     */         public void remove() {
/* 321 */           Preconditions.checkState(this.entry != null);
/* 322 */           Object value = this.entry.getValue();
/* 323 */           iterator.remove();
/* 324 */           AbstractBiMap.this.removeFromInverseMap(value);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 332 */       return standardToArray();
/*     */     }
/*     */     public <T> T[] toArray(T[] array) {
/* 335 */       return standardToArray(array);
/*     */     }
/*     */     public boolean contains(Object o) {
/* 338 */       return Maps.containsEntryImpl(delegate(), o);
/*     */     }
/*     */     public boolean containsAll(Collection<?> c) {
/* 341 */       return standardContainsAll(c);
/*     */     }
/*     */     public boolean removeAll(Collection<?> c) {
/* 344 */       return standardRemoveAll(c);
/*     */     }
/*     */     public boolean retainAll(Collection<?> c) {
/* 347 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ValueSet extends ForwardingSet<V>
/*     */   {
/* 226 */     final Set<V> valuesDelegate = AbstractBiMap.this.inverse.keySet();
/*     */ 
/*     */     private ValueSet() {  } 
/* 229 */     protected Set<V> delegate() { return this.valuesDelegate; }
/*     */ 
/*     */     public Iterator<V> iterator()
/*     */     {
/* 233 */       return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
/*     */     }
/*     */ 
/*     */     public Object[] toArray() {
/* 237 */       return standardToArray();
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 241 */       return standardToArray(array);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 245 */       return standardToString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class KeySet extends ForwardingSet<K>
/*     */   {
/*     */     private KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected Set<K> delegate()
/*     */     {
/* 186 */       return AbstractBiMap.this.delegate.keySet();
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 190 */       AbstractBiMap.this.clear();
/*     */     }
/*     */ 
/*     */     public boolean remove(Object key) {
/* 194 */       if (!contains(key)) {
/* 195 */         return false;
/*     */       }
/* 197 */       AbstractBiMap.this.removeFromBothMaps(key);
/* 198 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean removeAll(Collection<?> keysToRemove) {
/* 202 */       return standardRemoveAll(keysToRemove);
/*     */     }
/*     */ 
/*     */     public boolean retainAll(Collection<?> keysToRetain) {
/* 206 */       return standardRetainAll(keysToRetain);
/*     */     }
/*     */ 
/*     */     public Iterator<K> iterator() {
/* 210 */       return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractBiMap
 * JD-Core Version:    0.6.2
 */