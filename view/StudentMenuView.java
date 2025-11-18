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

        while (true) {
            // Display internship list only — no browser menu
            browserView.showFilteredList(s);
            List<Internship> internshipList = browserView.getLastResults(s);

            if (internshipList == null || internshipList.isEmpty()) {
                System.out.println("No internships are currently available.");
                return;
            }

            int choice = ConsoleUtil.readInt("Select internship index, 0 = cancel: ", 0, internshipList.size());
            if (choice == 0) {
                System.out.println("Returning to menu...");
                return;
            }

            Internship selected = internshipList.get(choice - 1);

            // Show details immediately
            browserView.printInternshipDetails(selected, true);

            if (!confirmAction("Confirm APPLY for this internship? (Y/N): ")) {
                System.out.println("Cancelled → Returning to internship list...\n");
                continue; // back to list, NOT back to browser menu
            }

            try {
                studentController.applyInternship(s, selected);
                System.out.println("Application submitted successfully.");
                return; // done
            } catch (Exception e) {
                System.out.println("Unable to apply: " + e.getMessage());
            }
        }
    }



    private void accept(Student s) {

        while (true) {
            List<InternshipApplication> applications = studentController.getInternshipApplications(s).stream()
                    .filter(a -> a.getStatus() == InternshipApplication.Status.SUCCESSFUL && !a.studentAccepted())
                    .toList();

            if (applications.isEmpty()) {
                System.out.println("No successful internship offers to accept.");
                return;
            }

            printApplications(applications);

            int choice = ConsoleUtil.readInt("Select index, 0 = cancel: ", 0, applications.size());
            if (choice == 0) return;

            InternshipApplication selectedApp = applications.get(choice - 1);
            Internship internship = studentController.getInternshipById(selectedApp.getInternshipId());

            browserView.printInternshipDetails(internship, true);

            if (!confirmAction("Confirm ACCEPT this internship offer? (Y/N): "))
                continue; // restart selection list

            try {
                studentController.acceptInternship(s, selectedApp, internship);
                System.out.println("Internship accepted. All other applications withdrawn.");
                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }



    private void withdraw(Student s) {

        while (true) {
            List<InternshipApplication> applications = studentController.getInternshipApplications(s);
            if (applications.isEmpty()) {
                System.out.println("You have no internship applications.");
                return;
            }

            printApplications(applications);

            int choice = ConsoleUtil.readInt("Select application to withdraw, 0 = cancel: ", 0, applications.size());
            if (choice == 0) return;

            InternshipApplication app = applications.get(choice - 1);
            Internship internship = studentController.getInternshipById(app.getInternshipId());

            browserView.printInternshipDetails(internship, true);

            if (!confirmAction("Confirm WITHDRAW request for this application? (Y/N): "))
                continue; // return to list

            try {
                studentController.withdrawFromInternship(s, app);
                System.out.println("✔ Withdrawal request sent to staff.");
                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
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
        System.out.printf("%-4s %-35s %-18s %-15s %-18s %-12s%n",
                "No.", "Internship Title", "Company", "Student ID", "Student Name", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);

            // Get internship
            Internship internship = studentController.getInternshipById(a.getInternshipId());
            String title   = (internship != null) ? internship.getTitle() : "(Deleted)";
            String company = (internship != null) ? internship.getCompanyName() : "-";

            // Get student
            Student student = studentController.getStudentById(a.getStudentId());
            String studentName = (student != null) ? student.getName() : "(Unknown)";

            System.out.printf("%-4d %-35s %-18s %-15s %-18s %-12s%n",
                    (i + 1),
                    title,
                    company,
                    a.getStudentId(),
                    studentName,
                    a.getStatus());
        }
    }

    private boolean confirmAction(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Please enter Y or N.");
        }
    }
}
