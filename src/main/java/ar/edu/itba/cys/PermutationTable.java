package ar.edu.itba.cys;

import java.util.Random;

public final class PermutationTable {
    private static final char SEED = 446;
    private static final int BYTE_UPPER_BOUND = 256;

    final Random random = new Random(SEED);

    final byte[][] table;

    public PermutationTable(final int size) {
        this.table = new byte[size][size];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                table[i][j] = (byte)random.nextInt(BYTE_UPPER_BOUND);
            }
        }
    }

    public static char getSEED() {
        return SEED;
    }

    public byte getValue(int row, int col) {
        return table[row][col];
    }

    /**
     * Print the permutation table in hex format
     */
    public void printInHexFormat() {
        for(int i = 0; i < table.length; i++) {
            for(int j = 0; j < table[0].length; j++) {
                System.out.printf("%02X ", table[i][j]);
            }
            System.out.println();
        }
    }
}
