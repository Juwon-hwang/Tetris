package src.kr.ac.jbnu.se.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class LoginFrame extends JFrame {

    private final JTextField idField;
    private final JPasswordField pwField;
    private final SoundManager buttonClickSound; // 버튼 클릭시 효과음을 위한 인스턴스

    public LoginFrame() {
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buttonClickSound = new SoundManager("src/sounds/button_click.wav"); // 버튼 클릭 초기화

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));//로그인
        JLabel idLabel = new JLabel("ID: ");
        idField = new JTextField(10);
        idPanel.add(idLabel);
        idPanel.add(idField);
        setTitle("Login");
        idPanel.setOpaque(false); // 배경 투명 설정

        JPanel pwPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel pwLabel = new JLabel("PW: ");
        pwField = new JPasswordField(10);
        pwPanel.add(pwLabel);
        pwPanel.add(pwField);
        pwPanel.setOpaque(false); // 배경 투명 설정

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.setOpaque(false); // 배경 투명 설정

        try {
            // 이미지 파일 경로에 맞게 수정
            Image backgroundImage = ImageIO.read(new File("src/images/Login.png"));

            setContentPane(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // 이미지를 불러오지 못했을 때 대체할 기본 설정
            getContentPane().setBackground(Color.LIGHT_GRAY);
        }


        loginButton.addActionListener(e -> {
            buttonClickSound.play(); // 효과음 재생
            String id = idField.getText();
            String pw = String.valueOf(pwField.getPassword());

            try {
                String validPassword = FirebaseUtil.validateUserPassword(id);
                if (pw.equals(validPassword)) {
                    openMainFrame(id);
                } else {
                    JOptionPane.showMessageDialog(null, "로그인 실패");
                }
            } catch (ExecutionException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        registerButton.addActionListener(e -> {
            buttonClickSound.play(); // 효과음 재생
            openRegisterFrame();
        });

        add(idPanel, BorderLayout.NORTH);
        add(pwPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void openMainFrame(String id) {
        MainMenu mainFrame = new MainMenu(id);
        setVisible(false);
        mainFrame.setVisible(true);
    }

    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        setVisible(false);
        registerFrame.setVisible(true);
    }
}