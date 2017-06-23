package ar.edu.itba.cryptography;

import static ar.edu.itba.cryptography.BMPUtils.BMP_OFFSET.*;

/**
 * Reference: http://www.fileformat.info/format/bmp/corion.htm
 */
final class BMPUtils {

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
        BITMAP_OFFSET(0x000A),
        HOR_WIDTH_PIXELS(0x0012),
        VER_WIDTH_PIXELS(0x0016),
        BITS_PER_PIXEL(0x001C);

        private final int position;

        BMP_OFFSET(final int position) {
            this.position = position;
        }
    }

    static boolean isBMPFile(final byte[] image) {
        // 0x4D42 represent BM backwards because of the way the value is retrieved
        return getValue(image, ID, BYTES.WORD) == BMP_ID;
    }

    static int getBitmapOffset(final byte[] image) {
        return getValue(image, BITMAP_OFFSET, BYTES.DWORD);
    }

    static int getHorizontalWidthInPixels(final byte[] image) {
        return getValue(image, HOR_WIDTH_PIXELS, BYTES.DWORD);
    }

    static int getVerticalWidthInPixels(final byte[] image) {
        return getValue(image, VER_WIDTH_PIXELS, BYTES.DWORD);
    }

    static int getBitsPerPixel(final byte[] image) {
        return getValue(image, BITS_PER_PIXEL, BYTES.DWORD);
    }

    static void saveSeed(final byte[] image, final char seed) {
        putValue(image, seed, BMP_OFFSET.RESERVED, BYTES.WORD);
    }

    static char recoverSeed(final byte[] image) {
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

    /**
     * Get the byte stored in the offset position. The bytes to the left of the least significate byte are cleaned
     * to zero
     * @param image the file from which to retrieve the byte
     * @param offset the position in which the byte will be retrieved
     * @return the value stored in the image in the offset
     */
    private static int getByte(byte[] image, final int offset) {
        // Apply the & operator to make sure that no byte other than the Least Significant Byte is carried over in
        // the return value
        return image[offset] & 0x00FF;
    }

    /**
     * Stores the value of size 1 byte in the LSB (least significant bit) of each byte stored from the starting position
     * up to the startingPosition + 8. Each bit of the value is stored from the most significant bit to the least
     * significant bit from the byte in the startingPosition to the byte of last position (startingPosition + 8). It
     * is important to understand that the bits are stored in a bottom up fashion from the least significant bit to
     * the most significant bit (if bottom up means going from a higher memory position to a lower one).
     * @param image the file where to store the value
     * @param value the value to hide in the image
     * @param startingPosition the position from where to start hiding the bits of the value
     */
    public static void putValueInLSB(final byte[] image, byte value, final int startingPosition) {
        for(int i = 7; i >= 0; i--) {
            image[startingPosition + i] &= 0xFE; // Clean the last bit in the current byte
            final byte currentBit = (byte)(value & 0x01); // Get the next bit to store

            // Append the current bit of value to the LSB of the current byte in the image
            image[startingPosition + i] |= currentBit;

            value >>>= 1; // Prepare to retrieve the next bit in the next iteration
        }
    }

    /**
     * Obtain a hidden value of size 1 byte by obtaining each bit of the value from the LSB of the 8 bytes of the image
     * starting from the startingPosition.
     * @param image the file from which to obtain the hidden value
     * @param startingPosition the position from where to start obtaining the bits of the value
     * @return the value hidden in the LSB of the 8 bytes in the image since startingPosition
     */
    public static byte getValueInLSB(final byte[] image, final int startingPosition) {
        byte value = 0;

        for(int i = 0; i < 8; i++) {
            // the shift has to be done here to avoid the last shifting to the left after obtaining the complete value
            value <<= 1; // Shift the current value to the left to make space for the next bit to append (currentBit)
            final byte currentBit = (byte) (image[startingPosition + i] & 0x01); // Obtain the next bit to append
            value |= currentBit; // Append the bit to the value in the LSB position
        }

        return value;
    }
}
