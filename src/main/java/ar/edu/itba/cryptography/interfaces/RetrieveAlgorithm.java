package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public interface RetrieveAlgorithm {
  /**
   * Runs the retrieve algorithm using the given resources
   * @param bmpIOService service managing all access & information of the specified shadows paths
   * @param shadowsPaths the shadows that will be used to retrieve the secret file.
   *                     There should be k different shadows paths
   * @return the secret, non-obfuscated bmp file data as string
   */
  byte[] run(BMPIOService bmpIOService, List<Path> shadowsPaths);
}
