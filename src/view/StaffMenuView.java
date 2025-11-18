package view;

import java.util.List;
import java.util.Scanner;
import controller.StaffController;
import model.*;

/**
 * view class responsible for displaying and managing staff-facing menu actions
 * allows staff members to approve or reject users, internships, and withdrawal requests,
 * as well as view, filter, and report on internship and application records
 */
public class StaffMenuView {

    /** controller handling staff-related business logic and data operations */
    private final StaffController staffController;

    /** shared browser view used for report and filtering features */
    private final InternshipBrowserView browserView;

    /** scanner used to capture administrator console input */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * construct staff menu view with required controller and shared browser view
     * @param staffController controller managing staff operations and data retrieval
     * @param browserView shared view used for browsing and reporting internship listings
     */
    public StaffMenuView(StaffController staffController,
                         InternshipBrowserView browserView) {
        this.staffController = staffController;
        this.browserView = browserView;
    }

    /**
     * display staff main menu and process selected menu commands
     * loops until logout is requested
     * @param staff currently logged-in staff member
     */
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
                case 5 -> viewAllApplications();
                case 6 -> changePassword(staff);
                case 7 -> { return; }
            }
        }
    }

    /**
     * display and review all company representatives whose accounts are pending approval
     * enables staff to approve or reject selected applicants
     */
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
        if (index == 0) return;

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

    /**
     * display list of internships pending approval and allow staff to inspect,
     * approve, or reject selected internship postings
     */
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
                    in.getRemainingSlots() + "/" + in.getMaxSlots());
        }

        int index = ConsoleUtil.readInt("Select internship to inspect (0 to cancel): ", 0, internships.size());
        if (index == 0) return;

        Internship selected = internships.get(index - 1);
        browserView.printInternshipDetails(selected, false);

        int decision = ConsoleUtil.readInt("\n1 = Approve, 2 = Reject, 3 = Back: ", 1, 3);
        if (decision == 3) return;

        if (decision == 1) {
            staffController.approveInternship(selected);
            System.out.println("Internship approved.");
        } else {
            staffController.rejectInternship(selected);
            System.out.println("Internship rejected.");
        }
    }

    /**
     * display and resolve pending withdrawal requests for internship applications
     * allows staff to approve or reject each selected request
     */
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
        Internship internship = staffController.getInternshipById(selected.getInternshipId());
        String studentName = staffController.getStudentName(selected.getStudentId());
        browserView.printApplicationDetails(selected, studentName, internship.getTitle());

        int decision = ConsoleUtil.readInt("\n1 = Approve withdrawal, 2 = Reject, 3 = Back: ", 1, 3);
        if (decision == 3) return;

        if (decision == 1) {
            staffController.approveWithdrawal(selected);
            System.out.println("Withdrawal approved.");
        } else {
            staffController.rejectWithdrawal(selected);
            System.out.println("Withdrawal rejected.");
        }
    }

    /**
     * display all internships and allow staff to view all submitted applications
     * includes expanded application details and inspection option
     */
    private void viewAllApplications() {
        List<Internship> list = staffController.getAllInternships();

        if (list.isEmpty()) {
            System.out.println("No internships available in system.");
            return;
        }

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
                    (in.getConfirmedSlots() + "/" + in.getMaxSlots()));
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
                    appShort);
        }

        int appIndex = ConsoleUtil.readInt("\nSelect application to inspect (0 to exit): ", 0, apps.size());
        if (appIndex == 0) return;

        InternshipApplication chosen = apps.get(appIndex - 1);
        String studentName = staffController.getStudentName(chosen.getStudentId());
        browserView.printApplicationDetails(chosen, studentName, selected.getTitle());
    }

    /**
     * shorten text for table display, appending ellipsis when truncated
     * @param text original full text
     * @param max maximum characters allowed in output
     * @return cropped text with ellipsis if beyond limit
     */
    private String crop(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    /**
     * prompt staff to change password for their account
     * @param s active staff user requesting password update
     */
    private void changePassword(Staff s) {
        System.out.print("Please enter new password: ");
        String newPw = sc.nextLine().trim();
        boolean pwChanged = s.changePassword(newPw);
        System.out.println(pwChanged ? "Password changed." : "You must be logged in to change your password.");
    }
}
