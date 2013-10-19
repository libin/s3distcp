package com.google.common.hash;

import com.google.common.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public abstract interface Hasher extends PrimitiveSink
{
  public abstract Hasher putByte(byte paramByte);

  public abstract Hasher putBytes(byte[] paramArrayOfByte);

  public abstract Hasher putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract Hasher putShort(short paramShort);

  public abstract Hasher putInt(int paramInt);

  public abstract Hasher putLong(long paramLong);

  public abstract Hasher putFloat(float paramFloat);

  public abstract Hasher putDouble(double paramDouble);

  public abstract Hasher putBoolean(boolean paramBoolean);

  public abstract Hasher putChar(char paramChar);

  public abstract Hasher putString(CharSequence paramCharSequence);

  public abstract Hasher putString(CharSequence paramCharSequence, Charset paramCharset);

  public abstract <T> Hasher putObject(T paramT, Funnel<? super T> paramFunnel);

  public abstract HashCode hash();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.Hasher
 * JD-Core Version:    0.6.2
 */