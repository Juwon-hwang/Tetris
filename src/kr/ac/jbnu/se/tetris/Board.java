package src.kr.ac.jbnu.se.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Font;
import javax.swing.JOptionPane; // 게임 종료 시 재시작 혹은 메뉴로 돌아갈 것인지 물어보게 하기 위한 모듈
public class Board extends JPanel implements ActionListener {
//test
	final int BoardWidth = 10;
	final int BoardHeight = 22;

	final Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved = 0;
	int level = 1;
	int delay = 400;
	int score = 0; // 스코어 점수
	int curX = 0;
	int curY = 0;
	final JLabel statusbar;
	Shape curPiece;
	final Tetrominoes[] board;
	Shape nextPiece; // 다음 블록 미리 보기 변수 생성
	int rotationCount = 0; // 도형의 회전 횟수를 저장할 변수 추가
	private final String id;
	private boolean isGameOver = false; // 게임오버상태인지 아닌지 확인할 변수 추가
	private PreviewBoard previewBoard;
	private final Tetris tetrisParent; // Board 클래스가 Tetris 클래스에 접근할 수 있도록 멤버 변수 생성

	private Shape holdPiece; // HOLD 기능
	private SoundManager lineClearSound; // 줄 완성 효과음을 위한 SoundManager 인스턴스
	private SoundManager blockDropSound; // 블록 쌓일 때 효과음을 위한 인스턴스
	private SoundManager buttonClickSound; // 버튼 클릭시 효과음을 위한 인스턴스
	private SoundManager gameOverSound; // 게임 오버시 효과음을 위한 인스턴스

	public void setPreviewBoard(PreviewBoard previewBoard) {
		this.previewBoard = previewBoard;
	} ///


