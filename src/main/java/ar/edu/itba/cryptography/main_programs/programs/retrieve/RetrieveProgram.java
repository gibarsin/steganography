package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.IMAGES_DIR;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.K;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.N;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.SECRET;
import static ar.edu.itba.cryptography.services.IOService.ExitStatus.VALIDATION_FAILED;

import ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs;
import ar.edu.itba.cryptography.interfaces.MainProgram;
import ar.edu.itba.cryptography.interfaces.RetrieveAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.BMPIOService.OpenMode;
import ar.edu.itba.cryptography.services.IOService;
import ar.edu.itba.cryptography.services.IOService.ExitStatus;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RetrieveProgram implements MainProgram {
  private static final int STANDARD_K_VALUE = 8;

  private final Path pathToOutput;
  private final int k;
  private final List<Path> pathsToShadows;
  private final BMPIOService bmpIOService;

  private RetrieveProgram(final Path pathToOutput, final int k, final List<Path> pathsToShadows,
      final BMPIOService bmpIOService) {
    this.pathToOutput = pathToOutput;
    this.k = k;
    this.pathsToShadows = pathsToShadows;
    this.bmpIOService = bmpIOService;
  }

  public static MainProgram build(final Map<InputArgs, String> parsedArgs) {
    final String secret = validateArgAccess(parsedArgs, SECRET, true);
    final String kString = validateArgAccess(parsedArgs, K, true);
    validateArgAccess(parsedArgs, N, false);
    final String dir = validateArgAccess(parsedArgs, IMAGES_DIR, true);
    final Path pathToOutput = IOService.createOutputFile(secret);
    final int k = IOService.parseAsInt(kString, K.getDescription());
    final BMPIOService bmpIOService = new BMPIOService();
    final List<Path> pathsToShadows = bmpIOService.openBmpFilesFrom(dir, OpenMode.INPUT);
    if (k != pathsToShadows.size()) IOService.exit(VALIDATION_FAILED, "k != pathsToShadows.size()");
    return new RetrieveProgram(pathToOutput, k, pathsToShadows, bmpIOService);
  }

  @Override
  public void run() {
    // Choose the retrieve algorithm based on the k number
    final RetrieveAlgorithm algorithm = chooseRetrieveAlgorithm(this.k);
    // Get the bmp file data as string
    final String bmpAsString = algorithm.run(this.bmpIOService, this.pathsToShadows);
    // Write the bmp file to the specified output path
    IOService.appendToFile(this.pathToOutput, bmpAsString);
    // Close the output path resources
    IOService.closeOutputFile(this.pathToOutput);
    // Close all the shadows files paths
    bmpIOService.closeBmpFiles(this.pathsToShadows, OpenMode.INPUT);
  }

  private RetrieveAlgorithm chooseRetrieveAlgorithm(final int k) {
    if (k == STANDARD_K_VALUE) {
      return new RetrieveK8Algorithm();
    }
    return new RetrieveCustomAlgorithm();
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
