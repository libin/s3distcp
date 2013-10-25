/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class TreeMultiset<E> extends AbstractSortedMultiset<E>
/*     */   implements Serializable
/*     */ {
/*     */   private final transient Reference<AvlNode<E>> rootReference;
/*     */   private final transient GeneralRange<E> range;
/*     */   private final transient AvlNode<E> header;
/*     */ 
/*     */   @GwtIncompatible("not needed in emulated source")
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public static <E extends Comparable> TreeMultiset<E> create()
/*     */   {
/*  72 */     return new TreeMultiset(Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <E> TreeMultiset<E> create(@Nullable Comparator<? super E> comparator)
/*     */   {
/*  89 */     return comparator == null ? new TreeMultiset(Ordering.natural()) : new TreeMultiset(comparator);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable> TreeMultiset<E> create(Iterable<? extends E> elements)
/*     */   {
/* 104 */     TreeMultiset multiset = create();
/* 105 */     Iterables.addAll(multiset, elements);
/* 106 */     return multiset;
/*     */   }
/*     */ 
/*     */   TreeMultiset(Reference<AvlNode<E>> rootReference, GeneralRange<E> range, AvlNode<E> endLink)
/*     */   {
/* 114 */     super(range.comparator());
/* 115 */     this.rootReference = rootReference;
/* 116 */     this.range = range;
/* 117 */     this.header = endLink;
/*     */   }
/*     */ 
/*     */   TreeMultiset(Comparator<? super E> comparator) {
/* 121 */     super(comparator);
/* 122 */     this.range = GeneralRange.all(comparator);
/* 123 */     this.header = new AvlNode(null, 1);
/* 124 */     successor(this.header, this.header);
/* 125 */     this.rootReference = new Reference(null);
/*     */   }
/*     */ 
/*     */   private long aggregateForEntries(Aggregate aggr)
/*     */   {
/* 160 */     AvlNode root = (AvlNode)this.rootReference.get();
/* 161 */     long total = aggr.treeAggregate(root);
/* 162 */     if (this.range.hasLowerBound()) {
/* 163 */       total -= aggregateBelowRange(aggr, root);
/*     */     }
/* 165 */     if (this.range.hasUpperBound()) {
/* 166 */       total -= aggregateAboveRange(aggr, root);
/*     */     }
/* 168 */     return total;
/*     */   }
/*     */ 
/*     */   private long aggregateBelowRange(Aggregate aggr, @Nullable AvlNode<E> node) {
/* 172 */     if (node == null) {
/* 173 */       return 0L;
/*     */     }
/* 175 */     int cmp = comparator().compare(this.range.getLowerEndpoint(), node.elem);
/* 176 */     if (cmp < 0)
/* 177 */       return aggregateBelowRange(aggr, node.left);
/* 178 */     if (cmp == 0) {
/* 179 */       switch (4.$SwitchMap$com$google$common$collect$BoundType[this.range.getLowerBoundType().ordinal()]) {
/*     */       case 1:
/* 181 */         return aggr.nodeAggregate(node) + aggr.treeAggregate(node.left);
/*     */       case 2:
/* 183 */         return aggr.treeAggregate(node.left);
/*     */       }
/* 185 */       throw new AssertionError();
/*     */     }
/*     */ 
/* 188 */     return aggr.treeAggregate(node.left) + aggr.nodeAggregate(node) + aggregateBelowRange(aggr, node.right);
/*     */   }
/*     */ 
/*     */   private long aggregateAboveRange(Aggregate aggr, @Nullable AvlNode<E> node)
/*     */   {
/* 194 */     if (node == null) {
/* 195 */       return 0L;
/*     */     }
/* 197 */     int cmp = comparator().compare(this.range.getUpperEndpoint(), node.elem);
/* 198 */     if (cmp > 0)
/* 199 */       return aggregateAboveRange(aggr, node.right);
/* 200 */     if (cmp == 0) {
/* 201 */       switch (4.$SwitchMap$com$google$common$collect$BoundType[this.range.getUpperBoundType().ordinal()]) {
/*     */       case 1:
/* 203 */         return aggr.nodeAggregate(node) + aggr.treeAggregate(node.right);
/*     */       case 2:
/* 205 */         return aggr.treeAggregate(node.right);
/*     */       }
/* 207 */       throw new AssertionError();
/*     */     }
/*     */ 
/* 210 */     return aggr.treeAggregate(node.right) + aggr.nodeAggregate(node) + aggregateAboveRange(aggr, node.left);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 217 */     return Ints.saturatedCast(aggregateForEntries(Aggregate.SIZE));
/*     */   }
/*     */ 
/*     */   int distinctElements()
/*     */   {
/* 222 */     return Ints.saturatedCast(aggregateForEntries(Aggregate.DISTINCT));
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/*     */     try
/*     */     {
/* 229 */       Object e = element;
/* 230 */       AvlNode root = (AvlNode)this.rootReference.get();
/* 231 */       if ((!this.range.contains(e)) || (root == null)) {
/* 232 */         return 0;
/*     */       }
/* 234 */       return root.count(comparator(), e);
/*     */     } catch (ClassCastException e) {
/* 236 */       return 0; } catch (NullPointerException e) {
/*     */     }
/* 238 */     return 0;
/*     */   }
/*     */ 
/*     */   public int add(E element, int occurrences)
/*     */   {
/* 244 */     Preconditions.checkArgument(occurrences >= 0, "occurrences must be >= 0 but was %s", new Object[] { Integer.valueOf(occurrences) });
/* 245 */     if (occurrences == 0) {
/* 246 */       return count(element);
/*     */     }
/* 248 */     Preconditions.checkArgument(this.range.contains(element));
/* 249 */     AvlNode root = (AvlNode)this.rootReference.get();
/* 250 */     if (root == null) {
/* 251 */       comparator().compare(element, element);
/* 252 */       AvlNode newRoot = new AvlNode(element, occurrences);
/* 253 */       successor(this.header, newRoot, this.header);
/* 254 */       this.rootReference.checkAndSet(root, newRoot);
/* 255 */       return 0;
/*     */     }
/* 257 */     int[] result = new int[1];
/* 258 */     AvlNode newRoot = root.add(comparator(), element, occurrences, result);
/* 259 */     this.rootReference.checkAndSet(root, newRoot);
/* 260 */     return result[0];
/*     */   }
/*     */ 
/*     */   public int remove(@Nullable Object element, int occurrences)
/*     */   {
/* 265 */     Preconditions.checkArgument(occurrences >= 0, "occurrences must be >= 0 but was %s", new Object[] { Integer.valueOf(occurrences) });
/* 266 */     if (occurrences == 0) {
/* 267 */       return count(element);
/* 269 */     }AvlNode root = (AvlNode)this.rootReference.get();
/* 270 */     int[] result = new int[1];
/*     */     AvlNode newRoot;
/*     */     try {
/* 274 */       Object e = element;
/* 275 */       if ((!this.range.contains(e)) || (root == null)) {
/* 276 */         return 0;
/*     */       }
/* 278 */       newRoot = root.remove(comparator(), e, occurrences, result);
/*     */     } catch (ClassCastException e) {
/* 280 */       return 0;
/*     */     } catch (NullPointerException e) {
/* 282 */       return 0;
/*     */     }
/* 284 */     this.rootReference.checkAndSet(root, newRoot);
/* 285 */     return result[0];
/*     */   }
/*     */ 
/*     */   public int setCount(@Nullable E element, int count)
/*     */   {
/* 290 */     Preconditions.checkArgument(count >= 0);
/* 291 */     if (!this.range.contains(element)) {
/* 292 */       Preconditions.checkArgument(count == 0);
/* 293 */       return 0;
/*     */     }
/*     */ 
/* 296 */     AvlNode root = (AvlNode)this.rootReference.get();
/* 297 */     if (root == null) {
/* 298 */       if (count > 0) {
/* 299 */         add(element, count);
/*     */       }
/* 301 */       return 0;
/*     */     }
/* 303 */     int[] result = new int[1];
/* 304 */     AvlNode newRoot = root.setCount(comparator(), element, count, result);
/* 305 */     this.rootReference.checkAndSet(root, newRoot);
/* 306 */     return result[0];
/*     */   }
/*     */ 
/*     */   public boolean setCount(@Nullable E element, int oldCount, int newCount)
/*     */   {
/* 311 */     Preconditions.checkArgument(newCount >= 0);
/* 312 */     Preconditions.checkArgument(oldCount >= 0);
/* 313 */     Preconditions.checkArgument(this.range.contains(element));
/*     */ 
/* 315 */     AvlNode root = (AvlNode)this.rootReference.get();
/* 316 */     if (root == null) {
/* 317 */       if (oldCount == 0) {
/* 318 */         if (newCount > 0) {
/* 319 */           add(element, newCount);
/*     */         }
/* 321 */         return true;
/*     */       }
/* 323 */       return false;
/*     */     }
/*     */ 
/* 326 */     int[] result = new int[1];
/* 327 */     AvlNode newRoot = root.setCount(comparator(), element, oldCount, newCount, result);
/* 328 */     this.rootReference.checkAndSet(root, newRoot);
/* 329 */     return result[0] == oldCount;
/*     */   }
/*     */ 
/*     */   private Multiset.Entry<E> wrapEntry(final AvlNode<E> baseEntry) {
/* 333 */     return new Multisets.AbstractEntry()
/*     */     {
/*     */       public E getElement() {
/* 336 */         return baseEntry.getElement();
/*     */       }
/*     */ 
/*     */       public int getCount()
/*     */       {
/* 341 */         int result = baseEntry.getCount();
/* 342 */         if (result == 0) {
/* 343 */           return TreeMultiset.this.count(getElement());
/*     */         }
/* 345 */         return result;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private AvlNode<E> firstNode()
/*     */   {
/* 355 */     AvlNode root = (AvlNode)this.rootReference.get();
/* 356 */     if (root == null)
/* 357 */       return null;
/*     */     AvlNode node;
/* 360 */     if (this.range.hasLowerBound()) {
/* 361 */       Object endpoint = this.range.getLowerEndpoint();
/* 362 */       AvlNode node = ((AvlNode)this.rootReference.get()).ceiling(comparator(), endpoint);
/* 363 */       if (node == null) {
/* 364 */         return null;
/*     */       }
/* 366 */       if ((this.range.getLowerBoundType() == BoundType.OPEN) && (comparator().compare(endpoint, node.getElement()) == 0))
/*     */       {
/* 368 */         node = node.succ;
/*     */       }
/*     */     } else {
/* 371 */       node = this.header.succ;
/*     */     }
/* 373 */     return (node == this.header) || (!this.range.contains(node.getElement())) ? null : node;
/*     */   }
/*     */   @Nullable
/*     */   private AvlNode<E> lastNode() {
/* 377 */     AvlNode root = (AvlNode)this.rootReference.get();
/* 378 */     if (root == null)
/* 379 */       return null;
/*     */     AvlNode node;
/* 382 */     if (this.range.hasUpperBound()) {
/* 383 */       Object endpoint = this.range.getUpperEndpoint();
/* 384 */       AvlNode node = ((AvlNode)this.rootReference.get()).floor(comparator(), endpoint);
/* 385 */       if (node == null) {
/* 386 */         return null;
/*     */       }
/* 388 */       if ((this.range.getUpperBoundType() == BoundType.OPEN) && (comparator().compare(endpoint, node.getElement()) == 0))
/*     */       {
/* 390 */         node = node.pred;
/*     */       }
/*     */     } else {
/* 393 */       node = this.header.pred;
/*     */     }
/* 395 */     return (node == this.header) || (!this.range.contains(node.getElement())) ? null : node;
/*     */   }
/*     */ 
/*     */   Iterator<Multiset.Entry<E>> entryIterator()
/*     */   {
/* 400 */     return new Iterator() {
/* 401 */       TreeMultiset.AvlNode<E> current = TreeMultiset.this.firstNode();
/*     */       Multiset.Entry<E> prevEntry;
/*     */ 
/*     */       public boolean hasNext() {
/* 406 */         if (this.current == null)
/* 407 */           return false;
/* 408 */         if (TreeMultiset.this.range.tooHigh(this.current.getElement())) {
/* 409 */           this.current = null;
/* 410 */           return false;
/*     */         }
/* 412 */         return true;
/*     */       }
/*     */ 
/*     */       public Multiset.Entry<E> next()
/*     */       {
/* 418 */         if (!hasNext()) {
/* 419 */           throw new NoSuchElementException();
/*     */         }
/* 421 */         Multiset.Entry result = TreeMultiset.this.wrapEntry(this.current);
/* 422 */         this.prevEntry = result;
/* 423 */         if (this.current.succ == TreeMultiset.this.header)
/* 424 */           this.current = null;
/*     */         else {
/* 426 */           this.current = this.current.succ;
/*     */         }
/* 428 */         return result;
/*     */       }
/*     */ 
/*     */       public void remove()
/*     */       {
/* 433 */         Preconditions.checkState(this.prevEntry != null);
/* 434 */         TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
/* 435 */         this.prevEntry = null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   Iterator<Multiset.Entry<E>> descendingEntryIterator()
/*     */   {
/* 442 */     return new Iterator() {
/* 443 */       TreeMultiset.AvlNode<E> current = TreeMultiset.this.lastNode();
/* 444 */       Multiset.Entry<E> prevEntry = null;
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 448 */         if (this.current == null)
/* 449 */           return false;
/* 450 */         if (TreeMultiset.this.range.tooLow(this.current.getElement())) {
/* 451 */           this.current = null;
/* 452 */           return false;
/*     */         }
/* 454 */         return true;
/*     */       }
/*     */ 
/*     */       public Multiset.Entry<E> next()
/*     */       {
/* 460 */         if (!hasNext()) {
/* 461 */           throw new NoSuchElementException();
/*     */         }
/* 463 */         Multiset.Entry result = TreeMultiset.this.wrapEntry(this.current);
/* 464 */         this.prevEntry = result;
/* 465 */         if (this.current.pred == TreeMultiset.this.header)
/* 466 */           this.current = null;
/*     */         else {
/* 468 */           this.current = this.current.pred;
/*     */         }
/* 470 */         return result;
/*     */       }
/*     */ 
/*     */       public void remove()
/*     */       {
/* 475 */         Preconditions.checkState(this.prevEntry != null);
/* 476 */         TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
/* 477 */         this.prevEntry = null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/* 484 */     return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.upTo(comparator(), upperBound, boundType)), this.header);
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/* 492 */     return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.downTo(comparator(), lowerBound, boundType)), this.header);
/*     */   }
/*     */ 
/*     */   static int distinctElements(@Nullable AvlNode<?> node)
/*     */   {
/* 499 */     return node == null ? 0 : node.distinctElements;
/*     */   }
/*     */ 
/*     */   private static <T> void successor(AvlNode<T> a, AvlNode<T> b)
/*     */   {
/* 936 */     a.succ = b;
/* 937 */     b.pred = a;
/*     */   }
/*     */ 
/*     */   private static <T> void successor(AvlNode<T> a, AvlNode<T> b, AvlNode<T> c) {
/* 941 */     successor(a, b);
/* 942 */     successor(b, c);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 957 */     stream.defaultWriteObject();
/* 958 */     stream.writeObject(elementSet().comparator());
/* 959 */     Serialization.writeMultiset(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 964 */     stream.defaultReadObject();
/*     */ 
/* 967 */     Comparator comparator = (Comparator)stream.readObject();
/* 968 */     Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set(this, comparator);
/* 969 */     Serialization.getFieldSetter(TreeMultiset.class, "range").set(this, GeneralRange.all(comparator));
/*     */ 
/* 972 */     Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set(this, new Reference(null));
/*     */ 
/* 975 */     AvlNode header = new AvlNode(null, 1);
/* 976 */     Serialization.getFieldSetter(TreeMultiset.class, "header").set(this, header);
/* 977 */     successor(header, header);
/* 978 */     Serialization.populateMultiset(this, stream);
/*     */   }
/*     */ 
/*     */   private static final class AvlNode<E> extends Multisets.AbstractEntry<E>
/*     */   {
/*     */ 
/*     */     @Nullable
/*     */     private final E elem;
/*     */     private int elemCount;
/*     */     private int distinctElements;
/*     */     private long totalCount;
/*     */     private int height;
/*     */     private AvlNode<E> left;
/*     */     private AvlNode<E> right;
/*     */     private AvlNode<E> pred;
/*     */     private AvlNode<E> succ;
/*     */ 
/*     */     AvlNode(@Nullable E elem, int elemCount)
/*     */     {
/* 532 */       Preconditions.checkArgument(elemCount > 0);
/* 533 */       this.elem = elem;
/* 534 */       this.elemCount = elemCount;
/* 535 */       this.totalCount = elemCount;
/* 536 */       this.distinctElements = 1;
/* 537 */       this.height = 1;
/* 538 */       this.left = null;
/* 539 */       this.right = null;
/*     */     }
/*     */ 
/*     */     public int count(Comparator<? super E> comparator, E e) {
/* 543 */       int cmp = comparator.compare(e, this.elem);
/* 544 */       if (cmp < 0)
/* 545 */         return this.left == null ? 0 : this.left.count(comparator, e);
/* 546 */       if (cmp > 0) {
/* 547 */         return this.right == null ? 0 : this.right.count(comparator, e);
/*     */       }
/* 549 */       return this.elemCount;
/*     */     }
/*     */ 
/*     */     private AvlNode<E> addRightChild(E e, int count)
/*     */     {
/* 554 */       this.right = new AvlNode(e, count);
/* 555 */       TreeMultiset.successor(this, this.right, this.succ);
/* 556 */       this.height = Math.max(2, this.height);
/* 557 */       this.distinctElements += 1;
/* 558 */       this.totalCount += count;
/* 559 */       return this;
/*     */     }
/*     */ 
/*     */     private AvlNode<E> addLeftChild(E e, int count) {
/* 563 */       this.left = new AvlNode(e, count);
/* 564 */       TreeMultiset.successor(this.pred, this.left, this);
/* 565 */       this.height = Math.max(2, this.height);
/* 566 */       this.distinctElements += 1;
/* 567 */       this.totalCount += count;
/* 568 */       return this;
/*     */     }
/*     */ 
/*     */     AvlNode<E> add(Comparator<? super E> comparator, @Nullable E e, int count, int[] result)
/*     */     {
/* 576 */       int cmp = comparator.compare(e, this.elem);
/* 577 */       if (cmp < 0) {
/* 578 */         AvlNode initLeft = this.left;
/* 579 */         if (initLeft == null) {
/* 580 */           result[0] = 0;
/* 581 */           return addLeftChild(e, count);
/*     */         }
/* 583 */         int initHeight = initLeft.height;
/*     */ 
/* 585 */         this.left = initLeft.add(comparator, e, count, result);
/* 586 */         if (result[0] == 0) {
/* 587 */           this.distinctElements += 1;
/*     */         }
/* 589 */         this.totalCount += count;
/* 590 */         return this.left.height == initHeight ? this : rebalance();
/* 591 */       }if (cmp > 0) {
/* 592 */         AvlNode initRight = this.right;
/* 593 */         if (initRight == null) {
/* 594 */           result[0] = 0;
/* 595 */           return addRightChild(e, count);
/*     */         }
/* 597 */         int initHeight = initRight.height;
/*     */ 
/* 599 */         this.right = initRight.add(comparator, e, count, result);
/* 600 */         if (result[0] == 0) {
/* 601 */           this.distinctElements += 1;
/*     */         }
/* 603 */         this.totalCount += count;
/* 604 */         return this.right.height == initHeight ? this : rebalance();
/*     */       }
/*     */ 
/* 608 */       result[0] = this.elemCount;
/* 609 */       long resultCount = this.elemCount + count;
/* 610 */       Preconditions.checkArgument(resultCount <= 2147483647L);
/* 611 */       this.elemCount += count;
/* 612 */       this.totalCount += count;
/* 613 */       return this;
/*     */     }
/*     */ 
/*     */     AvlNode<E> remove(Comparator<? super E> comparator, @Nullable E e, int count, int[] result) {
/* 617 */       int cmp = comparator.compare(e, this.elem);
/* 618 */       if (cmp < 0) {
/* 619 */         AvlNode initLeft = this.left;
/* 620 */         if (initLeft == null) {
/* 621 */           result[0] = 0;
/* 622 */           return this;
/*     */         }
/*     */ 
/* 625 */         this.left = initLeft.remove(comparator, e, count, result);
/*     */ 
/* 627 */         if (result[0] > 0) {
/* 628 */           if (count >= result[0]) {
/* 629 */             this.distinctElements -= 1;
/* 630 */             this.totalCount -= result[0];
/*     */           } else {
/* 632 */             this.totalCount -= count;
/*     */           }
/*     */         }
/* 635 */         return result[0] == 0 ? this : rebalance();
/* 636 */       }if (cmp > 0) {
/* 637 */         AvlNode initRight = this.right;
/* 638 */         if (initRight == null) {
/* 639 */           result[0] = 0;
/* 640 */           return this;
/*     */         }
/*     */ 
/* 643 */         this.right = initRight.remove(comparator, e, count, result);
/*     */ 
/* 645 */         if (result[0] > 0) {
/* 646 */           if (count >= result[0]) {
/* 647 */             this.distinctElements -= 1;
/* 648 */             this.totalCount -= result[0];
/*     */           } else {
/* 650 */             this.totalCount -= count;
/*     */           }
/*     */         }
/* 653 */         return rebalance();
/*     */       }
/*     */ 
/* 657 */       result[0] = this.elemCount;
/* 658 */       if (count >= this.elemCount) {
/* 659 */         return deleteMe();
/*     */       }
/* 661 */       this.elemCount -= count;
/* 662 */       this.totalCount -= count;
/* 663 */       return this;
/*     */     }
/*     */ 
/*     */     AvlNode<E> setCount(Comparator<? super E> comparator, @Nullable E e, int count, int[] result)
/*     */     {
/* 668 */       int cmp = comparator.compare(e, this.elem);
/* 669 */       if (cmp < 0) {
/* 670 */         AvlNode initLeft = this.left;
/* 671 */         if (initLeft == null) {
/* 672 */           result[0] = 0;
/* 673 */           return count > 0 ? addLeftChild(e, count) : this;
/*     */         }
/*     */ 
/* 676 */         this.left = initLeft.setCount(comparator, e, count, result);
/*     */ 
/* 678 */         if ((count == 0) && (result[0] != 0))
/* 679 */           this.distinctElements -= 1;
/* 680 */         else if ((count > 0) && (result[0] == 0)) {
/* 681 */           this.distinctElements += 1;
/*     */         }
/*     */ 
/* 684 */         this.totalCount += count - result[0];
/* 685 */         return rebalance();
/* 686 */       }if (cmp > 0) {
/* 687 */         AvlNode initRight = this.right;
/* 688 */         if (initRight == null) {
/* 689 */           result[0] = 0;
/* 690 */           return count > 0 ? addRightChild(e, count) : this;
/*     */         }
/*     */ 
/* 693 */         this.right = initRight.setCount(comparator, e, count, result);
/*     */ 
/* 695 */         if ((count == 0) && (result[0] != 0))
/* 696 */           this.distinctElements -= 1;
/* 697 */         else if ((count > 0) && (result[0] == 0)) {
/* 698 */           this.distinctElements += 1;
/*     */         }
/*     */ 
/* 701 */         this.totalCount += count - result[0];
/* 702 */         return rebalance();
/*     */       }
/*     */ 
/* 706 */       result[0] = this.elemCount;
/* 707 */       if (count == 0) {
/* 708 */         return deleteMe();
/*     */       }
/* 710 */       this.totalCount += count - this.elemCount;
/* 711 */       this.elemCount = count;
/* 712 */       return this;
/*     */     }
/*     */ 
/*     */     AvlNode<E> setCount(Comparator<? super E> comparator, @Nullable E e, int expectedCount, int newCount, int[] result)
/*     */     {
/* 721 */       int cmp = comparator.compare(e, this.elem);
/* 722 */       if (cmp < 0) {
/* 723 */         AvlNode initLeft = this.left;
/* 724 */         if (initLeft == null) {
/* 725 */           result[0] = 0;
/* 726 */           if ((expectedCount == 0) && (newCount > 0)) {
/* 727 */             return addLeftChild(e, newCount);
/*     */           }
/* 729 */           return this;
/*     */         }
/*     */ 
/* 732 */         this.left = initLeft.setCount(comparator, e, expectedCount, newCount, result);
/*     */ 
/* 734 */         if (result[0] == expectedCount) {
/* 735 */           if ((newCount == 0) && (result[0] != 0))
/* 736 */             this.distinctElements -= 1;
/* 737 */           else if ((newCount > 0) && (result[0] == 0)) {
/* 738 */             this.distinctElements += 1;
/*     */           }
/* 740 */           this.totalCount += newCount - result[0];
/*     */         }
/* 742 */         return rebalance();
/* 743 */       }if (cmp > 0) {
/* 744 */         AvlNode initRight = this.right;
/* 745 */         if (initRight == null) {
/* 746 */           result[0] = 0;
/* 747 */           if ((expectedCount == 0) && (newCount > 0)) {
/* 748 */             return addRightChild(e, newCount);
/*     */           }
/* 750 */           return this;
/*     */         }
/*     */ 
/* 753 */         this.right = initRight.setCount(comparator, e, expectedCount, newCount, result);
/*     */ 
/* 755 */         if (result[0] == expectedCount) {
/* 756 */           if ((newCount == 0) && (result[0] != 0))
/* 757 */             this.distinctElements -= 1;
/* 758 */           else if ((newCount > 0) && (result[0] == 0)) {
/* 759 */             this.distinctElements += 1;
/*     */           }
/* 761 */           this.totalCount += newCount - result[0];
/*     */         }
/* 763 */         return rebalance();
/*     */       }
/*     */ 
/* 767 */       result[0] = this.elemCount;
/* 768 */       if (expectedCount == this.elemCount) {
/* 769 */         if (newCount == 0) {
/* 770 */           return deleteMe();
/*     */         }
/* 772 */         this.totalCount += newCount - this.elemCount;
/* 773 */         this.elemCount = newCount;
/*     */       }
/* 775 */       return this;
/*     */     }
/*     */ 
/*     */     private AvlNode<E> deleteMe() {
/* 779 */       int oldElemCount = this.elemCount;
/* 780 */       this.elemCount = 0;
/* 781 */       TreeMultiset.successor(this.pred, this.succ);
/* 782 */       if (this.left == null)
/* 783 */         return this.right;
/* 784 */       if (this.right == null)
/* 785 */         return this.left;
/* 786 */       if (this.left.height >= this.right.height) {
/* 787 */         AvlNode newTop = this.pred;
/*     */ 
/* 789 */         newTop.left = this.left.removeMax(newTop);
/* 790 */         newTop.right = this.right;
/* 791 */         this.distinctElements -= 1;
/* 792 */         this.totalCount -= oldElemCount;
/* 793 */         return newTop.rebalance();
/*     */       }
/* 795 */       AvlNode newTop = this.succ;
/* 796 */       newTop.right = this.right.removeMin(newTop);
/* 797 */       newTop.left = this.left;
/* 798 */       this.distinctElements -= 1;
/* 799 */       this.totalCount -= oldElemCount;
/* 800 */       return newTop.rebalance();
/*     */     }
/*     */ 
/*     */     private AvlNode<E> removeMin(AvlNode<E> node)
/*     */     {
/* 806 */       if (this.left == null) {
/* 807 */         return this.right;
/*     */       }
/* 809 */       this.left = this.left.removeMin(node);
/* 810 */       this.distinctElements -= 1;
/* 811 */       this.totalCount -= node.elemCount;
/* 812 */       return rebalance();
/*     */     }
/*     */ 
/*     */     private AvlNode<E> removeMax(AvlNode<E> node)
/*     */     {
/* 818 */       if (this.right == null) {
/* 819 */         return this.left;
/*     */       }
/* 821 */       this.right = this.right.removeMax(node);
/* 822 */       this.distinctElements -= 1;
/* 823 */       this.totalCount -= node.elemCount;
/* 824 */       return rebalance();
/*     */     }
/*     */ 
/*     */     private void recomputeMultiset()
/*     */     {
/* 829 */       this.distinctElements = (1 + TreeMultiset.distinctElements(this.left) + TreeMultiset.distinctElements(this.right));
/*     */ 
/* 831 */       this.totalCount = (this.elemCount + totalCount(this.left) + totalCount(this.right));
/*     */     }
/*     */ 
/*     */     private void recomputeHeight() {
/* 835 */       this.height = (1 + Math.max(height(this.left), height(this.right)));
/*     */     }
/*     */ 
/*     */     private void recompute() {
/* 839 */       recomputeMultiset();
/* 840 */       recomputeHeight();
/*     */     }
/*     */ 
/*     */     private AvlNode<E> rebalance() {
/* 844 */       switch (balanceFactor()) {
/*     */       case -2:
/* 846 */         if (this.right.balanceFactor() > 0) {
/* 847 */           this.right = this.right.rotateRight();
/*     */         }
/* 849 */         return rotateLeft();
/*     */       case 2:
/* 851 */         if (this.left.balanceFactor() < 0) {
/* 852 */           this.left = this.left.rotateLeft();
/*     */         }
/* 854 */         return rotateRight();
/*     */       }
/* 856 */       recomputeHeight();
/* 857 */       return this;
/*     */     }
/*     */ 
/*     */     private int balanceFactor()
/*     */     {
/* 862 */       return height(this.left) - height(this.right);
/*     */     }
/*     */ 
/*     */     private AvlNode<E> rotateLeft() {
/* 866 */       Preconditions.checkState(this.right != null);
/* 867 */       AvlNode newTop = this.right;
/* 868 */       this.right = newTop.left;
/* 869 */       newTop.left = this;
/* 870 */       newTop.totalCount = this.totalCount;
/* 871 */       newTop.distinctElements = this.distinctElements;
/* 872 */       recompute();
/* 873 */       newTop.recomputeHeight();
/* 874 */       return newTop;
/*     */     }
/*     */ 
/*     */     private AvlNode<E> rotateRight() {
/* 878 */       Preconditions.checkState(this.left != null);
/* 879 */       AvlNode newTop = this.left;
/* 880 */       this.left = newTop.right;
/* 881 */       newTop.right = this;
/* 882 */       newTop.totalCount = this.totalCount;
/* 883 */       newTop.distinctElements = this.distinctElements;
/* 884 */       recompute();
/* 885 */       newTop.recomputeHeight();
/* 886 */       return newTop;
/*     */     }
/*     */ 
/*     */     private static long totalCount(@Nullable AvlNode<?> node) {
/* 890 */       return node == null ? 0L : node.totalCount;
/*     */     }
/*     */ 
/*     */     private static int height(@Nullable AvlNode<?> node) {
/* 894 */       return node == null ? 0 : node.height;
/*     */     }
/*     */     @Nullable
/*     */     private AvlNode<E> ceiling(Comparator<? super E> comparator, E e) {
/* 898 */       int cmp = comparator.compare(e, this.elem);
/* 899 */       if (cmp < 0)
/* 900 */         return this.left == null ? this : (AvlNode)Objects.firstNonNull(this.left.ceiling(comparator, e), this);
/* 901 */       if (cmp == 0) {
/* 902 */         return this;
/*     */       }
/* 904 */       return this.right == null ? null : this.right.ceiling(comparator, e);
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     private AvlNode<E> floor(Comparator<? super E> comparator, E e) {
/* 909 */       int cmp = comparator.compare(e, this.elem);
/* 910 */       if (cmp > 0)
/* 911 */         return this.right == null ? this : (AvlNode)Objects.firstNonNull(this.right.floor(comparator, e), this);
/* 912 */       if (cmp == 0) {
/* 913 */         return this;
/*     */       }
/* 915 */       return this.left == null ? null : this.left.floor(comparator, e);
/*     */     }
/*     */ 
/*     */     public E getElement()
/*     */     {
/* 921 */       return this.elem;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 926 */       return this.elemCount;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 931 */       return Multisets.immutableEntry(getElement(), getCount()).toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class Reference<T>
/*     */   {
/*     */ 
/*     */     @Nullable
/*     */     private T value;
/*     */ 
/*     */     @Nullable
/*     */     public T get()
/*     */     {
/* 506 */       return this.value;
/*     */     }
/*     */ 
/*     */     public void checkAndSet(@Nullable T expected, T newValue) {
/* 510 */       if (this.value != expected) {
/* 511 */         throw new ConcurrentModificationException();
/*     */       }
/* 513 */       this.value = newValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract enum Aggregate
/*     */   {
/* 132 */     SIZE, 
/*     */ 
/* 143 */     DISTINCT;
/*     */ 
/*     */     abstract int nodeAggregate(TreeMultiset.AvlNode<?> paramAvlNode);
/*     */ 
/*     */     abstract long treeAggregate(@Nullable TreeMultiset.AvlNode<?> paramAvlNode);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.TreeMultiset
 * JD-Core Version:    0.6.2
 */