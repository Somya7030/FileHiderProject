package views;

import dao.UserDAO;
import model.User;
import service.GenerateOTP;
import service.SendOTPService;
import service.UserService;

import java.sql.SQLException;
import java.util.Scanner;

public class Welcome {

    public void welcomeScreen() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (true) {
            System.out.println("\n=== Welcome to File Enc Dashboard===");
            System.out.println("1️⃣  Login");
            System.out.println("2️⃣  Signup");
            System.out.println("0️⃣  Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number (0-2).");
                continue;
            }

            switch (choice) {
                case 1 -> login(sc);
                case 2 -> signUp(sc);
                case 0 -> {
                    System.out.println("👋 Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }

    private void login(Scanner sc) {
        System.out.print("Enter your email: ");
        String email = sc.nextLine().trim();

        try {
            if (!UserDAO.isExists(email)) {
                System.out.println("⚠️ User not found. Please signup first.");
                return;
            }

            // Generate and send OTP
            String genOTP = GenerateOTP.getOTP();
            SendOTPService.sendOTP(email, genOTP);

            // Ask user to enter OTP (no OTP displayed!)
            System.out.print("Enter the OTP sent to your email: ");
            String otp = sc.nextLine().trim();

            if (otp.equals(genOTP)) {
                System.out.println("✅ Login successful!");
                new UserView(email).home();
            } else {
                System.out.println("❌ Wrong OTP. Login failed.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    private void signUp(Scanner sc) {
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = sc.nextLine().trim();

        try {
            if (UserDAO.isExists(email)) {
                System.out.println("⚠️ User already exists. Please login instead.");
                return;
            }

            // Generate and send OTP (do NOT display in console)
            String genOTP = GenerateOTP.getOTP();
            SendOTPService.sendOTP(email, genOTP);

            // Ask user to enter OTP
            System.out.print("Enter the OTP sent to your email: ");
            String otp = sc.nextLine().trim();

            if (otp.equals(genOTP)) {
                User user = new User(name, email);
                int response = UserService.saveUser(user);

                if (response > 0) {
                    System.out.println("✅ Signup successful! Welcome, " + name);
                    new UserView(email).home();
                } else {
                    System.out.println("❌ Unexpected error! Could not register user.");
                }

            } else {
                System.out.println("❌ Wrong OTP. Signup failed.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}
