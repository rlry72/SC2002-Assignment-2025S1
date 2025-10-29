import java.util.Scanner;

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
        if (staffId.equals(this.getUserId()) && password.equals(this.getPassword()))
            return true;
        else
            return false;
    }
    
    // apply for internship (max 3) -> need internship class?
}
