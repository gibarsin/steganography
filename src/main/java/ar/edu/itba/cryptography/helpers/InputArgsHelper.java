package ar.edu.itba.cryptography.helpers;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.*;
import static ar.edu.itba.cryptography.services.IOService.ExitStatus.BAD_ARGUMENT;

import ar.edu.itba.cryptography.services.IOService;
import java.util.HashMap;
import java.util.Map;

public class InputArgsHelper {
  public enum InputArgs {
    MAIN_PROGRAM("<main_program>", ""),
    HELP_PROGRAM("-h", ""),
    DISTRIBUTION_PROGRAM("-d", ""),
    RETRIEVE_PROGRAM("-r", ""),
    SECRET("-secret", "</path/to/image>"),
    K("-k", "<number>"),
    N("-n", "<number>"),
    IMAGES_DIR("-dir", "<images_directory");

    private final String type;
    private final String description;
    InputArgs(final String type, final String extraArg) {
      this.type = type;
      this.description = type + " " + extraArg;
    }

    public String getType() {
      return type;
    }

    public String getDescription() {
      return description;
    }
  }

  private static final Map<String, InputArgs> inputArgs;
  static {
    inputArgs = new HashMap<>();
    inputArgs.put(SECRET.getType(), SECRET);
    inputArgs.put(K.getType(), K);
    inputArgs.put(N.getType(), N);
    inputArgs.put(IMAGES_DIR.getType(), IMAGES_DIR);
  }

  public static Map<InputArgs, String> parseArgs(final String[] args) {
    final Map<InputArgs, String> parsedArgs = new HashMap<>();
    int i = 0;
    // Get main program
    parsedArgs.put(MAIN_PROGRAM, IOService.validArgsAccess(args, i++));
    // Get the other variables
    while (i + 1 <= args.length) { // This + 1 is for also validating the param value access
      final int paramTypeIndex = i, paramValueIndex = i + 1;
      final String paramType = IOService.validArgsAccess(args, paramTypeIndex);
      final String paramValue = IOService.validArgsAccess(args, paramValueIndex);
      final InputArgs paramTypeArg = inputArgs.get(paramType);
      if (paramTypeArg == null) {
        IOService.exit(BAD_ARGUMENT, paramType);
        throw new IllegalStateException(); // Should never reach here
      }
      parsedArgs.put(paramTypeArg, paramValue);
      i += 2; // We have just read 2 input args
    }
    return parsedArgs;
  }
}
