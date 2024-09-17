package src.kr.ac.jbnu.se.tetris;

import java.awt.BorderLayout;
import javax.swing.*;


public class TwoPlayer extends JFrame {
    final JLabel statusbar;

    public TwoPlayer() {
        statusbar = new JLabel(" ");
        add(statusbar, BorderLayout.SOUTH);
        TwoPlayerBoard board = new TwoPlayerBoard(this);
        add(board);
        board.start(1);
        board.start(2);

        setSize(720, 720);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 프레임 배치
    }

    public JLabel getStatusBar() {
        return statusbar;
    }

}