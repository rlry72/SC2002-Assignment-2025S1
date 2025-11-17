package view;

import java.util.Scanner;

public class ConsoleUtil {
    private static final Scanner SCANNER = new Scanner(System.in);

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

    public static void pause() {
        System.out.print("Press Enter to continue...");
        SCANNER.nextLine();
    }
}
