package ar.edu.itba.cryptography.interfaces;

import ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs;
import java.util.Map;

public interface MainProgramBuilder {
  /**
   * Build an instance of a Main Program with the specified {@code parsedArgs}.
   *
   * @param parsedArgs the parsed args that will be used within the main program instantiation & run
   * @return the built instance
   */
  MainProgram build(Map<InputArgs, String> parsedArgs);
}
