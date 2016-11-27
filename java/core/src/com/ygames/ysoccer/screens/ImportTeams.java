package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.export.Config;
import com.ygames.ysoccer.export.FileConfig;
import com.ygames.ysoccer.export.TeamConfig;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
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
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

class ImportTeams extends GLScreen {

    private enum State {NO_FILES, READY, IMPORTING, FINISHED}

    private State state = State.NO_FILES;

    private int year = 2016;

    static String[] countryCodes = {
            "ALB", "AUT", "BEL", "BUL", "CRO", "CYP", "CZE", "DEN", "ENG", "009",
            "EST", "FRO", "FIN", "FRA", "GER", "GRE", "HUN", "ISL", "IRL", "ISR",
            "ITA", "LVA", "LTU", "LUX", "MLT", "NED", "NIR", "NOR", "POL", "POR",
            "ROM", "RUS", "SMR", "SCO", "SVN", "ESP", "SWE", "SUI", "TUR", "UKR",
            "WAL", "SBM", "ALG", "ARG", "AUS", "BOL", "BRA", "CRC", "CHI", "COL",
            "ECU", "SLV", "052", "053", "054", "JPN", "056", "KOR", "058", "MAS",
            "MEX", "061", "NZL", "063", "PAR", "PER", "SUR", "TPE", "XXX", "RSA",
            "TAN", "URU", "XXX", "USA", "XXX", "IND", "BLR", "VEN", "SVK", "GHA",
            "EUROPE", "AFRICA", "SOUTH AMERICA", "NORTH AMERICA", "ASIA", "OCEANIA"
    };

