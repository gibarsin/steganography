package ar.edu.itba.cryptography.main_programs.programs.distribute;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;
import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.OUTPUT;
import static ar.edu.itba.cryptography.services.IOService.ExitStatus.VALIDATION_FAILED;

import ar.edu.itba.cryptography.helpers.ByteHelper;
import ar.edu.itba.cryptography.helpers.MatrixHelper;
import ar.edu.itba.cryptography.helpers.ObfuscatorHelper;
import ar.edu.itba.cryptography.interfaces.DistributeAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import ar.edu.itba.cryptography.services.IOService;
import java.nio.file.Path;
import java.util.List;

public abstract class DistributeBaseAlgorithm implements DistributeAlgorithm {
  private static final int SHADOW_BYTES_PER_SECRET_BYTE = 8;
  private static final int FIRST_ELEM_INDEX = 0;
  private static final int MODULUS = 257;

  /**
   *
   * @param bmpIOService service manager for the secret path
   * @param pathToSecret the path to the secret image. Use the given
   *                     bmpIOService instance to access image resources (identified by its path)
   * @return the secret bytes to be distributed into the shadow images
   */
  public abstract byte[] getSecretBytes(BMPIOService bmpIOService, Path pathToSecret);

  @Override
  public void run(final BMPIOService bmpIOService, final Path pathToSecret,
      final List<Path> pathsToShadows, final int k) {
    // Note: 'obf' stands for 'obfuscated'
    // Get the secret bytes
    final byte[] data = getSecretBytes(bmpIOService, pathToSecret);
    // Validate all parameter (with exit code error, if any) according to the given secret data
    validateParameters(bmpIOService, pathsToShadows, k, data);
    // Generate a seed for the obfuscation
    final char seed = ObfuscatorHelper.generateSeed();
    // Obfuscate the data bytes using the generated seed
    final byte[] obfData = ObfuscatorHelper.toggleObfuscation(data, seed);
    // Generate the matrix A containing all the exponential evaluations of each shadow number
    // Also, assign each shadow a shadow number according to its position in the constructed matrix
    final int[][] matrixA = initializeMatrix(bmpIOService, pathsToShadows, k, MODULUS);
    // Distribute the obfuscated data into the shadows in chunks of k bytes using the built matrix
    distributeData(bmpIOService, obfData, pathsToShadows, matrixA, k, MODULUS);
    // Save the seed and persist the updated data (seed + shadowNumber + secretBytes) in all shadows
    saveSeedAndOverwriteShadows(bmpIOService, pathsToShadows, seed);
  }

  private void validateParameters(final BMPIOService bmpIOService,
      final List<Path> pathsToShadows, final int k, final byte[] data) {
    final int length = data.length;
    // Validate that the secret data length can be divided into chunks of size k
    if (length < k ||  length % k != 0) {
      IOService.exit(VALIDATION_FAILED, "It should happen that secret.length >= k "
          + "&& secret.length % k == 0. Current values: secret.length = " + length + "; k = " + k);
      throw new IllegalStateException(); // Should never reach here
    }
    // Validate that the secret data fits in each of the given shadows
    for (final Path path : pathsToShadows) {
      final int shadowDataSize = bmpIOService.getDataSize(path, OUTPUT);
      if (!secretFitsInShadow(length, shadowDataSize, k)) {
        IOService.exit(VALIDATION_FAILED, "It should happen that 'shadowDataSize >= secretSize * "
            + SHADOW_BYTES_PER_SECRET_BYTE + " / k'. Current values: secretSize = " + length
            + "; shadowDataSize = " + shadowDataSize + "; k = " + k);
      }
    }
  }

  private boolean secretFitsInShadow(final int secretSize, final int shadowDataSize,
      final int k) {
    return shadowDataSize >= secretSize * SHADOW_BYTES_PER_SECRET_BYTE / k;
  }

  private void saveSeedAndOverwriteShadows(final BMPIOService bmpIOService,
      final List<Path> pathsToShadows, final char seed) {
    for (final Path path : pathsToShadows) {
      bmpIOService.setSeed(path, OUTPUT, seed);
      bmpIOService.writeDataToDisk(path, OUTPUT);
    }
  }

  private void distributeData(final BMPIOService bmpIOService, final byte[] obfData,
      final List<Path> pathsToShadows, final int[][] matrixA, final int k, final int modulus) {
    // If we are here, we know that obfData.length % k == 0
    // Take chunks of k bytes from obfData to build and solve each polynomial, until all
    // obfData bytes have been distributed
    for (int distributedBytes = 0 ; distributedBytes < obfData.length ; distributedBytes += k) {
      // Get the next k bytes in the order a0, a1, ..., ak-1
      final byte[] arrayX = getNextKBytes(obfData, distributedBytes, k);
      // Resolve the polynomial for all shadow numbers, i.e., perform Ax = b = P([1,n]), with
      // n the max shadow number, taking int account the modulus arithmetic
      final byte[] arrayB = resolvePolynomialForAllShadowNumbers(matrixA, arrayX, modulus);
      // Distribute each polynomial evaluation to its corresponding shadow
      distributePolynomialEvaluations(arrayB, bmpIOService, pathsToShadows);
    }
  }

