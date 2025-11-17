package model;
// import java.util.Scanner;

public class Student extends User {
    private int yearOfStudy;
    private String major;
    private Internship internship;

    public Student(String id, String name, String email, int year, String major) {
        super(id, name, email);
        yearOfStudy = year;
        this.major = major;
    }



    public boolean checkLoginInfo(String staffId, String password) {
        if (staffId.equals(this.getUserId()) && this.validatePassword(password))
            return true;
        else
            return false;
    }

    public String getLoginId() {
        return super.getUserId();
    }

    public String getMajor() {
        return major;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }
    
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setUserId(String userId) {
        super.setUserId(userId);
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public Internship getInternship() {
        return internship;
    }

    public void setInternship(Internship internship) {
        this.internship = internship;
    }
    // apply for internship (max 3) -> need internship class?
}
