package model;

public class CompanyRepresentative extends User {
    private Company c;
    private String dept;
    private String position;
    private boolean isApproved;

    public CompanyRepresentative(String id, String name, String email, Company c, String dept, String position) {
        super(id, position, email);
        this.c = c;
        this.dept = dept;
        this.position = position;

        isApproved = false;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    public String getDept() {
        return dept;
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    @Override
    public String getLoginId() {
        return super.getEmail();
    }

    public Company getCompany() {
        return this.c;
    }

    public void setCompany(Company c) {
        this.c = c;
    }

    @Override
    public void setUserId(String userId) {
        super.setUserId(userId);
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
