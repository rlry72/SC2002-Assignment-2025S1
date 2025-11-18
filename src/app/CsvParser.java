package app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.Staff;
import model.Student;

/**
 * utility class for reading CSV files from classpath and converting
 * records into domain model objects. Files must be placed under
 * src/resources/ or any classpath-accessible directory.
 */
public class CsvParser {

    /**
     * read student records from a CSV file and convert them into Student objects.
     * filename should not contain full path, only resource name.
     * e.g. CsvParser.importStudents("sample_student_list.csv");
     */
    public static List<Student> importStudents(String filename) {
        List<Student> students = new ArrayList<>();

        try (InputStream is = CsvParser.class.getResourceAsStream("/" + filename)) {
            
            if (is == null) {
                System.err.println("Resource not found: " + filename);
                return students;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                br.readLine(); // skip header

                while ((line = br.readLine()) != null) {
                    String[] d = line.split(",", -1);
                    if (d.length < 5) {
                        System.err.println("Skipping invalid student row: " + line);
                        continue;
                    }

                    try {
                        int year = Integer.parseInt(d[3].trim());
                        students.add(new Student(
                            d[0].trim(),
                            d[1].trim(),
                            d[4].trim(),
                            year,
                            d[2].trim()
                        ));
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid year, skipping row: " + line);
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("Failed to read student CSV: " + ex.getMessage());
        }

        return students;
    }

    /**
     * read staff records from classpath resource
     */
    public static List<Staff> importStaff(String filename) {
        List<Staff> staffList = new ArrayList<>();

        try (InputStream is = CsvParser.class.getResourceAsStream("/" + filename)) {
            
            if (is == null) {
                System.err.println("Resource not found: " + filename);
                return staffList;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                br.readLine(); // skip header

                while ((line = br.readLine()) != null) {
                    String[] d = line.split(",", -1);
                    if (d.length < 5) {
                        System.err.println("Skipping invalid staff row: " + line);
                        continue;
                    }

                    staffList.add(new Staff(
                        d[0].trim(),
                        d[1].trim(),
                        d[4].trim(),
                        d[2].trim(),
                        d[3].trim()
                    ));
                }
            }

        } catch (Exception ex) {
            System.err.println("Failed to read staff CSV: " + ex.getMessage());
        }

        return staffList;
    }
}
