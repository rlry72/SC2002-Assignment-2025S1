package view;

import controller.StudentController;
import controller.CompanyRepController;
import controller.StaffController;
import model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * view class responsible for displaying internships and applying user-defined filters
 * provides browsing capabilities for students, company representatives, and staff users
 * rendering and available actions vary based on caller role
 */
public class InternshipBrowserView {

    /**
     * enumeration of supported viewer roles for browser behaviour
     */
    public enum Role { STUDENT, REP, STAFF }

    /** current viewer's role controlling available actions */
    private final Role role;

    /** scanner used for console-based input collection */
    private final Scanner sc = new Scanner(System.in);

    /** controller used for student internship retrieval */
    private final StudentController studentCtrl;

    /** controller used for company representative internship retrieval */
    private final CompanyRepController repCtrl;

    /** controller used for staff-level internship retrieval and analytics */
    private final StaffController staffCtrl;

    /** per-user saved filter instances */
    private final Map<String, InternshipFilter> filterMap = new HashMap<>();

    /** per-user cached list of last displayed results */
    private final Map<String, List<Internship>> resultCache = new HashMap<>();


    /**
     * construct a browser view with fixed role and controller dependencies
     * @param role execution mode defining actions allowed
     * @param studentCtrl student controller instance
     * @param repCtrl company representative controller instance
     * @param staffCtrl staff controller instance
     */
    public InternshipBrowserView(Role role,
                                 StudentController studentCtrl,
                                 CompanyRepController repCtrl,
                                 StaffController staffCtrl) {
        this.role = role;
        this.studentCtrl = studentCtrl;
        this.repCtrl = repCtrl;
        this.staffCtrl = staffCtrl;
    }

    /**
     * continuously display browsing menu until caller exits
     * @param caller current logged in user
     */
    public void show(User caller) {
        while (true) {
            System.out.println("\n========= Internship Browser =========");
            System.out.println("Current Filters: " + summarizeFilters(caller));
            System.out.println("1. View internships");
            System.out.println("2. Change filters");
            System.out.println("3. Reset filters");
            if (role == Role.STAFF)
                System.out.println("4. Popularity report");
            System.out.println("0. Back");

            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> viewInternships(caller);
                case "2" -> updateFilters(caller);
                case "3" -> resetFilters(caller);
                case "4" -> { if (role == Role.STAFF) staffPopularity(); else System.out.println("Not allowed."); }
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * retrieve and display internship list based on caller role and active filters
     * allows follow-up selection for detailed view
     * @param caller user requesting internship list
     */
    private void viewInternships(User caller) {
        InternshipFilter f = getFilterFor(caller);
        List<Internship> list = switch (role) {
            case STUDENT -> studentCtrl.getEligibleInternships((Student) caller, f);
            case REP     -> repCtrl.getOwnInternshipsFiltered((CompanyRepresentative) caller, f);
            case STAFF   -> staffCtrl.getFiltered(f);
        };

        printTable(caller, list);
        if (list.isEmpty()) return;

        // System.out.println("\nEnter internship index to view details (0 to return): ");
        // String input = sc.nextLine().trim();
        // int index = Integer.parseInt(input);
        int index = ConsoleUtil.readInt("\nEnter internship index to view details (0 to return): ", 0, list.size());

        if (index == 0) return;

        if (index >= 1 && index <= list.size())
            printInternshipDetails(list.get(index - 1), caller instanceof Student);
    }

    /**
     * obtain existing filter for user or initialise a new one
     * @param u user requesting filter access
     * @return filter linked to user
     */
    private InternshipFilter getFilterFor(User u) {
        return filterMap.computeIfAbsent(u.getUserId(), id -> new InternshipFilter());
    }

    /**
     * update active filter values using console input prompts
     * @param caller user updating filter criteria
     */
    private void updateFilters(User caller) {
        InternshipFilter f = getFilterFor(caller);
        System.out.println("\n--- Update Filters (blank to skip) ---");

        // status allowed only for staff & company representatives
        if (!(caller instanceof Student)) {
            System.out.print("Status (PENDING/APPROVED/REJECTED/FILLED): ");
            String s = sc.nextLine().trim();
            f.setStatus(s.isEmpty() ? null : parseEnum(Internship.Status.class, s));
        } else {
            // students must always see only approved listings
            f.setStatus(null);
        }

        System.out.print("Preferred Major: ");
        String m = sc.nextLine().trim();
        f.setMajor(m.isEmpty() ? null : m);

        System.out.print("Level (BASIC/MEDIUM/HIGH): ");
        String lvl = sc.nextLine().trim();
        f.setLevel(lvl.isEmpty() ? null : parseEnum(Internship.Level.class, lvl));

        System.out.print("Company Name: ");
        String c = sc.nextLine().trim();
        f.setCompanyName(c.isEmpty() ? null : c);

        System.out.print("Closing Before (yyyy-MM-dd): ");
        String d = sc.nextLine().trim();
        f.setEndDate(d.isEmpty() ? null : parseDate(d));

        System.out.println("Filters saved.");
    }


    /**
     * clear all active filter fields for caller
     * @param caller user performing filter reset
     */
    private void resetFilters(User caller) {
        InternshipFilter f = getFilterFor(caller);
        f.setStatus(null)
            .setMajor(null)
            .setLevel(null)
            .setCompanyName(null)
            .setStartDate(null)
            .setEndDate(null)
            .setRepId(null)
            .setSlotsMin(null)
            .setSlotsMax(null);

        System.out.println("Filters reset.");
    }

    /**
     * display internship popularity ranking report (staff only)
     */
    private void staffPopularity() {
        System.out.println("\n=== Popularity Ranking ===");
        staffCtrl.getPopularityReport().forEach(System.out::println);
    }

    /**
     * build string representation of caller's active filter configuration
     * @param caller user requesting filter summary
     * @return formatted summary description
     */
    private String summarizeFilters(User caller) {
        InternshipFilter f = getFilterFor(caller);
        return String.format("Status=%s, Major=%s, Level=%s, Company=%s, EndBefore=%s",
                f.getStatus(), f.getMajor(), f.getLevel(), f.getCompanyName(), f.getEndDate());
    }

    /**
     * parse enum safely from user text entry
     * @param <E> enum type
     * @param type enum class reference
     * @param value text containing potential enum constant
     * @return parsed enum constant or null if invalid
     */
    private <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        try { return Enum.valueOf(type, value.toUpperCase()); }
        catch (Exception e) { System.out.println("Invalid input!"); return null; }
    }

    /**
     * attempt to parse string into LocalDate
     * @param s date text
     * @return parsed LocalDate or null if invalid
     */
    private LocalDate parseDate(String s) {
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) { System.out.println("Invalid date!"); return null; }
    }

