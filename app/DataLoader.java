package app;

import java.util.List;
import model.*;
import repository.UserRepository;

public class DataLoader {

    public static void loadInitialUsers(UserRepository userRepo) {
        List<Student> students = CsvParser.importStudents("students.csv");
        List<Staff> staff = CsvParser.importStaff("staff.csv");

        students.forEach(userRepo::save);
        staff.forEach(userRepo::save);

        System.out.println("Users loaded: " + (students.size() + staff.size()));
    }
}
