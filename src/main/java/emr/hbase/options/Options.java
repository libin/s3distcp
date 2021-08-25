package emr.hbase.options;

import com.google.common.collect.Lists;
import java.io.PrintStream;
import java.util.List;

public class Options {
  List<Option> options = Lists.newArrayList();
  List<String> extrArgs = Lists.newArrayList();

  public OptionWithArg withArg(String arg, String description) {
    OptionWithArg option = new OptionWithArg(arg, description);
    this.options.add(option);
    return option;
  }

  public SimpleOption noArg(String arg, String description) {
    SimpleOption option = new SimpleOption(arg, description);
    this.options.add(option);
    return option;
  }

  public Option add(Option option) {
    this.options.add(option);
    return option;
  }

  public String helpText() {
    StringBuffer result = new StringBuffer();
    result.append("Options:\n");
    for (Option option : this.options) {
      result.append("     ");
      result.append(option.helpLine());
      result.append("\n");
    }
    result.append("\n");
    return result.toString();
  }

  public void parseArguments(String[] args) {
    parseArguments(args, false);
  }

  public void parseArguments(String[] args, boolean allowExtraArguments) {
    int matchIndex = 0;
    int prevMatchIndex = 0;
    while (matchIndex < args.length) {
      prevMatchIndex = matchIndex;
      for (Option option : this.options) {
        matchIndex = option.matches(args, matchIndex);
        if (matchIndex >= args.length) {
          break;
        }
      }
      if (prevMatchIndex == matchIndex)
        if (allowExtraArguments) {
          this.extrArgs.add(args[matchIndex]);
          matchIndex++;
        } else {
          throw new RuntimeException("Argument " + args[matchIndex] + " doesn't match.");
        }
    }
  }

  public static void require(Option[] options) {
    try {
      for (Option option : options)
        option.require();
    } catch (RuntimeException e) {
      System.out.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * emr.hbase.options.Options JD-Core Version: 0.6.2
 */