/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class RegularImmutableMap<K, V> extends ImmutableMap<K, V>
/*     */ {
/*     */   private final transient LinkedEntry<K, V>[] entries;
/*     */   private final transient LinkedEntry<K, V>[] table;
/*     */   private final transient int mask;
/*     */   private final transient int keySetHashCode;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   RegularImmutableMap(Map.Entry<?, ?>[] immutableEntries)
/*     */   {
/*  47 */     int size = immutableEntries.length;
/*  48 */     this.entries = createEntryArray(size);
/*     */ 
/*  50 */     int tableSize = chooseTableSize(size);
/*  51 */     this.table = createEntryArray(tableSize);
/*  52 */     this.mask = (tableSize - 1);
/*     */ 
/*  54 */     int keySetHashCodeMutable = 0;
/*  55 */     for (int entryIndex = 0; entryIndex < size; entryIndex++)
/*     */     {
/*  58 */       Map.Entry entry = immutableEntries[entryIndex];
/*  59 */       Object key = entry.getKey();
/*  60 */       int keyHashCode = key.hashCode();
/*  61 */       keySetHashCodeMutable += keyHashCode;
/*  62 */       int tableIndex = Hashing.smear(keyHashCode) & this.mask;
/*  63 */       LinkedEntry existing = this.table[tableIndex];
/*     */ 
/*  65 */       LinkedEntry linkedEntry = newLinkedEntry(key, entry.getValue(), existing);
/*     */ 
/*  67 */       this.table[tableIndex] = linkedEntry;
/*  68 */       this.entries[entryIndex] = linkedEntry;
/*  69 */       while (existing != null) {
/*  70 */         Preconditions.checkArgument(!key.equals(existing.getKey()), "duplicate key: %s", new Object[] { key });
/*  71 */         existing = existing.next();
/*     */       }
/*     */     }
/*  74 */     this.keySetHashCode = keySetHashCodeMutable;
/*     */   }
/*     */ 
/*     */   private static int chooseTableSize(int size)
/*     */   {
/*  79 */     int tableSize = Integer.highestOneBit(size) << 1;
/*  80 */     Preconditions.checkArgument(tableSize > 0, "table too large: %s", new Object[] { Integer.valueOf(size) });
/*  81 */     return tableSize;
/*     */   }
/*     */ 
/*     */   private LinkedEntry<K, V>[] createEntryArray(int size)
/*     */   {
/*  91 */     return new LinkedEntry[size];
/*     */   }
/*     */ 
/*     */   private static <K, V> LinkedEntry<K, V> newLinkedEntry(K key, V value, @Nullable LinkedEntry<K, V> next)
/*     */   {
/*  96 */     return (LinkedEntry)(next == null ? new TerminalEntry(key, value) : new NonTerminalEntry(key, value, next));
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object key)
/*     */   {
/* 141 */     if (key == null) {
/* 142 */       return null;
/*     */     }
/* 144 */     int index = Hashing.smear(key.hashCode()) & this.mask;
/* 145 */     for (LinkedEntry entry = this.table[index]; 
/* 146 */       entry != null; 
/* 147 */       entry = entry.next()) {
/* 148 */       Object candidateKey = entry.getKey();
/*     */ 
/* 156 */       if (key.equals(candidateKey)) {
/* 157 */         return entry.getValue();
/*     */       }
/*     */     }
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 165 */     return this.entries.length;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 169 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/* 173 */     if (value == null) {
/* 174 */       return false;
/*     */     }
/* 176 */     for (Map.Entry entry : this.entries) {
/* 177 */       if (entry.getValue().equals(value)) {
/* 178 */         return true;
/*     */       }
/*     */     }
/* 181 */     return false;
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/* 185 */     return false;
/*     */   }
/*     */ 
/*     */   ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */   {
/* 190 */     return new EntrySet(null);
/*     */   }
/*     */ 
/*     */   ImmutableSet<K> createKeySet()
/*     */   {
/* 208 */     return new ImmutableMap.KeySet(this, this.keySetHashCode);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 212 */     StringBuilder result = Collections2.newStringBuilderForCollection(size()).append('{');
/*     */ 
/* 214 */     Collections2.STANDARD_JOINER.appendTo(result, this.entries);
/* 215 */     return '}';
/*     */   }
/*     */ 
/*     */   private class EntrySet extends ImmutableMap.EntrySet
/*     */   {
/*     */     private EntrySet()
/*     */     {
/* 194 */       super();
/*     */     }
/*     */     public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
/* 197 */       return asList().iterator();
/*     */     }
/*     */ 
/*     */     ImmutableList<Map.Entry<K, V>> createAsList()
/*     */     {
/* 202 */       return new RegularImmutableAsList(this, RegularImmutableMap.this.entries);
/*     */     }
/*     */   }
/*     */ 
/*     */   @Immutable
/*     */   private static final class TerminalEntry<K, V> extends ImmutableEntry<K, V>
/*     */     implements RegularImmutableMap.LinkedEntry<K, V>
/*     */   {
/*     */     TerminalEntry(K key, V value)
/*     */     {
/* 132 */       super(value);
/*     */     }
/*     */     @Nullable
/*     */     public RegularImmutableMap.LinkedEntry<K, V> next() {
/* 136 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Immutable
/*     */   private static final class NonTerminalEntry<K, V> extends ImmutableEntry<K, V>
/*     */     implements RegularImmutableMap.LinkedEntry<K, V>
/*     */   {
/*     */     final RegularImmutableMap.LinkedEntry<K, V> next;
/*     */ 
/*     */     NonTerminalEntry(K key, V value, RegularImmutableMap.LinkedEntry<K, V> next)
/*     */     {
/* 114 */       super(value);
/* 115 */       this.next = next;
/*     */     }
/*     */ 
/*     */     public RegularImmutableMap.LinkedEntry<K, V> next() {
/* 119 */       return this.next;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface LinkedEntry<K, V> extends Map.Entry<K, V>
/*     */   {
/*     */     @Nullable
/*     */     public abstract LinkedEntry<K, V> next();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularImmutableMap
 * JD-Core Version:    0.6.2
 */