package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;

import ar.edu.itba.cryptography.helpers.ByteHelper;
import ar.edu.itba.cryptography.helpers.GaussSolverHelper;
import ar.edu.itba.cryptography.helpers.MatrixHelper;
import ar.edu.itba.cryptography.interfaces.RetrieveAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

/*
 * IMPORTANT:
 *   Care must be taken between conversions of int and byte types.
 *   Use ByteHelper.byteToUnsignedInt when needed
 */
/* package-private */ abstract class RetrieveBaseAlgorithm implements RetrieveAlgorithm {
  private static final int MODULUS = 257;

  /* package-private */ static final int FIRST_ELEM_INDEX = 0;

  /**
   * Retrieves dataLength bytes from the shadowPaths shadows (managed through the bmpIOService).
   * <p>
   * Note that the bmpIOService handles each shadow's pointers to the next bytes to be
   * read for each path, and that this action is permanent, i.e., future reads from the same
   * shadow paths (that weren't closed by the BMPIOService) will keep reading from the
   * last read offset.
   * </p>
   * @param bmpIOService shadowsPath manager
   * @param shadowsPaths path to each of the shadow files that should be already opened in
   *                     INPUT mode using the current instance of the bmpIOService.
   *                     There should be k shadows paths
   * @param dataLength   the number of secret bytes to be retrieved
   * @return an array with the obfuscated bytes of the secret image
   *         being retrieved from the given shadows
   */
  /* package-private */ byte[] retrieveData(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths, final int dataLength) {
    final int k = shadowsPaths.size();
    final int[][] matrix = initializeMatrix(bmpIOService, shadowsPaths, k, MODULUS);
    final byte[] data = new byte[dataLength];
    // For each group of k bytes to retrieve
    for (int i = 0 ; i < dataLength ; i += k) {
      // For each shadow file (shadow number = j, with 1 <= j <= n)
      for (final Path shadowPath : shadowsPaths) {
        final int shadowNumber = bmpIOService.getPathMatrixRow(shadowPath, INPUT);
        // Get byte = p(shadowNumber) (recall that this byte is hidden among several shadow's bytes)
        final byte b = bmpIOService.getNextSecretByte(shadowPath, INPUT);
        matrix[shadowNumber][k] = ByteHelper.byteToUnsignedInt(b);
      }
      // Solve the equation system to get the k chunk bytes of current iteration
      final byte[] kDataByteChunk = solveEquationSystem(matrix, MODULUS);
      if (kDataByteChunk.length != k) throw new IllegalStateException("kDataByteChunk.length != k");
      // Copy the k bytes to the data array
      System.arraycopy(kDataByteChunk, FIRST_ELEM_INDEX, data, i, k);
    }
    return data;
  }

  /**
   * Solve the equation system represented by {@code matrix} using the Gauss method in arithmetic
   * operations in modulus {@code n}.
   * @param matrix the matrix representing the equation system A | b
   * @param n the modulus to be used for arithmetic operations
   * @return the x array solution of the equation system Ax = b
   */
  private byte[] solveEquationSystem(final int[][] matrix, final int n) {
    final int[] x = GaussSolverHelper.solve(matrix, n);
    final byte[] xAsBytes = new byte[x.length];
    for (int i = 0 ; i < x.length ; i++) {
      xAsBytes[i] = ByteHelper.intToByte(x[i]);
    }
    return xAsBytes;
  }

  /**
   * Initialize the matrix that will be used to solve the equation system with the Gauss method.<p>
   * System equation is Ax = b.<p>
   * matrix = A | b
   * Values of A are constant (i.e., each one representing the pow elevation of each term, with the
   * x value being elevated represented by the index of the shadows being used), so they are
   * initialized once and the only values that change each iteration are the once corresponding to
   * b, i.e., the ones at the last column of the matrix {@code matrix} for each row
   *
   * @param bmpIOService the service manager of each path (used to link a path with a matrix index)
   * @param shadowsPaths the shadows paths to get each of the x values and link them with a matrix
   *                     index. This is done so to avoid reconstructing the matrix
   *                     each new iteration
   * @param k the size of the square matrix A
   * @param modulus the modulus operation to be used during the equation solving problem
   * @return the constructed matrix = A | b
   */
  private int[][] initializeMatrix(final BMPIOService bmpIOService, final List<Path> shadowsPaths,
      final int k, final int modulus) {
    final int[][] matrix = new int[k][k+1];
    for (int row = 0 ; row < k ; row ++) {
      final Path path = shadowsPaths.get(row);
      bmpIOService.setPathMatrixRow(path, INPUT, row);
      final int x = bmpIOService.getShadowNumber(path, INPUT);
      for (int col = 0 ; col < k ; col ++) {
        matrix[row][col] =
            ByteHelper.byteToUnsignedInt(MatrixHelper.calculateMatrixXTerm(x, k, col, modulus));
      }
    }
    return matrix;
  }
}
