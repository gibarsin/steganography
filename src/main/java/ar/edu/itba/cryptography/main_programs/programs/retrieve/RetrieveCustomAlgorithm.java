package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;
import static ar.edu.itba.cryptography.services.BMPService.MIN_HEADER_SIZE;

import ar.edu.itba.cryptography.helpers.ByteHelper;
import ar.edu.itba.cryptography.helpers.ObfuscatorHelper;
import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.BMPService;
import java.nio.file.Path;
import java.util.List;

public class RetrieveCustomAlgorithm extends RetrieveBaseAlgorithm {
  @Override
  public String run(final BMPIOService bmpIOService, final List<Path> shadowsPaths) {
    // Note: 'obf' stands for 'obfuscated'
    final int k = shadowsPaths.size();
    // Get the min necessary header information respecting the k size of each chunk
    final int minHeaderChunkSize = calculateMinHeaderChunkSize(k);
    final byte[] obfHeaderChunk = retrieveData(bmpIOService, shadowsPaths, minHeaderChunkSize);
    // Remove obfuscation from the retrieve data
    final char seed = bmpIOService.getSeedFromSample(shadowsPaths, INPUT);
    final byte[] nonObfHeaderChunk = ObfuscatorHelper.toggleObfuscation(obfHeaderChunk, seed);
    // Retrieve the total size of the secret file
    final int totalSize = BMPService.getBitmapSize(nonObfHeaderChunk);
    // Only remains to read what it hasn't already been read.
    // Remaining size should already be divisible by k.
    // It it weren't so, this secret couldn't have been ever distributed.
    final int remainingDataSize = totalSize - minHeaderChunkSize;
    final byte[] obfRemainingData = retrieveData(bmpIOService, shadowsPaths, remainingDataSize);
    // Merge all the read bytes to remove obfuscation from the full data
    final byte[] obfFullData = ByteHelper.merge(obfHeaderChunk, obfRemainingData);
    // Remove obfuscation of the full data using the already obtained seed
    final byte[] nonObfFullData = ObfuscatorHelper.toggleObfuscation(obfFullData, seed);
    // Return the secret image bytes as a string
    return ByteHelper.hexadecimalBytesToString(nonObfFullData);
  }

  /**
   *
   * @param k integer to me used for modulus calculation
   * @return x = MIN_HEADER_SIZE + j  such as x == 0 mod(k), j in [0, k-1]
   */
  private int calculateMinHeaderChunkSize(final int k) {
    final int minHeaderSizeModK = MIN_HEADER_SIZE % k;
    if (minHeaderSizeModK == 0) return MIN_HEADER_SIZE;
    return MIN_HEADER_SIZE - (minHeaderSizeModK - k); // [OK]
  }
}
