package app;

import controller.CompanyRepController;
import controller.LoginController;
import controller.StaffController;
import controller.StudentController;
import repository.CompanyRepository;
import repository.InMemoryCompanyRepository;
import repository.InMemoryInternshipAppRepository;
import repository.InMemoryInternshipRepository;
import repository.InMemoryUserRepository;
import repository.InternshipAppRepository;
import repository.InternshipRepository;
import repository.UserRepository;
import view.CompanyRepMenuView;
import view.InternshipBrowserView;
import view.LoginView;
import view.MainMenuView;
import view.StaffMenuView;
import view.StudentMenuView;

public class AppConfig {
    public void start() {
        // Instantiate repositories
        UserRepository userRepo = new InMemoryUserRepository();
        InternshipRepository internshipRepo = new InMemoryInternshipRepository();
        InternshipAppRepository appRepo = new InMemoryInternshipAppRepository();
        CompanyRepository companyRepo = new InMemoryCompanyRepository();

        // Load initial data
        DataLoader.loadInitialUsers(userRepo);

        // Instantiate controllers
        LoginController loginController = new LoginController(userRepo);
        StudentController studentController = new StudentController(internshipRepo, appRepo);
        CompanyRepController repController = new CompanyRepController(internshipRepo, appRepo, userRepo, companyRepo);
        StaffController staffController = new StaffController(userRepo, internshipRepo, appRepo);

        // Create dedicated browser views for each role
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


        LoginView loginView = new LoginView(loginController);

        StudentMenuView studentMenuView = new StudentMenuView(studentController, studentBrowserView);
        CompanyRepMenuView companyRepMenuView = new CompanyRepMenuView(repController, repBrowserView);
        StaffMenuView staffMenuView = new StaffMenuView(staffController, staffBrowserView);


        // Instantiate primary view
        MainMenuView mainMenuView = new MainMenuView(
                loginView,
                loginController,
                studentMenuView,
                companyRepMenuView,
                staffMenuView
        );



        mainMenuView.start();  // launches UI
    }
}
