package model;

/**
 * represent a student's internship application record
 * stores links to student and internship, tracks status and acceptance state
 * includes withdrawal request flag for staff approval workflow
 */
public class InternshipApplication {

    /** unique application identifier */
    private String id;

    /** id of student owning this application */
    private String studentId;

    /** id of internship associated with this application */
    private String internshipId;

    /**
     * represent possible application states
     * pending: awaiting representative decision
     * successful: approved by representative
     * unsuccessful: rejected by representative
     * withdrawn: withdrawn by student or student has accepted another offer
     */
    public enum Status {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        WITHDRAWN
    }

    /** current application status */
    private Status status;

    /** flag indicating that the student has accepted this offer */
    private boolean studentAccepted;

    /** flag indicating that student submitted a withdrawal request for staff review */
    private boolean withdrawalRequested;

    /**
     * create new internship application in pending state
     * @param id unique application identifier
     * @param studentId id of student applying
     * @param internshipId id of internship applied to
     */
    public InternshipApplication(String id, String studentId, String internshipId) {
        this.id = id;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.status = Status.PENDING;
        this.studentAccepted = false;
        this.withdrawalRequested = false;
    }

    /**
     * get unique application id
     * @return id string
     */
    public String getId() {
        return id;
    }

    /**
     * get internship id linked to this application
     * @return internship id
     */
    public String getInternshipId() {
        return internshipId;
    }

    /**
     * get current application status
     * @return application status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * get student id linked to this application
     * @return student id
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * update application status
     * @param status new status value
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * check whether student has officially accepted the offer
     * @return true if accepted, else false
     */
    public boolean studentAccepted() {
        return this.studentAccepted;
    }

    /**
     * mark this application as accepted by student
     */
    public void accept() {
        this.studentAccepted = true;
    }

    /**
     * permanently withdraw this application
     * sets status to withdrawn
     */
    public void withdraw() {
        this.status = Status.WITHDRAWN;
    }

    /**
     * check if application is in a state counting as active
     * active means pending review or successful offer
     * @return true if active, else false
     */
    public boolean isActive() {
        return status == Status.PENDING || status == Status.SUCCESSFUL;
    }

    /**
     * check if application is still pending decision
     * @return true if pending, else false
     */
    public boolean isPending() {
        return status == Status.PENDING;
    }

    /**
     * check if withdrawal has been requested by student but not yet processed
     * @return true if withdrawal requested, else false
     */
    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    /**
     * request withdrawal of this application
     * does not immediately change status until staff approval
     */
    public void requestWithdrawal() {
        this.withdrawalRequested = true;
    }

    /**
     * format object as readable string representation
     * @return printable application summary
     */
    @Override
    public String toString() {
        return "ApplicationID=" + id +
               ", Student=" + studentId +
               ", Internship=" + internshipId +
               ", Status=" + status +
               ", Accepted=" + studentAccepted;
    }
}
