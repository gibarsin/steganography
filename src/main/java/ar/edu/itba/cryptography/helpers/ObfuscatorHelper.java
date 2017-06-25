package ar.edu.itba.cryptography.helpers;

import java.util.Random;

public abstract class ObfuscatorHelper {
  private static final Random randomGenerator = new Random();
  private static final int MAX_BYTE_EXCLUDED = 256;

  public static char generateSeed() {
    return (char) randomGenerator.nextInt();
  }

  public static byte[] toggleObfuscation(final byte[] originalData, final int seed) {
    final int length = originalData.length;
    final byte[] permutationTable = createPermutationTable(length, seed);
    final byte[] toggledObfuscationData = new byte[length];
    for (int i = 0 ; i < length ; i ++) {
      toggledObfuscationData[i] = (byte) (originalData[i] ^ permutationTable[i]);
    }
    return toggledObfuscationData;
  }

  private static byte[] createPermutationTable(final int length, final int seed) {
    final byte[] permutationTable = new byte[length];
    final Random seededRandom = new Random();
    seededRandom.setSeed(seed);
    for(int i=0; i < length; i++) {
      int num = seededRandom.nextInt(MAX_BYTE_EXCLUDED); // generates a number in [0,255]
      permutationTable[i] = (byte) num;
    }
    return permutationTable;
  }
}
