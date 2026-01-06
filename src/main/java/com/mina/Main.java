package com.mina;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mina.app.AppMenu;

public class Main {
    private static final Path SURVEY_DEFIN_DIR =
            Paths.get("storage","definitions", "survey");
    private static final Path TEST_DEFIN_DIR =
            Paths.get("storage","definitions", "test");
    private static final Path SURVEY_RESP_DIR =
            Paths.get("storage","responses", "survey");
    private static final Path TEST_RESP_DIR =
            Paths.get("storage","responses", "test");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ensureDirectoriesExist();
        new AppMenu(sc).run();
    }

    // check if director exist
    private static void ensureDirectoriesExist() {
        try {
            Files.createDirectories(SURVEY_DEFIN_DIR);
            Files.createDirectories(TEST_DEFIN_DIR);
            Files.createDirectories(SURVEY_RESP_DIR);
            Files.createDirectories(TEST_RESP_DIR);
        } catch (IOException e) {
            System.out.println("Failed to initialize storage directories.");
            System.exit(1); // fatal
        }
    }

}


