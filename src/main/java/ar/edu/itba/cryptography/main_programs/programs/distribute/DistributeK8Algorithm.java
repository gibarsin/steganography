package ar.edu.itba.cryptography.main_programs.programs.distribute;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;
import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.OUTPUT;

import ar.edu.itba.cryptography.helpers.ObfuscatorHelper;
import ar.edu.itba.cryptography.interfaces.DistributeAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public class DistributeK8Algorithm implements DistributeAlgorithm {
  private static final int MODULUS = 257;

  @Override
  public void run(final BMPIOService bmpIOService, final Path pathToSecret,
      final List<Path> pathsToShadows, final int k) {
    // Note: 'obf' stands for 'obfuscated'
    // Extract the data bytes only
    final byte[] data = bmpIOService.getDataBytes(pathToSecret, INPUT);
    // Generate a seed for the obfuscation
    final char seed = ObfuscatorHelper.generateSeed();
    // Obfuscate the data bytes using the generated seed
    final byte[] obfData = ObfuscatorHelper.toggleObfuscation(data, seed);
    // Save the seed in all shadow paths, associating its shadow number too
    bmpIOService.storeSecretDataInto(pathsToShadows, OUTPUT, seed);
    // Generate the matrix A containing all the exponential evaluations of each shadow number
    final int[][] matrixA = initializeMatrix(pathsToShadows, MODULUS); // TODO
    // Distribute the obfuscated data into the shadows in chunks of k bytes using the built matrix
    distributeData(bmpIOService, obfData, pathsToShadows, k, MODULUS); // TODO
  }
}
