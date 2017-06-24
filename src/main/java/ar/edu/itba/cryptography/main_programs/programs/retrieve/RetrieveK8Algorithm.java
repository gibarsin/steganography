package ar.edu.itba.cryptography.main_programs.programs.retrieve;

import ar.edu.itba.cryptography.interfaces.RetrieveAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public class RetrieveK8Algorithm implements RetrieveAlgorithm {

  @Override
  public byte[] retrieveHeader(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths) {
    // get the header of any image => it will be used as the header of the retrieved message
    return new byte[0]; // TODO
  }

  @Override
  public byte[] retrieveData(final BMPIOService bmpIOService,
      final List<Path> shadowsPaths, final int dataLength) {
    // for each byte to retrieve
    //  for each shadow i file
    //    get byte = p(i) joining, from the first to the last byte of the range, the last bit of each byte in the range
    //  solve the equation system using the Gauss method => this is the byte of the current iteration
    return new byte[0]; // TODO
  }
}
