package view;

import java.util.Scanner;

/**
 * utility class providing reusable console input and pause functionality
 * designed for menu-driven console applications where valid numeric input is required
 * this class cannot be instantiated
 */
public class ConsoleUtil {

    /** shared scanner instance for all console input operations */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * read and validate an integer input within a specified inclusive range
     * method continues prompting until valid input is entered
     *
     * @param prompt message shown to user before input
     * @param min minimum acceptable value (inclusive)
     * @param max maximum acceptable value (inclusive)
     * @return validated integer within the given range
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * pause console execution until user presses Enter
     * used to improve readability of multi-step menus
     */
    public static void pause() {
        System.out.print("Press Enter to continue...");
        SCANNER.nextLine();
    }
}