    /**
     * render table of internships in summary form
     * @param caller user viewing internship list
     * @param list internships to display
     */
    private void printTable(User caller, List<Internship> list) {
        if (list.isEmpty()) {
            System.out.println("No matching internships found.");
            return;
        }

        boolean isStudent = caller instanceof Student;

        if (isStudent) {
            System.out.printf("%-4s %-25s %-18s %-14s %-35s %-12s%n",
                    "#", "Title", "Company", "Level", "Major", "Remaining");
        } else {
            System.out.printf("%-4s %-25s %-18s %-14s %-35s %-12s %-10s%n",
                    "#", "Title", "Company", "Level", "Major", "Remaining", "Status");
        }

        int index = 1;
        for (Internship i : list) {
            if (isStudent) {
                System.out.printf("%-4d %-25s %-18s %-14s %-35s %-12s%n",
                        index++, i.getTitle(), i.getCompanyName(),
                        i.getLevel(), i.getMajor(),
                        i.getRemainingSlots() + "/" + i.getMaxSlots());
            } else {
                System.out.printf("%-4d %-25s %-18s %-14s %-35s %-12s %-10s%n",
                        index++, i.getTitle(), i.getCompanyName(),
                        i.getLevel(), i.getMajor(),
                        i.getRemainingSlots() + "/" + i.getMaxSlots(),
                        i.getStatus());
            }
        }

        resultCache.put(caller.getUserId(), list);
    }

    /**
     * retrieve most recent internship results for caller
     * @param caller user requesting cached results
     * @return cached list of internships or empty list if none
     */
    public List<Internship> getLastResults(User caller) {
        return resultCache.getOrDefault(caller.getUserId(), List.of());
    }

    /**
     * display filtered internship results without full browser interaction loop
     * @param caller user viewing filtered list
     */
    public void showFilteredList(User caller) {
        List<Internship> list = switch (role) {
            case STUDENT -> studentCtrl.getEligibleInternships((Student) caller, getFilterFor(caller));
            case REP     -> repCtrl.getOwnInternshipsFiltered((CompanyRepresentative) caller, getFilterFor(caller));
            case STAFF   -> staffCtrl.getFiltered(getFilterFor(caller));
        };

        printTable(caller, list);
    }

