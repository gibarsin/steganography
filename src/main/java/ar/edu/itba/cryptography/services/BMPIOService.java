package ar.edu.itba.cryptography.services;

import static ar.edu.itba.cryptography.services.IOService.exit;

import ar.edu.itba.cryptography.helpers.ByteHelper;
import ar.edu.itba.cryptography.services.IOService.ExitStatus;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is in charge of handling input & output files and references to where
 * each file should be written to or read from
 */
public class BMPIOService {
  public enum OpenMode {
    INPUT, OUTPUT
  }
  private static final String CWD = System.getProperty("user.dir");
  private static final int FIRST_ELEM_INDEX = 0;
  private static final String BMP_EXT = "glob:*.bmp";
  private static final PathMatcher bmpExtMatcher = FileSystems.getDefault().getPathMatcher(BMP_EXT);

  private final Map<Path, BMPData> inputFiles;
  private final Map<Path, BMPData> outputFiles;

  public BMPIOService() {
    inputFiles= new HashMap<>();
    outputFiles= new HashMap<>();
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public List<Path> openBmpFilesFrom(final Optional<String> optionalDir,
      final Optional<Integer> optionalN, final OpenMode mode) {
    final List<Path> paths;
    final String dir = optionalDir.orElse(CWD);
    try (final Stream<Path> pathsStream = Files.walk(Paths.get(dir))) {
      paths = pathsStream.filter(path -> Files.isRegularFile(path)
          && bmpExtMatcher.matches(path)).collect(Collectors.toList());
      loadPathsBasedOn(mode, optionalN, paths);
    } catch (final IOException e) {
      exit(ExitStatus.COULD_NOT_OPEN_INPUT_FILE, e);
      throw new IllegalStateException(); // Should never return from the above method
    }
    return paths;
  }


  public Path openBmpFile(final String filePathString, final OpenMode mode) {
    final Path pathToFile = Paths.get(filePathString);
    if (!bmpExtMatcher.matches(pathToFile)) {
      exit(ExitStatus.BAD_FILE_FORMAT, pathToFile);
      throw new IllegalStateException(); // Should never return from the above method
    }
    final Map<Path, BMPData> map = chooseMapBasedOn(mode);
    try {
      map.put(pathToFile, createBmpData(pathToFile));
    } catch (IOException e) {
      exit(ExitStatus.COULD_NOT_OPEN_INPUT_FILE, e);
      throw new IllegalStateException(); // Should never return from the above method
    }
    return pathToFile;
  }

  public void closeBmpFiles(final List<Path> paths, final OpenMode mode) {
    final Map<Path, BMPData> map = chooseMapBasedOn(mode);
    for (final Path path : paths) {
      map.remove(path);
    }
  }

  public void closeBmpFile(final Path path, final OpenMode mode) {
    chooseMapBasedOn(mode).remove(path);
  }

  public byte[] getHeaderBytesOf(final Path path, final OpenMode mode) {
    return chooseMapBasedOn(mode).get(path).getHeaderBytes(); // assuming path != null & path opened
  }

  public void setPathMatrixRow(final Path path, final OpenMode mode, final int row) {
    chooseMapBasedOn(mode).get(path).setMatrixRow(row); // assuming path != null & path opened
  }

  public int getPathMatrixRow(final Path path, final OpenMode mode) {
    return chooseMapBasedOn(mode).get(path).getMatrixRow(); // assuming path != null & path opened
  }

  public int getShadowNumber(final Path path, final OpenMode mode) {
    // assuming path != null & path opened
    return BMPService.recoverShadowNumber(chooseMapBasedOn(mode).get(path).getHeaderBytes());
  }

  public byte getNextSecretByte(final Path path, final OpenMode mode) { // TODO: bad feeling
    // assuming path != null & path opened
    final BMPData bmpData = chooseMapBasedOn(mode).get(path);
    return BMPService.getValueInLSB(bmpData.getBmp(), bmpData.getNext8BytesOffset());
  }

  public char getSeedFromSample(final List<Path> shadowsPaths, final OpenMode mode) {
    return chooseMapBasedOn(mode).get(shadowsPaths.get(FIRST_ELEM_INDEX)).getSeed();
  }

  public byte[] getDataBytes(final Path pathToSecret, final OpenMode mode) {
    return chooseMapBasedOn(mode).get(pathToSecret).getDataBytes();
  }

  public void setShadowNumber(final Path path, final OpenMode mode, final char x) {
    chooseMapBasedOn(mode).get(path).setShadowNumber(x);
  }

  public void setSeed(final Path path, final OpenMode mode, final char seed) {
    chooseMapBasedOn(mode).get(path).setSeed(seed);
  }

  public void writeDataToDisk(final Path path, final OpenMode mode) {
    final byte[] bmp = chooseMapBasedOn(mode).get(path).getBmp();
    IOService.createFile(path, ByteHelper.hexadecimalBytesToString(bmp));
  }

  // private methods

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private void loadPathsBasedOn(final OpenMode mode, final Optional<Integer> optionalN,
      final List<Path> paths) throws IOException {
    final Map<Path, BMPData> map = chooseMapBasedOn(mode);
    if (optionalN.isPresent()) {
      final int n = optionalN.get();
      for (int i = 0 ; i < n ; i++) {
        final Path path = paths.get(i);
        map.put(path, createBmpData(path));
      }
    } else {
      for (final Path path : paths) {
        map.put(path, createBmpData(path));
      }
    }
  }

  private BMPData createBmpData(final Path path) throws IOException {
    return new BMPData(Files.readAllBytes(path));
  }

  private Map<Path, BMPData> chooseMapBasedOn(final OpenMode mode) {
    if (mode == OpenMode.INPUT) {
      return inputFiles;
    }
    return outputFiles;
  }

  private static class BMPData {
    private final byte[] bmp;
    private int nextByte;
    private int matrixRow;

    /* package-private */ BMPData(final byte[] bmp) {
      // TODO: validate correct BMP format: bmp file header and size == offset + width * height
      this.bmp = bmp;
      this.nextByte = BMPService.getBitmapOffset(bmp);
      this.matrixRow = 0;
    }

    /* package-private */ byte[] getBmp() {
      return this.bmp;
    }

    /* package-private */ byte[] getHeaderBytes() {
      final int offset = BMPService.getBitmapOffset(bmp);
      final byte[] header = new byte[offset];
      System.arraycopy(bmp, FIRST_ELEM_INDEX, header, FIRST_ELEM_INDEX, offset);
      return header;
    }

    /* package-private */ byte[] getDataBytes() {
      final int totalSize = BMPService.getBitmapSize(bmp);
      final int offset = BMPService.getBitmapOffset(bmp);
      final int dataSize = totalSize - offset;
      final byte[] data = new byte[dataSize];
      System.arraycopy(bmp, offset, data, FIRST_ELEM_INDEX, dataSize);
      return data;
    }

    /* package-private */ int getNext8BytesOffset() {
      final int aux = this.nextByte;
      this.nextByte += 8; // 8 bytes will be consumed if this method is called
      return aux;
    }

    /* package-private */ void setMatrixRow(final int matrixIndex) {
      this.matrixRow = matrixIndex;
    }

    /* package-private */ int getMatrixRow() {
      return this.matrixRow;
    }

    /* package-private */ char getSeed() {
      return BMPService.recoverSeed(this.bmp);
    }

    /* package-private */ void setShadowNumber(final char shadowNumber) {
      BMPService.saveShadowNumber(bmp, shadowNumber);
    }

    /* package-private */ void setSeed(final char seed) {
      BMPService.saveSeed(bmp, seed);
    }
  }
}
