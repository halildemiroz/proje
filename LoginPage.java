import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class User {
    String username;
    String password;
}

public class LoginPage extends JFrame implements ActionListener {

    private JTextField userTextField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JLabel statusLabel;
    private List<User> users;

    public LoginPage() {
        loadUsersFromFile();

        setTitle("Login Page");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Username:"));
        userTextField = new JTextField();
        add(userTextField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            new RegisterPage();
            this.dispose();
        });
        add(registerButton);

        statusLabel = new JLabel();
        add(statusLabel);

        setVisible(true);
    }

    private void loadUsersFromFile() {
        users = new ArrayList<>();
        File file = new File("users.txt");
        
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    User user = new User();
                    user.username = parts[0];
                    user.password = parts[1];
                    users.add(user);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private boolean isValidLogin(String username, String password) {
        return users.stream().anyMatch(u -> u.username.equals(username) && u.password.equals(password));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userTextField.getText();
        String password = new String(passwordField.getPassword());

        if (isValidLogin(username, password)) {
            statusLabel.setText("Login successful!");
            new DashboardPage(username);
            this.dispose();
        } else {
            statusLabel.setText("Invalid credentials.");
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}

class DashboardPage extends JFrame implements ActionListener {
    private JTextField studentIdField, addressField;
    private JComboBox<String> genderComboBox;
    private JButton saveButton;
    private JLabel statusLabel;
    private String username;

    public DashboardPage() {
        this(null);
    }
    
    public DashboardPage(String username) {
        this.username = username;
        
        setTitle("Student Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Dashboard!", JLabel.CENTER);
        add(welcomeLabel);
        add(new JLabel());

        // Student ID
        add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        add(studentIdField);

        // Gender selection
        add(new JLabel("Gender:"));
        String[] genders = {"Select Gender", "Male", "Female"};
        genderComboBox = new JComboBox<>(genders);
        add(genderComboBox);

        // Address
        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        // Save button
        saveButton = new JButton("Save Information");
        saveButton.addActionListener(this);
        add(saveButton);

        // Status label
        statusLabel = new JLabel();
        add(statusLabel);
        
        // Load existing data if available
        loadStudentInfo();

        setVisible(true);
    }

    private void loadStudentInfo() {
        if (username == null) return;
        
        File file = new File("student_info.txt");
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4 && parts[0].equals(username)) {
                    // Found record for this username
                    studentIdField.setText(parts[1]);
                    
                    // Set gender in combo box
                    for (int i = 0; i < genderComboBox.getItemCount(); i++) {
                        if (genderComboBox.getItemAt(i).equals(parts[2])) {
                            genderComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    addressField.setText(parts[3]);
                    break;
                }
            }
        } catch (IOException e) {
            statusLabel.setText("Error loading your information: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveStudentInfo();
        }
    }

    private void saveStudentInfo() {
        String studentId = studentIdField.getText().trim();
        String gender = genderComboBox.getSelectedItem().toString();
        String address = addressField.getText().trim();
        
        // Validate inputs
        if (studentId.isEmpty()) {
            statusLabel.setText("Please enter a student ID");
            return;
        }
        
        if (gender.equals("Select Gender")) {
            statusLabel.setText("Please select a gender");
            return;
        }
        
        if (address.isEmpty()) {
            statusLabel.setText("Please enter an address");
            return;
        }
        
        // Read existing records
        List<String> lines = new ArrayList<>();
        boolean recordUpdated = false;
        File file = new File("student_info.txt");
        
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length >= 4 && parts[0].equals(username)) {
                        // Replace existing record
                        lines.add(username + ":" + studentId + ":" + gender + ":" + address);
                        recordUpdated = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                statusLabel.setText("Error reading student info: " + e.getMessage());
                return;
            }
        }
        
        // If record wasn't updated, add a new one
        if (!recordUpdated) {
            lines.add(username + ":" + studentId + ":" + gender + ":" + address);
        }
        
        // Write all records back to file
        try (FileWriter writer = new FileWriter(file)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            statusLabel.setText("Information saved successfully!");
        } catch (IOException e) {
            statusLabel.setText("Error saving information: " + e.getMessage());
        }
    }
}

class RegisterPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel statusLabel;

    public RegisterPage() {
        setTitle("Register");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerUser());
        add(registerButton);

        statusLabel = new JLabel();
        add(statusLabel);

        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        List<User> users = new ArrayList<>();
        File file = new File("users.txt");
        
        // Read existing users
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        User user = new User();
                        user.username = parts[0];
                        user.password = parts[1];
                        users.add(user);
                    }
                }
            } catch (IOException e) {
                statusLabel.setText("Error reading users file.");
                return;
            }
        }

        // Check if username already exists
        boolean exists = users.stream().anyMatch(u -> u.username.equals(username));
        if (exists) {
            statusLabel.setText("Username already exists.");
            return;
        }

        // Validate password strength
        if (password.length() < 8) {
            statusLabel.setText("Password must be at least 8 characters long.");
            return;
        }
        if (!password.matches(".*[A-Z].*")) {
            statusLabel.setText("Password must contain at least one uppercase letter.");
            return;
        }
        if (!password.matches(".*[a-z].*")) {
            statusLabel.setText("Password must contain at least one lowercase letter.");
            return;
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            statusLabel.setText("Password must contain at least one special character.");
            return;
        }

        // Add new user
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(username + ":" + password + "\n");
        } catch (IOException e) {
            statusLabel.setText("Error saving user.");
            return;
        }
    }
}
