package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import static com.ygames.ysoccer.framework.Font.Align.CENTER;

public class PlayerSprite extends Sprite {

    Player player;
    public static int[][][] offsets = {
            {{15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25}, {15, 25}, {14, 24}, {15, 25}},   // 0
            {{15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25}, {15, 25}, {14, 24}, {15, 25}},   // 1
            {{15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25}, {15, 25}, {14, 24}, {15, 25}},   // 2
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 3
            {{28, 22}, {24, 24}, {15, 29}, {7, 24}, {6, 23}, {6, 14}, {16, 10}, {28, 15}},   // 4
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 5
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 6
            {{13, 19}, {12, 16}, {16, 16}, {17, 14}, {17, 19}, {15, 19}, {14, 19}, {15, 19}},   // 7
            {{15, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25}, {15, 25}, {14, 24}, {15, 25}},   // 8
            {{13, 25}, {16, 25}, {15, 24}, {15, 25}, {14, 25}, {15, 25}, {14, 24}, {15, 25}},   // 9
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 10
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 11
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 12
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 13
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 14
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 15
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 16
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 17
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}},   // 18
            {{15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}, {15, 25}}    // 19
    };

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

        if (player.role == Player.Role.GOALKEEPER) {
            glGraphics.batch.draw(Assets.keeper[player.team.index][player.skinColor.ordinal()][d.fmx][d.fmy], d.x - 24, d.y - 34 - d.z);

            if (Hair.map[d.fmy][d.fmx + 8][2] != 0 || Hair.map[d.fmy][d.fmx + 8][3] != 0) {
                glGraphics.batch.draw(
                        Assets.hairs.get(player.hair)[Hair.map[d.fmy][d.fmx + 8][0]][Hair.map[d.fmy][d.fmx + 8][1]],
                        d.x - 24 + Hair.map[d.fmy][d.fmx + 8][2],
                        d.y - 34 - d.z + Hair.map[d.fmy][d.fmx + 8][3]
                );
            }
        } else {
            int offsetX = offsets[d.fmy][d.fmx][0];
            int offsetY = offsets[d.fmy][d.fmx][1];
            glGraphics.batch.draw(
                    Assets.player[player.team.index][player.skinColor.ordinal()][d.fmx][d.fmy],
                    d.x - offsetX,
                    d.y - offsetY - d.z
            );

            if (Hair.map[d.fmy][d.fmx][2] != 0 || Hair.map[d.fmy][d.fmx][3] != 0) {
                glGraphics.batch.draw(
                        Assets.hairs.get(player.hair)[Hair.map[d.fmy][d.fmx][0]][Hair.map[d.fmy][d.fmx][1]],
                        d.x - offsetX - 9 + Hair.map[d.fmy][d.fmx][2],
                        d.y - offsetY - d.z - 9 + Hair.map[d.fmy][d.fmx][3]
                );
            }
        }

        // development
        if (Settings.showDevelopmentInfo) {
            if (Settings.showPlayerState && player.fsm != null) {
                Assets.font6.draw(glGraphics.batch, PlayerFsm.Id.values()[d.playerState].toString(), d.x, d.y - 68 - d.z, CENTER);
            }
            if (Settings.showPlayerAiState && d.playerAiState != -1) {
                Assets.font6.draw(glGraphics.batch, AiFsm.Id.values()[d.playerAiState].toString(), d.x, d.y - 54 - d.z, CENTER);
            }
            if (Settings.showBestDefender && d.isBestDefender) {
                Assets.font6.draw(glGraphics.batch, "_", d.x, d.y - 12 - d.z, CENTER);
            }
            if (Settings.showFrameDistance) {
                Assets.font6.draw(glGraphics.batch, d.frameDistance, d.x, d.y - d.z, CENTER);
            }
        }
    }

    @Override
    public int getY(int subframe) {
        return player.data[subframe].y;
    }
}
