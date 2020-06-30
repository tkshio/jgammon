package com.github.tkshio.jgammon;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DefaultTDReader {
    private final static String path = "/td_default.txt";

    public static BufferedReader getDefaultTDReader() {
        return new BufferedReader(new InputStreamReader(
                DefaultTDReader.class.getResourceAsStream(path))
        );
    }
}