  private void distributePolynomialEvaluations(final byte[] arrayB, final BMPIOService bmpIOService,
      final List<Path> pathsToShadows) {
    // Save each polynomial evaluation into it's corresponding shadow in a properly manner
    // (i.e.: as specified by the paper)
    for (final Path path : pathsToShadows) {
      final int row = bmpIOService.getPathMatrixRow(path, OUTPUT);
      bmpIOService.hideByte(path, OUTPUT, arrayB[row]);
    }
  }

  /**
   * Solves matrixA x arrayX (mod `mod`) without byte overflow.<p>
   * If overflow is detected during the operation,
   * the first non-zero element (from a0, to ak-1) in arrayX is decremented by one and calculation
   * is taken over again, until the calculation does not produce overflow. <p>
   * IMPORTANT: this method directly modifies the arrayX
   * @param matrixA the n x k matrix
   * @param arrayX the k x 1 array, with constants in the order [a0, ..., ak-1].
   *               Recall that it may be modified.
   * @param mod the modulus to be used during calculations
   * @return matrixA x arrayX' (mod n) with arrayX' being the original
   *         arrayX or a modification of it such that the specified multiplication
   *         does not produce byte overflow while it's being calculated
   */
  private byte[] resolvePolynomialForAllShadowNumbers(final int[][] matrixA, final byte[] arrayX,
      final int mod) {
    byte[] arrayB;
    while (true) {
      arrayB = MatrixHelper.byteNoOverflowMultiply(matrixA, arrayX, mod);
      if (arrayB != null) {
        return arrayB;
      }
      decrementFirstNonZeroElement(arrayX);
    }
  }

  private void decrementFirstNonZeroElement(final byte[] arrayX) {
    // As arrayX is [a0, ..., ak-1]
    for (int i = 0; i < arrayX.length ; i++) {
      if (ByteHelper.byteToUnsignedInt(arrayX[i]) > 0) {
        arrayX[i] -= ((byte) 1);
        return;
      }
    }
    // If this method was called, any byte in the array should have been greater than 0 (as
    // demonstrated in the paper)
    throw new IllegalStateException("decrementFirstNonZeroElement hasn't found a non-zero elem");
  }

  private byte[] getNextKBytes(final byte[] obfData, final int distributedBytes, final int k) {
    final byte[] arrayX = new byte[k];
    System.arraycopy(obfData, distributedBytes, arrayX, FIRST_ELEM_INDEX, k);
    return arrayX;
  }

  /**
   * Initialize the A matrix that will be used to solve the operation Ax = b<p>
   * Sizes: { A: nxk ; x: kx1 ; b: nx1 }<p>
   * A is representing the x factors of the polynomial.<p>
   * x is representing the k bytes to be distributed into the shadows<p>
   * Ax = b is the polynomial evaluation for all the n shadows for the current k bytes of the
   * secret to be distributed. b[0] = P(1), b[1] = P(2), ..., b[n-1] = P(n). <p>
   * Values of A are constant (i.e., each one representing the pow elevation of each term, with the
   * x value being elevated represented by the index of the shadows being used), so they are
   * initialized once and the only values that change each iteration are the once corresponding to
   * x and b, i.e., the ones that correspond to each k bytes of the secret to be distributed
   *
   * @param bmpIOService the service manager of each path (used to link a path with a matrix index)
   * @param shadowsPaths the shadows paths. Each one is set a shadow number for retrieving purposes
   * @param k the columns of the A matrix. Note that the number of rows of A equals
   *          {@code shadowsPaths.size()}, i.e., {@code n = shadowsPaths.size()}
   * @param modulus the modulus operation to be used during the equation solving problem
   * @return the constructed A matrix
   */
  private int[][] initializeMatrix(final BMPIOService bmpIOService, final List<Path> shadowsPaths,
      final int k, final int modulus) {
    final int n = shadowsPaths.size(); // assumed that n <= char MAX_VALUE
    final int[][] matrix = new int[n][k];
    for (char row = 0 ; row < n ; row ++) {
      final Path path = shadowsPaths.get(row);
      final char x = (char) (row + 1);
      bmpIOService.setShadowNumber(path, OUTPUT, x); // set for retrieving purposes only
      bmpIOService.setPathMatrixRow(path, OUTPUT, row); // set for distribution purposes
      for (int col = 0 ; col < k ; col ++) {
        matrix[row][col] = MatrixHelper.getCoefficient(x, col, modulus);
      }
    }
    return matrix;
  }
}
