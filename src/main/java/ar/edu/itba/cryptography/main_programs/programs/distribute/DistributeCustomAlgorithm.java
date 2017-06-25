package ar.edu.itba.cryptography.main_programs.programs.distribute;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;

public class DistributeCustomAlgorithm extends DistributeBaseAlgorithm {

  @Override
  public byte[] getSecretBytes(final BMPIOService bmpIOService, final Path pathToSecret) {
    // Return all the bmp bytes
    return bmpIOService.getBmp(pathToSecret, INPUT);
  }
}
