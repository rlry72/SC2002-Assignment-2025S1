public class Student extends User {
    private int yearOfStudy;
    private String major;
    private Internship internship;

    public Student(String id, String name, String email, int year, String major) {
        super(id, name, email);
        yearOfStudy = year;
        this.major = major;
    }

    // apply for internship (max 3) -> need internship class?
}
