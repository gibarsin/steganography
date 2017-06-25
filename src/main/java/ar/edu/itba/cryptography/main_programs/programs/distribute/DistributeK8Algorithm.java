package ar.edu.itba.cryptography.main_programs.programs.distribute;

import ar.edu.itba.cryptography.interfaces.DistributeAlgorithm;
import ar.edu.itba.cryptography.services.BMPIOService;
import java.nio.file.Path;
import java.util.List;

public class DistributeK8Algorithm implements DistributeAlgorithm {
  @Override
  public void run(final BMPIOService bmpIOService, final Path pathToSecret,
      final List<Path> pathsToShadows, final int k) { // TODO
    // Extract the data bytes only
    // Generate a seed for the obfuscation
    // Obfuscate the data bytes using the generated seed
    // Save the seed in all shadow paths, associating its shadow number too
    // Generate the matrix A containing all the exponential evaluations of each shadow number
    // Distribute the obfuscated data into the shadows in chunks of k bytes using the built matrix
  }
}
