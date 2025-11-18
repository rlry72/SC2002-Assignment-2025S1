package view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import controller.StudentController;
import model.*;

/**
 * view class responsible for handling student-facing system interactions
 * presents menu operations relating to internship browsing, application,
 * acceptance, withdrawal and account maintenance
 */
public class StudentMenuView {

    /** controller containing student-related business logic */
    private final StudentController studentController;

    /** shared browser interface used to view and filter internships */
    private final InternshipBrowserView browserView;

    /** formatter for consistent internship date display */
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** console scanner for user text input */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * construct student menu view with the required student controller and browser view
     * @param studentController controller managing student internship operations
     * @param browserView shared browsing utility for internship viewing and filtering
     */
    public StudentMenuView(StudentController studentController, InternshipBrowserView browserView) {
        this.studentController = studentController;
        this.browserView = browserView;
    }

    /**
     * display the main student menu and execute commands until logout occurs
     * @param student currently logged-in student user
     */
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

    /**
     * open the internship browser view for this student
     * allowing interactive filtering and selection
     * @param s student requesting to view internships
     */
    private void viewEligible(Student s) {
        browserView.show(s);
    }

    /**
     * display all internship applications submitted by the student
     * @param s student requesting the list
     */
    private void viewApplications(Student s) {
        List<InternshipApplication> applications = studentController.getInternshipApplications(s);
        if (applications.isEmpty()) {
            System.out.println("No Internship Applications to display.");
            return;
        }
        printApplications(applications);
    }

    /**
     * allow a student to select and apply for an internship
     * enforces system rules and re-prompts for new selections after error
     * @param s student applying for internship
     */
    private void apply(Student s) {
        while (true) {
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
            browserView.printInternshipDetails(selected, true);

            if (!confirmAction("Confirm APPLY for this internship? (Y/N): ")) {
                System.out.println("Cancelled. Returning to internship list...\n");
                continue;
            }

            try {
                studentController.applyInternship(s, selected);
                System.out.println("Application submitted successfully.");
                return;
            } catch (Exception e) {
                System.out.println("Unable to apply: " + e.getMessage());
            }
        }
    }

    /**
     * allow a student to accept a successful internship offer
     * and automatically withdraw all other applications
     * @param s student attempting to accept an internship offer
     */
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
                continue;

            try {
                studentController.acceptInternship(s, selectedApp, internship);
                System.out.println("Internship accepted. All other applications withdrawn.");
                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * allow a student to request withdrawal from a submitted application
     * request may require staff approval depending on status
     * @param s student requesting withdrawal
     */
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
                continue;

            try {
                studentController.withdrawFromInternship(s, app);
                System.out.println("✔ Withdrawal request sent to staff.");
                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * allow currently logged-in student to change their own password
     * @param s student requesting password change
     */
    private void changePassword(Student s) {
        System.out.print("Please enter new password: ");
        String newPw = sc.nextLine().trim();
        boolean pwChanged = s.changePassword(newPw);
        System.out.println(pwChanged ? "Password changed." : "You must be logged in to change your password.");
    }

    /**
     * print a formatted list of internships for console output
     * @param list list of internships to display
     */
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

    /**
     * print formatted internship application records for console display
     * @param applications list of applications to display
     */
    private void printApplications(List<InternshipApplication> applications) {
        System.out.printf("%-4s %-35s %-18s %-15s %-18s %-12s%n",
                "No.", "Internship Title", "Company", "Student ID", "Student Name", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);

            Internship internship = studentController.getInternshipById(a.getInternshipId());
            String title   = (internship != null) ? internship.getTitle() : "(Deleted)";
            String company = (internship != null) ? internship.getCompanyName() : "-";

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

    /**
     * ask user to confirm a yes/no action
     * @param message confirmation prompt message
     * @return true if user confirms, false otherwise
     */
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
