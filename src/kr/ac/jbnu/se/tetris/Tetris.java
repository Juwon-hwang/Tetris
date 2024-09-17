package src.kr.ac.jbnu.se.tetris;

import java.awt.*;

import javax.swing.*;

public class Tetris extends JFrame {

	final JLabel statusbar;

	public Tetris(String id) {

		statusbar = new JLabel(" 0");
		add(statusbar, BorderLayout.SOUTH);

		// 메인 게임 보드 생성 및 추가
		Board mainBoard = new Board(this, id);

		// 추가로 생성된 미리보기 보드의 레이아웃 및 속성 설정
		PreviewBoard previewBoard = new PreviewBoard(mainBoard);
		mainBoard.setPreviewBoard(previewBoard);  // Board가 PreviewBoard를 업데이트 할 수 있도록 설정합니다.

		// 두 보드를 포함하는 패널을 생성
		JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new GridLayout(1, 2)); // 1행 2열의 그리드 레이아웃 사용
		containerPanel.add(mainBoard);
		containerPanel.add(previewBoard);

		// 메인 프레임에 패널 추가
		add(containerPanel);

		mainBoard.start(); // 게임 시작

		setSize(800, 800); // 프레임 사이즈 조정
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void closeFrame(String id) {
		this.dispose();
	}

	public JLabel getStatusBar(String id) {
		return statusbar;
	}

	public static void main(String[] args) {
        FirebaseUtil.initialize();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);

	}
}