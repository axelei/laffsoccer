package net.krusher.laffsoccer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Auxiliary {

    public static List<String> fileToStringList(String filepath) throws FileNotFoundException {
        Scanner s = new Scanner(new File(Auxiliary.class.getClassLoader().getResource(filepath).getFile()));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()){
            list.add(s.next());
        }
        s.close();
        return list;
    }
}
