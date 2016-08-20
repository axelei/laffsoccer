package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

public class TacticsBoard extends Widget {

    boolean viewOpponent;
    boolean compareTactics;
    Team teamA;
    Team teamB;

    static int[][][] positions = new int[][][]{
            {{+0, +0}, {-2, +2}, {-1, +1}, {+1, +1}, {+2, +2}, {-2, +6}, {+0, +4}, {+0, +5}, {+2, +6}, {-1, +8}, {+1, +8}}, // 442
            {{+0, +0}, {-2, +3}, {+0, +1}, {+1, +2}, {+2, +3}, {-1, +5}, {-1, +2}, {+0, +4}, {+1, +5}, {+0, +6}, {+0, +8}}, // 541
            {{+0, +0}, {-2, +2}, {-1, +1}, {+1, +1}, {+2, +2}, {-2, +6}, {+0, +4}, {+1, +5}, {+2, +6}, {-1, +5}, {+0, +8}}, // 451
            {{+0, +0}, {-2, +3}, {+0, +1}, {+1, +2}, {+2, +3}, {-2, +5}, {-1, +2}, {+0, +6}, {+2, +5}, {-1, +8}, {+1, +8}}, // 532
            {{+0, +0}, {-1, +2}, {+0, +1}, {+1, +4}, {+1, +2}, {-2, +5}, {-1, +4}, {+0, +6}, {+2, +5}, {-1, +8}, {+1, +8}}, // 352
            {{+0, +0}, {-2, +2}, {-1, +1}, {+1, +1}, {+2, +2}, {-2, +5}, {+0, +4}, {+1, +7}, {+2, +5}, {-1, +7}, {+0, +8}}, // 433
            {{+0, +0}, {-2, +2}, {-1, +1}, {+1, +1}, {+2, +2}, {-2, +6}, {-1, +4}, {+1, +4}, {+2, +6}, {+0, +7}, {+0, +8}}, // 424
            {{+0, +0}, {-1, +2}, {+0, +1}, {+0, +4}, {+1, +2}, {-2, +5}, {+0, +6}, {+1, +7}, {+2, +5}, {-1, +7}, {+0, +8}}, // 343
            {{+0, +0}, {-2, +3}, {+0, +1}, {+0, +2}, {+2, +3}, {-2, +6}, {+0, +4}, {+0, +5}, {+2, +6}, {-1, +8}, {+1, +8}}, // sweep
            {{+0, +0}, {-2, +3}, {+0, +1}, {+1, +2}, {+2, +3}, {-2, +7}, {-1, +2}, {+1, +5}, {+2, +7}, {-1, +5}, {+0, +8}}, // 523
            {{+0, +0}, {-1, +2}, {+0, +1}, {-1, +5}, {+1, +2}, {-2, +7}, {+1, +5}, {+0, +6}, {+2, +7}, {-1, +8}, {+1, +8}}, // attack
            {{+0, +0}, {-1, +2}, {-1, +1}, {+1, +1}, {+1, +2}, {-2, +3}, {-1, +5}, {+1, +5}, {+2, +3}, {+0, +6}, {+0, +8}}  // defend
    };

    public TacticsBoard(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;

        image = new Image("images/board.png");
        w = image.getRegionWidth();
        h = image.getRegionHeight();

        this.font = Assets.font10;
    }

    public TacticsBoard(Team team) {
        this(team, null);
    }

    @Override
    public void render(GlGraphics glGraphics) {
        if (!isVisible) {
            return;
        }

        glGraphics.batch.begin();

        glGraphics.batch.draw(image, x, y, image.getRegionWidth(), image.getRegionHeight());

        if ((!compareTactics) || (viewOpponent)) {

            Team team = viewOpponent ? teamB : teamA;

            int baseTactics = Assets.tactics[team.getTacticsIndex()].basedOn;

            for (int ply = 0; ply < Const.TEAM_SIZE; ply++) {

                // name
                // fix x -y position & alignment
                int tx = 0;
                int ty = 0;
                Font.Align align = Font.Align.CENTER;
                switch (positions[baseTactics][ply][0]) {
                    case +2:
                        tx = x + 10;
                        align = Font.Align.LEFT;
                        break;
                    case +1:
                        tx = x + w / 2 - 7;
                        align = Font.Align.RIGHT;
                        break;
                    case 0:
                        tx = x + w / 2;
                        align = Font.Align.CENTER;
                        break;
                    case -1:
                        tx = x + w / 2 + 7;
                        align = Font.Align.LEFT;
                        break;
                    case -2:
                        tx = x + w - 10;
                        align = Font.Align.RIGHT;
                        break;
                }

                if (viewOpponent) {
                    ty = y + h - 14 - 18 - 28 * positions[baseTactics][ply][1];
                } else {
                    ty = y + 14 + 28 * positions[baseTactics][ply][1];
                }

                Player player = team.players.get(ply);

                String name = player.shirtName.length() > 10 ?
                        player.shirtName.substring(0, 8) + "." :
                        player.shirtName;

                font.draw(glGraphics.batch, name, tx, ty, align);
            }

        } else {
            // TODO
        }

        glGraphics.batch.end();
    }
}
