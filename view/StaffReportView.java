package view;

import controller.StaffController;
import model.Internship;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class StaffReportView {

    private final StaffController controller;
    private final Scanner sc = new Scanner(System.in);

    public StaffReportView(StaffController controller) {
        this.controller = controller;
    }

    public void displayReportMenu() {
        while (true) {
            System.out.println("\n================= Internship Reporting Menu =================");
            System.out.println("1. View All Internships");
            System.out.println("2. Filter By Status");
            System.out.println("3. Filter By Preferred Major");
            System.out.println("4. Filter By Internship Level");
            System.out.println("5. Filter By Company Name");
            System.out.println("6. Filter By Company Representative");
            System.out.println("7. Filter By Date Range");
            System.out.println("8. Sort By Remaining Slots (Descending)");
            System.out.println("9. View Most Popular Internships");
            System.out.println("0. Back");
            System.out.print("==============================================================\nEnter option: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> print(controller.getAll());
                case "2" -> filterByStatus();
                case "3" -> filterByMajor();
                case "4" -> filterByLevel();
                case "5" -> filterByCompanyName();
                case "6" -> filterByRepresentative();
                case "7" -> filterByDateRange();
                case "8" -> print(controller.sortByRemainingSlots());
                case "9" -> printPopularity();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void filterByStatus() {
        System.out.print("Enter status (PENDING, APPROVED, REJECTED, FILLED): ");
        String input = sc.nextLine().toUpperCase();
        try {
            Internship.Status status = Internship.Status.valueOf(input);
            print(controller.filterByStatus(status));
        } catch (Exception e) {
            System.out.println("Invalid status.");
        }
    }

    private void filterByMajor() {
        System.out.print("Enter preferred major: ");
        print(controller.filterByMajor(sc.nextLine().trim()));
    }

    private void filterByLevel() {
        System.out.print("Enter level (LOW, MEDIUM, HIGH): ");
        try {
            Internship.Level level = Internship.Level.valueOf(sc.nextLine().trim().toUpperCase());
            print(controller.filterByLevel(level));
        } catch (Exception e) {
            System.out.println("Invalid level.");
        }
    }

    private void filterByCompanyName() {
        System.out.print("Enter company name: ");
        print(controller.filterByCompany(sc.nextLine().trim()));
    }

    private void filterByRepresentative() {
        System.out.print("Enter representative ID: ");
        print(controller.filterByRepresentative(sc.nextLine().trim()));
    }

    private void filterByDateRange() {
        System.out.print("Enter start date (YYYY-MM-DD or blank): ");
        String startInput = sc.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD or blank): ");
        String endInput = sc.nextLine();

        LocalDate start = startInput.isBlank() ? null : LocalDate.parse(startInput);
        LocalDate end = endInput.isBlank() ? null : LocalDate.parse(endInput);

        print(controller.filterByDateRange(start, end));
    }

    private void printPopularity() {
        System.out.println("\n===== Popularity Ranking by Applications =====");
        controller.getPopularityReport().forEach(System.out::println);
    }

    private void print(List<Internship> list) {
        System.out.println("\n===== Filter / Report Result =====");
        if (list.isEmpty()) {
            System.out.println("No matching internships found.");
            return;
        }

        System.out.printf("%-8s %-20s %-15s %-12s %-8s %-10s %-12s%n",
                "ID", "Title", "Company", "Level", "Major", "Remain", "Status");

        list.forEach(i ->
                System.out.printf("%-8s %-20s %-15s %-12s %-8s %-10s %-12s%n",
                        i.getId(),
                        i.getTitle(),
                        i.getCompanyName(),
                        i.getLevel(),
                        i.getMajor(),
                        (i.getRemainingSlots() + "/" + i.getMaxSlots()),
                        i.getStatus()
                )
        );
    }
}
