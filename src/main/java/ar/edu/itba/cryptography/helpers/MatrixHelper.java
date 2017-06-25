package ar.edu.itba.cryptography.helpers;

import java.util.Arrays;

public abstract class MatrixHelper {
  private static final int MAX_BYTE = 255;
  /**
   * <p>
   *   Calculates the x term value of the matrix based on the given parameters. For a better
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
  public static byte calculateMatrixXTerm(final int x, final int k, final int col, final int n) {
    int xTerm = 1;
    for (int exp = k - 1 - col ; exp > 0 ; exp--) {
      xTerm *= x;
      xTerm %= n;
    }
    return ByteHelper.intToByte(xTerm);
  }

  public static int getCoefficient(final int x, final int coefficientPos, final int modulus) {
    int coefficient = 1;

    for (int i = coefficientPos; i > 0; i--) {
      coefficient *= x;
      coefficient %= modulus;
    }

    return coefficient;
  }

  /**
   * Solves matrixA x arrayX (mod `mod`) without byte overflow.<p>
   * If byte overflow is detected after applying the mod n in any operation, null is returned.
   * @param matrixA the n x k matrix
   * @param arrayX the k x 1 array
   * @param mod the modulus to be used during calculations
   * @return matrixA x arrayX (mod n) if no byte overflow is detected; null otherwise
   * @implNote To check byte overflow, all operations are first carried out with
   *           integer type variables and then safely casted to byte
   *           (i.e.: checking there will no be byte overflow if the cast is performed)
   */
  public static byte[] byteNoOverflowMultiply(final int[][] matrixA, final byte[] arrayX,
      final int mod) {
    final int rows = matrixA.length;
    final int cols = arrayX.length;
    final byte[] arrayB = new byte[rows];
    for (int i = 0 ; i < rows ; i++) {
      int sum = 0;
      for (int j = 0 ; j < cols ; j++) {
        // it is assumed that cols(matrixA) = cols
        sum += (matrixA[i][j] * ByteHelper.byteToUnsignedInt(arrayX[j]));
        sum %= mod; // perform mod operation on each step
      }
      if (sum > MAX_BYTE) { // byte overflow detected
        return null;
      }
      arrayB[i] = (byte) sum; // safe cast (with no overflow)
    }
    return arrayB;
  }

  public static int[][] copyOf(final int[][] matrix) {
    final int rows = matrix.length;
    final int[][] copy = new int[rows][];
    for (int row = 0 ; row < rows ; row ++) {
      copy[row] = Arrays.copyOf(matrix[row], matrix[row].length);
    }
    return copy;
  }
}
