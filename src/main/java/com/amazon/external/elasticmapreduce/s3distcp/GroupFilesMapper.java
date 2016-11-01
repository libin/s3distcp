/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.apache.hadoop.io.LongWritable;
/*    */ import org.apache.hadoop.io.Text;
/*    */ import org.apache.hadoop.mapred.JobConf;
/*    */ import org.apache.hadoop.mapred.Mapper;
/*    */ import org.apache.hadoop.mapred.OutputCollector;
/*    */ import org.apache.hadoop.mapred.Reporter;
/*    */ 
/*    */ public class GroupFilesMapper
/*    */   implements Mapper<LongWritable, FileInfo, Text, FileInfo>
/*    */ {
/* 20 */   private static final Log log = LogFactory.getLog(GroupFilesMapper.class);
/*    */   protected JobConf conf;
/* 23 */   protected Pattern pattern = null;
/*    */   private String destDir;
		   private Integer fileBuckets; 
/*    */ 
/*    */   public void configure(JobConf conf)
/*    */   {
/* 28 */     this.conf = conf;
/*  98 */    this.fileBuckets = conf.getInt("s3DistCp.copyfiles.mapper.fileBuckets", -1);
/* 29 */     String patternString = conf.get("s3DistCp.listfiles.gropubypattern");
/* 30 */     if (patternString != null) {
/* 31 */       this.pattern = Pattern.compile(patternString);
/*    */     }
/* 33 */     this.destDir = conf.get("s3DistCp.copyfiles.destDir");
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void map(LongWritable fileUID, FileInfo fileInfo, OutputCollector<Text, FileInfo> collector, Reporter reporter) throws IOException
/*    */   {
/*    */     Text key;
String inputFileName = fileInfo.inputFileName.toString().replace("[", "%5B").replace("]", "%5D").replace(":", "%3A").replace(" ", "%20");
/*    */     try
/*    */     {				
/* 46 */       String path = new URI(inputFileName).getPath();

				System.out.println("filename = " + inputFileName);
				System.out.println("path = " + path);

/* 47 */       if (path.startsWith(this.destDir)) {
/* 48 */         path = path.substring(this.destDir.length());
/*    */       }			   
				System.out.println("path after = " + path);
/* 50 */       key = new Text(path);
/*    */     } catch (URISyntaxException e) {
/* 52 */       throw new RuntimeException(new StringBuilder().append("Bad URI: ").append(inputFileName).toString(), e);
/*    */     }
/*    */ 
			 if (this.fileBuckets > 0) {
	/*    */     try
	/*    */     {
	/* 46 */       String path = new URI(inputFileName).getPath();
	/* 47 */       if (path.startsWith(this.destDir)) {
	/* 48 */         path = path.substring(this.destDir.length());
	/*    */       }			   
	/* 50 */       key = new Text("" + ("" + (path.hashCode() % this.fileBuckets)).hashCode());
	/*    */     } catch (URISyntaxException e) {
	/* 52 */       throw new RuntimeException(new StringBuilder().append("Bad URI: ").append(inputFileName).toString(), e);
	/*    */     }
			 } else if (this.pattern != null) {
/* 56 */       Matcher matcher = this.pattern.matcher(fileInfo.inputFileName.toString());
/* 57 */       if (matcher.matches()) {
/* 58 */         int numGroups = matcher.groupCount();
/* 59 */         StringBuilder builder = new StringBuilder();
/* 60 */         for (int i = 0; i < numGroups; i++) {
/* 61 */           builder.append(matcher.group(i + 1));
/*    */         }
/* 63 */         key = new Text(builder.toString());
/*    */       }
/*    */     }
/* 66 */     log.debug(new StringBuilder().append("Adding ").append(key.toString()).append(": ").append(fileInfo.inputFileName.toString()).toString());
/* 67 */     collector.collect(key, fileInfo);
/*    */   }

	public static void main(String[] args) throws URISyntaxException {
		//s3n://data-lake-production-ca/earnings_inputs/-12861
		String path = new URI("s3n://XPlenty/test-colon/airasia_failied_luigi_job_3168441.txt").getPath();
		System.out.println(path);
	}
/*    */ }


/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.GroupFilesMapper
 * JD-Core Version:    0.6.2
 */