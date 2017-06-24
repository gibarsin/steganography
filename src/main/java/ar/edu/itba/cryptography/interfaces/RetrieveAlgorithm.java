package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public interface RetrieveAlgorithm {
  // TODO: document
  String run(BMPIOService bmpIOService, List<Path> shadowsPaths, int modulus);
}
