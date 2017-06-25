package ar.edu.itba.cryptography.main_programs.programs.distribute;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;

public class DistributeK8Algorithm extends DistributeBaseAlgorithm {

  @Override
  public byte[] getSecretBytes(final BMPIOService bmpIOService, final Path pathToSecret) {
    // Extract the data bytes only
    return bmpIOService.getDataBytes(pathToSecret, INPUT);
  }
}
