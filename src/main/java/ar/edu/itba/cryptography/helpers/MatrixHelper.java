package ar.edu.itba.cryptography.helpers;

import java.util.Arrays;

public abstract class MatrixHelper {
  private static final int MAX_BYTE = 255;
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
