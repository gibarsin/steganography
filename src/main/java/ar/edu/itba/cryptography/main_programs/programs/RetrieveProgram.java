package ar.edu.itba.cryptography.main_programs.programs;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.IMAGES_DIR;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.K;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.N;
import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.SECRET;

import ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs;
import ar.edu.itba.cryptography.interfaces.MainProgram;
import ar.edu.itba.cryptography.services.BMPService;
import ar.edu.itba.cryptography.services.IOService;
import ar.edu.itba.cryptography.services.IOService.ExitStatus;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RetrieveProgram implements MainProgram {
  private static final String HEXADECIMAL_FORMAT = "%02X";
  private static final int STANDARD_K_VALUE = 8;

  private final Path pathToOutput;
  private final int k;
  private final List<Path> pathsToShadows;

  private RetrieveProgram(final Path pathToOutput,
      final int k, final List<Path> pathsToShadows) {
    this.pathToOutput = pathToOutput;
    this.k = k;
    this.pathsToShadows = pathsToShadows;
  }

  @Override
  public void run() {
    if (this.k == STANDARD_K_VALUE) {
      runStandardRetrieveAlgorithm();
    } else {
//      runCustomRetrieveAlgorithm(); // TODO
    }
  }

  private void runStandardRetrieveAlgorithm() { // TODO: DOING
    // get the header of any image => it will be used as the header of the retrieved message
    final byte[] header = getHeader();
    // get the total bytes to retrieve (size - offset)
    final int size = BMPService.getBitmapSize(header);
    final int offset = BMPService.getBitmapOffset(header);
    final int dataBytes = size - offset;
    // TODO: in k != 8, header.length will initially be 54 bytes => keep adding bytes to header
    // TODO:   until offset-1 is the length of the header (in bytes). The remaining data will be the data
    final byte[] data = getData(dataBytes);
    // write the retrieved secret (header + data) into the specified pathToOutput
    final StringBuilder bmpFileString = hexadecimalBytesToString(header);
    bmpFileString.append(hexadecimalBytesToString(data));
    IOService.appendToFile(this.pathToOutput, bmpFileString.toString());
    // close the pathToOutput file
    IOService.closeOutputFile(this.pathToOutput);
  }

  private byte[] getHeader() {
    return new byte[0]; // TODO
  }

  private StringBuilder hexadecimalBytesToString(final byte[] bytes) {
    final StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      sb.append(String.format(HEXADECIMAL_FORMAT, aByte));
    }
    return sb;
  }

  private byte[] getData(final int dataBytes) { // TODO
    // for each byte to retrieve
    //  for each shadow i file
    //    get byte = p(i) joining, from the first to the last byte of the range, the last bit of each byte in the range
    //  solve the equation system using the Gauss method => this is the byte of the current iteration
    return new byte[0];
  }

  public static MainProgram build(final Map<InputArgs, String> parsedArgs) {
    final String secret = validateArgAccess(parsedArgs, SECRET, true);
    final String kString = validateArgAccess(parsedArgs, K, true);
    validateArgAccess(parsedArgs, N, false);
    final String dir = validateArgAccess(parsedArgs, IMAGES_DIR, true);

    final Path pathToOutput = IOService.createOutputFile(secret);
    final int k = IOService.parseAsInt(kString, K.getDescription());
    final List<Path> pathsToShadows = IOService.openAllByteFilesFrom(dir); // TODO
    return new RetrieveProgram(pathToOutput, k, pathsToShadows);
  }

  /**
   *
   * @param parsedArgs all the parsed arguments
   * @param arg the argument to be retrieved, if access is allowed
   * @param shouldBeDefined true if arg is expected to be != null; false if should be == null
   * @return the parsed argument value, if access is allowed
   * @implNote
   *  null && should => error: missing argument
   *  null && !should => OK
   *  !null && should => OK
   *  !null && !should => error: extra argument
   */
  private static String validateArgAccess(final Map<InputArgs, String> parsedArgs,
      final InputArgs arg, final boolean shouldBeDefined) {
    final String parsedArg = parsedArgs.get(arg);
    if (parsedArg == null && shouldBeDefined) {
      IOService.exit(ExitStatus.BAD_ARGUMENT, "Undefined parameter: " + arg.getDescription());
    } else if (parsedArg != null && !shouldBeDefined) {
      IOService.exit(ExitStatus.BAD_ARGUMENT, "Extra defined parameter: " + arg.getDescription());
    }
    return parsedArg;
  }
}
