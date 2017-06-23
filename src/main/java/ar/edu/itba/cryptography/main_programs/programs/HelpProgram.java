package ar.edu.itba.cryptography.main_programs.programs;

import static ar.edu.itba.cryptography.helpers.InputParam.*;

import ar.edu.itba.cryptography.interfaces.MainProgram;
import ar.edu.itba.cryptography.interfaces.MainProgramBuilder;

public class HelpProgram implements MainProgram {
  // System independent new line character
  private static final String NL = System.lineSeparator();
  private static final String HELP_TEXT =
      "Skyscraper Game Solver.\n" +
          "Arguments: \n" +
          "* -h : `help` that prints all available commands" + NL +
          "* " + D_METHOD.getName() + " " + D_SECRET.getName() + " " + D_K.getName() + " " +
          D_N.getName() + " " + D_IMAGES_DIR.getName() + NL +
          "     distributes a secret image into other images." + NL +
          "     - " + D_SECRET.getName() + ": the path to the secret image to be hidden." + NL +
          "     - " + D_K.getName() + ": the minimum number of shadows to recover the " + NL +
          "       secret image, in a (k, n)-threshold scheme." + NL +
          "     - " + D_N.getName() + ": OPTIONAL: the number of total shadows to be used " + NL +
          "       in a (k, n)-threshold scheme. If not specified, n will be the total " + NL +
          "       number of images in the specified directory." + NL +
          "     - " + D_IMAGES_DIR.getName() + ": OPTIONAL: the directory of the images " + NL +
          "       to be used as shadows. If not specified, the program will look those " + NL +
          "       images in the current working directory." + NL +
          "* " + R_METHOD.getName() + " " + R_SECRET.getName() + " " + R_K.getName() + " "
          + R_IMAGES_DIR.getName() + NL +
          "     retrieves a secret image from shadow images." + NL +
          "     - " + R_SECRET.getName() + ": the path where the revealed secret image " + NL +
          "       will be saved." + NL +
          "     - " + R_K.getName() + ": the minimum number of shadows to recover the " + NL +
          "       secret image, in a (k, n)-threshold scheme." + NL +
          "     - " + R_IMAGES_DIR.getName() + ": OPTIONAL: the directory of the shadow  " + NL +
          "       images containing the secret. If not specified, the program " + NL +
          "       will look those images in the current working directory." + NL;

  @Override
  public void run() {
    System.out.println(HELP_TEXT);
  }
}