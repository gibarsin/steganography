package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public interface RetrieveAlgorithm {
  // TODO: we are assuming that only k shadowsPaths are given
  byte[] retrieveHeader(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths);
  // TODO: we are assuming that only k shadowsPaths are given
  byte[] retrieveData(BMPIOService bmpIOService,
      List<Path> shadowsPaths, int dataLength, int modulus);
}
