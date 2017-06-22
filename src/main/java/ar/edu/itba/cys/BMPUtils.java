package ar.edu.itba.cys;

/**
 * Reference: http://www.fileformat.info/format/bmp/corion.htm
 */
public final class BMPUtils {
    final static int OFFSET_BITMAP_OFFSET = 0x000A;
    final static int OFFSET_RESERVED = 0x0006;

    public static boolean isBMPFile(final byte[] image) {
        return image[0] == 'B' && image[1] == 'M';
    }

    public static int getBitmapOffset(final byte[] image) {
        return getOffsetValue(image, OFFSET_BITMAP_OFFSET);
    }

    private static int getOffsetValue(byte[] image, final int offset) {
        return image[offset] & 0x00FF;
    }

    public static void saveSeed(final byte[] image, final char seed) {
        image[OFFSET_RESERVED] = (byte)(seed & 0x00FF);
        image[OFFSET_RESERVED + 1] = (byte) (seed >>> 8);
    }

    public static char recoverSeed(final byte[] image) {
        char seed = (char) (image[OFFSET_RESERVED] & 0x00FF);
        seed |= (image[OFFSET_RESERVED + 1] << 8);

        return seed;
    }
}
