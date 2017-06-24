package ar.edu.itba.cryptography.helpers;

public abstract class ByteHelper {
  private static final int BYTE_MASK = 0x00FF;
  private static final String HEXADECIMAL_FORMAT = "%02X";

  public static int byteToUnsignedInt(final byte b) {
    return b & BYTE_MASK;
  }

  public static byte intToByte(final int i) {
    return (byte) i;
  }

  public static StringBuilder hexadecimalBytesToString(final byte[] bytes) {
    final StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      sb.append(String.format(HEXADECIMAL_FORMAT, aByte));
    }
    return sb;
  }
}
