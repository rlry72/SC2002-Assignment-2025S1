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
            System.out.println("4. View / Filter / Report internships"); // now browser
            System.out.println("5. Change password");
            System.out.println("6. Logout");

            int choice = ConsoleUtil.readInt("Choose: ", 1, 6);

            switch (choice) {
                case 1 -> manageCompanyReps();
                case 2 -> manageInternships();
                case 3 -> manageWithdrawals();
                case 4 -> browserView.show(staff);   // <-- uses shared browser
                case 5 -> changePassword(staff);
                case 6 -> { return; }
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

        System.out.printf("%-4s %-25s %-20s %-10s%n", "No.", "Title", "Company", "Level");
        for (int i = 0; i < internships.size(); i++) {
            Internship in = internships.get(i);
            System.out.printf("%-4d %-25s %-20s %-10s%n",
                    i + 1, in.getTitle(), in.getCompanyName(), in.getLevel());
        }

        int index = ConsoleUtil.readInt("Select index, 0 to cancel: ", 0, internships.size());
        if (index == 0) return;

        Internship selected = internships.get(index - 1);
        int decision = ConsoleUtil.readInt("1 = Approve, 2 = Reject: ", 1, 2);

        if (decision == 1) {
            staffController.approveInternship(selected);
            System.out.println("Approved internship.");
        } else {
            staffController.rejectInternship(selected);
            System.out.println("Rejected internship.");
        }
    }

    private void manageWithdrawals() {
        List<InternshipApplication> applications = staffController.getWithdrawalRequests();
        if (applications.isEmpty()) {
            System.out.println("No withdrawal requests.");
            return;
        }

        System.out.printf("%-4s %-18s %-25s %-12s%n",
                "No.", "Student", "Internship", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);

            // Fetch readable details instead of UUIDs
            String studentName = staffController.getStudentName(a.getStudentId());
            String internshipTitle = staffController.getInternshipTitle(a.getInternshipId());

            System.out.printf("%-4d %-18s %-25s %-12s%n",
                    (i + 1), studentName, internshipTitle, a.getStatus());
        }

        int index = ConsoleUtil.readInt("Select application to manage, 0 to cancel: ",
                                        0, applications.size());
        if (index == 0) return;

        InternshipApplication selected = applications.get(index - 1);

        System.out.println("1 = Approve, 2 = Reject");
        int decision = ConsoleUtil.readInt("Choose: ", 1, 2);

        if (decision == 1) {
            staffController.approveWithdrawal(selected);
            System.out.println("Withdrawal approved.");
        } else {
            staffController.rejectWithdrawal(selected);
            System.out.println("Withdrawal rejected.");
        }
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
