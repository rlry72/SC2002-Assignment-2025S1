import java.time.LocalDate;

public class Internship extends Company {
    private String title;
    private String desc;
    private enum Level {LOW, MEDIUM, HIGH};
    private Level level;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private enum Status {PENDING, APPROVED, REJECTED, FILLED};
    private Status status;
    private CompanyRepresentative cr;
    // private int slots = 10;  
    private Student student;

    public Internship(String title, String desc, Level level, String major, LocalDate startDate, LocalDate endDate, Status status, CompanyRepresentative cr) {
        this.title = title;
        this.desc = desc;
        this.level = level;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.cr = cr;
    }


}
