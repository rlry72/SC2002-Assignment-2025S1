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

public class InternshipBrowserView {

    public enum Role { STUDENT, REP, STAFF }

    private final Role role;
    private final Scanner sc = new Scanner(System.in);

    private final StudentController studentCtrl;
    private final CompanyRepController repCtrl;
    private final StaffController staffCtrl;

    // saved filter state
    // private final InternshipFilter filter = new InternshipFilter();

    // private List<Internship> lastResults = List.of(); // cache

    private final Map<String, InternshipFilter> filterMap = new HashMap<>();
    private final Map<String, List<Internship>> resultCache = new HashMap<>();


    public InternshipBrowserView(Role role,
                                 StudentController studentCtrl,
                                 CompanyRepController repCtrl,
                                 StaffController staffCtrl) {
        this.role = role;
        this.studentCtrl = studentCtrl;
        this.repCtrl = repCtrl;
        this.staffCtrl = staffCtrl;
    }

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
                case "4" -> {
                    if (role == Role.STAFF) staffPopularity();
                    else System.out.println("Not allowed.");
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewInternships(User caller) {
        InternshipFilter f = getFilterFor(caller);
        List<Internship> list = switch (role) {
            case STUDENT -> studentCtrl.getEligibleInternships((Student) caller, f);
            case REP     -> repCtrl.getOwnInternshipsFiltered((CompanyRepresentative) caller, f);
            case STAFF   -> staffCtrl.getFiltered(f);
        };
        printTable(caller, list);
        if (list.isEmpty()) return;

        System.out.println("\nEnter internship index to view details (0 to return): ");
        String input = sc.nextLine().trim();

        int index = Integer.parseInt(input);

        if (index >= 1 && index <= list.size()) {
            printInternshipDetails(list.get(index - 1));
        }
    }

    private InternshipFilter getFilterFor(User u) {
        return filterMap.computeIfAbsent(u.getUserId(), id -> new InternshipFilter());
    }

    private void updateFilters(User caller) {
        InternshipFilter f = getFilterFor(caller);
        System.out.println("\n--- Update Filters (blank to skip) ---");

        System.out.print("Status (PENDING/APPROVED/REJECTED/FILLED): ");
        String s = sc.nextLine().trim();
        f.setStatus(s.isEmpty() ? null : parseEnum(Internship.Status.class, s));

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


    private void staffPopularity() {
        System.out.println("\n=== Popularity Ranking ===");
        staffCtrl.getPopularityReport().forEach(System.out::println);
    }

    private String summarizeFilters(User caller) {
        InternshipFilter f = getFilterFor(caller);
        return String.format("Status=%s, Major=%s, Level=%s, Company=%s, EndBefore=%s",
                f.getStatus(), f.getMajor(), f.getLevel(),
                f.getCompanyName(), f.getEndDate());
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        try { return Enum.valueOf(type, value.toUpperCase()); }
        catch (Exception e) { System.out.println("Invalid input!"); return null; }
    }

    private LocalDate parseDate(String s) {
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) { System.out.println("Invalid date!"); return null; }
    }

    private void printTable(User caller, List<Internship> list) {
        if (list.isEmpty()) {
            System.out.println("No matching internships found.");
            return;
        }

        System.out.printf("%-4s %-22s %-14s %-8s %-8s %-13s %-10s%n",
                "#", "Title", "Company", "Level", "Major", "Remaining", "Status");

        int index = 1;
        for (Internship i : list) {
            System.out.printf("%-4d %-22s %-14s %-8s %-8s %-13s %-10s%n",
                    index++,
                    i.getTitle(),
                    i.getCompanyName(),
                    i.getLevel(),
                    i.getMajor(),
                    i.getRemainingSlots() + "/" + i.getMaxSlots(),
                    i.getStatus());
        }

        // Save per user
        resultCache.put(caller.getUserId(), list);
    }

    public List<Internship> getLastResults(User caller) {
        return resultCache.getOrDefault(caller.getUserId(), List.of());
    }

    private void printInternshipDetails(Internship i) {
        String title = " " + i.getTitle() + " ";
        int width = Math.max(80, title.length() + 6); // dynamic width
        String border = repeat("-", width);

        System.out.println(border);
        System.out.println("|" + center(title.trim(), width - 2) + "|");
        System.out.println(border);

        detailRow("Description", i.getDesc(), width);
        detailRow("Level", i.getLevel().toString(), width);
        detailRow("Preferred Major", i.getMajor(), width);
        detailRow("Opening Date", i.getStartDate().toString(), width);
        detailRow("Closing Date", i.getEndDate().toString(), width);
        detailRow("Status", i.getStatus().toString(), width);
        detailRow("Company Name", i.getCompanyName(), width);
        detailRow("Company Representative", i.getCr().getName(), width);
        detailRow("Slots", i.getMaxSlots() + " available", width);

        System.out.println(border);
    }

    private void detailRow(String label, String value, int width) {
        String line = " " + label + " : " + value;
        System.out.println("|" + padRight(line, width - 2) + "|");
    }

    private String padRight(String text, int length) {
        if (text.length() >= length) return text;
        return text + " ".repeat(length - text.length());
    }

    private String center(String text, int width) {
        int padding = width - text.length();
        int padLeft = padding / 2;
        int padRight = padding - padLeft;
        return " ".repeat(padLeft) + text + " ".repeat(padRight);
    }

    private String repeat(String s, int count) {
        return s.repeat(count);
    }

}
