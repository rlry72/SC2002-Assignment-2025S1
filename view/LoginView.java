package view;

import java.util.Scanner;

import controller.LoginController;
import model.User;

public class LoginView {
    private final LoginController loginController;
    private static final Scanner sc = new Scanner(System.in);

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
    }

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
