package ar.edu.itba.cryptography.helpers;

// Code taken & adapted from:
// https://martin-thoma.com/solving-linear-equations-with-gaussian-elimination
public abstract class GaussSolverHelper {

  // TODO: check: ¿should be as int or as byte? (decide to be int and then transform it to byte)
  // https://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back

  /**
   * Solves the equation system represented by the specified matrix and returns the solution
   * array. Note that all operations will be calculated using the specified modulus
   *
   * @param matrix The matrix representing the system equation to be solved (it will be transformed)
   * @param modulus The modulus to be applied to each calculus
   * @return The system equation array solution, if any, with values in a valid modulus range
   * @implNote
   * Ax = b =>
   *   matrix = A | b (size k x k+1)
   *   x = value to be returned (size k)
   *
   * The idea is as follows:
   *
   * Step 1: row = 0:
   *        col
   *        |
   * row -> a00   a01   a02   ... a0k-1   | a0k
   *        a10   a11   a12   ... a1k-1   | a1k
   *        a20   a21   a22   ... a2k-1   | a2k
   *        .     .     .         .       | .
   *        .     .     .         .       | .
   *        .     .     .         .       | .
   *        ak-10 ak-11 ak-12 ... ak-1k-1 | ak-1k
   *
   * Step 2: row = 1:
   *              col
   *              |
   *        a00   a'01   a'02   ... a'0k-1   | a'0k
   * row -> 0     a'11   a'12   ... a'1k-1   | a'1k
   *        0     a'21   a'22   ... a'2k-1   | a'2k
   *        .     .      .          .        | .
   *        .     .      .          .        | .
   *        .     .      .          .        | .
   *        0     a'k-11 a'k-12 ... a'k-1k-1 | a'k-1k
   *
   * Step 3: row = 2:
   *                      col
   *                      |
   *        a00   a''01   a''02   ... a''0k-1   | a''0k
   *        0     a''11   a''12   ... a''1k-1   | a''1k
   * row -> 0     0       a''22   ... a''2k-1   | a''2k
   *        .     .       .           .         | .
   *        .     .       .           .         | .
   *        .     .       .           .         | .
   *        0     0       a''k-12 ... a''k-1k-1 | a''k-1k
   *
   * etc.
   *
   * Note that in each step we will getting to the row position the row with the max
   * value in the first non-zero column of the current row, so a swap may be performed.
   *
   * When we get the upper triangular matrix
   * (i.e.: all elements below the main diagonal are zero),
   * we solve the Ax = b system equation
   *
   */
  public static int[] solve(final int[][] matrix, final int modulus) {
    final int rows = matrix.length;
    final int cols = matrix.length + 1; // cols = rows + 1 assuming matrix is valid
    for (int diagonalIndex = 0 ; diagonalIndex < rows ; diagonalIndex ++) {
      final int maxRowIndex = getRowWithMaxColumnValueIndex(matrix, diagonalIndex, rows);
      swapCurrentRowWithMaxRow(matrix, diagonalIndex, cols, maxRowIndex);
      suppressFirstNonZeroColumnBelowCurrentRow(matrix, diagonalIndex, rows, cols, modulus);
    }
    return solveUpperTriangleMatrix(matrix, modulus);
  }

  private static int getRowWithMaxColumnValueIndex(final int[][] matrix, final int diagonalIndex,
      final int rows) {
    int maxRowIndex = diagonalIndex;
    int maxValue = matrix[maxRowIndex][maxRowIndex];
    for (int row = diagonalIndex + 1 ; row < rows ; row ++) {
      final int cValue = matrix[row][diagonalIndex];
      if (cValue > maxValue) {
        maxRowIndex = row;
        maxValue = cValue;
      }
    }
    return maxRowIndex;
  }

  private static void swapCurrentRowWithMaxRow(final int[][] matrix, final int diagonalIndex,
      final int cols, final int maxRowIndex) { // diagonalIndex == currentRow
    if (maxRowIndex == diagonalIndex) {
      return; // max row already is current row => there's no need to swap
    }
    for (int col = diagonalIndex ; col < cols ; col ++) {
      final int aux = matrix[diagonalIndex][col];
      matrix[diagonalIndex][col] = matrix[maxRowIndex][col];
      matrix[maxRowIndex][col] = aux;
    }
  }

  private static void suppressFirstNonZeroColumnBelowCurrentRow(final int[][] matrix,
      final int diagonalIndex, final int rows, final int cols, final int modulus) {
    if (matrix[diagonalIndex][diagonalIndex] == 0) {
      // the greatest value for the current column among all rows is 0
      // => there's nothing to suppress as all column elements are 0
      // (as we are using modulus operations) => skip method call
      return;
    }

    for (int row = diagonalIndex + 1 ; row < rows ; row ++) {
      if (matrix[row][diagonalIndex] == 0) {
        // current value for main column of the current row is already 0 => skip
        continue;
      }

      // If we reached here, we are sure both values for lcm calculation are non-zero integers
      //   because of the previous checks
      final int mcm = lcmOf(matrix[diagonalIndex][diagonalIndex], matrix[row][diagonalIndex]);
      // We know that multiply will be exact as the mcm is multiple of the referenced matrix value
      final int multiplier = - mcm / matrix[row][diagonalIndex];
      // This is the value on the column being made 0
      matrix[row][diagonalIndex] = 0;
      // We are not multiplying the reference row (i.e.: the one referenced by diagonalIndex)
      //   so as it can be used in the consecutive row indexed iteration calls
      for (int col = diagonalIndex + 1 ; col < cols ; col ++) {
        matrix[row][col] += (multiplier * matrix[diagonalIndex][col]);
        matrix[row][col] %= modulus;
      }
    }
  }

  private static int[] solveUpperTriangleMatrix(final int[][] matrix, final int modulus) {
    // Solve equation Ax=b for an upper triangular matrix A
//    var x= new Array(n);
//    for (var i=n-1; i>-1; i--) {
//      x[i] = A[i][n]/A[i][i];
//      for (var k=i-1; k>-1; k--) {
//        A[k][n] -= A[k][i] * x[i];
//      }
//    }
//    return x;
//  }
    return new int[0]; // TODO
  }

  /**
   *
   * @param x non-zero integer
   * @param y non-zero integer
   * @return the least common multiple between x and y
   */
  private static int lcmOf(final int x, final int y) {
    if (x * y == 0) { // mcm is defined only for non-null integers
      throw new IllegalArgumentException();
    }
    final int xAbs =  Math.abs(x);
    final int yAbs = Math.abs(y);
    final int a = Math.max(xAbs, yAbs) == xAbs ? xAbs : yAbs;
    final int b = a == xAbs ? yAbs : xAbs;
    return  a * b / gcmOf(a, b);
  }

  /**
   *
   * @param a non-zero integer such as |a| >= |b|
   * @param b integer such as |a| >= |b|
   * @return the least common divisor between a and b
   */
  private static int gcmOf(/* non-final */ int a, /* non-final */ int b) { // Euclid's Algorithm
    a = Math.abs(a);
    b = Math.abs(b);
    int aux;
    while (b > 0) {
      aux = b;
      b = a % b;
      a = aux;
    }
    return a;
  }
}
