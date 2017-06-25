package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.IMAGES_DIR;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.K;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.N;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.SECRET;
import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;
import static ar.edu.itba.cryptography.services.IOService.ExitStatus.VALIDATION_FAILED;

import ar.edu.itba.cryptography.helpers.InputArgsHelper;
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
import java.util.Optional;

public class RetrieveProgram implements MainProgram {
  private static final int STANDARD_K_VALUE = 8;
  private static final int MIN_K_VALUE = 2;

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
    final String secret = InputArgsHelper.validateArgAccess(parsedArgs, SECRET, true);
    final String kString = InputArgsHelper.validateArgAccess(parsedArgs, K, true);
    if (InputArgsHelper.validateArgAccess(parsedArgs, N, false) != null) {
      IOService.exit(ExitStatus.BAD_ARGUMENT, N.getDescription() + "shouldn't be specified");
    }
    final String dirString = InputArgsHelper.validateArgAccess(parsedArgs, IMAGES_DIR, false);
    final Optional<String> dir = Optional.ofNullable(dirString);
    final Path pathToOutput = IOService.createOutputFile(secret);
    final int k = IOService.parseAsInt(kString, K.getDescription());
    final BMPIOService bmpIOService = new BMPIOService();
    final List<Path> pathsToShadows = bmpIOService.openBmpFilesFrom(dir, Optional.empty(), INPUT);
    if (k < MIN_K_VALUE) IOService.exit(VALIDATION_FAILED, "k < " + MIN_K_VALUE);
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
    bmpIOService.closeBmpFiles(this.pathsToShadows, INPUT);
  }

  private RetrieveAlgorithm chooseRetrieveAlgorithm(final int k) {
    if (k == STANDARD_K_VALUE) {
      return new RetrieveK8Algorithm();
    }
    return new RetrieveCustomAlgorithm();
  }
}
