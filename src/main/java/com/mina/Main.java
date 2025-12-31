package com.mina;

import java.io.*;
import java.util.Scanner;

import com.mina.engine.*;

public class Main {
    private static final String SURVEY_DIR = "surveys";
    private static final String RESP_DIR = "responses";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Survey survey = null;
        Test test = null;

        menuOne(sc, null, null);
    }

    // menu 1
    private static void menuOne(Scanner sc, Survey survey, Test test) {
        //menu 1
        while (true) {
            Output.printMenu1ChooseSurveyTest();
            int choice = Input.checkInt(sc, 1, 3);
            try {
                switch (choice) {
                    case 1 -> surveyMenu(sc, survey);
                    case 2 -> testMenu(sc, test);
                    case 3 -> {
                        System.out.println("Goodbye!");
                        sc.close();
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // menu 2
    // send user to survey menu
    private static void surveyMenu(Scanner sc, Survey survey) {
        while (true) {
            Output.printMenu2TakeSurvey();
            int choice = Input.checkInt(sc, 1, 8);

            // process user selection
            try {
                switch (choice) {
                    case 1 -> survey = createSurvey(sc);
                    case 2 -> display(survey);
                    case 3 -> survey = loadSurvey(sc);
                    case 4 -> saveSurvey(sc, survey);
                    case 5 -> take(sc, survey);
                    case 6 -> modify(sc, survey);
                    case 7 -> {
                        if (survey == null || survey.getQuestions().isEmpty()) {
                            System.out.println("You must have a survey loaded in order to tabulate it.");
                        } else {
                            tabulateCurrentSession(sc, survey);
                        }
                    }
                    case 8 -> menuOne(sc, null, null);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // menu 3
    // send user to test menu
    private static void testMenu(Scanner sc, Test test) {
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
                        // Take test (reuse survey take logic, also saves responses)
                        if (test == null || test.getQuestions().isEmpty()) {
                            System.out.println("You must have a test loaded in order to take it.");
                        } else {
                            take(sc, test);  // method already exists for Survey; Test extends Survey
                        }
                    }
                    case 7 -> modify(sc, test);
                    case 8 -> {
                        if (test == null || test.getQuestions().isEmpty()) {
                            System.out.println("You must have a test loaded in order to tabulate it.");
                        } else {
                            tabulateCurrentSession(sc, test);
                        }
                    }
                    case 9 -> gradeCurrentTest(test);
                    case 10 -> {
                        menuOne(sc, null, null);
                        return;
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
        ensureDirectoriesExist();

        File surveyDir = new File(SURVEY_DIR);
        File[] testFiles = surveyDir.listFiles((dir, name) -> name.endsWith(".json"));

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
    public static void saveTest(Scanner sc, Test current) {
        if (current == null || current.getQuestions().isEmpty()) {
            System.out.println("You must have a test loaded in order to save it.");
            return;
        }

        ensureDirectoriesExist();

        System.out.println("Enter the name of the test: ");
        String testName = sc.nextLine().trim();
        if (testName.isEmpty()) {
            System.out.println("Test name cannot be empty.");
            return;
        }

        String filename = SURVEY_DIR + File.separator + testName + ".json";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(current);
            System.out.println("Test saved successfully as: " + testName);
        } catch (IOException e) {
            System.out.println("Error saving test: " + e.getMessage());
        }
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
        int autoGradable = totalQuestions - essayCount;

        int score = test.grade();

        double pointsPerQuestion = 100.0 / totalQuestions;
        double possibleAutoPoints = autoGradable * pointsPerQuestion;

        System.out.println("You received a " + score + " on the test.");
        System.out.println("The test was worth 100 points, but only " +
                Math.round(possibleAutoPoints) +
                " of those points could be auto-graded because there were " +
                essayCount + " essay question(s).");
    }

    // survey menu: option 1 - create survey
    private static Survey createSurvey(Scanner sc) {
        Survey survey = new Survey();
        while (true) {
            Output.printMenu4AddQuestions();
            int choice = Input.checkInt(sc, 1, 7);

            // process user selection
            switch (choice) {
                case 1 -> survey.getQuestions().add(Survey.createTrueFalse(sc));
                case 2 -> survey.getQuestions().add(Survey.createMultipleChoice(sc));
                case 3 -> survey.getQuestions().add(Survey.createShortAnswer(sc));
                case 4 -> survey.getQuestions().add(Survey.createEssay(sc));
                case 5 -> survey.getQuestions().add(Survey.createDate(sc));
                case 6 -> survey.getQuestions().add(Survey.createMatching(sc));
                case 7 -> {
                    if (survey.getQuestions().isEmpty()) {
                        System.out.println("Survey created with no questions!");
                    } else {
                        System.out.println("Survey created with " + survey.getQuestions().size() + " questions!");
                    }
                    return survey;
                }
            }
            System.out.println("Question added. Current total: " + survey.getQuestions().size());
        }
    }

    // survey menu: option 2 - display survey
    private static void display(Survey current) {
        if (current == null || current.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to display it.");
            return;
        }
        System.out.println();
        System.out.print(current.displayQuestions());
    }

    // survey menu: option 3 - load Survey
    public static Survey loadSurvey(Scanner sc) {
        ensureDirectoriesExist();

        File surveyDir = new File(SURVEY_DIR);
        File[] surveyFiles = surveyDir.listFiles((dir, name) -> name.endsWith(".ser"));

        if (surveyFiles == null || surveyFiles.length == 0) {
            System.out.println("No saved surveys found.");
            return null;
        }

        System.out.println("Please select a survey to load:");
        for (int i = 0; i < surveyFiles.length; i++) {
            String name = surveyFiles[i].getName().replace(".ser", "");
            System.out.println((i + 1) + ") " + name);
        }

        int choice = Input.checkInt(sc, 1, surveyFiles.length);
        File selectedFile = surveyFiles[choice - 1];

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile))) {
            Survey loadedSurvey = (Survey) in.readObject();
            System.out.println("Survey loaded: " + selectedFile.getName().replace(".ser", ""));
            return loadedSurvey;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading survey: " + e.getMessage());
            return null;
        }
    }

    // survey menu: option 4 - save Survey
    public static void saveSurvey(Scanner sc, Survey current) {
        if (current == null || current.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to save it.");
            return;
        }

        ensureDirectoriesExist();

        System.out.println("Enter the name of the survey: ");
        String surveyName = sc.nextLine().trim();
        if (surveyName.isEmpty()) {
            System.out.println("Survey name cannot be empty!");
            return;
        }

        String filename = SURVEY_DIR + File.separator + surveyName + ".ser";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(current);
            System.out.println("Survey loaded successfully as: " + surveyName);
        } catch (IOException e) {
            System.out.println("Error saving survey: " + e.getMessage());
        }
    }

    // survey menu: option 5 - take survey
    public static void take(Scanner sc, Survey current) {
        if (current == null || current.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to take it.");
            return;
        }

        System.out.println("Enter the name of this survey (for saving responses):");
        String surveyName = sc.nextLine().trim();
        if (surveyName.isEmpty()) {
            System.out.println("Survey name cannot be empty for saving responses!");
            return;
        }

        current.getResponses().clear();
        System.out.println("-------Start Survey-------");

        for (int i = 0; i < current.getQuestions().size(); i++) {
            Question question = current.getQuestions().get(i);
            System.out.println((i + 1) + ". " + question.displayQuestion());
            Response response = question.collectResponse(sc);
            current.getResponses().add(response);
            System.out.println();
        }

        System.out.println("Response saved!");
        saveResponses(current, surveyName);
    }

    // survey menu: option 6 - modify survey
    private static void modify(Scanner sc, Survey current) {
        if (current == null || current.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to modify it.");
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


    // check if director exist
    private static void ensureDirectoriesExist() {
        File surveyDir = new File(SURVEY_DIR);
        File respDir = new File(RESP_DIR);
        if (!surveyDir.exists()) {
            surveyDir.mkdirs();
        }
        if (!respDir.exists()) {
            respDir.mkdirs();
        }
    }

    // save user responses method
    private static void saveResponses(Survey survey, String surveyName) {
        ensureDirectoriesExist();

        // Create filename with survey name and timestamp
        String filename = RESP_DIR + File.separator +
                surveyName + "_response_" + System.currentTimeMillis() + ".ser";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(survey.getResponses());
            System.out.println("Responses saved to: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving responses: " + e.getMessage());
        }
    }

    // Load responses for a specific survey/test name
    private static void loadResponsesForTabulation(Survey current, String surveyName) {
        ensureDirectoriesExist();

        File respDir = new File(RESP_DIR);
        // Only load responses that match the survey name pattern
        File[] respFiles = respDir.listFiles((dir, name) ->
                name.startsWith(surveyName + "_response_") && name.endsWith(".ser"));

        if (respFiles == null || respFiles.length == 0) {
            System.out.println("No saved responses found for: " + surveyName);
            return;
        }

        // Clear existing in-memory responses on each question
        for (Question q : current.getQuestions()) {
            q.getUserResponse().clear();
        }

        // Load each response file for this survey
        for (File file : respFiles) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (!(obj instanceof java.util.List<?> rawList)) {
                    continue;
                }

                // Filter to Response objects
                java.util.List<Response> responses = new java.util.ArrayList<>();
                for (Object o : rawList) {
                    if (o instanceof Response) {
                        responses.add((Response) o);
                    }
                }

                // Each response file should match the number of questions
                if (responses.size() == current.getQuestions().size()) {
                    for (int i = 0; i < responses.size(); i++) {
                        current.getQuestions().get(i).addResponse(responses.get(i));
                    }
                    System.out.println("Loaded " + responses.size() + " responses from: " + file.getName());
                } else {
                    System.out.println("Skipping " + file.getName() + " - response count mismatch");
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading responses from " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    // Quick fix: Tabulate only current session's responses
    private static void tabulateCurrentSession(Scanner sc, Survey current) {
        // Clear any previously loaded responses from questions
        for (Question q : current.getQuestions()) {
            q.getUserResponse().clear();
        }

        // Only use responses from the current session
        if (current.getResponses() != null && !current.getResponses().isEmpty()) {
            for (int i = 0; i < current.getQuestions().size() && i < current.getResponses().size(); i++) {
                current.getQuestions().get(i).addResponse(current.getResponses().get(i));
            }
        }

        System.out.println("\n=== Tabulation Results ===");
        System.out.print(current.tabulateSurvey());
    }
}


