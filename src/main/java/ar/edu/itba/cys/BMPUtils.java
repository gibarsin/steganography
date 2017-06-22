package ar.edu.itba.cys;

import static ar.edu.itba.cys.BMPUtils.BMP_OFFSET.BITMAP_OFFSET;
import static ar.edu.itba.cys.BMPUtils.BMP_OFFSET.ID;

/**
 * Reference: http://www.fileformat.info/format/bmp/corion.htm
 */
public final class BMPUtils {

    private static final int BMP_ID = 0x4D42;

    /**
     * Represent size in byte units
     */
    enum BYTES {
        WORD(2),
        DWORD(4);

        private final int size;

        BYTES(final int size) {
            this.size = size;
        }
    }

    /**
     * Represents the memory positions of header fields in the BMP file
     */
    enum BMP_OFFSET {
        ID(0x0000),
        RESERVED(0x0006),
        BITMAP_OFFSET(0x000A);

        private final int position;

        BMP_OFFSET(final int position) {
            this.position = position;
        }
    }

    public static boolean isBMPFile(final byte[] image) {
        // 0x4D42 represent BM backwards because of the way the value is retrieved
        return getValue(image, ID, BYTES.WORD) == BMP_ID;
    }

    public static int getBitmapOffset(final byte[] image) {
        return getValue(image, BITMAP_OFFSET, BYTES.DWORD);
    }

    public static void saveSeed(final byte[] image, final char seed) {
        putValue(image, seed, BMP_OFFSET.RESERVED, BYTES.WORD);
    }

    public static char recoverSeed(final byte[] image) {
        return (char) getValue(image, BMP_OFFSET.RESERVED, BYTES.WORD);
    }

    /**
     * Get the value of size "bytes" from the starting offset in the image
     * @param image file to retrieve the value
     * @param startingOffset the starting position in the image file to retrieve the value
     * @param bytes the size of the value in byte units.
     * @return the value
     */
    private static int getValue(final byte[] image, final BMP_OFFSET startingOffset, final BYTES bytes) {
        int value = 0;

        // Each iteration grabs a byte from the final value which will be returned
        for(int i = 0; i < bytes.size; i++) {
            // The byte is bit shifted by a multiple of 8 because the value is stored in byte units
            value |= (getByte(image, startingOffset.position + i) << 8 * i);
        }

        return value;
    }

    /**
     * Put a value of size "bytes" in the image file from the startingOffset.
     * The value is stored from Less Significant Byte to Most Significant Byte. For example, if the value to store
     * is "12 BD" (represented in HEX) then the value will be stored as BD in the startingOffset position and 12 in the
     * (startingOffset + 1) position.
     * @param image the file to put in the value
     * @param value the value to store
     * @param startingOffset the position in the image to start saving the value
     * @param bytes the size of the value in byte units
     */
    private static void putValue(final byte[] image, final int value, final BMP_OFFSET startingOffset,
                                 final BYTES bytes) {
        for(int i = 0; i < bytes.size; i++) {
            // The byte is bit shifted by a multiple of 8 because the value was stored in byte units
            image[startingOffset.position + i] = (byte) (value >>> 8 * i);
        }
    }

    private static int getByte(byte[] image, final int offset) {
        // Apply the & operator to make sure that no byte other than the Least Significant Byte is carried over in
        // the return value
        return image[offset] & 0x00FF;
    }
}
