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
    private int maxSlots;  
    private int confirmedSlots = 0;
    private final Set<String> applicantIds = new HashSet<>();
    private boolean isVisible;
    private Company company;

    public Internship(String id, String title, String desc, Level level, String major, LocalDate startDate, 
        LocalDate endDate, CompanyRepresentative cr, int slots, boolean isVisible, Company company) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.level = level;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = Status.PENDING;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getApplicantIds() {
        return applicantIds;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int slots) {
        this.maxSlots = slots;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isOpen(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public void addConfirmedSlot() {
        if (confirmedSlots < maxSlots) {
            confirmedSlots++;

            if (confirmedSlots == maxSlots) {
                this.status = Status.FILLED;
            }
        }

    }
    
    public boolean isFull() {
        return this.status == Status.FILLED;
    }

    public int getRemainingSlots() {
        return maxSlots - confirmedSlots;
    }
}
