package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public interface DistributeAlgorithm {
  /**
   * Runs the distribute algorithm using the given resources
   * @param bmpIOService service managing all access & information of the specified shadows paths &
   *                     the given secret path
   * @param pathToSecret path to the secret file (should have been opened using the bmpIOService in
   *                     INPUT mode)
   * @param pathsToShadows the shadows that will be used to retrieve the secret file
   *                       (should have been opened using the bmpIOService in OUTPUT mode).
   *                       There should be n different shadows paths.
   * @param k the amount of required shadows to retrieve the secret after distribution
   */
  void run(BMPIOService bmpIOService, Path pathToSecret, List<Path> pathsToShadows, int k);
}
