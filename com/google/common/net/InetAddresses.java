/*      */ package com.google.common.net;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.hash.HashCode;
/*      */ import com.google.common.hash.HashFunction;
/*      */ import com.google.common.hash.Hashing;
/*      */ import com.google.common.io.ByteArrayDataInput;
/*      */ import com.google.common.io.ByteStreams;
/*      */ import com.google.common.primitives.Ints;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Arrays;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @Beta
/*      */ public final class InetAddresses
/*      */ {
/*      */   private static final int IPV4_PART_COUNT = 4;
/*      */   private static final int IPV6_PART_COUNT = 8;
/*  123 */   private static final Inet4Address LOOPBACK4 = (Inet4Address)forString("127.0.0.1");
/*  124 */   private static final Inet4Address ANY4 = (Inet4Address)forString("0.0.0.0");
/*      */ 
/*      */   private static Inet4Address getInet4Address(byte[] bytes)
/*      */   {
/*  136 */     Preconditions.checkArgument(bytes.length == 4, "Byte array has invalid length for an IPv4 address: %s != 4.", new Object[] { Integer.valueOf(bytes.length) });
/*      */ 
/*  141 */     return (Inet4Address)bytesToInetAddress(bytes);
/*      */   }
/*      */ 
/*      */   public static InetAddress forString(String ipString)
/*      */   {
/*  155 */     byte[] addr = ipStringToBytes(ipString);
/*      */ 
/*  158 */     if (addr == null) {
/*  159 */       throw new IllegalArgumentException(String.format("'%s' is not an IP string literal.", new Object[] { ipString }));
/*      */     }
/*      */ 
/*  163 */     return bytesToInetAddress(addr);
/*      */   }
/*      */ 
/*      */   public static boolean isInetAddress(String ipString)
/*      */   {
/*  174 */     return ipStringToBytes(ipString) != null;
/*      */   }
/*      */ 
/*      */   private static byte[] ipStringToBytes(String ipString)
/*      */   {
/*  179 */     boolean hasColon = false;
/*  180 */     boolean hasDot = false;
/*  181 */     for (int i = 0; i < ipString.length(); i++) {
/*  182 */       char c = ipString.charAt(i);
/*  183 */       if (c == '.') {
/*  184 */         hasDot = true;
/*  185 */       } else if (c == ':') {
/*  186 */         if (hasDot) {
/*  187 */           return null;
/*      */         }
/*  189 */         hasColon = true;
/*  190 */       } else if (Character.digit(c, 16) == -1) {
/*  191 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  196 */     if (hasColon) {
/*  197 */       if (hasDot) {
/*  198 */         ipString = convertDottedQuadToHex(ipString);
/*  199 */         if (ipString == null) {
/*  200 */           return null;
/*      */         }
/*      */       }
/*  203 */       return textToNumericFormatV6(ipString);
/*  204 */     }if (hasDot) {
/*  205 */       return textToNumericFormatV4(ipString);
/*      */     }
/*  207 */     return null;
/*      */   }
/*      */ 
/*      */   private static byte[] textToNumericFormatV4(String ipString) {
/*  211 */     String[] address = ipString.split("\\.", 5);
/*  212 */     if (address.length != 4) {
/*  213 */       return null;
/*      */     }
/*      */ 
/*  216 */     byte[] bytes = new byte[4];
/*      */     try {
/*  218 */       for (int i = 0; i < bytes.length; i++)
/*  219 */         bytes[i] = parseOctet(address[i]);
/*      */     }
/*      */     catch (NumberFormatException ex) {
/*  222 */       return null;
/*      */     }
/*      */ 
/*  225 */     return bytes;
/*      */   }
/*      */ 
/*      */   private static byte[] textToNumericFormatV6(String ipString)
/*      */   {
/*  230 */     String[] parts = ipString.split(":", 10);
/*  231 */     if ((parts.length < 3) || (parts.length > 9)) {
/*  232 */       return null;
/*      */     }
/*      */ 
/*  237 */     int skipIndex = -1;
/*  238 */     for (int i = 1; i < parts.length - 1; i++)
/*  239 */       if (parts[i].length() == 0) {
/*  240 */         if (skipIndex >= 0) {
/*  241 */           return null;
/*      */         }
/*  243 */         skipIndex = i;
/*      */       }
/*      */     int partsHi;
/*      */     int partsLo;
/*  249 */     if (skipIndex >= 0)
/*      */     {
/*  251 */       int partsHi = skipIndex;
/*  252 */       int partsLo = parts.length - skipIndex - 1;
/*  253 */       if (parts[0].length() == 0) { partsHi--; if (partsHi != 0)
/*  254 */           return null;
/*      */       }
/*  256 */       if (parts[(parts.length - 1)].length() == 0) { partsLo--; if (partsLo != 0)
/*  257 */           return null;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  262 */       partsHi = parts.length;
/*  263 */       partsLo = 0;
/*      */     }
/*      */ 
/*  268 */     int partsSkipped = 8 - (partsHi + partsLo);
/*  269 */     if (skipIndex >= 0 ? partsSkipped < 1 : partsSkipped != 0) {
/*  270 */       return null;
/*      */     }
/*      */ 
/*  274 */     ByteBuffer rawBytes = ByteBuffer.allocate(16);
/*      */     try {
/*  276 */       for (int i = 0; i < partsHi; i++) {
/*  277 */         rawBytes.putShort(parseHextet(parts[i]));
/*      */       }
/*  279 */       for (int i = 0; i < partsSkipped; i++) {
/*  280 */         rawBytes.putShort((short)0);
/*      */       }
/*  282 */       for (int i = partsLo; i > 0; i--)
/*  283 */         rawBytes.putShort(parseHextet(parts[(parts.length - i)]));
/*      */     }
/*      */     catch (NumberFormatException ex) {
/*  286 */       return null;
/*      */     }
/*  288 */     return rawBytes.array();
/*      */   }
/*      */ 
/*      */   private static String convertDottedQuadToHex(String ipString) {
/*  292 */     int lastColon = ipString.lastIndexOf(':');
/*  293 */     String initialPart = ipString.substring(0, lastColon + 1);
/*  294 */     String dottedQuad = ipString.substring(lastColon + 1);
/*  295 */     byte[] quad = textToNumericFormatV4(dottedQuad);
/*  296 */     if (quad == null) {
/*  297 */       return null;
/*      */     }
/*  299 */     String penultimate = Integer.toHexString((quad[0] & 0xFF) << 8 | quad[1] & 0xFF);
/*  300 */     String ultimate = Integer.toHexString((quad[2] & 0xFF) << 8 | quad[3] & 0xFF);
/*  301 */     return new StringBuilder().append(initialPart).append(penultimate).append(":").append(ultimate).toString();
/*      */   }
/*      */ 
/*      */   private static byte parseOctet(String ipPart)
/*      */   {
/*  306 */     int octet = Integer.parseInt(ipPart);
/*      */ 
/*  309 */     if ((octet > 255) || ((ipPart.startsWith("0")) && (ipPart.length() > 1))) {
/*  310 */       throw new NumberFormatException();
/*      */     }
/*  312 */     return (byte)octet;
/*      */   }
/*      */ 
/*      */   private static short parseHextet(String ipPart)
/*      */   {
/*  317 */     int hextet = Integer.parseInt(ipPart, 16);
/*  318 */     if (hextet > 65535) {
/*  319 */       throw new NumberFormatException();
/*      */     }
/*  321 */     return (short)hextet;
/*      */   }
/*      */ 
/*      */   private static InetAddress bytesToInetAddress(byte[] addr)
/*      */   {
/*      */     try
/*      */     {
/*  337 */       return InetAddress.getByAddress(addr);
/*      */     } catch (UnknownHostException e) {
/*  339 */       throw new AssertionError(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String toAddrString(InetAddress ip)
/*      */   {
/*  361 */     Preconditions.checkNotNull(ip);
/*  362 */     if ((ip instanceof Inet4Address))
/*      */     {
/*  364 */       return ip.getHostAddress();
/*      */     }
/*  366 */     Preconditions.checkArgument(ip instanceof Inet6Address);
/*  367 */     byte[] bytes = ip.getAddress();
/*  368 */     int[] hextets = new int[8];
/*  369 */     for (int i = 0; i < hextets.length; i++) {
/*  370 */       hextets[i] = Ints.fromBytes(0, 0, bytes[(2 * i)], bytes[(2 * i + 1)]);
/*      */     }
/*      */ 
/*  373 */     compressLongestRunOfZeroes(hextets);
/*  374 */     return hextetsToIPv6String(hextets);
/*      */   }
/*      */ 
/*      */   private static void compressLongestRunOfZeroes(int[] hextets)
/*      */   {
/*  387 */     int bestRunStart = -1;
/*  388 */     int bestRunLength = -1;
/*  389 */     int runStart = -1;
/*  390 */     for (int i = 0; i < hextets.length + 1; i++) {
/*  391 */       if ((i < hextets.length) && (hextets[i] == 0)) {
/*  392 */         if (runStart < 0)
/*  393 */           runStart = i;
/*      */       }
/*  395 */       else if (runStart >= 0) {
/*  396 */         int runLength = i - runStart;
/*  397 */         if (runLength > bestRunLength) {
/*  398 */           bestRunStart = runStart;
/*  399 */           bestRunLength = runLength;
/*      */         }
/*  401 */         runStart = -1;
/*      */       }
/*      */     }
/*  404 */     if (bestRunLength >= 2)
/*  405 */       Arrays.fill(hextets, bestRunStart, bestRunStart + bestRunLength, -1);
/*      */   }
/*      */ 
/*      */   private static String hextetsToIPv6String(int[] hextets)
/*      */   {
/*  424 */     StringBuilder buf = new StringBuilder(39);
/*  425 */     boolean lastWasNumber = false;
/*  426 */     for (int i = 0; i < hextets.length; i++) {
/*  427 */       boolean thisIsNumber = hextets[i] >= 0;
/*  428 */       if (thisIsNumber) {
/*  429 */         if (lastWasNumber) {
/*  430 */           buf.append(':');
/*      */         }
/*  432 */         buf.append(Integer.toHexString(hextets[i]));
/*      */       }
/*  434 */       else if ((i == 0) || (lastWasNumber)) {
/*  435 */         buf.append("::");
/*      */       }
/*      */ 
/*  438 */       lastWasNumber = thisIsNumber;
/*      */     }
/*  440 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public static String toUriString(InetAddress ip)
/*      */   {
/*  469 */     if ((ip instanceof Inet6Address)) {
/*  470 */       return new StringBuilder().append("[").append(toAddrString(ip)).append("]").toString();
/*      */     }
/*  472 */     return toAddrString(ip);
/*      */   }
/*      */ 
/*      */   public static InetAddress forUriString(String hostAddr)
/*      */   {
/*  491 */     Preconditions.checkNotNull(hostAddr);
/*      */     int expectBytes;
/*      */     String ipString;
/*      */     int expectBytes;
/*  496 */     if ((hostAddr.startsWith("[")) && (hostAddr.endsWith("]"))) {
/*  497 */       String ipString = hostAddr.substring(1, hostAddr.length() - 1);
/*  498 */       expectBytes = 16;
/*      */     } else {
/*  500 */       ipString = hostAddr;
/*  501 */       expectBytes = 4;
/*      */     }
/*      */ 
/*  505 */     byte[] addr = ipStringToBytes(ipString);
/*  506 */     if ((addr == null) || (addr.length != expectBytes)) {
/*  507 */       throw new IllegalArgumentException(String.format("Not a valid URI IP literal: '%s'", new Object[] { hostAddr }));
/*      */     }
/*      */ 
/*  511 */     return bytesToInetAddress(addr);
/*      */   }
/*      */ 
/*      */   public static boolean isUriInetAddress(String ipString)
/*      */   {
/*      */     try
/*      */     {
/*  523 */       forUriString(ipString);
/*  524 */       return true; } catch (IllegalArgumentException e) {
/*      */     }
/*  526 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isCompatIPv4Address(Inet6Address ip)
/*      */   {
/*  555 */     if (!ip.isIPv4CompatibleAddress()) {
/*  556 */       return false;
/*      */     }
/*      */ 
/*  559 */     byte[] bytes = ip.getAddress();
/*  560 */     if ((bytes[12] == 0) && (bytes[13] == 0) && (bytes[14] == 0) && ((bytes[15] == 0) || (bytes[15] == 1)))
/*      */     {
/*  562 */       return false;
/*      */     }
/*      */ 
/*  565 */     return true;
/*      */   }
/*      */ 
/*      */   public static Inet4Address getCompatIPv4Address(Inet6Address ip)
/*      */   {
/*  576 */     Preconditions.checkArgument(isCompatIPv4Address(ip), "Address '%s' is not IPv4-compatible.", new Object[] { toAddrString(ip) });
/*      */ 
/*  579 */     return getInet4Address(Arrays.copyOfRange(ip.getAddress(), 12, 16));
/*      */   }
/*      */ 
/*      */   public static boolean is6to4Address(Inet6Address ip)
/*      */   {
/*  597 */     byte[] bytes = ip.getAddress();
/*  598 */     return (bytes[0] == 32) && (bytes[1] == 2);
/*      */   }
/*      */ 
/*      */   public static Inet4Address get6to4IPv4Address(Inet6Address ip)
/*      */   {
/*  609 */     Preconditions.checkArgument(is6to4Address(ip), "Address '%s' is not a 6to4 address.", new Object[] { toAddrString(ip) });
/*      */ 
/*  612 */     return getInet4Address(Arrays.copyOfRange(ip.getAddress(), 2, 6));
/*      */   }
/*      */ 
/*      */   public static boolean isTeredoAddress(Inet6Address ip)
/*      */   {
/*  687 */     byte[] bytes = ip.getAddress();
/*  688 */     return (bytes[0] == 32) && (bytes[1] == 1) && (bytes[2] == 0) && (bytes[3] == 0);
/*      */   }
/*      */ 
/*      */   public static TeredoInfo getTeredoInfo(Inet6Address ip)
/*      */   {
/*  700 */     Preconditions.checkArgument(isTeredoAddress(ip), "Address '%s' is not a Teredo address.", new Object[] { toAddrString(ip) });
/*      */ 
/*  703 */     byte[] bytes = ip.getAddress();
/*  704 */     Inet4Address server = getInet4Address(Arrays.copyOfRange(bytes, 4, 8));
/*      */ 
/*  706 */     int flags = ByteStreams.newDataInput(bytes, 8).readShort() & 0xFFFF;
/*      */ 
/*  709 */     int port = (ByteStreams.newDataInput(bytes, 10).readShort() ^ 0xFFFFFFFF) & 0xFFFF;
/*      */ 
/*  711 */     byte[] clientBytes = Arrays.copyOfRange(bytes, 12, 16);
/*  712 */     for (int i = 0; i < clientBytes.length; i++)
/*      */     {
/*  714 */       clientBytes[i] = ((byte)(clientBytes[i] ^ 0xFFFFFFFF));
/*      */     }
/*  716 */     Inet4Address client = getInet4Address(clientBytes);
/*      */ 
/*  718 */     return new TeredoInfo(server, client, port, flags);
/*      */   }
/*      */ 
/*      */   public static boolean isIsatapAddress(Inet6Address ip)
/*      */   {
/*  740 */     if (isTeredoAddress(ip)) {
/*  741 */       return false;
/*      */     }
/*      */ 
/*  744 */     byte[] bytes = ip.getAddress();
/*      */ 
/*  746 */     if ((bytes[8] | 0x3) != 3)
/*      */     {
/*  750 */       return false;
/*      */     }
/*      */ 
/*  753 */     return (bytes[9] == 0) && (bytes[10] == 94) && (bytes[11] == -2);
/*      */   }
/*      */ 
/*      */   public static Inet4Address getIsatapIPv4Address(Inet6Address ip)
/*      */   {
/*  765 */     Preconditions.checkArgument(isIsatapAddress(ip), "Address '%s' is not an ISATAP address.", new Object[] { toAddrString(ip) });
/*      */ 
/*  768 */     return getInet4Address(Arrays.copyOfRange(ip.getAddress(), 12, 16));
/*      */   }
/*      */ 
/*      */   public static boolean hasEmbeddedIPv4ClientAddress(Inet6Address ip)
/*      */   {
/*  784 */     return (isCompatIPv4Address(ip)) || (is6to4Address(ip)) || (isTeredoAddress(ip));
/*      */   }
/*      */ 
/*      */   public static Inet4Address getEmbeddedIPv4ClientAddress(Inet6Address ip)
/*      */   {
/*  801 */     if (isCompatIPv4Address(ip)) {
/*  802 */       return getCompatIPv4Address(ip);
/*      */     }
/*      */ 
/*  805 */     if (is6to4Address(ip)) {
/*  806 */       return get6to4IPv4Address(ip);
/*      */     }
/*      */ 
/*  809 */     if (isTeredoAddress(ip)) {
/*  810 */       return getTeredoInfo(ip).getClient();
/*      */     }
/*      */ 
/*  813 */     throw new IllegalArgumentException(String.format("'%s' has no embedded IPv4 address.", new Object[] { toAddrString(ip) }));
/*      */   }
/*      */ 
/*      */   public static boolean isMappedIPv4Address(String ipString)
/*      */   {
/*  840 */     byte[] bytes = ipStringToBytes(ipString);
/*  841 */     if ((bytes != null) && (bytes.length == 16)) {
/*  842 */       for (int i = 0; i < 10; i++) {
/*  843 */         if (bytes[i] != 0) {
/*  844 */           return false;
/*      */         }
/*      */       }
/*  847 */       for (int i = 10; i < 12; i++) {
/*  848 */         if (bytes[i] != -1) {
/*  849 */           return false;
/*      */         }
/*      */       }
/*  852 */       return true;
/*      */     }
/*  854 */     return false;
/*      */   }
/*      */ 
/*      */   public static Inet4Address getCoercedIPv4Address(InetAddress ip)
/*      */   {
/*  878 */     if ((ip instanceof Inet4Address)) {
/*  879 */       return (Inet4Address)ip;
/*      */     }
/*      */ 
/*  883 */     byte[] bytes = ip.getAddress();
/*  884 */     boolean leadingBytesOfZero = true;
/*  885 */     for (int i = 0; i < 15; i++) {
/*  886 */       if (bytes[i] != 0) {
/*  887 */         leadingBytesOfZero = false;
/*  888 */         break;
/*      */       }
/*      */     }
/*  891 */     if ((leadingBytesOfZero) && (bytes[15] == 1))
/*  892 */       return LOOPBACK4;
/*  893 */     if ((leadingBytesOfZero) && (bytes[15] == 0)) {
/*  894 */       return ANY4;
/*      */     }
/*      */ 
/*  897 */     Inet6Address ip6 = (Inet6Address)ip;
/*  898 */     long addressAsLong = 0L;
/*  899 */     if (hasEmbeddedIPv4ClientAddress(ip6)) {
/*  900 */       addressAsLong = getEmbeddedIPv4ClientAddress(ip6).hashCode();
/*      */     }
/*      */     else
/*      */     {
/*  904 */       addressAsLong = ByteBuffer.wrap(ip6.getAddress(), 0, 8).getLong();
/*      */     }
/*      */ 
/*  908 */     int coercedHash = Hashing.murmur3_32().hashLong(addressAsLong).asInt();
/*      */ 
/*  911 */     coercedHash |= -536870912;
/*      */ 
/*  915 */     if (coercedHash == -1) {
/*  916 */       coercedHash = -2;
/*      */     }
/*      */ 
/*  919 */     return getInet4Address(Ints.toByteArray(coercedHash));
/*      */   }
/*      */ 
/*      */   public static int coerceToInteger(InetAddress ip)
/*      */   {
/*  944 */     return ByteStreams.newDataInput(getCoercedIPv4Address(ip).getAddress()).readInt();
/*      */   }
/*      */ 
/*      */   public static Inet4Address fromInteger(int address)
/*      */   {
/*  955 */     return getInet4Address(Ints.toByteArray(address));
/*      */   }
/*      */ 
/*      */   public static InetAddress fromLittleEndianByteArray(byte[] addr)
/*      */     throws UnknownHostException
/*      */   {
/*  970 */     byte[] reversed = new byte[addr.length];
/*  971 */     for (int i = 0; i < addr.length; i++) {
/*  972 */       reversed[i] = addr[(addr.length - i - 1)];
/*      */     }
/*  974 */     return InetAddress.getByAddress(reversed);
/*      */   }
/*      */ 
/*      */   public static InetAddress increment(InetAddress address)
/*      */   {
/*  987 */     byte[] addr = address.getAddress();
/*  988 */     int i = addr.length - 1;
/*  989 */     while ((i >= 0) && (addr[i] == -1)) {
/*  990 */       addr[i] = 0;
/*  991 */       i--;
/*      */     }
/*      */ 
/*  994 */     Preconditions.checkArgument(i >= 0, "Incrementing %s would wrap.", new Object[] { address });
/*      */     int tmp55_54 = i;
/*      */     byte[] tmp55_53 = addr; tmp55_53[tmp55_54] = ((byte)(tmp55_53[tmp55_54] + 1));
/*  997 */     return bytesToInetAddress(addr);
/*      */   }
/*      */ 
/*      */   public static boolean isMaximum(InetAddress address)
/*      */   {
/* 1009 */     byte[] addr = address.getAddress();
/* 1010 */     for (int i = 0; i < addr.length; i++) {
/* 1011 */       if (addr[i] != -1) {
/* 1012 */         return false;
/*      */       }
/*      */     }
/* 1015 */     return true;
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static final class TeredoInfo
/*      */   {
/*      */     private final Inet4Address server;
/*      */     private final Inet4Address client;
/*      */     private final int port;
/*      */     private final int flags;
/*      */ 
/*      */     public TeredoInfo(@Nullable Inet4Address server, @Nullable Inet4Address client, int port, int flags)
/*      */     {
/*  650 */       Preconditions.checkArgument((port >= 0) && (port <= 65535), "port '%s' is out of range (0 <= port <= 0xffff)", new Object[] { Integer.valueOf(port) });
/*      */ 
/*  652 */       Preconditions.checkArgument((flags >= 0) && (flags <= 65535), "flags '%s' is out of range (0 <= flags <= 0xffff)", new Object[] { Integer.valueOf(flags) });
/*      */ 
/*  655 */       this.server = ((Inet4Address)Objects.firstNonNull(server, InetAddresses.ANY4));
/*  656 */       this.client = ((Inet4Address)Objects.firstNonNull(client, InetAddresses.ANY4));
/*  657 */       this.port = port;
/*  658 */       this.flags = flags;
/*      */     }
/*      */ 
/*      */     public Inet4Address getServer() {
/*  662 */       return this.server;
/*      */     }
/*      */ 
/*      */     public Inet4Address getClient() {
/*  666 */       return this.client;
/*      */     }
/*      */ 
/*      */     public int getPort() {
/*  670 */       return this.port;
/*      */     }
/*      */ 
/*      */     public int getFlags() {
/*  674 */       return this.flags;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.net.InetAddresses
 * JD-Core Version:    0.6.2
 */