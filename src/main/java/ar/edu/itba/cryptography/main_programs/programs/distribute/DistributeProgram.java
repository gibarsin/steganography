package ar.edu.itba.cryptography.main_programs.programs.distribute;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.IMAGES_DIR;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.K;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.N;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.SECRET;
import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;
import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.OUTPUT;
import static ar.edu.itba.cryptography.services.IOService.ExitStatus.VALIDATION_FAILED;

import ar.edu.itba.cryptography.helpers.InputArgsHelper;
import ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs;
import ar.edu.itba.cryptography.interfaces.DistributeAlgorithm;
import ar.edu.itba.cryptography.interfaces.MainProgram;
import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.IOService;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DistributeProgram implements MainProgram {
  private static final int STANDARD_K_VALUE = 8;

  private final Path pathToSecret;
  private final int k;
  private final List<Path> pathsToShadows;
  private final BMPIOService bmpIOService;

  private DistributeProgram(final Path pathToSecret, final int k,
      final List<Path> pathsToShadows, final BMPIOService bmpIOService) {
    this.pathToSecret = pathToSecret;
    this.k = k;
    this.pathsToShadows = pathsToShadows;
    this.bmpIOService = bmpIOService;
  }

  public static MainProgram build(final Map<InputArgs, String> parsedArgs) {
    final String secret = InputArgsHelper.validateArgAccess(parsedArgs, SECRET, true);
    final String kString = InputArgsHelper.validateArgAccess(parsedArgs, K, true);
    final String nString = InputArgsHelper.validateArgAccess(parsedArgs, N, false);
    final String dirString = InputArgsHelper.validateArgAccess(parsedArgs, IMAGES_DIR, false);
    final BMPIOService bmpIOService = new BMPIOService();
    final Path pathToInput = bmpIOService.openBmpFile(secret, INPUT);
    final int k = IOService.parseAsInt(kString, K.getDescription());
    final Optional<Integer> n;
    if (nString != null) {
      n = Optional.of(IOService.parseAsInt(nString, N.getDescription()));
    } else {
      n = Optional.empty();
    }
    final Optional<String> dir = Optional.ofNullable(dirString);
    final List<Path> pathsToShadows = bmpIOService.openBmpFilesFrom(dir, n, OUTPUT);
    if (n.isPresent() && n.get() != pathsToShadows.size()) {
      IOService.exit(VALIDATION_FAILED, "n != pathsToShadows.size()");
      throw new IllegalStateException(); // Should never return from the above method
    }
    return new DistributeProgram(pathToInput, k, pathsToShadows, bmpIOService);
  }

  @Override
  public void run() {
    // Choose the distribute algorithm based on the k number
    final DistributeAlgorithm algorithm = chooseDistributeAlgorithm(this.k);
    // Distribute the secret among all the shadows using the correct algorithm
    algorithm.run(this.bmpIOService, this.pathToSecret, this.pathsToShadows, this.k);
    // Close the secret file path
    bmpIOService.closeBmpFile(this.pathToSecret, INPUT);
    // Close all the shadows files paths
    bmpIOService.closeBmpFiles(this.pathsToShadows, OUTPUT);
  }

  private DistributeAlgorithm chooseDistributeAlgorithm(final int k) {
    if (k == STANDARD_K_VALUE) {
      return new DistributeK8Algorithm();
    }
    return new DistributeCustomAlgorithm();
  }
}
