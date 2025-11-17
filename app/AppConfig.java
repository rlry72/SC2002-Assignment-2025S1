package app;

import controller.CompanyRepController;
import controller.LoginController;
import controller.StaffController;
import controller.StudentController;
import repository.InMemoryInternshipAppRepository;
import repository.InMemoryInternshipRepository;
import repository.InMemoryUserRepository;
import repository.InternshipAppRepository;
import repository.InternshipRepository;
import repository.UserRepository;
import view.CompanyRepMenuView;
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

        // Load initial data
        DataLoader.loadInitialUsers(userRepo);

        // Instantiate controllers
        LoginController loginController = new LoginController(userRepo);
        StudentController studentController = new StudentController(internshipRepo, appRepo);
        CompanyRepController repController = new CompanyRepController(internshipRepo, appRepo, userRepo);
        StaffController staffController = new StaffController(userRepo, internshipRepo, appRepo);

        // Instantiate primary view
        MainMenuView mainMenuView = new MainMenuView(
            new LoginView(loginController),
            loginController,
            new StudentMenuView(studentController),
            new CompanyRepMenuView(repController),
            new StaffMenuView(staffController)   // ← StaffReportView will be created here
        );


        mainMenuView.start();  // launches UI
    }
}
