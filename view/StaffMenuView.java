package view;

import java.util.List;
import java.util.Scanner;

import controller.StaffController;
import model.*;

public class StaffMenuView {
    private final StaffController staffController;
    private final StaffReportView reportView;
    private static final Scanner sc = new Scanner(System.in);

    public StaffMenuView(StaffController staffController) {
        this.staffController = staffController;
        this.reportView = new StaffReportView(staffController);
    }

    public void displayStaffMenu(Staff staff) {
        while (true) {
            System.out.println("\n========= Staff Menu =========");
            System.out.println("1. Approve/Reject company representatives");
            System.out.println("2. Approve/Reject internship postings");
            System.out.println("3. Approve/Reject withdrawal requests");
            System.out.println("4. Generate Internship Opportunities reports");
            System.out.println("5. Change password");
            System.out.println("6. Logout");

            int choice = ConsoleUtil.readInt("Choose: ", 1, 6);

            switch (choice) {
                case 1 -> manageCompanyReps();
                case 2 -> manageInternships();
                case 3 -> manageWithdrawals();
                case 4 -> reportView.displayReportMenu();
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

        System.out.printf("%-4s %-15s %-22s %-15s %-10s%n",
                "No.", "ID", "Title", "Company", "Level");

        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            System.out.printf("%-4d %-15s %-22s %-15s %-10s%n",
                    (i + 1),
                    internship.getId(),
                    internship.getTitle(),
                    internship.getCompanyName(),
                    internship.getLevel());
        }

        int index = ConsoleUtil.readInt("Select index of Internship to manage, 0 to cancel: ", 0, internships.size());
        if (index == 0)
            return;

        Internship selectedInternship = internships.get(index - 1);
        System.out.println("1 = Approve, 2 = Reject");
        int decision = ConsoleUtil.readInt("Choose: ", 1, 2);

        if (decision == 1) {
            staffController.approveInternship(selectedInternship);
            System.out.println("Approved internship.");
        } else {
            staffController.rejectInternship(selectedInternship);
            System.out.println("Rejected internship.");
        }
    }

    private void manageWithdrawals() {
        List<InternshipApplication> applications = staffController.getWithdrawalRequests();
        if (applications.isEmpty()) {
            System.out.println("No withdrawl requests.");
            return;
        }

        System.out.printf("%-4s %-15s %-15s %-15s %-12s%n",
                "No.", "ApplicationID", "StudentID", "InternshipID", "Status");

        for (int i = 0; i < applications.size(); i++) {
            InternshipApplication a = applications.get(i);
            System.out.printf("%-4d %-15s %-15s %-15s %-12s%n",
                    (i + 1), a.getId(), a.getStudentId(), a.getInternshipId(), a.getStatus());
        }

        int index = ConsoleUtil.readInt("Select index of Internship Application to manage, 0 to cancel: ", 0, applications.size());
        if (index == 0)
            return;

        InternshipApplication selectedApplication = applications.get(index - 1);
        System.out.println("1 = Approve, 2 = Reject");
        int decision = ConsoleUtil.readInt("Choose: ", 1, 2);

        if (decision == 1) {
            staffController.approveWithdrawal(selectedApplication);
            System.out.println("Approved withdrawal.");
        } else {
            staffController.rejectWithdrawal(selectedApplication);
            System.out.println("Rejected withdrawal.");
        }
    }

    private void printReportTable(List<Internship> internships) {
        System.out.printf("%-12s %-20s %-15s %-10s %-8s %-12s %-18s %-20s%n",
            "ID","Title","Company","Level","Major","Status","Remaining Slots","Representative");

        for (Internship i : internships) {
            System.out.printf("%-12s %-20s %-15s %-10s %-8s %-12s %-18s %-20s%n",
                    i.getId(),
                    i.getTitle(),
                    i.getCompany().getCompanyName(),
                    i.getLevel(),
                    i.getMajor(),
                    i.getStatus(),
                    i.getRemainingSlots(),
                    i.getCr().getName()
            );
        }
    }

    private void printInternshipTable(List<Internship> list) {
    System.out.printf("%-10s %-18s %-15s %-15s %-10s %-10s %-12s %-8s\n",
            "ID","Title","Company","Rep","Level","Remaining","Status","Major");

    for (Internship i : list) {
        System.out.printf("%-10s %-18s %-15s %-15s %-10s %-10s %-12s %-8s\n",
                i.getId(),
                i.getTitle(),
                i.getCompanyName(),
                i.getCr().getUserId(),
                i.getLevel(),
                i.getRemainingSlots() + "/" + i.getMaxSlots(),
                i.getStatus(),
                i.getMajor()
        );
        }
    }   

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
