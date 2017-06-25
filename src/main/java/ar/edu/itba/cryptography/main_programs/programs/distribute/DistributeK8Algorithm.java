package ar.edu.itba.cryptography.main_programs.programs.distribute;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;
import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.OUTPUT;
import static ar.edu.itba.cryptography.services.IOService.ExitStatus.K8_MISMATCHING_SIZE;

import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.BMPService;
import ar.edu.itba.cryptography.services.IOService;
import java.nio.file.Path;
import java.util.List;

public class DistributeK8Algorithm extends DistributeBaseAlgorithm {

  @Override
  public byte[] getSecretBytes(final BMPIOService bmpIOService, final Path pathToSecret) {
    // Extract the data bytes only
    return bmpIOService.getDataBytes(pathToSecret, INPUT);
  }

  @Override
  void validateParameters(final BMPIOService bmpIOService, final List<Path> pathsToShadows,
      final int k, final byte[] data, final Path pathToSecret) {
    final byte[] secretHeaderBytes = bmpIOService.getHeaderBytesOf(pathToSecret, INPUT);
    final int width = BMPService.getHorizontalWidthInPixels(secretHeaderBytes);
    final int height = BMPService.getVerticalWidthInPixels(secretHeaderBytes);
    for (final Path path : pathsToShadows) {
      final byte[] shadowHeaderBytes = bmpIOService.getHeaderBytesOf(path, OUTPUT);
      final int shadowWidth = BMPService.getHorizontalWidthInPixels(shadowHeaderBytes);
      final int shadowHeight = BMPService.getVerticalWidthInPixels(shadowHeaderBytes);
      if (shadowWidth != width || shadowHeight != height) {
        IOService.exit(K8_MISMATCHING_SIZE, path);
      }
    }
    super.validateParameters(bmpIOService, pathsToShadows, k, data, pathToSecret);
  }
}
