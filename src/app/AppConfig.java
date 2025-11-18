package app;

import controller.CompanyRepController;
import controller.LoginController;
import controller.StaffController;
import controller.StudentController;
import repository.*;
import view.*;

/**
 * application configuration and bootstrap class
 * responsible for creating repositories, controllers, views,
 * loading initial data, and launching the main UI
 */
public class AppConfig {

    /**
     * initialise and start the application by g etting required dependencies and invoking main menu view
     */
    public void start() {

        UserRepository userRepo = new InMemoryUserRepository();
        InternshipRepository internshipRepo = new InMemoryInternshipRepository();
        InternshipAppRepository appRepo = new InMemoryInternshipAppRepository();
        CompanyRepository companyRepo = new InMemoryCompanyRepository();

        // Load initial sample users into memory storage
        DataLoader.loadInitialUsers(userRepo);


        LoginController loginController = new LoginController(userRepo);
        StudentController studentController = new StudentController(internshipRepo, appRepo, userRepo);
        CompanyRepController repController = new CompanyRepController(internshipRepo, appRepo, userRepo, companyRepo);
        StaffController staffController = new StaffController(userRepo, internshipRepo, appRepo);


        // Each role receives an isolated browser instance to ensure filter and state separation

        InternshipBrowserView studentBrowserView = new InternshipBrowserView(
                InternshipBrowserView.Role.STUDENT,
                studentController,
                repController,
                staffController
        );

        InternshipBrowserView repBrowserView = new InternshipBrowserView(
                InternshipBrowserView.Role.REP,
                studentController,
                repController,
                staffController
        );

        InternshipBrowserView staffBrowserView = new InternshipBrowserView(
                InternshipBrowserView.Role.STAFF,
                studentController,
                repController,
                staffController
        );

        // Dedicated login view
        LoginView loginView = new LoginView(loginController);

        // Dedicated role-specific menu views
        StudentMenuView studentMenuView = new StudentMenuView(studentController, studentBrowserView);
        CompanyRepMenuView companyRepMenuView = new CompanyRepMenuView(repController, repBrowserView);
        StaffMenuView staffMenuView = new StaffMenuView(staffController, staffBrowserView);


        MainMenuView mainMenuView = new MainMenuView(
                loginView,
                loginController,
                studentMenuView,
                companyRepMenuView,
                staffMenuView
        );

        mainMenuView.start();
    }
}
