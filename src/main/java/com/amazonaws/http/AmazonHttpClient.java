/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.AmazonServiceException;
/*     */ import com.amazonaws.AmazonServiceException.ErrorType;
/*     */ import com.amazonaws.AmazonWebServiceRequest;
/*     */ import com.amazonaws.AmazonWebServiceResponse;
/*     */ import com.amazonaws.ClientConfiguration;
/*     */ import com.amazonaws.Request;
/*     */ import com.amazonaws.RequestClientOptions;
/*     */ import com.amazonaws.ResponseMetadata;
/*     */ import com.amazonaws.auth.Signer;
/*     */ import com.amazonaws.handlers.RequestHandler;
/*     */ import com.amazonaws.internal.CRC32MismatchException;
/*     */ import com.amazonaws.internal.CustomBackoffStrategy;
/*     */ import com.amazonaws.util.AWSRequestMetrics;
/*     */ import com.amazonaws.util.AWSRequestMetrics.Field;
/*     */ import com.amazonaws.util.CountingInputStream;
/*     */ import com.amazonaws.util.ResponseMetadataCache;
/*     */ import com.amazonaws.util.TimingInfo;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
/*     */ import org.apache.http.client.methods.HttpRequestBase;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ 
/*     */ public class AmazonHttpClient
/*     */ {
/*  61 */   private static final Log requestLog = LogFactory.getLog("com.amazonaws.request");
/*     */ 
/*  67 */   static final Log log = LogFactory.getLog(AmazonHttpClient.class);
/*     */   private final HttpClient httpClient;
/*     */   private static final int MAX_BACKOFF_IN_MILLISECONDS = 20000;
/*     */   private final ClientConfiguration config;
/*  79 */   private final ResponseMetadataCache responseMetadataCache = new ResponseMetadataCache(50);
/*     */ 
/*  81 */   private static final Random random = new Random();
/*     */ 
/*  83 */   private static HttpRequestFactory httpRequestFactory = new HttpRequestFactory();
/*  84 */   private static HttpClientFactory httpClientFactory = new HttpClientFactory();
/*     */   public static final String PROFILING_SYSTEM_PROPERTY = "com.amazonaws.sdk.enableRuntimeProfiling";
/*     */ 
/*     */   public AmazonHttpClient(ClientConfiguration clientConfiguration)
/*     */   {
/* 111 */     this.config = clientConfiguration;
/* 112 */     this.httpClient = httpClientFactory.createHttpClient(this.config);
/*     */   }
/*     */ 
/*     */   public ResponseMetadata getResponseMetadataForRequest(AmazonWebServiceRequest request)
/*     */   {
/* 130 */     return this.responseMetadataCache.get(request);
/*     */   }
/*     */ 
/*     */   public <T> T execute(Request<?> request, HttpResponseHandler<AmazonWebServiceResponse<T>> responseHandler, HttpResponseHandler<AmazonServiceException> errorResponseHandler, ExecutionContext executionContext)
/*     */     throws AmazonClientException, AmazonServiceException
/*     */   {
/* 152 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 154 */     if (executionContext == null) throw new AmazonClientException("Internal SDK Error: No execution context parameter specified.");
/* 155 */     List<RequestHandler> requestHandlers = executionContext.getRequestHandlers();
/* 156 */     if (requestHandlers == null) requestHandlers = new ArrayList();
/*     */ 
/* 159 */     for (RequestHandler requestHandler : requestHandlers) {
/* 160 */       requestHandler.beforeRequest(request);
/*     */     }
/*     */     try
/*     */     {
/* 164 */       TimingInfo timingInfo = new TimingInfo(startTime);
/* 165 */       T t = executeHelper(request, responseHandler, errorResponseHandler, executionContext);
/* 166 */       timingInfo.setEndTime(System.currentTimeMillis());
/*     */ 
/* 168 */       for (RequestHandler handler : requestHandlers)
/*     */         try {
/* 170 */           handler.afterResponse(request, t, timingInfo);
/*     */         } catch (ClassCastException cce) {
/*     */         }
/* 173 */       return t;
/*     */     } catch (AmazonClientException e) {
/* 175 */       for (RequestHandler handler : requestHandlers) {
/* 176 */         handler.afterError(request, e);
/*     */       }
/* 178 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> T executeHelper(Request<?> request, HttpResponseHandler<AmazonWebServiceResponse<T>> responseHandler, HttpResponseHandler<AmazonServiceException> errorResponseHandler, ExecutionContext executionContext)
/*     */     throws AmazonClientException, AmazonServiceException
/*     */   {
/* 201 */     boolean leaveHttpConnectionOpen = false;
/*     */ 
/* 203 */     AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
/*     */ 
/* 205 */     awsRequestMetrics.addProperty(AWSRequestMetrics.Field.ServiceName.name(), request.getServiceName());
/* 206 */     awsRequestMetrics.addProperty(AWSRequestMetrics.Field.ServiceEndpoint.name(), request.getEndpoint());
/*     */ 
/* 210 */     applyRequestData(request);
/*     */ 
/* 212 */     int retryCount = 0;
/* 213 */     URI redirectedURI = null;
/* 214 */     HttpEntity entity = null;
/* 215 */     AmazonServiceException exception = null;
/*     */ 
/* 219 */     Map originalParameters = new HashMap();
/* 220 */     originalParameters.putAll(request.getParameters());
/* 221 */     Map originalHeaders = new HashMap();
/* 222 */     originalHeaders.putAll(request.getHeaders());
/*     */     while (true)
/*     */     {
/* 225 */       awsRequestMetrics.setCounter(AWSRequestMetrics.Field.AttemptCount.name(), retryCount + 1);
/* 226 */       if (retryCount > 0) {
/* 227 */         request.setParameters(originalParameters);
/* 228 */         request.setHeaders(originalHeaders);
/*     */       }
/*     */ 
/* 231 */       HttpRequestBase httpRequest = null;
/* 232 */       org.apache.http.HttpResponse response = null;
/*     */       try
/*     */       {
/* 237 */         if ((executionContext.getSigner() != null) && (executionContext.getCredentials() != null)) {
/* 238 */           awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestSigningTime.name());
/* 239 */           executionContext.getSigner().sign(request, executionContext.getCredentials());
/* 240 */           awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestSigningTime.name());
/*     */         }
/*     */ 
/* 243 */         if (requestLog.isDebugEnabled()) {
/* 244 */           requestLog.debug("Sending Request: " + request.toString());
/*     */         }
/*     */ 
/* 247 */         httpRequest = httpRequestFactory.createHttpRequest(request, this.config, entity, executionContext);
/*     */ 
/* 249 */         if ((httpRequest instanceof HttpEntityEnclosingRequest)) {
/* 250 */           entity = ((HttpEntityEnclosingRequest)httpRequest).getEntity();
/*     */         }
/*     */ 
/* 253 */         if (redirectedURI != null) {
/* 254 */           httpRequest.setURI(redirectedURI);
/*     */         }
/*     */ 
/* 257 */         if (retryCount > 0) {
/* 258 */           awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RetryPauseTime.name());
/* 259 */           pauseExponentially(retryCount, exception, executionContext.getCustomBackoffStrategy());
/* 260 */           awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RetryPauseTime.name());
/*     */         }
/*     */         InputStream content;
/* 263 */         if (entity != null) {
/* 264 */           content = entity.getContent();
/* 265 */           if (retryCount > 0) {
/* 266 */             if (content.markSupported()) {
/* 267 */               content.reset();
/* 268 */               content.mark(-1);
/*     */             }
/*     */           }
/* 271 */           else if (content.markSupported()) {
/* 272 */             content.mark(-1);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 277 */         exception = null;
/*     */ 
/* 279 */         awsRequestMetrics.startEvent(AWSRequestMetrics.Field.HttpRequestTime.name());
/* 280 */         response = this.httpClient.execute(httpRequest);
/* 281 */         awsRequestMetrics.endEvent(AWSRequestMetrics.Field.HttpRequestTime.name());
/*     */ 
/* 284 */         if (isRequestSuccessful(response))
/*     */         {
/* 286 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.StatusCode.name(), Integer.valueOf(response.getStatusLine().getStatusCode()));
/*     */ 
/* 292 */           leaveHttpConnectionOpen = responseHandler.needsConnectionLeftOpen();
/* 293 */           return handleResponse(request, responseHandler, httpRequest, response, executionContext);
/* 294 */         }if (isTemporaryRedirect(response))
/*     */         {
/* 301 */           Header[] locationHeaders = response.getHeaders("location");
/* 302 */           String redirectedLocation = locationHeaders[0].getValue();
/* 303 */           log.debug("Redirecting to: " + redirectedLocation);
/* 304 */           redirectedURI = URI.create(redirectedLocation);
/* 305 */           httpRequest.setURI(redirectedURI);
/* 306 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.StatusCode.name(), Integer.valueOf(response.getStatusLine().getStatusCode()));
/* 307 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.RedirectLocation.name(), redirectedLocation);
/* 308 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.AWSRequestID.name(), null);
/*     */         }
/*     */         else {
/* 311 */           leaveHttpConnectionOpen = errorResponseHandler.needsConnectionLeftOpen();
/* 312 */           exception = handleErrorResponse(request, errorResponseHandler, httpRequest, response);
/* 313 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.AWSRequestID.name(), exception.getRequestId());
/* 314 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.AWSErrorCode.name(), exception.getErrorCode());
/* 315 */           awsRequestMetrics.addProperty(AWSRequestMetrics.Field.StatusCode.name(), Integer.valueOf(exception.getStatusCode()));
/*     */ 
/* 317 */           if (!shouldRetry(httpRequest, exception, retryCount)) {
/* 318 */             throw exception;
/*     */           }
/* 320 */           resetRequestAfterError(request, exception);
/*     */         }
/*     */       } catch (IOException ioe) {
/* 323 */         log.info("Unable to execute HTTP request: " + ioe.getMessage(), ioe);
/* 324 */         awsRequestMetrics.addProperty(AWSRequestMetrics.Field.Exception.name(), ioe.toString());
/* 325 */         awsRequestMetrics.addProperty(AWSRequestMetrics.Field.AWSRequestID.name(), null);
/*     */ 
/* 327 */         if (!shouldRetry(httpRequest, ioe, retryCount)) {
/* 328 */           throw new AmazonClientException("Unable to execute HTTP request: " + ioe.getMessage(), ioe);
/*     */         }
/* 330 */         resetRequestAfterError(request, ioe);
/*     */       } finally {
/* 332 */         retryCount++;
/*     */ 
/* 341 */         if (!leaveHttpConnectionOpen) try {
/* 342 */             response.getEntity().getContent().close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resetRequestAfterError(Request<?> request, Exception cause)
/*     */     throws AmazonClientException
/*     */   {
/* 363 */     if ((request.getContent() != null) && (request.getContent().markSupported()))
/*     */       try {
/* 365 */         request.getContent().reset();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 369 */         throw new AmazonClientException("Encountered an exception and couldn't reset the stream to retry", cause);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void applyRequestData(Request<?> request)
/*     */   {
/* 379 */     if (this.config.getUserAgent() != null) {
/* 380 */       request.addHeader("User-Agent", this.config.getUserAgent());
/*     */     }
/*     */ 
/* 383 */     if ((request.getOriginalRequest() != null) && (request.getOriginalRequest().getRequestClientOptions() != null) && (request.getOriginalRequest().getRequestClientOptions().getClientMarker() != null))
/*     */     {
/* 385 */       request.addHeader("User-Agent", createUserAgentString(this.config.getUserAgent(), request.getOriginalRequest().getRequestClientOptions().getClientMarker()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String createUserAgentString(String existingUserAgentString, String userAgent)
/*     */   {
/* 396 */     if (existingUserAgentString.contains(userAgent)) {
/* 397 */       return existingUserAgentString;
/*     */     }
/* 399 */     return existingUserAgentString.trim() + " " + userAgent.trim();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 410 */     IdleConnectionReaper.removeConnectionManager(this.httpClient.getConnectionManager());
/* 411 */     this.httpClient.getConnectionManager().shutdown();
/*     */   }
/*     */ 
/*     */   private boolean shouldRetry(HttpRequestBase method, Exception exception, int retries)
/*     */   {
/* 427 */     if (retries >= this.config.getMaxErrorRetry()) return false;
/*     */ 
/* 429 */     if ((method instanceof HttpEntityEnclosingRequest)) {
/* 430 */       HttpEntity entity = ((HttpEntityEnclosingRequest)method).getEntity();
/* 431 */       if ((entity != null) && (!entity.isRepeatable())) {
/* 432 */         if (log.isDebugEnabled()) {
/* 433 */           log.debug("Entity not repeatable");
/*     */         }
/* 435 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 439 */     if ((exception instanceof IOException)) {
/* 440 */       if (log.isDebugEnabled()) {
/* 441 */         log.debug("Retrying on " + exception.getClass().getName() + ": " + exception.getMessage());
/*     */       }
/*     */ 
/* 444 */       return true;
/*     */     }
/*     */ 
/* 447 */     if ((exception instanceof AmazonServiceException)) {
/* 448 */       AmazonServiceException ase = (AmazonServiceException)exception;
/*     */ 
/* 458 */       if ((ase.getStatusCode() == 500) || (ase.getStatusCode() == 503))
/*     */       {
/* 460 */         return true;
/*     */       }
/*     */ 
/* 469 */       if (isThrottlingException(ase)) return true;
/*     */     }
/*     */ 
/* 472 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isTemporaryRedirect(org.apache.http.HttpResponse response) {
/* 476 */     int status = response.getStatusLine().getStatusCode();
/* 477 */     return (status == 307) && (response.getHeaders("Location") != null) && (response.getHeaders("Location").length > 0);
/*     */   }
/*     */ 
/*     */   private boolean isRequestSuccessful(org.apache.http.HttpResponse response)
/*     */   {
/* 483 */     int status = response.getStatusLine().getStatusCode();
/* 484 */     return status / 100 == 2;
/*     */   }
/*     */ 
/*     */   private <T> T handleResponse(Request<?> request, HttpResponseHandler<AmazonWebServiceResponse<T>> responseHandler, HttpRequestBase method, org.apache.http.HttpResponse apacheHttpResponse, ExecutionContext executionContext)
/*     */     throws IOException
/*     */   {
/* 517 */     HttpResponse httpResponse = createResponse(method, request, apacheHttpResponse);
/* 518 */     if ((responseHandler.needsConnectionLeftOpen()) && ((method instanceof HttpEntityEnclosingRequest))) {
/* 519 */       HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest)method;
/* 520 */       httpResponse.setContent(new HttpMethodReleaseInputStream(httpEntityEnclosingRequest));
/*     */     }
/*     */     try
/*     */     {
/* 524 */       CountingInputStream countingInputStream = null;
/* 525 */       if (System.getProperty("com.amazonaws.sdk.enableRuntimeProfiling") != null) {
/* 526 */         countingInputStream = new CountingInputStream(httpResponse.getContent());
/* 527 */         httpResponse.setContent(countingInputStream);
/*     */       }
/*     */ 
/* 530 */       AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
/* 531 */       awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ResponseProcessingTime.name());
/* 532 */       AmazonWebServiceResponse<T> awsResponse = (AmazonWebServiceResponse)responseHandler.handle(httpResponse);
/* 533 */       awsRequestMetrics.endEvent(AWSRequestMetrics.Field.ResponseProcessingTime.name());
/* 534 */       if (countingInputStream != null) {
/* 535 */         awsRequestMetrics.setCounter(AWSRequestMetrics.Field.BytesProcessed.name(), countingInputStream.getByteCount());
/*     */       }
/*     */ 
/* 539 */       if (awsResponse == null) {
/* 540 */         throw new RuntimeException("Unable to unmarshall response metadata");
/*     */       }
/* 542 */       this.responseMetadataCache.add(request.getOriginalRequest(), awsResponse.getResponseMetadata());
/*     */ 
/* 544 */       if (requestLog.isDebugEnabled()) {
/* 545 */         requestLog.debug("Received successful response: " + apacheHttpResponse.getStatusLine().getStatusCode() + ", AWS Request ID: " + awsResponse.getRequestId());
/*     */       }
/*     */ 
/* 548 */       awsRequestMetrics.addProperty(AWSRequestMetrics.Field.AWSRequestID.name(), awsResponse.getRequestId());
/*     */ 
/* 550 */       return awsResponse.getResult();
/*     */     } catch (CRC32MismatchException e) {
/* 552 */       throw e;
/*     */     } catch (Exception e) {
/* 554 */       String errorMessage = "Unable to unmarshall response (" + e.getMessage() + ")";
/* 555 */       throw new AmazonClientException(errorMessage, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private AmazonServiceException handleErrorResponse(Request<?> request, HttpResponseHandler<AmazonServiceException> errorResponseHandler, HttpRequestBase method, org.apache.http.HttpResponse apacheHttpResponse)
/*     */     throws IOException
/*     */   {
/* 580 */     int status = apacheHttpResponse.getStatusLine().getStatusCode();
/* 581 */     HttpResponse response = createResponse(method, request, apacheHttpResponse);
/* 582 */     if ((errorResponseHandler.needsConnectionLeftOpen()) && ((method instanceof HttpEntityEnclosingRequestBase))) {
/* 583 */       HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase)method;
/* 584 */       response.setContent(new HttpMethodReleaseInputStream(entityEnclosingRequest));
/*     */     }
/*     */ 
/* 587 */     AmazonServiceException exception = null;
/*     */     try {
/* 589 */       exception = (AmazonServiceException)errorResponseHandler.handle(response);
/* 590 */       requestLog.debug("Received error response: " + exception.toString());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 594 */       if (status == 413) {
/* 595 */         exception = new AmazonServiceException("Request entity too large");
/* 596 */         exception.setServiceName(request.getServiceName());
/* 597 */         exception.setStatusCode(413);
/* 598 */         exception.setErrorType(AmazonServiceException.ErrorType.Client);
/* 599 */         exception.setErrorCode("Request entity too large");
/* 600 */       } else if ((status == 503) && ("Service Unavailable".equalsIgnoreCase(apacheHttpResponse.getStatusLine().getReasonPhrase()))) {
/* 601 */         exception = new AmazonServiceException("Service unavailable");
/* 602 */         exception.setServiceName(request.getServiceName());
/* 603 */         exception.setStatusCode(503);
/* 604 */         exception.setErrorType(AmazonServiceException.ErrorType.Service);
/* 605 */         exception.setErrorCode("Service unavailable");
/*     */       } else {
/* 607 */         String errorMessage = "Unable to unmarshall error response (" + e.getMessage() + ")";
/* 608 */         throw new AmazonClientException(errorMessage, e);
/*     */       }
/*     */     }
/*     */ 
/* 612 */     exception.setStatusCode(status);
/* 613 */     exception.setServiceName(request.getServiceName());
/* 614 */     exception.fillInStackTrace();
/* 615 */     return exception;
/*     */   }
/*     */ 
/*     */   private HttpResponse createResponse(HttpRequestBase method, Request<?> request, org.apache.http.HttpResponse apacheHttpResponse)
/*     */     throws IOException
/*     */   {
/* 635 */     HttpResponse httpResponse = new HttpResponse(request, method);
/*     */ 
/* 637 */     if (apacheHttpResponse.getEntity() != null) {
/* 638 */       httpResponse.setContent(apacheHttpResponse.getEntity().getContent());
/*     */     }
/*     */ 
/* 641 */     httpResponse.setStatusCode(apacheHttpResponse.getStatusLine().getStatusCode());
/* 642 */     httpResponse.setStatusText(apacheHttpResponse.getStatusLine().getReasonPhrase());
/* 643 */     for (Header header : apacheHttpResponse.getAllHeaders()) {
/* 644 */       httpResponse.addHeader(header.getName(), header.getValue());
/*     */     }
/*     */ 
/* 647 */     return httpResponse;
/*     */   }
/*     */ 
/*     */   private void pauseExponentially(int retries, AmazonServiceException previousException, CustomBackoffStrategy backoffStrategy)
/*     */   {
/* 660 */     long delay = 0L;
/* 661 */     if (backoffStrategy != null) {
/* 662 */       delay = backoffStrategy.getBackoffPeriod(retries);
/*     */     } else {
/* 664 */       long scaleFactor = 300L;
/* 665 */       if (isThrottlingException(previousException)) {
/* 666 */         scaleFactor = 500 + random.nextInt(100);
/*     */       }
/* 668 */       delay = (long)(Math.pow(2.0D, retries) * scaleFactor);
/*     */     }
/*     */ 
/* 671 */     delay = Math.min(delay, 20000L);
/* 672 */     if (log.isDebugEnabled()) {
/* 673 */       log.debug("Retriable error detected, will retry in " + delay + "ms, attempt number: " + retries);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 678 */       Thread.sleep(delay);
/*     */     } catch (InterruptedException e) {
/* 680 */       throw new AmazonClientException(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isThrottlingException(AmazonServiceException ase)
/*     */   {
/* 694 */     if (ase == null) return false;
/* 695 */     return ("Throttling".equals(ase.getErrorCode())) || ("ThrottlingException".equals(ase.getErrorCode())) || ("ProvisionedThroughputExceededException".equals(ase.getErrorCode()));
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 702 */     shutdown();
/* 703 */     super.finalize();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  93 */     List problematicJvmVersions = Arrays.asList(new String[] { "1.6.0_06", "1.6.0_13", "1.6.0_17" });
/*     */ 
/*  95 */     String jvmVersion = System.getProperty("java.version");
/*  96 */     if (problematicJvmVersions.contains(jvmVersion))
/*  97 */       log.warn("Detected a possible problem with the current JVM version (" + jvmVersion + ").  " + "If you experience XML parsing problems using the SDK, try upgrading to a more recent JVM update.");
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.AmazonHttpClient
 * JD-Core Version:    0.6.2
 */