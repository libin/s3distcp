package emr.hbase.options;

public class OptionWithArg extends OptionBase implements Option {
  public String value;

  public OptionWithArg(String arg, String desc) {
    super(arg, desc);
  }

  public int matches(String[] arguments, int matchIndex) {
    String argument = arguments[matchIndex];
    if (argument.equals(this.arg)) {
      if (matchIndex + 1 < arguments.length) {
        this.value = arguments[(matchIndex + 1)];
        return matchIndex + 2;
      }

      throw new RuntimeException("expected argument for " + this.arg + " but no argument was given");
    }

    if ((argument.length() >= this.arg.length() + 1)
        && (argument.substring(0, this.arg.length() + 1).equals(this.arg + "="))) {
      this.value = argument.substring(this.arg.length() + 1);
      return matchIndex + 1;
    }
    if ((argument.length() >= this.arg.length() + 2)
        && (argument.substring(0, this.arg.length() + 2).equals(this.arg + "=="))) {
      this.value = argument.substring(this.arg.length() + 2);
      return matchIndex + 1;
    }
    return matchIndex;
  }

  public String helpLine() {
    return this.arg + "=VALUE   -   " + this.desc;
  }

  public boolean defined() {
    return this.value != null;
  }

  public String getValue(String defaultValue) {
    if (this.value != null) {
      return this.value;
    }
    return defaultValue;
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * emr.hbase.options.OptionWithArg JD-Core Version: 0.6.2
 */