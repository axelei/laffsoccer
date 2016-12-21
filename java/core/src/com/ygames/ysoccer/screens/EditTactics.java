package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Picture;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;

import java.util.Collections;

import static com.ygames.ysoccer.match.Const.FULL_TEAM;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;

class EditTactics extends GLScreen {

    boolean copyMode;
    private Team team;
    private int selectedForSwap;
    private int selectedForPair;
    private int[] ballZone;
    private int[] ballCopyZone;

    private Font font10yellow;

    private Widget copyButton;

    EditTactics(GLGame game) {
        super(game);

        copyMode = false;
        team = game.tacticsTeam;
        selectedForSwap = -1;
        selectedForPair = -1;
        ballZone = new int[]{0, 0};
        ballCopyZone = new int[]{0, 0};

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        font10yellow = new Font(10, new RgbPair(0xFCFCFC, 0xFCFC00));
        font10yellow.load();

        Widget w;

        w = new TitleBar(Assets.strings.get("EDIT TACTICS") + " (" + Tactics.codes[game.tacticsToEdit] + ")", 0xBA9206);
        widgets.add(w);

        // players
        for (int pos = 0; pos < FULL_TEAM; pos++) {

            w = new PlayerFaceButton(pos);
            widgets.add(w);

            w = new PlayerNumberButton(pos);
            widgets.add(w);

            w = new PlayerNameButton(pos);
            widgets.add(w);

            int x = 394;
            if (team.type != Team.Type.NATIONAL) {
                if (game.settings.useFlags) {
                    w = new PlayerNationalityFlagButton(pos);
                    widgets.add(w);
                    x += 30;
                } else {
                    w = new PlayerNationalityCodeButton(pos);
                    widgets.add(w);
                    x += 58;
                }
            }

            w = new PlayerRoleButton(x, pos);
            widgets.add(w);
            x += 34;

            for (int skillIndex = 0; skillIndex < 3; skillIndex++) {
                w = new PlayerSkillButton(pos, skillIndex, x);
                widgets.add(w);
                x += 13;
            }
            x += 4;
        }

        w = new TacticsBoard();
        widgets.add(w);

        copyButton = new CopyButton();
        widgets.add(copyButton);

        w = new FlipButton();
        widgets.add(w);
    }

    private class TacticsBoard extends Picture {

        TacticsBoard() {
            Texture texture = new Texture("images/tactics_board.png");
            textureRegion = new TextureRegion(texture);
            textureRegion.flip(false, true);
            setGeometry(580, 110, 396, 576);
            hAlign = HAlign.LEFT;
            vAlign = VAlign.TOP;
        }
    }

    private class PlayerFaceButton extends Button {

        int position;

        PlayerFaceButton(int position) {
            this.position = position;
            setGeometry(60, 114 + 22 * position, 24, 20);
            setImagePosition(2, -2);
            setActive(false);
            setAddShadow(true);
        }

        @Override
        public void refresh() {
            if (game.tacticsFlip) {
                setActive((position > 0) && (position < TEAM_SIZE));
                setPlayerFlipColor(this, position);
            } else {
                setActive(false);
                setPlayerWidgetColor(this, position);
            }
            Player player = team.playerAtPosition(position);
            if (player == null) {
                textureRegion = null;
            } else {
                textureRegion = player.createFace();
            }
        }

        @Override
        public void onFire1Down() {
            // swap and pair are mutually exclusive
            if (selectedForSwap != -1) {
                return;
            }

            // select
            if (selectedForPair == -1) {
                selectedForPair = position;
            }

            // deselect
            else if (selectedForPair == position) {
                selectedForPair = -1;
            }

            // add / delete pair
            else {
                pushUndoStack();

                int ply1 = team.playerIndexAtPosition(selectedForPair, game.editedTactics);
                int ply2 = team.playerIndexAtPosition(position, game.editedTactics);

                game.editedTactics.addDeletePair(ply1, ply2);
                selectedForPair = -1;
            }

            refreshAllWidgets();
        }
    }

    private class PlayerNumberButton extends Button {

        int position;

        PlayerNumberButton(int position) {
            this.position = position;
            setGeometry(86, 114 + 22 * position, 30, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(position);
            if (player == null) {
                setText("");
            } else {
                setText(player.number);
            }
        }
    }

    private class PlayerNameButton extends Button {

        int position;

        PlayerNameButton(int position) {
            this.position = position;
            setGeometry(118, 114 + 22 * position, 276, 20);
            setText("", Font.Align.LEFT, Assets.font10);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(position);
            if (player == null) {
                setText("");
                setActive(false);
            } else {
                setText(player.name);
                setActive(true);
            }
            setPlayerWidgetColor(this, position);
        }

        @Override
        protected void onFire1Down() {

            // swap and pair are mutually exclusive
            if (selectedForPair != -1) {
                return;
            }

            // select
            if (selectedForSwap == -1) {
                selectedForSwap = position;
            }

            // deselect
            else if (selectedForSwap == position) {
                selectedForSwap = -1;
            }

            // swap
            else {
                int ply1 = team.playerIndexAtPosition(selectedForSwap, game.editedTactics);
                int ply2 = team.playerIndexAtPosition(position, game.editedTactics);

                Collections.swap(team.players, ply1, ply2);

                selectedForSwap = -1;
            }
            refreshAllWidgets();
        }
    }

