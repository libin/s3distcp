/*      */ package com.google.common.util.concurrent;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.collect.ImmutableList;
/*      */ import com.google.common.collect.Lists;
/*      */ import com.google.common.collect.Ordering;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.UndeclaredThrowableException;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.CancellationException;
/*      */ import java.util.concurrent.CountDownLatch;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.TimeoutException;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @Beta
/*      */ public final class Futures
/*      */ {
/* 1029 */   private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function()
/*      */   {
/*      */     public Boolean apply(Constructor<?> input) {
/* 1032 */       return Boolean.valueOf(Arrays.asList(input.getParameterTypes()).contains(String.class));
/*      */     }
/*      */   }).reverse();
/*      */ 
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> future, Function<Exception, X> mapper)
/*      */   {
/*   85 */     return new MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(future), mapper);
/*      */   }
/*      */ 
/*      */   public static <V> ListenableFuture<V> immediateFuture(@Nullable V value)
/*      */   {
/*   95 */     SettableFuture future = SettableFuture.create();
/*   96 */     future.set(value);
/*   97 */     return future;
/*      */   }
/*      */ 
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V value)
/*      */   {
/*  110 */     SettableFuture future = SettableFuture.create();
/*  111 */     future.set(value);
/*  112 */     return makeChecked(future, new Function()
/*      */     {
/*      */       public X apply(Exception e) {
/*  115 */         throw new AssertionError("impossible");
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable)
/*      */   {
/*  133 */     Preconditions.checkNotNull(throwable);
/*  134 */     SettableFuture future = SettableFuture.create();
/*  135 */     future.setException(throwable);
/*  136 */     return future;
/*      */   }
/*      */ 
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X exception)
/*      */   {
/*  153 */     Preconditions.checkNotNull(exception);
/*  154 */     return makeChecked(immediateFailedFuture(exception), new Function()
/*      */     {
/*      */       public X apply(Exception e)
/*      */       {
/*  158 */         return this.val$exception;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function)
/*      */   {
/*  213 */     return transform(input, function, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor)
/*      */   {
/*  268 */     ChainingListenableFuture output = new ChainingListenableFuture(function, input, null);
/*      */ 
/*  270 */     input.addListener(output, executor);
/*  271 */     return output;
/*      */   }
/*      */ 
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function)
/*      */   {
/*  323 */     return transform(input, function, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor)
/*      */   {
/*  376 */     Preconditions.checkNotNull(function);
/*  377 */     AsyncFunction wrapperFunction = new AsyncFunction()
/*      */     {
/*      */       public ListenableFuture<O> apply(I input) {
/*  380 */         Object output = this.val$function.apply(input);
/*  381 */         return Futures.immediateFuture(output);
/*      */       }
/*      */     };
/*  384 */     return transform(input, wrapperFunction, executor);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <I, O> Future<O> lazyTransform(Future<I> input, final Function<? super I, ? extends O> function)
/*      */   {
/*  413 */     Preconditions.checkNotNull(input);
/*  414 */     Preconditions.checkNotNull(function);
/*  415 */     return new Future()
/*      */     {
/*      */       public boolean cancel(boolean mayInterruptIfRunning)
/*      */       {
/*  419 */         return this.val$input.cancel(mayInterruptIfRunning);
/*      */       }
/*      */ 
/*      */       public boolean isCancelled()
/*      */       {
/*  424 */         return this.val$input.isCancelled();
/*      */       }
/*      */ 
/*      */       public boolean isDone()
/*      */       {
/*  429 */         return this.val$input.isDone();
/*      */       }
/*      */ 
/*      */       public O get() throws InterruptedException, ExecutionException
/*      */       {
/*  434 */         return applyTransformation(this.val$input.get());
/*      */       }
/*      */ 
/*      */       public O get(long timeout, TimeUnit unit)
/*      */         throws InterruptedException, ExecutionException, TimeoutException
/*      */       {
/*  440 */         return applyTransformation(this.val$input.get(timeout, unit));
/*      */       }
/*      */ 
/*      */       private O applyTransformation(I input) throws ExecutionException {
/*      */         try {
/*  445 */           return function.apply(input);
/*      */         } catch (Throwable t) {
/*  447 */           throw new ExecutionException(t);
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>[] futures)
/*      */   {
/*  597 */     return new ListFuture(ImmutableList.copyOf(futures), true, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> futures)
/*      */   {
/*  620 */     return new ListFuture(ImmutableList.copyOf(futures), true, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>[] futures)
/*      */   {
/*  640 */     return new ListFuture(ImmutableList.copyOf(futures), false, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> futures)
/*      */   {
/*  660 */     return new ListFuture(ImmutableList.copyOf(futures), false, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback)
/*      */   {
/*  708 */     addCallback(future, callback, MoreExecutors.sameThreadExecutor());
/*      */   }
/*      */ 
/*      */   public static <V> void addCallback(ListenableFuture<V> future, final FutureCallback<? super V> callback, Executor executor)
/*      */   {
/*  760 */     Preconditions.checkNotNull(callback);
/*  761 */     Runnable callbackListener = new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*  767 */           Object value = Uninterruptibles.getUninterruptibly(this.val$future);
/*  768 */           callback.onSuccess(value);
/*      */         } catch (ExecutionException e) {
/*  770 */           callback.onFailure(e.getCause());
/*      */         } catch (RuntimeException e) {
/*  772 */           callback.onFailure(e);
/*      */         } catch (Error e) {
/*  774 */           callback.onFailure(e);
/*      */         }
/*      */       }
/*      */     };
/*  778 */     future.addListener(callbackListener, executor);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V, X extends Exception> V get(Future<V> future, Class<X> exceptionClass)
/*      */     throws Exception
/*      */   {
/*  831 */     Preconditions.checkNotNull(future);
/*  832 */     Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
/*      */     try
/*      */     {
/*  836 */       return future.get();
/*      */     } catch (InterruptedException e) {
/*  838 */       Thread.currentThread().interrupt();
/*  839 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (ExecutionException e) {
/*  841 */       wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
/*  842 */     }throw new AssertionError();
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V, X extends Exception> V get(Future<V> future, long timeout, TimeUnit unit, Class<X> exceptionClass)
/*      */     throws Exception
/*      */   {
/*  898 */     Preconditions.checkNotNull(future);
/*  899 */     Preconditions.checkNotNull(unit);
/*  900 */     Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
/*      */     try
/*      */     {
/*  904 */       return future.get(timeout, unit);
/*      */     } catch (InterruptedException e) {
/*  906 */       Thread.currentThread().interrupt();
/*  907 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (TimeoutException e) {
/*  909 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (ExecutionException e) {
/*  911 */       wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
/*  912 */     }throw new AssertionError();
/*      */   }
/*      */ 
/*      */   private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass)
/*      */     throws Exception
/*      */   {
/*  918 */     if ((cause instanceof Error)) {
/*  919 */       throw new ExecutionError((Error)cause);
/*      */     }
/*  921 */     if ((cause instanceof RuntimeException)) {
/*  922 */       throw new UncheckedExecutionException(cause);
/*      */     }
/*  924 */     throw newWithCause(exceptionClass, cause);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <V> V getUnchecked(Future<V> future)
/*      */   {
/*  966 */     Preconditions.checkNotNull(future);
/*      */     try {
/*  968 */       return Uninterruptibles.getUninterruptibly(future);
/*      */     } catch (ExecutionException e) {
/*  970 */       wrapAndThrowUnchecked(e.getCause());
/*  971 */     }throw new AssertionError();
/*      */   }
/*      */ 
/*      */   private static void wrapAndThrowUnchecked(Throwable cause)
/*      */   {
/*  976 */     if ((cause instanceof Error)) {
/*  977 */       throw new ExecutionError((Error)cause);
/*      */     }
/*      */ 
/*  984 */     throw new UncheckedExecutionException(cause);
/*      */   }
/*      */ 
/*      */   private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause)
/*      */   {
/* 1008 */     List constructors = Arrays.asList(exceptionClass.getConstructors());
/*      */ 
/* 1010 */     for (Constructor constructor : preferringStrings(constructors)) {
/* 1011 */       Exception instance = (Exception)newFromConstructor(constructor, cause);
/* 1012 */       if (instance != null) {
/* 1013 */         if (instance.getCause() == null) {
/* 1014 */           instance.initCause(cause);
/*      */         }
/* 1016 */         return instance;
/*      */       }
/*      */     }
/* 1019 */     throw new IllegalArgumentException("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception", cause);
/*      */   }
/*      */ 
/*      */   private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors)
/*      */   {
/* 1026 */     return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause)
/*      */   {
/* 1038 */     Class[] paramTypes = constructor.getParameterTypes();
/* 1039 */     Object[] params = new Object[paramTypes.length];
/* 1040 */     for (int i = 0; i < paramTypes.length; i++) {
/* 1041 */       Class paramType = paramTypes[i];
/* 1042 */       if (paramType.equals(String.class))
/* 1043 */         params[i] = cause.toString();
/* 1044 */       else if (paramType.equals(Throwable.class))
/* 1045 */         params[i] = cause;
/*      */       else
/* 1047 */         return null;
/*      */     }
/*      */     try
/*      */     {
/* 1051 */       return constructor.newInstance(params);
/*      */     } catch (IllegalArgumentException e) {
/* 1053 */       return null;
/*      */     } catch (InstantiationException e) {
/* 1055 */       return null;
/*      */     } catch (IllegalAccessException e) {
/* 1057 */       return null; } catch (InvocationTargetException e) {
/*      */     }
/* 1059 */     return null;
/*      */   }
/*      */ 
/*      */   private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X>
/*      */   {
/*      */     final Function<Exception, X> mapper;
/*      */ 
/*      */     MappingCheckedFuture(ListenableFuture<V> delegate, Function<Exception, X> mapper)
/*      */     {
/* 1208 */       super();
/*      */ 
/* 1210 */       this.mapper = ((Function)Preconditions.checkNotNull(mapper));
/*      */     }
/*      */ 
/*      */     protected X mapException(Exception e)
/*      */     {
/* 1215 */       return (Exception)this.mapper.apply(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ListFuture<V> extends AbstractFuture<List<V>>
/*      */   {
/*      */     ImmutableList<? extends ListenableFuture<? extends V>> futures;
/*      */     final boolean allMustSucceed;
/*      */     final AtomicInteger remaining;
/*      */     List<V> values;
/*      */ 
/*      */     ListFuture(ImmutableList<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor)
/*      */     {
/* 1087 */       this.futures = futures;
/* 1088 */       this.values = Lists.newArrayListWithCapacity(futures.size());
/* 1089 */       this.allMustSucceed = allMustSucceed;
/* 1090 */       this.remaining = new AtomicInteger(futures.size());
/*      */ 
/* 1092 */       init(listenerExecutor);
/*      */     }
/*      */ 
/*      */     private void init(Executor listenerExecutor)
/*      */     {
/* 1097 */       addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/* 1102 */           Futures.ListFuture.this.values = null;
/*      */ 
/* 1105 */           Futures.ListFuture.this.futures = null;
/*      */         }
/*      */       }
/*      */       , MoreExecutors.sameThreadExecutor());
/*      */ 
/* 1112 */       if (this.futures.isEmpty()) {
/* 1113 */         set(Lists.newArrayList(this.values));
/* 1114 */         return;
/*      */       }
/*      */ 
/* 1118 */       for (int i = 0; i < this.futures.size(); i++) {
/* 1119 */         this.values.add(null);
/*      */       }
/*      */ 
/* 1129 */       ImmutableList localFutures = this.futures;
/* 1130 */       for (int i = 0; i < localFutures.size(); i++) {
/* 1131 */         final ListenableFuture listenable = (ListenableFuture)localFutures.get(i);
/* 1132 */         final int index = i;
/* 1133 */         listenable.addListener(new Runnable()
/*      */         {
/*      */           public void run() {
/* 1136 */             Futures.ListFuture.this.setOneValue(index, listenable);
/*      */           }
/*      */         }
/*      */         , listenerExecutor);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void setOneValue(int index, Future<? extends V> future)
/*      */     {
/* 1146 */       List localValues = this.values;
/* 1147 */       if ((isDone()) || (localValues == null))
/*      */       {
/* 1151 */         Preconditions.checkState(this.allMustSucceed, "Future was done before all dependencies completed");
/*      */ 
/* 1153 */         return;
/*      */       }
/*      */       try
/*      */       {
/* 1157 */         Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
/*      */ 
/* 1159 */         localValues.set(index, Uninterruptibles.getUninterruptibly(future));
/*      */       }
/*      */       catch (CancellationException e)
/*      */       {
/*      */         int newRemaining;
/* 1161 */         if (this.allMustSucceed)
/*      */         {
/* 1166 */           cancel(false);
/*      */         }
/*      */       }
/*      */       catch (ExecutionException e)
/*      */       {
/*      */         int newRemaining;
/* 1169 */         if (this.allMustSucceed)
/*      */         {
/* 1172 */           setException(e.getCause());
/*      */         }
/*      */       }
/*      */       catch (RuntimeException e)
/*      */       {
/*      */         int newRemaining;
/* 1175 */         if (this.allMustSucceed)
/* 1176 */           setException(e);
/*      */       }
/*      */       catch (Error e)
/*      */       {
/*      */         int newRemaining;
/* 1180 */         setException(e);
/*      */       }
/*      */       finally
/*      */       {
/*      */         int newRemaining;
/* 1182 */         int newRemaining = this.remaining.decrementAndGet();
/* 1183 */         Preconditions.checkState(newRemaining >= 0, "Less than 0 remaining futures");
/* 1184 */         if (newRemaining == 0) {
/* 1185 */           localValues = this.values;
/* 1186 */           if (localValues != null)
/* 1187 */             set(Lists.newArrayList(localValues));
/*      */           else
/* 1189 */             Preconditions.checkState(isDone());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ChainingListenableFuture<I, O> extends AbstractFuture<O>
/*      */     implements Runnable
/*      */   {
/*      */     private AsyncFunction<? super I, ? extends O> function;
/*      */     private ListenableFuture<? extends I> inputFuture;
/*      */     private volatile ListenableFuture<? extends O> outputFuture;
/*  469 */     private final BlockingQueue<Boolean> mayInterruptIfRunningChannel = new LinkedBlockingQueue(1);
/*      */ 
/*  471 */     private final CountDownLatch outputCreated = new CountDownLatch(1);
/*      */ 
/*      */     private ChainingListenableFuture(AsyncFunction<? super I, ? extends O> function, ListenableFuture<? extends I> inputFuture)
/*      */     {
/*  476 */       this.function = ((AsyncFunction)Preconditions.checkNotNull(function));
/*  477 */       this.inputFuture = ((ListenableFuture)Preconditions.checkNotNull(inputFuture));
/*      */     }
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  486 */       if (super.cancel(mayInterruptIfRunning))
/*      */       {
/*  489 */         Uninterruptibles.putUninterruptibly(this.mayInterruptIfRunningChannel, Boolean.valueOf(mayInterruptIfRunning));
/*  490 */         cancel(this.inputFuture, mayInterruptIfRunning);
/*  491 */         cancel(this.outputFuture, mayInterruptIfRunning);
/*  492 */         return true;
/*      */       }
/*  494 */       return false;
/*      */     }
/*      */ 
/*      */     private void cancel(@Nullable Future<?> future, boolean mayInterruptIfRunning)
/*      */     {
/*  499 */       if (future != null)
/*  500 */         future.cancel(mayInterruptIfRunning);
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/*      */         Object sourceResult;
/*      */         final ListenableFuture outputFuture;
/*      */         return;
/*      */       }
/*      */       catch (ExecutionException e)
/*      */       {
/*      */       }
/*      */       finally
/*      */       {
/*  570 */         this.function = null;
/*  571 */         this.inputFuture = null;
/*      */ 
/*  573 */         this.outputCreated.countDown();
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.Futures
 * JD-Core Version:    0.6.2
 */