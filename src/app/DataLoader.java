// package app;

// import java.util.List;
// import model.*;
// import repository.UserRepository;

// public class DataLoader {

//     public static void loadInitialUsers(UserRepository userRepo) {
//         List<Student> students = CsvParser.importStudents("sample_student_list.csv");
//         List<Staff> staff = CsvParser.importStaff("sample_staff_list.csv");

//         students.forEach(userRepo::save);
//         staff.forEach(userRepo::save);

//         System.out.println("Users loaded: " + (students.size() + staff.size()));
//     }
// }

package app;

import java.util.List;
import model.Staff;
import model.Student;
import repository.UserRepository;

/**
 * utility class responsible for loading initial user records from CSV files
 * typically invoked during system startup to prepopulate in-memory repositories
 */
public final class DataLoader {

    /** prevent instantiation of utility class */
    private DataLoader() { }

    /**
     * load initial student and staff records from CSV sources and store them in repository
     * output files must be located relative to application execution directory
     *
     * @param userRepo central repository where loaded user accounts are saved
     */
    public static void loadInitialUsers(UserRepository userRepo) {
        List<Student> students = CsvParser.importStudents("sample_student_list.csv");
        List<Staff> staff     = CsvParser.importStaff("sample_staff_list.csv");

        students.forEach(userRepo::save);
        staff.forEach(userRepo::save);

        System.out.printf("Initial user data loaded: Students: %d, Staff: %d, Total: %d%n",
                students.size(), staff.size(), students.size() + staff.size());
    }
}
