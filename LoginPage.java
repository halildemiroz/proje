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
    private JTextField studentIdField, nameField;
    private JTextArea addressArea;
    private JScrollPane addressScrollPane;
    private JRadioButton maleRadio, femaleRadio, otherRadio;
    private ButtonGroup genderGroup;
    private JPanel genderPanel;
    private JComboBox<String> departmentComboBox;
    private JButton saveButton;
    private JLabel statusLabel;
    private String username;

    public DashboardPage() {
        this(null);
    }
    
    public DashboardPage(String username) {
        this.username = username;
        
        setTitle("Student Dashboard");
        setSize(500, 400);  // Made larger to accommodate new fields
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));  // Changed from 5 to 7 rows

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Dashboard!", JLabel.CENTER);
        add(welcomeLabel);
        add(new JLabel());

        // Name field
        add(new JLabel("Full Name:"));
        nameField = new JTextField();
        add(nameField);

        // Student ID
        add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        add(studentIdField);

        // Gender selection with radio buttons
        add(new JLabel("Gender:"));
        genderPanel = new JPanel();
        genderGroup = new ButtonGroup();
        
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        otherRadio = new JRadioButton("Other");
        
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);
        add(genderPanel);
        
        // Department selection
        add(new JLabel("Department:"));
        String[] departments = {"Select Department", "Computer Engineering", "Software Engineering", 
                               "Mathematics", "Physics", "Engineering"};
        departmentComboBox = new JComboBox<>(departments);
        add(departmentComboBox);

        // Address with scrollpane
        add(new JLabel("Address:"));
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(addressScrollPane);

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
                if (parts.length >= 6 && parts[0].equals(username)) {
                    // Found record for this username
                    nameField.setText(parts[1]);
                    studentIdField.setText(parts[2]);
                    
                    // Set gender radio button
                    String gender = parts[3];
                    if (gender.equals("Male")) {
                        maleRadio.setSelected(true);
                    } else if (gender.equals("Female")) {
                        femaleRadio.setSelected(true);
                    } else {
                        otherRadio.setSelected(true);
                    }
                    
                    // Set department
                    String department = parts[4];
                    for (int i = 0; i < departmentComboBox.getItemCount(); i++) {
                        if (departmentComboBox.getItemAt(i).equals(department)) {
                            departmentComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    addressArea.setText(parts[5]);
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

    private String getSelectedGender() {
        if (maleRadio.isSelected()) {
            return "Male";
        } else if (femaleRadio.isSelected()) {
            return "Female";
        } else if (otherRadio.isSelected()) {
            return "Other";
        } else {
            return "";
        }
    }

    private void saveStudentInfo() {
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String gender = getSelectedGender();
        String department = departmentComboBox.getSelectedItem().toString();
        String address = addressArea.getText().trim();
        
        // Validate inputs
        if (name.isEmpty()) {
            statusLabel.setText("Please enter your full name");
            return;
        }
        
        if (studentId.isEmpty()) {
            statusLabel.setText("Please enter a student ID");
            return;
        }
        
        if (gender.isEmpty()) {
            statusLabel.setText("Please select a gender");
            return;
        }
        
        if (department.equals("Select Department")) {
            statusLabel.setText("Please select a department");
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
                    if (parts.length >= 6 && parts[0].equals(username)) {
                        // Replace existing record
                        lines.add(username + ":" + name + ":" + studentId + ":" + gender + ":" + 
                                 department + ":" + address.replace("\n", "\\n"));
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
            lines.add(username + ":" + name + ":" + studentId + ":" + gender + ":" + 
                     department + ":" + address.replace("\n", "\\n"));
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
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton;
    private JLabel statusLabel;

    public RegisterPage() {
        setTitle("Register");
        setSize(350, 250);  // Made taller to accommodate new field
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));  // Changed from 4 to 5 rows

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        
        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerUser());
        add(registerButton);
        
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            new LoginPage();
            this.dispose();
        });
        add(backButton);

        statusLabel = new JLabel();
        add(statusLabel);
        add(new JLabel());  // Empty label for layout balance

        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match!");
            return;
        }

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
            statusLabel.setText("Registration successful!");
            
            // Automatically go to login page after 2 seconds
            Timer timer = new Timer(2000, e -> {
                new LoginPage();
                this.dispose();
            });
            timer.setRepeats(false);
            timer.start();
            
        } catch (IOException e) {
            statusLabel.setText("Error saving user.");
        }
    }
}
