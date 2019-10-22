package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

import static com.ygames.ysoccer.framework.Font.Align.CENTER;

public class PlayerSprite extends Sprite {

    Player player;

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
            Integer[] origin = Assets.keeperOrigins[d.fmy][d.fmx];
            glGraphics.batch.draw(
                    Assets.keeper[player.team.index][player.skinColor.ordinal()][d.fmx][d.fmy],
                    d.x - origin[0],
                    d.y - origin[1] - d.z
            );

            Integer[] hairMap = Assets.keeperHairMap[d.fmy][d.fmx];
            if (hairMap[2] != 0 || hairMap[3] != 0) {
                glGraphics.batch.draw(
                        Assets.hairs.get(player.hair)[hairMap[0]][hairMap[1]],
                        d.x - origin[0] + hairMap[2],
                        d.y - origin[1] - d.z + hairMap[3]
                );
            }
        } else {
            Integer[] origin = Assets.playerOrigins[d.fmy][d.fmx];
            glGraphics.batch.draw(
                    Assets.player[player.team.index][player.skinColor.ordinal()][d.fmx][d.fmy],
                    d.x - origin[0],
                    d.y - origin[1] - d.z
            );

            Integer[] hairMap = Assets.playerHairMap[d.fmy][d.fmx];
            if (hairMap[2] != 0 || hairMap[3] != 0) {
                glGraphics.batch.draw(
                        Assets.hairs.get(player.hair)[hairMap[0]][hairMap[1]],
                        d.x - origin[0] - 9 + hairMap[2],
                        d.y - origin[1] - d.z - 9 + hairMap[3]
                );
            }
        }

        // development
        if (Settings.showDevelopmentInfo) {
            if (Settings.showPlayerState && player.fsm != null) {
                Assets.font3.draw(glGraphics.batch, PlayerFsm.Id.values()[d.playerState].toString(), d.x, d.y - 50 - d.z, CENTER);
            }
            if (Settings.showPlayerAiState && d.playerAiState != -1) {
                Assets.font3.draw(glGraphics.batch, AiFsm.Id.values()[d.playerAiState].toString(), d.x, d.y - 40 - d.z, CENTER);
            }
            if (Settings.showBestDefender && d.isBestDefender) {
                Assets.font6.draw(glGraphics.batch, "_", d.x, d.y - 12 - d.z, CENTER);
            }
            if (Settings.showFrameDistance) {
                Assets.font3.draw(glGraphics.batch, d.frameDistance, d.x, d.y - d.z + 4, CENTER);
            }
        }
    }

    @Override
    public int getY(int subframe) {
        return player.data[subframe].y;
    }
}
