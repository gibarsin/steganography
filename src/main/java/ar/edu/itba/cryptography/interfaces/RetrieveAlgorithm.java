package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public interface RetrieveAlgorithm {
  byte[] retrieveHeader(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths);
  byte[] retrieveData(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths, final int dataLength);
}
