package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import controller.CompanyRepController;
import model.CompanyRepresentative;
import model.Internship;
import model.InternshipApplication;
import model.Student;

public class CompanyRepMenuView {
    private final CompanyRepController companyRepController;
    private final InternshipBrowserView browserView;
    private static final Scanner sc = new Scanner(System.in);
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CompanyRepMenuView(CompanyRepController companyRepController, InternshipBrowserView browserView) {
        this.companyRepController = companyRepController;
        this.browserView = browserView;
    }

    public void displayCompanyRepMenu(CompanyRepresentative rep) {
        while (true) {
            System.out.println("\n========= Compnay Representative Menu =========");
            System.out.println("1. Create internship opportunity");
            System.out.println("2. View / Filter my internships");
            System.out.println("3. Edit internship");
            System.out.println("4. Delete internship");
            System.out.println("5. Toggle internship visibility");
            System.out.println("6. View applications");
            System.out.println("7. Approve / Reject applications");
            System.out.println("8. Change password");
            System.out.println("9. Logout");

            int choice = ConsoleUtil.readInt("Choose: ", 1, 9);

            switch (choice) {
                case 1 -> createInternship(rep);
                case 2 -> browserView.show(rep);
                case 3 -> editInternship(rep);
                case 4 -> deleteInternship(rep);
                case 5 -> toggleInternshipVisibility(rep);
                case 6 -> viewApplications(rep);
                case 7 -> approveRejectInternshipApplication(rep);
                case 8 -> changePassword(rep);
                case 9 -> { return; }
            }
        }
    }

    private void createInternship(CompanyRepresentative rep) {
        System.out.println("\n--- Create Internship Opportunity ---");

        System.out.print("Enter Internship title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter Internship description: ");
        String desc = sc.nextLine().trim();

        System.out.print("Enter Internship preferred major: ");
        String major = sc.nextLine().trim();

        System.out.println("Internship Levels:" +
                            "\n1. Basic" +
                            "\n2. Intermediate" +
                            "\n3. Advanced");
        int level = ConsoleUtil.readInt("Enter Internship level (1, 2, 3): ", 1, 3);
        Internship.Level internshipLevel = Internship.Level.values()[level - 1];


        LocalDate openDate = null;
        LocalDate closeDate = null;

        // ---- Ask for Opening Date ----
        while (openDate == null) {
            System.out.print("Enter Internship Application opening date (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                openDate = LocalDate.parse(input, fmt);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format or non-existent date. Please try again.");
            }
        }

        // ---- Ask for Closing Date ----
        while (closeDate == null) {
            System.out.print("Enter Internship Application closing date (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                LocalDate parsed = LocalDate.parse(input, fmt);
                if (parsed.isBefore(openDate)) {
                    System.out.println("Closing date cannot be earlier than opening date. Please try again.");
                } else {
                    closeDate = parsed;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format or non-existent date. Please try again.");
            }
        }


        int slots = ConsoleUtil.readInt("Enter number of slots available for Internship (1-10): ", 1, 10);

        Internship internship = companyRepController.createInternship(title, desc, internshipLevel, major, openDate, closeDate, rep, slots, false);

        System.out.println("Internship created with ID: " + internship.getId() + " (PENDING APPROVAL)");
        browserView.printInternshipDetails(internship, false);
    }

    private List<Internship> getInternshipList(CompanyRepresentative rep) {
        List<Internship> internshipList = companyRepController.getInternshipsByCompanyRep(rep);

        if (internshipList.isEmpty()) {
            System.out.println("You have not created any internships yet.");
            return internshipList;
        }

        System.out.println("\n==================== Your Internship Listings ====================");
        System.out.printf("%-4s %-30s %-28s %-12s %-18s %-18s %-12s %-12s %-12s %-10s %-8s%n",
                "No.", "Title", "Major", "Level", "Company", "Representative",
                "Open Date", "Close Date", "Status", "Visibility", "Slots");

        for (int i = 0; i < internshipList.size(); i++) {
            Internship in = internshipList.get(i);
            System.out.printf("%-4d %-30s %-28s %-12s %-18s %-18s %-12s %-12s %-12s %-10s %-8s%n",
                    (i + 1),
                    in.getTitle(),
                    in.getMajor(),
                    in.getLevel(),
                    in.getCompanyName(),
                    in.getCr().getName(),
                    in.getStartDate(),
                    in.getEndDate(),
                    in.getStatus(),
                    (in.getVisibility() ? "Visible" : "Hidden"),
                    (in.getConfirmedSlots() + "/" + in.getMaxSlots())
            );
        }

        return internshipList;
    }

 

    // private void viewMyInternships(CompanyRepresentative rep) {
    //     getInternshipList(rep);
    // }

