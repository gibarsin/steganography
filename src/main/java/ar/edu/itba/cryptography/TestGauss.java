package ar.edu.itba.cryptography;

import ar.edu.itba.cryptography.helpers.GaussSolverHelper;
import ar.edu.itba.cryptography.helpers.MatrixHelper;
import java.util.Arrays;

public class TestGauss {

  public static void main(String[] args) {
    final int[][] matrix = new int[][] {
        {1,1,1,6},
        {1,2,4,1},
        {1,3,2,0}
    };
    final int mod = 7;
    final int[] resolve = GaussSolverHelper.solve(matrix, mod);
    for (final int i : resolve) {
      System.out.println(i);
    } // expect: 1, 3, 2

    final int[][] copy = MatrixHelper.copyOf(matrix);
    for (int i = 0 ; i < matrix.length ; i++) {
      if(!Arrays.equals(copy[i], matrix[i])) throw new IllegalStateException("NOT EQUAL");
    }
  }
}
