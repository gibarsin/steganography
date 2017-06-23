package ar.edu.itba.cryptography.main_programs;

import ar.edu.itba.cryptography.interfaces.MainProgramBuilder;
import ar.edu.itba.cryptography.main_programs.programs.DistributionProgram;
import ar.edu.itba.cryptography.main_programs.programs.HelpProgram;
import ar.edu.itba.cryptography.main_programs.programs.RetrieveProgram;

public abstract class ProgramBuilderFactory {
  /*
   * For each method, when `MainProgramBuilder.build` is called,
   * it returns an instance of the specified program
   */

  public static MainProgramBuilder helpProgramBuilder() {
    return args -> new HelpProgram();
  }

  public static MainProgramBuilder distributionProgramBuilder() {
    return DistributionProgram::new;
  }

  public static MainProgramBuilder retrieveProgramBuilder() {
    return RetrieveProgram::new;
  }
}
