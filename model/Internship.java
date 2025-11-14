package model;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Internship {
    private String id;
    private String title;
    private String desc;
    public enum Level {BASIC, INTERMEDIATE, ADVANCED};
    private Level level;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    public enum Status {PENDING, APPROVED, REJECTED, FILLED};
    private Status status;
    private CompanyRepresentative cr;
    private final int maxSlots;  
    private int confirmedSlots = 0;
    private final Set<String> applicantIds = new HashSet<>();
    private boolean isVisible;
    private Company company;

    public Internship(String id, String title, String desc, Level level, String major, LocalDate startDate, 
        LocalDate endDate, Status status, CompanyRepresentative cr, int slots, boolean isVisible, Company company) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.level = level;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.cr = cr;
        this.maxSlots = Math.min(10, Math.max(1, slots));
        this.isVisible = isVisible;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public boolean getVisibility() {
        return isVisible;
    }

    public void setVisibility(boolean visiblity) {
        this.isVisible = visiblity;
    }

    public String getCompanyName() {
        return company.getCompanyName();
    }

    public String getTitle() {
        return title;
    }

    public Set<String> getApplicantIds() {
        return applicantIds;
    }

    public Company getCompany() {
        return company;
    }

    public int getConfirmedSlots() {
        return confirmedSlots;
    }

    public CompanyRepresentative getCr() {
        return cr;
    }

    public String getDesc() {
        return desc;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Level getLevel() {
        return level;
    }

    public String getMajor() {
        return major;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public boolean isOpen(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    
}
