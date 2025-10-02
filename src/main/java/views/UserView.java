
package views;
import dao.DataDAO;
import model.Data;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserView {
    private String email;

    public UserView(String email) {
        this.email = email;
    }

    public void home() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        do {
            // Clear separator for better readability
            System.out.println("\n====================================");
            System.out.println("👋 Welcome, " + this.email);
            System.out.println("📁 File Management Dashboard");
            System.out.println("====================================");
            System.out.println("1️⃣  Show Hidden Files");
            System.out.println("2️⃣  Hide a New File");
            System.out.println("3️⃣  Unhide a File");
            System.out.println("0️⃣  Logout / Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number (0-3).");
                continue;
            }
            switch (choice) {
                case 1 -> showHiddenFiles();
                case 2 -> hideNewFile(sc);
                case 3 -> unhideFile(sc);
                case 0 -> System.out.println("👋 Logging out. Goodbye!");
                default -> System.out.println("❌ Invalid choice! Please try again.");
            }
        } while (choice != 0);
    }

    private void showHiddenFiles() {
        try {
            List<Data> files = DataDAO.getAllFiles(this.email);
            System.out.println("\n📂 Your Hidden Files:");
            if (files.isEmpty()) {
                System.out.println("No hidden files found.");
                return;
            }
            System.out.println("ID\tFile Name");
            System.out.println("---------------------");
            for (Data file : files) {
                System.out.println(file.getId() + "\t" + file.getFileName());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching files: " + e.getMessage());
        }
    }

    private void hideNewFile(Scanner sc) {
        System.out.print("\nEnter the full path of the file to hide: ");
        String path = sc.nextLine();
        File f = new File(path);
        if (!f.exists() || !f.isFile()) {
            System.out.println("❌ File not found. Please check the path.");
            return;
        }
        Data file = new Data(0, f.getName(), path, this.email);
        try {
            int result = DataDAO.hideFile(file);
            if (result > 0) {
                System.out.println("✅ File hidden successfully!");
            } else {
                System.out.println("❌ Failed to hide file.");
            }
        } catch (SQLException | IOException e) {
            System.out.println("❌ Error hiding file: " + e.getMessage());
        }
    }

    private void unhideFile(Scanner sc) {
        try {
            List<Data> files = DataDAO.getAllFiles(this.email);
            if (files.isEmpty()) {
                System.out.println("⚠️ No files available to unhide.");
                return;
            }

            System.out.println("\n📂 Your Hidden Files:");
            System.out.println("ID\tFile Name");
            System.out.println("---------------------");
            for (Data file : files) {
                System.out.println(file.getId() + "\t" + file.getFileName());
            }

            System.out.print("Enter the ID of the file to unhide: ");
            int id = Integer.parseInt(sc.nextLine());

            boolean valid = files.stream().anyMatch(f -> f.getId() == id);
            if (!valid) {
                System.out.println("❌ Invalid ID. Try again.");
                return;
            }

            DataDAO.unhide(id);
            System.out.println("✅ File unhidden successfully!");
        } catch (SQLException | IOException e) {
            System.out.println("❌ Error unhiding file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter a valid number.");
        }
    }
}
