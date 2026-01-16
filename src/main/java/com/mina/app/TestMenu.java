package com.mina.app;

import com.mina.engine.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TestMenu {
    private static final Path TEST_DEFIN_DIR =
            Paths.get("storage", "definitions", "test");
    private static final Path TEST_RESP_DIR =
            Paths.get("storage", "responses", "test");

    private final Scanner sc;
    private Test test;

    public TestMenu(Scanner sc, Test test) {
        this.sc = sc;
        this.test = test;
    }

    public Test run() {
        // send user to test menu
        while (true) {
            Output.printMenu3TakeTest();
            int choice = Input.checkInt(sc, 1, 10);

            try {
                switch (choice) {
                    case 1 -> test = createTest(sc);
                    case 2 -> {
                        // Display test without answers
                        if (test == null || test.getQuestions().isEmpty()) {
                            System.out.println("You must have a test loaded in order to display it.");
                        } else {
                            System.out.println(test.displayTestWithoutAnswer());
                        }
                    }
                    case 3 -> {
                        // Display test with answers
                        if (test == null || test.getQuestions().isEmpty()) {
                            System.out.println("You must have a test loaded in order to display it.");
                        } else {
                            System.out.println(test.displayTestWithAnswer());
                        }
                    }
                    case 4 -> test = loadTest(sc);
                    case 5 -> saveTest(sc, test);
                    case 6 -> {
                        if (test == null || test.getQuestions().isEmpty()) {
                            System.out.println("You must have a test loaded in order to take it.");
                        } else {
                            takeTest(sc, test);
                        }
                    }
                    case 7 -> modifyTest(sc, test);
                    case 8 -> {
                        if (test == null || test.getQuestions().isEmpty()) {
                            System.out.println("You must have a test loaded in order to tabulate it.");
                        } else {
                            tabulateAllStoredResponses(test);
                        }
                    }
                    case 9 -> gradeCurrentTest(test);
                    case 10 -> {
                        return test;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    // test menu: option 1 - create test
    private static Test createTest(Scanner sc) {
        Test test = new Test();
        while (true) {
            Output.printMenu4AddQuestions();
            int choice = Input.checkInt(sc, 1, 7);

            try {
                switch (choice) {
                    case 1 -> {
                        Question q = Survey.createTrueFalse(sc);
                        q.setAnswerKeyFromInput(sc);
                        test.getQuestions().add(q);
                    }
                    case 2 -> {
                        Question q = Survey.createMultipleChoice(sc);
                        q.setAnswerKeyFromInput(sc);
                        test.getQuestions().add(q);
                    }
                    case 3 -> {
                        Question q = Survey.createShortAnswer(sc);
                        q.setAnswerKeyFromInput(sc);
                        test.getQuestions().add(q);
                    }
                    case 4 -> {
                        Question q = Survey.createEssay(sc);
                        q.setAnswerKeyFromInput(sc);
                        test.getQuestions().add(q);
                    }
                    case 5 -> {
                        Question q = Survey.createDate(sc);
                        q.setAnswerKeyFromInput(sc);
                        test.getQuestions().add(q);
                    }
                    case 6 -> {
                        Question q = Survey.createMatching(sc);
                        q.setAnswerKeyFromInput(sc);
                        test.getQuestions().add(q);
                    }
                    case 7 -> {
                        return test;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // test menu: option 4 - load Test
    public static Test loadTest(Scanner sc) {
        File testDir = TEST_DEFIN_DIR.toFile();
        File[] testFiles = testDir.listFiles(
                (dir, name) -> name.endsWith(".ser")
        );

        if (testFiles == null || testFiles.length == 0) {
            System.out.println("No saved test found.");
            return null;
        }

        System.out.println("Please select a test to load");
        for (int i = 0; i < testFiles.length; i++) {
            String name = testFiles[i].getName().replace(".ser", "");
            System.out.println((i + 1) + ") " + name);
        }

        int choice = Input.checkInt(sc, 1, testFiles.length);
        File selectedFile = testFiles[choice - 1];

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile))) {
            Test loadedTest = (Test) in.readObject();
            System.out.println("Test loaded: " + selectedFile.getName().replace(".ser", ""));
            return loadedTest;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading test: " + e.getMessage());
            return null;
        }
    }

    // test menu: option 5 -save test
    public static void saveTest(Scanner sc, Test test) {
        if (test == null || test.getQuestions().isEmpty()) {
            System.out.println("You must have a test loaded in order to save it.");
            return;
        }

        System.out.println("Enter the name of the test: ");
        String testName = sc.nextLine().trim();
        if (testName.isEmpty()) {
            System.out.println("Test name cannot be empty.");
            return;
        }

        String filename = TEST_DEFIN_DIR + File.separator + testName + ".ser";
        test.setTitle(testName);

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(test);
            System.out.println("Test saved successfully as: " + testName);
        } catch (IOException e) {
            System.out.println("Error saving test: " + e.getMessage());
        }
    }

    // test menu: option 6 - take test
    public static void takeTest(Scanner sc, Test test) {
        if (test == null || test.getQuestions().isEmpty()) {
            System.out.println("You must have a test loaded in order to take it.");
            return;
        }

        test.getResponses().clear();
        System.out.println("-------Start Test-------");

        for (int i = 0; i < test.getQuestions().size(); i++) {
            Question question = test.getQuestions().get(i);
            System.out.println((i + 1) + ". " + question.displayQuestion());
            Response response = question.collectResponse(sc);
            test.getResponses().add(response);
            System.out.println();
        }

        String testName = test.getTitle()
                .trim()
                .replaceAll("\\s+", "_");

        File testDir = TEST_RESP_DIR.resolve(testName).toFile();
        File[] testFiles = testDir.listFiles(
                (dir, name) -> name.endsWith(".ser")
        );
        int testLength = testFiles == null ? 1 : testFiles.length + 1;
        String attemptName = String.format("attempt_%04d", testLength);

        System.out.println("Response saved!");
        saveTestResponses(test, attemptName);
    }

    // save user responses method
    private static void saveTestResponses(Test test, String responseTitle) {
        // create a response directories if not exist
        String testName = test.getTitle()
                .trim()
                .replaceAll("\\s+", "_");

        Path testRespFolder = TEST_RESP_DIR.resolve(testName);
        try {
            Files.createDirectories(testRespFolder);
        } catch (IOException e) {
            System.out.println("Error creating test response folder: " + e.getMessage());
        }

        // Normalize attempt file name
        String safeResponseTitle = responseTitle
                .trim()
                .replaceAll("\\s+", "_");

        Path filePath = testRespFolder.resolve(safeResponseTitle + ".ser");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(test.getResponses());
            System.out.println("Responses saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving responses: " + e.getMessage());
        }
    }

    // test menu: option 7 - modify test
    private static void modifyTest(Scanner sc, Test current) {
        if (current == null || current.getQuestions().isEmpty()) {
            System.out.println("You must have a test loaded in order to modify it.");
            return;
        }

        System.out.print(current.displayQuestions());
        System.out.print("What question number do you wish to modify? ");
        int number = Input.checkInt(sc, 1, current.getQuestions().size());
        int index = number - 1;

        Question q = current.getQuestions().get(index);
        q.modifyQuestion(sc);
        System.out.println("Question modified.");
    }

    private static void tabulateAllStoredResponses(Test test) {
        if (test == null || test.getQuestions() == null || test.getQuestions().isEmpty()) {
            System.out.println("You must have a test loaded in order to tabulate it.");
            return;
        }

        // Clear any previously loaded responses from questions
        for (Question q : test.getQuestions()) {
            q.getUserResponse().clear();
        }

        String testName = test.getTitle()
                .trim()
                .replaceAll("\\s+", "_");

        Path testRespFolder = TEST_RESP_DIR.resolve(testName);

        if (!Files.exists(testRespFolder) || !Files.isDirectory(testRespFolder)) {
            System.out.println("No saved responses found for: " + test.getTitle());
            return;
        }

        File[] responseFiles = testRespFolder.toFile().listFiles(
                (dir, name) -> name.endsWith(".ser")
        );

        if (responseFiles == null || responseFiles.length == 0) {
            System.out.println("No saved responses found for: " + test.getTitle());
            return;
        }

        // Sort attempts by filename so attempt_0001, attempt_0002, ... are in order
        Arrays.sort(responseFiles, Comparator.comparing(File::getName));

        int attemptCountLoaded = 0;

        for (File f : responseFiles) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Object obj = ois.readObject();

                if (!(obj instanceof List<?>)) {
                    System.out.println("Skipping invalid response file: " + f.getName());
                    continue;
                }

                @SuppressWarnings("unchecked")
                List<Response> attemptResponses = (List<Response>) obj;

                // Merge attempt responses into question response lists
                int limit = Math.min(test.getQuestions().size(), attemptResponses.size());
                for (int i = 0; i < limit; i++) {
                    Response r = attemptResponses.get(i);
                    if (r != null) {
                        test.getQuestions().get(i).addResponse(r);
                    }
                }

                attemptCountLoaded++;

            } catch (Exception e) {
                System.out.println("Failed to read " + f.getName() + ": " + e.getMessage());
            }
        }

        if (attemptCountLoaded == 0) {
            System.out.println("No valid response attempts could be loaded.");
            return;
        }

        System.out.println("\n=== Tabulation Results (All Attempts) ===");
        System.out.println("Test: " + test.getTitle());
        System.out.println("Attempts loaded: " + attemptCountLoaded);
        System.out.print(test.tabulateSurvey());
    }

    // test menu: option 9 - grade the currently loaded test
    private static void gradeCurrentTest(Test test) {
        if (test == null || test.getQuestions().isEmpty()) {
            System.out.println("You must have a test loaded in order to grade it.");
            return;
        }

        if (test.getResponses() == null || test.getResponses().isEmpty()) {
            System.out.println("You must take the test before grading it.");
            return;
        }

        int totalQuestions = test.getQuestions().size();
        int essayCount = 0;
        for (Question q : test.getQuestions()) {
            if (q instanceof EssayQuestion) {
                essayCount++;
            }
        }

        int score = test.grade();

        // print result based on have essay question or not
        if (essayCount == 0) {
            System.out.println("You received a " + score + " on the test.");
        } else {
            //count total score
            int autoGradable = totalQuestions - essayCount;
            double pointsPerQuestion = 100.0 / totalQuestions;
            double possibleAutoPoints = autoGradable * pointsPerQuestion;

            System.out.println("You received a " + score + " on the test.");
            System.out.println("The test was worth 100 points, but only " +
                    Math.round(possibleAutoPoints) +
                    " of those points could be auto-graded because there were " +
                    essayCount + " essay question(s).");
        }
    }
}
