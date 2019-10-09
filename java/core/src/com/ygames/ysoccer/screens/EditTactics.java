package com.ygames.ysoccer.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Picture;
import com.ygames.ysoccer.gui.Piece;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Data;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.PlayerSprite;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;

import java.util.Collections;

import static com.ygames.ysoccer.framework.GLGame.State.NONE;
import static com.ygames.ysoccer.match.Const.FULL_TEAM;
import static com.ygames.ysoccer.match.Const.TACT_DX;
import static com.ygames.ysoccer.match.Const.TACT_DY;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;

class EditTactics extends GLScreen {

    private boolean copyMode;
    private Team team;
    private int selectedForSwap;
    private int selectedForPair;
    private int[] ballZone;
    private int[] ballCopyZone;

    private Font font10yellow;
    private TextureRegion ballTextureRegion;
    private TextureRegion ballCopyTextureRegion;

    private Piece ball;
    private Piece ballCopy;
    private Piece[] players = new Piece[TEAM_SIZE];
    private PlayerSprite[] playerSprite = new PlayerSprite[FULL_TEAM];

    private Widget copyButton;
    private Widget undoButton;
    private Widget saveButton;
    private Widget exitButton;

    EditTactics(GLGame game) {
        super(game);

        copyMode = false;
        team = game.tacticsTeam;
        selectedForSwap = -1;
        selectedForPair = -1;
        ballZone = new int[]{0, 0};
        ballCopyZone = new int[]{0, 0};

        background = new Texture("images/backgrounds/menu_set_team.jpg");
        ballTextureRegion = new TextureRegion(new Texture("images/ball.png"), 0, 0, 8, 8);
        ballTextureRegion.flip(false, true);
        ballCopyTextureRegion = new TextureRegion(new Texture("images/ballsnow.png"), 0, 0, 8, 8);
        ballCopyTextureRegion.flip(false, true);

        for (int ply = 0; ply < team.players.size(); ply++) {
            Player player = team.players.get(ply);
            player.data[0] = new Data();
            player.isVisible = true;
            if (player.role == Player.Role.GOALKEEPER) {
                Assets.loadKeeper(player);
            } else {
                Assets.loadPlayer(player, team.kits.get(team.kitIndex));
            }
            Assets.loadHair(player);
            player.fmx = 2;
            player.fmy = 1;
            playerSprite[ply] = new PlayerSprite(game.glGraphics, player);
        }

        font10yellow = new Font(10, 13, 17, 12, 16, new RgbPair(0xFCFCFC, 0xFCFC00));
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

            int x = 404;
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
        }

        w = new TacticsBoard();
        widgets.add(w);

        ball = new BallPiece();
        widgets.add(ball);

        ballCopy = new BallCopyPiece();
        widgets.add(ballCopy);

        for (int ply = 0; ply < TEAM_SIZE; ply++) {
            players[ply] = new PlayerPiece(ply);
            widgets.add(players[ply]);
        }

        copyButton = new CopyButton();
        widgets.add(copyButton);

        w = new FlipButton();
        widgets.add(w);

        undoButton = new UndoButton();
        widgets.add(undoButton);

        w = new ImportButton();
        widgets.add(w);

        saveButton = new SaveExitButton();
        widgets.add(saveButton);

        setSelectedWidget(w);

        exitButton = new AbortButton();
        widgets.add(exitButton);
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

    private class BallPiece extends Piece {

        BallPiece() {
            setSize(24, 14);
            textureRegion = ballTextureRegion;
            setImagePosition(6, -2);
            setRanges(0, 4, 0, 6);
            setGridGeometry(604, 155, 324, 472);
            setActive(true);
        }

        @Override
        public void refresh() {
            setSquare(2 - ballZone[0], 3 - ballZone[1]);
        }

        @Override
        public void onChanged() {
            ballZone[0] = 2 - square[0];
            ballZone[1] = 3 - square[1];

            updatePlayerPieces();
        }

