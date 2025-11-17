package app;

import java.util.List;
import model.*;
import repository.UserRepository;

public class DataLoader {

    public static void loadInitialUsers(UserRepository userRepo) {
        List<Student> students = CsvParser.importStudents("sample_student_list.csv");
        List<Staff> staff = CsvParser.importStaff("sample_staff_list.csv");

        students.forEach(userRepo::save);
        staff.forEach(userRepo::save);

        System.out.println("Users loaded: " + (students.size() + staff.size()));
    }
}
