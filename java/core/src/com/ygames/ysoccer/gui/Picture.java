package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;

public class Picture extends Widget {

    private enum HAlign {
        RIGHT, CENTER, LEFT
    }

    private enum VAlign {
        TOP, CENTER, BOTTOM
    }

    private HAlign hAlign;
    private VAlign vAlign;

    protected Picture() {
        hAlign = HAlign.CENTER;
        vAlign = VAlign.CENTER;
    }

    public Picture(TextureRegion textureRegion) {
        this();
        this.textureRegion = textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        if (textureRegion != null) {
            this.textureRegion = textureRegion;
            w = textureRegion.getRegionWidth();
            h = textureRegion.getRegionHeight();
        }
    }

    protected void limitToSize(int wMax, int hMax) {
        if (textureRegion != null) {
            float w = textureRegion.getRegionWidth();
            float h = textureRegion.getRegionHeight();
            imageScaleX = 1f;
            imageScaleY = 1f;
            if (w > wMax || h > hMax) {
                imageScaleX = Math.min(wMax / w, hMax / h);
                imageScaleY = Math.min(wMax / w, hMax / h);
            }
        }
    }

    @Override
    public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
        if (!visible) {
            return;
        }

        if (textureRegion != null) {
            int posX = x;
            if (hAlign == HAlign.CENTER) {
                posX -= (int) (textureRegion.getRegionWidth() * imageScaleX / 2);
            } else if (hAlign == HAlign.RIGHT) {
                posX -= (int) (textureRegion.getRegionWidth() * imageScaleX);
            }

            int posY = y;
            if (vAlign == VAlign.CENTER) {
                posY -= (int) (textureRegion.getRegionHeight() * imageScaleY / 2);
            } else if (vAlign == VAlign.BOTTOM) {
                posY -= (int) (textureRegion.getRegionHeight() * imageScaleY);
            }
            if (addShadow) {
                batch.setColor(0x242424, alpha);
                batch.begin();
                batch.draw(textureRegion, posX + 2, posY + 2, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), imageScaleX, imageScaleY, 0);
                batch.end();
            }
            batch.setColor(0xFFFFFF, alpha);
            batch.begin();
            batch.draw(textureRegion, posX, posY, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), imageScaleX, imageScaleY, 0);
            batch.end();
        }
    }
}
