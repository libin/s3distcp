/*     */ package com.google.common.net;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @Beta
/*     */ @Immutable
/*     */ public final class HostAndPort
/*     */ {
/*     */   private static final int NO_PORT = -1;
/*     */   private final String host;
/*     */   private final int port;
/*     */   private final boolean hasBracketlessColons;
/* 136 */   private static final Pattern BRACKET_PATTERN = Pattern.compile("^\\[(.*:.*)\\](?::(\\d*))?$");
/*     */ 
/*     */   private HostAndPort(String host, int port, boolean hasBracketlessColons)
/*     */   {
/*  77 */     this.host = host;
/*  78 */     this.port = port;
/*  79 */     this.hasBracketlessColons = hasBracketlessColons;
/*     */   }
/*     */ 
/*     */   public String getHostText()
/*     */   {
/*  90 */     return this.host;
/*     */   }
/*     */ 
/*     */   public boolean hasPort()
/*     */   {
/*  95 */     return this.port >= 0;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 106 */     Preconditions.checkState(hasPort());
/* 107 */     return this.port;
/*     */   }
/*     */ 
/*     */   public int getPortOrDefault(int defaultPort)
/*     */   {
/* 114 */     return hasPort() ? this.port : defaultPort;
/*     */   }
/*     */ 
/*     */   public static HostAndPort fromParts(String host, int port)
/*     */   {
/* 130 */     Preconditions.checkArgument(isValidPort(port));
/* 131 */     HostAndPort parsedHost = fromString(host);
/* 132 */     Preconditions.checkArgument(!parsedHost.hasPort());
/* 133 */     return new HostAndPort(parsedHost.host, port, parsedHost.hasBracketlessColons);
/*     */   }
/*     */ 
/*     */   public static HostAndPort fromString(String hostPortString)
/*     */   {
/* 149 */     Preconditions.checkNotNull(hostPortString);
/*     */ 
/* 151 */     String portString = null;
/* 152 */     boolean hasBracketlessColons = false;
/*     */     String host;
/* 154 */     if (hostPortString.startsWith("["))
/*     */     {
/* 156 */       Matcher matcher = BRACKET_PATTERN.matcher(hostPortString);
/* 157 */       Preconditions.checkArgument(matcher.matches(), "Invalid bracketed host/port: %s", new Object[] { hostPortString });
/* 158 */       String host = matcher.group(1);
/* 159 */       portString = matcher.group(2);
/*     */     } else {
/* 161 */       int colonPos = hostPortString.indexOf(':');
/* 162 */       if ((colonPos >= 0) && (hostPortString.indexOf(':', colonPos + 1) == -1))
/*     */       {
/* 164 */         String host = hostPortString.substring(0, colonPos);
/* 165 */         portString = hostPortString.substring(colonPos + 1);
/*     */       }
/*     */       else {
/* 168 */         host = hostPortString;
/* 169 */         hasBracketlessColons = colonPos >= 0;
/*     */       }
/*     */     }
/*     */ 
/* 173 */     int port = -1;
/* 174 */     if (portString != null)
/*     */     {
/* 177 */       Preconditions.checkArgument(!portString.startsWith("+"), "Unparseable port number: %s", new Object[] { hostPortString });
/*     */       try {
/* 179 */         port = Integer.parseInt(portString);
/*     */       } catch (NumberFormatException e) {
/* 181 */         throw new IllegalArgumentException(new StringBuilder().append("Unparseable port number: ").append(hostPortString).toString());
/*     */       }
/* 183 */       Preconditions.checkArgument(isValidPort(port), "Port number out of range: %s", new Object[] { hostPortString });
/*     */     }
/*     */ 
/* 186 */     return new HostAndPort(host, port, hasBracketlessColons);
/*     */   }
/*     */ 
/*     */   public HostAndPort withDefaultPort(int defaultPort)
/*     */   {
/* 200 */     Preconditions.checkArgument(isValidPort(defaultPort));
/* 201 */     if ((hasPort()) || (this.port == defaultPort)) {
/* 202 */       return this;
/*     */     }
/* 204 */     return new HostAndPort(this.host, defaultPort, this.hasBracketlessColons);
/*     */   }
/*     */ 
/*     */   public HostAndPort requireBracketsForIPv6()
/*     */   {
/* 223 */     Preconditions.checkArgument(!this.hasBracketlessColons, "Possible bracketless IPv6 literal: %s", new Object[] { this.host });
/* 224 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 229 */     if (this == other) {
/* 230 */       return true;
/*     */     }
/* 232 */     if ((other instanceof HostAndPort)) {
/* 233 */       HostAndPort that = (HostAndPort)other;
/* 234 */       return (Objects.equal(this.host, that.host)) && (this.port == that.port) && (this.hasBracketlessColons == that.hasBracketlessColons);
/*     */     }
/*     */ 
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 243 */     return Objects.hashCode(new Object[] { this.host, Integer.valueOf(this.port), Boolean.valueOf(this.hasBracketlessColons) });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 249 */     StringBuilder builder = new StringBuilder(this.host.length() + 7);
/* 250 */     if (this.host.indexOf(':') >= 0)
/* 251 */       builder.append('[').append(this.host).append(']');
/*     */     else {
/* 253 */       builder.append(this.host);
/*     */     }
/* 255 */     if (hasPort()) {
/* 256 */       builder.append(':').append(this.port);
/*     */     }
/* 258 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static boolean isValidPort(int port)
/*     */   {
/* 263 */     return (port >= 0) && (port <= 65535);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.net.HostAndPort
 * JD-Core Version:    0.6.2
 */