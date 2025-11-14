
import java.util.Scanner;

import controller.LoginManager;
import model.Student;
import model.User;

public class StudentMain {
    public static void StudentMain(User user) {

        LoginManager lm = null;
        Scanner sc = new Scanner(System.in);

        if (user instanceof Student) {
            System.out.println("Welcome, " + user.getName());
            System.out.println("Student Menu\n" +
                            "=======================================\n" +
                            "1. View and Apply for Internship Opportunities\n" +
                            "2. View Internship Applications\n" +
                            "3. View Profile\n" +
                            "4. Change Password\n" +
                            "5. Logout");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            
            switch (choice) {
                case 1 -> System.out.println("view and apply");
                case 2 -> System.out.println("view int apps");
                case 3 -> System.out.println("view profile");
                case 4 -> lm.changePassword(user);
                case 5 -> System.out.println("logout");
            }

        }

       
        
    }

    public static void viewInternshipApps(Student student) {

    }
}
