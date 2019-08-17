package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Widget;
import com.ysoccer.android.ysdemo.match.Team;
import com.ysoccer.android.ysdemo.match.TeamComparator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuSelectTeams extends GLScreen {

    private final ContinueButton continueButton;
    private final Team[] teams;

    class TeamButton extends Button {

        Team team;

        TeamButton(Team team) {
            setSize(300, 28);
            setText(team.getName(), 0, 14);
            this.team = team;
            updateColor();
        }

        void updateColor() {
            switch (team.getControlMode()) {
                case UNDEFINED:
                    setColors(0x98691E, 0xC88B28, 0x3E2600);
                    break;
                case COMPUTER:
                    setColors(0x981E1E, 0xC72929, 0x640000);
                    break;
                case PLAYER:
                    setColors(0x0000C8, 0x1919FF, 0x000078);
                    break;
            }
        }

        @Override
        public void onFire1Down() {
            Team home = teams[0];
            Team away = teams[1];
            if ((home != null) && (away != null) && (home != team) && (away != team)) {
                return;
            }

            switch (team.getControlMode()) {
                case UNDEFINED:
                    if (home != null && home != team && home.getControlMode() == Team.ControlMode.PLAYER) {
                        team.setControlMode(Team.ControlMode.COMPUTER);
                    } else {
                        team.setControlMode(Team.ControlMode.PLAYER);
                    }
                    addTeam(team);
                    break;
                case COMPUTER:
                    team.setControlMode(Team.ControlMode.UNDEFINED);
                    removeTeam(team);
                    break;
                case PLAYER:
                    if (home != null && home != team && home.getControlMode() == Team.ControlMode.COMPUTER) {
                        team.setControlMode(Team.ControlMode.UNDEFINED);
                        removeTeam(team);
                    } else if (away != null && away != team && away.getControlMode() == Team.ControlMode.COMPUTER) {
                        team.setControlMode(Team.ControlMode.UNDEFINED);
                        removeTeam(team);
                    } else {
                        team.setControlMode(Team.ControlMode.COMPUTER);
                        addTeam(team);
                    }
                    break;
            }
            updateColor();
            continueButton.updateStatus();
        }
    }

    class BackButton extends Button {

        BackButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((Settings.GUI_WIDTH) / 2 - 300 - 30,
                    Settings.GUI_HEIGHT - 40 - 20, 300, 40);
            setText(gettext(R.string.BACK), 0, 14);
        }

        @Override
        public void onFire1Down() {
            onKeyBackHw();
        }
    }

    class ContinueButton extends Button {

        ContinueButton() {
            setGeometry(Settings.GUI_WIDTH / 2 + 30,
                    Settings.GUI_HEIGHT - 40 - 20, 340, 40);
            setText(gettext(R.string.PLAY_MATCH), 0, 14);
            isActive = false;
            updateStatus();
        }

        void updateStatus() {
            Team home = teams[0];
            Team away = teams[1];
            isActive = (home != null) && (away != null) && (home.getControlMode() != away.getControlMode());
            if (isActive) {
                setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            } else {
                setColors(0x666666, 0x8F8D8D, 0x404040);
            }
        }

        @Override
        public void onFire1Down() {
            teams[0].loadFromFile(glGame);
            teams[0].index = 0;
            teams[1].loadFromFile(glGame);
            teams[1].index = 1;

            game.setScreen(new MenuOptions(game, teams));
        }
    }

    MenuSelectTeams(Game game) {
        super(game);

        Assets.Backgrounds.menuMatch = new Texture(glGame,
                "images/backgrounds/menu_match.jpg");

        setBackground(Assets.Backgrounds.menuMatch);

        teams = new Team[2];

        List<Team> list = loadTeams();

        Widget w;

        w = new Button();
        w.setColors(0x536B90, 0x7090C2, 0x7090C2);
        w.setGeometry((Settings.GUI_WIDTH - 400) / 2, 20, 400, 40);
        w.setText(gettext(R.string.SELECT_TEAMS), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        // player - coach
        w = new Button();
        w.setColors(0x0000C8);
        w.setGeometry(Settings.GUI_WIDTH / 2 - 300 - 20, 80, 60, 32);
        w.isActive = false;
        getWidgets().add(w);

        w = new Button();
        w.setGeometry(Settings.GUI_WIDTH / 2 - 300 - 20 +80, 80, 180, 32);
        w.setText(gettext(R.string.PLAYER), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        // computer
        w = new Button();
        w.setColors(0x981E1E);
        w.setGeometry(Settings.GUI_WIDTH / 2 + 20, 80, 60, 32);
        w.isActive = false;
        getWidgets().add(w);

        w = new Button();
        w.setGeometry(Settings.GUI_WIDTH / 2 + 20 +80, 80, 180, 32);
        w.setText(gettext(R.string.COMPUTER), 0, 14);
        w.isActive = false;
        getWidgets().add(w);

        int len = list.size();
        int bWidth = 300;
        int tm = 0;
        int col1, col2;
        for (int i = 0; i < len; i++) {
            Team team = list.get(i);
            tm = tm + 1;
            w = new TeamButton(team);
            if (len <= 8) {
                w.setX((Settings.GUI_WIDTH - bWidth) / 2);
                w.setY(265 - 30 * len / 2 + 30 * tm);
            } else {
                col1 = len / 3 + (len % 3 == 2 ? 1 : 0);
                col2 = len / 3 + (len % 3 > 0 ? 1 : 0);
                if (tm <= col1) {
                    w.setX((Settings.GUI_WIDTH - 3 * bWidth) / 2 - 8);
                    w.setY(265 - 30 * col2 / 2 + 30 * tm);
                } else if (tm <= col1 + col2) {
                    w.setX((Settings.GUI_WIDTH - bWidth) / 2);
                    w.setY(265 - 30 * col2 / 2 + 30 * (tm - col1));
                } else {
                    w.setX((Settings.GUI_WIDTH + bWidth) / 2 + 8);
                    w.setY(265 - 30 * col2 / 2 + 30 * (tm - col1 - col2));
                }
            }

            if (getSelectedWidget() == null) {
                setSelectedWidget(w);
            }

            getWidgets().add(w);
        }

        w = new BackButton();
        getWidgets().add(w);

        continueButton = new ContinueButton();
        getWidgets().add(continueButton);

    }

    private void addTeam(Team buttonTeam) {
        if (teams[0] == null) {
            teams[0] = buttonTeam;
        } else if (buttonTeam != teams[0] && teams[1] == null) {
            teams[1] = buttonTeam;
        }
    }

    private void removeTeam(Team buttonTeam) {
        if (teams[0] == buttonTeam) {
            teams[0] = teams[1];
        }
        teams[1] = null;
    }

    @Override
    public void resume() {
        super.resume();
        Assets.Backgrounds.menuMatch.reload();
    }

    @Override
    public void onKeyBackHw() {
        game.setScreen(new MenuMain(game));
    }

    private List<Team> loadTeams() {
        List<Team> teams = new ArrayList<>();
        String ext = "wld";

        InputStream in = null;
        try {
            in = glGame.getFileIO().readAsset("data/team_" + ext + ".yst");
            DataInputStream is = new DataInputStream(in);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0 && line.charAt(0) == '#') {

                    // number
                    int number = Integer.parseInt(line.substring(10));

                    // skip Country
                    br.readLine();

                    // name
                    line = br.readLine();
                    String nameKey = line.substring(10).trim();

                    // skip tactics
                    br.readLine();

                    // skip division
                    br.readLine();

                    Team team = new Team(nameKey, ext, number);
                    team.name = glGame.translate(nameKey);

                    teams.add(team);
                }
            }
            Collections.sort(teams, new TeamComparator());

        } catch (IOException e) {
            throw new RuntimeException(
                    "Couldn't load team ", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }

        return teams;
    }
}
