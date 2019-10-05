package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.export.Config;
import com.ygames.ysoccer.export.FileConfig;
import com.ygames.ysoccer.export.TeamConfig;
import com.ygames.ysoccer.framework.GLColor;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.framework.Assets.fileComparatorByName;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Assets.teamsRootFolder;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static com.ygames.ysoccer.match.Player.Skill.CONTROL;
import static com.ygames.ysoccer.match.Player.Skill.FINISHING;
import static com.ygames.ysoccer.match.Player.Skill.HEADING;
import static com.ygames.ysoccer.match.Player.Skill.PASSING;
import static com.ygames.ysoccer.match.Player.Skill.SHOOTING;
import static com.ygames.ysoccer.match.Player.Skill.SPEED;
import static com.ygames.ysoccer.match.Player.Skill.TACKLING;

class ExportTeams extends GLScreen {

    private enum State {NO_FOLDERS, READY, EXPORTING, FINISHED}

    private State state = State.NO_FOLDERS;

    private Json json;

    private Config exportConfig;
    private KitStyle[] kitStyles;
    private int exportConfigIndex;
    private FileHandle exportFolder;
    private int exportedTeams;
    private int notFoundTeams;
    private Widget exitButton;

    ExportTeams(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        exportFolder = Gdx.files.local("data/export");
        kitStyles = json.fromJson(KitStyle[].class, Gdx.files.local("data/config/kit_styles.json").readString("UTF-8"));

        Widget w;

        w = new TitleBar(gettext("EXPORT"), 0x762B8E);
        widgets.add(w);

        // Folders buttons
        List<Widget> list = new ArrayList<>();
        ArrayList<FileHandle> files = new ArrayList<>(Arrays.asList(teamsRootFolder.list()));
        Collections.sort(files, fileComparatorByName);
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                list.add(w);
                widgets.add(w);
            }
        }

        if (list.size() > 0) {
            state = State.READY;
            Widget.arrange(game.gui.WIDTH, 380, 34, 20, list);
            setSelectedWidget(list.get(0));
        }
        w = new InfoLabel();
        widgets.add(w);

        exitButton = new ExitButton();
        widgets.add(exitButton);

        if (getSelectedWidget() == null) {
            setSelectedWidget(exitButton);
        }
    }

    private class FolderButton extends Button {

        private FileHandle configFile;

        FolderButton(FileHandle folder) {
            configFile = Gdx.files.local("data/config/export_" + folder.name() + ".json");
            setSize(340, 32);
            if (configFile.exists()) {
                setColors(0x568200, 0x77B400, 0x243E00);
            } else {
                setColor(0x666666);
                setActive(false);
            }
            setText(folder.name().replace('_', ' '), CENTER, font14);
        }

        @Override
        public void refresh() {
            setVisible(state == State.READY);
        }

        @Override
        public void onFire1Down() {
            exportConfig = json.fromJson(Config.class, configFile.readString("UTF-8"));
            state = State.EXPORTING;
            refreshAllWidgets();
        }
    }

    private class InfoLabel extends Label {

        InfoLabel() {
            setGeometry((game.gui.WIDTH - 400) / 2, 300, 400, 40);
            setText("", CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (state) {
                case NO_FOLDERS:
                    setText("NO FOLDERS");
                    break;
                case READY:
                    break;
                case EXPORTING:
                    setText("EXPORTING " + (exportConfigIndex + 1) + "/" + exportConfig.files.size());
                    break;
                case FINISHED:
                    String message = exportedTeams + " TEAMS EXPORTED";
                    if (notFoundTeams > 0) message += " - " + notFoundTeams + " TEAMS NOT FOUND";
                    setText(message);
                    break;
            }
            setVisible(state != State.READY);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (state) {
                case NO_FOLDERS:
                case FINISHED:
                    setColor(0xC84200);
                    setText(gettext("EXIT"));
                    break;
                case READY:
                    setText(gettext("ABORT"));
                    setColor(0xC8000E);
                    break;
            }
            setVisible(state != State.EXPORTING);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        switch (state) {
            case EXPORTING:
                if (exportConfigIndex < exportConfig.files.size()) {
                    FileConfig exportFileConfig = exportConfig.files.get(exportConfigIndex++);
                    exportFile(exportFileConfig);
                } else {
                    state = State.FINISHED;
                }
                refreshAllWidgets();
                break;
            case FINISHED:
                setSelectedWidget(exitButton);
                refreshAllWidgets();
                break;
        }
    }

    private boolean exportFile(FileConfig exportConfig) {

        FileHandle file = exportFolder.child(exportConfig.filename);

        // number of teams
        int numberOfTeams = 0;
        for (TeamConfig teamConfig : exportConfig.teams) {
            FileHandle sourceFile = teamsRootFolder.child(teamConfig.path);
            if (sourceFile.exists()) {
                teamConfig.sourceFile = sourceFile;
                numberOfTeams++;
            } else {
                Gdx.app.log("File not found", sourceFile.path());
                notFoundTeams++;
            }
        }
        file.writeBytes(getHalfWord(numberOfTeams), false);

        int teamIndex = 0;
        for (TeamConfig teamConfig : exportConfig.teams) {
            if (teamConfig.sourceFile == null) {
                continue;
            }

            Team team = json.fromJson(Team.class, teamConfig.sourceFile);

            file.writeBytes(getByte(teamConfig.country), true);

            file.writeBytes(getByte(teamIndex), true);

            file.writeBytes(getHalfWord(teamConfig.gtn), true);

            // unknown
            file.writeBytes(getByte(0), true);

            // team name
            writePaddedString(file, decompose(team.name), 16);

            // unknown
            file.writeBytes(getByte(0), true);
            file.writeBytes(getByte(0), true);
            file.writeBytes(getByte(0), true);

            file.writeBytes(getByte(team.tactics), true);

            file.writeBytes(getByte(teamConfig.division), true);

            // home/away kits
            for (int i = 0; i < 2; i++) {
                Kit kit = team.kits.get(i);
                file.writeBytes(getByte(getKitStyleIndex(kit.style)), true);
                file.writeBytes(getByte(getKitColorIndex(kit.shirt1)), true);
                file.writeBytes(getByte(getKitColorIndex(kit.shirt2)), true);
                file.writeBytes(getByte(getKitColorIndex(kit.shorts)), true);
                file.writeBytes(getByte(getKitColorIndex(kit.socks)), true);
            }

            // coach name
            writePaddedString(file, decompose(team.coach.name), 24);

            // players' positions
            for (int i = 0; i < 16; i++) {
                file.writeBytes(getByte(i), true);
            }

            // players
            for (int i = 0; i < 16; i++) {
                Player player = team.players.get(i);

                file.writeBytes(getByte(getPlayerNationalityIndex(player.nationality)), true);

                // unknown
                file.writeBytes(getByte(0), true);

                // shirt number
                file.writeBytes(getByte(player.number), true);

                // name
                writePaddedString(file, decompose(player.name), 22);

                // cards and injures
                file.writeBytes(getByte(0), true);

                // role + skin color + hair color
                int role = player.role.ordinal();
                int skinColor = getSkinColorIndex(player.skinColor);
                int hairColor;
                if (skinColor == 0) {
                    hairColor = getHairColorIndex(player.hairColor);
                } else {
                    hairColor = 0;
                }
                file.writeBytes(getByte(role << 5 | skinColor << 4 | hairColor << 3), true);

                // unknown
                file.writeBytes(getByte(0), true);

                Player.Skills skills = player.skills;
                int passing = skills.passing;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(PASSING)) {
                    passing |= 8;
                }
                file.writeBytes(getByte(passing), true);

                int shooting = skills.shooting;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(SHOOTING)) {
                    shooting |= 8;
                }
                int heading = skills.heading;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(HEADING)) {
                    heading |= 8;
                }
                file.writeBytes(getByte(shooting << 4 | heading), true);

                int tackling = skills.tackling;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(TACKLING)) {
                    tackling |= 8;
                }
                int control = skills.control;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(CONTROL)) {
                    control |= 8;
                }
                file.writeBytes(getByte(tackling << 4 | control), true);

                int speed = skills.speed;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(SPEED)) {
                    speed |= 8;
                }
                int finishing = skills.finishing;
                if (player.role != Player.Role.GOALKEEPER && player.bestSkills.contains(FINISHING)) {
                    finishing |= 8;
                }
                file.writeBytes(getByte(speed << 4 | finishing), true);

                // value
                file.writeBytes(getByte(player.getValue()), true);

                // league goals (career)
                file.writeBytes(getByte(0), true);

                // cup goals (career)
                file.writeBytes(getByte(0), true);

                // unknown (career)
                file.writeBytes(getByte(0), true);
                file.writeBytes(getByte(0), true);
                file.writeBytes(getByte(0), true);
            }

            exportedTeams++;

            teamIndex++;
        }

        return true;
    }

    private byte[] getByte(int i) {
        return new byte[]{(byte) i};
    }

    private byte[] getHalfWord(int i) {
        return new byte[]{(byte) (i >>> 8), (byte) (i & 0xFF)};
    }

    private void writePaddedString(FileHandle file, String string, int totalLength) {
        if (string.length() > totalLength) {
            file.writeString(string.substring(0, totalLength), true);
        } else {
            file.writeString(string, true);
            padWithZeroes(file, string.length(), totalLength);
        }
    }

    private void padWithZeroes(FileHandle file, int stringLength, int totaLength) {
        if (stringLength < totaLength) {
            for (int i = stringLength; i < totaLength; i++) {
                file.writeBytes(getByte(0), true);
            }
        }
    }

    private int getKitStyleIndex(String style) {
        for (KitStyle kitStyle : kitStyles) {
            if (kitStyle.name.equals(style)) {
                return kitStyle.index;
            }
        }
        return 0;
    }

    private int getKitColorIndex(int color) {
        int index = 0;
        float difference = GLColor.difference(color, Kit.colors[0]);
        for (int i = 1; i < 10; i++) {
            if (GLColor.difference(color, Kit.colors[i]) < difference) {
                difference = GLColor.difference(color, Kit.colors[i]);
                index = i;
            }
        }
        return index;
    }

    private int getPlayerNationalityIndex(String nationality) {
        for (int i = 0; i < ImportTeams.playerCountryCodes.length; i++) {
            if (ImportTeams.playerCountryCodes[i].equals(nationality)) return i;
        }
        return 0;
    }

    private int getHairColorIndex(Hair.Color color) {
        switch (color) {
            case BLACK:
                return 0;
            case BLOND:
                return 1;
            case BROWN:
                return 0;
            case GRAY:
                return 0;
            case PLATINUM:
                return 1;
            case PUNK_BLOND:
                return 1;
            case PUNK_FUCHSIA:
                return 0;
            case RED:
                return 1;
            case WHITE:
                return 1;
            default:
                return 0;
        }
    }

    private int getSkinColorIndex(Skin.Color color) {
        switch (color) {
            case PINK:
                return 0;
            case BLACK:
                return 1;
            case PALE:
                return 0;
            case ASIATIC:
                return 0;
            case ARAB:
                return 0;
            case MULATTO:
                return 1;
            case RED:
                return 0;
            default:
                return 0;
        }
    }

    private static class KitStyle {
        String name;
        int index;
    }

    private static String decompose(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
