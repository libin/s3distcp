/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V>
/*     */   implements SortedMap<K, V>
/*     */ {
/*     */   protected abstract SortedMap<K, V> delegate();
/*     */ 
/*     */   public Comparator<? super K> comparator()
/*     */   {
/*  66 */     return delegate().comparator();
/*     */   }
/*     */ 
/*     */   public K firstKey()
/*     */   {
/*  71 */     return delegate().firstKey();
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> headMap(K toKey)
/*     */   {
/*  76 */     return delegate().headMap(toKey);
/*     */   }
/*     */ 
/*     */   public K lastKey()
/*     */   {
/*  81 */     return delegate().lastKey();
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> subMap(K fromKey, K toKey)
/*     */   {
/*  86 */     return delegate().subMap(fromKey, toKey);
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> tailMap(K fromKey)
/*     */   {
/*  91 */     return delegate().tailMap(fromKey);
/*     */   }
/*     */ 
/*     */   private int unsafeCompare(Object k1, Object k2)
/*     */   {
/*  97 */     Comparator comparator = comparator();
/*  98 */     if (comparator == null) {
/*  99 */       return ((Comparable)k1).compareTo(k2);
/*     */     }
/* 101 */     return comparator.compare(k1, k2);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardContainsKey(@Nullable Object key)
/*     */   {
/*     */     try
/*     */     {
/* 117 */       SortedMap self = this;
/* 118 */       Object ceilingKey = self.tailMap(key).firstKey();
/* 119 */       return unsafeCompare(ceilingKey, key) == 0;
/*     */     } catch (ClassCastException e) {
/* 121 */       return false;
/*     */     } catch (NoSuchElementException e) {
/* 123 */       return false; } catch (NullPointerException e) {
/*     */     }
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected V standardRemove(@Nullable Object key)
/*     */   {
/*     */     try
/*     */     {
/* 141 */       SortedMap self = this;
/* 142 */       Iterator entryIterator = self.tailMap(key).entrySet().iterator();
/*     */ 
/* 144 */       if (entryIterator.hasNext()) {
/* 145 */         Map.Entry ceilingEntry = (Map.Entry)entryIterator.next();
/* 146 */         if (unsafeCompare(ceilingEntry.getKey(), key) == 0) {
/* 147 */           Object value = ceilingEntry.getValue();
/* 148 */           entryIterator.remove();
/* 149 */           return value;
/*     */         }
/*     */       }
/*     */     } catch (ClassCastException e) {
/* 153 */       return null;
/*     */     } catch (NullPointerException e) {
/* 155 */       return null;
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected SortedMap<K, V> standardSubMap(K fromKey, K toKey)
/*     */   {
/* 169 */     return tailMap(fromKey).headMap(toKey);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingSortedMap
 * JD-Core Version:    0.6.2
 */