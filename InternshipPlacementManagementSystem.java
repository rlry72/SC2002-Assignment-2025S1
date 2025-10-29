import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InternshipPlacementManagementSystem {
    public static void main(String[] args) {
        List<User> users = ImportUsers.importUsersFromFile();
        User currentLoggedInUser = null;
        LoginManager loginManager = new LoginManager(users);

        // for (User user : users) {
        //     System.out.println("User: " + user.getClass().getSimpleName() + " " + user.getName());
        // }
        
        Scanner sc = new Scanner(System.in);

        System.out.print("Login: please enter user type: ");
        String userType = sc.nextLine();
        currentLoggedInUser = loginManager.login(userType);

        if (currentLoggedInUser != null)
            System.out.println("Current logged in user: Type: " + currentLoggedInUser.getClass().getSimpleName() + ", Name: " + currentLoggedInUser.getName() + ", is logged in: " + currentLoggedInUser.isLoggedIn());
    }

}
