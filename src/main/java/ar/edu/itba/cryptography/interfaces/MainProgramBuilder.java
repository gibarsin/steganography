package ar.edu.itba.cryptography.interfaces;

public interface MainProgramBuilder {
  /**
   * Build an instance of a Main Program with the specified {@code args}.
   *
   * @param args the desired params that will be used to instantiate & within the main program run
   * @return the built instance
   */
  MainProgram build(String[] args);
}
