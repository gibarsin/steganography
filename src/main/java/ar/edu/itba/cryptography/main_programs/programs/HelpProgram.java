package ar.edu.itba.cryptography.main_programs.programs;

import static ar.edu.itba.cryptography.helpers.InputArgsHelper.InputArgs.*;

import ar.edu.itba.cryptography.interfaces.MainProgram;

public class HelpProgram implements MainProgram {
  // System independent new line character
  private static final String NL = System.lineSeparator();
  private static final String HELP_TEXT =
    "Steganography: share a .bmp secret among other .bmp files.\n" +
    "Arguments: \n" +
    "* -h : `help` that prints all available commands" + NL +
    "* " + DISTRIBUTION_PROGRAM.getDescription() + " " + SECRET.getDescription() + " "
    + K.getDescription() + " " +
    N.getDescription() + " " + IMAGES_DIR.getDescription() + NL +
    "     distributes a secret image into other images." + NL +
    "     - " + SECRET.getDescription() + ": the path to the secret image to be hidden." + NL +
    "     - " + K.getDescription() + ": the minimum number of shadows to recover the " + NL +
    "       secret image, in a (k, n)-threshold scheme." + NL +
    "     - " + N.getDescription() + ": OPTIONAL: the number of total shadows to be used " + NL +
    "       in a (k, n)-threshold scheme. If not specified, n will be the total " + NL +
    "       number of images in the specified directory." + NL +
    "     - " + IMAGES_DIR.getDescription() + ": OPTIONAL: the directory of the images " + NL +
    "       to be used as shadows. If not specified, the program will look those " + NL +
    "       images in the current working directory." + NL +
    "* " + RETRIEVE_PROGRAM.getDescription() + " " + SECRET.getDescription() + " " +
    K.getDescription() + " " + IMAGES_DIR.getDescription() + NL +
    "     retrieves a secret image from shadow images." + NL +
    "     - " + SECRET.getDescription() + ": the path where the revealed secret image " + NL +
    "       will be saved." + NL +
    "     - " + K.getDescription() + ": the minimum number of shadows to recover the " + NL +
    "       secret image, in a (k, n)-threshold scheme." + NL +
    "     - " + IMAGES_DIR.getDescription() + ": OPTIONAL: the directory of the shadow  " + NL +
    "       images containing the secret. If not specified, the program " + NL +
    "       will look those images in the current working directory." + NL;

  @Override
  public void run() {
    System.out.println(HELP_TEXT);
  }
}