    private static String[][] countries = {
            {"ALB", "EUROPE", "ALBANIA"},
            {"AND", "EUROPE", "ANDORRA"},
            {"ARM", "EUROPE", "ARMENIA"},
            {"AUT", "EUROPE", "AUSTRIA"},
            {"AZE", "EUROPE", "AZERBAIJAN"},
            {"BLR", "EUROPE", "BELARUS"},
            {"BEL", "EUROPE", "BELGIUM"},
            {"BIH", "EUROPE", "BOSNIA HERZEGOVINA"},
            {"BUL", "EUROPE", "BULGARIA"},
            {"CRO", "EUROPE", "CROATIA"},
            {"CYP", "EUROPE", "CYPRUS"},
            {"CZE", "EUROPE", "CZECH REPUBLIC"},
            {"DEN", "EUROPE", "DENMARK"},
            {"ENG", "EUROPE", "ENGLAND"},
            {"EST", "EUROPE", "ESTONIA"},
            {"FRO", "EUROPE", "FAROE ISLANDS"},
            {"FIN", "EUROPE", "FINLAND"},
            {"FRA", "EUROPE", "FRANCE"},
            {"GEO", "EUROPE", "GEORGIA"},
            {"GER", "EUROPE", "GERMANY"},
            {"GRE", "EUROPE", "GREECE"},
            {"HUN", "EUROPE", "HUNGARY"},
            {"ISL", "EUROPE", "ICELAND"},
            {"IRL", "EUROPE", "IRELAND REPUBLIC"},
            {"ISR", "EUROPE", "ISRAEL"},
            {"ITA", "EUROPE", "ITALY"},
            {"KAZ", "EUROPE", "KAZAKHSTAN"},
            {"LVA", "EUROPE", "LATVIA"},
            {"LIE", "EUROPE", "LIECHTENSTEIN"},
            {"LTU", "EUROPE", "LITHUANIA"},
            {"LUX", "EUROPE", "LUXEMBOURG"},
            {"MKD", "EUROPE", "MACEDONIA FYR"},
            {"MLT", "EUROPE", "MALTA"},
            {"MDA", "EUROPE", "MOLDOVA"},
            {"MNE", "EUROPE", "MONTENEGRO"},
            {"NED", "EUROPE", "NETHERLANDS"},
            {"NIR", "EUROPE", "NORTHERN IRELAND"},
            {"NOR", "EUROPE", "NORWAY"},
            {"POL", "EUROPE", "POLAND"},
            {"POR", "EUROPE", "PORTUGAL"},
            {"ROM", "EUROPE", "ROMANIA"},
            {"ROU", "EUROPE", "ROMANIA"},
            {"RUS", "EUROPE", "RUSSIA"},
            {"SMR", "EUROPE", "SAN MARINO"},
            {"SCO", "EUROPE", "SCOTLAND"},
            {"SBM", "EUROPE", "SERBIA & MONTENEGRO"},
            {"SRB", "EUROPE", "SERBIA"},
            {"SVK", "EUROPE", "SLOVAKIA"},
            {"SVN", "EUROPE", "SLOVENIA"},
            {"ESP", "EUROPE", "SPAIN"},
            {"SWE", "EUROPE", "SWEDEN"},
            {"SUI", "EUROPE", "SWITZERLAND"},
            {"TUR", "EUROPE", "TURKEY"},
            {"UKR", "EUROPE", "UKRAINE"},
            {"WAL", "EUROPE", "WALES"},

            {"AIA", "NORTH_AMERICA", "ANGUILLA"},
            {"ATG", "NORTH_AMERICA", "ANTIGUA & BARBUDA"},
            {"ARU", "NORTH_AMERICA", "ARUBA"},
            {"BAH", "NORTH_AMERICA", "BAHAMAS"},
            {"BRB", "NORTH_AMERICA", "BARBADOS"},
            {"BER", "NORTH_AMERICA", "BERMUDA"},
            {"VGB", "NORTH_AMERICA", "BR.VIRGIN ISLANDS"},
            {"CAN", "NORTH_AMERICA", "CANADA"},
            {"CAY", "NORTH_AMERICA", "CAYMAN ISLANDS"},
            {"CUB", "NORTH_AMERICA", "CUBA"},
            {"CUW", "NORTH_AMERICA", "CURAÇAO"},
            {"DMA", "NORTH_AMERICA", "DOMINICA"},
            {"DOM", "NORTH_AMERICA", "DOMINICAN REPUBLIC"},
            {"GRN", "NORTH_AMERICA", "GRENADA"},
            {"GUY", "NORTH_AMERICA", "GUYANA"},
            {"HAI", "NORTH_AMERICA", "HAITI"},
            {"JAM", "NORTH_AMERICA", "JAMAICA"},
            {"MEX", "NORTH_AMERICA", "MEXICO"},
            {"MSR", "NORTH_AMERICA", "MONTSERRAT"},
            {"PUR", "NORTH_AMERICA", "PUERTO RICO"},
            {"SKN", "NORTH_AMERICA", "ST.KITTS & NEVIS"},
            {"LCA", "NORTH_AMERICA", "SAINT LUCIA"},
            {"VIN", "NORTH_AMERICA", "ST.VINCENT & GREN."},
            {"TRI", "NORTH_AMERICA", "TRINIDAD & TOBAGO"},
            {"TCA", "NORTH_AMERICA", "TURKS & CAICOS IS."},
            {"VIR", "NORTH_AMERICA", "US VIRGIN ISLANDS"},
            {"USA", "NORTH_AMERICA", "UNITED STATES"},

            {"BLZ", "CENTRAL_AMERICA", "BELIZE"},
            {"CRC", "CENTRAL_AMERICA", "COSTA RICA"},
            {"SLV", "CENTRAL_AMERICA", "EL SALVADOR"},
            {"GUA", "CENTRAL_AMERICA", "GUATEMALA"},
            {"HON", "CENTRAL_AMERICA", "HONDURAS"},
            {"NCA", "CENTRAL_AMERICA", "NICARAGUA"},
            {"PAN", "CENTRAL_AMERICA", "PANAMA"},

            {"ARG", "SOUTH_AMERICA", "ARGENTINA"},
            {"BOL", "SOUTH_AMERICA", "BOLIVIA"},
            {"BRA", "SOUTH_AMERICA", "BRAZIL"},
            {"CHI", "SOUTH_AMERICA", "CHILE"},
            {"COL", "SOUTH_AMERICA", "COLOMBIA"},
            {"ECU", "SOUTH_AMERICA", "ECUADOR"},
            {"PAR", "SOUTH_AMERICA", "PARAGUAY"},
            {"PER", "SOUTH_AMERICA", "PERU"},
            {"SUR", "SOUTH_AMERICA", "SURINAM"},
            {"URU", "SOUTH_AMERICA", "URUGUAY"},
            {"VEN", "SOUTH_AMERICA", "VENEZUELA"},

            {"ALG", "AFRICA", "ALGERIA"},
            {"ANG", "AFRICA", "ANGOLA"},
            {"BEN", "AFRICA", "BENIN"},
            {"BOT", "AFRICA", "BOTSWANA"},
            {"BFA", "AFRICA", "BURKINA FASO"},
            {"BDI", "AFRICA", "BURUNDI"},
            {"CMR", "AFRICA", "CAMEROON"},
            {"CPV", "AFRICA", "CAPE VERDE ISLANDS"},
            {"CTA", "AFRICA", "CENTR.AFRICAN REP."},
            {"CHA", "AFRICA", "CHAD"},
            {"COM", "AFRICA", "COMOROS"},
            {"CGO", "AFRICA", "CONGO"},
            {"COD", "AFRICA", "CONGO DR"},
            {"CIV", "AFRICA", "CÔTE D'IVOIRE"},
            {"DJI", "AFRICA", "DJIBOUTI"},
            {"EGY", "AFRICA", "EGYPT"},
            {"EQG", "AFRICA", "EQUATORIAL GUINEA"},
            {"ERI", "AFRICA", "ERITREA"},
            {"ETH", "AFRICA", "ETHIOPIA"},
            {"GAB", "AFRICA", "GABON"},
            {"GAM", "AFRICA", "GAMBIA"},
            {"GHA", "AFRICA", "GHANA"},
            {"GUI", "AFRICA", "GUINEA"},
            {"GNB", "AFRICA", "GUINEA-BISSAU"},
            {"KEN", "AFRICA", "KENYA"},
            {"LES", "AFRICA", "LESOTHO"},
            {"LBR", "AFRICA", "LIBERIA"},
            {"LBY", "AFRICA", "LIBYA"},
            {"MAD", "AFRICA", "MADAGASCAR"},
            {"MWI", "AFRICA", "MALAWI"},
            {"MLI", "AFRICA", "MALI"},
            {"MTN", "AFRICA", "MAURITANIA"},
            {"MRI", "AFRICA", "MAURITIUS"},
            {"MAR", "AFRICA", "MOROCCO"},
            {"MOZ", "AFRICA", "MOZAMBIQUE"},
            {"NAM", "AFRICA", "NAMIBIA"},
            {"NIG", "AFRICA", "NIGER"},
            {"NGA", "AFRICA", "NIGERIA"},
            {"RWA", "AFRICA", "RWANDA"},
            {"STP", "AFRICA", "SÃO TOMÉ PRÍNCIPE"},
            {"SEN", "AFRICA", "SENEGAL"},
            {"SEY", "AFRICA", "SEYCHELLES"},
            {"SLE", "AFRICA", "SIERRA LEONE"},
            {"SOM", "AFRICA", "SOMALIA"},
            {"RSA", "AFRICA", "SOUTH AFRICA"},
            {"SSD", "AFRICA", "SOUTH SUDAN"},
            {"SDN", "AFRICA", "SUDAN"},
            {"SWZ", "AFRICA", "SWAZILAND"},
            {"TAN", "AFRICA", "TANZANIA"},
            {"TOG", "AFRICA", "TOGO"},
            {"TUN", "AFRICA", "TUNISIA"},
            {"UGA", "AFRICA", "UGANDA"},
            {"ZAM", "AFRICA", "ZAMBIA"},
            {"ZIM", "AFRICA", "ZIMBABWE"},

            {"AFG", "ASIA", "AFGHANISTAN"},
            {"BHR", "ASIA", "BAHRAIN"},
            {"BAN", "ASIA", "BANGLADESH"},
            {"BHU", "ASIA", "BHUTAN"},
            {"BRU", "ASIA", "BRUNEI DARUSSALAM"},
            {"CAM", "ASIA", "CAMBODIA"},
            {"CHN", "ASIA", "CHINA PR"},
            {"TPE", "ASIA", "TAIWAN"},
            {"GUM", "ASIA", "GUAM"},
            {"HKG", "ASIA", "HONG KONG"},
            {"IND", "ASIA", "INDIA"},
            {"IDN", "ASIA", "INDONESIA"},
            {"IRN", "ASIA", "IRAN"},
            {"IRQ", "ASIA", "IRAQ"},
            {"JPN", "ASIA", "JAPAN"},
            {"JOR", "ASIA", "JORDAN"},
            {"PRK", "ASIA", "KOREA DPR"},
            {"KOR", "ASIA", "KOREA REPUBLIC"},
            {"KUW", "ASIA", "KUWAIT"},
            {"KGZ", "ASIA", "KYRGYZSTAN"},
            {"LAO", "ASIA", "LAOS"},
            {"LIB", "ASIA", "LEBANON"},
            {"MAC", "ASIA", "MACAO"},
            {"MAS", "ASIA", "MALAYSIA"},
            {"MDV", "ASIA", "MALDIVES"},
            {"MGL", "ASIA", "MONGOLIA"},
            {"MYA", "ASIA", "MYANMAR"},
            {"NEP", "ASIA", "NEPAL"},
            {"OMA", "ASIA", "OMAN"},
            {"PAK", "ASIA", "PAKISTAN"},
            {"PLE", "ASIA", "PALESTINE"},
            {"PHI", "ASIA", "PHILIPPINES"},
            {"QAT", "ASIA", "QATAR"},
            {"KSA", "ASIA", "SAUDI ARABIA"},
            {"SIN", "ASIA", "SINGAPORE"},
            {"SRI", "ASIA", "SRI LANKA"},
            {"SYR", "ASIA", "SYRIA"},
            {"TJK", "ASIA", "TAJIKISTAN"},
            {"THA", "ASIA", "THAILAND"},
            {"TLS", "ASIA", "TIMOR-LESTE"},
            {"TKM", "ASIA", "TURKMENISTAN"},
            {"UAE", "ASIA", "UN. ARAB EMIRATES"},
            {"UZB", "ASIA", "UZBEKISTAN"},
            {"VIE", "ASIA", "VIETNAM SR"},
            {"YEM", "ASIA", "YEMEN"},

            {"ASA", "OCEANIA", "AMERICAN SAMOA"},
            {"AUS", "OCEANIA", "AUSTRALIA"},
            {"COK", "OCEANIA", "COOK ISLANDS"},
            {"FIJ", "OCEANIA", "FIJI"},
            {"NCL", "OCEANIA", "NEW CALEDONIA"},
            {"NZL", "OCEANIA", "NEW ZEALAND"},
            {"PNG", "OCEANIA", "PAPUA NEW GUINEA"},
            {"SAM", "OCEANIA", "SAMOA"},
            {"SOL", "OCEANIA", "SOLOMON ISLANDS"},
            {"TAH", "OCEANIA", "TAHITI"},
            {"TGA", "OCEANIA", "TONGA"},
            {"VAN", "OCEANIA", "VANUATU"},

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

    private List<Integer> position = new ArrayList<Integer>();
    FileHandle[] files;
    private int fileIndex;
    private int importedTeams, failedTeams, skippedFiles;
    private Widget exitButton;

    private Config exportConfigs;

    ImportTeams(GLGame game) {
        super(game);

        background = new Texture("images/backgrounds/menu_set_team.jpg");

        FileHandle importFolder = Gdx.files.local("data/import");

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        exportConfigs = new Config();

        for (int i = 0; i < 16; i++) {
            position.add(i);
        }

        Widget w;

        w = new TitleBar(Assets.strings.get("IMPORT"), 0x762B8E);
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

            w = new WarningLabel();
            widgets.add(w);

            w = new StartButton();
            widgets.add(w);

            w = new YearLabel();
            widgets.add(w);

            w = new YearButton();
            widgets.add(w);
        }

        exitButton = new ExitButton();
        widgets.add(exitButton);

        if (selectedWidget == null) {
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
                    FileHandle fileHandle = files[fileIndex++];
                    if (!importFile(fileHandle)) {
                        skippedFiles++;
                    }
                }
                updateAllWidgets();
                break;
            case FINISHED:
                updateAllWidgets();
                break;
        }
    }

    private class InfoLabel extends Label {

        InfoLabel() {
            setGeometry((game.gui.WIDTH - 400) / 2, 300, 400, 40);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (state) {
                case NO_FILES:
                    setText("NO FILES");
                    break;
                case READY:
                    setText(files.length + " FILES FOUND");
                    break;
                case IMPORTING:
                    setText("IMPORTING " + (fileIndex + 1) + "/" + files.length);
                    break;
                case FINISHED:
                    String message = importedTeams + " TEAMS IMPORTED";
                    if (failedTeams > 0) message += " - " + failedTeams + " TEAMS FAILED";
                    if (skippedFiles > 0) message += " - " + skippedFiles + " FILES SKIPPED";
                    setText(message);
                    setSelectedWidget(exitButton);
                    break;
            }
        }
    }

    private class YearLabel extends Button {

        YearLabel() {
            setColors(0xAD8600);
            setGeometry(game.gui.WIDTH / 2 - 180 - 5, 360, 180, 36);
            setText("YEAR", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(state == State.READY);
        }
    }

    private class YearButton extends Button {

        YearButton() {
            setColors(0x568200);
            setGeometry(game.gui.WIDTH / 2 + 5, 360, 180, 36);
            setText("", Font.Align.CENTER, Assets.font14);
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
            year = Emath.slide(year, 1863, 2100, n);
            setDirty(true);
        }
    }

    private String getYearFolder() {
        return year + "-" + ("" + (year + 1)).substring(2);
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
            setColors(0xDC0000);
            setGeometry((game.gui.WIDTH - 920) / 2, 420, 920, 60);
            setText("EXISTING FILES IN THE DESTINATION FOLDER WILL BE OVERWRITTEN", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(state == State.READY);
        }
    }

    private class StartButton extends Button {

        StartButton() {
            setColors(0x138B21);
            setGeometry((game.gui.WIDTH - 220) / 2, 498, 220, 42);
            setText("START", Font.Align.CENTER, Assets.font14);
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
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (state) {
                case NO_FILES:
                case FINISHED:
                    setColors(0xC84200);
                    setText(Assets.strings.get("EXIT"));
                    break;
                case READY:
                    setText(Assets.strings.get("ABORT"));
                    setColors(0xC8000E);
                    break;
            }
            setVisible(state != State.IMPORTING);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    private boolean importFile(FileHandle fileHandle) {

        String extension = fileHandle.extension();
        Team.Type teamType;

        // prepare an export config for easy exporting back
        FileConfig exportConfig = new FileConfig(fileHandle.name());

        if (extension.equals("CUS")) {
            Gdx.app.log("Skipped", fileHandle.name());
            return false;
        } else {
            int countryIndex = Integer.parseInt(extension);
            if (countryIndex < 80) {
                teamType = Team.Type.CLUB;
            } else if (countryIndex < 86) {
                teamType = Team.Type.NATIONAL;
            } else {
                Gdx.app.log("Skipped", fileHandle.name());
                return false;
            }
            if (countryCodes[countryIndex].equals("XXX")) {
                Gdx.app.log("Skipped", fileHandle.name());
                return false;
            }
        }

        byte[] bytes = fileHandle.readBytes();

        // skip first byte
        int pos = 1;

        int teams = bytes[pos++] & 0xFF;

        // read teamList
        for (int tm = 0; tm < teams; tm++) {
            pos = importTeam(fileHandle, teamType, bytes, pos, exportConfig);
        }

        exportConfigs.files.add(exportConfig);

        return true;
    }

    private int importTeam(FileHandle fileHandle, Team.Type teamType, byte[] bytes, int pos, FileConfig exportConfig) {

        int startingPosition = pos;

        Team team = new Team();

        team.type = teamType;

        int countryIndex = (bytes[pos++] & 0xFF);

        String continent = "";
        String teamCountry = "";
        if (team.type == Team.Type.CLUB) {
            team.country = "";
            if (countryIndex < countryCodes.length) {
                String countryCode = countryCodes[countryIndex];
                team.country = countryCode;
                for (String[] country : countries) {
                    if (country[0].equals(countryCode)) {
                        continent = country[1];
                        teamCountry = country[2];
                    }
                }
            }
        }
        if (team.type == Team.Type.NATIONAL) {
            if (countryIndex < countryCodes.length) {
                continent = countryCodes[countryIndex];
            }
        }
        team.city = "";
        team.stadium = "";

        // skip team number
        pos++;

        // skip general team number
        int gtn = (bytes[pos++] & 0xFF) << 8 | (bytes[pos++] & 0xFF);

        // skip unused byte
        pos++;

        team.name = "";
        for (int i = 0; i < 19; i++) {
            int b = bytes[pos++] & 0xFF;
            if (b > 0) {
                team.name += (char) (b);
            }
        }

        // tactics
        team.tactics = Tactics.codes[bytes[pos++] & 0xFF];

        // league
        int division = bytes[pos++] & 0xFF;

        switch (team.type) {
            case CLUB:
                if (division == 4) {
                    team.league = "NO LEAGUE";
                } else {
                    team.league = "LEAGUE " + (char) ('A' + division);
                }
                break;

            case NATIONAL:
                team.confederation = countryCodes[countryIndex];
                break;
        }

        // main kit
        team.kits = new ArrayList<Kit>();
        for (int i = 0; i < 2; i++) {
            Kit kit = new Kit();
            kit.style = kitNames[bytes[pos++] & 0xFF];
            kit.shirt1 = Kit.colors[bytes[pos++] & 0xFF];
            kit.shirt2 = Kit.colors[bytes[pos++] & 0xFF];
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
        team.coach.name = "";
        team.coach.nationality = "";
        for (int i = 0; i < 24; i++) {
            int b = bytes[pos++] & 0xFF;
            if (b > 0) {
                team.coach.name += (char) (b);
            }
        }

        // player vector
        // tell which player is stored in 'pos' position
        for (int i = 0; i < 16; i++) {
            position.set(i, -1);
        }
        for (int i = 0; i < 16; i++) {
            int p = bytes[pos++] & 0xFF;
            if (position.indexOf(p) != -1) {
                Gdx.app.log("Error", "duplicate position: " + p + " in file: " + fileHandle.name() + ", team: " + team.name);
                failedTeams++;
                return startingPosition + 684;
            }
            position.set(i, p);
        }

        // read players
        team.players = new ArrayList<Player>();
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
            String surname = "";
            String name = "";
            for (int j = 1; j <= 22; j++) {
                int b = bytes[pos++] & 0xFF;
                if (b == 32 && !surnameFound) {
                    surnameFound = true;
                } else {
                    if (b > 0) {
                        if (surnameFound) {
                            surname += (char) (b);
                        } else {
                            name += (char) (b);
                        }
                    }
                }
            }
            if (surname.equals("")) {
                player.shirtName = name;
                player.name = name;
            } else {
                player.shirtName = surname;
                player.name = name + " " + surname;
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
            player.skills.passing = bytes[pos++] & 0x7;

            b = bytes[pos++] & 0xFF;
            player.skills.shooting = (b >> 4) & 0x7;
            player.skills.heading = (b & 0x7);

            b = bytes[pos++] & 0xFF;
            player.skills.tackling = (b >> 4) & 0x7;
            player.skills.control = b & 0x7;

            b = bytes[pos++] & 0xFF;
            player.skills.speed = (b >> 4) & 0x7;
            player.skills.finishing = b & 0x7;

            // value
            b = bytes[pos++] & 0xFF;
            player.value = b;

            // skip unknown bytes
            pos += 5;
        }

        String folder = getYearFolder() + "/" + getTeamTypeFolder(team) + "/";
        switch (team.type) {
            case CLUB:
                folder += continent + "/" + teamCountry + "/";
                break;
            case NATIONAL:
                folder += continent + "/";
                break;
        }

        String cleanName = team.name.toLowerCase().replace(" ", "_").replace("/", "_");
        FileHandle fh = Assets.teamsRootFolder.child(folder + "team." + cleanName + ".json");
        fh.writeString(Assets.json.prettyPrint(team), false, "UTF-8");

        exportConfig.teams.add(new TeamConfig(Assets.getRelativeTeamPath(fh), gtn, division));

        importedTeams++;

        return pos;
    }
}
