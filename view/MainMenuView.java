package view;

import controller.LoginController;
import model.*;

public class MainMenuView {

    private final LoginView loginView;
    private final LoginController loginController;
    private final StudentMenuView studentMenuView;
    private final CompanyRepMenuView companyRepMenuView;
    private final StaffMenuView staffMenuView;

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
            User user = loginView.showLoginScreen();
            if (user == null) continue;

            if (user instanceof Student s) {
                studentMenuView.displayStudentMenu(s);
            } else if (user instanceof CompanyRepresentative rep) {
                companyRepMenuView.displayCompanyRepMenu(rep);
            } else if (user instanceof Staff staff) {
                staffMenuView.displayStaffMenu(staff);
            } else {
                System.out.println("Unknown user type.");
            }

            loginController.logout(user);
        }
    }
}
