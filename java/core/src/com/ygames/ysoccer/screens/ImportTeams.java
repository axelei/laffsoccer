package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.database.ImportConfig;
import com.ygames.ysoccer.database.ImportFileConfig;
import com.ygames.ysoccer.export.Config;
import com.ygames.ysoccer.export.FileConfig;
import com.ygames.ysoccer.export.TeamConfig;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Coach;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.framework.EMath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;

class ImportTeams extends GLScreen {

    private enum State {NO_FILES, READY, IMPORTING, FINISHED}

    private State state = State.NO_FILES;

    private int year = 2019;

    private final String[] divisions = {
            "PREMIER DIVISION",
            "DIVISION ONE",
            "DIVISION TWO",
            "DIVISION THREE",
            "NO LEAGUE"
    };

    static String[] playerCountryCodes = {
            "ALB", "AUT", "BEL", "BUL", "CRO", "CYP", "CZE", "DEN", "ENG", "EST",
            "FRO", "FIN", "FRA", "GER", "GRE", "HUN", "ISL", "ISR", "ITA", "LVA",
            "LTU", "LUX", "MLT", "NED", "NIR", "NOR", "POL", "POR", "ROM", "RUS",
            "SMR", "SCO", "SVN", "SWE", "TUR", "UKR", "WAL", "SBM", "BLR", "SVK",
            "ESP", "ARM", "BIH", "AZE", "GEO", "SUI", "IRL", "MKD", "TKM", "LIE",
            "MDA", "CRC", "SLV", "GUA", "HON", "BAH", "MEX", "PAN", "USA", "BHR",
            "NCA", "BER", "JAM", "TRI", "CAN", "BRB", "SLV", "VIN", "ARG", "BOL",
            "BRA", "CHI", "COL", "ECU", "PAR", "SUR", "URU", "VEN", "GUY", "PER",
            "ALG", "RSA", "BOT", "BFA", "BDI", "LES", "COD", "ZAM", "GHA", "SEN",
            "CIV", "TUN", "MLI", "MAD", "CMR", "CHA", "UGA", "LBR", "MOZ", "KEN",
            "SUD", "SWZ", "ANG", "TOG", "ZIM", "EGY", "TAN", "NGA", "ETH", "GAB",
            "SLE", "BEN", "CGO", "GUI", "SRI", "MAR", "GAM", "MWI", "JPN", "TPE",
            "IND", "BAN", "BRU", "IRQ", "JOR", "SRI", "SYR", "KOR", "IRN", "VIE",
            "MAS", "KSA", "YEM", "KUW", "LAO", "PRK", "OMA", "PAK", "PHI", "CHN",
            "SIN", "MRI", "MYA", "PNG", "THA", "UZB", "QAT", "UAE", "AUS", "NZL",
            "FIJ", "SOL", "CUS"
    };

    private String[] kitNames = {
            "PLAIN",
            "SLEEVES",
            "VERTICAL",
            "HORIZONTAL"
    };

    private List<Integer> position = new ArrayList<>();
    FileHandle[] files;
    private int fileIndex;
    private int importedTeams, failedTeams, skippedFiles;
    private Widget warningLabel, exitButton;

    private Json json;
    private FileHandle configFile;
    private Config exportConfigs;

    private Map<String, List<String>> leagues = new HashMap<>();

