package view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import controller.StudentController;
import model.*;

public class StudentMenuView {
    private final StudentController studentController;
    private final InternshipBrowserView browserView;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Scanner sc = new Scanner(System.in);

    public StudentMenuView(StudentController studentController, InternshipBrowserView browserView) {
        this.studentController = studentController;
        this.browserView = browserView;
    }

     public void displayStudentMenu(Student student) {
        while (true) {
            System.out.println("\n========== Student Menu ==========");
            System.out.println("1. View eligible internships");
            System.out.println("2. View my applications");
            System.out.println("3. Apply for internship");
            System.out.println("4. Accept successful internship");
            System.out.println("5. Request withdrawal");
            System.out.println("6. Change password");
            System.out.println("7. Logout");

            int choice = ConsoleUtil.readInt("Choose: ", 1, 7);

            switch (choice) {
                case 1 -> viewEligible(student);
                case 2 -> viewApplications(student);
                case 3 -> apply(student);
                case 4 -> accept(student);
                case 5 -> withdraw(student);
                case 6 -> changePassword(student);
                case 7 -> { return; }
            }
        }
    }

    private void viewEligible(Student s) {
        // List<Internship> internshipList = studentController.getEligibleInternships(s);
        // if (internshipList.isEmpty()) {
        //     System.out.println("No eligible internships!");
        //     return;
        // }

        // printInternships(internshipList);
        browserView.show(s);
    }

    private void viewApplications(Student s) {
        List<InternshipApplication> applications = studentController.getInternshipApplications(s);
        if (applications.isEmpty()) {
            System.out.println("No Internship Applications to display.");
            return;
        }
        printApplications(applications);
    }

    private void apply(Student s) {

        browserView.show(s); // let student view & update filters
        List<Internship> internshipList = browserView.getLastResults(s);

        if (internshipList == null || internshipList.isEmpty()) {
            System.out.println("No internships are currently available.");
            return;
        }

        printInternships(internshipList);

        int choice = ConsoleUtil.readInt("Select index, 0 to cancel: ", 0, internshipList.size());
        if (choice == 0) return;

        Internship selected = internshipList.get(choice - 1);

        try {
            studentController.applyInternship(s, selected);
            System.out.println("Application submitted.");
        } catch (IllegalStateException e) {
            System.out.println("Unable to apply: " + e.getMessage());
        }
    }


    // private void apply(Student s) {
    //     List<Internship> internshipList = studentController.getEligibleInternships(s);
    //     if (internshipList.isEmpty()) {
    //         System.out.println("No internships are currently available.");
    //         return;
    //     }

    //     printInternships(internshipList);

    //     int choice = ConsoleUtil.readInt("Select index, 0 to cancel: ", 0, internshipList.size());
    //     if (choice == 0)
    //         return;
        
    //     Internship selectedInternship = internshipList.get(choice - 1);
    //     try {
    //         studentController.applyInternship(s, selectedInternship);
    //         System.out.println("Internship Application submitted.");
    //     } catch (IllegalStateException e) {
    //         System.out.println("Unable to apply: " + e.getMessage());
    //     }
    // }

    private void accept(Student s) {
        List<InternshipApplication> applications = studentController.getInternshipApplications(s).stream()
            .filter(a -> a.getStatus() == InternshipApplication.Status.SUCCESSFUL && !a.studentAccepted()).toList();
        
        if (applications.isEmpty()) {
            System.out.println("No successful Internship Applications available.");
            return;
        }
        printApplications(applications);

        int choice = ConsoleUtil.readInt("Select index, 0 to cancel: ", 0, applications.size());

        if (choice == 0)
            return;
        
        InternshipApplication application = applications.get(choice - 1);
        Internship internship = studentController.getInternshipById(application.getInternshipId());

        try {
            studentController.acceptInternship(s, application, internship);
            System.out.println("Successfully accepted internship.");
        } catch (IllegalStateException e) {
            System.out.println("Error accepting internship: " + e.getMessage());
        }
    }

    private void withdraw(Student s) {
        List<InternshipApplication> applications = studentController.getInternshipApplications(s);
        if (applications.isEmpty()) {
            System.out.println("No Internship Applications available.");
            return;
        } 
        printApplications(applications);
        
        int choice = ConsoleUtil.readInt("Select index, 0 to cancel: ", 0, applications.size());

        if (choice == 0)
            return;

        InternshipApplication application = applications.get(choice - 1);

        try {
            studentController.withdrawFromInternship(s, application);
            System.out.println("Withdrawal request sent.");
        } catch (IllegalStateException e) {
            System.out.println("Error sending withdrawal request: " + e.getMessage());
        }
    }

    private void changePassword(Student s) {
        System.out.print("Please enter new password: ");
        String newPw = sc.nextLine().trim();
        boolean pwChanged = s.changePassword(newPw);
        System.out.println(pwChanged ? "Password changed." : "You must be logged in to change your password.");
    }


    private void printInternships(List<Internship> list) {
        System.out.printf("%-4s %-20s %-15s %-10s %-12s %-12s%n",
                "No.","Title","Company","Level","Open","Close");
        for (int i=0; i<list.size(); i++) {
            Internship in = list.get(i);
            System.out.printf("%-4d %-20s %-15s %-10s %-12s %-12s%n",
                    i+1,
                    in.getTitle(),
                    in.getCompanyName(),
                    in.getLevel(),
                    in.getStartDate().format(fmt),
                    in.getEndDate().format(fmt));
        }
    }

    private void printApplications(List<InternshipApplication> applications) {
        System.out.printf("%-4s %-15s %-15s %-15s %-10s%n",
                "No.","App ID","Internship ID","Student ID","Status");
        for (int i=0;i<applications.size();i++) {
            InternshipApplication a = applications.get(i);
            System.out.printf("%-4d %-15s %-15s %-15s %-10s%n",
                    i+1,a.getId(),a.getInternshipId(),a.getStudentId(),a.getStatus());
        }
    }
}
