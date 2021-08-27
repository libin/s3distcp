package com.amazon.external.elasticmapreduce.s3distcp;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import emr.hbase.options.OptionWithArg;
import emr.hbase.options.Options;
import emr.hbase.options.SimpleOption;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.Counters.Group;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;

public class S3DistCp implements Tool {
  private static final Log LOG = LogFactory.getLog(S3DistCp.class);
  private static final int MAX_LIST_RETRIES = 10;
  public static final String EC2_META_AZ_URL = "http://169.254.169.254/latest/meta-data/placement/availability-zone";
  public static final String S3_ENDPOINT_PDT = "s3-us-gov-west-1.amazonaws.com";
  private static String ec2MetaDataAz = null;
  private Configuration conf;

  public void createInputFileList(Configuration conf, Path srcPath, FileInfoListing fileInfoListing) {
    URI srcUri = srcPath.toUri();
    if ((srcUri.getScheme().equals("s3")) || (srcUri.getScheme().equals("s3n")))
      createInputFileListS3(conf, srcUri, fileInfoListing);
    else
      try {
        FileSystem fs = srcPath.getFileSystem(conf);
        Queue pathsToVisit = new ArrayDeque();
        pathsToVisit.add(srcPath);
        while (pathsToVisit.size() > 0) {
          Path curPath = (Path) pathsToVisit.remove();
          FileStatus[] statuses = fs.listStatus(curPath);
          for (FileStatus status : statuses)
            if (status.isDir())
              pathsToVisit.add(status.getPath());
            else
              fileInfoListing.add(status.getPath(), status.getLen());
        }
      } catch (IOException e) {
        LOG.fatal("Failed to list input files", e);
        System.exit(-4);
      }
  }

  public void createInputFileListS3(Configuration conf, URI srcUri, FileInfoListing fileInfoListing) {
    AmazonS3Client s3Client = createAmazonS3Client(conf);
    ObjectListing objects = null;
    boolean finished = false;
    int retryCount = 0;
    String scheme = srcUri.getScheme() + "://";
    while (!finished) {
      ListObjectsRequest listObjectRequest = new ListObjectsRequest().withBucketName(srcUri.getHost());

      if (srcUri.getPath().length() > 1) {
        listObjectRequest.setPrefix(srcUri.getPath().substring(1));
      }
      if (objects != null) {
        listObjectRequest.withMaxKeys(Integer.valueOf(1000)).withMarker(objects.getNextMarker());
      }

      try {
        objects = s3Client.listObjects(listObjectRequest);
        retryCount = 0;
      } catch (AmazonClientException e) {
        retryCount++;
        if (retryCount > 10) {
          LOG.fatal("Failed to list objects", e);
          throw e;
        }
        LOG.warn("Error listing objects: " + e.getMessage(), e);
        continue;
      }

      for (S3ObjectSummary object : objects.getObjectSummaries())
        if (object.getKey().endsWith("/")) {
          LOG.info("Skipping key '" + object.getKey() + "' because it ends with '/'");
        } else {
          String s3FilePath = scheme + object.getBucketName() + "/" + object.getKey();
          LOG.debug("About to add " + s3FilePath);
          fileInfoListing.add(new Path(s3FilePath), object.getSize());
        }
      if (!objects.isTruncated())
        finished = true;
    }
  }

