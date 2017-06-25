package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;

import ar.edu.itba.cryptography.helpers.ByteHelper;
import ar.edu.itba.cryptography.helpers.ObfuscatorHelper;
import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.BMPService;
import java.nio.file.Path;
import java.util.List;

public class RetrieveCustomAlgorithm extends RetrieveBaseAlgorithm {
  private static final int MIN_HEADER_SIZE = 54;

  @Override
  public String run(final BMPIOService bmpIOService, final List<Path> shadowsPaths) { // TODO
    // Note: 'obf' stands for 'obfuscated'
    final int k = shadowsPaths.size();
    // Get the min necessary header information respecting the k size of each chunk
    final int minHeaderChunkSize = calculateMinHeaderChunkSize(k); // TODO
    final byte[] obfHeaderChunk = retrieveData(bmpIOService, shadowsPaths, minHeaderChunkSize); // TODO: check if cycle is correctly started
    final char seed = BMPIOService.getSeedFromSample(shadowsPaths, INPUT); // TODO
    final byte[] nonObfHeaderChunk = ObfuscatorHelper.toggleObfuscation(obfHeaderChunk, seed);
    final int totalSize = BMPService.getBitmapSize(nonObfHeaderChunk);
    // Only remains to read what it was not already read
    final int remainingDataSize = totalSize - minHeaderChunkSize;
    final byte[] obfRemainingData = retrieveData(bmpIOService, shadowsPaths, remainingDataSize); // TODO: check if cycle is correctly continued
    final byte[] obfFullData = ByteHelper.merge(obfHeaderChunk, obfRemainingData); // TODO
    final byte[] nonObfFullData = ObfuscatorHelper.toggleObfuscation(obfFullData, seed);
    return ByteHelper.hexadecimalBytesToString(nonObfFullData).toString();
  }

  private int calculateMinHeaderChunkSize(final int k) {
    return 0; // TODO: return x = 54 + j  such as x == 0 mod(k), j in [0, k-1]
  }
}
