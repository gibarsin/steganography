package ar.edu.itba.cryptography.helpers;

public abstract class MatrixHelper {
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
    int xTerm = x;
    for (int exp = k - 1 - col ; exp > 0 ; exp--) {
      xTerm *= xTerm;
      xTerm %= n;
    }
    return ByteHelper.intToByte(xTerm);
  }
}
