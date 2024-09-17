package src.kr.ac.jbnu.se.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegisterFrame extends JFrame {
    private final JTextField idField;
    private final JPasswordField pwField;
    private final SoundManager buttonClickSound; // 버튼 클릭시 효과음을 위한 인스턴스

    public RegisterFrame(LoginFrame loginFrame) {
        buttonClickSound = new SoundManager("src/sounds/button_click.wav"); // 버튼 클릭 초기화

        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel idLabel = new JLabel("ID: ");
        idField = new JTextField(10);
        idPanel.add(idLabel);
        idPanel.add(idField);
        idPanel.setOpaque(false); // 배경 투명 설정

        JPanel pwPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel pwLabel = new JLabel("PW: ");
        pwField = new JPasswordField(10);
        pwPanel.add(pwLabel);
        pwPanel.add(pwField);
        pwPanel.setOpaque(false); // 배경 투명 설정


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setOpaque(false); // 배경 투명 설정

        try {
            // 이미지 파일 경로에 맞게 수정
            Image backgroundImage = ImageIO.read(new File("src/images/Register2.png"));

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


        registerButton.addActionListener(e -> {
            buttonClickSound.play(); // 효과음 재생
            String id = idField.getText();
            String pw = String.valueOf(pwField.getPassword());
            int score = 0;

            try {
                String validPassword = FirebaseUtil.validateUserId(id);
                if (id.equals(validPassword)) {
                    JOptionPane.showMessageDialog(null, "이미 사용중인 아이디입니다.");
                } else {
                    FirebaseUtil.addUser(new User(id, pw, score));

                    JOptionPane.showMessageDialog(null, "회원 가입이 완료되었습니다.");
                    loginFrame.setVisible(true);
                    dispose();
                }
            } catch (ExecutionException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });


        cancelButton.addActionListener(e -> {
            buttonClickSound.play(); // 효과음 재생
            loginFrame.setVisible(true);
            dispose();
        });

        add(idPanel, BorderLayout.NORTH);
        add(pwPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