	public Board(Tetris parent, String id) {
		this.id = id;
		this.tetrisParent = parent; // 멤버 추가

		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(400, this);
		timer.start();

		statusbar = parent.getStatusBar(id);
		board = new Tetrominoes[BoardWidth * BoardHeight];
		addKeyListener(new TAdapter());
		clearBoard();
	}

	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
	}

	// overiding을 위해 protected로 받음
	protected int squareWidth() {
		return (int) getSize().getWidth() / BoardWidth;
	}

	protected int squareHeight() {
		return (int) getSize().getHeight() / BoardHeight;
	}


	Tetrominoes shapeAt(int x, int y) {
		return board[(y * BoardWidth) + x];
	}
	//

	public void start() {
		if (isPaused)
			return;

		lineClearSound = new SoundManager("src/sounds/line_clear.wav"); // 줄 제거 초기화
		blockDropSound = new SoundManager("src/sounds/block_stack.wav"); // 블럭 쌓기 초기화
		buttonClickSound = new SoundManager("src/sounds/button_click.wav"); // 버튼 클릭 초기화
		gameOverSound = new SoundManager("src/sounds/game_over.wav"); // 게임 오버 사운드 초기화
		nextPiece = new Shape();
		nextPiece.setRandomShape();

		isStarted = true;
		isFallingFinished = false;
		isGameOver = false;   // 게임을 다시 시작할 때 isGameOver를 false로 초기화
		numLinesRemoved = 0;
		clearBoard();

		newPiece();
		timer.start();
		resetLevel();
		resetScore();
		resetHoldPiece();
	}

	private void pause() {
		if (!isStarted)
			return;

		isPaused = !isPaused;
		if (isPaused) {
			timer.stop();
			statusbar.setText("paused");
		} else {
			timer.start();
			statusbar.setText(String.valueOf(numLinesRemoved));
		}
		repaint();
	}

	// 도형 미리보기 시각화
	public void drawPreview(Graphics g, int offsetX, int offsetY) {
		if (nextPiece.getShape() == Tetrominoes.NoShape) {
			return;
		}
		Font newFont = new Font("SansSerif", Font.BOLD, 30); // 폰트명, 스타일, 크기 지정
		g.setFont(newFont);

		g.setColor(new Color(128, 0, 128));  // 글자색 변경
		g.drawString(" ", offsetX - 165, offsetY + 30);  // 텍스트 위치 지정

		g.drawString(String.valueOf(score), offsetX, offsetY +280);  // 텍스트 위치 지정
		g.drawString(String.valueOf(level), offsetX, offsetY +380);  // 텍스트 위치 지정

		for (int i = 0; i < 4; ++i) {
			int x = offsetX + nextPiece.x(i) * squareWidth();
			int y = offsetY + nextPiece.y(i) * squareHeight();
			drawSquare(g, x, y, nextPiece.getShape());
		}
	}

	// paint 메서드 간소화
	public void paint(Graphics g) {
		super.paint(g);

		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		// 보드 내부를 채우는 모양 그리기
		drawBoardShapes(g, boardTop);

		// 고스트 블록 그리기
		drawGhostBlock(g, boardTop);

		// 격자 무늬 그리기
		drawGrid(g, boardTop);
	}

	private void drawBoardShapes(Graphics g, int boardTop) {
		for (int i = 0; i < BoardHeight; i++) {
			for (int j = 0; j < BoardWidth; j++) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape) {
					drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
				}
			}
		}

		if (curPiece.getShape() != Tetrominoes.NoShape) {
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}
	}

	private void drawGhostBlock(Graphics g, int boardTop) {
		if (isStarted && !isPaused) {
			int ghostY = curY;
			while (ghostY > 0) {
				if (!tryMove(curPiece, curX, ghostY - 1, false))
					break;
				--ghostY;
			}

			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = ghostY - curPiece.y(i);
				drawGhostSquare(g, x * squareWidth(),
						boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}
	}

	private void drawGrid(Graphics g, int boardTop) {
		g.setColor(Color.GRAY);
		// 가로선 그리기
		for (int i = 0; i < BoardHeight; i++) {
			g.drawLine(0, boardTop + i * squareHeight(), BoardWidth * squareWidth(), boardTop + i * squareHeight());
		}

		// 세로선 그리기
		for (int j = 0; j < BoardWidth; j++) {
			g.drawLine(j * squareWidth(), boardTop, j * squareWidth(), boardTop + BoardHeight * squareHeight());
		}
	}

	private void drawGhostSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color[] colors = {new Color(0, 0, 0), new Color(204, 102, 102, 255),
				new Color(102, 204, 102, 255), new Color(102, 102, 204, 255),
				new Color(204, 204, 102, 255), new Color(204, 102, 204, 255),
				new Color(102, 204, 204, 255), new Color(218, 170, 0, 255),
				new Color(128, 128, 128)};  // GrayShape 추가함
		Color color = colors[shape.ordinal()];
		g.setColor(color);
		g.drawRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
	}



	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	private void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}

	private void pieceDropped() {
		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}

		removeFullLines();

		if (!isFallingFinished)
			newPiece();

		blockDropSound.play();
	}

	private void newPiece() {
		curPiece.setShape(nextPiece.getShape()); // 현재 조각을 다음 조각으로 설정
		nextPiece.setRandomShape(); // 다음 조각을 랜덤으로 생성
		rotationCount = 0; // 도형이 생성될 때 회전 횟수를 0으로 초기화

		if (previewBoard != null) {
			previewBoard.repaint(); // PreviewBoard의 paintComponent를 호출하여 그리기 업데이트
		}

		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();

		if (!tryMove(curPiece, curX, curY)) {
			gameOver();
		}
	}

	public void gameOver(){
		curPiece.setShape(Tetrominoes.NoShape);
		timer.stop();
		isStarted = false;
		isGameOver = true; // 게임 오버 시 isGameOver변수를 false에서 true로 변경
		statusbar.setText("game over");
		repaint(); // gameover시 회색으로 변경
		gameOverSound.play(); //게임 오버시 효과음 재생
		updateScore();
		gameOverAction();  // 게임 오버 액션 추가

	}

	// 게임오버 상황에서의 액션
	private void gameOverAction() {
		int option = JOptionPane.showOptionDialog(this, "게임이 종료되었습니다. 다시 시작하시겠습니까?", "게임 종료",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"다시 시작", "메인 메뉴"}, "default");
		if (option == JOptionPane.YES_OPTION) {
			start();  // 게임 재시작
			buttonClickSound.play(); // 효과음 재생
		} else {
			openMainFrame();// 메인 메뉴로 돌아가게 하는 메서드.
			buttonClickSound.play(); // 효과음 재생
		}
	}

	private void resetScore(){
		score = 0;
	}
	private void resetLevel(){
		level = 1;
	}
	private void resetHoldPiece() {
		holdPiece = null;
		if (previewBoard != null) {
			previewBoard.setHoldPiece(null);
		}
	}
	private void openMainFrame() {

		tetrisParent.closeFrame(id); // Tetris 닫아버리기

		MainMenu mainMenu = new MainMenu(id);  // 메인 메뉴 인스턴스 생성
		mainMenu.setLocationRelativeTo(null);
		mainMenu.setVisible(true);
	}

	private boolean tryMove(Shape newPiece, int newX, int newY) {
		return tryMove(newPiece, newX, newY, true);
	}

	private boolean tryMove(Shape newPiece, int newX, int newY, boolean actualMove) {
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		if (actualMove) {
			curPiece = newPiece;
			curX = newX;
			curY = newY;
			repaint();
		}
		return true;
	}
	private void removeFullLines() {
		int numFullLines = countFullLines();

		if (numFullLines > 0) {
			removeLines(numFullLines);
			updateScoreAndLevel(numFullLines);
		}
	}

	private int countFullLines() {
		int numFullLines = 0;

		for (int i = BoardHeight - 1; i >= 0; --i) {
			if (isLineFull(i)) {
				++numFullLines;
			}
		}

		return numFullLines;
	}

	private boolean isLineFull(int line) {
		for (int j = 0; j < BoardWidth; ++j) {
			if (shapeAt(j, line) == Tetrominoes.NoShape) {
				return false;
			}
		}
		return true;
	}

	private void removeLines(int numFullLines) {
		for (int i = BoardHeight - 1; i >= 0; --i) {
			if (isLineFull(i)) {
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j) {
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
					}
				}
			}
		}
	}

	private void updateScoreAndLevel(int numFullLines) {
		numLinesRemoved += numFullLines;
		statusbar.setText(String.valueOf(numLinesRemoved));
		isFallingFinished = true;
		curPiece.setShape(Tetrominoes.NoShape);
		repaint();
		score += numFullLines;  // Increase score based on lines cleared
		lineClearSound.play(); // Play line clearing sound

		level = (score / 5) + 1;  // Increase level every 5 points
		delay = 400 - (level * 30);  // Adjust delay based on level
		timer.setDelay(delay); // Update timer delay
	}

	void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color[] colors = {new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
				new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
				new Color(218, 170, 0), new Color(128, 128, 128)}; //gray색 추가

		Color color = colors[shape.ordinal()];


		if (shape == Tetrominoes.GrayShape) {
			color = new Color(128, 128, 128); // Gray Shape를 회색으로 정의
		}
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}

	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
				return;
			}

			int keycode = e.getKeyCode();

			if (keycode == 'p' || keycode == 'P') {
				pause();
				return;
			}

			if (isPaused)
				return;

			switch (keycode) {
				case KeyEvent.VK_LEFT:
					tryMove(curPiece, curX - 1, curY);
					break;
				case KeyEvent.VK_RIGHT:
					tryMove(curPiece, curX + 1, curY);
					break;
				// 도형 횟수 제한 (현재 4번 0회~3회)
				case KeyEvent.VK_DOWN:
					if (rotationCount < 3) {   // 횟수제한 변경
						tryMove(curPiece.rotateRight(), curX, curY);
						rotationCount++;
					}
					break;
				case KeyEvent.VK_UP:
					if (rotationCount < 3) {   // 횟수제한 변경
						tryMove(curPiece.rotateLeft(), curX, curY);
						rotationCount++;
					}
					break;
				case KeyEvent.VK_SPACE:
					dropDown();
					break;
				case KeyEvent.VK_D:
					oneLineDown();
					break;
				case KeyEvent.VK_H:
					holdPiece();
					break;
			}

		}
	}

	public void updateScore() {
		try {
			if (id != null && score > FirebaseUtil.getUserScore(id)) {
				FirebaseUtil.addScore(id, score);
			}
		} catch (ExecutionException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void holdPiece() {
		if (holdPiece == null) {
			holdPiece = curPiece;
			curPiece = nextPiece;
			nextPiece.setRandomShape();
		} else {
			// HOLD 블록과 현재 블록 교체
			Shape temp = holdPiece;
			holdPiece = curPiece;
			curPiece = temp.rotateRight(); // 블록을 회전시켜 교체
			rotationCount = 0; // 도형이 생성될 때 회전 횟수를 0으로 초기화
		}

		if (previewBoard != null) {
			previewBoard.setHoldPiece(holdPiece);
		}

		repaint();
	}

}