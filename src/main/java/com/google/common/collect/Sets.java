/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import com.google.common.base.Predicates;
/*      */ import com.google.common.math.IntMath;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NavigableSet;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.CopyOnWriteArraySet;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Sets
/*      */ {
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(E anElement, E[] otherElements)
/*      */   {
/*   85 */     return new ImmutableEnumSet(EnumSet.of(anElement, otherElements));
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> elements)
/*      */   {
/*  103 */     Iterator iterator = elements.iterator();
/*  104 */     if (!iterator.hasNext()) {
/*  105 */       return ImmutableSet.of();
/*      */     }
/*  107 */     if ((elements instanceof EnumSet)) {
/*  108 */       EnumSet enumSetClone = EnumSet.copyOf((EnumSet)elements);
/*  109 */       return new ImmutableEnumSet(enumSetClone);
/*      */     }
/*  111 */     Enum first = (Enum)iterator.next();
/*  112 */     EnumSet set = EnumSet.of(first);
/*  113 */     while (iterator.hasNext()) {
/*  114 */       set.add(iterator.next());
/*      */     }
/*  116 */     return new ImmutableEnumSet(set);
/*      */   }
/*      */ 
/*      */   public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType)
/*      */   {
/*  140 */     Preconditions.checkNotNull(iterable);
/*  141 */     EnumSet set = EnumSet.noneOf(elementType);
/*  142 */     Iterables.addAll(set, iterable);
/*  143 */     return set;
/*      */   }
/*      */ 
/*      */   public static <E> HashSet<E> newHashSet()
/*      */   {
/*  160 */     return new HashSet();
/*      */   }
/*      */ 
/*      */   public static <E> HashSet<E> newHashSet(E[] elements)
/*      */   {
/*  178 */     HashSet set = newHashSetWithExpectedSize(elements.length);
/*  179 */     Collections.addAll(set, elements);
/*  180 */     return set;
/*      */   }
/*      */ 
/*      */   public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize)
/*      */   {
/*  197 */     return new HashSet(Maps.capacity(expectedSize));
/*      */   }
/*      */ 
/*      */   public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements)
/*      */   {
/*  214 */     return (elements instanceof Collection) ? new HashSet(Collections2.cast(elements)) : newHashSet(elements.iterator());
/*      */   }
/*      */ 
/*      */   public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements)
/*      */   {
/*  233 */     HashSet set = newHashSet();
/*  234 */     while (elements.hasNext()) {
/*  235 */       set.add(elements.next());
/*      */     }
/*  237 */     return set;
/*      */   }
/*      */ 
/*      */   public static <E> LinkedHashSet<E> newLinkedHashSet()
/*      */   {
/*  251 */     return new LinkedHashSet();
/*      */   }
/*      */ 
/*      */   public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize)
/*      */   {
/*  270 */     return new LinkedHashSet(Maps.capacity(expectedSize));
/*      */   }
/*      */ 
/*      */   public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> elements)
/*      */   {
/*  286 */     if ((elements instanceof Collection)) {
/*  287 */       return new LinkedHashSet(Collections2.cast(elements));
/*      */     }
/*  289 */     LinkedHashSet set = newLinkedHashSet();
/*  290 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/*  291 */       set.add(element);
/*      */     }
/*  293 */     return set;
/*      */   }
/*      */ 
/*      */   public static <E extends Comparable> TreeSet<E> newTreeSet()
/*      */   {
/*  308 */     return new TreeSet();
/*      */   }
/*      */ 
/*      */   public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> elements)
/*      */   {
/*  328 */     TreeSet set = newTreeSet();
/*  329 */     for (Comparable element : elements) {
/*  330 */       set.add(element);
/*      */     }
/*  332 */     return set;
/*      */   }
/*      */ 
/*      */   public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator)
/*      */   {
/*  347 */     return new TreeSet((Comparator)Preconditions.checkNotNull(comparator));
/*      */   }
/*      */ 
/*      */   public static <E> Set<E> newIdentityHashSet()
/*      */   {
/*  361 */     return newSetFromMap(Maps.newIdentityHashMap());
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("CopyOnWriteArraySet")
/*      */   public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet()
/*      */   {
/*  376 */     return new CopyOnWriteArraySet();
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("CopyOnWriteArraySet")
/*      */   public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> elements)
/*      */   {
/*  392 */     Collection elementsCollection = (elements instanceof Collection) ? Collections2.cast(elements) : Lists.newArrayList(elements);
/*      */ 
/*  395 */     return new CopyOnWriteArraySet(elementsCollection);
/*      */   }
/*      */ 
/*      */   public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection)
/*      */   {
/*  415 */     if ((collection instanceof EnumSet)) {
/*  416 */       return EnumSet.complementOf((EnumSet)collection);
/*      */     }
/*  418 */     Preconditions.checkArgument(!collection.isEmpty(), "collection is empty; use the other version of this method");
/*      */ 
/*  420 */     Class type = ((Enum)collection.iterator().next()).getDeclaringClass();
/*  421 */     return makeComplementByHand(collection, type);
/*      */   }
/*      */ 
/*      */   public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection, Class<E> type)
/*      */   {
/*  438 */     Preconditions.checkNotNull(collection);
/*  439 */     return (collection instanceof EnumSet) ? EnumSet.complementOf((EnumSet)collection) : makeComplementByHand(collection, type);
/*      */   }
/*      */ 
/*      */   private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(Collection<E> collection, Class<E> type)
/*      */   {
/*  446 */     EnumSet result = EnumSet.allOf(type);
/*  447 */     result.removeAll(collection);
/*  448 */     return result;
/*      */   }
/*      */ 
/*      */   public static <E> Set<E> newSetFromMap(Map<E, Boolean> map)
/*      */   {
/*  491 */     return new SetFromMap(map);
/*      */   }
/*      */ 
/*      */   public static <E> SetView<E> union(Set<? extends E> set1, final Set<? extends E> set2)
/*      */   {
/*  626 */     Preconditions.checkNotNull(set1, "set1");
/*  627 */     Preconditions.checkNotNull(set2, "set2");
/*      */ 
/*  629 */     final Set set2minus1 = difference(set2, set1);
/*      */ 
/*  631 */     return new SetView(set1) {
/*      */       public int size() {
/*  633 */         return this.val$set1.size() + set2minus1.size();
/*      */       }
/*      */       public boolean isEmpty() {
/*  636 */         return (this.val$set1.isEmpty()) && (set2.isEmpty());
/*      */       }
/*      */       public Iterator<E> iterator() {
/*  639 */         return Iterators.unmodifiableIterator(Iterators.concat(this.val$set1.iterator(), set2minus1.iterator()));
/*      */       }
/*      */ 
/*      */       public boolean contains(Object object) {
/*  643 */         return (this.val$set1.contains(object)) || (set2.contains(object));
/*      */       }
/*      */       public <S extends Set<E>> S copyInto(S set) {
/*  646 */         set.addAll(this.val$set1);
/*  647 */         set.addAll(set2);
/*  648 */         return set;
/*      */       }
/*      */       public ImmutableSet<E> immutableCopy() {
/*  651 */         return new ImmutableSet.Builder().addAll(this.val$set1).addAll(set2).build();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <E> SetView<E> intersection(Set<E> set1, final Set<?> set2)
/*      */   {
/*  685 */     Preconditions.checkNotNull(set1, "set1");
/*  686 */     Preconditions.checkNotNull(set2, "set2");
/*      */ 
/*  688 */     final Predicate inSet2 = Predicates.in(set2);
/*  689 */     return new SetView(set1) {
/*      */       public Iterator<E> iterator() {
/*  691 */         return Iterators.filter(this.val$set1.iterator(), inSet2);
/*      */       }
/*      */       public int size() {
/*  694 */         return Iterators.size(iterator());
/*      */       }
/*      */       public boolean isEmpty() {
/*  697 */         return !iterator().hasNext();
/*      */       }
/*      */       public boolean contains(Object object) {
/*  700 */         return (this.val$set1.contains(object)) && (set2.contains(object));
/*      */       }
/*      */       public boolean containsAll(Collection<?> collection) {
/*  703 */         return (this.val$set1.containsAll(collection)) && (set2.containsAll(collection));
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <E> SetView<E> difference(Set<E> set1, final Set<?> set2)
/*      */   {
/*  722 */     Preconditions.checkNotNull(set1, "set1");
/*  723 */     Preconditions.checkNotNull(set2, "set2");
/*      */ 
/*  725 */     final Predicate notInSet2 = Predicates.not(Predicates.in(set2));
/*  726 */     return new SetView(set1) {
/*      */       public Iterator<E> iterator() {
/*  728 */         return Iterators.filter(this.val$set1.iterator(), notInSet2);
/*      */       }
/*      */       public int size() {
/*  731 */         return Iterators.size(iterator());
/*      */       }
/*      */       public boolean isEmpty() {
/*  734 */         return set2.containsAll(this.val$set1);
/*      */       }
/*      */       public boolean contains(Object element) {
/*  737 */         return (this.val$set1.contains(element)) && (!set2.contains(element));
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <E> SetView<E> symmetricDifference(Set<? extends E> set1, Set<? extends E> set2)
/*      */   {
/*  756 */     Preconditions.checkNotNull(set1, "set1");
/*  757 */     Preconditions.checkNotNull(set2, "set2");
/*      */ 
/*  760 */     return difference(union(set1, set2), intersection(set1, set2));
/*      */   }
/*      */ 
/*      */   public static <E> Set<E> filter(Set<E> unfiltered, Predicate<? super E> predicate)
/*      */   {
/*  792 */     if ((unfiltered instanceof SortedSet)) {
/*  793 */       return filter((SortedSet)unfiltered, predicate);
/*      */     }
/*  795 */     if ((unfiltered instanceof FilteredSet))
/*      */     {
/*  798 */       FilteredSet filtered = (FilteredSet)unfiltered;
/*  799 */       Predicate combinedPredicate = Predicates.and(filtered.predicate, predicate);
/*      */ 
/*  801 */       return new FilteredSet((Set)filtered.unfiltered, combinedPredicate);
/*      */     }
/*      */ 
/*  805 */     return new FilteredSet((Set)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <E> SortedSet<E> filter(SortedSet<E> unfiltered, Predicate<? super E> predicate)
/*      */   {
/*  857 */     if ((unfiltered instanceof FilteredSet))
/*      */     {
/*  860 */       FilteredSet filtered = (FilteredSet)unfiltered;
/*  861 */       Predicate combinedPredicate = Predicates.and(filtered.predicate, predicate);
/*      */ 
/*  863 */       return new FilteredSortedSet((SortedSet)filtered.unfiltered, combinedPredicate);
/*      */     }
/*      */ 
/*  867 */     return new FilteredSortedSet((SortedSet)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
/*      */   }
/*      */ 
/*      */   public static <B> Set<List<B>> cartesianProduct(List<? extends Set<? extends B>> sets)
/*      */   {
/*  972 */     for (Set set : sets) {
/*  973 */       if (set.isEmpty()) {
/*  974 */         return ImmutableSet.of();
/*      */       }
/*      */     }
/*  977 */     CartesianSet cartesianSet = new CartesianSet(sets);
/*  978 */     return cartesianSet;
/*      */   }
/*      */ 
/*      */   public static <B> Set<List<B>> cartesianProduct(Set<? extends B>[] sets)
/*      */   {
/* 1028 */     return cartesianProduct(Arrays.asList(sets));
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=false)
/*      */   public static <E> Set<Set<E>> powerSet(Set<E> set)
/*      */   {
/* 1182 */     ImmutableSet input = ImmutableSet.copyOf(set);
/* 1183 */     Preconditions.checkArgument(input.size() <= 30, "Too many elements to create power set: %s > 30", new Object[] { Integer.valueOf(input.size()) });
/*      */ 
/* 1185 */     return new PowerSet(input);
/*      */   }
/*      */ 
/*      */   static int hashCodeImpl(Set<?> s)
/*      */   {
/* 1282 */     int hashCode = 0;
/* 1283 */     for (Iterator i$ = s.iterator(); i$.hasNext(); ) { Object o = i$.next();
/* 1284 */       hashCode += (o != null ? o.hashCode() : 0);
/*      */     }
/* 1286 */     return hashCode;
/*      */   }
/*      */ 
/*      */   static boolean equalsImpl(Set<?> s, @Nullable Object object)
/*      */   {
/* 1293 */     if (s == object) {
/* 1294 */       return true;
/*      */     }
/* 1296 */     if ((object instanceof Set)) {
/* 1297 */       Set o = (Set)object;
/*      */       try
/*      */       {
/* 1300 */         return (s.size() == o.size()) && (s.containsAll(o));
/*      */       } catch (NullPointerException ignored) {
/* 1302 */         return false;
/*      */       } catch (ClassCastException ignored) {
/* 1304 */         return false;
/*      */       }
/*      */     }
/* 1307 */     return false;
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableSet")
/*      */   public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> set)
/*      */   {
/* 1329 */     if (((set instanceof ImmutableSortedSet)) || ((set instanceof UnmodifiableNavigableSet)))
/*      */     {
/* 1331 */       return set;
/*      */     }
/* 1333 */     return new UnmodifiableNavigableSet(set);
/*      */   }
/*      */ 
/*      */   static boolean removeAllImpl(Set<?> set, Iterator<?> iterator)
/*      */   {
/* 1429 */     boolean changed = false;
/* 1430 */     while (iterator.hasNext()) {
/* 1431 */       changed |= set.remove(iterator.next());
/*      */     }
/* 1433 */     return changed;
/*      */   }
/*      */ 
/*      */   static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
/* 1437 */     if ((collection instanceof Multiset)) {
/* 1438 */       collection = ((Multiset)collection).elementSet();
/*      */     }
/* 1440 */     if (collection.size() < set.size()) {
/* 1441 */       return removeAllImpl(set, collection.iterator());
/*      */     }
/* 1443 */     return Iterators.removeAll(set.iterator(), collection);
/*      */   }
/*      */ 
/*      */   static <T> SortedSet<T> cast(Iterable<T> iterable)
/*      */   {
/* 1585 */     return (SortedSet)iterable;
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableSet")
/*      */   static class DescendingSet<E> extends ForwardingNavigableSet<E>
/*      */   {
/*      */     private final NavigableSet<E> forward;
/*      */ 
/*      */     DescendingSet(NavigableSet<E> forward)
/*      */     {
/* 1452 */       this.forward = forward;
/*      */     }
/*      */ 
/*      */     protected NavigableSet<E> delegate()
/*      */     {
/* 1457 */       return this.forward;
/*      */     }
/*      */ 
/*      */     public E lower(E e)
/*      */     {
/* 1462 */       return this.forward.higher(e);
/*      */     }
/*      */ 
/*      */     public E floor(E e)
/*      */     {
/* 1467 */       return this.forward.ceiling(e);
/*      */     }
/*      */ 
/*      */     public E ceiling(E e)
/*      */     {
/* 1472 */       return this.forward.floor(e);
/*      */     }
/*      */ 
/*      */     public E higher(E e)
/*      */     {
/* 1477 */       return this.forward.lower(e);
/*      */     }
/*      */ 
/*      */     public E pollFirst()
/*      */     {
/* 1482 */       return this.forward.pollLast();
/*      */     }
/*      */ 
/*      */     public E pollLast()
/*      */     {
/* 1487 */       return this.forward.pollFirst();
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> descendingSet()
/*      */     {
/* 1492 */       return this.forward;
/*      */     }
/*      */ 
/*      */     public Iterator<E> descendingIterator()
/*      */     {
/* 1497 */       return this.forward.iterator();
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*      */     {
/* 1506 */       return this.forward.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> headSet(E toElement, boolean inclusive)
/*      */     {
/* 1511 */       return this.forward.tailSet(toElement, inclusive).descendingSet();
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
/*      */     {
/* 1516 */       return this.forward.headSet(fromElement, inclusive).descendingSet();
/*      */     }
/*      */ 
/*      */     public Comparator<? super E> comparator()
/*      */     {
/* 1522 */       Comparator forwardComparator = this.forward.comparator();
/* 1523 */       if (forwardComparator == null) {
/* 1524 */         return Ordering.natural().reverse();
/*      */       }
/* 1526 */       return reverse(forwardComparator);
/*      */     }
/*      */ 
/*      */     private static <T> Ordering<T> reverse(Comparator<T> forward)
/*      */     {
/* 1532 */       return Ordering.from(forward).reverse();
/*      */     }
/*      */ 
/*      */     public E first()
/*      */     {
/* 1537 */       return this.forward.last();
/*      */     }
/*      */ 
/*      */     public SortedSet<E> headSet(E toElement)
/*      */     {
/* 1542 */       return standardHeadSet(toElement);
/*      */     }
/*      */ 
/*      */     public E last()
/*      */     {
/* 1547 */       return this.forward.first();
/*      */     }
/*      */ 
/*      */     public SortedSet<E> subSet(E fromElement, E toElement)
/*      */     {
/* 1552 */       return standardSubSet(fromElement, toElement);
/*      */     }
/*      */ 
/*      */     public SortedSet<E> tailSet(E fromElement)
/*      */     {
/* 1557 */       return standardTailSet(fromElement);
/*      */     }
/*      */ 
/*      */     public Iterator<E> iterator()
/*      */     {
/* 1562 */       return this.forward.descendingIterator();
/*      */     }
/*      */ 
/*      */     public Object[] toArray()
/*      */     {
/* 1567 */       return standardToArray();
/*      */     }
/*      */ 
/*      */     public <T> T[] toArray(T[] array)
/*      */     {
/* 1572 */       return standardToArray(array);
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1577 */       return standardToString();
/*      */     }
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableSet")
/*      */   static final class UnmodifiableNavigableSet<E> extends ForwardingSortedSet<E>
/*      */     implements NavigableSet<E>, Serializable
/*      */   {
/*      */     private final NavigableSet<E> delegate;
/*      */     private transient UnmodifiableNavigableSet<E> descendingSet;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     UnmodifiableNavigableSet(NavigableSet<E> delegate)
/*      */     {
/* 1342 */       this.delegate = ((NavigableSet)Preconditions.checkNotNull(delegate));
/*      */     }
/*      */ 
/*      */     protected SortedSet<E> delegate()
/*      */     {
/* 1347 */       return Collections.unmodifiableSortedSet(this.delegate);
/*      */     }
/*      */ 
/*      */     public E lower(E e)
/*      */     {
/* 1352 */       return this.delegate.lower(e);
/*      */     }
/*      */ 
/*      */     public E floor(E e)
/*      */     {
/* 1357 */       return this.delegate.floor(e);
/*      */     }
/*      */ 
/*      */     public E ceiling(E e)
/*      */     {
/* 1362 */       return this.delegate.ceiling(e);
/*      */     }
/*      */ 
/*      */     public E higher(E e)
/*      */     {
/* 1367 */       return this.delegate.higher(e);
/*      */     }
/*      */ 
/*      */     public E pollFirst()
/*      */     {
/* 1372 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public E pollLast()
/*      */     {
/* 1377 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> descendingSet()
/*      */     {
/* 1384 */       UnmodifiableNavigableSet result = this.descendingSet;
/* 1385 */       if (result == null) {
/* 1386 */         result = this.descendingSet = new UnmodifiableNavigableSet(this.delegate.descendingSet());
/*      */ 
/* 1388 */         result.descendingSet = this;
/*      */       }
/* 1390 */       return result;
/*      */     }
/*      */ 
/*      */     public Iterator<E> descendingIterator()
/*      */     {
/* 1395 */       return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*      */     {
/* 1404 */       return Sets.unmodifiableNavigableSet(this.delegate.subSet(fromElement, fromInclusive, toElement, toInclusive));
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> headSet(E toElement, boolean inclusive)
/*      */     {
/* 1413 */       return Sets.unmodifiableNavigableSet(this.delegate.headSet(toElement, inclusive));
/*      */     }
/*      */ 
/*      */     public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
/*      */     {
/* 1418 */       return Sets.unmodifiableNavigableSet(this.delegate.tailSet(fromElement, inclusive));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class PowerSet<E> extends AbstractSet<Set<E>>
/*      */   {
/*      */     final ImmutableSet<E> inputSet;
/*      */     final ImmutableList<E> inputList;
/*      */     final int powerSetSize;
/*      */ 
/*      */     PowerSet(ImmutableSet<E> input)
/*      */     {
/* 1194 */       this.inputSet = input;
/* 1195 */       this.inputList = input.asList();
/* 1196 */       this.powerSetSize = (1 << input.size());
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1200 */       return this.powerSetSize;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 1204 */       return false;
/*      */     }
/*      */ 
/*      */     public Iterator<Set<E>> iterator() {
/* 1208 */       return new AbstractIndexedListIterator(this.powerSetSize) {
/*      */         protected Set<E> get(final int setBits) {
/* 1210 */           return new AbstractSet() {
/*      */             public int size() {
/* 1212 */               return Integer.bitCount(setBits);
/*      */             }
/*      */             public Iterator<E> iterator() {
/* 1215 */               return new Sets.PowerSet.BitFilteredSetIterator(Sets.PowerSet.this.inputList, setBits);
/*      */             }
/*      */           };
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object obj)
/*      */     {
/* 1249 */       if ((obj instanceof Set)) {
/* 1250 */         Set set = (Set)obj;
/* 1251 */         return this.inputSet.containsAll(set);
/*      */       }
/* 1253 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object obj) {
/* 1257 */       if ((obj instanceof PowerSet)) {
/* 1258 */         PowerSet that = (PowerSet)obj;
/* 1259 */         return this.inputSet.equals(that.inputSet);
/*      */       }
/* 1261 */       return super.equals(obj);
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1270 */       return this.inputSet.hashCode() << this.inputSet.size() - 1;
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1274 */       return "powerSet(" + this.inputSet + ")";
/*      */     }
/*      */ 
/*      */     private static final class BitFilteredSetIterator<E> extends UnmodifiableIterator<E>
/*      */     {
/*      */       final ImmutableList<E> input;
/*      */       int remainingSetBits;
/*      */ 
/*      */       BitFilteredSetIterator(ImmutableList<E> input, int allSetBits)
/*      */       {
/* 1228 */         this.input = input;
/* 1229 */         this.remainingSetBits = allSetBits;
/*      */       }
/*      */ 
/*      */       public boolean hasNext() {
/* 1233 */         return this.remainingSetBits != 0;
/*      */       }
/*      */ 
/*      */       public E next() {
/* 1237 */         int index = Integer.numberOfTrailingZeros(this.remainingSetBits);
/* 1238 */         if (index == 32) {
/* 1239 */           throw new NoSuchElementException();
/*      */         }
/*      */ 
/* 1242 */         int currentElementMask = 1 << index;
/* 1243 */         this.remainingSetBits &= (currentElementMask ^ 0xFFFFFFFF);
/* 1244 */         return this.input.get(index);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CartesianSet<B> extends AbstractSet<List<B>>
/*      */   {
/*      */     final ImmutableList<CartesianSet<B>.Axis> axes;
/*      */     final int size;
/*      */ 
/*      */     CartesianSet(List<? extends Set<? extends B>> sets)
/*      */     {
/* 1036 */       int dividend = 1;
/* 1037 */       ImmutableList.Builder builder = ImmutableList.builder();
/*      */       try {
/* 1039 */         for (Set set : sets) {
/* 1040 */           Axis axis = new Axis(set, dividend);
/* 1041 */           builder.add(axis);
/* 1042 */           dividend = IntMath.checkedMultiply(dividend, axis.size());
/*      */         }
/*      */       } catch (ArithmeticException overflow) {
/* 1045 */         throw new IllegalArgumentException("cartesian product too big");
/*      */       }
/* 1047 */       this.axes = builder.build();
/* 1048 */       this.size = dividend;
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1052 */       return this.size;
/*      */     }
/*      */ 
/*      */     public UnmodifiableIterator<List<B>> iterator() {
/* 1056 */       return new AbstractIndexedListIterator(this.size)
/*      */       {
/*      */         protected List<B> get(int index) {
/* 1059 */           Object[] tuple = new Object[Sets.CartesianSet.this.axes.size()];
/* 1060 */           for (int i = 0; i < tuple.length; i++) {
/* 1061 */             tuple[i] = ((Sets.CartesianSet.Axis)Sets.CartesianSet.this.axes.get(i)).getForIndex(index);
/*      */           }
/*      */ 
/* 1065 */           List result = ImmutableList.copyOf(tuple);
/* 1066 */           return result;
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean contains(Object element) {
/* 1072 */       if (!(element instanceof List)) {
/* 1073 */         return false;
/*      */       }
/* 1075 */       List tuple = (List)element;
/* 1076 */       int dimensions = this.axes.size();
/* 1077 */       if (tuple.size() != dimensions) {
/* 1078 */         return false;
/*      */       }
/* 1080 */       for (int i = 0; i < dimensions; i++) {
/* 1081 */         if (!((Axis)this.axes.get(i)).contains(tuple.get(i))) {
/* 1082 */           return false;
/*      */         }
/*      */       }
/* 1085 */       return true;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/* 1091 */       if ((object instanceof CartesianSet)) {
/* 1092 */         CartesianSet that = (CartesianSet)object;
/* 1093 */         return this.axes.equals(that.axes);
/*      */       }
/* 1095 */       return super.equals(object);
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1103 */       int adjust = this.size - 1;
/* 1104 */       for (int i = 0; i < this.axes.size(); i++) {
/* 1105 */         adjust *= 31;
/*      */       }
/* 1107 */       return this.axes.hashCode() + adjust;
/*      */     }
/*      */     private class Axis {
/*      */       final ImmutableSet<? extends B> choices;
/*      */       final ImmutableList<? extends B> choicesList;
/*      */       final int dividend;
/*      */ 
/* 1116 */       Axis(int set) { this.choices = ImmutableSet.copyOf(set);
/* 1117 */         this.choicesList = this.choices.asList();
/* 1118 */         this.dividend = dividend; }
/*      */ 
/*      */       int size()
/*      */       {
/* 1122 */         return this.choices.size();
/*      */       }
/*      */ 
/*      */       B getForIndex(int index) {
/* 1126 */         return this.choicesList.get(index / this.dividend % size());
/*      */       }
/*      */ 
/*      */       boolean contains(Object target) {
/* 1130 */         return this.choices.contains(target);
/*      */       }
/*      */ 
/*      */       public boolean equals(Object obj) {
/* 1134 */         if ((obj instanceof Axis)) {
/* 1135 */           Axis that = (Axis)obj;
/* 1136 */           return this.choices.equals(that.choices);
/*      */         }
/*      */ 
/* 1139 */         return false;
/*      */       }
/*      */ 
/*      */       public int hashCode()
/*      */       {
/* 1146 */         return Sets.CartesianSet.this.size / this.choices.size() * this.choices.hashCode();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FilteredSortedSet<E> extends Collections2.FilteredCollection<E>
/*      */     implements SortedSet<E>
/*      */   {
/*      */     FilteredSortedSet(SortedSet<E> unfiltered, Predicate<? super E> predicate)
/*      */     {
/*  875 */       super(predicate);
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/*  879 */       return Sets.equalsImpl(this, object);
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  883 */       return Sets.hashCodeImpl(this);
/*      */     }
/*      */ 
/*      */     public Comparator<? super E> comparator()
/*      */     {
/*  888 */       return ((SortedSet)this.unfiltered).comparator();
/*      */     }
/*      */ 
/*      */     public SortedSet<E> subSet(E fromElement, E toElement)
/*      */     {
/*  893 */       return new FilteredSortedSet(((SortedSet)this.unfiltered).subSet(fromElement, toElement), this.predicate);
/*      */     }
/*      */ 
/*      */     public SortedSet<E> headSet(E toElement)
/*      */     {
/*  899 */       return new FilteredSortedSet(((SortedSet)this.unfiltered).headSet(toElement), this.predicate);
/*      */     }
/*      */ 
/*      */     public SortedSet<E> tailSet(E fromElement)
/*      */     {
/*  904 */       return new FilteredSortedSet(((SortedSet)this.unfiltered).tailSet(fromElement), this.predicate);
/*      */     }
/*      */ 
/*      */     public E first()
/*      */     {
/*  909 */       return iterator().next();
/*      */     }
/*      */ 
/*      */     public E last()
/*      */     {
/*  914 */       SortedSet sortedUnfiltered = (SortedSet)this.unfiltered;
/*      */       while (true) {
/*  916 */         Object element = sortedUnfiltered.last();
/*  917 */         if (this.predicate.apply(element)) {
/*  918 */           return element;
/*      */         }
/*  920 */         sortedUnfiltered = sortedUnfiltered.headSet(element);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FilteredSet<E> extends Collections2.FilteredCollection<E>
/*      */     implements Set<E>
/*      */   {
/*      */     FilteredSet(Set<E> unfiltered, Predicate<? super E> predicate)
/*      */     {
/*  812 */       super(predicate);
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/*  816 */       return Sets.equalsImpl(this, object);
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  820 */       return Sets.hashCodeImpl(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class SetView<E> extends AbstractSet<E>
/*      */   {
/*      */     public ImmutableSet<E> immutableCopy()
/*      */     {
/*  586 */       return ImmutableSet.copyOf(this);
/*      */     }
/*      */ 
/*      */     public <S extends Set<E>> S copyInto(S set)
/*      */     {
/*  599 */       set.addAll(this);
/*  600 */       return set;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SetFromMap<E> extends AbstractSet<E>
/*      */     implements Set<E>, Serializable
/*      */   {
/*      */     private final Map<E, Boolean> m;
/*      */     private transient Set<E> s;
/*      */ 
/*      */     @GwtIncompatible("not needed in emulated source")
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     SetFromMap(Map<E, Boolean> map)
/*      */     {
/*  500 */       Preconditions.checkArgument(map.isEmpty(), "Map is non-empty");
/*  501 */       this.m = map;
/*  502 */       this.s = map.keySet();
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  506 */       this.m.clear();
/*      */     }
/*      */     public int size() {
/*  509 */       return this.m.size();
/*      */     }
/*      */     public boolean isEmpty() {
/*  512 */       return this.m.isEmpty();
/*      */     }
/*      */     public boolean contains(Object o) {
/*  515 */       return this.m.containsKey(o);
/*      */     }
/*      */     public boolean remove(Object o) {
/*  518 */       return this.m.remove(o) != null;
/*      */     }
/*      */     public boolean add(E e) {
/*  521 */       return this.m.put(e, Boolean.TRUE) == null;
/*      */     }
/*      */     public Iterator<E> iterator() {
/*  524 */       return this.s.iterator();
/*      */     }
/*      */     public Object[] toArray() {
/*  527 */       return this.s.toArray();
/*      */     }
/*      */     public <T> T[] toArray(T[] a) {
/*  530 */       return this.s.toArray(a);
/*      */     }
/*      */     public String toString() {
/*  533 */       return this.s.toString();
/*      */     }
/*      */     public int hashCode() {
/*  536 */       return this.s.hashCode();
/*      */     }
/*      */     public boolean equals(@Nullable Object object) {
/*  539 */       return (this == object) || (this.s.equals(object));
/*      */     }
/*      */     public boolean containsAll(Collection<?> c) {
/*  542 */       return this.s.containsAll(c);
/*      */     }
/*      */     public boolean removeAll(Collection<?> c) {
/*  545 */       return this.s.removeAll(c);
/*      */     }
/*      */     public boolean retainAll(Collection<?> c) {
/*  548 */       return this.s.retainAll(c);
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  558 */       stream.defaultReadObject();
/*  559 */       this.s = this.m.keySet();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Sets
 * JD-Core Version:    0.6.2
 */