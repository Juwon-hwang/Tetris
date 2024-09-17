package src.kr.ac.jbnu.se.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PreviewBoard extends JPanel {
//test
    private final Board mainBoard;
    private Image backgroundImage; // 이미지를 저장할 변수 추가

    private Shape holdPiece; // HOLD된 블록을 저장하는 필드 추가

    public PreviewBoard(Board mainBoard) {
        this.mainBoard = mainBoard;
        this.setPreferredSize(new Dimension(200, 200));
        this.setBackground(Color.GRAY);
        try {
            // 이미지 파일 경로 설정
            File imgFile = new File("src/images/Previewbackground9.png"); // 이미지 파일 경로로 변경해야 함

            // 이미지 파일을 읽어와 배경 이미지로 설정
            backgroundImage = ImageIO.read(imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        mainBoard.drawPreview(g, 185, 150);
        drawHoldPiece(g, 185, 300);  // 추가된 부분
    }
    public void updatePreview() {
        repaint();
    }

    public void setHoldPiece(Shape holdPiece) {
        this.holdPiece = holdPiece;
        updatePreview();
    }

    public void drawHoldPiece(Graphics g, int offsetX, int offsetY) {
        if (holdPiece != null) {
            for (int i = 0; i < 4; ++i) {
                int x = offsetX + holdPiece.x(i) * mainBoard.squareWidth();
                int y = offsetY + holdPiece.y(i) * mainBoard.squareHeight();
                mainBoard.drawSquare(g, x, y, holdPiece.getShape());
            }
        }
    }
}