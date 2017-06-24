package ar.edu.itba.cryptography.services;

import static ar.edu.itba.cryptography.services.IOService.exit;

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

  private static final int FIRST_ELEM_INDEX = 0;
  private static final String BMP_EXT = "glob:*.bmp";
  private static final PathMatcher bmpExtMatcher = FileSystems.getDefault().getPathMatcher(BMP_EXT);

  private final Map<Path, BMPData> inputFiles;
  private final Map<Path, BMPData> outputFiles;

  public BMPIOService() {
    inputFiles= new HashMap<>();
    outputFiles= new HashMap<>();
  }

  public List<Path> openBmpFilesFrom(final String dir, final OpenMode mode) {
    final List<Path> paths;
    try (final Stream<Path> pathsStream = Files.walk(Paths.get(dir))) {
      paths = pathsStream.filter(path -> Files.isRegularFile(path)
          && bmpExtMatcher.matches(path)).collect(Collectors.toList());
      final Map<Path, BMPData> map = chooseMapBasedOn(mode);
      for (final Path path : paths) {
        map.put(path, createBmpData(path));
      }
    } catch (final IOException e) {
      exit(ExitStatus.COULD_NOT_OPEN_INPUT_FILE, e);
      throw new IllegalStateException(); // Should never return from the above method
    }
    return paths;
  }

  public byte[] getHeaderBytesOf(final Path path) {
    // We are assuming the given path has already been open as input file & path != null
    return inputFiles.get(path).getHeaderBytes();
  }

  // private methods

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

    /* package-private */ BMPData(final byte[] bmp) {
      this.bmp = bmp;
      this.nextByte = BMPService.getBitmapOffset(bmp);
    }

    /* package-private */ byte getNextByte() {
      return bmp[nextByte ++];
    }

    /* package-private */ byte[] getHeaderBytes() {
      final int offset = BMPService.getBitmapOffset(bmp);
      final byte[] header = new byte[offset];
      System.arraycopy(bmp, FIRST_ELEM_INDEX, header, FIRST_ELEM_INDEX, offset);
      return header;
    }
  }
}