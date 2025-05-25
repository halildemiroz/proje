import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class UserApp extends JFrame {
    CardLayout card = new CardLayout();
    JPanel mainPanel = new JPanel(card);

    JTextField loginUsername = new JTextField(15);
    JPasswordField loginPassword = new JPasswordField(15);
    JLabel loginStatus = new JLabel();

    JTextField registerUsername = new JTextField(15);
    JPasswordField registerPassword = new JPasswordField(15);
    JPasswordField registerConfirmPassword = new JPasswordField(15);
    JTextArea registerStatus = new JTextArea();

    JLabel welcomeLabel = new JLabel("Welcome!");

    // New fields for additional user info
    JTextField studentNumberField = new JTextField(15);
    JRadioButton maleRadio = new JRadioButton("Male");
    JRadioButton femaleRadio = new JRadioButton("Female");
    ButtonGroup genderGroup = new ButtonGroup();
    JTextField nameField = new JTextField(15);
    JComboBox<String> departmentBox = new JComboBox<>(new String[]{"Computer", "Software", "Math"});
    JLabel infoStatus = new JLabel();

    String currentUser = "";

    public UserApp() {
        setTitle("User Login Application");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(5, 1));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(loginUsername);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(loginPassword);
        JButton loginBtn = new JButton("Login");
        JButton toRegisterBtn = new JButton("Register");
        loginPanel.add(loginBtn);
        loginPanel.add(toRegisterBtn);
        loginPanel.add(loginStatus);

        // Register Panel
        JPanel registerPanel = new JPanel(new GridLayout(5, 1));
        registerPanel.add(new JLabel("New Username:"));
        registerPanel.add(registerUsername);
        registerPanel.add(new JLabel("New Password"));
        registerPanel.add(registerPassword);
        registerPanel.add(new JLabel("Confirm Password"));
        registerPanel.add(registerConfirmPassword);
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        registerStatus.setLineWrap(true);
        registerStatus.setWrapStyleWord(true);
        registerPanel.add(registerBtn);
        registerPanel.add(backBtn);
        registerPanel.add(registerStatus);

        // Admin Panel
        JPanel adminPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        JButton deleteUserButton = new JButton("Delete Selected User");
        JButton adminBackButton = new JButton("Back to Login");
    
        JPanel adminButtonsPanel = new JPanel(new FlowLayout());
        adminButtonsPanel.add(deleteUserButton);
        adminButtonsPanel.add(adminBackButton);

        adminPanel.add(new JLabel("Admin Panel - User Management", SwingConstants.CENTER), BorderLayout.NORTH);
        adminPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        adminPanel.add(adminButtonsPanel, BorderLayout.SOUTH);
    
        // Add action listener to admin back button
        adminBackButton.addActionListener(e -> {
            loginUsername.setText("");
            loginPassword.setText("");
            loginStatus.setText("");
            card.show(mainPanel, "login");
        });

        // Welcome Panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.add(welcomeLabel);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(10, 1));
        infoPanel.add(new JLabel("Student Number:"));
        infoPanel.add(studentNumberField);

        infoPanel.add(new JLabel("Gender:"));
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        infoPanel.add(genderPanel);

        infoPanel.add(new JLabel("Full Name:"));
        infoPanel.add(nameField);

        infoPanel.add(new JLabel("Department:"));
        infoPanel.add(departmentBox);

        JButton submitInfoBtn = new JButton("Submit");
        infoPanel.add(submitInfoBtn);
        infoPanel.add(infoStatus);

        // Panels to CardLayout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(adminPanel, "admin");
        mainPanel.add(infoPanel, "info");

        add(mainPanel);

        // Button actions
        loginBtn.addActionListener(e -> login());
        toRegisterBtn.addActionListener(e -> card.show(mainPanel, "register"));
        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> card.show(mainPanel, "login"));
        submitInfoBtn.addActionListener(e -> submitUserInfo());

        setVisible(true);
    }

    void login() {
        String username = loginUsername.getText();
        String password = new String(loginPassword.getPassword());
        
        if(username.equals("admin") && password.equals("admin")){
            currentUser = "ADMIN";
            loginStatus.setText("");
            loadUserList();
            card.show(mainPanel, "admin");
            return;
        }

        if (checkUser(username, password)) {
            currentUser = username;
            card.show(mainPanel, "info");
        } else {
            loginStatus.setText("Login failed!");
        }
    }

    void register() {
        String username = registerUsername.getText();
        String password = new String(registerPassword.getPassword());
        String confirmPassword = new String(registerConfirmPassword.getPassword());

        if(!password.equals(confirmPassword)){
            registerStatus.setText("Passwords do not match!");
            return;
        }

        if (password.length() < 8) {
            registerStatus.setText("Password too short.");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            registerStatus.setText("Password must contain at least one uppercase letter.");
            return;
        }
        
        if(!password.matches(".*[a-z].*")){
            registerStatus.setText("Password must contain at least one lowercase letter.");
            return;
        }
        if(!password.matches(".*[^a-zA-Z0-9].*")){
            registerStatus.setText("Password must contain at least one special character.");
            return;
        }
        
        if(!password.matches(".*[0-9].*")){
            registerStatus.setText("Password must contain at least one number.");
            return;
        }

        if (checkUser(username, password)) {
            registerStatus.setText("User already exists.");
            return;
        }

        try (FileWriter fw = new FileWriter("users.txt", true)) {
            fw.write(username + ":" + password + "\n");
            registerStatus.setText("Registration successful!");
        } catch (IOException e) {
            registerStatus.setText("File error.");
        }
    }

    void loadUserList(){
        JPanel adminPanel = (JPanel) mainPanel.getComponent(3);
        JScrollPane scrollPane = (JScrollPane) adminPanel.getComponent(1);
        @SuppressWarnings("unchecked")
        JList<String> userList = (JList<String>) scrollPane.getViewport().getView();
        DefaultListModel<String> model = getUserListModel(userList);

        model.clear();

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if(parts.length >= 2) {
                    model.addElement(parts[0]);
                }
            }
        } catch(IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }

        JPanel buttonPanel = (JPanel) adminPanel.getComponent(2); 
        JButton deleteButton = (JButton) buttonPanel.getComponent(0);

        for (ActionListener al : deleteButton.getActionListeners()) {
            deleteButton.removeActionListener(al);
        }

        deleteButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "Please select a user first.");
                return;
            }
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(
                this, "Delete user '" + selectedUser + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            
            // Delete from users.txt
            deleteUserFromFile(selectedUser, "users.txt");
            
            // Delete from userinfo.txt if exists
            deleteUserFromFile(selectedUser, "userinfo.txt");
            
            // Reload list
            loadUserList();
        });
    }

    private DefaultListModel<String> getUserListModel(JList<String> userList) {
        return (DefaultListModel<String>) userList.getModel();
    }

    private void deleteUserFromFile(String username, String filename) {
        File file = new File(filename);
        if (!file.exists()) return;
        
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 1 && !parts[0].equals(username)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    boolean checkUser(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File might not exist yet
        }
        return false;
    }

    void submitUserInfo() {
        String studentNumber = studentNumberField.getText();
        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
        String fullName = nameField.getText();
        String department = (String) departmentBox.getSelectedItem();

        if (studentNumber.isEmpty() || gender.isEmpty() || fullName.isEmpty()) {
            infoStatus.setText("Please fill all fields.");
            return;
        }

        try (FileWriter fw = new FileWriter("userinfo.txt", true)) {
            fw.write(currentUser + ":" + studentNumber + ":" + gender + ":" + fullName + ":" + department + "\n");
            infoStatus.setText("Info saved successfully!");
            welcomeLabel.setText("Welcome, " + fullName + "!");
            card.show(mainPanel, "welcome");
        } catch (IOException e) {
            infoStatus.setText("File error.");
        }
    }

    public static void main(String[] args) {
        new UserApp();
    }
}
