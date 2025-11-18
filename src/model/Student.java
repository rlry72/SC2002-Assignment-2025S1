package model;

/**
 * student user entity that extends base user
 */
public class Student extends User {

    /**
     * year of study for student (1-4)
     */
    private int yearOfStudy;

    /**
     * major of student
     */
    private String major;


    /**
     * create student with id, name, email, year of study and major
     * @param id unique id of student (e.g. U1234567A)
     * @param name student name
     * @param email student email
     * @param year year of study (1 to 4)
     * @param major student's major field
     */
    public Student(String id, String name, String email, int year, String major) {
        super(id, name, email);
        yearOfStudy = year;
        this.major = major;
    }

    /**
     * get login id used by student when logging in
     * @return student user id
     */
    @Override
    public String getLoginId() {
        return super.getUserId();
    }

    /**
     * get student's major
     * @return major of student
     */
    public String getMajor() {
        return major;
    }

    /**
     * get student's year of study
     * @return year of study
     */
    public int getYearOfStudy() {
        return yearOfStudy;
    }

    /**
     * update student's email
     * @param email new email value
     */
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    /**
     * update student's name
     * @param name new name value
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * update student's user id
     * @param userId new user id value
     */
    @Override
    public void setUserId(String userId) {
        super.setUserId(userId);
    }

    /**
     * update student's major
     * @param major new major value
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * update student's year of study
     * @param yearOfStudy new year value
     */
    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
}
