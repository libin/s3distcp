/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ abstract class AbstractMapBasedMultiset<E> extends AbstractMultiset<E>
/*     */   implements Serializable
/*     */ {
/*     */   private transient Map<E, Count> backingMap;
/*     */   private transient long size;
/*     */ 
/*     */   @GwtIncompatible("not needed in emulated source.")
/*     */   private static final long serialVersionUID = -2250766705698539974L;
/*     */ 
/*     */   protected AbstractMapBasedMultiset(Map<E, Count> backingMap)
/*     */   {
/*  62 */     this.backingMap = ((Map)Preconditions.checkNotNull(backingMap));
/*  63 */     this.size = super.size();
/*     */   }
/*     */ 
/*     */   Map<E, Count> backingMap() {
/*  67 */     return this.backingMap;
/*     */   }
/*     */ 
/*     */   void setBackingMap(Map<E, Count> backingMap)
/*     */   {
/*  72 */     this.backingMap = backingMap;
/*     */   }
/*     */ 
/*     */   public Set<Multiset.Entry<E>> entrySet()
/*     */   {
/*  86 */     return super.entrySet();
/*     */   }
/*     */ 
/*     */   Iterator<Multiset.Entry<E>> entryIterator()
/*     */   {
/*  91 */     final Iterator backingEntries = this.backingMap.entrySet().iterator();
/*     */ 
/*  93 */     return new Iterator()
/*     */     {
/*     */       Map.Entry<E, Count> toRemove;
/*     */ 
/*     */       public boolean hasNext() {
/*  98 */         return backingEntries.hasNext();
/*     */       }
/*     */ 
/*     */       public Multiset.Entry<E> next()
/*     */       {
/* 103 */         final Map.Entry mapEntry = (Map.Entry)backingEntries.next();
/* 104 */         this.toRemove = mapEntry;
/* 105 */         return new Multisets.AbstractEntry()
/*     */         {
/*     */           public E getElement() {
/* 108 */             return mapEntry.getKey();
/*     */           }
/*     */ 
/*     */           public int getCount() {
/* 112 */             int count = ((Count)mapEntry.getValue()).get();
/* 113 */             if (count == 0) {
/* 114 */               Count frequency = (Count)AbstractMapBasedMultiset.this.backingMap.get(getElement());
/* 115 */               if (frequency != null) {
/* 116 */                 count = frequency.get();
/*     */               }
/*     */             }
/* 119 */             return count;
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       public void remove()
/*     */       {
/* 126 */         Iterators.checkRemove(this.toRemove != null);
/* 127 */         AbstractMapBasedMultiset.access$122(AbstractMapBasedMultiset.this, ((Count)this.toRemove.getValue()).getAndSet(0));
/* 128 */         backingEntries.remove();
/* 129 */         this.toRemove = null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 136 */     for (Count frequency : this.backingMap.values()) {
/* 137 */       frequency.set(0);
/*     */     }
/* 139 */     this.backingMap.clear();
/* 140 */     this.size = 0L;
/*     */   }
/*     */ 
/*     */   int distinctElements()
/*     */   {
/* 145 */     return this.backingMap.size();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 151 */     return Ints.saturatedCast(this.size);
/*     */   }
/*     */ 
/*     */   public Iterator<E> iterator() {
/* 155 */     return new MapBasedMultisetIterator();
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/*     */     try
/*     */     {
/* 207 */       Count frequency = (Count)this.backingMap.get(element);
/* 208 */       return frequency == null ? 0 : frequency.get();
/*     */     } catch (NullPointerException e) {
/* 210 */       return 0; } catch (ClassCastException e) {
/*     */     }
/* 212 */     return 0;
/*     */   }
/*     */ 
/*     */   public int add(@Nullable E element, int occurrences)
/*     */   {
/* 226 */     if (occurrences == 0) {
/* 227 */       return count(element);
/*     */     }
/* 229 */     Preconditions.checkArgument(occurrences > 0, "occurrences cannot be negative: %s", new Object[] { Integer.valueOf(occurrences) });
/*     */ 
/* 231 */     Count frequency = (Count)this.backingMap.get(element);
/*     */     int oldCount;
/* 233 */     if (frequency == null) {
/* 234 */       int oldCount = 0;
/* 235 */       this.backingMap.put(element, new Count(occurrences));
/*     */     } else {
/* 237 */       oldCount = frequency.get();
/* 238 */       long newCount = oldCount + occurrences;
/* 239 */       Preconditions.checkArgument(newCount <= 2147483647L, "too many occurrences: %s", new Object[] { Long.valueOf(newCount) });
/*     */ 
/* 241 */       frequency.getAndAdd(occurrences);
/*     */     }
/* 243 */     this.size += occurrences;
/* 244 */     return oldCount;
/*     */   }
/*     */ 
/*     */   public int remove(@Nullable Object element, int occurrences) {
/* 248 */     if (occurrences == 0) {
/* 249 */       return count(element);
/*     */     }
/* 251 */     Preconditions.checkArgument(occurrences > 0, "occurrences cannot be negative: %s", new Object[] { Integer.valueOf(occurrences) });
/*     */ 
/* 253 */     Count frequency = (Count)this.backingMap.get(element);
/* 254 */     if (frequency == null) {
/* 255 */       return 0;
/*     */     }
/*     */ 
/* 258 */     int oldCount = frequency.get();
/*     */     int numberRemoved;
/*     */     int numberRemoved;
/* 261 */     if (oldCount > occurrences) {
/* 262 */       numberRemoved = occurrences;
/*     */     } else {
/* 264 */       numberRemoved = oldCount;
/* 265 */       this.backingMap.remove(element);
/*     */     }
/*     */ 
/* 268 */     frequency.addAndGet(-numberRemoved);
/* 269 */     this.size -= numberRemoved;
/* 270 */     return oldCount;
/*     */   }
/*     */ 
/*     */   public int setCount(E element, int count)
/*     */   {
/* 275 */     Multisets.checkNonnegative(count, "count");
/*     */     int oldCount;
/*     */     int oldCount;
/* 279 */     if (count == 0) {
/* 280 */       Count existingCounter = (Count)this.backingMap.remove(element);
/* 281 */       oldCount = getAndSet(existingCounter, count);
/*     */     } else {
/* 283 */       Count existingCounter = (Count)this.backingMap.get(element);
/* 284 */       oldCount = getAndSet(existingCounter, count);
/*     */ 
/* 286 */       if (existingCounter == null) {
/* 287 */         this.backingMap.put(element, new Count(count));
/*     */       }
/*     */     }
/*     */ 
/* 291 */     this.size += count - oldCount;
/* 292 */     return oldCount;
/*     */   }
/*     */ 
/*     */   private static int getAndSet(Count i, int count) {
/* 296 */     if (i == null) {
/* 297 */       return 0;
/*     */     }
/*     */ 
/* 300 */     return i.getAndSet(count);
/*     */   }
/*     */ 
/*     */   Set<E> createElementSet()
/*     */   {
/* 306 */     return new MapBasedElementSet();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectStreamException")
/*     */   private void readObjectNoData()
/*     */     throws ObjectStreamException
/*     */   {
/* 320 */     throw new InvalidObjectException("Stream data required");
/*     */   }
/*     */ 
/*     */   class MapBasedElementSet extends Multisets.ElementSet<E>
/*     */   {
/*     */     MapBasedElementSet()
/*     */     {
/*     */     }
/*     */ 
/*     */     Multiset<E> multiset()
/*     */     {
/* 312 */       return AbstractMapBasedMultiset.this;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MapBasedMultisetIterator
/*     */     implements Iterator<E>
/*     */   {
/*     */     final Iterator<Map.Entry<E, Count>> entryIterator;
/*     */     Map.Entry<E, Count> currentEntry;
/*     */     int occurrencesLeft;
/*     */     boolean canRemove;
/*     */ 
/*     */     MapBasedMultisetIterator()
/*     */     {
/* 170 */       this.entryIterator = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 175 */       return (this.occurrencesLeft > 0) || (this.entryIterator.hasNext());
/*     */     }
/*     */ 
/*     */     public E next()
/*     */     {
/* 180 */       if (this.occurrencesLeft == 0) {
/* 181 */         this.currentEntry = ((Map.Entry)this.entryIterator.next());
/* 182 */         this.occurrencesLeft = ((Count)this.currentEntry.getValue()).get();
/*     */       }
/* 184 */       this.occurrencesLeft -= 1;
/* 185 */       this.canRemove = true;
/* 186 */       return this.currentEntry.getKey();
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 191 */       Preconditions.checkState(this.canRemove, "no calls to next() since the last call to remove()");
/*     */ 
/* 193 */       int frequency = ((Count)this.currentEntry.getValue()).get();
/* 194 */       if (frequency <= 0) {
/* 195 */         throw new ConcurrentModificationException();
/*     */       }
/* 197 */       if (((Count)this.currentEntry.getValue()).addAndGet(-1) == 0) {
/* 198 */         this.entryIterator.remove();
/*     */       }
/* 200 */       AbstractMapBasedMultiset.access$110(AbstractMapBasedMultiset.this);
/* 201 */       this.canRemove = false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractMapBasedMultiset
 * JD-Core Version:    0.6.2
 */