        @Override
        public void onFire1Down() {
            toggleEntryMode();
        }
    }

    private class BallCopyPiece extends Piece {

        BallCopyPiece() {
            setSize(24, 14);
            textureRegion = ballCopyTextureRegion;
            setImagePosition(6, -2);
            setRanges(0, 4, 0, 6);
            setGridGeometry(604, 155, 324, 472);
        }

        @Override
        public void refresh() {
            setVisible(copyMode);
            setSquare(2 - ballCopyZone[0], 3 - ballCopyZone[1]);
        }

        @Override
        public void onChanged() {
            ballCopyZone[0] = 2 - square[0];
            ballCopyZone[1] = 3 - square[1];
        }

        @Override
        public void onFire1Down() {
            ballCopy();
        }

        private void ballCopy() {
            // copy tactics
            pushUndoStack();

            int toZone = 17 + ballCopyZone[0] + 5 * ballCopyZone[1];
            int fromZone = 17 + ballZone[0] + 5 * ballZone[1];
            for (int p1 = 1; p1 < TEAM_SIZE; p1++) {

                // flip mode on
                if (game.tacticsFlip && Math.signum(ballZone[0] * ballCopyZone[0]) == -1) {
                    // paired players
                    if (game.editedTactics.isPaired(p1)) {
                        int p2 = game.editedTactics.getPaired(p1);
                        game.editedTactics.target[p1][toZone][0] = -game.editedTactics.target[p2][fromZone][0];
                        game.editedTactics.target[p1][toZone][1] = +game.editedTactics.target[p2][fromZone][1];
                    } else {
                        game.editedTactics.target[p1][toZone][0] = -game.editedTactics.target[p1][fromZone][0];
                        game.editedTactics.target[p1][toZone][1] = +game.editedTactics.target[p1][fromZone][1];
                    }
                } else {
                    game.editedTactics.target[p1][toZone][0] = game.editedTactics.target[p1][fromZone][0];
                    game.editedTactics.target[p1][toZone][1] = game.editedTactics.target[p1][fromZone][1];
                }
            }

            // copy flipped zone
            if (game.tacticsFlip && (Math.signum(ballZone[0] * ballCopyZone[0]) != 0)) {
                int flippedToZone = 17 - ballCopyZone[0] + 5 * ballCopyZone[1];
                for (int p1 = 1; p1 < TEAM_SIZE; p1++) {

                    // paired players
                    if (game.editedTactics.isPaired(p1)) {
                        int p2 = game.editedTactics.getPaired(p1);
                        game.editedTactics.target[p1][flippedToZone][0] = -game.editedTactics.target[p2][toZone][0];
                        game.editedTactics.target[p1][flippedToZone][1] = +game.editedTactics.target[p2][toZone][1];
                    } else {
                        game.editedTactics.target[p1][flippedToZone][0] = -game.editedTactics.target[p1][toZone][0];
                        game.editedTactics.target[p1][flippedToZone][1] = +game.editedTactics.target[p1][toZone][1];
                    }
                }
            }

            // disable copy mode
            copyMode = false;
            setSelectedWidget(ball);
            setEntryMode(false);
            copyButton.setDirty(true);
            setDirty(true);

            // update ball zone
            ballZone[0] = ballCopyZone[0];
            ballZone[1] = ballCopyZone[1];
            ball.setDirty(true);

            updatePlayerPieces();
        }
    }

    private class PlayerPiece extends Piece {

        int ply;
        Player player;

        PlayerPiece(int ply) {
            this.ply = ply;
            setSize(24, 14);
            setRanges(0, 14, 0, 15);
            if (ply == 0) {
                setGridGeometry(580, 110, 372, 562);
                setActive(false);
            } else {
                setGridGeometry(584, 130, 364, 522);
                setActive(true);
            }
        }

