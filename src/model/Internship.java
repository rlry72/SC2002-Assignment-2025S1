package model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * represent an internship posting created by a company representative
 * stores available slots, timeline, level, majors, company and visibility state
 * supports status change and slot confirmation logic
 */
public class Internship {

    /** unique internship identifier */
    private String id;

    /** internship title shown to students */
    private String title;

    /** description of internship scope and duties */
    private String desc;

    /**
     * represent internship year of study requirement
     * BASIC: year 1–2 only
     * INTERMEDIATE: suitable for year 3 and above students
     * ADVANCED: suitable for year 3 and above students
     */
    public enum Level { BASIC, INTERMEDIATE, ADVANCED }

    /** assigned academic level requirement */
    private Level level;

    /** preferred major required for eligibility */
    private String major;

    /** internship application opening date */
    private LocalDate startDate;

    /** internship application closing date */
    private LocalDate endDate;

    /**
     * represent approval state
     * PENDING: awaiting staff review
     * APPROVED: visible for student application
     * REJECTED: denied by staff
     * FILLED: all available slots confirmed
     */
    public enum Status { PENDING, APPROVED, REJECTED, FILLED }

    /** current approval status of internship */
    private Status status;

    /** company representative who created and manages this internship */
    private CompanyRepresentative cr;

    /** maximum allowed applicant acceptance slots (1 to 10) */
    private int maxSlots;

    /** number of confirmed student placements */
    private int confirmedSlots = 0;

    /** unique set of applicant student IDs */
    private final Set<String> applicantIds = new HashSet<>();

    /** visibility flag controlling whether students can view posting */
    private boolean isVisible;

    /** company owning and offering this internship */
    private Company company;

    /**
     * create new internship posting in pending state
     * slot count automatically bounded between 1 and 10
     * @param id internal internship id
     * @param title internship title
     * @param desc internship description
     * @param level academic level requirement
     * @param major preferred major
     * @param startDate application open date
     * @param endDate application close date
     * @param cr responsible company representative
     * @param slots requested max slot count
     * @param isVisible initial visibility flag
     * @param company offering company object
     */
    public Internship(String id, String title, String desc, Level level, String major,
                      LocalDate startDate, LocalDate endDate, CompanyRepresentative cr,
                      int slots, boolean isVisible, Company company) {

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

    /** @return internship id */
    public String getId() {
        return id;
    }

    /** update internship id */
    public void setId(String id) {
        this.id = id;
    }

    /** @return internship status */
    public Status getStatus() {
        return status;
    }

    /** update internship approval state */
    public void setStatus(Status status) {
        this.status = status;
    }

    /** @return visibility state */
    public boolean getVisibility() {
        return isVisible;
    }

    /** update visibility state */
    public void setVisibility(boolean visibility) {
        this.isVisible = visibility;
    }

    /** @return name of offering company */
    public String getCompanyName() {
        return company.getCompanyName();
    }

    /** @return internship title */
    public String getTitle() {
        return title;
    }

    /** update internship title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return immutable applicant id set */
    public Set<String> getApplicantIds() {
        return applicantIds;
    }

    /** @return company object */
    public Company getCompany() {
        return company;
    }

    /** update company reference */
    public void setCompany(Company company) {
        this.company = company;
    }

    /** @return number of confirmed slots */
    public int getConfirmedSlots() {
        return confirmedSlots;
    }

    /** @return company representative who created posting */
    public CompanyRepresentative getCr() {
        return cr;
    }

    /** @return internship description */
    public String getDesc() {
        return desc;
    }

    /** update description text */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /** @return closing date */
    public LocalDate getEndDate() {
        return endDate;
    }

    /** update closing date */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /** @return internship academic level */
    public Level getLevel() {
        return level;
    }

    /** update internship level */
    public void setLevel(Level level) {
        this.level = level;
    }

    /** @return preferred major text */
    public String getMajor() {
        return major;
    }

    /** update preferred major */
    public void setMajor(String major) {
        this.major = major;
    }

    /** @return max available slot count */
    public int getMaxSlots() {
        return maxSlots;
    }

    /** update max allowed slot count */
    public void setMaxSlots(int slots) {
        this.maxSlots = slots;
    }

    /** @return open date */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** update open date */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * check if posting is open for applications on given date
     * @param today date checked
     * @return true if within inclusive start and end date
     */
    public boolean isOpen(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    /**
     * increment confirmed slot if space available
     * automatically marks internship FILLED when last slot is taken
     */
    public void addConfirmedSlot() {
        if (confirmedSlots < maxSlots) {
            confirmedSlots++;
            if (confirmedSlots == maxSlots) {
                this.status = Status.FILLED;
            }
        }
    }

    /**
     * check if internship has reached maximum confirmation limit
     * @return true if FILLED
     */
    public boolean isFull() {
        return this.status == Status.FILLED;
    }

    /**
     * @return available slot count
     */
    public int getRemainingSlots() {
        return maxSlots - confirmedSlots;
    }
}
