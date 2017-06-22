package ar.edu.itba.cys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ar.edu.itba.cys.App.EXIT_CODE.*;


public final class App
{
    private static final String FILE_EXTENSION_BMP = "bmp";

    enum EXIT_CODE {
        NO_ARGS(-1, "[FAIL] - No arguments passed."),
        NO_FILE(-2, ""),
        BAD_N_ARGUMENTS(-3, "[FAIL] - Invalid number of arguments."),
        BAD_ARGUMENT(-4, "[FAIL] - Invalid argument."),
        FILE_READING_ERROR(-5, "[FAIL] - Error when trying to read the file."),
        UNEXPECTED_ERROR(-6, "[FAIL] - An unexpected error has occurred."),
        BAD_FILE_EXTENSION(-7, "[FAIL] - File has not the correct extension.");

        private final int code;
        private final String message;

        EXIT_CODE(final int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    private static void exit(final EXIT_CODE exitCode) {
        System.err.println(exitCode.getMessage());
        System.exit(exitCode.getCode());
    }

    public static void main(final String[] args) {
        if(args.length == 0) {
            exit(NO_ARGS);
        } else if(args.length < 5) {
            exit(BAD_N_ARGUMENTS);
        }

        switch (args[0]) {
            case "-d":
                distributeSecretImage(args);
                break;
            case "-r":
                recoverSecretImage(args);
                break;
            default:
                exit(BAD_ARGUMENT);
                break;
        }
    }

    private static void distributeSecretImage(final String[] args) {
        if(!args[1].equals("-secret")) {
            exit(EXIT_CODE.BAD_ARGUMENT);
        }

        final String workingPath = getWorkingPath();

        final String secretImageFileName = args[2];
        final byte[] image = loadImage(workingPath + secretImageFileName);

//        printFileInHex(image); //Uncomment to print the whole image file

        if(!args[3].equals("-k")) {
            exit(EXIT_CODE.BAD_ARGUMENT);
        }

        try {
            final int k = Integer.valueOf(args[4]);
        } catch (final NumberFormatException e) {
            exit(EXIT_CODE.BAD_ARGUMENT);
        }

        // Check if the file obtained has a BMP header
        System.out.println("isBMP? " + BMPUtils.isBMPFile(image));

        // Save and recover seed (For testing purposes)
        System.out.println("Seed to save = " + (int) PermutationTable.getSEED());
        BMPUtils.saveSeed(image, PermutationTable.getSEED());
        System.out.println("Seed recovered from image = " + (int) BMPUtils.recoverSeed(image));
    }

    private static void recoverSecretImage(final String[] args) {
        if(!args[1].equals("-secret")) {
            exit(EXIT_CODE.BAD_ARGUMENT);
        }

        final String outputFileName = args[2];

        if(!args[3].equals("-k")) {
            exit(EXIT_CODE.BAD_ARGUMENT);
        }
        try {
            final int k = Integer.valueOf(args[4]);
        } catch (final NumberFormatException e) {
            exit(EXIT_CODE.BAD_ARGUMENT);
        }
    }

    /**
     * Return the current working directory.
     * Example: a string returned may be "/Users/gonzalo/IdeaProjects/steganography/"
     *
     * @return a string containing the absolute path of the working directory
     */
    public static String getWorkingPath() {
        return System.getProperty("user.dir") + "/";
    }

    private static byte[] loadImage(final String imagePath) {
        if(!hasCorrectExtension(imagePath, FILE_EXTENSION_BMP)) {
            exit(EXIT_CODE.BAD_FILE_EXTENSION);
        }

        final Path path = Paths.get(imagePath);
        byte[] imageFile = null;

        try {
            imageFile = Files.readAllBytes(path);
        } catch (final IOException e) {
            exit(EXIT_CODE.FILE_READING_ERROR);
        } catch (final OutOfMemoryError | SecurityException e) {
            exit(EXIT_CODE.UNEXPECTED_ERROR);
        }

        return imageFile;
    }

    private static boolean hasCorrectExtension(final String filePath, final String fileExtension) {
        try {
            return filePath.substring(filePath.lastIndexOf(".") + 1).equals(fileExtension);
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Prints file in a table manner in which the first column is the position and the second column is the value
     * in that position.
     *
     * @param file the file to printInHexFormat
     */
    private static void printFileInHex(final byte[] file) {
        for(int i = 0; i < file.length; i++) {
            System.out.println(i + "\t" + String.format("%02X", file[i]));
        }
    }
}