        @Override
        public void refresh() {
            player = team.players.get(ply);

            // update position
            if (ply == 0) {
                setSquare(7, 15);
            } else {
                int[] target = game.editedTactics.target[ply][17 + ballZone[0] + 5 * ballZone[1]];
                setSquare(14 - (target[0] / TACT_DX + 7), 15 - ((target[1] / TACT_DY + 15) / 2));
            }
        }

        @Override
        public void onChanged() {
            pushUndoStack();

            int[] target = new int[2];
            target[0] = (7 - square[0]) * TACT_DX;
            target[1] = (15 - 2 * square[1]) * TACT_DY;
            int currentZone = 17 + ballZone[0] + 5 * ballZone[1];
            game.editedTactics.target[ply][currentZone] = target;

            // flip mode on
            if (game.tacticsFlip && (ballZone[0] != 0)) {
                int flippedZone = 17 - ballZone[0] + 5 * ballZone[1];
                for (int p1 = 1; p1 < TEAM_SIZE; p1++) {

                    // paired players
                    if (game.editedTactics.isPaired(p1)) {
                        int p2 = game.editedTactics.getPaired(p1);
                        game.editedTactics.target[p1][flippedZone][0] = -game.editedTactics.target[p2][currentZone][0];
                        game.editedTactics.target[p1][flippedZone][1] = +game.editedTactics.target[p2][currentZone][1];
                    } else {
                        game.editedTactics.target[p1][flippedZone][0] = -game.editedTactics.target[p1][currentZone][0];
                        game.editedTactics.target[p1][flippedZone][1] = +game.editedTactics.target[p1][currentZone][1];
                    }
                }
            }
        }

        @Override
        public void onFire1Down() {
            toggleEntryMode();
        }

        @Override
        protected void drawImage(GLSpriteBatch batch) {
            Player player = team.players.get(ply);
            if (player != null) {
                player.x = x + w / 2f - 1;
                player.y = y + h / 2f;

                batch.begin();

                player.save(0);

                Data d = player.data[0];
                Integer[] origin = Assets.playerOrigins[d.fmy][d.fmx];
                float mX = 0.65f;
                float mY = 0.46f;
                batch.draw(Assets.playerShadow[d.fmx][d.fmy][0], d.x - origin[0] + mX * d.z, d.y - origin[1] + 5 + mY * d.z);

                playerSprite[ply].draw(0);

                // number
                int f0 = player.number % 10;
                int f1 = (player.number - f0) / 10 % 10;

                int dx = x + w / 2;
                int dy = y - 32;

                int w0 = 6 - (f0 == 1 ? 2 : 0);
                int w1 = 6 - (f1 == 1 ? 2 : 0);

                if (f1 > 0) {
                    dx = dx - (w0 + 2 + w1) / 2;
                    batch.draw(Assets.playerNumbers[f1][0], dx, dy);
                    dx = dx + w1 + 2;
                    batch.draw(Assets.playerNumbers[f0][0], dx, dy);
                } else {
                    batch.draw(Assets.playerNumbers[f0][0], dx - w0 / 2f, dy);
                }
                batch.end();
            }
        }
    }

    private class PlayerFaceButton extends Button {

        int position;

        PlayerFaceButton(int position) {
            this.position = position;
            setGeometry(70, 114 + 22 * position, 24, 20);
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
            setGeometry(96, 114 + 22 * position, 30, 20);
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
            setGeometry(128, 114 + 22 * position, 276, 20);
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
            setGeometry(406, 114 + 22 * position, 30, 20);
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
            setGeometry(406, 114 + 22 * position, 58, 20);
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
            setGeometry(1120 - 170 / 2, 175, 170, 40);
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
            setActive(false);
            ballCopy.setVisible(true);
            ballCopy.setEntryMode(true);
            setSelectedWidget(ballCopy);
            ballCopyZone[0] = ballZone[0];
            ballCopyZone[1] = ballZone[1];
            setDirty(true);
            ballCopy.setDirty(true);
        }
    }

