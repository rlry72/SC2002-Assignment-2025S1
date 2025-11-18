package view;

import java.util.List;
import java.util.Scanner;

import controller.StaffController;
import model.*;

public class StaffMenuView {
    private final StaffController staffController;
    private final InternshipBrowserView browserView;   // <-- Added
    private static final Scanner sc = new Scanner(System.in);

    public StaffMenuView(StaffController staffController,
                         InternshipBrowserView browserView) {
        this.staffController = staffController;
        this.browserView = browserView;
    }

    public void displayStaffMenu(Staff staff) {
        while (true) {
            System.out.println("\n========= Staff Menu =========");
            System.out.println("1. Approve / Reject company representatives");
            System.out.println("2. Approve / Reject internship postings");
            System.out.println("3. Approve / Reject withdrawal requests");
            System.out.println("4. View / Filter / Report internships");
            System.out.println("5. View ALL internship applications");
            System.out.println("6. Change password");
            System.out.println("7. Logout");

            int choice = ConsoleUtil.readInt("Choose: ", 1, 7);

            switch (choice) {
                case 1 -> manageCompanyReps();
                case 2 -> manageInternships();
                case 3 -> manageWithdrawals();
                case 4 -> browserView.show(staff);
                case 5 -> viewAllApplications();        // <--- NEW METHOD
                case 6 -> changePassword(staff);
                case 7 -> { return; }
            }
        }
    }

    private void manageCompanyReps() {
        List<CompanyRepresentative> repList = staffController.getPendingCompanyReps();
        if (repList.isEmpty()) {
            System.out.println("No pending Company Representatives.");
            return;
        }

        System.out.printf("%-4s %-15s %-20s %-20s%n",
                "No.", "ID", "Name", "Company");
        for (int i = 0; i < repList.size(); i++) {
            CompanyRepresentative rep = repList.get(i);
            System.out.printf("%-4d %-15s %-20s %-20s%n",
                    (i + 1),
                    rep.getUserId(),
                    rep.getName(),
                    rep.getCompany().getCompanyName());
        }

        int index = ConsoleUtil.readInt("Select index of Company Representative to manage, 0 to cancel: ", 0, repList.size());
        if (index == 0)
            return;

        CompanyRepresentative selectedRep = repList.get(index - 1);
        System.out.println("1 = Approve, 2 = Reject");
        int decision = ConsoleUtil.readInt("Choose: ", 1, 2);

        if (decision == 1) {
            staffController.approveCompanyRep(selectedRep);
            System.out.println("Approved Company Representative.");
        } else {
            staffController.rejectCompanyRep(selectedRep);
            System.out.println("Rejected Company Representative.");
        }
    }

    private void manageInternships() {
        List<Internship> internships = staffController.getPendingInternships();
        if (internships.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }

        System.out.println("\n================ Pending Internships ================");
        System.out.printf("%-4s %-25s %-20s %-10s %-30s %-12s %-12s %-15s%n",
                "No.", "Title", "Company", "Level", "Major", "Open", "Close", "Slots");

        for (int i = 0; i < internships.size(); i++) {
            Internship in = internships.get(i);
            System.out.printf("%-4d %-25s %-20s %-10s %-30s %-12s %-12s %-15s%n",
                    (i + 1),
                    crop(in.getTitle(), 25),
                    crop(in.getCompanyName(), 20),
                    in.getLevel(),
                    crop(in.getMajor(), 30),
                    in.getStartDate(),
                    in.getEndDate(),
                    in.getRemainingSlots() + "/" + in.getMaxSlots()
            );
        }


        int index = ConsoleUtil.readInt("Select internship to inspect (0 to cancel): ", 0, internships.size());
        if (index == 0) return;

        Internship selected = internships.get(index - 1);

        // View full details
        browserView.printInternshipDetails(selected, false);

        // Confirm decision
        int decision = ConsoleUtil.readInt("\n1 = Approve, 2 = Reject, 3 = Back: ", 1, 3);
        if (decision == 3) {
            System.out.println("Returning to menu...");
            return;
        }

        if (decision == 1) {
            staffController.approveInternship(selected);
            System.out.println("Internship approved.");
        } else {
            staffController.rejectInternship(selected);
            System.out.println("Internship rejected.");
        }
    }


    private void manageWithdrawals() {
        List<InternshipApplication> applications = staffController.getWithdrawalRequests();
        if (applications.isEmpty()) {
            System.out.println("No withdrawal requests.");
            return;
        }

        System.out.println("\n================ Withdrawal Requests ================");
        System.out.printf("%-4s %-18s %-28s %-12s%n", "No.", "Student", "Internship", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);
            String studentName = staffController.getStudentName(a.getStudentId());
            String internshipTitle = staffController.getInternshipTitle(a.getInternshipId());

            System.out.printf("%-4d %-18s %-28s %-12s%n",
                    i + 1, studentName, internshipTitle, a.getStatus());
        }

        int index = ConsoleUtil.readInt("Select application to inspect (0 to cancel): ", 0, applications.size());
        if (index == 0) return;

        InternshipApplication selected = applications.get(index - 1);

        // Fetch related models
        Internship internship = staffController.getInternshipById(selected.getInternshipId());
        String studentName = staffController.getStudentName(selected.getStudentId());

        // Display full detail view
        browserView.printApplicationDetails(selected, studentName, internship.getTitle());

