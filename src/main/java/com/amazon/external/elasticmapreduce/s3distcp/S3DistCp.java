/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.auth.AWSCredentials;
/*     */ import com.amazonaws.auth.BasicAWSCredentials;
/*     */ import com.amazonaws.auth.InstanceProfileCredentialsProvider;
/*     */ import com.amazonaws.services.s3.AmazonS3Client;
/*     */ import com.amazonaws.services.s3.model.ListObjectsRequest;
/*     */ import com.amazonaws.services.s3.model.ObjectListing;
/*     */ import com.amazonaws.services.s3.model.S3ObjectSummary;
/*     */ import com.google.gson.Gson;
/*     */ import emr.hbase.options.OptionWithArg;
/*     */ import emr.hbase.options.Options;
/*     */ import emr.hbase.options.SimpleOption;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URI;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Scanner;
/*     */ import java.util.TreeMap;
/*     */ import java.util.UUID;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import org.apache.commons.httpclient.HttpClient;
/*     */ import org.apache.commons.httpclient.HttpConnectionManager;
/*     */ import org.apache.commons.httpclient.SimpleHttpConnectionManager;
/*     */ import org.apache.commons.httpclient.methods.GetMethod;
/*     */ import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FSDataInputStream;
/*     */ import org.apache.hadoop.fs.FileStatus;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ import org.apache.hadoop.io.Text;
/*     */ import org.apache.hadoop.mapred.Counters;
/*     */ import org.apache.hadoop.mapred.Counters.Counter;
/*     */ import org.apache.hadoop.mapred.Counters.Group;
/*     */ import org.apache.hadoop.mapred.FileInputFormat;
/*     */ import org.apache.hadoop.mapred.FileOutputFormat;
/*     */ import org.apache.hadoop.mapred.JobClient;
/*     */ import org.apache.hadoop.mapred.JobConf;
/*     */ import org.apache.hadoop.mapred.RunningJob;
/*     */ import org.apache.hadoop.mapred.SequenceFileInputFormat;
/*     */ import org.apache.hadoop.mapred.TextOutputFormat;
/*     */ import org.apache.hadoop.util.Tool;
/*     */ 
/*     */ public class S3DistCp
/*     */   implements Tool
/*     */ {
/*  56 */   private static final Log LOG = LogFactory.getLog(S3DistCp.class);
/*     */   private static final int MAX_LIST_RETRIES = 10;
/*     */   public static final String EC2_META_AZ_URL = "http://169.254.169.254/latest/meta-data/placement/availability-zone";
/*     */   public static final String S3_ENDPOINT_PDT = "s3-us-gov-west-1.amazonaws.com";
/*  63 */   private static String ec2MetaDataAz = null;
/*     */   private Configuration conf;
/*     */ 
/*     */   public void createInputFileList(Configuration conf, Path srcPath, FileInfoListing fileInfoListing)
/*     */   {
/* 378 */     URI srcUri = srcPath.toUri();
/* 379 */     if ((srcUri.getScheme().equals("s3")) || (srcUri.getScheme().equals("s3n")))
/* 380 */       createInputFileListS3(conf, srcUri, fileInfoListing);
/*     */     else
/*     */       try {
/* 383 */         FileSystem fs = srcPath.getFileSystem(conf);
/* 384 */         Queue pathsToVisit = new ArrayDeque();
/* 385 */         pathsToVisit.add(srcPath);
/* 386 */         while (pathsToVisit.size() > 0) {
/* 387 */           Path curPath = (Path)pathsToVisit.remove();
/* 388 */           FileStatus[] statuses = fs.listStatus(curPath);
/* 389 */           for (FileStatus status : statuses)
/* 390 */             if (status.isDir())
/* 391 */               pathsToVisit.add(status.getPath());
/*     */             else
/* 393 */               fileInfoListing.add(status.getPath(), status.getLen());
/*     */         }
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 398 */         LOG.fatal("Failed to list input files", e);
/* 399 */         System.exit(-4);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void createInputFileListS3(Configuration conf, URI srcUri, FileInfoListing fileInfoListing)
/*     */   {
/* 405 */     AmazonS3Client s3Client = createAmazonS3Client(conf);
/* 406 */     ObjectListing objects = null;
/* 407 */     boolean finished = false;
/* 408 */     int retryCount = 0;
/* 409 */     String scheme = srcUri.getScheme() + "://";
/* 410 */     while (!finished) {
/* 411 */       ListObjectsRequest listObjectRequest = new ListObjectsRequest().withBucketName(srcUri.getHost());
/*     */ 
/* 413 */       if (srcUri.getPath().length() > 1) {
/* 414 */         listObjectRequest.setPrefix(srcUri.getPath().substring(1));
/*     */       }
/* 416 */       if (objects != null) {
/* 417 */         listObjectRequest.withMaxKeys(Integer.valueOf(1000)).withMarker(objects.getNextMarker());
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 422 */         objects = s3Client.listObjects(listObjectRequest);
/* 423 */         retryCount = 0;
/*     */       } catch (AmazonClientException e) {
/* 425 */         retryCount++;
/* 426 */         if (retryCount > 10) {
/* 427 */           LOG.fatal("Failed to list objects", e);
/* 428 */           throw e;
/*     */         }
/* 430 */         LOG.warn("Error listing objects: " + e.getMessage(), e);
                  continue;
/* 431 */       }
/*     */ 
/* 434 */       for (S3ObjectSummary object : objects.getObjectSummaries())
/* 435 */         if (object.getKey().endsWith("/")) {
/* 436 */           LOG.info("Skipping key '" + object.getKey() + "' because it ends with '/'");
/*     */         }
/*     */         else {
/* 439 */           String s3FilePath = scheme + object.getBucketName() + "/" + object.getKey();
/* 440 */           LOG.debug("About to add " + s3FilePath);
/* 441 */           fileInfoListing.add(new Path(s3FilePath), object.getSize());
/*     */         }
/* 443 */       if (!objects.isTruncated())
/* 444 */         finished = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static AmazonS3Client createAmazonS3Client(Configuration conf)
/*     */   {
/* 452 */     String accessKeyId = conf.get("fs.s3n.awsAccessKeyId");
/* 453 */     String SecretAccessKey = conf.get("fs.s3n.awsSecretAccessKey");
/*     */     AmazonS3Client s3Client;
/* 455 */     if ((accessKeyId != null) && (SecretAccessKey != null)) {
/* 456 */       s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKeyId, SecretAccessKey));
/* 457 */       LOG.info("Created AmazonS3Client with conf KeyId " + accessKeyId);
/*     */     } else {
/* 459 */       InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider();
/* 460 */       s3Client = new AmazonS3Client(provider);
/* 461 */       LOG.info("Created AmazonS3Client with role keyId " + provider.getCredentials().getAWSAccessKeyId());
/*     */     }
/* 463 */     String endpoint = conf.get("fs.s3n.endpoint");
/* 464 */     if ((endpoint == null) && (isGovCloud())) {
/* 465 */       endpoint = "s3-us-gov-west-1.amazonaws.com";
/*     */     }
/* 467 */     if (endpoint != null) {
/* 468 */       LOG.info("AmazonS3Client setEndpoint s3-us-gov-west-1.amazonaws.com");
/* 469 */       s3Client.setEndpoint("s3-us-gov-west-1.amazonaws.com");
/*     */     }
/* 471 */     return s3Client;
/*     */   }
/*     */ 
/*     */   private static String getHostName() {
/*     */     try {
/* 476 */       InetAddress addr = InetAddress.getLocalHost();
/* 477 */       return addr.getHostName(); } catch (UnknownHostException ex) {
/*     */     }
/* 479 */     return "unknown";
/*     */   }
/*     */ 
/*     */   private static boolean isGovCloud()
/*     */   {
/* 486 */     if (ec2MetaDataAz != null) {
/* 487 */       return ec2MetaDataAz.startsWith("us-gov-west-1");
/*     */     }
/*     */ 
/* 492 */     String hostname = getHostName();
/* 493 */     int timeout = hostname.startsWith("ip-") ? 30000 : 5000;
/* 494 */     GetMethod getMethod = new GetMethod("http://169.254.169.254/latest/meta-data/placement/availability-zone");
/*     */     try {
/* 496 */       HttpConnectionManager manager = new SimpleHttpConnectionManager();
/* 497 */       HttpConnectionManagerParams params = manager.getParams();
/*     */ 
/* 499 */       params.setConnectionTimeout(timeout);
/*     */ 
/* 501 */       params.setSoTimeout(timeout);
/* 502 */       HttpClient httpClient = new HttpClient(manager);
/* 503 */       int status = httpClient.executeMethod(getMethod);
/* 504 */       if ((status < 200) || (status > 299)) {
/* 505 */         LOG.info("error status code" + status + " GET " + "http://169.254.169.254/latest/meta-data/placement/availability-zone");
/*     */       } else {
/* 507 */         ec2MetaDataAz = getMethod.getResponseBodyAsString().trim();
/* 508 */         LOG.info("GET http://169.254.169.254/latest/meta-data/placement/availability-zone result: " + ec2MetaDataAz);
/* 509 */         return ec2MetaDataAz.startsWith("us-gov-west-1");
/*     */       }
/*     */     } catch (Exception e) {
/* 512 */       LOG.info("GET http://169.254.169.254/latest/meta-data/placement/availability-zone exception ", e);
/*     */     } finally {
/* 514 */       getMethod.releaseConnection();
/*     */     }
/* 516 */     return false;
/*     */   }
/*     */ 
/*     */   public int run(String[] args)
/*     */   {
/* 521 */     S3DistCpOptions options = new S3DistCpOptions(args, getConf());
/* 522 */     if (options.isHelpDefined()) return 0;
/* 523 */     return run(options);
/*     */   }
/*     */ 
/*     */   public int run(S3DistCpOptions options) {
/* 527 */     JobConf job = new JobConf(getConf(), S3DistCp.class);
/* 528 */     Path srcPath = new Path(options.getSrcPath());
/* 529 */     if (!srcPath.isAbsolute()) {
/* 530 */       LOG.fatal("Source path must be absolute");
/* 531 */       System.exit(5);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 537 */       FileSystem fs = FileSystem.get(srcPath.toUri(), job);
/* 538 */       srcPath = fs.getFileStatus(srcPath).getPath();
/*     */     } catch (Exception e) {
/* 540 */       LOG.fatal("Failed to get source file system", e);
/* 541 */       throw new RuntimeException("Failed to get source file system", e);
/*     */     }
/* 543 */     job.set("s3DistCp.copyfiles.srcDir", srcPath.toString());
/*     */ 
/* 546 */     String tempDirRoot = job.get("s3DistCp.copyfiles.reducer.tempDir", options.getTmpDir());
/* 547 */     if (tempDirRoot == null) {
/* 548 */       tempDirRoot = "hdfs:///tmp";
/*     */     }
/*     */ 
/* 551 */     tempDirRoot = tempDirRoot + "/" + UUID.randomUUID();
/*     */ 
/* 553 */     Path outputPath = new Path(tempDirRoot, "output");
/* 554 */     Path inputPath = new Path(tempDirRoot, "files");
/* 555 */     Path tempPath = new Path(tempDirRoot, "tempspace");
/* 556 */     Path destPath = new Path(options.getDest());
/*     */ 
/* 558 */     if (!destPath.isAbsolute()) {
/* 559 */       LOG.fatal("Destination path must be absolute");
/* 560 */       System.exit(4);
/*     */     }
/*     */ 
/* 563 */     job.set("s3DistCp.copyfiles.reducer.tempDir", tempDirRoot);
/* 564 */     LOG.info("Using output path '" + outputPath.toString() + "'");
/*     */ 
/* 566 */     job.set("s3DistCp.copyfiles.destDir", destPath.toString());
/* 567 */     job.setBoolean("s3DistCp.copyfiles.reducer.numberFiles", options.getNumberFiles().booleanValue());
/*     */ 
/* 570 */     deleteRecursive(job, inputPath);
/* 571 */     deleteRecursive(job, outputPath);
/*     */ 
/* 573 */     FileInfoListing fileInfoListing = null;
/* 574 */     File manifestFile = null;
/* 575 */     if (options.getManifestPath() != null) {
/* 576 */       manifestFile = new File(options.getManifestPath());
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 582 */       Map previousManifest = null;
/* 583 */       if (!options.copyFromManifest.booleanValue()) {
/* 584 */         previousManifest = options.getPreviousManifest();
/*     */       }
/* 586 */       fileInfoListing = new FileInfoListing(job, srcPath, inputPath, destPath, options.getStartingIndex().longValue(), manifestFile, previousManifest);
/*     */     }
/*     */     catch (IOException e1) {
/* 589 */       LOG.fatal("Error initializing manifest file", e1);
/* 590 */       System.exit(5);
/*     */     }
/*     */ 
/* 593 */     if (options.getSrcPattern() != null) {
/* 594 */       fileInfoListing.setSrcPattern(Pattern.compile(options.getSrcPattern()));
/*     */     }
/*     */ 
/* 600 */     if (options.getGroupByPattern() != null) {
/* 601 */       String groupByPattern = options.getGroupByPattern();
/* 602 */       if ((!groupByPattern.contains("(")) || (!groupByPattern.contains(")"))) {
/* 603 */         LOG.fatal("Group by pattern must contain at least one group.  Use () to enclose a group");
/* 604 */         System.exit(1);
/*     */       }
/*     */       try {
/* 607 */         fileInfoListing.setGroupBy(Pattern.compile(groupByPattern));
/* 608 */         job.set("s3DistCp.listfiles.gropubypattern", groupByPattern);
/*     */       } catch (Exception e) {
/* 610 */         System.err.println("Invalid group by pattern");
/* 611 */         System.exit(1);
/*     */       }
/*     */     }
/*     */ 
/* 615 */     if (options.getFilePerMapper() != null) {
/* 616 */       fileInfoListing.setRecordsPerFile(options.getFilePerMapper());
/*     */     }
/*     */ 
/* 622 */     if (options.getS3Endpoint() != null)
/* 623 */       job.set("fs.s3n.endpoint", options.getS3Endpoint());
/* 624 */     else if (isGovCloud()) {
/* 625 */       job.set("fs.s3n.endpoint", "s3-us-gov-west-1.amazonaws.com");
/*     */     }
/*     */ 
/* 628 */     job.setBoolean("s3DistCp.copyFiles.useMultipartUploads", !options.getDisableMultipartUpload().booleanValue());
/* 629 */     if (options.getMultipartUploadPartSize() != null) {
/* 630 */       Integer partSize = options.getMultipartUploadPartSize();
/* 631 */       job.setInt("s3DistCp.copyFiles.multipartUploadPartSize", partSize.intValue() * 1024 * 1024);
/*     */     }
/*     */     try
/*     */     {
/* 635 */       if ((options.getCopyFromManifest()) && (options.getPreviousManifest() != null)) {
/* 636 */         for (ManifestEntry entry : options.getPreviousManifest().values())
/* 637 */           fileInfoListing.add(new Path(entry.path), new Path(entry.srcDir), entry.size);
/*     */       }
/*     */       else {
/* 640 */         createInputFileList(job, srcPath, fileInfoListing);
/*     */       }
/* 642 */       LOG.info("Created " + fileInfoListing.getFileIndex() + " files to copy " + fileInfoListing.getRecordIndex() + " files ");
/*     */     }
/*     */     finally {
/* 645 */       fileInfoListing.close();
/*     */     }
/*     */ 
/* 648 */     job.setJobName("S3DistCp: " + srcPath.toString() + " -> " + destPath.toString());
/*     */ 
/* 650 */     job.setReduceSpeculativeExecution(false);
/*     */ 
/* 653 */     if (options.getTargetSize() != null) {
/*     */       try {
/* 655 */         long targetSize = options.getTargetSize().intValue();
/* 656 */         job.setLong("s3DistCp.copyfiles.reducer.targetSize", targetSize * 1024L * 1024L);
/*     */       } catch (Exception e) {
/* 658 */         System.err.println("Error parsing target file size");
/* 659 */         System.exit(2);
/*     */       }
/*     */     }
/*     */ 
/* 663 */     String outputCodec = options.getOutputCodec();
/* 664 */     job.set("s3DistCp.copyfiles.reducer.outputCodec", outputCodec);
/*     */ 
/* 666 */     job.setBoolean("s3DistCp.copyFiles.deleteFilesOnSuccess", options.getDeleteOnSuccess().booleanValue());
/*     */ 
/* 668 */     FileInputFormat.addInputPath(job, inputPath);
/* 669 */     FileOutputFormat.setOutputPath(job, outputPath);
/*     */ 
/* 671 */     job.setInputFormat(SequenceFileInputFormat.class);
/* 672 */     job.setOutputKeyClass(Text.class);
/* 673 */     job.setOutputValueClass(FileInfo.class);
/* 674 */     job.setMapperClass(GroupFilesMapper.class);
/* 675 */     job.setReducerClass(CopyFilesReducer.class);
/* 676 */     job.setOutputFormat(TextOutputFormat.class);
/*     */     try
/*     */     {
/* 679 */       RunningJob runningJob = JobClient.runJob(job);
/* 680 */       deleteRecursiveNoThrow(job, tempPath);
/* 681 */       Counters counters = runningJob.getCounters();
/* 682 */       Counters.Group group = counters.getGroup("org.apache.hadoop.mapred.Task$Counter");
/* 683 */       long reduceOutputRecords = group.getCounterForName("REDUCE_OUTPUT_RECORDS").getValue();
/* 684 */       if (reduceOutputRecords > 0L) {
/* 685 */         LOG.error(reduceOutputRecords + " files failed to copy");
/* 686 */         throw new RuntimeException(reduceOutputRecords + " files failed to copy");
/*     */       }
/* 688 */       FileSystem tempFs = FileSystem.get(tempPath.toUri(), job);
/* 689 */       tempFs.delete(tempPath, true);
/* 690 */       if (manifestFile != null) {
/* 691 */         FileSystem destFs = FileSystem.get(destPath.toUri(), job);
/* 692 */         destFs.copyFromLocalFile(new Path(manifestFile.getAbsolutePath()), destPath);
/* 693 */         manifestFile.delete();
/*     */       }
/*     */     } catch (IOException e) {
/* 696 */       deleteRecursiveNoThrow(job, tempPath);
/* 697 */       throw new RuntimeException("Error running job", e);
/*     */     }
/* 699 */     return 0;
/*     */   }
/*     */ 
/*     */   private void deleteRecursiveNoThrow(Configuration conf, Path path) {
/* 703 */     LOG.info("Try to recursively delete " + path.toString());
/*     */     try {
/* 705 */       FileSystem.get(path.toUri(), conf).delete(path, true);
/*     */     }
/*     */     catch (IOException e) {
/* 708 */       LOG.info("Failed to recursively delete " + path.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deleteRecursive(Configuration conf, Path outputPath) {
/*     */     try {
/* 714 */       FileSystem.get(outputPath.toUri(), conf).delete(outputPath, true);
/*     */     }
/*     */     catch (IOException e) {
/* 717 */       throw new RuntimeException("Unable to delete directory " + outputPath.toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Configuration getConf()
/*     */   {
/* 723 */     return this.conf;
/*     */   }
/*     */ 
/*     */   public void setConf(Configuration conf)
/*     */   {
/* 728 */     this.conf = conf;
/*     */   }
/*     */ 
/*     */   public static class S3DistCpOptions
/*     */   {
/*  69 */     private static final Log LOG = LogFactory.getLog(S3DistCpOptions.class);
/*     */     String srcPath;
/*     */     String tmpDir;
/*     */     String dest;
/*  74 */     boolean numberFiles = false;
/*     */     String srcPattern;
/*     */     Long filePerMapper;
/*     */     String groupByPattern;
/*     */     Integer targetSize;
/*  79 */     String outputCodec = "keep";
/*     */     String s3Endpoint;
/*  81 */     boolean deleteOnSuccess = false;
/*  82 */     boolean disableMultipartUpload = false;
/*     */     String manifestPath;
/*     */     Integer multipartUploadPartSize;
/*  85 */     Long startingIndex = Long.valueOf(0L);
/*     */     Map<String, ManifestEntry> previousManifest;
/*  87 */     Boolean copyFromManifest = Boolean.valueOf(false);
/*  88 */     boolean helpDefined = false;
/*     */ 
/*     */     public S3DistCpOptions()
/*     */     {
/*     */     }
/*     */ 
/*     */     public S3DistCpOptions(String[] args, Configuration conf) {
/*  95 */       Options options = new Options();
/*  96 */       SimpleOption helpOption = options.noArg("--help", "Print help text");
/*  97 */       OptionWithArg srcOption = options.withArg("--src", "Directory to copy files from");
/*  98 */       OptionWithArg destOption = options.withArg("--dest", "Directory to copy files to");
/*  99 */       OptionWithArg tmpDirOption = options.withArg("--tmpDir", "Temporary directory location");
/* 100 */       OptionWithArg srcPatternOption = options.withArg("--srcPattern", "Include only source files matching this pattern");
/* 101 */       OptionWithArg filePerMapperOption = options.withArg("--filesPerMapper", "Place up to this number of files in each map task");
/* 102 */       OptionWithArg groupByPatternOption = options.withArg("--groupBy", "Pattern to group input files by");
/* 103 */       OptionWithArg targetSizeOption = options.withArg("--targetSize", "Target size for output files");
/* 104 */       OptionWithArg outputCodecOption = options.withArg("--outputCodec", "Compression codec for output files");
/* 105 */       OptionWithArg s3EndpointOption = options.withArg("--s3Endpoint", "S3 endpoint to use for uploading files");
/* 106 */       SimpleOption deleteOnSuccessOption = options.noArg("--deleteOnSuccess", "Delete input files after a successful copy");
/* 107 */       SimpleOption disableMultipartUploadOption = options.noArg("--disableMultipartUpload", "Disable the use of multipart upload");
/* 108 */       OptionWithArg multipartUploadPartSizeOption = options.withArg("--multipartUploadChunkSize", "The size in MiB of the multipart upload part size");
/* 109 */       OptionWithArg startingIndexOption = options.withArg("--startingIndex", "The index to start with for file numbering");
/* 110 */       SimpleOption numberFilesOption = options.noArg("--numberFiles", "Prepend sequential numbers the file names");
/* 111 */       OptionWithArg outputManifest = options.withArg("--outputManifest", "The name of the manifest file");
/* 112 */       OptionWithArg previousManifest = options.withArg("--previousManifest", "The path to an existing manifest file");
/* 113 */       SimpleOption copyFromManifest = options.noArg("--copyFromManifest", "Copy from a manifest instead of listing a directory");
/* 114 */       options.parseArguments(args);
/* 115 */       if (helpOption.defined()) {
/* 116 */         LOG.info(options.helpText());
/* 117 */         this.helpDefined = true;
/*     */       }
/*     */ 
/* 120 */       srcOption.require();
/* 121 */       destOption.require();
/*     */ 
/* 123 */       if (srcOption.defined()) {
/* 124 */         setSrcPath(srcOption.value);
/*     */       }
/* 126 */       if (tmpDirOption.defined()) {
/* 127 */         setTmpDir(tmpDirOption.value);
/*     */       }
/* 129 */       if (destOption.defined()) {
/* 130 */         setDest(destOption.value);
/*     */       }
/* 132 */       if (numberFilesOption.defined()) {
/* 133 */         setNumberFiles(Boolean.valueOf(numberFilesOption.value));
/*     */       }
/* 135 */       if (srcPatternOption.defined()) {
/* 136 */         setSrcPattern(srcPatternOption.value);
/*     */       }
/* 138 */       if (filePerMapperOption.defined()) {
/* 139 */         setFilePerMapper(filePerMapperOption.value);
/*     */       }
/* 141 */       if (groupByPatternOption.defined()) {
/* 142 */         setGroupByPattern(groupByPatternOption.value);
/*     */       }
/* 144 */       if (targetSizeOption.defined()) {
/* 145 */         setTargetSize(targetSizeOption.value);
/*     */       }
/* 147 */       if (outputCodecOption.defined()) {
/* 148 */         setOutputCodec(outputCodecOption.value);
/*     */       }
/* 150 */       if (s3EndpointOption.defined()) {
/* 151 */         setS3Endpoint(s3EndpointOption.value);
/*     */       }
/* 153 */       if (deleteOnSuccessOption.defined()) {
/* 154 */         setDeleteOnSuccess(Boolean.valueOf(deleteOnSuccessOption.value));
/*     */       }
/* 156 */       if (disableMultipartUploadOption.defined()) {
/* 157 */         setDisableMultipartUpload(Boolean.valueOf(disableMultipartUploadOption.value));
/*     */       }
/* 159 */       if (multipartUploadPartSizeOption.defined()) {
/* 160 */         setMultipartUploadPartSize(multipartUploadPartSizeOption.value);
/*     */       }
/* 162 */       if (startingIndexOption.defined()) {
/* 163 */         setStartingIndex(startingIndexOption.value);
/*     */       }
/* 165 */       if (numberFilesOption.defined()) {
/* 166 */         setNumberFiles(Boolean.valueOf(numberFilesOption.value));
/*     */       }
/* 168 */       if (outputManifest.defined()) {
/* 169 */         setManifestPath(outputManifest.value);
/*     */       }
/* 171 */       if (previousManifest.defined()) {
/* 172 */         setPreviousManifest(loadManifest(new Path(previousManifest.value), conf));
/*     */       }
/* 174 */       if (copyFromManifest.defined())
/* 175 */         setCopyFromManifest(true);
/*     */     }
/*     */ 
/*     */     public static Map<String, ManifestEntry> loadManifest(Path manifestPath, Configuration config)
/*     */     {
/* 180 */       Gson gson = new Gson();
/* 181 */       Map manifest = null;
/* 182 */       FSDataInputStream inStream = null;
/*     */       try
/*     */       {
/* 185 */         manifest = new TreeMap();
/* 186 */         FileSystem fs = FileSystem.get(manifestPath.toUri(), config);
/* 187 */         inStream = fs.open(manifestPath);
/* 188 */         GZIPInputStream gzipStream = new GZIPInputStream(inStream);
/* 189 */         Scanner scanner = new Scanner(gzipStream);
/* 190 */         manifest = new TreeMap();
/* 191 */         while (scanner.hasNextLine()) {
/* 192 */           String line = scanner.nextLine();
/* 193 */           ManifestEntry entry = (ManifestEntry)gson.fromJson(line, ManifestEntry.class);
/* 194 */           manifest.put(entry.baseName, entry);
/*     */         }
                  scanner.close();
/*     */       } catch (Exception e) {
/* 197 */         LOG.error("Failed to load manifest '" + manifestPath + "'");
/*     */       } finally {
/* 199 */         if (inStream != null) {
/*     */           try {
/* 201 */             inStream.close();
/*     */           } catch (IOException e) {
/* 203 */             LOG.warn("Failed to clsoe stream for manifest file " + manifestPath, e);
/*     */           }
/*     */         }
/*     */       }
/* 207 */       return manifest;
/*     */     }
/*     */ 
/*     */     public String getSrcPath() {
/* 211 */       return this.srcPath;
/*     */     }
/*     */ 
/*     */     public void setSrcPath(String srcPath) {
/* 215 */       this.srcPath = srcPath;
/*     */     }
/*     */ 
/*     */     public String getTmpDir() {
/* 219 */       return this.tmpDir;
/*     */     }
/*     */ 
/*     */     public void setTmpDir(String tmpDir) {
/* 223 */       this.tmpDir = tmpDir;
/*     */     }
/*     */ 
/*     */     public String getDest() {
/* 227 */       return this.dest;
/*     */     }
/*     */ 
/*     */     public void setDest(String dest) {
/* 231 */       this.dest = dest;
/*     */     }
/*     */ 
/*     */     public Boolean getNumberFiles() {
/* 235 */       return Boolean.valueOf(this.numberFiles);
/*     */     }
/*     */ 
/*     */     public void setNumberFiles(Boolean numberFiles) {
/* 239 */       this.numberFiles = numberFiles.booleanValue();
/*     */     }
/*     */ 
/*     */     public String getSrcPattern() {
/* 243 */       return this.srcPattern;
/*     */     }
/*     */ 
/*     */     public void setSrcPattern(String srcPattern) {
/* 247 */       this.srcPattern = srcPattern;
/*     */     }
/*     */ 
/*     */     public Long getFilePerMapper() {
/* 251 */       return this.filePerMapper;
/*     */     }
/*     */ 
/*     */     public void setFilePerMapper(String filePerMapper) {
/* 255 */       this.filePerMapper = toLong(filePerMapper);
/*     */     }
/*     */ 
/*     */     private Long toLong(String s) {
/* 259 */       if (s != null) {
/* 260 */         return Long.valueOf(s);
/*     */       }
/*     */ 
/* 263 */       return null;
/*     */     }
/*     */ 
/*     */     private Integer toInteger(String s)
/*     */     {
/* 268 */       if (s != null) {
/* 269 */         return Integer.valueOf(s);
/*     */       }
/*     */ 
/* 272 */       return null;
/*     */     }
/*     */ 
/*     */     public String getGroupByPattern()
/*     */     {
/* 277 */       return this.groupByPattern;
/*     */     }
/*     */ 
/*     */     public void setGroupByPattern(String groupByPattern) {
/* 281 */       this.groupByPattern = groupByPattern;
/*     */     }
/*     */ 
/*     */     public Integer getTargetSize() {
/* 285 */       return this.targetSize;
/*     */     }
/*     */ 
/*     */     public void setTargetSize(String targetSize) {
/* 289 */       this.targetSize = toInteger(targetSize);
/*     */     }
/*     */ 
/*     */     public String getOutputCodec() {
/* 293 */       return this.outputCodec;
/*     */     }
/*     */ 
/*     */     public void setOutputCodec(String outputCodec) {
/* 297 */       this.outputCodec = outputCodec;
/*     */     }
/*     */ 
/*     */     public String getS3Endpoint() {
/* 301 */       return this.s3Endpoint;
/*     */     }
/*     */ 
/*     */     public void setS3Endpoint(String s3Endpoint) {
/* 305 */       this.s3Endpoint = s3Endpoint;
/*     */     }
/*     */ 
/*     */     public Boolean getDeleteOnSuccess() {
/* 309 */       return Boolean.valueOf(this.deleteOnSuccess);
/*     */     }
/*     */ 
/*     */     public void setDeleteOnSuccess(Boolean deleteOnSuccess) {
/* 313 */       this.deleteOnSuccess = deleteOnSuccess.booleanValue();
/*     */     }
/*     */ 
/*     */     public Boolean getDisableMultipartUpload() {
/* 317 */       return Boolean.valueOf(this.disableMultipartUpload);
/*     */     }
/*     */ 
/*     */     public void setDisableMultipartUpload(Boolean disableMultipartUpload) {
/* 321 */       this.disableMultipartUpload = disableMultipartUpload.booleanValue();
/*     */     }
/*     */ 
/*     */     public String getManifestPath() {
/* 325 */       return this.manifestPath;
/*     */     }
/*     */ 
/*     */     public void setManifestPath(String manifestPath) {
/* 329 */       this.manifestPath = manifestPath;
/*     */     }
/*     */ 
/*     */     public Integer getMultipartUploadPartSize() {
/* 333 */       return this.multipartUploadPartSize;
/*     */     }
/*     */ 
/*     */     public void setMultipartUploadPartSize(String multipartUploadPartSize) {
/* 337 */       this.multipartUploadPartSize = toInteger(multipartUploadPartSize);
/*     */     }
/*     */ 
/*     */     public Long getStartingIndex() {
/* 341 */       return this.startingIndex;
/*     */     }
/*     */ 
/*     */     public void setStartingIndex(String startingIndex) {
/* 345 */       if (startingIndex != null) {
/* 346 */         this.startingIndex = Long.valueOf(startingIndex);
/*     */       }
/*     */       else
/* 349 */         this.startingIndex = Long.valueOf(0L);
/*     */     }
/*     */ 
/*     */     public Map<String, ManifestEntry> getPreviousManifest()
/*     */     {
/* 354 */       return this.previousManifest;
/*     */     }
/*     */ 
/*     */     public void setPreviousManifest(Map<String, ManifestEntry> previousManifest) {
/* 358 */       this.previousManifest = previousManifest;
/*     */     }
/*     */ 
/*     */     public boolean getCopyFromManifest() {
/* 362 */       return this.copyFromManifest.booleanValue();
/*     */     }
/*     */ 
/*     */     public void setCopyFromManifest(boolean copyFromManifest) {
/* 366 */       this.copyFromManifest = Boolean.valueOf(copyFromManifest);
/*     */     }
/*     */ 
/*     */     public boolean isHelpDefined() {
/* 370 */       return this.helpDefined;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.S3DistCp
 * JD-Core Version:    0.6.2
 */