package src.kr.ac.jbnu.se.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class ScorePanel extends JFrame implements ActionListener {
    final JButton backButton;
    MainMenu mainMenu;
    private String id;
    private final JLabel scoreLabel;
    private final JTextField userIdField;
    private int userScore;
    private ArrayList<User> topScores = new ArrayList<>(); // 초기화된 ArrayList
    private final SoundManager buttonClickSound; // 버튼 클릭시 효과음을 위한 인스턴스

    private Image backgroundImage; // 이미지를 저장할 변수 추가


    ScorePanel(String id){
        buttonClickSound = new SoundManager("src/sounds/button_click.wav"); // 버튼 클릭 초기화


        try {
            // 이미지 파일 경로 설정
            File imgFile = new File("src/images/Ranking.png"); // 이미지 파일 경로로 변경해야 함

            // 이미지 파일을 읽어와 배경 이미지로 설정
            backgroundImage = ImageIO.read(imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        });
        this.revalidate();
        this.repaint();

        // frame setup
        this.id = id;
        this.setTitle("Tetris"); // app title
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 700);// window dimensions
        this.getContentPane().setBackground(Color.white);
        this.setLocationRelativeTo(null);

        // logo setup
        ImageIcon logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("src/images/logo.png")));
        this.setIconImage(logo.getImage());

        // 사용자 ID 레이블
        JLabel userIdLabel = new JLabel("사용자 ID:");
        userIdLabel.setBounds(100, 200, 100, 30);
        userIdLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정
        this.add(userIdLabel);

        // 사용자 ID 입력 필드
        userIdField = new JTextField(id);
        userIdField.setBounds(200, 200, 150, 30);
        this.add(userIdField);

        // 스코어 레이블
        scoreLabel = new JLabel("스코어: " + userScore);
        scoreLabel.setBounds(100, 250, 100, 30);
        scoreLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정
        this.add(scoreLabel);

        // 스코어 불러오기 버튼
        JButton loadScoreButton = getScoreButton();

        //backButton setup
        backButton = new JButton("Back to Main-Menu");
        backButton.setFocusable(false);
        backButton.setBounds(370, 500, 150, 50);
        Color goldColor = new Color(255, 215, 0); // RGB 값으로 금색 만들기
        backButton.setBackground(goldColor); // 버튼 색상을 금색으로 설정
        backButton.addActionListener(this);


        //adding elements to the frame
        this.setLayout(null);
        this.add(backButton);
        this.setVisible(true);
        this.add(loadScoreButton);

        // Firebase에서 사용자 스코어 가져오기
        fetchUserScore();

        // 스코어 레이블 업데이트
        updateScoreLabel();


        fetchTopScores(); // 최고 점수 순위 정보 가져오기
        // JTable을 사용하여 최고 점수 순위와 스코어 표시
        String[] columnNames = {"순위", "사용자 ID", "스코어"};
        // Reinitialize topScores
        topScores = FirebaseUtil.getTopScores(50);

        // JTable 설정
        Object[][] data = new Object[topScores.size()][3];
        for (int i = 0; i < topScores.size(); i++) {
            data[i][0] = i + 1; // 순위
            data[i][1] = topScores.get(i).id(); // 사용자 ID
            data[i][2] = topScores.get(i).score(); // 스코어
        }

        JTable scoreTable = new JTable(data, columnNames);
        scoreTable.setBounds(450, 200, 400, 180);

        // JScrollPane로 JTable 래핑
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setBounds(400, 200, 400, 180);

        //글자
        JLabel header = new JLabel();
        header.setBounds(400, 100, 400, 100);
        header.setForeground(Color.black);
        header.setFont(new Font("MV Boli", Font.PLAIN, 80));

        this.add(header);
        this.add(scrollPane);

    }

    private JButton getScoreButton() {
        JButton loadScoreButton = new JButton("Enter");
        loadScoreButton.setBounds(280, 250, 70, 30);
        Color goldColor1 = new Color(255, 215, 0); // RGB 값으로 금색 만들기
        loadScoreButton.setBackground(goldColor1); // 버튼 색상을 금색으로 설정
        loadScoreButton.addActionListener(e -> {
            buttonClickSound.play(); // 효과음 재생
            fetchUserScore(); // 사용자 스코어 불러오기
            updateScoreLabel(); // 스코어 레이블 업데이트
        });
        return loadScoreButton;
    }

    // Firebase에서 사용자 스코어 가져오기
    private void fetchUserScore() {
        id = userIdField.getText(); // 사용자가 입력한 ID를 가져옴
            userScore = FirebaseUtil.getUserScore(id);
            updateScoreLabel();
        }


    // 가져온 사용자 스코어로 스코어 레이블 업데이트
    private void updateScoreLabel() {
        scoreLabel.setText("스코어: " + userScore);
    }
    private void fetchTopScores() {
        // FirebaseUtil 클래스를 사용하여 최고 점수 순위 정보 가져오기
        topScores = FirebaseUtil.getTopScores(50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        buttonClickSound.play(); // 효과음 재생
        //go back to main-menu if backButton pressed
        if(e.getSource() == backButton){
            mainMenu = new MainMenu(id);
            this.setVisible(false); // sound 겹침 오류 해결
            mainMenu.setVisible(true);
        }
    }

}
