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
    final int rows = getMatrixRows(matrix);
    for (int diagonalIndex = 0 ; diagonalIndex < rows ; diagonalIndex ++) {
      final int maxRowIndex = getRowWithMaxColumnValueIndex(matrix, diagonalIndex);
      swapCurrentRowWithMaxRow(matrix, diagonalIndex, maxRowIndex);
      suppressFirstNonZeroColumnBelowCurrentRow(matrix, diagonalIndex, modulus);
    }
    return solveUpperTriangleMatrix(matrix, modulus);
  }

  private static int getRowWithMaxColumnValueIndex(final int[][] matrix, final int diagonalIndex) {
    final int rows = getMatrixRows(matrix);
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
      final int maxRowIndex) { // diagonalIndex == currentRow
    final int cols = getMatrixCols(matrix);
    for (int col = diagonalIndex ; col < cols ; col ++) {
      final int aux = matrix[diagonalIndex][col];
      matrix[diagonalIndex][col] = matrix[maxRowIndex][col];
      matrix[maxRowIndex][col] = aux;
    }
  }

  private static void suppressFirstNonZeroColumnBelowCurrentRow(final int[][] matrix,
      final int diagonalIndex, final int modulus) {
    // TODO
  }

  private static int[] solveUpperTriangleMatrix(final int[][] matrix, final int modulus) {
    return new int[0]; // TODO
  }

  private static int getMatrixRows(final int[][] matrix) {
    return matrix.length;
  }

  private static int getMatrixCols(final int[][] matrix) {
    return matrix.length + 1; // cols = rows + 1 assuming matrix is valid
  }
//  function gauss(A) {
//    var n = A.length;
//
//    for (var i=0; i<n; i++) {
//      // Search for maximum in this column
//      var maxEl = Math.abs(A[i][i]);
//      var maxRow = i;
//      for(var k=i+1; k<n; k++) {
//        if (Math.abs(A[k][i]) > maxEl) {
//          maxEl = Math.abs(A[k][i]);
//          maxRow = k;
//        }
//      }
//
//      // Swap maximum row with current row (column by column)
//      for (var k=i; k<n+1; k++) {
//        var tmp = A[maxRow][k];
//        A[maxRow][k] = A[i][k];
//        A[i][k] = tmp;
//      }
//
//      // Make all rows below this one 0 in current column
//      for (k=i+1; k<n; k++) {
//        var c = -A[k][i]/A[i][i];
//        for(var j=i; j<n+1; j++) {
//          if (i==j) {
//            A[k][j] = 0;
//          } else {
//            A[k][j] += c * A[i][j];
//          }
//        }
//      }
//    }
//
//    // Solve equation Ax=b for an upper triangular matrix A
//    var x= new Array(n);
//    for (var i=n-1; i>-1; i--) {
//      x[i] = A[i][n]/A[i][i];
//      for (var k=i-1; k>-1; k--) {
//        A[k][n] -= A[k][i] * x[i];
//      }
//    }
//    return x;
//  }

}
