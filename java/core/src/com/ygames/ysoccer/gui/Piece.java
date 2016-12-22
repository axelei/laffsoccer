package com.ygames.ysoccer.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.GLShapeRenderer;

import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.ENTER;
import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.UP;

public class Piece extends Button {

    private final int GL_MIN = 36;
    private final int GL_MAX = 200;

    protected int[] square;
    private int gridX;
    private int gridY;
    private int gridW;
    private int gridH;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;

    private InputProcessor inputProcessor;

    protected Piece() {
        super();
        square = new int[2];
        inputProcessor = new PieceInputProcessor();
    }

    protected void toggleEntryMode() {
        setEntryMode(!entryMode);
    }

    public void setEntryMode(boolean entryMode) {
        if(!this.entryMode && entryMode) {
            this.entryMode = true;
            Gdx.input.setInputProcessor(inputProcessor);
        }
        if(this.entryMode && !entryMode) {
            this.entryMode = false;
            Gdx.input.setInputProcessor(null);
        }
    }

    protected void setSquare(int x, int y) {
        square[0] = x;
        square[1] = y;
        updatePosition();
    }

    protected void setGridGeometry(int x, int y, int w, int h) {
        gridX = x;
        gridY = y;
        gridW = w;
        gridH = h;
        updatePosition();
    }

    protected void setRanges(int xMin, int xMax, int yMin, int yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        updatePosition();
    }

    private void updatePosition() {
        x = gridX + square[0] * gridW / (xMax - xMin);
        y = gridY + square[1] * gridH / (yMax - yMin);
    }

    private class PieceInputProcessor extends InputAdapter {

        public boolean keyDown(int keycode) {
            switch (keycode) {
                case LEFT:
                    square[0] = Math.max(square[0] - 1, xMin);
                    updatePosition();
                    onChanged();
                    setDirty(true);
                    break;

                case RIGHT:
                    square[0] = Math.min(square[0] + 1, xMax);
                    updatePosition();
                    onChanged();
                    setDirty(true);
                    break;

                case UP:
                    square[1] = Math.max(square[1] - 1, yMin);
                    updatePosition();
                    onChanged();
                    setDirty(true);
                    break;

                case DOWN:
                    square[1] = Math.min(square[1] + 1, yMax);
                    updatePosition();
                    onChanged();
                    setDirty(true);
                    break;
            }
            return true;
        }
    }

    public void onChanged() {
    }

    @Override
    protected void drawBorder(GLShapeRenderer shapeRenderer, int bx, int by, int bw, int bh, int topLeftColor, int bottomRightColor) {
        int color = GLColor.rgb(GL_MAX, GL_MAX, 0);
        shapeRenderer.setColor(color, alpha);
        shapeRenderer.ellipse(x, y, w, h);
    }

    @Override
    protected void drawAnimatedBorder(GLShapeRenderer shapeRenderer) {
        // gray level
        int gl = (int) Math.abs(((0.8 * Math.abs(System.currentTimeMillis())) % (2 * (GL_MAX - GL_MIN))) - (GL_MAX - GL_MIN)) + GL_MIN;
        int color = GLColor.rgb(gl, gl, gl);
        shapeRenderer.setColor(color, alpha);
        shapeRenderer.ellipse(x, y, w, h);
        shapeRenderer.setColor(0xFFFFFF, alpha);
    }
}
