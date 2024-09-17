package src.kr.ac.jbnu.se.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SettingsFrame extends JFrame implements ActionListener {
    private static final String BACKGROUND_SOUND_PATH = "src/sounds/background.wav";
    private static final String BUTTON_CLICK_SOUND_PATH = "src/sounds/button_click.wav";
    private static final String LOGO_PATH = "src/images/logo.png";

    private final JButton backButton;
    private MainMenu mainMenu;
    private final SoundManager backgroundMusic;
    private final JSlider volumeSlider;
    private final String id;
    private final SoundManager buttonClickSound;

    public SettingsFrame(String id) {

        try {
            // 이미지 파일 경로에 맞게 수정
            Image backgroundImage = ImageIO.read(new File("src/images/Settingbackground6.png"));

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
        buttonClickSound = initializeSoundManager(BUTTON_CLICK_SOUND_PATH);
        this.setUpFrame();
        backgroundMusic = initializeSoundManager(BACKGROUND_SOUND_PATH);
        backgroundMusic.loop();

        backButton = createButton("Back to Main-Menu", 370, 580, 150, 50);

        volumeSlider = initializeVolumeSlider(345, 280, 200, 50);

        this.addComponents(backButton, volumeSlider);
        this.setLayout(null);
        this.setVisible(true);
    }

    private SoundManager initializeSoundManager(String path) {
        return new SoundManager(path);
    }

    private void setUpFrame() {
        this.setTitle("Settings");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 700);
        this.getContentPane().setBackground(Color.white);
        ImageIcon logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(LOGO_PATH)));
        this.setIconImage(logo.getImage());
    }

    private JLabel createLabel(String text, Font font, int x, int y, int width, int height) {
        JLabel label = new JLabel();
        label.setText(text);
        label.setForeground(Color.black);
        label.setFont(font);
        label.setBounds(x, y, width, height);
        return label;
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBounds(x, y, width, height);
        button.addActionListener(this);
        return button;
    }

    private JSlider initializeVolumeSlider(int x, int y, int width, int height) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        slider.setBounds(x, y, width, height);
        slider.addChangeListener(e -> {
            int sliderValue = slider.getValue();
            float volume = (float) sliderValue / 100.0f;
            backgroundMusic.setVolume(volume);
        });
        return slider;
    }

    private void addComponents(JComponent... components) {
        for (JComponent component : components) {
            this.add(component);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        buttonClickSound.play();

        if (e.getSource() == backButton) {
            mainMenu = new MainMenu(id);
            this.setVisible(false);
            mainMenu.setVisible(true);
            backgroundMusic.stop();
        }
    }
}
