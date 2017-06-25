package ar.edu.itba.cryptography.main_programs.programs.distribute;

import ar.edu.itba.cryptography.interfaces.DistributeAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public class DistributeCustomAlgorithm implements DistributeAlgorithm {
  @Override
  public void run(final BMPIOService bmpIOService, final Path pathToSecret,
      final List<Path> pathsToShadows, final int k) {
    // TODO
  }
}
