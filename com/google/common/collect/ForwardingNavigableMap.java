/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V>
/*     */   implements NavigableMap<K, V>
/*     */ {
/*     */   protected abstract NavigableMap<K, V> delegate();
/*     */ 
/*     */   public Map.Entry<K, V> lowerEntry(K key)
/*     */   {
/*  65 */     return delegate().lowerEntry(key);
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardLowerEntry(K key)
/*     */   {
/*  74 */     return headMap(key, false).lastEntry();
/*     */   }
/*     */ 
/*     */   public K lowerKey(K key)
/*     */   {
/*  79 */     return delegate().lowerKey(key);
/*     */   }
/*     */ 
/*     */   protected K standardLowerKey(K key)
/*     */   {
/*  88 */     return Maps.keyOrNull(lowerEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> floorEntry(K key)
/*     */   {
/*  93 */     return delegate().floorEntry(key);
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardFloorEntry(K key)
/*     */   {
/* 102 */     return headMap(key, true).lastEntry();
/*     */   }
/*     */ 
/*     */   public K floorKey(K key)
/*     */   {
/* 107 */     return delegate().floorKey(key);
/*     */   }
/*     */ 
/*     */   protected K standardFloorKey(K key)
/*     */   {
/* 116 */     return Maps.keyOrNull(floorEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> ceilingEntry(K key)
/*     */   {
/* 121 */     return delegate().ceilingEntry(key);
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardCeilingEntry(K key)
/*     */   {
/* 130 */     return tailMap(key, true).firstEntry();
/*     */   }
/*     */ 
/*     */   public K ceilingKey(K key)
/*     */   {
/* 135 */     return delegate().ceilingKey(key);
/*     */   }
/*     */ 
/*     */   protected K standardCeilingKey(K key)
/*     */   {
/* 144 */     return Maps.keyOrNull(ceilingEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> higherEntry(K key)
/*     */   {
/* 149 */     return delegate().higherEntry(key);
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardHigherEntry(K key)
/*     */   {
/* 158 */     return tailMap(key, false).firstEntry();
/*     */   }
/*     */ 
/*     */   public K higherKey(K key)
/*     */   {
/* 163 */     return delegate().higherKey(key);
/*     */   }
/*     */ 
/*     */   protected K standardHigherKey(K key)
/*     */   {
/* 172 */     return Maps.keyOrNull(higherEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> firstEntry()
/*     */   {
/* 177 */     return delegate().firstEntry();
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardFirstEntry()
/*     */   {
/* 186 */     return (Map.Entry)Iterables.getFirst(entrySet(), null);
/*     */   }
/*     */ 
/*     */   protected K standardFirstKey()
/*     */   {
/* 195 */     Map.Entry entry = firstEntry();
/* 196 */     if (entry == null) {
/* 197 */       throw new NoSuchElementException();
/*     */     }
/* 199 */     return entry.getKey();
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> lastEntry()
/*     */   {
/* 205 */     return delegate().lastEntry();
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardLastEntry()
/*     */   {
/* 214 */     return (Map.Entry)Iterables.getFirst(descendingMap().entrySet(), null);
/*     */   }
/*     */ 
/*     */   protected K standardLastKey()
/*     */   {
/* 222 */     Map.Entry entry = lastEntry();
/* 223 */     if (entry == null) {
/* 224 */       throw new NoSuchElementException();
/*     */     }
/* 226 */     return entry.getKey();
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> pollFirstEntry()
/*     */   {
/* 232 */     return delegate().pollFirstEntry();
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardPollFirstEntry()
/*     */   {
/* 241 */     return (Map.Entry)poll(entrySet().iterator());
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> pollLastEntry()
/*     */   {
/* 246 */     return delegate().pollLastEntry();
/*     */   }
/*     */ 
/*     */   protected Map.Entry<K, V> standardPollLastEntry()
/*     */   {
/* 255 */     return (Map.Entry)poll(descendingMap().entrySet().iterator());
/*     */   }
/*     */ 
/*     */   public NavigableMap<K, V> descendingMap()
/*     */   {
/* 260 */     return delegate().descendingMap();
/*     */   }
/*     */ 
/*     */   public NavigableSet<K> navigableKeySet()
/*     */   {
/* 321 */     return delegate().navigableKeySet();
/*     */   }
/*     */ 
/*     */   public NavigableSet<K> descendingKeySet()
/*     */   {
/* 345 */     return delegate().descendingKeySet();
/*     */   }
/*     */ 
/*     */   protected NavigableSet<K> standardDescendingKeySet()
/*     */   {
/* 356 */     return descendingMap().navigableKeySet();
/*     */   }
/*     */ 
/*     */   protected SortedMap<K, V> standardSubMap(K fromKey, K toKey)
/*     */   {
/* 367 */     return subMap(fromKey, true, toKey, false);
/*     */   }
/*     */ 
/*     */   public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
/*     */   {
/* 372 */     return delegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
/*     */   }
/*     */ 
/*     */   public NavigableMap<K, V> headMap(K toKey, boolean inclusive)
/*     */   {
/* 377 */     return delegate().headMap(toKey, inclusive);
/*     */   }
/*     */ 
/*     */   public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive)
/*     */   {
/* 382 */     return delegate().tailMap(fromKey, inclusive);
/*     */   }
/*     */ 
/*     */   protected SortedMap<K, V> standardHeadMap(K toKey)
/*     */   {
/* 391 */     return headMap(toKey, false);
/*     */   }
/*     */ 
/*     */   protected SortedMap<K, V> standardTailMap(K fromKey)
/*     */   {
/* 400 */     return tailMap(fromKey, true);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private static <T> T poll(Iterator<T> iterator) {
/* 405 */     if (iterator.hasNext()) {
/* 406 */       Object result = iterator.next();
/* 407 */       iterator.remove();
/* 408 */       return result;
/*     */     }
/* 410 */     return null;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected class StandardNavigableKeySet extends Maps.NavigableKeySet<K, V>
/*     */   {
/*     */     public StandardNavigableKeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     NavigableMap<K, V> map()
/*     */     {
/* 339 */       return ForwardingNavigableMap.this;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected class StandardDescendingMap extends Maps.DescendingMap<K, V>
/*     */   {
/*     */     public StandardDescendingMap()
/*     */     {
/*     */     }
/*     */ 
/*     */     NavigableMap<K, V> forward()
/*     */     {
/* 282 */       return ForwardingNavigableMap.this;
/*     */     }
/*     */ 
/*     */     protected Iterator<Map.Entry<K, V>> entryIterator()
/*     */     {
/* 287 */       return new Iterator() {
/* 288 */         private Map.Entry<K, V> toRemove = null;
/* 289 */         private Map.Entry<K, V> nextOrNull = ForwardingNavigableMap.StandardDescendingMap.this.forward().lastEntry();
/*     */ 
/*     */         public boolean hasNext()
/*     */         {
/* 293 */           return this.nextOrNull != null;
/*     */         }
/*     */ 
/*     */         public Map.Entry<K, V> next()
/*     */         {
/* 298 */           if (!hasNext())
/* 299 */             throw new NoSuchElementException();
/*     */           try
/*     */           {
/* 302 */             return this.nextOrNull;
/*     */           } finally {
/* 304 */             this.toRemove = this.nextOrNull;
/* 305 */             this.nextOrNull = ForwardingNavigableMap.StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
/*     */           }
/*     */         }
/*     */ 
/*     */         public void remove()
/*     */         {
/* 311 */           Iterators.checkRemove(this.toRemove != null);
/* 312 */           ForwardingNavigableMap.StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
/* 313 */           this.toRemove = null;
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingNavigableMap
 * JD-Core Version:    0.6.2
 */