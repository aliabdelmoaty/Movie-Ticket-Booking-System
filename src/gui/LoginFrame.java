package gui;

import javax.swing.*;
import java.awt.*;
import core.BookingSystem;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel title;
    private JLabel description;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Dark background panel
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(16, 22, 34)); // Dark Mode
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(28, 31, 39)); // Dark card
        form.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84),8,true)); // border dark
        form.setPreferredSize(new Dimension(400, 400));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 12, 8, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        // Title
        title = new JLabel("Welcome Back");
        title.setFont(new Font("Spline Sans", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        c.gridy = 0;
        form.add(title, c);

        // Description
        description = new JLabel("Sign in to book your favorite seats instantly.");
        description.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        description.setForeground(new Color(156, 163, 175)); // gray text
        c.gridy++;
        form.add(description, c);

        // Email label and field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(Color.WHITE);
        c.gridy++;
        form.add(emailLabel, c);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        emailField.setForeground(Color.WHITE);
        emailField.setBackground(new Color(28, 31, 39));
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 5, true)); 

        c.gridy++;
        form.add(emailField, c);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        c.gridy++;
        form.add(passwordLabel, c);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(28, 31, 39));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 5, true)); // radius 15
        c.gridy++;
        form.add(passwordField, c);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        loginBtn.setBackground(new Color(19, 91, 236)); // primary color
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        
        loginBtn.addActionListener(e -> handleLogin());
        c.gridy++;
        form.add(loginBtn, c);

        // don't have an account? register
        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        c.gridy++;
        form.add(registerLabel, c);
        // register button
        JButton goRegister = new JButton("Create account");
        goRegister.setFont(new Font("Spline Sans", Font.BOLD, 14));
        goRegister.setBackground(new Color(59, 67, 84)); // secondary dark
        goRegister.setForeground(Color.WHITE);
        goRegister.setFocusPainted(false);
        goRegister.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
        c.gridy++;
        form.add(goRegister, c);



        // Add form to background panel
        backgroundPanel.add(form);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BookingSystem bookingSystem = BookingSystem.getInstance();
        
        if (bookingSystem.login(email, password)) {
            JOptionPane.showMessageDialog(this,
                "Welcome back, " + bookingSystem.getCurrentUser().getName() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new BookingFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid email or password!",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

   
}
