package ar.edu.itba.cryptography;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.*;

import ar.edu.itba.cryptography.helpers.InputArgsHelper;
import ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs;
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
    mainPrograms.put(HELP_PROGRAM.getType(), ProgramBuilderFactory.helpProgramBuilder());
    mainPrograms.put(DISTRIBUTION_PROGRAM.getType(), ProgramBuilderFactory.distProgramBuilder());
    mainPrograms.put(RETRIEVE_PROGRAM.getType(), ProgramBuilderFactory.retrieveProgramBuilder());
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
    final Map<InputArgs, String> parsedArgs = InputArgsHelper.parseArgs(args);
    final MainProgramBuilder mainProgramBuilder = mainPrograms.get(parsedArgs.get(MAIN_PROGRAM));
    if (mainProgramBuilder == null) {
      return Optional.empty();
    }
    return Optional.of(mainProgramBuilder.build(parsedArgs));
  }
}
