 package com.amazon.external.elasticmapreduce.s3distcp;
 
 import java.security.SecureRandom;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.ThreadPoolExecutor;
 
 public class Utils
 {
   public static String randomString(long value)
   {
     StringBuffer result = new StringBuffer();
 
     if (value < 0L)
       value = -value;
     do
     {
       long remainder = value % 58L;
       int c;
       if (remainder < 24L) {
         c = 'a' + (char)(int)remainder;
       }
       else
       {
         if (remainder < 48L) {
           c = 'A' + (char)(int)(remainder - 24L);
         }
         else
           c = '0' + (char)(int)(remainder - 48L);
       }
       result.appendCodePoint(c);
       value /= 58L;
     }
     while (value > 0L);
     return result.reverse().toString();
   }
 
   public static String randomString() {
     return randomString(new SecureRandom().nextLong());
   }
 
   public static String getSuffix(String name) {
     if (name != null) {
       String[] parts = name.split("\\.");
       if (parts.length > 1) {
         return parts[(parts.length - 1)];
       }
     }
     return "";
   }
 
   public static String replaceSuffix(String name, String suffix) {
     if (getSuffix(name).equals("")) {
       return name + suffix;
     }
     int index = name.lastIndexOf('.');
     return name.substring(0, index) + suffix;
   }
 
   public static boolean isS3Scheme(String scheme)
   {
     return (scheme.equals("s3")) || (scheme.equals("s3n"));
   }
 
   public static ThreadPoolExecutor createDefaultExecutorService() {
     ThreadFactory threadFactory = new ThreadFactory() {
       private int threadCount = 1;
 
       public Thread newThread(Runnable r) {
         Thread thread = new Thread(r);
         thread.setName("s3-transfer-manager-worker-" + this.threadCount++);
         return thread;
       }
     };
     return (ThreadPoolExecutor)Executors.newFixedThreadPool(10, threadFactory);
   }
 }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.Utils
 * JD-Core Version:    0.6.2
 */