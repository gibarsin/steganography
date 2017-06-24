package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import static ar.edu.itba.cryptography.services.BMPIOService.OpenMode.INPUT;

import ar.edu.itba.cryptography.helpers.BitHelper;
import ar.edu.itba.cryptography.interfaces.RetrieveAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public class RetrieveK8Algorithm implements RetrieveAlgorithm {
  private static final int FIRST_ELEM_INDEX = 0;

  @Override
  public byte[] retrieveHeader(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths) {
    // Get the header of any image: it will be used as the header of the retrieved message
    return bmpIOService.getHeaderBytesOf(shadowsPaths.get(FIRST_ELEM_INDEX), INPUT);
  }

  @Override
  public byte[] retrieveData(final BMPIOService bmpIOService,
      // TODO: we are assuming that only k shadowsPaths are given
      final List<Path> shadowsPaths, final int dataLength, final int modulus) { // TODO
    final int k = shadowsPaths.size();
    final byte[][] matrix = initializeMatrix(bmpIOService, shadowsPaths, k, modulus);
    final byte[] data = new byte[dataLength];
    // for each group of k bytes to retrieve
    for (int i = 0 ; i < dataLength ; i += k) {
      //  for each shadow file (shadow index = j, with 1 <= j <= n)
      for (final Path shadowPath : shadowsPaths) {
        // get byte = p(i) joining, from the first to the last byte of the range, the last bit of each byte in the range

      }
      // solve the equation system using the Gauss method => this is the byte of the current iteration
    }
    return data;
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
  private byte[][] initializeMatrix(final BMPIOService bmpIOService, final List<Path> shadowsPaths,
      final int k, final int modulus) {
    final byte[][] matrix = new byte[k][k+1];
    for (int row = 0 ; row < k ; row ++) {
      final Path path = shadowsPaths.get(row);
      bmpIOService.setPathMatrixRow(path, INPUT, row);
      final int x = bmpIOService.getShadowNumber(path, INPUT);
      for (int col = 0 ; col < k ; col ++) {
        matrix[row][col] = calculateMatrixXTerm(x, k, col, modulus);
      }
    }
    return matrix;
  }

  /**
   * <p>
   *   Calculates the constant value of the matrix based on the given parameters. For a better
   *   understanding, an example is left below.
   * </p>
   * <pre>
   *   matrix has the below template:
   *      a00   a01   a02   ... a0k-1   | a0k
   *      a10   a11   a12   ... a1k-1   | a1k
   *      a20   a21   a22   ... a2k-1   | a2k
   *      .     .     .         .       | .
   *      .     .     .         .       | .
   *      .     .     .         .       | .
   *      ak-10 ak-11 ak-12 ... ak-1k-1 | ak-1k
   *                                        |-----> this is the `b` column
   *
   *   Each row of the above matrix corresponds to the polynomial
   *      P(x) = x0 * x^k-1 + x1 * x^k-2 + ... + xk-2 * x^1 + xk-1 * x^0, where
   *        x^k-i = a_{row, k - 1 - col} for col in [0, k-1] and i in [1, k]
   *        and
   *        P(x) = a_{row, col}, for col = k
   *
   *   Note that for a given row,
   *    col = 0 => exp = k-1
   *    col = 1 => exp = k-2
   *    col = 2 => exp = k-3
   *    .
   *    .
   *    .
   *    col = k-1 => exp = 0 (with k-1 the last column of matrix A),
   *
   *   getting the general formula
   *      exp = k-1-col for col in [0, k-1]
   *   as declared above
   *
   *   So, this method calculates the corresponding ith term of the polynomial, without the xi
   *   constant term (it will be obtained later with the Gauss method
   *   when solving the equation system), based on the current col position and the given k,
   *   and applying the corresponding modulus arithmetic.
   * </pre>
   * @param x the shadow x value used to evaluate the polynomial
   * @param k the total amount of shadows (or, equivalently, the A matrix amount of columns)
   * @param col the current column for which the x exponential value will be calculated
   * @param n the modulus used for the modulus arithmetic
   * @return x^{k-1-col} mod (n) as explained above
   */
  private byte calculateMatrixXTerm(final int x, final int k, final int col, final int n) {
    int xTerm = x;
    for (int exp = k - 1 - col ; exp > 0 ; exp--) {
      xTerm *= xTerm;
      xTerm %= n;
    }
    return BitHelper.intToByte(xTerm);
  }
}
