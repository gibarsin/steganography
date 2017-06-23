package ar.edu.itba.cryptography.helpers;

public enum InputParam {
  // Used to choose the current program
  METHOD(0, "<method>"),
  // Help Program Parameters
  H_METHOD(0, "-h"),
  // Distribution Program Parameters
  D_METHOD(0, "-d"), D_SECRET(1, "-secret </path/to/image>"), D_K(2, "-k <number>"),
  D_N(3, "[-n <number>]"), D_IMAGES_DIR(4, "[-dir <images_directory]"),
  // Retrieve Program Parameters
  R_METHOD(0, "-r"), R_SECRET(1, "-secret </path/to/image>"), R_K(2, "-k <number>"),
  R_IMAGES_DIR(3, "[-dir <images_directory]");
  private final int index;
  private final String name;
  InputParam(final int index, final String name) {
    this.index = index;
    this.name = name;
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }
}
