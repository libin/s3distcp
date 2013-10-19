package emr.hbase.options;

public abstract interface Option
{
  public abstract int matches(String[] paramArrayOfString, int paramInt);

  public abstract String helpLine();

  public abstract void require();

  public abstract boolean defined();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     emr.hbase.options.Option
 * JD-Core Version:    0.6.2
 */