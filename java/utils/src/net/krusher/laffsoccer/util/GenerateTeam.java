package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.match.Team;

import java.io.FileNotFoundException;
import java.util.List;

public class GenerateTeam {

    public static void main(String[] arg) throws FileNotFoundException {

        List<String> names = Auxiliary.fileToStringList("names.txt");

        System.out.println("Generating new team...");

        Team team = new Team();

        team.name = "";

    }
}
