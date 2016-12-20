package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

public class TacticsBoard extends Widget {

    private boolean viewOpponent;
    private boolean compareTactics;
    private Team teamA;
    private Team teamB;

    private static int[][][] positions = new int[][][]{
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

        Texture texture = new Texture("images/board.png");
        textureRegion = new TextureRegion(texture);
        textureRegion.flip(false, true);
        w = textureRegion.getRegionWidth();
        h = textureRegion.getRegionHeight();

        this.font = Assets.font10;
    }

    public TacticsBoard(Team team) {
        this(team, null);
    }

    public void setViewOpponent(boolean viewOpponent) {
        this.viewOpponent = viewOpponent;
    }

    public void setCompareTactics(boolean compareTactics) {
        this.compareTactics = compareTactics;
    }

    @Override
    public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
        if (!visible) {
            return;
        }

        batch.begin();

        batch.draw(textureRegion, x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());

        if ((!compareTactics) || (viewOpponent)) {

            Team team = viewOpponent ? teamB : teamA;

            int baseTactics = Assets.tactics[team.tactics].basedOn;

            for (int ply = 0; ply < Const.TEAM_SIZE; ply++) {

                // name
                // fix x -y position & alignment
                int tx = 0;
                Font.Align align = Font.Align.CENTER;
                int xSide = positions[baseTactics][ply][0];
                switch (viewOpponent ? -xSide : xSide) {
                    case -2:
                        tx = x + 10;
                        align = Font.Align.LEFT;
                        break;

                    case -1:
                        tx = x + w / 2 - 7;
                        align = Font.Align.RIGHT;
                        break;

                    case 0:
                        tx = x + w / 2;
                        align = Font.Align.CENTER;
                        break;

                    case +1:
                        tx = x + w / 2 + 7;
                        align = Font.Align.LEFT;
                        break;

                    case +2:
                        tx = x + w - 10;
                        align = Font.Align.RIGHT;
                        break;
                }

                int ty;
                if (viewOpponent) {
                    ty = y + h - 14 - 18 - 28 * positions[baseTactics][ply][1];
                } else {
                    ty = y + 14 + 28 * positions[baseTactics][ply][1];
                }

                Player player = team.players.get(ply);

                String name = player.shirtName.length() > 10 ?
                        player.shirtName.substring(0, 8) + "." :
                        player.shirtName;

                font.draw(batch, name, tx, ty, align);
            }

        } else {
            // pieces of both teams
            for (int tm = 0; tm < 2; tm++) {

                Team teamToShow;
                if (tm == 0) {
                    teamToShow = teamA;
                } else {
                    teamToShow = teamB;
                }

                int baseTactics = Assets.tactics[teamToShow.tactics].basedOn;

                for (int ply = 0; ply < Const.TEAM_SIZE; ply++) {
                    int tx = 0;
                    int xSide = positions[baseTactics][ply][0];
                    switch (tm == (viewOpponent ? 0 : 1) ? -xSide : xSide) {
                        case -2:
                            tx = x + 26;
                            break;

                        case -1:
                            tx = x + w / 2 - 36 - 20;
                            break;

                        case 0:
                            tx = x + w / 2 - 8;
                            break;

                        case +1:
                            tx = x + w / 2 + 36;
                            break;

                        case +2:
                            tx = x + w - 26 - 20;
                            break;
                    }

                    // pieces
                    int ty;
                    if (tm == (viewOpponent ? 0 : 1)) {
                        ty = y + h - 14 - 14 - 28 * positions[baseTactics][ply][1];
                    } else {
                        ty = y + 14 + 28 * positions[baseTactics][ply][1];
                    }
                    batch.draw(Assets.pieces[tm][ply > 0 ? 1 : 0], tx, ty);

                    // number
                    Player player = teamToShow.players.get(ply);
                    Assets.font10.draw(batch, "" + player.number, tx + 10, ty - 1, Font.Align.CENTER);
                }
            }
        }

        batch.end();
    }
}
