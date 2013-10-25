/*    */ package org.apache.log4j.or;
/*    */ 
/*    */ import org.apache.log4j.Layout;
/*    */ 
/*    */ public class ThreadGroupRenderer
/*    */   implements ObjectRenderer
/*    */ {
/*    */   public String doRender(Object o)
/*    */   {
/* 51 */     if ((o instanceof ThreadGroup)) {
/* 52 */       StringBuffer sbuf = new StringBuffer();
/* 53 */       ThreadGroup tg = (ThreadGroup)o;
/* 54 */       sbuf.append("java.lang.ThreadGroup[name=");
/* 55 */       sbuf.append(tg.getName());
/* 56 */       sbuf.append(", maxpri=");
/* 57 */       sbuf.append(tg.getMaxPriority());
/* 58 */       sbuf.append("]");
/* 59 */       Thread[] t = new Thread[tg.activeCount()];
/* 60 */       tg.enumerate(t);
/* 61 */       for (int i = 0; i < t.length; i++) {
/* 62 */         sbuf.append(Layout.LINE_SEP);
/* 63 */         sbuf.append("   Thread=[");
/* 64 */         sbuf.append(t[i].getName());
/* 65 */         sbuf.append(",");
/* 66 */         sbuf.append(t[i].getPriority());
/* 67 */         sbuf.append(",");
/* 68 */         sbuf.append(t[i].isDaemon());
/* 69 */         sbuf.append("]");
/*    */       }
/* 71 */       return sbuf.toString();
/*    */     }
/*    */     try
/*    */     {
/* 75 */       return o.toString();
/*    */     } catch (Exception ex) {
/* 77 */       return ex.toString();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.or.ThreadGroupRenderer
 * JD-Core Version:    0.6.2
 */