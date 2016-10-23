package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.ysdemo.Assets;

public class PlayerSprite extends Sprite {

    Player player;
    static int[][][] offsets = {

            {
                    // row 0
                    {15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25},
                    {15, 25}, {14, 24}, {15, 25}

            },
            {
                    // row 1
                    {15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25},
                    {15, 25}, {14, 24}, {15, 25}

            },
            {
                    // row 2
                    {15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25},
                    {15, 25}, {14, 24}, {15, 25}

            },
            {
                    // row 3
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 4
                    {15, 22}, {13, 18}, {13, 19}, {16, 19}, {17, 24},
                    {15, 26}, {17, 24}, {18, 27}

            },
            {
                    // row 5
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 6
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 7
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 8
                    {15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25},
                    {15, 25}, {14, 24}, {15, 25}

            },
            {
                    // row 9
                    {13, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25},
                    {15, 25}, {14, 24}, {15, 25}

            },
            {
                    // row 10
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 11
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 12
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 13
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 14
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 15
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 16
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 17
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 18
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}

            },
            {
                    // row 19
                    {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25},
                    {15, 25}, {15, 25}, {15, 25}}};

    public PlayerSprite(GLGraphics glGraphics, Player player) {
        super(glGraphics);
        this.player = player;
    }

    @Override
    public void draw(int subframe) {
        Data d = player.data[subframe];
        if (!d.isVisible) {
            return;
        }

        if (player.role == Player.GOALKEEPER) {
            glGraphics.drawTextureRect(Assets.keeper, d.x - 24, d.y - 34 - d.z,
                    50 * d.fmx, 50 * d.fmy, 50, 50);

            if (Hair.map[d.fmy][d.fmx + 8][2] != 0
                    || Hair.map[d.fmy][d.fmx + 8][3] != 0) {
                glGraphics.drawTextureRect(Assets.hairs.get(player.hair), d.x
                                - 24 + Hair.map[d.fmy][d.fmx + 8][2], d.y - 34 - d.z
                                + Hair.map[d.fmy][d.fmx + 8][3],
                        Hair.map[d.fmy][d.fmx + 8][0] * 21,
                        Hair.map[d.fmy][d.fmx + 8][1] * 21, 20, 20);
            }
        } else {
            int offsetX = offsets[d.fmy][d.fmx][0];
            int offsetY = offsets[d.fmy][d.fmx][1];
            if (Assets.player[player.team.index][player.skinColor] != null)
                glGraphics.drawTextureRect(
                        Assets.player[player.team.index][player.skinColor], d.x
                                - offsetX, d.y - offsetY - d.z, 32 * d.fmx,
                        32 * d.fmy, 32, 32);

            if (Hair.map[d.fmy][d.fmx][2] != 0
                    || Hair.map[d.fmy][d.fmx][3] != 0) {
                glGraphics.drawTextureRect(Assets.hairs.get(player.hair), d.x
                                - offsetX - 9 + Hair.map[d.fmy][d.fmx][2], d.y
                                - offsetY - d.z - 9 + Hair.map[d.fmy][d.fmx][3],
                        Hair.map[d.fmy][d.fmx][0] * 21,
                        Hair.map[d.fmy][d.fmx][1] * 21, 20, 20);
            }

        }
    }

    @Override
    public int getY(int subframe) {
        return player.data[subframe].y;
    }

}
