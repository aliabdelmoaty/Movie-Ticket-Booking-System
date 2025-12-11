package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel title;
    private JLabel description;

    public RegisterFrame() {
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
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
        form.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(59, 67, 84), 8, true),
                        new EmptyBorder(20, 24, 20, 24)
                )
        ); // border dark with inner padding
        form.setPreferredSize(new Dimension(550, 600));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 0, 8, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;

        // Title
        title = new JLabel("Create Account");
        title.setFont(new Font("Spline Sans", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        c.gridy = 0;
        form.add(title, c);

        // Description
        description = new JLabel("Create an account to book your favorite seats instantly.");
        description.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        description.setForeground(new Color(156, 163, 175)); // gray text
        c.gridy++;
        form.add(description, c);
        
        // Name label and field
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setForeground(Color.WHITE);
        c.gridy++;
        form.add(nameLabel, c);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        nameField.setForeground(Color.WHITE);
        nameField.setBackground(new Color(28, 31, 39));
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 5, true));
        c.gridy++;
        form.add(nameField, c);

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
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        c.gridy++;
        form.add(usernameLabel, c);
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBackground(new Color(28, 31, 39));
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 5, true));
        c.gridy++;
        form.add(usernameField, c);

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
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 5, true));
        c.gridy++;
        form.add(passwordField, c);

        // Register button
        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        registerBtn.setBackground(new Color(19, 91, 236)); // primary color
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> handleRegister());
        c.gridy++;
        form.add(registerBtn, c);
        // do you have an account? login
        JLabel loginLabel = new JLabel("Do you have an account?");
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        c.gridy++;
        form.add(loginLabel, c);
        // login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        loginBtn.setBackground(new Color(59, 67, 84)); // secondary dark
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        c.gridy++;
        form.add(loginBtn, c);
        // Add form to background panel
        GridBagConstraints rootConstraints = new GridBagConstraints();
        rootConstraints.gridx = 0;
        rootConstraints.gridy = 0;
        rootConstraints.weightx = 1;
        rootConstraints.weighty = 1;
        // rootConstraints.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(form, rootConstraints);
    }

    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        JOptionPane.showMessageDialog(this, "Registered as " + name + " " + email + " " + username + " " + password);
    }
}