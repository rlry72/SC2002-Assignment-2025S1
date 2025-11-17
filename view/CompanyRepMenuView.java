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
            System.out.println("\n====== Company Representative Menu ======");
            System.out.println("1. Create internship opportunity");
            System.out.println("2. View / Filter my internships");
            System.out.println("3. Toggle internship visibility");
            System.out.println("4. View applications for internship");
            System.out.println("5. Approve or Reject applications");
            System.out.println("6. Change password");
            System.out.println("7. Logout");

            int choice = ConsoleUtil.readInt("Choose: ", 1, 7);

            switch (choice) {
                case 1 -> createInternship(rep);
                case 2 -> browserView.show(rep);
                case 3 -> toggleInternshipVisibility(rep);
                case 4 -> viewApplications(rep);
                case 5 -> approveRejectInternshipApplication(rep);
                case 6 -> changePassword(rep);
                case 7 -> { return; }
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
    }

    private List<Internship> getInternshipList(CompanyRepresentative rep) {
        List<Internship> internshipList = companyRepController.getInternshipsByCompanyRep(rep);

        if (internshipList.isEmpty()) {
            System.out.println("You have not created any internships yet.");
            return internshipList;
        }

        System.out.printf("%-4s %-15s %-22s %-10s %-12s %-12s %-10s%n",
                "No.", "ID", "Title", "Level", "Status", "Visibility", "Slots");
        
        for (int i = 0; i < internshipList.size(); i++) {
            Internship internship = internshipList.get(i);
            System.out.printf("%-4d %-15s %-22s %-10s %-12s %-10s %-10s%n",
                    (i + 1),
                    internship.getId(),
                    internship.getTitle(),
                    internship.getLevel(),
                    internship.getStatus(),
                    internship.getVisibility(),
                    internship.getConfirmedSlots() + "/" + internship.getMaxSlots());
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

    private void printApplications(List<InternshipApplication> applications) {
        System.out.printf("%-4s %-15s %-15s %-15s %-12s%n",
                "No.", "Application ID", "Student ID", "Internship ID", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);
            System.out.printf("%-4d %-15s %-15s %-15s %-12s%n",
                    (i + 1),
                    a.getId(),
                    a.getStudentId(),
                    a.getInternshipId(),
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

}