    /**
     * interactive browsing mode for selecting a specific internship
     * @param caller active user making a selection
     * @return list used during final decision step, or empty list if cancelled
     */
    public List<Internship> browseForSelection(User caller) {
        while (true) {
            List<Internship> list = caller instanceof Student
                    ? studentCtrl.getEligibleInternships((Student) caller, getFilterFor(caller))
                    : caller instanceof CompanyRepresentative
                        ? repCtrl.getOwnInternshipsFiltered((CompanyRepresentative) caller, getFilterFor(caller))
                        : staffCtrl.getFiltered(getFilterFor(caller));

            printTable(caller, list);

            int choice = ConsoleUtil.readInt("Enter internship index to view details (0 to exit): ", 0, list.size());
            if (choice == 0) return List.of();

            Internship selected = list.get(choice - 1);
            printInternshipDetails(selected, caller instanceof Student);

            if (confirmAction("Apply / Select this internship? (Y/N): "))
                return list;
        }
    }

    /**
     * prompt user until valid confirmation response is received
     * @param message confirmation prompt text
     * @return true if confirmed, false if declined
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

    /**
     * print detailed internship information in formatted display layout
     * @param i internship to display
     * @param isStudentView hide certain internal fields when true
     */
    public void printInternshipDetails(Internship i, boolean isStudentView) {
        String title = " " + i.getTitle() + " ";
        int width = Math.max(80, title.length() + 6);
        String border = repeat("-", width);

        System.out.println(border);
        System.out.println("|" + center(title.trim(), width - 2) + "|");
        System.out.println(border);

        detailRow("Description", i.getDesc(), width);
        detailRow("Level", i.getLevel().toString(), width);
        detailRow("Preferred Major", i.getMajor(), width);
        detailRow("Opening Date", i.getStartDate().toString(), width);
        detailRow("Closing Date", i.getEndDate().toString(), width);
        detailRow("Company Name", i.getCompanyName(), width);
        detailRow("Representative", i.getCr().getName(), width);
        detailRow("Slots", i.getRemainingSlots() + "/" + i.getMaxSlots(), width);

        if (!isStudentView) {
            detailRow("Status", i.getStatus().toString(), width);
            detailRow("Visibility", i.getVisibility() ? "Visible" : "Hidden", width);
        }

        System.out.println(border);
    }

    /**
     * print formatted application details including resolved student and internship text
     * @param app application being displayed
     * @param studentName resolved student display name
     * @param internshipTitle resolved internship title
     */
    public void printApplicationDetails(InternshipApplication app, String studentName, String internshipTitle) {
        String title = " Internship Application Details ";
        int width = Math.max(80, title.length() + 6);
        String border = repeat("-", width);

        System.out.println(border);
        System.out.println("|" + center(title.trim(), width - 2) + "|");
        System.out.println(border);

        detailRow("Application ID", app.getId(), width);
        detailRow("Student ID", app.getStudentId(), width);
        detailRow("Student Name", studentName, width);
        detailRow("Internship", internshipTitle, width);
        detailRow("Status", app.getStatus().toString(), width);
        detailRow("Student Accepted", app.studentAccepted() ? "YES" : "NO", width);

        System.out.println(border);
    }

    /**
     * print one key-value field pair inside formatted detail box
     * @param label field name
     * @param value field value
     * @param width maximum line width
     */
    private void detailRow(String label, String value, int width) {
        String line = " " + label + " : " + value;
        System.out.println("|" + padRight(line, width - 2) + "|");
    }

    /**
     * pad text to right boundary for formatted output field
     * @param text original text
     * @param length target width
     * @return right-padded text
     */
    private String padRight(String text, int length) {
        return text.length() >= length ? text : text + " ".repeat(length - text.length());
    }

    /**
     * center text horizontally inside specified width
     * @param text input text
     * @param width total usable width
     * @return centered text string
     */
    private String center(String text, int width) {
        int padding = width - text.length();
        int padLeft = padding / 2;
        return " ".repeat(padLeft) + text + " ".repeat(padding - padLeft);
    }

    /**
     * return string repeated N times
     * @param s string to repeat
     * @param count number of repetitions
     * @return repeated string
     */
    private String repeat(String s, int count) {
        return s.repeat(count);
    }

}
