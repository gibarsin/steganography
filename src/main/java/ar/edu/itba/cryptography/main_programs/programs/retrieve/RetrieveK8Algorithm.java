package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;

import ar.edu.itba.cryptography.helpers.ByteHelper;
import ar.edu.itba.cryptography.helpers.ObfuscatorHelper;
import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.BMPService;
import java.nio.file.Path;
import java.util.List;

public class RetrieveK8Algorithm extends RetrieveBaseAlgorithm {
  @Override
  public byte[] run(final BMPIOService bmpIOService, final List<Path> shadowsPaths) {
    // Retrieve the secret image header
    final byte[] header = retrieveHeader(bmpIOService, shadowsPaths);
    // Get the total data bytes to be retrieved (size - offset)
    final int size = BMPService.getBitmapSize(header);
    final int offset = BMPService.getBitmapOffset(header);
    final int dataBytes = size - offset;
    // Retrieve the obfuscated secret image data
    final byte[] obfuscatedData = retrieveData(bmpIOService, shadowsPaths, dataBytes);
    // Remove obfuscation
    final int seed = BMPService.recoverSeed(header);
    final byte[] originalData = ObfuscatorHelper.toggleObfuscation(obfuscatedData, seed);
    // Return the retrieved secret (header + data)
    return ByteHelper.merge(header, originalData);
  }

  private byte[] retrieveHeader(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths) {
    // Get the header of any image: it will be used as the header of the retrieved message
    return bmpIOService.getHeaderBytesOf(shadowsPaths.get(FIRST_ELEM_INDEX), INPUT);
  }
}
