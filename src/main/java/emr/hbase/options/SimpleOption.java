package emr.hbase.options;

public class SimpleOption extends OptionBase implements Option {
  public boolean value;

  SimpleOption(String arg, String desc) {
    super(arg, desc);
    this.value = false;
  }

  public int matches(String[] arguments, int matchIndex) {
    String argument = arguments[matchIndex];
    if (argument.equals(this.arg)) {
      this.value = true;
      return matchIndex + 1;
    }
    return matchIndex;
  }

  public String helpLine() {
    return this.arg + "   -   " + this.desc;
  }

  public void require() {
    if (!this.value)
      throw new RuntimeException("expected argument " + this.arg);
  }

  public boolean defined() {
    return this.value;
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * emr.hbase.options.SimpleOption JD-Core Version: 0.6.2
 */