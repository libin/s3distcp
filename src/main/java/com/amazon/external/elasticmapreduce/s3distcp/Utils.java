package com.amazon.external.elasticmapreduce.s3distcp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class Utils {
  public static String randomString(long value) {
	StringBuffer result = new StringBuffer();

	if (value < 0L)
	  value = -value;
	do {
	  long remainder = value % 58L;
	  int c;
	  if (remainder < 24L) {
		c = 'a' + (char) (int) remainder;
	  } else {
		if (remainder < 48L) {
		  c = 'A' + (char) (int) (remainder - 24L);
		} else
		  c = '0' + (char) (int) (remainder - 48L);
	  }
	  result.appendCodePoint(c);
	  value /= 58L;
	} while (value > 0L);
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

  public static boolean isS3Scheme(String scheme) {
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
	return (ThreadPoolExecutor) Executors.newFixedThreadPool(10, threadFactory);
  }
  
  private static String getHostName() {
	try {
	  InetAddress addr = InetAddress.getLocalHost();
	  return addr.getHostName();
	} catch (UnknownHostException ex) {
	}
	return "unknown";
  }
  
  public static boolean isGovCloud(String ec2MetaDataAz) {
	if (ec2MetaDataAz != null) {
	  return ec2MetaDataAz.startsWith("us-gov-west-1");
	}

	String hostname = getHostName();
	int timeout = hostname.startsWith("ip-") ? 30000 : 5000;
	GetMethod getMethod = new GetMethod("http://169.254.169.254/latest/meta-data/placement/availability-zone");
	try {
	  HttpConnectionManager manager = new SimpleHttpConnectionManager();
	  HttpConnectionManagerParams params = manager.getParams();

	  params.setConnectionTimeout(timeout);

	  params.setSoTimeout(timeout);
	  HttpClient httpClient = new HttpClient(manager);
	  int status = httpClient.executeMethod(getMethod);
	  if ((status < 200) || (status > 299)) {
		
	  } else {
		ec2MetaDataAz = getMethod.getResponseBodyAsString().trim();
		
		return ec2MetaDataAz.startsWith("us-gov-west-1");
	  }
	} catch (Exception e) {
	  
	} finally {
	  getMethod.releaseConnection();
	}
	return false;
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.Utils JD-Core Version: 0.6.2
 */