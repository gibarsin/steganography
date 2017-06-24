package ar.edu.itba.cryptography.main_programs.programs;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.*;

import ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs;
import ar.edu.itba.cryptography.interfaces.MainProgram;
import ar.edu.itba.cryptography.services.IOService;
import ar.edu.itba.cryptography.services.IOService.ExitStatus;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RetrieveProgram implements MainProgram {
  private final Path pathToOutput;
  private final int k;
  private final List<Path> pathsToShadows;

  private RetrieveProgram(final Path pathToOutput,
      final int k, final List<Path> pathsToShadows) {
    this.pathToOutput = pathToOutput;
    this.k = k;
    this.pathsToShadows = pathsToShadows;
  }

  @Override
  public void run() {
    // TODO: DOING
    pathsToShadows.forEach(System.out::println);
  }

  public static MainProgram build(final Map<InputArgs, String> parsedArgs) {
    final String secret = validateArgAccess(parsedArgs, SECRET, true);
    final String kString = validateArgAccess(parsedArgs, K, true);
    validateArgAccess(parsedArgs, N, false);
    final String dir = validateArgAccess(parsedArgs, IMAGES_DIR, true);

    final Path pathToOutput = IOService.createOutputFile(secret);
    final int k = IOService.parseAsInt(kString, K.getDescription());
    final List<Path> pathsToShadows = IOService.openAllByteFilesFrom(dir); // TODO
    return new RetrieveProgram(pathToOutput, k, pathsToShadows);
  }

  /**
   *
   * @param parsedArgs all the parsed arguments
   * @param arg the argument to be retrieved, if access is allowed
   * @param shouldBeDefined true if arg is expected to be != null; false if should be == null
   * @return the parsed argument value, if access is allowed
   * @implNote
   *  null && should => error: missing argument
   *  null && !should => OK
   *  !null && should => OK
   *  !null && !should => error: extra argument
   */
  private static String validateArgAccess(final Map<InputArgs, String> parsedArgs,
      final InputArgs arg, final boolean shouldBeDefined) {
    final String parsedArg = parsedArgs.get(arg);
    if (parsedArg == null && shouldBeDefined) {
      IOService.exit(ExitStatus.BAD_ARGUMENT, "Undefined parameter: " + arg.getDescription());
    } else if (parsedArg != null && !shouldBeDefined) {
      IOService.exit(ExitStatus.BAD_ARGUMENT, "Extra defined parameter: " + arg.getDescription());
    }
    return parsedArg;
  }
}
