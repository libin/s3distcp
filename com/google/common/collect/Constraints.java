/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.RandomAccess;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class Constraints
/*     */ {
/*     */   public static <E> Constraint<E> notNull()
/*     */   {
/*  65 */     return NotNullConstraint.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <E> Collection<E> constrainedCollection(Collection<E> collection, Constraint<? super E> constraint)
/*     */   {
/*  82 */     return new ConstrainedCollection(collection, constraint);
/*     */   }
/*     */ 
/*     */   public static <E> Set<E> constrainedSet(Set<E> set, Constraint<? super E> constraint)
/*     */   {
/* 121 */     return new ConstrainedSet(set, constraint);
/*     */   }
/*     */ 
/*     */   public static <E> SortedSet<E> constrainedSortedSet(SortedSet<E> sortedSet, Constraint<? super E> constraint)
/*     */   {
/* 159 */     return new ConstrainedSortedSet(sortedSet, constraint);
/*     */   }
/*     */ 
/*     */   public static <E> List<E> constrainedList(List<E> list, Constraint<? super E> constraint)
/*     */   {
/* 209 */     return (list instanceof RandomAccess) ? new ConstrainedRandomAccessList(list, constraint) : new ConstrainedList(list, constraint);
/*     */   }
/*     */ 
/*     */   private static <E> ListIterator<E> constrainedListIterator(ListIterator<E> listIterator, Constraint<? super E> constraint)
/*     */   {
/* 279 */     return new ConstrainedListIterator(listIterator, constraint);
/*     */   }
/*     */ 
/*     */   static <E> Collection<E> constrainedTypePreservingCollection(Collection<E> collection, Constraint<E> constraint)
/*     */   {
/* 308 */     if ((collection instanceof SortedSet))
/* 309 */       return constrainedSortedSet((SortedSet)collection, constraint);
/* 310 */     if ((collection instanceof Set))
/* 311 */       return constrainedSet((Set)collection, constraint);
/* 312 */     if ((collection instanceof List)) {
/* 313 */       return constrainedList((List)collection, constraint);
/*     */     }
/* 315 */     return constrainedCollection(collection, constraint);
/*     */   }
/*     */ 
/*     */   public static <E> Multiset<E> constrainedMultiset(Multiset<E> multiset, Constraint<? super E> constraint)
/*     */   {
/* 333 */     return new ConstrainedMultiset(multiset, constraint);
/*     */   }
/*     */ 
/*     */   private static <E> Collection<E> checkElements(Collection<E> elements, Constraint<? super E> constraint)
/*     */   {
/* 376 */     Collection copy = Lists.newArrayList(elements);
/* 377 */     for (Iterator i$ = copy.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 378 */       constraint.checkElement(element);
/*     */     }
/* 380 */     return copy;
/*     */   }
/*     */ 
/*     */   static class ConstrainedMultiset<E> extends ForwardingMultiset<E>
/*     */   {
/*     */     private Multiset<E> delegate;
/*     */     private final Constraint<? super E> constraint;
/*     */ 
/*     */     public ConstrainedMultiset(Multiset<E> delegate, Constraint<? super E> constraint)
/*     */     {
/* 343 */       this.delegate = ((Multiset)Preconditions.checkNotNull(delegate));
/* 344 */       this.constraint = ((Constraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     protected Multiset<E> delegate() {
/* 347 */       return this.delegate;
/*     */     }
/*     */     public boolean add(E element) {
/* 350 */       return standardAdd(element);
/*     */     }
/*     */     public boolean addAll(Collection<? extends E> elements) {
/* 353 */       return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
/*     */     }
/*     */     public int add(E element, int occurrences) {
/* 356 */       this.constraint.checkElement(element);
/* 357 */       return this.delegate.add(element, occurrences);
/*     */     }
/*     */     public int setCount(E element, int count) {
/* 360 */       this.constraint.checkElement(element);
/* 361 */       return this.delegate.setCount(element, count);
/*     */     }
/*     */     public boolean setCount(E element, int oldCount, int newCount) {
/* 364 */       this.constraint.checkElement(element);
/* 365 */       return this.delegate.setCount(element, oldCount, newCount);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedListIterator<E> extends ForwardingListIterator<E>
/*     */   {
/*     */     private final ListIterator<E> delegate;
/*     */     private final Constraint<? super E> constraint;
/*     */ 
/*     */     public ConstrainedListIterator(ListIterator<E> delegate, Constraint<? super E> constraint)
/*     */     {
/* 289 */       this.delegate = delegate;
/* 290 */       this.constraint = constraint;
/*     */     }
/*     */     protected ListIterator<E> delegate() {
/* 293 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public void add(E element) {
/* 297 */       this.constraint.checkElement(element);
/* 298 */       this.delegate.add(element);
/*     */     }
/*     */     public void set(E element) {
/* 301 */       this.constraint.checkElement(element);
/* 302 */       this.delegate.set(element);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedRandomAccessList<E> extends Constraints.ConstrainedList<E>
/*     */     implements RandomAccess
/*     */   {
/*     */     ConstrainedRandomAccessList(List<E> delegate, Constraint<? super E> constraint)
/*     */     {
/* 264 */       super(constraint);
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtCompatible
/*     */   private static class ConstrainedList<E> extends ForwardingList<E>
/*     */   {
/*     */     final List<E> delegate;
/*     */     final Constraint<? super E> constraint;
/*     */ 
/*     */     ConstrainedList(List<E> delegate, Constraint<? super E> constraint)
/*     */     {
/* 221 */       this.delegate = ((List)Preconditions.checkNotNull(delegate));
/* 222 */       this.constraint = ((Constraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     protected List<E> delegate() {
/* 225 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public boolean add(E element) {
/* 229 */       this.constraint.checkElement(element);
/* 230 */       return this.delegate.add(element);
/*     */     }
/*     */     public void add(int index, E element) {
/* 233 */       this.constraint.checkElement(element);
/* 234 */       this.delegate.add(index, element);
/*     */     }
/*     */     public boolean addAll(Collection<? extends E> elements) {
/* 237 */       return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
/*     */     }
/*     */ 
/*     */     public boolean addAll(int index, Collection<? extends E> elements) {
/* 241 */       return this.delegate.addAll(index, Constraints.checkElements(elements, this.constraint));
/*     */     }
/*     */     public ListIterator<E> listIterator() {
/* 244 */       return Constraints.constrainedListIterator(this.delegate.listIterator(), this.constraint);
/*     */     }
/*     */     public ListIterator<E> listIterator(int index) {
/* 247 */       return Constraints.constrainedListIterator(this.delegate.listIterator(index), this.constraint);
/*     */     }
/*     */     public E set(int index, E element) {
/* 250 */       this.constraint.checkElement(element);
/* 251 */       return this.delegate.set(index, element);
/*     */     }
/*     */     public List<E> subList(int fromIndex, int toIndex) {
/* 254 */       return Constraints.constrainedList(this.delegate.subList(fromIndex, toIndex), this.constraint);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedSortedSet<E> extends ForwardingSortedSet<E>
/*     */   {
/*     */     final SortedSet<E> delegate;
/*     */     final Constraint<? super E> constraint;
/*     */ 
/*     */     ConstrainedSortedSet(SortedSet<E> delegate, Constraint<? super E> constraint)
/*     */     {
/* 169 */       this.delegate = ((SortedSet)Preconditions.checkNotNull(delegate));
/* 170 */       this.constraint = ((Constraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     protected SortedSet<E> delegate() {
/* 173 */       return this.delegate;
/*     */     }
/*     */     public SortedSet<E> headSet(E toElement) {
/* 176 */       return Constraints.constrainedSortedSet(this.delegate.headSet(toElement), this.constraint);
/*     */     }
/*     */     public SortedSet<E> subSet(E fromElement, E toElement) {
/* 179 */       return Constraints.constrainedSortedSet(this.delegate.subSet(fromElement, toElement), this.constraint);
/*     */     }
/*     */ 
/*     */     public SortedSet<E> tailSet(E fromElement) {
/* 183 */       return Constraints.constrainedSortedSet(this.delegate.tailSet(fromElement), this.constraint);
/*     */     }
/*     */     public boolean add(E element) {
/* 186 */       this.constraint.checkElement(element);
/* 187 */       return this.delegate.add(element);
/*     */     }
/*     */     public boolean addAll(Collection<? extends E> elements) {
/* 190 */       return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedSet<E> extends ForwardingSet<E>
/*     */   {
/*     */     private final Set<E> delegate;
/*     */     private final Constraint<? super E> constraint;
/*     */ 
/*     */     public ConstrainedSet(Set<E> delegate, Constraint<? super E> constraint)
/*     */     {
/* 130 */       this.delegate = ((Set)Preconditions.checkNotNull(delegate));
/* 131 */       this.constraint = ((Constraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     protected Set<E> delegate() {
/* 134 */       return this.delegate;
/*     */     }
/*     */     public boolean add(E element) {
/* 137 */       this.constraint.checkElement(element);
/* 138 */       return this.delegate.add(element);
/*     */     }
/*     */     public boolean addAll(Collection<? extends E> elements) {
/* 141 */       return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedCollection<E> extends ForwardingCollection<E>
/*     */   {
/*     */     private final Collection<E> delegate;
/*     */     private final Constraint<? super E> constraint;
/*     */ 
/*     */     public ConstrainedCollection(Collection<E> delegate, Constraint<? super E> constraint)
/*     */     {
/*  92 */       this.delegate = ((Collection)Preconditions.checkNotNull(delegate));
/*  93 */       this.constraint = ((Constraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     protected Collection<E> delegate() {
/*  96 */       return this.delegate;
/*     */     }
/*     */     public boolean add(E element) {
/*  99 */       this.constraint.checkElement(element);
/* 100 */       return this.delegate.add(element);
/*     */     }
/*     */     public boolean addAll(Collection<? extends E> elements) {
/* 103 */       return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum NotNullConstraint
/*     */     implements Constraint<Object>
/*     */   {
/*  46 */     INSTANCE;
/*     */ 
/*     */     public Object checkElement(Object element)
/*     */     {
/*  50 */       return Preconditions.checkNotNull(element);
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  54 */       return "Not null";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Constraints
 * JD-Core Version:    0.6.2
 */