package view;

import java.util.Scanner;
import controller.LoginController;
import model.User;

/**
 * view class responsible for handling user login interaction
 * displays login form, collects user credentials,
 * and uses LoginController for authentication
 */
public class LoginView {

    /** controller used for validating login credentials */
    private final LoginController loginController;

    /** scanner used for console-based credential input */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * construct login view with required controller dependency
     * @param loginController controller responsible for login validation
     */
    public LoginView(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     * display login screen, request input, and authenticate user
     * prints login result feedback to console
     *
     * @return authenticated User instance if login successful, otherwise null
     */
    public User showLoginScreen() {
        System.out.println("=====================================");
        System.out.println("       Internship Placement System   ");
        System.out.println("=====================================");

        System.out.print("Please enter your Login ID: ");
        String loginId = sc.nextLine().trim();

        System.out.print("Please enter your password: ");
        String password = sc.nextLine().trim();

        User user = loginController.login(loginId, password);

        if (user == null) {
            System.out.println("Login failed: invalid ID or password.");
        } else {
            System.out.printf("Welcome, %s (%s)%n", user.getName(), user.getClass().getSimpleName());
        }

        return user;
    }
}
