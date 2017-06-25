package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public interface DistributeAlgorithm {
  // TODO: document
  void run(BMPIOService bmpIOService, Path pathToSecret, List<Path> pathsToShadows, int k);
}