        // Confirm action
        int decision = ConsoleUtil.readInt("\n1 = Approve withdrawal, 2 = Reject, 3 = Back: ", 1, 3);
        if (decision == 3) {
            System.out.println("Returning to menu...");
            return;
        }

        if (decision == 1) {
            staffController.approveWithdrawal(selected);
            System.out.println("Withdrawal approved.");
        } else {
            staffController.rejectWithdrawal(selected);
            System.out.println("Withdrawal rejected.");
        }
    }


    private void viewAllApplications() {
        List<Internship> list = staffController.getAllInternships();

        if (list.isEmpty()) {
            System.out.println("No internships available in system.");
            return;
        }

        // ──────────────────────── Internship Table ─────────────────────────
        System.out.println("\n====================== ALL INTERNSHIPS ======================");
        System.out.printf("%-4s %-30s %-26s %-12s %-26s %-12s %-12s %-10s %-10s%n",
                "No.", "Title", "Major", "Level", "Company", "Open", "Close", "Status", "Slots");

        for (int i = 0; i < list.size(); i++) {
            Internship in = list.get(i);
            System.out.printf("%-4d %-30s %-26s %-12s %-26s %-12s %-12s %-10s %-10s%n",
                    i + 1,
                    crop(in.getTitle(), 30),
                    crop(in.getMajor(), 26),
                    in.getLevel(),
                    crop(in.getCompanyName(), 26),
                    in.getStartDate(),
                    in.getEndDate(),
                    in.getStatus(),
                    (in.getConfirmedSlots() + "/" + in.getMaxSlots())
            );
        }

        int index = ConsoleUtil.readInt("\nSelect internship to view applications (0 to cancel): ", 0, list.size());
        if (index == 0) return;

        Internship selected = list.get(index - 1);

        List<InternshipApplication> apps = staffController.getAllApplicationsForInternship(selected.getId());

        System.out.println("\n================ Applications for: " + selected.getTitle() + " =================");
        if (apps.isEmpty()) {
            System.out.println("No applications submitted for this internship.");
            return;
        }

        // ──────────────────────── Applications Table ─────────────────────────
        System.out.printf("%-4s %-15s %-22s %-26s %-14s %-12s %-10s%n",
                "No.", "Student ID", "Student Name", "Email", "Status", "Accepted", "App ID(Short)");

        for (int i = 0; i < apps.size(); i++) {
            InternshipApplication a = apps.get(i);
            String studentName = staffController.getStudentName(a.getStudentId());
            String email = staffController.getStudentById(a.getStudentId()).getEmail();
            String accepted = a.studentAccepted() ? "YES" : "NO";
            String appShort = a.getId().substring(0, 8);

            System.out.printf("%-4d %-15s %-22s %-26s %-14s %-12s %-10s%n",
                    i + 1,
                    a.getStudentId(),
                    crop(studentName, 22),
                    crop(email, 26),
                    a.getStatus(),
                    accepted,
                    appShort
            );
        }

        // Detailed view option
        int appIndex = ConsoleUtil.readInt("\nSelect application to inspect (0 to exit): ", 0, apps.size());
        if (appIndex == 0) return;

        InternshipApplication chosen = apps.get(appIndex - 1);
        String studentName = staffController.getStudentName(chosen.getStudentId());
        browserView.printApplicationDetails(chosen, studentName, selected.getTitle());
    }


    private String crop(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }



    // private void printReportTable(List<Internship> internships) {
    //     System.out.printf("%-12s %-20s %-15s %-10s %-8s %-12s %-18s %-20s%n",
    //         "ID","Title","Company","Level","Major","Status","Remaining Slots","Representative");

    //     for (Internship i : internships) {
    //         System.out.printf("%-12s %-20s %-15s %-10s %-8s %-12s %-18s %-20s%n",
    //                 i.getId(),
    //                 i.getTitle(),
    //                 i.getCompany().getCompanyName(),
    //                 i.getLevel(),
    //                 i.getMajor(),
    //                 i.getStatus(),
    //                 i.getRemainingSlots(),
    //                 i.getCr().getName()
    //         );
    //     }
    // }

    // private void printInternshipTable(List<Internship> list) {
    // System.out.printf("%-10s %-18s %-15s %-15s %-10s %-10s %-12s %-8s\n",
    //         "ID","Title","Company","Rep","Level","Remaining","Status","Major");

    // for (Internship i : list) {
    //     System.out.printf("%-10s %-18s %-15s %-15s %-10s %-10s %-12s %-8s\n",
    //             i.getId(),
    //             i.getTitle(),
    //             i.getCompanyName(),
    //             i.getCr().getUserId(),
    //             i.getLevel(),
    //             i.getRemainingSlots() + "/" + i.getMaxSlots(),
    //             i.getStatus(),
    //             i.getMajor()
    //     );
    //     }
    // }   

    // private void openReportMenu() {
    //     StaffReportView reportView = new StaffReportView(staffController);
    //     reportView.displayReportMenu();
    // }

    

    private void changePassword(Staff s) {
        System.out.print("Please enter new password: ");
        String newPw = sc.nextLine().trim();
        boolean pwChanged = s.changePassword(newPw);
        System.out.println(pwChanged ? "Password changed." : "You must be logged in to change your password.");
    }
}
