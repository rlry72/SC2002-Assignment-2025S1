package view;

import java.util.Scanner;
import controller.LoginController;
import model.*;

/**
 * view class responsible for rendering the initial system menu
 * handles main user navigation, login requests, and company representative registration
 */
public class MainMenuView {

    /** view used for displaying and processing user login */
    private final LoginView loginView;

    /** controller used for executing authentication and logout actions */
    private final LoginController loginController;

    /** view used for directing logged-in students to their respective menu */
    private final StudentMenuView studentMenuView;

    /** view used for directing logged-in company representatives to their respective menu */
    private final CompanyRepMenuView companyRepMenuView;

    /** view used for directing logged-in staff members to their respective menu */
    private final StaffMenuView staffMenuView;

    /** scanner used for console menu input */
    private final Scanner sc = new Scanner(System.in);

    /**
     * construct main menu view with required view and controller dependencies
     *
     * @param loginView view responsible for user login prompts
     * @param loginController controller responsible for validating login actions
     * @param studentMenuView view for student role menu navigation
     * @param companyRepMenuView view for company representative role navigation
     * @param staffMenuView view for staff role navigation
     */
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

    /**
     * display the main system menu and manage user high-level selections
     * options include login, company representative registration, or system exit
     * loops indefinitely until user selects exit
     */
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
                case "2" -> companyRepMenuView.registerCompanyRep();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * authenticate user, determine assigned role, and redirect to appropriate menu
     * automatically logs user out upon returning from submenu views
     *
     * @implNote
     *  • unapproved company representatives may view notice but cannot access menu  
     *  • valid role types: Student, CompanyRepresentative, Staff
     */
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