    private class PlayerNationalityFlagButton extends Button {

        int position;

        PlayerNationalityFlagButton(int position) {
            this.position = position;
            setGeometry(396, 114 + 22 * position, 30, 20);
            setImagePosition(2, 3);
            setActive(false);
            setAddShadow(true);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(position);
            if (player == null) {
                textureRegion = null;
            } else {
                textureRegion = Assets.getNationalityFlag(player.nationality);
            }
            setVisible(team.type != Team.Type.NATIONAL);
        }
    }

    private class PlayerNationalityCodeButton extends Button {

        int position;

        PlayerNationalityCodeButton(int position) {
            this.position = position;
            setGeometry(396, 114 + 22 * position, 58, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(position);
            if (player == null) {
                setText("");
            } else {
                setText("(" + player.nationality + ")");
            }
        }
    }

    private class PlayerRoleButton extends Button {

        int position;

        PlayerRoleButton(int x, int position) {
            this.position = position;
            setGeometry(x, 114 + 22 * position, 34, 20);
            setText("", Font.Align.CENTER, Assets.font10);
            setActive(false);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(position);
            if (player == null) {
                setText("");
            } else {
                setText(Assets.strings.get(player.getRoleLabel()));
            }
        }
    }

    private class PlayerSkillButton extends Button {

        int pos;
        int skillIndex;

        PlayerSkillButton(int pos, int skillIndex, int x) {
            this.pos = pos;
            this.skillIndex = skillIndex;
            setGeometry(x, 114 + 22 * pos, 13, 20);
            setText("", Font.Align.CENTER, font10yellow);
            setActive(false);
        }

        @Override
        public void refresh() {
            Player player = team.playerAtPosition(pos);
            if (player == null) {
                setText("");
            } else {
                Player.Skill[] skills = player.getOrderedSkills();
                if (skills == null) {
                    setText("");
                } else {
                    setText(Assets.strings.get(Player.getSkillLabel(skills[skillIndex])));
                }
            }
        }
    }

    private class CopyButton extends Button {

        CopyButton() {
            setGeometry(1134 - 140 / 2, 175, 140, 36);
            setText(Assets.strings.get("TACTICS.COPY"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (copyMode) {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            } else {
                setColors(0x1769BD, 0x3A90E8, 0x10447A);
                setActive(true);
            }
        }

        @Override
        protected void onFire1Down() {
            copyMode = true;
            // TODO
            // selectedWidget = ballCopy;
            // ballCopy.setEntryMode(true);
            ballCopyZone[0] = ballZone[0];
            ballCopyZone[1] = ballZone[1];
            setDirty(true);
            // TODO
            //updateBallCopyPiece();
        }
    }

    private class FlipButton extends Button {

        FlipButton() {
            setGeometry(1134 - 150 / 2, 240, 150, 36);
            setColors(0x536B90, 0x7090C2, 0x263142);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (game.tacticsFlip) {
                setText(Assets.strings.get("TACTICS.FLIP ON"));
            } else {
                setText(Assets.strings.get("TACTICS.FLIP OFF"));
            }
        }

        @Override
        protected void onFire1Down() {
            game.tacticsFlip = !game.tacticsFlip;
            refreshAllWidgets();
        }
    }

    private void pushUndoStack() {
        Tactics tactics = new Tactics();
        tactics.copyFrom(game.editedTactics);
        game.tacticsUndo.push(tactics);

        // TODO
        // updateUndoButton();
    }

    private void setPlayerWidgetColor(Widget w, int pos) {
        // selected for swap
        if (selectedForSwap == pos) {
            w.setColors(0x993333);
        }
        // selected for pair
        else if (selectedForPair == pos) {
            w.setColors(0x339999);
        }
        // goalkeeper
        else if (pos == 0) {
            w.setColors(0x0094DE);
        }
        // other player
        else if (pos < TEAM_SIZE) {
            w.setColors(0x005DDE);
        }
        // bench / out
        else if (pos < team.players.size()) {
            int benchSize;
            if (team.match != null && team.match.competition != null) {
                benchSize = team.match.competition.benchSize;
            } else {
                benchSize = game.settings.benchSize;
            }
            // bench
            if (pos < TEAM_SIZE + benchSize) {
                w.setColors(0x0046A6);
            }
            // out
            else if (pos < team.players.size()) {
                w.setColors(0x303030);
            }
        }
        // void
        else {
            w.setColors(0x101010);
        }
    }

    private void setPlayerFlipColor(Widget w, int pos) {
        if ((pos > 0) && (pos < TEAM_SIZE)) {
            int baseTactics = game.editedTactics.basedOn;
            int ply = Tactics.order[baseTactics][pos];
            switch (game.editedTactics.pairs[ply]) {
                case 0:
                    w.setColors(0x5FC24A);
                    break;

                case 1:
                    w.setColors(0xCC3E4C);
                    break;

                case 2:
                    w.setColors(0x9D9A98);
                    break;

                case 3:
                    w.setColors(0xBE8445);
                    break;

                case 4:
                    w.setColors(0xBD4DB8);
                    break;

                case 255:
                    w.setColors(null);
                    break;

                default:
                    throw new GdxRuntimeException("invalid pair value");
            }
        } else {
            w.setColors(null);
        }
    }
}
