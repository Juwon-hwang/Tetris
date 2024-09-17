package src.kr.ac.jbnu.se.tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;


public class TwoPlayerBoard extends JPanel implements ActionListener {

    private boolean isGameOver = false; // 게임오버상태인지 아닌지 확인할 변수 추가
    final int BoardWidth = 10;
    final int BoardHeight = 22;

    private String id;

    final Timer timer;
    boolean isFallingFinished = false;
    boolean isFallingFinished2 = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curX2 = 0;
    int curY = 0;
    int curY2 = 0;
    private int score = 0; // 현재 스코어
    private int score2 = 0; // 현재 스코어

    final JLabel statusbar;
    Shape curPiece;
    Shape curPiece2;
    final Tetrominoes[] board;
    final Tetrominoes[] board2;

    public TwoPlayerBoard(TwoPlayer parent) {



        setFocusable(true);
        curPiece = new Shape();
        curPiece2 = new Shape();
        timer = new Timer(400, this);
        timer.start();

        statusbar = parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        board2 = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();

    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece(1);
        } else {
            oneLineDown(1);
        }
        if (isFallingFinished2) {
            isFallingFinished2 = false;
            newPiece(2);
        } else {
            oneLineDown(2);
        }
        if (score >= 2) {
            timer.stop();
            isStarted = false;
            statusbar.setText("1p wins!"); // 게임 종료 메시지 설정

            gameOverAction();  // 게임 오버 액션 추가

        }

        // 2p가 스코어 20점에 도달하면 게임 종료
        if (score2 >= 2) {
            timer.stop();
            isStarted = false;
            statusbar.setText("2p wins!"); // 게임 종료 메시지 설정

            gameOverAction();  // 게임 오버 액션 추가
        }