    private class FlipButton extends Button {

        FlipButton() {
            setGeometry(1120 - 170 / 2, 240, 170, 40);
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

    private class UndoButton extends Button {

        UndoButton() {
            setGeometry(1120 - 170 / 2, 305, 170, 40);
            setText(Assets.strings.get("TACTICS.UNDO"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            if (game.tacticsUndo.isEmpty()) {
                setColors(0x666666, 0x8F8D8D, 0x404040);
                setActive(false);
            } else {
                setColors(0xBDBF2F, 0xF0F23C, 0x8B8C23);
                setActive(true);
            }
        }

        @Override
        protected void onFire1Down() {
            if (game.tacticsUndo.isEmpty()) {
                return;
            }
            game.editedTactics = game.tacticsUndo.pop();
            refreshAllWidgets();
        }
    }

    private class ImportButton extends Button {

        ImportButton() {
            setGeometry(1120 - 170 / 2, 460, 170, 40);
            setColors(0xAB148D, 0xDE1AB7, 0x780E63);
            setText(Assets.strings.get("TACTICS.IMPORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            game.setScreen(new ImportTactics(game));
        }
    }

    private class SaveExitButton extends Button {

        SaveExitButton() {
            setGeometry(1120 - 240 / 2, 525, 240, 40);
            setColors(0x10A000, 0x15E000, 0x096000);
            setText(Assets.strings.get("SAVE") + "/" + Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Up() {
            if (game.tacticsUndo.isEmpty()) {
                // exit
                if (game.getState() == NONE) {
                    game.setScreen(new Main(game));
                } else {
                    game.setScreen(new SetTeam(game));
                }
            } else {
                // save warning
                game.setScreen(new SaveTacticsWarning(game));
            }
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setGeometry(1120 - 170 / 2, 590, 170, 40);
            setColor(0xC84200);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        protected void onFire1Down() {
            if (game.tacticsUndo.isEmpty()) {
                if (game.getState() == NONE) {
                    game.setScreen(new Main(game));
                } else {
                    game.setScreen(new SetTeam(game));
                }
            } else {
                game.setScreen(new TacticsAbortWarning(game));
            }
        }
    }

    private void pushUndoStack() {
        Tactics tactics = new Tactics();
        tactics.copyFrom(game.editedTactics);
        game.tacticsUndo.push(tactics);
        undoButton.setDirty(true);
        saveButton.setDirty(true);
        exitButton.setDirty(true);
    }

    private void setPlayerWidgetColor(Widget w, int pos) {
        // selected for swap
        if (selectedForSwap == pos) {
            w.setColor(0x993333);
        }
        // selected for pair
        else if (selectedForPair == pos) {
            w.setColor(0x339999);
        }
        // goalkeeper
        else if (pos == 0) {
            w.setColor(0x0094DE);
        }
        // other player
        else if (pos < TEAM_SIZE) {
            w.setColor(0x005DDE);
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
                w.setColor(0x0046A6);
            }
            // out
            else {
                w.setColor(0x303030);
            }
        }
        // void
        else {
            w.setColor(0x101010);
        }
    }

    private void setPlayerFlipColor(Widget w, int pos) {
        if ((pos > 0) && (pos < TEAM_SIZE)) {
            int baseTactics = game.editedTactics.basedOn;
            int ply = Tactics.order[baseTactics][pos];
            switch (game.editedTactics.pairs[ply]) {
                case 0:
                    w.setColor(0x5FC24A);
                    break;

                case 1:
                    w.setColor(0xCC3E4C);
                    break;

                case 2:
                    w.setColor(0x9D9A98);
                    break;

                case 3:
                    w.setColor(0xBE8445);
                    break;

                case 4:
                    w.setColor(0xBD4DB8);
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

    private void updatePlayerPieces() {
        for (Piece player : players) {
            player.setDirty(true);
        }
    }
}
