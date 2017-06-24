package ar.edu.itba.cryptography.services;

import static ar.edu.itba.cryptography.services.IOService.ExitStatus.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOService {
  private static final Logger LOGGER = LoggerFactory.getLogger(IOService.class);
  private static final String CWD = System.getProperty("user.dir");

  private static final String CHECK_LOGS = "\nCheck logs for more info.";
  private static final String ABORTING = CHECK_LOGS + "\nAborting...";
  private static final String NO_DETAIL = "[NO DETAIL GIVEN]";

  private static final Map<Path, BufferedWriter> outputFiles = new HashMap<>();
  private static final Map<Path, Stream<String>> inputFiles = new HashMap<>();

  // Exit Codes
  public enum ExitStatus {
    NO_ARGS(-1,
        "[FAIL] - No arguments passed. Try 'help' for more information.",
        "[FAIL] - No arguments passed. Try 'help' for more information."),
    NO_FILE(-2, "", ""),
    BAD_N_ARGUMENTS(-3,
        "[FAIL] - Bad number of arguments. Try 'help' for more information.",
        "[FAIL] - Bad number of arguments. Try 'help' for more information."),
    NUMBER_EXPECTED(-4,
        "[FAIL] - Argument must be a number: {}",
        "[FAIL] - Invalid argument. Try 'help' for more information." + ABORTING),
    NOT_A_FILE(-5, "", ""),
    UNEXPECTED_ERROR(-6,
        "[FAIL] - An unexpected error has occurred. Caused by: ",
        "[FAIL] - An unexpected error has occurred." + ABORTING),
    BAD_FILE_FORMAT(-7,
        "[FAIL] - File {} has not the correct extension. Caused by: ",
        "[FAIL] - File has not the correct extension." + ABORTING),
    MKDIRS_FAILED(-8,
        "[FAIL] - Create directory operation failed while trying to create dir: '{}'",
        "[FAIL] - Create directory operation failed." + ABORTING),
    VALIDATION_FAILED(-9,
        "[FAIL] - Validation not passed: {}",
        "[FAIL] - Validation not passed." + ABORTING),
    DELETE_EXISTING_FILE_FAILED(-10,
        "[FAIL] - Could not delete the existing file: '{}'",
        "[FAIL] - Could not delete an existing file." + ABORTING),
    WRITE_FILE_ERROR(-11,
        "[FAIL] - An unexpected IO Exception occurred while writing the file. Caused by: ",
        "[FAIL] -  An unexpected IO Exception occurred while writing a file." + CHECK_LOGS),
    COULD_NOT_OPEN_OUTPUT_FILE(-13,
        "[FAIL] - Could not open output file: {}",
        "[FAIL] - Could not open an output file." + ABORTING),
    COULD_NOT_OPEN_INPUT_FILE(-14,
        "[FAIL] - Could not open input file: {}",
        "[FAIL] - Could not open an input file." + ABORTING),
    BAD_ARGUMENT(-15,
        "[FAIL] - Invalid argument: {}.",
        "[FAIL] - Invalid argument. Try 'help' for more information.");

    private final int code;
    private final String loggerMsg;
    private final String msg;

    ExitStatus(final int code, final String loggerMsg, final String msg) {
      this.code = code;
      this.loggerMsg = loggerMsg;
      this.msg = msg;
    }

    public int getCode() {
      return code;
    }

    public String getLoggerMsg() {
      return loggerMsg;
    }

    public String getMsg() {
      return msg;
    }
  }

  /**
   * Access i-th element of the array
   * validating that the args array has the necessary length to access it.
   * @param args The array that will be validated
   * @param i The index that wants to be accessed
   * @return The i-th element of the args array, if access is valid;
   *         otherwise, program is automatically aborted, with a corresponding error message
   */
  public static String validArgsAccess(final String[] args, final int i) {
    if (args.length <= i) {
      IOService.exit(IOService.ExitStatus.BAD_N_ARGUMENTS, null);
      // should never return from the above code
      throw new IllegalStateException();
    }

    return args[i];
  }

  /**
   * Creates the specified {@code fileName}.
   * <P>
   * It also prepares the file for being written.
   * After using this file, you MUST close the file using the method provided by this service.
   * IF NOT, DATA CAN BE LOST
   * <P>
   * The destination folder for the file must exists.
   * <P>
   * If the file exists, it tries to delete it first.
   * <P>
   * If anything fails during these operations,
   * program is aborted with a detail log and display message,
   * and the corresponding exit status code.
   * @param fileName file path + name + extension
   * @return path of the created output file
   *
   */
  public static Path createOutputFile(final String fileName) {
    final Path pathToFile = IOService.createFile(CWD, fileName);
    if (!IOService.openOutputFile(pathToFile, true)) {
      IOService.exit(COULD_NOT_OPEN_OUTPUT_FILE, pathToFile);
    }
    // only reach here if could open file
    return pathToFile;
  }

  /**
   * Creates the specified {@code fileName.fileExtension} file at the specified {@code fileFolder} destination folder.
   * <P>
   * It also prepares the file for being written.
   * After using this file, you MUST close the file using the method provided by this service. IF NOT, DATA CAN BE LOST
   * <P>
   * If the destination folder does not exists, it tries to create it.
   * <P>
   * If the file exists, it tries to delete it first.
   * <P>
   * If anything fails during these operations, program is aborted with a detail log and display message,
   * and the corresponding exit status code.
   * @param fileFolder folder to save the new file
   * @param fileName file's name without extension
   * @param fileExtension file's extension
   * @return path of the created output file
   *
   */
  public static Path createOutputFile(final String fileFolder,
      final String fileName,
      final String fileExtension) {
    final String file = fileName + fileExtension;
    final Path pathToFile = IOService.createFile(fileFolder, file);
    if (!IOService.openOutputFile(pathToFile, true)) {
      IOService.exit(COULD_NOT_OPEN_OUTPUT_FILE, pathToFile);
    }
    // only reach here if could open file
    return pathToFile;
  }

  private static Path createFile(final String destFolder, final String file) {
    return createFile(destFolder, file, null);
  }

  /**
   * Creates the specified {@code file} at the specified destination folder
   * and saves the specified {@code data} on it.
   * <P>
   * If the destination folder does not exists, it tries to create it.
   * <P>
   * If the file exists, it tries to delete it first.
   * <P>
   * If anything fails during these operations, program is aborted with a detail log and display message,
   * and the corresponding exit status code.
   * @param destFolder destination folder of the new file
   * @param file name of the new file
   * @param data data to be saved on the new file
   * @return the path to the just created file
   */
  private static Path createFile(final String destFolder, final String file, final String data) {
    return createFile(Paths.get(destFolder, file), data);
  }

  private static Path createFile(final Path pathToFile, final String data) {
    final Path destFolder = pathToFile.normalize().getParent();
    if (destFolder != null) {
      final File dataFolder = new File(destFolder.toString());
      // tries to make directory
      if (Files.notExists(destFolder) && !dataFolder.mkdirs()) {
        exit(MKDIRS_FAILED, destFolder);
      }
    }

    if(Files.exists(pathToFile)) {
      deleteWhenExists(pathToFile);
    }

    if (data != null) {
      if (!appendToFile(pathToFile, data)) {
        exit(WRITE_FILE_ERROR, null);
      }
    }

    return pathToFile;
  }

  /**
   * Appends - {@code data} to the specified file
   *
   * @param pathToFile path to the file where data is going to be written
   * @param data data to be written in file
   * @return true if data could be appended; false otherwise (and error is logged)
   */
  public static boolean appendToFile(final Path pathToFile, final String data) {
    return writeFile(pathToFile, data);
  }

  /**
   * Exits program using the exit status information (code, logger message and standard output message).
   * <P>
   * An errorSource object can be passed so as the logger can show what made the program failed.
   * @param exitStatus exit status enum
   * @param errorSource detail error source for being passed to the logger; can be null if no detail is needed
   */
  public static void exit(final ExitStatus exitStatus, final Object errorSource) {
    final Object reason = errorSource == null ? NO_DETAIL : errorSource;
    writeFailMessages(exitStatus, reason);
    System.exit(exitStatus.getCode());
  }

  /**
   * Opens the given {@code pathToFile} file with the given {@code append} mode.
   *
   * @param pathToFile path to the file to be opened for writing
   * @param append mode of write - true for append ; false otherwise
   * @return true if the file could be successfully opened ; false otherwise
   */
  public static boolean openOutputFile(final Path pathToFile, final boolean append) {
    try {
      final BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile.toFile(), append));

      // if here, there was no exception
      outputFiles.put(pathToFile, writer);

      return true; // file opened
    } catch (final IOException e) {
      return false; // file not opened
    }
  }

  /**
   * Closes the given {@code pathToFile} file
   * @param pathToFile path to the output file to be closed
   */
  public static void closeOutputFile(final Path pathToFile) {
    final BufferedWriter writer = outputFiles.remove(pathToFile);
    try {
      // close the writer regardless of what happens...
      if (writer != null) {
        writer.close();
      }
    } catch (Exception ignored) {
    }
  }

  public static boolean openInputFile(final Path pathToFile) {
    try {
      final Stream<String> reader = Files.lines(pathToFile);

      // if here, there was no exception
      inputFiles.put(pathToFile, reader);
      return true; // file opened
    } catch (final IOException e) {
      return false; // file not opened
    }
  }

  /**
   * Closes the given {@code pathToFile} file
   * @param pathToFile path to the input file to be closed
   */
  public static void closeInputFile(final Path pathToFile) {
    final Stream<String> reader = inputFiles.remove(pathToFile);
    try {
      // close the writer regardless of what happens...
      if (reader != null) {
        reader.close();
      }
    } catch (Exception ignored) {
    }
  }

  /**
   * Parses as double the given string.
   * Exits if an error is encountered
   * @param s string to be parsed
   * @param varErrMsg variable name to be displayed if an error raise
   * @return the parsed double
   */
  public static double parseAsDouble(final String s, final String varErrMsg) {
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      exit(NUMBER_EXPECTED, new Object[] { varErrMsg, e });
      return -1;
    }
  }

  /**
   * Parses as int the given string.
   * Exits if an error is encountered
   * @param s string to be parsed
   * @param varErrMsg variable name to be displayed if an error raise
   * @return the parsed int
   */
  public static int parseAsInt(final String s, final String varErrMsg) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      exit(NUMBER_EXPECTED, new Object[] { varErrMsg, e });
      return -1;
    }
  }

  /**
   * Parses as boolean the given string.
   * Exits if an error is encountered
   * @param s string to be parsed
   * @param varErrMsg variable name to be displayed if an error raise
   * @return the parsed boolean
   */
  public static boolean parseAsBoolean(final String s, final String varErrMsg) {
    try {
      return Boolean.parseBoolean(s);
    } catch (NumberFormatException e) {
      exit(NUMBER_EXPECTED, new Object[] { varErrMsg, e });
      return false; // should never reach here
    }
  }


  public static Stream<String> readLines(final Path filePath) {
    return inputFiles.get(filePath);
  }

  // private methods

  private static void writeFailMessages(final ExitStatus exitStatus, final Object reason) {
    LOGGER.error(exitStatus.getLoggerMsg(), reason);
    System.out.println(exitStatus.getMsg());
  }

  /**
   * Try to delete a file, knowing that it exists.
   * If the file cannot be deleted, program is aborted with the corresponding exit code
   * @param pathToFile the file path that refers to the file that will be deleted
   */
  private static void deleteWhenExists(final Path pathToFile) {
    try {
      Files.deleteIfExists(pathToFile);
    } catch(IOException e) {
      exit(DELETE_EXISTING_FILE_FAILED, pathToFile.toString());
    }
  }

  /**
   * Writes {@code data} to the specified file using or not appended mode accordingly to {@code appended}'s value
   *
   * @param pathToFile path to the file where data is going to be written
   * @param data data to be written in file
   * @return true if data could be written; false otherwise
   */
  private static boolean writeFile(final Path pathToFile, final String data) {
    final BufferedWriter writer = outputFiles.get(pathToFile);
    try {
      writer.write(data);
      return true;
    } catch (IOException e) {
      writeFailMessages(WRITE_FILE_ERROR, e);
      return false;
    }
  }
}
