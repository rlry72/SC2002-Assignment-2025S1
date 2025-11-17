package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import model.Staff;
import model.Student;
import repository.UserRepository;

public class LoadUsers {

    public static void load(UserRepository repo) {
        loadStudents("sample_student_list.csv", repo);
        loadStaff("sample_staff_list.csv", repo);
    }

    public static void loadStudents(String filename, UserRepository repo) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Student s = new Student(data[0], data[1], data[4], Integer.parseInt(data[3]), data[2]);
                repo.save(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadStaff(String filename, UserRepository repo) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Staff s = new Staff(data[0], data[1], data[4], data[2], data[3]);
                repo.save(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
