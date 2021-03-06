package net.krusher.laffsoccer.util;

import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.badlogic.gdx.utils.Json;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import static net.krusher.laffsoccer.util.Auxiliary.RND;
import static net.krusher.laffsoccer.util.Auxiliary.generateSkills;

public class GenerateTeam {

    public static final List<String> PREFIXES = Arrays.asList("UD", "RCD", "REAL", "CD", "U");
    public static final List<String> SUFIXES = Arrays.asList("UTD", "FC", "SAD", "FOOTBALL", "CLUB");
    public static final List<String> SUFIXES_STADIUM = Arrays.asList("ARENA", "STADIUM", "OLIMPIC", "CENTER");
    public static final List<String> PREFIXES_STADIUM = Arrays.asList("ST", "STADE", "LE");
    public static final List<String> LEAGUES = Arrays.asList("LALIGA", "PREMIER", "FIRST");

    public static List<String> NAMES;
    public static List<String> SURNAMES;
    public static List<String> CITIES;
    public static List<String> COUNTRIES;
    public static List<String> KIT_STYLES;
    public static List<String> HAIR_STYLES;

    static {
        try {
            NAMES = Auxiliary.fileToStringList("playernames.txt");
            SURNAMES = Auxiliary.fileToStringList("playersurnames.txt");
            CITIES = Auxiliary.fileToStringList("cities.txt");
            COUNTRIES = Auxiliary.fileToStringList("contries.txt");
            KIT_STYLES = Auxiliary.fileToStringList("kits.txt");
            HAIR_STYLES = Auxiliary.fileToStringList("hairstyles.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String composeName() {
        switch(RND.nextInt(2)) {
            case 0: return Auxiliary.getRandomNormalizedItem(NAMES) + " " + Auxiliary.getRandomNormalizedItem(SURNAMES);
            case 1: return Auxiliary.getRandomNormalizedItem(NAMES) + " " + Auxiliary.getRandomNormalizedItem(SURNAMES) + " " + Auxiliary.getRandomNormalizedItem(SURNAMES);
            default: return "";
        }
    }

    public static void main(String[] arg) throws IOException {

        System.out.println("Generating new team...");

        Team team = new Team();

        team.city = Auxiliary.getRandomNormalizedItem(CITIES);
        switch(RND.nextInt(3)) {
            case 0: team.name = Auxiliary.getRandomNormalizedItem(PREFIXES) + " " + team.city; break;
            case 1: team.name = team.city + " " + Auxiliary.getRandomNormalizedItem(SUFIXES); break;
            case 2: team.name = team.city; break;
        }

        switch(RND.nextInt(5)) {
            case 0: team.stadium = Auxiliary.getRandomNormalizedItem(PREFIXES_STADIUM) + " " + Auxiliary.getRandomNormalizedItem(NAMES); break;
            case 1: team.stadium = team.city + " " + Auxiliary.getRandomNormalizedItem(SUFIXES_STADIUM); break;
            case 2: team.stadium = team.city; break;
            case 3: team.stadium = Auxiliary.getRandomNormalizedItem(SURNAMES) + " " + Auxiliary.getRandomNormalizedItem(SUFIXES_STADIUM); break;
            case 4: team.stadium = Auxiliary.normalize(composeName()) + " " + Auxiliary.getRandomNormalizedItem(SUFIXES_STADIUM); break;
        }

        team.name = team.city;
        team.coach = new Coach();
        team.type = Team.Type.CUSTOM;
        team.country = Auxiliary.getRandomItem(COUNTRIES);
        team.coach.name = composeName();
        team.coach.nationality = Auxiliary.getRandomItem(COUNTRIES);
        // team.league = Auxiliary.getRandomItem(LEAGUES);

        team.kits = new ArrayList<>();
        for (int i=0; i<3; i++) {
            Kit kit = new Kit();
            kit.style = Auxiliary.getRandomItem(KIT_STYLES);
            kit.shirt1 = Auxiliary.randomColour();
            kit.shirt2 = Auxiliary.randomColour();
            kit.shirt3 = Auxiliary.randomColour();
            kit.shorts = Auxiliary.randomColour();
            kit.socks = Auxiliary.randomColour();
            team.kits.add(kit);
        }

        team.players = new ArrayList<>();
        int numPlayers = RND.nextInt(5) + 18;

        Integer[] playerNumbers = Auxiliary.getRndSeq(numPlayers, numPlayers);
        int[] goalKeepers = {1, 12};

        for (int i = 0; i < numPlayers; i++) {
            final int finalI = i;

            Player player = new Player();

            player.name = composeName();
            player.shirtName = Auxiliary.shortName(player.name);
            player.nationality = Auxiliary.getRandomItem(COUNTRIES);
            player.number = playerNumbers[i] + 1;

            player.skinColor = Skin.Color.values()[RND.nextInt(Skin.Color.values().length - 1)];
            player.hairColor = Hair.Color.values()[RND.nextInt(Hair.Color.values().length - 1)];
            player.hairStyle = Auxiliary.getRandomItem(HAIR_STYLES);

            // Goalkeeper
            if (Arrays.stream(goalKeepers).anyMatch(value -> value == finalI + 1)) {
                player.role = Player.Role.GOALKEEPER;
                player.value = RND.nextInt(30) + 10;
            } else {
                player.role = Player.Role.values()[RND.nextInt(Player.Role.values().length - 2) + 1];

                player.skills = generateSkills(player.role);

                List<Player.Skill> skills = new LinkedList<Player.Skill>(Arrays.asList(Player.Skill.values()));
                Collections.shuffle(skills);
                player.bestSkills.addAll(skills.subList(0, RND.nextInt(skills.size())));

            }

            team.players.add(player);
        }

        JFileChooser fileChooser = new JFileChooser();
        FileFilter extensionFilter = new FileNameExtensionFilter("JSON File", "json");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File("team." + FileUtils.normalizeName(team.name) + ".json"));
        fileChooser.setFileFilter(extensionFilter);
        fileChooser.setDialogTitle("Specify a file to save");

        if (arg.length != 0) {
            File fileToSave = new File(arg[0]);
            Auxiliary.saveTeam(team, fileToSave);
        } else {

            JFrame parentFrame = new JFrame();
            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (!fileToSave.getName().endsWith("team." + FileUtils.normalizeName(team.name) + ".json")) {
                    String name = fileToSave.getName();
                    team.name = name.substring(name.indexOf('.') + 1, name.lastIndexOf('.')).toUpperCase();
                }

                Auxiliary.saveTeam(team, fileToSave);
                parentFrame.dispose();
            }
        }

        System.out.print("Done");

    }

}