    ImportTeams(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        FileHandle importFolder = Gdx.files.local("data/import");

        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        updateConfigFile();

        exportConfigs = new Config();

        for (int i = 0; i < 16; i++) {
            position.add(i);
        }

        Widget w;

        w = new TitleBar(gettext("IMPORT"), 0x762B8E);
        widgets.add(w);

        w = new InfoLabel();
        widgets.add(w);

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toUpperCase().startsWith("TEAM.");
            }
        };
        files = importFolder.list(filter);

        if (files.length > 0) {
            state = State.READY;

            warningLabel = new WarningLabel();
            widgets.add(warningLabel);

            w = new StartButton();
            widgets.add(w);

            w = new FolderLabel();
            widgets.add(w);

            w = new FolderButton();
            widgets.add(w);
        }

        exitButton = new ExitButton();
        widgets.add(exitButton);

        if (getSelectedWidget() == null) {
            setSelectedWidget(exitButton);
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        switch (state) {
            case IMPORTING:
                if (fileIndex == files.length) {
                    state = State.FINISHED;
                    FileHandle fh = Assets.teamsRootFolder.child(getYearFolder() + "/export_" + getYearFolder() + ".json");
                    fh.writeString(Assets.json.prettyPrint(exportConfigs), false, "UTF-8");
                } else {
                    ImportConfig importConfig = json.fromJson(ImportConfig.class, configFile.readString("UTF-8"));
                    FileHandle fileHandle = files[fileIndex++];
                    for (ImportFileConfig importFileConfig : importConfig.files) {
                        if (fileHandle.name().equals(importFileConfig.filename)) {
                            if (!importFile(fileHandle, importFileConfig)) {
                                skippedFiles++;
                            }
                            break;
                        }
                    }
                }
                for (String folder : leagues.keySet()) {
                    FileHandle fileHandle = Assets.teamsRootFolder.child(folder).child("leagues.json");
                    List<String> names = new ArrayList<>(leagues.get(folder));
                    fileHandle.writeString(Assets.json.toJson(names, String[].class, String.class), false, "UTF-8");
                }
                refreshAllWidgets();
                break;
            case FINISHED:
                refreshAllWidgets();
                break;
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
                case NO_FILES:
                    setText(gettext("IMPORT.NO FILES"));
                    break;
                case READY:
                    setText(gettext("IMPORT.%n FILES FOUND").replaceFirst("%n", "" + files.length));
                    break;
                case IMPORTING:
                    setText(gettext("IMPORT.IMPORTING") + " " + (fileIndex + 1) + "/" + files.length);
                    break;
                case FINISHED:
                    String message = gettext("IMPORT.%n TEAMS IMPORTED").replaceFirst("%n", "" + importedTeams);
                    if (failedTeams > 0) {
                        message += " - " + gettext("IMPORT.%n TEAMS NOT IMPORTED").replaceFirst("%n", "" + failedTeams);
                    }
                    if (skippedFiles > 0) {
                        message += " - " + gettext("IMPORT.%n FILES IGNORED").replaceFirst("5n", "" + skippedFiles);
                    }
                    setText(message);
                    setSelectedWidget(exitButton);
                    break;
            }
        }
    }

    private class FolderLabel extends Button {

        FolderLabel() {
            setColor(0xAD8600);
            setGeometry(game.gui.WIDTH / 2 - 280, 360, 420, 36);
            setText(gettext("IMPORT.DESTINATION FOLDER"), CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(state == State.READY);
        }
    }

    private class FolderButton extends Button {

        FolderButton() {
            setColor(0x568200);
            setGeometry(game.gui.WIDTH / 2 + 150, 360, 160, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(getYearFolder());
            setVisible(state == State.READY);
        }

        @Override
        public void onFire1Down() {
            updateYear(+1);
        }

        @Override
        public void onFire1Hold() {
            updateYear(+1);
        }

        @Override
        public void onFire2Down() {
            updateYear(-1);
        }

        @Override
        public void onFire2Hold() {
            updateYear(-1);
        }

        private void updateYear(int n) {
            year = EMath.slide(year, 1863, 2100, n);
            updateConfigFile();
            setDirty(true);
            warningLabel.setDirty(true);
        }
    }

    private String getYearFolder() {
        return year + "-" + ("" + (year + 1)).substring(2);
    }

    private void updateConfigFile() {
        configFile = Gdx.files.local("data/config/import_" + getYearFolder() + ".json");
        if (!configFile.exists()) {
            configFile = Gdx.files.local("data/config/import.json");
        }
    }

    private String getTeamTypeFolder(Team team) {
        switch (team.type) {
            case CLUB:
                return "CLUB_TEAMS";
            case NATIONAL:
                return "NATIONAL_TEAMS";
        }
        return "";
    }

    private class WarningLabel extends Button {

        WarningLabel() {
            setColor(0xDC0000);
            setGeometry((game.gui.WIDTH - 1020) / 2, 430, 1020, 80);
            setText(gettext("IMPORT.EXISTING FILES IN THE DESTINATION FOLDER WILL BE OVERWRITTEN"), CENTER, font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            FileHandle fh = Assets.teamsRootFolder.child(getYearFolder());
            setVisible(fh.isDirectory() && state == State.READY);
        }
    }

    private class StartButton extends Button {

        StartButton() {
            setColor(0x138B21);
            setGeometry((game.gui.WIDTH - 220) / 2, 538, 220, 50);
            setText(gettext("IMPORT.START"), CENTER, font14);
        }

        @Override
        public void refresh() {
            setVisible(state == State.READY);
        }

        @Override
        public void onFire1Down() {
            state = State.IMPORTING;
            for (Widget widget : widgets) {
                widget.setDirty(true);
            }
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
                case NO_FILES:
                case FINISHED:
                    setColor(0xC84200);
                    setText(gettext("EXIT"));
                    break;
                case READY:
                    setText(gettext("ABORT"));
                    setColor(0xC8000E);
                    break;
            }
            setVisible(state != State.IMPORTING);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    private boolean importFile(FileHandle fileHandle, ImportFileConfig importConfig) {

        // prepare an export config for easy exporting back
        FileConfig exportConfig = new FileConfig(fileHandle.name());

        byte[] bytes = fileHandle.readBytes();

        // skip first byte
        int pos = 1;

        int teams = bytes[pos++] & 0xFF;

        // read teamList
        for (int tm = 0; tm < teams; tm++) {
            pos = importTeam(fileHandle, importConfig, bytes, pos, exportConfig);
        }

        exportConfigs.files.add(exportConfig);

        return true;
    }

    private int importTeam(FileHandle fileHandle, ImportFileConfig importConfig, byte[] bytes, int pos, FileConfig exportConfig) {

        int startingPosition = pos;

        Team team = new Team();

        team.type = importConfig.type;

        int countryIndex = (bytes[pos++] & 0xFF);

        String continent = "";
        switch (team.type) {
            case CLUB:
                team.country = importConfig.country;
                continent = importConfig.continent;
                break;

            case NATIONAL:
                continent = importConfig.continent;
                break;
        }
        team.city = "";
        team.stadium = "";

        // skip team number
        pos++;

        // skip general team number
        int gtn = (bytes[pos++] & 0xFF) << 8 | (bytes[pos++] & 0xFF);

        // skip unused byte
        pos++;

        // team name
        StringBuilder teamNameBuilder = new StringBuilder();
        for (int i = 0; i < 19; i++) {
            int b = bytes[pos++] & 0xFF;
            if (b > 0) {
                teamNameBuilder.append((char) b);
            }
        }
        team.name = teamNameBuilder.toString();

        // tactics
        team.tactics = bytes[pos++] & 0xFF;

        // league
        int division = bytes[pos++] & 0xFF;

        if (team.type == Team.Type.CLUB) {
            if (importConfig.leagues != null && importConfig.leagues.length > division) {
                team.league = importConfig.leagues[division];
            } else {
                team.league = divisions[division];
            }
        }

        // main kit
        team.kits = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Kit kit = new Kit();
            int styleIndex = bytes[pos++] & 0xFF;
            kit.style = kitNames[styleIndex];
            kit.shirt1 = Kit.colors[bytes[pos++] & 0xFF];
            kit.shirt2 = Kit.colors[bytes[pos++] & 0xFF];
            switch (styleIndex) {
                case 0: // plain
                case 1: // sleeves
                    kit.shirt3 = kit.shirt1;
                    break;
                case 2: // vertical
                case 3: // horizontal
                    kit.shirt3 = kit.shirt2;
                    break;
            }
            kit.shorts = Kit.colors[bytes[pos++] & 0xFF];
            kit.socks = Kit.colors[bytes[pos++] & 0xFF];
            team.kits.add(kit);
        }

        // third kit
        Kit kit = new Kit();
        kit.style = team.kits.get(0).style;
        kit.shirt1 = team.kits.get(0).shirt1;
        kit.shirt2 = team.kits.get(0).shirt2;
        kit.shorts = team.kits.get(1).shorts;
        kit.socks = team.kits.get(1).socks;
        team.kits.add(kit);

        // coach name
        team.coach = new Coach();
        StringBuilder coachNameBuilder = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            int b = bytes[pos++] & 0xFF;
            if (b > 0) {
                coachNameBuilder.append((char) b);
            }
        }
        team.coach.name = coachNameBuilder.toString();
        team.coach.nationality = "";

        // player vector
        // tell which player is stored in 'pos' position
        for (int i = 0; i < 16; i++) {
            position.set(i, -1);
        }
        for (int i = 0; i < 16; i++) {
            int p = bytes[pos++] & 0xFF;
            if (position.contains(p)) {
                Gdx.app.log("Error", "duplicate position: " + p + " in file: " + fileHandle.name() + ", team: " + team.name);
                failedTeams++;
                return startingPosition + 684;
            }
            position.set(i, p);
        }

        // read players
        team.players = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            team.players.add(new Player());
        }

        for (int i = 0; i < 16; i++) {

            // initialize player
            Player player = team.players.get(position.indexOf(i));

            // nationality
            player.nationality = "";
            int nationalityIndex = bytes[pos++] & 0xFF;
            if (nationalityIndex < playerCountryCodes.length) {
                player.nationality = playerCountryCodes[nationalityIndex];
            }

            // skip unknown Byte
            pos++;

            // shirt number
            player.number = (bytes[pos++] & 0xFF);

            // name
            boolean surnameFound = false;
            StringBuilder surnameBuilder = new StringBuilder();
            StringBuilder nameBuilder = new StringBuilder();
            for (int j = 1; j <= 22; j++) {
                int b = bytes[pos++] & 0xFF;
                if (b == 32 && !surnameFound) {
                    surnameFound = true;
                } else {
                    if (b > 0) {
                        if (surnameFound) {
                            surnameBuilder.append((char) b);
                        } else {
                            nameBuilder.append((char) b);
                        }
                    }
                }
            }
            if (surnameBuilder.length() == 0) {
                player.shirtName = nameBuilder.toString();
                player.name = nameBuilder.toString();
            } else {
                player.shirtName = surnameBuilder.toString();
                nameBuilder.append(" ").append(surnameBuilder);
                player.name = nameBuilder.toString();
            }

            // skip cards and injures
            pos++;

            // player Type (role) + head/skin Type + skip 3 unknown bits
            int b = bytes[pos++] & 0xFF;
            player.role = Player.Role.values()[b >> 5];
            int headSkin = (b >> 3) & 0x3;
            switch (headSkin) {
                case 0:
                    player.hairColor = Hair.Color.BLACK;
                    player.skinColor = Skin.Color.values()[0];
                    break;
                case 1:
                    player.hairColor = Hair.Color.BLOND;
                    player.skinColor = Skin.Color.values()[0];
                    break;
                case 2:
                    player.hairColor = Hair.Color.BLACK;
                    player.skinColor = Skin.Color.values()[1];
                    break;
            }
            player.hairStyle = "SMOOTH_A";

            // skip unknown byte
            pos++;

            // player skills
            player.skills = new Player.Skills();
            int passing = bytes[pos++] & 0xF;
            player.skills.passing = passing & 0x7;
            if ((passing >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.PASSING);
            }

            b = bytes[pos++] & 0xFF;
            int shooting = b >>> 4;
            player.skills.shooting = shooting & 0x7;
            if ((shooting >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.SHOOTING);
            }
            int heading = b & 0xF;
            player.skills.heading = heading & 0x7;
            if ((heading >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.HEADING);
            }

            b = bytes[pos++] & 0xFF;
            int tackling = b >>> 4;
            player.skills.tackling = tackling & 0x7;
            if ((tackling >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.TACKLING);
            }
            int control = b & 0xF;
            player.skills.control = control & 0x7;
            if ((control >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.CONTROL);
            }

            b = bytes[pos++] & 0xFF;
            int speed = b >>> 4;
            player.skills.speed = speed & 0x7;
            if ((speed >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.SPEED);
            }
            int finishing = b & 0xF;
            player.skills.finishing = finishing & 0x7;
            if ((finishing >>> 3) == 1) {
                player.bestSkills.add(Player.Skill.FINISHING);
            }

            // value
            b = bytes[pos++] & 0xFF;
            player.value = b;

            // skip unknown bytes
            pos += 5;
        }

        String folder = getYearFolder() + "/" + getTeamTypeFolder(team) + "/";
        switch (team.type) {
            case CLUB:
                folder += continent + "/" + team.country + "/";
                break;
            case NATIONAL:
                folder += continent + "/";
                break;
        }

        String cleanName = team.name.toLowerCase().replace(" ", "_").replace("/", "_").replace(".", "");
        team.path = folder + "team." + cleanName + ".json";

        team.persist();

        exportConfig.teams.add(new TeamConfig(team.path, countryIndex, gtn, division));

        if (team.type == Team.Type.CLUB) {
            if (importConfig.leagues != null) {
                leagues.put(folder, Arrays.asList(importConfig.leagues));
            } else if (leagues.get(folder) == null) {
                List<String> list = new ArrayList<>();
                list.add(team.league);
                leagues.put(folder, list);
            } else {
                List<String> list = leagues.get(folder);
                if (!list.contains(team.league)) {
                    list.add(team.league);
                }
                leagues.put(folder, list);
            }
        }

        importedTeams++;

        return pos;
    }
}
