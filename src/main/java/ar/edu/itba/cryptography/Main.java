package ar.edu.itba.cryptography;

import static ar.edu.itba.cryptography.helpers.InputParam.D_METHOD;
import static ar.edu.itba.cryptography.helpers.InputParam.H_METHOD;
import static ar.edu.itba.cryptography.helpers.InputParam.METHOD;
import static ar.edu.itba.cryptography.helpers.InputParam.R_METHOD;

import ar.edu.itba.cryptography.interfaces.MainProgram;
import ar.edu.itba.cryptography.interfaces.MainProgramBuilder;
import ar.edu.itba.cryptography.main_programs.ProgramBuilderFactory;
import ar.edu.itba.cryptography.services.IOService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Main {
  private static final Map<String, MainProgramBuilder> mainPrograms;
  static {
    mainPrograms = new HashMap<>();
    mainPrograms.put(H_METHOD.getName(), ProgramBuilderFactory.helpProgramBuilder());
    mainPrograms.put(D_METHOD.getName(), ProgramBuilderFactory.distributionProgramBuilder());
    mainPrograms.put(R_METHOD.getName(), ProgramBuilderFactory.retrieveProgramBuilder());
  }

  public static void main(String[] args) {
    final Optional<MainProgram> mainProgram = getMainProgram(args);
    if (!mainProgram.isPresent()) {
      IOService.exit(IOService.ExitStatus.BAD_ARGUMENT, "Invalid method");
      throw new IllegalStateException(); // Should never reach here
    }
    mainProgram.get().run(); // Run main method
  }

  // private methods

  private static Optional<MainProgram> getMainProgram(final String[] args) {
    if (args.length == 0) {
      IOService.exit(IOService.ExitStatus.NO_ARGS, null);
    }
    final String method = args[METHOD.getIndex()];
    final MainProgramBuilder mainProgramBuilder = mainPrograms.get(method);
    if (mainProgramBuilder == null) {
      return Optional.empty();
    }
    return Optional.of(mainProgramBuilder.build(args));
  }
}
