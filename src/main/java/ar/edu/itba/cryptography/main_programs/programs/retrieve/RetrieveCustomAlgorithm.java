package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import ar.edu.itba.cryptography.interfaces.RetrieveAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public class RetrieveCustomAlgorithm implements RetrieveAlgorithm {

  @Override
  public byte[] retrieveHeader(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths) {
    // TODO: we will initially retrieve 54 bytes for the header =>
    // TODO: => keep adding bytes to header until offset-1 is the length of the header (in bytes).
    // TODO: The remaining data (from offset to size) will be the data
    return new byte[0]; // TODO
  }

  @Override
  public byte[] retrieveData(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths, final int dataLength) {
    return new byte[0]; // TODO
  }
}