    private void toggleInternshipVisibility(CompanyRepresentative rep) {
        List<Internship> internshipList = getInternshipList(rep);
        if (internshipList.isEmpty())
            return;
        
        int index = ConsoleUtil.readInt("Select internship index, 0 to cancel: ", 0, internshipList.size());
        if (index == 0)
            return;
        
        Internship selectedInternship = internshipList.get(index - 1);
        boolean newVisibility = !selectedInternship.getVisibility();
        companyRepController.toggleVisibility(selectedInternship, newVisibility);

        System.out.println("Internship visibility toggled to: " + newVisibility);
    }

    private void viewApplications(CompanyRepresentative rep) {
        List<Internship> internshipList = getInternshipList(rep);
        if (internshipList.isEmpty())
            return;
        
        int index = ConsoleUtil.readInt("Select internship index, 0 to cancel: ", 0, internshipList.size());
        if (index == 0)
            return;
        
        Internship selectedInternship = internshipList.get(index - 1);
        List<InternshipApplication> applications = companyRepController.getInternshipApplications(selectedInternship.getId());

        if (applications.isEmpty()) {
            System.out.println("No applications for this internship.");
            return;
        }

        printApplications(applications);
    }

    private void approveRejectInternshipApplication(CompanyRepresentative rep) {
        List<Internship> internshipList = getInternshipList(rep);
        if (internshipList.isEmpty())
            return;
        
        int index = ConsoleUtil.readInt("Select internship index, 0 to cancel: ", 0, internshipList.size());
        if (index == 0)
            return;
        
        Internship selectedInternship = internshipList.get(index - 1);
        List<InternshipApplication> applications = companyRepController.getInternshipApplications(selectedInternship.getId()).stream()
            .filter(a -> a.getStatus() == InternshipApplication.Status.PENDING).toList();
        
        if (applications.isEmpty()) {
            System.out.println("No pending applications.");
            return;
        }

        printApplications(applications);

        int applicationIndex = ConsoleUtil.readInt("Select application index, 0 to cancel: ", 0, applications.size());
        if (applicationIndex == 0)
            return;
        
        InternshipApplication selectedApplication = applications.get(applicationIndex - 1);

        System.out.println("1 = Approve, 2 = Reject");
        int decision = ConsoleUtil.readInt("Choose: ", 1, 2);

        try {
            if (decision == 1) {
                companyRepController.approveApplication(selectedApplication, selectedInternship);
                System.out.println("Application approved.");
            } else {
                companyRepController.rejectApplication(selectedApplication);
                System.out.println("Application rejected.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void changePassword(CompanyRepresentative rep) {
        System.out.print("Please enter new password: ");
        String newPw = sc.nextLine().trim();
        boolean pwChanged = rep.changePassword(newPw);
        System.out.println(pwChanged ? "Password changed." : "You must be logged in to change your password.");
    }

    // private void printApplications(List<InternshipApplication> applications) {
    //     System.out.printf("%-4s %-15s %-15s %-15s %-12s%n",
    //             "No.", "Application ID", "Student ID", "Internship ID", "Status");

    //     for (int i = 0; i < applications.size(); i++) {
    //         InternshipApplication a = applications.get(i);
    //         System.out.printf("%-4d %-15s %-15s %-15s %-12s%n",
    //                 (i + 1),
    //                 a.getId(),
    //                 a.getStudentId(),
    //                 a.getInternshipId(),
    //                 a.getStatus());
    //     }
    // }

    private void printApplications(List<InternshipApplication> applications) {
        System.out.printf("%-4s %-35s %-18s %-15s %-18s %-12s%n",
                "No.", "Internship Title", "Company", "Student ID", "Student Name", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);

            // Get internship
            Internship internship = companyRepController.getInternshipById(a.getInternshipId());
            String title   = (internship != null) ? internship.getTitle() : "(Deleted)";
            String company = (internship != null) ? internship.getCompanyName() : "-";

            // Get student
            Student student = companyRepController.getStudentById(a.getStudentId());
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

    public void registerCompanyRep() {
        System.out.println("\n===== Company Representative Registration =====");

        // Name
        String name;
        do {
            System.out.print("Enter your full name: ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) System.out.println("Name cannot be empty.");
        } while (name.isEmpty());

        // Email (used as login ID)
        String email;
        do {
            System.out.print("Enter your company email (used as login ID): ");
            email = sc.nextLine().trim();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Try again (example: user@company.com).");
                email = ""; // reset
            }
        } while (email.isEmpty());

        // Company
        String company;
        do {
            System.out.print("Enter company name: ");
            company = sc.nextLine().trim();
            if (company.isEmpty()) System.out.println("Company cannot be empty.");
        } while (company.isEmpty());

        // Department
        String dept;
        do {
            System.out.print("Enter department: ");
            dept = sc.nextLine().trim();
            if (dept.isEmpty()) System.out.println("Department cannot be empty.");
        } while (dept.isEmpty());

        // Position
        String position;
        do {
            System.out.print("Enter position: ");
            position = sc.nextLine().trim();
            if (position.isEmpty()) System.out.println("Position cannot be empty.");
        } while (position.isEmpty());

        CompanyRepresentative rep = companyRepController.register(name, email, company, dept, position);

        System.out.println("\nRegistration submitted successfully!");
        System.out.println("Your account is pending approval by staff.");
    }
    
    private boolean isValidEmail(String email) {
        // Basic RFC-compliant pattern for console applications
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void editInternship(CompanyRepresentative rep) {
        while (true) {
            List<Internship> list = getInternshipList(rep);
            if (list.isEmpty()) return;

            int index = ConsoleUtil.readInt("Select internship index, 0 to cancel: ", 0, list.size());
            if (index == 0) return;

            Internship original = list.get(index - 1);

            System.out.println("\n--- Current Details ---");
            browserView.printInternshipDetails(original, false);

            // ─────── Step 1: Gather new data ───────
            System.out.println("\n--- Enter New Values (blank = unchanged) ---");

            System.out.print("New title: ");
            String title = sc.nextLine().trim();
            title = title.isEmpty() ? original.getTitle() : title;

            System.out.print("New description: ");
            String desc = sc.nextLine().trim();
            desc = desc.isEmpty() ? original.getDesc() : desc;

            System.out.print("New major: ");
            String major = sc.nextLine().trim();
            major = major.isEmpty() ? original.getMajor() : major;

            System.out.print("Change level? (1=Basic,2=Intermediate,3=Advanced,0=Skip): ");
            int lvl = ConsoleUtil.readInt("", 0, 3);
            Internship.Level level = (lvl == 0) ? original.getLevel() : Internship.Level.values()[lvl - 1];

            LocalDate newStart = promptDate("New start date (yyyy-MM-dd, blank = unchanged): ");
            LocalDate newEnd   = promptDate("New end date (yyyy-MM-dd, blank = unchanged): ");

            LocalDate start = (newStart == null) ? original.getStartDate() : newStart;
            LocalDate end   = (newEnd == null) ? original.getEndDate() : newEnd;

            int newSlots = ConsoleUtil.readInt("New slot count (1-10, 0 = unchanged): ", 0, 10);
            int slots = (newSlots == 0) ? original.getMaxSlots() : newSlots;

            // ─────── Step 2: Display preview ───────
            System.out.println("\n=========== PREVIEW CHANGES ===========");
            System.out.printf("%-20s | %-20s -> %-20s%n", "Title", original.getTitle(), title);
            System.out.printf("%-20s | %-20s -> %-20s%n", "Description", truncate(original.getDesc()), truncate(desc));
            System.out.printf("%-20s | %-20s -> %-20s%n", "Major", original.getMajor(), major);
            System.out.printf("%-20s | %-20s -> %-20s%n", "Level", original.getLevel(), level);
            System.out.printf("%-20s | %-20s -> %-20s%n", "Start Date", original.getStartDate(), start);
            System.out.printf("%-20s | %-20s -> %-20s%n", "End Date", original.getEndDate(), end);
            System.out.printf("%-20s | %-20d -> %-20d%n", "Slots", original.getMaxSlots(), slots);
            System.out.println("========================================");

            // ─────── Step 3: Confirm or redo ───────
            if (confirmAction("Apply these changes? (Y/N): ")) {
                try {
                    companyRepController.editInternship(original, title, desc, level, major, start, end, slots);
                    System.out.println("Internship updated successfully!");
                    return;
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            } else {
                System.out.println("Re-entering edit menu...");
                // Loop continues - no return needed
            }
        }
    }


    private void deleteInternship(CompanyRepresentative rep) {
        while (true) {
            List<Internship> list = getInternshipList(rep);
            if (list.isEmpty()) return;

            int index = ConsoleUtil.readInt("Select internship index, 0 to cancel: ", 0, list.size());
            if (index == 0) return;

            Internship i = list.get(index - 1);
            browserView.printInternshipDetails(i, false);

            if (confirmAction("Are you sure you want to DELETE this internship? (Y/N): ")) {
                try {
                    companyRepController.deleteInternship(i);
                    System.out.println("Internship deleted successfully!");
                    return;
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            } else {
                System.out.println("Returning to internship selection...");
                // Continue loop
            }
        }
    }

    /**
     * prompt for a date from user input, allows blank to skip
     * @param msg message to display before input
     * @return parsed LocalDate, or null if blank input
     */
    private LocalDate promptDate(String msg) {
        while (true) {
            System.out.print(msg);
            String input = sc.nextLine().trim();

            // allow skipping
            if (input.isEmpty()) {
                return null;
            }

            // validate date format and real date
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Use yyyy-MM-dd.");
            }
        }
    }
    private String truncate(String s) {
        return (s.length() > 18) ? s.substring(0, 15) + "..." : s;
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
