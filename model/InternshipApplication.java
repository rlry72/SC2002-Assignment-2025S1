package model;

public class InternshipApplication {
    private String id;
    private String studentId;
    private String internshipId;
    public enum Status {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        WITHDRAWN
    }
    private Status status;
    private boolean studentAccepted;

    public InternshipApplication(String id, String studentId, String internshipId) {
        this.id = id;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.status = Status.PENDING;
        this.studentAccepted = false;
    }

    public String getId() {
        return id;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public Status getStatus() {
        return status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean studentAccepted() {
        return this.studentAccepted;
    }

    public void accept() {
        this.studentAccepted = true;
    }

    public boolean isActive() {
        return status == Status.PENDING || status == Status.SUCCESSFUL;
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

}
