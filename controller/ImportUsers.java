package controller;
import java.util.ArrayList;
import java.util.List;

import model.Staff;
import model.Student;
import model.User;

import java.io.*;

public class ImportUsers {
    /**
     * high level function to import users from csv file
     * @return list of imported users
     */
    public static List<User> importUsersFromFile() {
        List<User> users = new ArrayList<>();
        users.addAll(importStudents("sample_student_list.csv"));
        users.addAll(importStaff("sample_staff_list.csv"));
        return users;
    }

    /**
     * import students from a file
     * @param filename name of file to read from
     * @return list of students imported
     */
    public static List<Student> importStudents(String filename) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                students.add(new Student(data[0], data[1], data[4], Integer.parseInt(data[3]), data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * import staff from a file
     * @param filename name of file to read from
     * @return list of staff imported
     */
    public static List<Staff> importStaff(String filename) {
        List<Staff> staffList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                staffList.add(new Staff(data[0], data[1], data[4], data[2], data[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staffList;
    }

}
