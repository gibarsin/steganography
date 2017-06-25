package ar.edu.itba.cryptography.helpers;

public abstract class ByteHelper {
  private static final int FIRST_ELEM_INDEX = 0;
  private static final int BYTE_MASK = 0x00FF;
  private static final String HEXADECIMAL_FORMAT = "%02X";

  public static int byteToUnsignedInt(final byte b) {
    return b & BYTE_MASK;
  }

  public static byte intToByte(final int i) {
    return (byte) i;
  }

  @Deprecated
  public static String hexadecimalBytesToString(final byte[] bytes) {
    final StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      sb.append(String.format(HEXADECIMAL_FORMAT, aByte));
    }
    return sb.toString();
  }

  public static byte[] merge(final byte[] bytes1, final byte[] bytes2) {
    final int length1 = bytes1.length;
    final int length2 = bytes2.length;
    final byte[] merge = new byte[length1 + length2];
    System.arraycopy(bytes1, FIRST_ELEM_INDEX, merge, FIRST_ELEM_INDEX, length1);
    System.arraycopy(bytes2, FIRST_ELEM_INDEX, merge, length1, length2);
    return merge;
  }
}
