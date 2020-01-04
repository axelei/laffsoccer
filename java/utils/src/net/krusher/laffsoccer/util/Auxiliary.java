package net.krusher.laffsoccer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Auxiliary {

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
}
