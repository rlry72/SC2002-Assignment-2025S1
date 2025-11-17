package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import model.Staff;
import model.Student;

public class CsvParser {

    public static List<Student> importStudents(String filename) {
        List<Student> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line; 
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                list.add(new Student(d[0], d[1], d[4], Integer.parseInt(d[3]), d[2]));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static List<Staff> importStaff(String filename) {
        List<Staff> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line; 
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                list.add(new Staff(d[0], d[1], d[4], d[2], d[3]));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
