package src.kr.ac.jbnu.se.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainMenu extends JFrame implements ActionListener {

    private final String id;
    private final SoundManager backgroundMusic;
    private final SoundManager buttonClickSound;

    private final JButton singlePlayerButton;
    private final JButton multiPlayerButton;
    private final JButton scoreboardButton;
    private final JButton aboutButton;
    private final JButton settingsButton;

    public MainMenu(String id) {
        try {
            // 이미지 파일 경로에 맞게 수정
            Image backgroundImage = ImageIO.read(new File("src/images/background.png"));

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

        this.id = id;
        buttonClickSound = new SoundManager("src/sounds/button_click.wav");
        setUpFrame();

        backgroundMusic = new SoundManager("src/sounds/background.wav");
        backgroundMusic.loop();

        singlePlayerButton = createButton("Single-Player", new Font("MV Boli", Font.BOLD, 21));
        multiPlayerButton = createButton("Multi-Player", new Font("MV Boli", Font.BOLD, 21));
        scoreboardButton = createButton("Score List", new Font("MV Boli", Font.BOLD, 21));
        aboutButton = createButtonWithIcon("src/images/about.png");
        settingsButton = createButtonWithIcon("src/images/settings.png");

        JPanel buttonsPanel = createPanel(new GridLayout(3, 1), 325, 480, 250, 150, Color.black);
        buttonsPanel.add(singlePlayerButton);
        buttonsPanel.add(multiPlayerButton);
        buttonsPanel.add(scoreboardButton);

        JPanel aboutPanel = createPanel(new GridLayout(1, 1), 70, 550, 50, 50, Color.black);
        aboutPanel.add(aboutButton);

        JPanel settingsPanel = createPanel(new GridLayout(1, 1), 780, 550, 50, 50, Color.black);
        settingsPanel.add(settingsButton);

        this.addComponents(buttonsPanel, aboutPanel, settingsPanel);
        this.setLayout(null);
        this.setVisible(true);

    }

    private JButton createButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.addActionListener(this);
        button.setFocusable(false);
        return button;
    }

    private JButton createButtonWithIcon(String iconPath) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(iconPath)));
        JButton button = new JButton(icon);
        button.addActionListener(this);
        button.setFocusable(false);
        return button;
    }

    private JPanel createPanel(LayoutManager layout, int x, int y, int width, int height, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBounds(x, y, width, height);
        panel.setBackground(color);
        return panel;
    }

    private void setUpFrame() {
        this.setTitle("Tetris");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 700);

        ImageIcon logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("src/images/logo.png")));
        this.setIconImage(logo.getImage());
        this.setLocationRelativeTo(null);
    }

    private void addComponents(JComponent... components) {
        for (JComponent component : components) {
            this.add(component);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        buttonClickSound.play();

        if (e.getSource() == singlePlayerButton) {
            openFrame(new Tetris(id));
        } else if (e.getSource() == scoreboardButton) {
            openFrame(new ScorePanel(id));
        } else if (e.getSource() == aboutButton) {
            openFrame(new AboutFrame(id));
        } else if (e.getSource() == settingsButton) {
            openFrame(new SettingsFrame(id));
        } else if (e.getSource() == multiPlayerButton) {
            this.setVisible(false);
            backgroundMusic.stop();
            SwingUtilities.invokeLater(() -> {
                TwoPlayer twoPlayer = new TwoPlayer();
                twoPlayer.setVisible(true);
            });
        }
    }

    private void openFrame(JFrame frame) {
        this.setVisible(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        backgroundMusic.stop();
    }
}