  public static AmazonS3Client createAmazonS3Client(Configuration conf) {
    String accessKeyId = conf.get("fs.s3n.awsAccessKeyId");
    String SecretAccessKey = conf.get("fs.s3n.awsSecretAccessKey");
    AmazonS3Client s3Client;
    if ((accessKeyId != null) && (SecretAccessKey != null)) {
      s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKeyId, SecretAccessKey));
      LOG.info("Created AmazonS3Client with conf KeyId " + accessKeyId);
    } else {
      InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider();
      s3Client = new AmazonS3Client(provider);
      LOG.info("Created AmazonS3Client with role keyId " + provider.getCredentials().getAWSAccessKeyId());
    }
    String endpoint = conf.get("fs.s3n.endpoint");
    if ((endpoint == null) && (Utils.isGovCloud(ec2MetaDataAz))) {
      endpoint = "s3-us-gov-west-1.amazonaws.com";
    }
    if (endpoint != null) {
      LOG.info("AmazonS3Client setEndpoint " + endpoint);
      s3Client.setEndpoint(endpoint);
    }
    return s3Client;
  }

  public int run(String[] args) {
    S3DistCpOptions options = new S3DistCpOptions(args, getConf());
    if (options.isHelpDefined())
      return 0;
    return run(options);
  }

  public int run(S3DistCpOptions options) {
    JobConf job = new JobConf(getConf(), S3DistCp.class);
    Path srcPath = new Path(options.getSrcPath());
    if (!srcPath.isAbsolute()) {
      LOG.fatal("Source path must be absolute");
      System.exit(5);
    }

    try {
      FileSystem fs = FileSystem.get(srcPath.toUri(), job);
      srcPath = fs.getFileStatus(srcPath).getPath();
    } catch (Exception e) {
      LOG.fatal("Failed to get source file system", e);
      throw new RuntimeException("Failed to get source file system", e);
    }
    job.set("s3DistCp.copyfiles.srcDir", srcPath.toString());

    String tempDirRoot = job.get("s3DistCp.copyfiles.reducer.tempDir", options.getTmpDir());
    if (tempDirRoot == null) {
      tempDirRoot = "hdfs:///tmp";
    }

    tempDirRoot = tempDirRoot + "/" + UUID.randomUUID();

    Path outputPath = new Path(tempDirRoot, "output");
    Path inputPath = new Path(tempDirRoot, "files");
    Path tempPath = new Path(tempDirRoot, "tempspace");
    Path destPath = new Path(options.getDest());

    if (!destPath.isAbsolute()) {
      LOG.fatal("Destination path must be absolute");
      System.exit(4);
    }

    job.set("s3DistCp.copyfiles.reducer.tempDir", tempDirRoot);
    LOG.info("Using output path '" + outputPath.toString() + "'");

    job.set("s3DistCp.copyfiles.destDir", destPath.toString());
    job.setBoolean("s3DistCp.copyfiles.reducer.numberFiles", options.getNumberFiles().booleanValue());

    deleteRecursive(job, inputPath);
    deleteRecursive(job, outputPath);

    FileInfoListing fileInfoListing = null;
    File manifestFile = null;
    if (options.getManifestPath() != null) {
      manifestFile = new File(options.getManifestPath());
    }

    try {
      Map previousManifest = null;
      if (!options.copyFromManifest.booleanValue()) {
        previousManifest = options.getPreviousManifest();
      }
      fileInfoListing = new FileInfoListing(job, srcPath, inputPath, destPath, options.getStartingIndex().longValue(),
          manifestFile, previousManifest);
    } catch (IOException e1) {
      LOG.fatal("Error initializing manifest file", e1);
      System.exit(5);
    }

    if (options.getSrcPattern() != null) {
      fileInfoListing.setSrcPattern(Pattern.compile(options.getSrcPattern()));
    }

    if (options.getGroupByPattern() != null) {
      String groupByPattern = options.getGroupByPattern();
      if ((!groupByPattern.contains("(")) || (!groupByPattern.contains(")"))) {
        LOG.fatal("Group by pattern must contain at least one group.  Use () to enclose a group");
        System.exit(1);
      }
      try {
        fileInfoListing.setGroupBy(Pattern.compile(groupByPattern));
        job.set("s3DistCp.listfiles.gropubypattern", groupByPattern);
      } catch (Exception e) {
        System.err.println("Invalid group by pattern");
        System.exit(1);
      }
      boolean groupWithNewLine = options.getGroupWithNewLine();
      if (groupWithNewLine) {
        job.set("s3DistCp.listfiles.gropubypattern.withnewline", "true");
      } else {
        job.set("s3DistCp.listfiles.gropubypattern.withnewline", "false");
      }
    }

    if (options.getFilePerMapper() != null) {
      fileInfoListing.setRecordsPerFile(options.getFilePerMapper());
    }

    if (options.getS3Endpoint() != null)
      job.set("fs.s3n.endpoint", options.getS3Endpoint());
    else if (Utils.isGovCloud(ec2MetaDataAz)) {
      job.set("fs.s3n.endpoint", "s3-us-gov-west-1.amazonaws.com");
    }

    job.setBoolean("s3DistCp.copyFiles.useMultipartUploads", !options.getDisableMultipartUpload().booleanValue());
    if (options.getMultipartUploadPartSize() != null) {
      Integer partSize = options.getMultipartUploadPartSize();
      job.setInt("s3DistCp.copyFiles.multipartUploadPartSize", partSize.intValue() * 1024 * 1024);
    }
    
    job.setBoolean("s3DistCp.groupWithNewLine", options.getGroupWithNewLine());
    job.setInt("s3DistCp.numberDeletePartition", options.getNumberDeletePartition());

    try {
      if ((options.getCopyFromManifest()) && (options.getPreviousManifest() != null)) {
        for (ManifestEntry entry : options.getPreviousManifest().values())
          fileInfoListing.add(new Path(entry.path), new Path(entry.srcDir), entry.size);
      } else {
        createInputFileList(job, srcPath, fileInfoListing);
      }
      LOG.info("Created " + fileInfoListing.getFileIndex() + " files to copy " + fileInfoListing.getRecordIndex()
          + " files ");
    } finally {
      fileInfoListing.close();
    }

    job.setJobName("S3DistCp: " + srcPath.toString() + " -> " + destPath.toString());

    job.setReduceSpeculativeExecution(false);

    if (options.getTargetSize() != null) {
      try {
        long targetSize = options.getTargetSize().intValue();
        job.setLong("s3DistCp.copyfiles.reducer.targetSize", targetSize * 1024L * 1024L);
      } catch (Exception e) {
        System.err.println("Error parsing target file size");
        System.exit(2);
      }
    }

    String outputCodec = options.getOutputCodec();
    job.set("s3DistCp.copyfiles.reducer.outputCodec", outputCodec);

    job.setBoolean("s3DistCp.copyFiles.deleteFilesOnSuccess", options.getDeleteOnSuccess().booleanValue());

    FileInputFormat.addInputPath(job, inputPath);
    FileOutputFormat.setOutputPath(job, outputPath);

    job.setInputFormat(SequenceFileInputFormat.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(FileInfo.class);
    job.setMapperClass(GroupFilesMapper.class);
    job.setReducerClass(CopyFilesReducer.class);
    job.setOutputFormat(TextOutputFormat.class);
    try {
      RunningJob runningJob = JobClient.runJob(job);
      deleteRecursiveNoThrow(job, tempPath);
      Counters counters = runningJob.getCounters();
      Counters.Group group = counters.getGroup("org.apache.hadoop.mapred.Task$Counter");
      long reduceOutputRecords = group.getCounterForName("REDUCE_OUTPUT_RECORDS").getValue();
      if (reduceOutputRecords > 0L) {
        LOG.error(reduceOutputRecords + " files failed to copy");
        throw new RuntimeException(reduceOutputRecords + " files failed to copy");
      }
      FileSystem tempFs = FileSystem.get(tempPath.toUri(), job);
      tempFs.delete(tempPath, true);
      if (manifestFile != null) {
        FileSystem destFs = FileSystem.get(destPath.toUri(), job);
        destFs.copyFromLocalFile(new Path(manifestFile.getAbsolutePath()), destPath);
        manifestFile.delete();
      }
    } catch (IOException e) {
      deleteRecursiveNoThrow(job, tempPath);
      throw new RuntimeException("Error running job", e);
    }
    return 0;
  }

  private void deleteRecursiveNoThrow(Configuration conf, Path path) {
    LOG.info("Try to recursively delete " + path.toString());
    try {
      FileSystem.get(path.toUri(), conf).delete(path, true);
    } catch (IOException e) {
      LOG.info("Failed to recursively delete " + path.toString());
    }
  }

  private void deleteRecursive(Configuration conf, Path outputPath) {
    try {
      FileSystem.get(outputPath.toUri(), conf).delete(outputPath, true);
    } catch (IOException e) {
      throw new RuntimeException("Unable to delete directory " + outputPath.toString(), e);
    }
  }

  public Configuration getConf() {
    return this.conf;
  }

  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  public static class S3DistCpOptions {
    private static final Log LOG = LogFactory.getLog(S3DistCpOptions.class);
    String srcPath;
    String tmpDir;
    String dest;
    boolean numberFiles = false;
    String srcPattern;
    Long filePerMapper;
    String groupByPattern;
    boolean groupWithNewLine = false;
    Integer numberDeletePartition = 0;
    Integer targetSize;
    String outputCodec = "keep";
    String s3Endpoint;
    boolean deleteOnSuccess = false;
    boolean disableMultipartUpload = false;
    String manifestPath;
    Integer multipartUploadPartSize;
    Long startingIndex = Long.valueOf(0L);
    Map<String, ManifestEntry> previousManifest;
    Boolean copyFromManifest = Boolean.valueOf(false);
    boolean helpDefined = false;

    public S3DistCpOptions() {
    }

    public S3DistCpOptions(String[] args, Configuration conf) {
      Options options = new Options();
      SimpleOption helpOption = options.noArg("--help", "Print help text");
      OptionWithArg srcOption = options.withArg("--src", "Directory to copy files from");
      OptionWithArg destOption = options.withArg("--dest", "Directory to copy files to");
      OptionWithArg tmpDirOption = options.withArg("--tmpDir", "Temporary directory location");
      OptionWithArg srcPatternOption = options.withArg("--srcPattern",
          "Include only source files matching this pattern");
      OptionWithArg filePerMapperOption = options.withArg("--filesPerMapper",
          "Place up to this number of files in each map task");
      OptionWithArg groupByPatternOption = options.withArg("--groupBy", "Pattern to group input files by");
      OptionWithArg groupWithNewLineOption = options.withArg("--groupWithNewLine", "Grouping with new line option");
      OptionWithArg numberDeletePartition = options.withArg("--numberDeletePartition", "Number of delete partitions");

      OptionWithArg targetSizeOption = options.withArg("--targetSize", "Target size for output files");
      OptionWithArg outputCodecOption = options.withArg("--outputCodec", "Compression codec for output files");
      OptionWithArg s3EndpointOption = options.withArg("--s3Endpoint", "S3 endpoint to use for uploading files");
      SimpleOption deleteOnSuccessOption = options.noArg("--deleteOnSuccess",
          "Delete input files after a successful copy");
      SimpleOption disableMultipartUploadOption = options.noArg("--disableMultipartUpload",
          "Disable the use of multipart upload");
      OptionWithArg multipartUploadPartSizeOption = options.withArg("--multipartUploadChunkSize",
          "The size in MiB of the multipart upload part size");
      OptionWithArg startingIndexOption = options.withArg("--startingIndex",
          "The index to start with for file numbering");
      SimpleOption numberFilesOption = options.noArg("--numberFiles", "Prepend sequential numbers the file names");
      OptionWithArg outputManifest = options.withArg("--outputManifest", "The name of the manifest file");
      OptionWithArg previousManifest = options.withArg("--previousManifest", "The path to an existing manifest file");
      SimpleOption copyFromManifest = options.noArg("--copyFromManifest",
          "Copy from a manifest instead of listing a directory");
      options.parseArguments(args);
      if (helpOption.defined()) {
        LOG.info(options.helpText());
        this.helpDefined = true;
      }

      srcOption.require();
      destOption.require();

      if (srcOption.defined()) {
        setSrcPath(srcOption.value);
      }
      if (tmpDirOption.defined()) {
        setTmpDir(tmpDirOption.value);
      }
      if (destOption.defined()) {
        setDest(destOption.value);
      }
      if (numberFilesOption.defined()) {
        setNumberFiles(Boolean.valueOf(numberFilesOption.value));
      }
      if (srcPatternOption.defined()) {
        setSrcPattern(srcPatternOption.value);
      }
      if (filePerMapperOption.defined()) {
        setFilePerMapper(filePerMapperOption.value);
      }
      if (groupByPatternOption.defined()) {
        setGroupByPattern(groupByPatternOption.value);
      }
      if (groupWithNewLineOption.defined()) {
        setGroupWithNewLine(Boolean.valueOf(groupWithNewLineOption.value));
      }
      if (numberDeletePartition.defined()) {
        setNumberDeletePartition(Integer.valueOf(numberDeletePartition.value));
      }

      if (targetSizeOption.defined()) {
        setTargetSize(targetSizeOption.value);
      }
      if (outputCodecOption.defined()) {
        setOutputCodec(outputCodecOption.value);
      }
      if (s3EndpointOption.defined()) {
        setS3Endpoint(s3EndpointOption.value);
      }
      if (deleteOnSuccessOption.defined()) {
        setDeleteOnSuccess(Boolean.valueOf(deleteOnSuccessOption.value));
      }
      if (disableMultipartUploadOption.defined()) {
        setDisableMultipartUpload(Boolean.valueOf(disableMultipartUploadOption.value));
      }
      if (multipartUploadPartSizeOption.defined()) {
        setMultipartUploadPartSize(multipartUploadPartSizeOption.value);
      }
      if (startingIndexOption.defined()) {
        setStartingIndex(startingIndexOption.value);
      }
      if (numberFilesOption.defined()) {
        setNumberFiles(Boolean.valueOf(numberFilesOption.value));
      }
      if (outputManifest.defined()) {
        setManifestPath(outputManifest.value);
      }
      if (previousManifest.defined()) {
        setPreviousManifest(loadManifest(new Path(previousManifest.value), conf));
      }
      if (copyFromManifest.defined())
        setCopyFromManifest(true);
    }

    public static Map<String, ManifestEntry> loadManifest(Path manifestPath, Configuration config) {
      Gson gson = new Gson();
      Map manifest = null;
      FSDataInputStream inStream = null;
      try {
        manifest = new TreeMap();
        FileSystem fs = FileSystem.get(manifestPath.toUri(), config);
        inStream = fs.open(manifestPath);
        GZIPInputStream gzipStream = new GZIPInputStream(inStream);
        Scanner scanner = new Scanner(gzipStream);
        manifest = new TreeMap();
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          ManifestEntry entry = (ManifestEntry) gson.fromJson(line, ManifestEntry.class);
          manifest.put(entry.baseName, entry);
        }
        scanner.close();
      } catch (Exception e) {
        LOG.error("Failed to load manifest '" + manifestPath + "'");
      } finally {
        if (inStream != null) {
          try {
            inStream.close();
          } catch (IOException e) {
            LOG.warn("Failed to clsoe stream for manifest file " + manifestPath, e);
          }
        }
      }
      return manifest;
    }

    public String getSrcPath() {
      return this.srcPath;
    }

    public void setSrcPath(String srcPath) {
      this.srcPath = srcPath;
    }

    public String getTmpDir() {
      return this.tmpDir;
    }

    public void setTmpDir(String tmpDir) {
      this.tmpDir = tmpDir;
    }

    public String getDest() {
      return this.dest;
    }

    public void setDest(String dest) {
      this.dest = dest;
    }

    public Boolean getNumberFiles() {
      return Boolean.valueOf(this.numberFiles);
    }

    public void setNumberFiles(Boolean numberFiles) {
      this.numberFiles = numberFiles.booleanValue();
    }

    public String getSrcPattern() {
      return this.srcPattern;
    }

    public void setSrcPattern(String srcPattern) {
      this.srcPattern = srcPattern;
    }

    public Long getFilePerMapper() {
      return this.filePerMapper;
    }

    public void setFilePerMapper(String filePerMapper) {
      this.filePerMapper = toLong(filePerMapper);
    }

    private Long toLong(String s) {
      if (s != null) {
        return Long.valueOf(s);
      }

      return null;
    }

    private Integer toInteger(String s) {
      if (s != null) {
        return Integer.valueOf(s);
      }

      return null;
    }

    public String getGroupByPattern() {
      return this.groupByPattern;
    }

    public void setGroupByPattern(String groupByPattern) {
      this.groupByPattern = groupByPattern;
    }

    public boolean getGroupWithNewLine() {
      return this.groupWithNewLine;
    }

    public void setGroupWithNewLine(boolean a) {
      this.groupWithNewLine = a;
    }

    public int getNumberDeletePartition() {
      return this.numberDeletePartition;
    }

    public void setNumberDeletePartition(int a) {
      this.numberDeletePartition = a;
    }

    public Integer getTargetSize() {
      return this.targetSize;
    }

    public void setTargetSize(String targetSize) {
      this.targetSize = toInteger(targetSize);
    }

    public String getOutputCodec() {
      return this.outputCodec;
    }

    public void setOutputCodec(String outputCodec) {
      this.outputCodec = outputCodec;
    }

    public String getS3Endpoint() {
      return this.s3Endpoint;
    }

    public void setS3Endpoint(String s3Endpoint) {
      this.s3Endpoint = s3Endpoint;
    }

    public Boolean getDeleteOnSuccess() {
      return Boolean.valueOf(this.deleteOnSuccess);
    }

    public void setDeleteOnSuccess(Boolean deleteOnSuccess) {
      this.deleteOnSuccess = deleteOnSuccess.booleanValue();
    }

    public Boolean getDisableMultipartUpload() {
      return Boolean.valueOf(this.disableMultipartUpload);
    }

    public void setDisableMultipartUpload(Boolean disableMultipartUpload) {
      this.disableMultipartUpload = disableMultipartUpload.booleanValue();
    }

    public String getManifestPath() {
      return this.manifestPath;
    }

    public void setManifestPath(String manifestPath) {
      this.manifestPath = manifestPath;
    }

    public Integer getMultipartUploadPartSize() {
      return this.multipartUploadPartSize;
    }

    public void setMultipartUploadPartSize(String multipartUploadPartSize) {
      this.multipartUploadPartSize = toInteger(multipartUploadPartSize);
    }

    public Long getStartingIndex() {
      return this.startingIndex;
    }

    public void setStartingIndex(String startingIndex) {
      if (startingIndex != null) {
        this.startingIndex = Long.valueOf(startingIndex);
      } else
        this.startingIndex = Long.valueOf(0L);
    }

    public Map<String, ManifestEntry> getPreviousManifest() {
      return this.previousManifest;
    }

    public void setPreviousManifest(Map<String, ManifestEntry> previousManifest) {
      this.previousManifest = previousManifest;
    }

    public boolean getCopyFromManifest() {
      return this.copyFromManifest.booleanValue();
    }

    public void setCopyFromManifest(boolean copyFromManifest) {
      this.copyFromManifest = Boolean.valueOf(copyFromManifest);
    }

    public boolean isHelpDefined() {
      return this.helpDefined;
    }
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.S3DistCp JD-Core Version: 0.6.2
 */