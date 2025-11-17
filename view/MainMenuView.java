package view;

import java.util.Scanner;

import controller.LoginController;
import model.*;

public class MainMenuView {

    private final LoginView loginView;
    private final LoginController loginController;
    private final StudentMenuView studentMenuView;
    private final CompanyRepMenuView companyRepMenuView;
    private final StaffMenuView staffMenuView;

    private final Scanner sc = new Scanner(System.in);

    public MainMenuView(LoginView loginView,
                        LoginController loginController,
                        StudentMenuView studentMenuView,
                        CompanyRepMenuView companyRepMenuView,
                        StaffMenuView staffMenuView) {
        this.loginView = loginView;
        this.loginController = loginController;
        this.studentMenuView = studentMenuView;
        this.companyRepMenuView = companyRepMenuView;
        this.staffMenuView = staffMenuView;
    }

    public void start() {
        while (true) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Company Representative");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> companyRepMenuView.registerCompanyRep();  // <-- new call
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void handleLogin() {
        User user = loginView.showLoginScreen();
        if (user == null) return;

        if (user instanceof Student s) {
            studentMenuView.displayStudentMenu(s);
        } else if (user instanceof CompanyRepresentative rep) {
            if (!rep.isApproved()) {
                System.out.println("Your registration is pending approval by Staff.");
            } else {
                companyRepMenuView.displayCompanyRepMenu(rep);
            }
        } else if (user instanceof Staff staff) {
            staffMenuView.displayStaffMenu(staff);
        }
        loginController.logout(user);
    }

}
