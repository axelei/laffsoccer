package net.krusher.laffsoccer.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Auxiliary {

    public static final Random RND = new Random();

    public static final int DICE = 5;
    public static final int MAX_STAT = 8;

    public static List<String> fileToStringList(String filepath) throws IOException {
        Path file = new File(Auxiliary.class.getClassLoader().getResource(filepath).getFile()).toPath();
        return Files.readAllLines(file);
    }

    public static String normalize(String string) {
        return string.trim().toUpperCase();
    }

    public static <T> T getRandomItem(List<T> list)
    {
        Random random = new Random();
        int listSize = list.size();
        int randomIndex = random.nextInt(listSize);
        return list.get(randomIndex);
    }

    public static <T> String getRandomNormalizedItem(List<T> list) {
        return normalize(String.valueOf(getRandomItem(list)));
    }

    public static int randomColour() {
        return RND.nextInt(256*256*256);
    }

    public static Player.Skills generateSkills(Player.Role role) {

        Player.Skills skills = new Player.Skills();
        int variability = RND.nextInt(2);
        switch (role) {

            case DEFENDER:
            case RIGHT_BACK:
            case LEFT_BACK:
                skills.shooting = getRndStatGauss(-variability);
                skills.heading = getRndStatGauss(-variability);
                skills.finishing = getRndStatGauss(-variability);
                skills.control = getRndStatGauss(variability);
                skills.speed = getRndStatGauss(variability);
                skills.passing = getRndStatGauss(variability);
                skills.tackling = getRndStatGauss(variability);
                break;
            case RIGHT_WINGER:
            case LEFT_WINGER:
            case ATTACKER:
                skills.tackling = getRndStatGauss(-variability);
                skills.heading = getRndStatGauss(-variability);
                skills.passing = getRndStatGauss(-variability);
                skills.speed = getRndStatGauss(variability);
                skills.shooting = getRndStatGauss(variability);
                skills.finishing = getRndStatGauss(variability);
                skills.control = getRndStatGauss(variability);
                break;
            case MIDFIELDER:
            default:
                skills.shooting = getRndStatGauss(0);
                skills.tackling = getRndStatGauss(0);
                skills.control = getRndStatGauss(0);
                skills.heading = getRndStatGauss(0);
                skills.speed = getRndStatGauss(0);
                skills.finishing = getRndStatGauss(0);
                skills.passing = getRndStatGauss(0);
        }

        return skills;
    }

    public static int getRndStatGauss(int towards) {

        int dice = 0;
        for (int i = 0; i < DICE; i++) {
            dice += RND.nextInt(MAX_STAT);
        }
        dice /= DICE;

        return EMath.bound(dice + towards, 0, MAX_STAT);
    }

    public static Integer[] getRndSeq(int amount, int bound) {

        if (amount == bound) {
            List<Integer> list = IntStream.range(0, bound).boxed().collect(Collectors.toList());
            Collections.shuffle(list);
            return list.toArray(new Integer[amount]);
        }

        Set<Integer> intSet = new HashSet<>();
        while (intSet.size() < amount) {
            intSet.add(RND.nextInt(bound) + 1);
        }

        ArrayList<Integer> result = new ArrayList<>(intSet);
        Collections.shuffle(result);
        return result.toArray(new Integer[result.size()]);
    }

    public static String shortName(String name) {
        if (name.length() < 15) {
            return name;
        }
        String[] split = name.split(" ");
        if (split.length == 1) {
            return name;
        }

        Integer[] numbers = getRndSeq(split.length, split.length);
        switch(RND.nextInt(4)) {
            case 0: return split[numbers[0]];
            case 1: return shortName(split[numbers[0]] + " " + split[numbers[1]]);
            case 2: return split[numbers[0]] + " " + split[numbers[1]].charAt(0) + ".";
            case 3: return split[numbers[0]].charAt(0) + ". " + split[numbers[1]];
        }

        return name;
    }

    public static void saveTeam(Team team, File fileToSave) throws IOException {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        json.addClassTag("kits", Kit[].class);
        String result = json.toJson(team, Team.class);
        System.out.println("Save as file: " + fileToSave.getAbsolutePath());
        Files.write(Paths.get(fileToSave.getPath()), result.getBytes());
    }
}
