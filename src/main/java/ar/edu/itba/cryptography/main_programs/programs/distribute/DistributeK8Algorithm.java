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
import org.apache.commons.lang3.ArrayUtils;

public class DistributeK8Algorithm implements DistributeAlgorithm {
  private static final int FIRST_ELEM_INDEX = 0;
  private static final int MODULUS = 257;

  @Override
  public void run(final BMPIOService bmpIOService, final Path pathToSecret,
      final List<Path> pathsToShadows, final int k) {
    // Note: 'obf' stands for 'obfuscated'
    // Extract the data bytes only
    final byte[] data = bmpIOService.getDataBytes(pathToSecret, INPUT);
    // Validate (with exit code error, if any) the given k according to the data size
    if (data.length < k ||  data.length % k != 0) {
      IOService.exit(VALIDATION_FAILED, "It should happen that secret.length >= k "
          + "&& secret.length % k == 0. Current values: secret.length = " + data.length + "; k = ");
      throw new IllegalStateException(); // Should never reach here
    }
    // Generate a seed for the obfuscation
    final char seed = ObfuscatorHelper.generateSeed();
    // Obfuscate the data bytes using the generated seed
    final byte[] obfData = ObfuscatorHelper.toggleObfuscation(data, seed);
    // Generate the matrix A containing all the exponential evaluations of each shadow number
    // Also, assign each shadow a shadow number according to its position in the constructed matrix
    final int[][] matrixA = initializeMatrix(bmpIOService, pathsToShadows, k, MODULUS);
    // Save the seed in all shadows and persist the new shadow number and seed information
    saveSeedAndOverwriteShadows(bmpIOService, pathsToShadows, seed);
    // Distribute the obfuscated data into the shadows in chunks of k bytes using the built matrix
    distributeData(bmpIOService, obfData, pathsToShadows, matrixA, k, MODULUS);
    // TODO: save all shadows with updated data (seed + shadowNumber + secretBytes)
  }

  private void saveSeedAndOverwriteShadows(final BMPIOService bmpIOService,
      final List<Path> pathsToShadows, final char seed) {
    for (final Path path : pathsToShadows) {
      bmpIOService.setSeed(path, OUTPUT, seed);
      bmpIOService.writeDataToDisk(path, OUTPUT);
    }
  }

  private void distributeData(final BMPIOService bmpIOService, final byte[] obfData,
      final List<Path> pathsToShadows, final int[][] matrixA, final int k, final int modulus) { // TODO
    // If we are here, we know that obfData.length % k == 0
    // Take chunks of k bytes from obfData to build and solve each polynomial, until all
    // obfData bytes have been distributed
    for (int distributedBytes = 0 ; distributedBytes < obfData.length ; distributedBytes += k) {
      // Get the next k bytes in the order ak-1, ..., a1, a0 (see method comments)
      final byte[] arrayX = getNextKBytes(obfData, distributedBytes, k);
      // Resolve the polynomial for all shadow numbers, i.e., perform Ax = b = P([1,n]), with
      // n the max shadow number, taking int account the modulus arithmetic
      final byte[] arrayB =
          resolvePolynomialForAllShadowNumbers(matrixA, arrayX, modulus);
      // Distribute each polynomial evaluation to its corresponding shadow
      distributePolynomialEvaluations(arrayB, bmpIOService, pathsToShadows); // TODO
    }
  }

  /**
   * Solves matrixA x arrayX (mod `mod`) without byte overflow.<p>
   * If overflow is detected during the operation,
   * the first non-zero element (from a0, to ak-1) in arrayX is decremented by one and calculation
   * is taken over again, until the calculation does not produce overflow. <p>
   * IMPORTANT: this method directly modifies the arrayX
   * @param matrixA the n x k matrix
   * @param arrayX the k x 1 array, with constants in the order [ak-1, ..., a0].
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
    // As arrayX is [ak-1, ..., a0], we should be finding this non-zero element
    // in the inverse order (from the end to the beginning) to strictly obey the paper algorithm
    for (int i = arrayX.length-1 ; i >= 0 ; i--) {
      if (arrayX[i] > 0) {
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
    // As the paper algorithm suggest us to use the order a0, a1, ... ak-1
    // instead of ak-1, ..., a1, a0, and we are going to calculate matrixA x arrayX, we need
    // to reverse the array order to strictly implement the paper algorithm
    ArrayUtils.reverse(arrayX);
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
      bmpIOService.setShadowNumber(path, OUTPUT, x);
      bmpIOService.setPathMatrixRow(path, OUTPUT, row);
      for (int col = 0 ; col < k ; col ++) {
        matrix[row][col] =
            ByteHelper.byteToUnsignedInt(MatrixHelper.calculateMatrixXTerm(x, k, col, modulus));
      }
    }
    return matrix;
  }
}
