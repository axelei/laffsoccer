package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
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

    private Team team;
    private int selectedForSwap;
    private int selectedForPair;

    EditTactics(GLGame game) {
        super(game);
        team = game.tacticsTeam;
        selectedForSwap = -1;
        selectedForPair = -1;

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        Widget w;

        w = new TitleBar(Assets.strings.get("EDIT TACTICS") + " (" + Tactics.codes[game.tacticsToEdit] + ")", 0xBA9206);
        widgets.add(w);

        w = new TacticsBoard();
        widgets.add(w);

        // players
        for (int pos = 0; pos < FULL_TEAM; pos++) {

            w = new PlayerFaceButton(pos);
            widgets.add(w);

            w = new PlayerNumberButton(pos);
            widgets.add(w);

            w = new PlayerNameButton(pos);
            widgets.add(w);

            int x = 396;
            if (team.type != Team.Type.NATIONAL) {
                w = new PlayerNationalityFlagButton(pos);
                widgets.add(w);
                x += 28;
            }
        }
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
            pairPlayer(position);
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
            setGeometry(396, 114 + 22 * position, 26, 20);
            setImagePosition(1, 3);
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

    private void pairPlayer(int n) {
        // swap and pair are mutually exclusive
        if (selectedForSwap != -1) {
            return;
        }

        // select
        if (selectedForPair == -1) {
            selectedForPair = n;
        }

        // deselect
        else if (selectedForPair == n) {
            selectedForPair = -1;
        }

        // add / delete pair
        else {
            pushUndoStack();

            int ply1 = team.playerIndexAtPosition(selectedForSwap, game.editedTactics);
            int ply2 = team.playerIndexAtPosition(n, game.editedTactics);

            game.editedTactics.addDeletePair(ply1, ply2);
            selectedForPair = -1;
        }

        refreshAllWidgets();
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
            w.setColors(0x993333, 0xC24242, 0x5A1E1E);
        }
        // selected for pair
        else if (selectedForPair == pos) {
            w.setColors(0x339999, 0x42C2C2, 0x1E5A5A);
        }
        // goalkeeper
        else if (pos == 0) {
            w.setColors(0x00A7DE, 0x33CCFF, 0x005F7E);
        }
        // other player
        else if (pos < TEAM_SIZE) {
            w.setColors(0x003FDE, 0x255EFF, 0x00247E);
        }
        // bench
        else if (pos < TEAM_SIZE + game.settings.benchSize) {
            w.setColors(0x111188, 0x2D2DB3, 0x001140);
        }
        // reserve
        else if (pos < team.players.size()) {
            w.setColors(0x404040, 0x606060, 0x202020);
        }
        // void
        else {
            w.setColors(0x202020, 0x404040, 0x101010);
        }
    }

    private void setPlayerFlipColor(Widget w, int pos) {
        if ((pos > 0) && (pos < TEAM_SIZE)) {
            int baseTactics = game.editedTactics.basedOn;
            int ply = Tactics.order[baseTactics][pos];
            switch (game.editedTactics.pairs[ply]) {
                case 0:
                    w.setColors(0x5FC24A, 0x78F55D, 0x468F36);
                    break;

                case 1:
                    w.setColors(0xCC3E4C, 0xFF4E5F, 0x992F39);
                    break;

                case 2:
                    w.setColors(0x9D9A98, 0xD1CDCA, 0x6B6968);
                    break;

                case 3:
                    w.setColors(0xBE8445, 0xF2A858, 0x8C6133);
                    break;

                case 4:
                    w.setColors(0xBD4DB8, 0xF062E9, 0x8A3886);
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