        statusbar.setFont(new Font("Arial", Font.BOLD, 48)); // 폰트 크기를 48로 변경
        statusbar.setHorizontalAlignment(SwingConstants.CENTER); // 중앙 정렬

    }
    private void initializeGame() {
                score = 0;
        // 현재 제거한 줄 수
        int linesCleared = 0;
                score2 = 0;
        // 현재 제거한 줄 수
        int linesCleared2 = 0;
        }

    int squareWidth() {
        return BoardWidth * 2;
    }

    int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

    Tetrominoes shapeAt(int x, int y, int var) {
        return switch (var) {
            case 2 -> board2[(y * BoardWidth) + x];
            default -> board[(y * BoardWidth) + x];
        };
    }

    public void start(int var) {

        switch (var) {
            case 1:
                if (isPaused)
                    return;

                isStarted = true;
                isFallingFinished = false;
                numLinesRemoved = 0;
                clearBoard();
                newPiece(1);
                timer.start();
                initializeGame();
                isGameOver = false;   // 게임을 다시 시작할 때 isGameOver를 false로 초기화
                break;
            case 2:
                if (isPaused)
                    return;

                isStarted = true;
                isFallingFinished2 = false;
                numLinesRemoved = 0;
                clearBoard();
                newPiece(2);
                timer.start();
                initializeGame();
                isGameOver = false;   // 게임을 다시 시작할 때 isGameOver를 false로 초기화
                break;
        }
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
            statusbar.setText(" ");
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        ImageIcon background = new ImageIcon("src/images/Basebackground.png");
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1,1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 80 + j * squareWidth(), boardTop + i * squareHeight(), shape);
            }
        }
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1,2);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 440 + j * squareWidth(), boardTop + i * squareHeight(), shape);
            }
        }
        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 80 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }

        if (curPiece2.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x2 = curX2 + curPiece2.x(i);
                int y2 = curY2 - curPiece2.y(i);
                drawSquare(g, 440 + x2 * squareWidth(), boardTop + (BoardHeight - y2 - 1) * squareHeight(),
                        curPiece2.getShape());
            }
        }

        // 격자 무늬 그리기
        g.setColor(Color.GRAY);  // 격자 선의 색상 설정

        // 가로선 그리기
        for (int i = 0; i < BoardHeight; i++) {
            g.drawLine(80, boardTop + i * squareHeight(),
                    80 + BoardWidth * squareWidth(), boardTop + i * squareHeight());
        }

        // 세로선 그리기
        for (int j = 0; j < BoardWidth; j++) {
            g.drawLine(80 + j * squareWidth(), boardTop,
                    80 + j * squareWidth(), boardTop + BoardHeight * squareHeight());
        }

        // 가로선 그리기
        for (int i = 0; i < BoardHeight; i++) {
            g.drawLine(440, boardTop + i * squareHeight(),
                    440 + BoardWidth * squareWidth(), boardTop + i * squareHeight());
        }

        // 세로선 그리기
        for (int j = 0; j < BoardWidth; j++) {
            g.drawLine(440 + j * squareWidth(), boardTop,
                    440 + j * squareWidth(), boardTop + BoardHeight * squareHeight());
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 40);
        g.drawString("1p", 10, 60);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score2, 350, 40);
        g.drawString("2p", 350, 60);
    }

    // 게임오버 상황에서의 액션
    private void gameOverAction() {
        int option = JOptionPane.showOptionDialog(this, "게임이 종료되었습니다. 다시 시작하시겠습니까?", "게임 종료",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"다시 시작", "메인 메뉴"}, "default");
        if (option == JOptionPane.YES_OPTION) {
            start(2);
            start(1);  // 게임 재시작
        } else {
            openMainFrame();// 메인 메뉴로 돌아가게 하는 메서드.
            SwingUtilities.windowForComponent(this).dispose();
        }
    }

    private void openMainFrame() {
        this.setVisible(false); // Tetris 닫아버리기

        MainMenu mainMenu = new MainMenu(id);  // 메인 메뉴 인스턴스 생성
        mainMenu.setLocationRelativeTo(null);
        mainMenu.setVisible(true);
    }

    private void dropDown(int var) {
        int newY = curY;
        int newY2 = curY2;
        switch (var) {

            case 1:
                while (newY > 0) {
                    if (!tryMove(curPiece, curX, newY - 1, 1))
                        break;
                    --newY;
                }
                pieceDropped(1);
                break;

            case 2:
                while (newY2 > 0) {
                    if (!tryMove(curPiece2, curX2, newY2 - 1, 2))
                        break;
                    --newY2;
                }
                pieceDropped(2);
                break;

        }
    }

    private void oneLineDown(int var) {
        switch (var) {
            case 1:
                if (!tryMove(curPiece, curX, curY - 1, 1))
                    pieceDropped(1);
                break;
            case 2:
                if (!tryMove(curPiece2, curX2, curY2 - 1, 2))
                    pieceDropped(2);
                break;

        }
    }

    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i){
            board[i] = Tetrominoes.NoShape;
            board2[i] = Tetrominoes.NoShape;
        }
    }

    private void pieceDropped(int var) {

        switch (var) {
            case 1:
                for (int i = 0; i < 4; ++i) {
                    int x = curX + curPiece.x(i);
                    int y = curY - curPiece.y(i);
                    board[(y * BoardWidth) + x] = curPiece.getShape();
                }

                removeFullLines(1);

                if (!isFallingFinished)
                    newPiece(1);
                break;
            case 2:
                for (int i = 0; i < 4; ++i) {
                    int x2 = curX2 + curPiece2.x(i);
                    int y2 = curY2 - curPiece2.y(i);
                    board2[(y2 * BoardWidth) + x2] = curPiece2.getShape();
                }

                removeFullLines(2);

                if (!isFallingFinished2)
                    newPiece(2);
                break;

        }
    }

    private void newPiece(int var) {
        switch (var) {
            case 1:
                curPiece.setRandomShape();
                curX = BoardWidth / 2 + 1;
                curY = BoardHeight - 1 + curPiece.minY();

                if (!tryMove(curPiece, curX, curY, 1)) {
                    curPiece.setShape(Tetrominoes.NoShape);
                    timer.stop();
                    isStarted = false;
                    isGameOver = true; // 게임 오버 시 isGameOver변수를 false에서 true로 변경
                    gameOverAction();  // 게임 오버 액션 추가

                }
                break;
            case 2:
                curPiece2.setRandomShape();
                curX2 = BoardWidth / 2 + 1;
                curY2 = BoardHeight - 1 + curPiece2.minY();

                if (!tryMove(curPiece2, curX2, curY2, 2)) {
                    curPiece2.setShape(Tetrominoes.NoShape);
                    timer.stop();
                    isStarted = false;
                    isGameOver = true; // 게임 오버 시 isGameOver변수를 false에서 true로 변경
                    gameOverAction();  // 게임 오버 액션 추가
                }
                break;
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY, int var) {

        switch (var) {
            case 1:
                for (int i = 0; i < 4; ++i) {
                    int x = newX + newPiece.x(i);
                    int y = newY - newPiece.y(i);
                    if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                        return false;
                    if (shapeAt(x, y,1) != Tetrominoes.NoShape)
                        return false;
                }
                curPiece = newPiece;
                curX = newX;
                curY = newY;
                repaint();
                return true;

            case 2:
                for (int i = 0; i < 4; ++i) {
                    int x = newX + newPiece.x(i);
                    int y = newY - newPiece.y(i);
                    if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                        return false;
                    if (shapeAt(x, y,2) != Tetrominoes.NoShape)
                        return false;
                }
                curPiece2 = newPiece;
                curX2 = newX;
                curY2 = newY;
                repaint();
                return true;

            default: return true;
        }
    }


    private void removeFullLines(int var) {
        int numFullLines = 0;
        int numFullLines2 = 0;
        switch (var) {

            case 1:

                for (int i = BoardHeight - 1; i >= 0; --i) {
                    boolean lineIsFull = true;

                    for (int j = 0; j < BoardWidth; ++j) {
                        if (shapeAt(j, i, 1) == Tetrominoes.NoShape) {
                            lineIsFull = false;
                            break;
                        }
                    }

                    if (lineIsFull) {
                        ++numFullLines;
                        for (int k = i; k < BoardHeight - 1; ++k) {
                            for (int j = 0; j < BoardWidth; ++j) {
                                board[(k * BoardWidth) + j] = shapeAt(j, k + 1, 1);
                            }
                        }
                    }
                }

                if (numFullLines > 0) {
                    numLinesRemoved += numFullLines;
                    isFallingFinished = true;
                    curPiece.setShape(Tetrominoes.NoShape);
                    repaint();
                    score += numFullLines;
                }
            case 2:
                for (int i = BoardHeight - 1; i >= 0; --i) {
                    boolean lineIsFull2 = true;

                    for (int j = 0; j < BoardWidth; ++j) {
                        if (shapeAt(j, i, 2) == Tetrominoes.NoShape) {
                            lineIsFull2 = false;
                            break;
                        }
                    }

                    if (lineIsFull2) {
                        ++numFullLines2;
                        for (int k = i; k < BoardHeight - 1; ++k) {
                            for (int j = 0; j < BoardWidth; ++j) {
                                board2[(k * BoardWidth) + j] = shapeAt(j, k + 1, 2);
                            }
                        }
                    }
                }

                if (numFullLines2 > 0) {
                    numLinesRemoved += numFullLines2;
                    isFallingFinished2 = true;
                    curPiece2.setShape(Tetrominoes.NoShape);
                    repaint();
                    score2 += numFullLines2;
                }
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color[] colors = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
                new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
                new Color(218, 170, 0) };

        Color color = colors[shape.ordinal()];

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
                case KeyEvent.VK_A:
                    tryMove(curPiece, curX - 1, curY,1);
                    break;
                case KeyEvent.VK_D:
                    tryMove(curPiece, curX + 1, curY,1);
                    break;
                case KeyEvent.VK_S:
                    tryMove(curPiece.rotateRight(), curX, curY,1);
                    break;
                case KeyEvent.VK_W:
                    tryMove(curPiece.rotateLeft(), curX, curY,1);
                    break;
                case KeyEvent.VK_F:
                    dropDown(1);
                    break;
                case KeyEvent.VK_C:
                    oneLineDown(1);
                    break;
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece2, curX2 - 1, curY2,2);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece2, curX2 + 1, curY2,2);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece2.rotateRight(), curX2, curY2,2);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece2.rotateLeft(), curX2, curY2,2);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown(2);
                    break;
            }

        }
    }
}
