package src.kr.ac.jbnu.se.tetris;

import java.util.Random;

public class Shape {

	private Tetrominoes pieceShape;
	private final int[][] coords;

	public Shape() {
		coords = new int[4][2];
		setShape(Tetrominoes.NoShape);
	}

	public void setShape(Tetrominoes shape) {

		int[][][] coordsTable = new int[][][]{{{0, 0}, {0, 0}, {0, 0}, {0, 0}},
				{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
				{{0, -1}, {0, 0}, {0, 1}, {0, 2}}, {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
				{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
				{{1, -1}, {0, -1}, {0, 0}, {0, 1}}};

		for (int i = 0; i < 4; i++) {
            System.arraycopy(coordsTable[shape.ordinal()][i], 0, coords[i], 0, 2);
		}
		pieceShape = shape;

	}

	private void setX(int index, int x) {
		coords[index][0] = x;
	}

	private void setY(int index, int y) {
		coords[index][1] = y;
	}

	public int x(int index) {
		return coords[index][0];
	}

	public int y(int index) {
		return coords[index][1];
	}

	public Tetrominoes getShape() {
		return pieceShape;
	}

	public void setRandomShape() {
		Random r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1;
		Tetrominoes[] values = Tetrominoes.values();
		setShape(values[x]);
	}

	public int minY() {
		int m = coords[0][1];
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][1]);
		}
		return m;
	}

	public Shape rotateLeft() {
		if (pieceShape == Tetrominoes.SquareShape)
			return this;

		Shape result = new Shape();
		result.pieceShape = pieceShape;

		for (int i = 0; i < 4; ++i) {
			result.setX(i, y(i));
			result.setY(i, -x(i));
		}
		return result;
	}

	public Shape rotateRight() {
		if (pieceShape == Tetrominoes.SquareShape)
			return this;

		Shape result = new Shape();
		result.pieceShape = pieceShape;

		for (int i = 0; i < 4; ++i) {
			result.setX(i, -y(i));
			result.setY(i, x(i));
		}
		return result;
	}
}