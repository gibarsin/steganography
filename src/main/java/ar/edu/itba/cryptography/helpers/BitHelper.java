package ar.edu.itba.cryptography.helpers;

public abstract class BitHelper {
  private static final int BYTE_MASK = 0x00FF;

  public static int byteToUnsignedInt(final byte b) {
    return b & BYTE_MASK;
  }

  public static byte intToByte(final int i) {
    return (byte) i;
  }
}
