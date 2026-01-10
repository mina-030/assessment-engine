package com.mina.app;
import com.mina.engine.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class SurveyMenu {
    private static final Path SURVEY_DEFIN_DIR =
            Paths.get("storage","definitions", "survey");
    private static final Path SURVEY_RESP_DIR =
            Paths.get("storage","responses", "survey");

    private final Scanner sc;
    private Survey survey;

    public SurveyMenu(Scanner sc, Survey survey) {
        this.sc = sc;
        this.survey = survey;
    }

    public Survey run() {
        while (true) {
            Output.printMenu2TakeSurvey();
            int choice = Input.checkInt(sc, 1, 8);

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
                            tabulateAllStoredResponses(survey);
                        }
                    }
                    case 8 -> {return survey;}
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
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
            System.out.println("Question added. Current Question total: " + survey.getQuestions().size());
        }
    }

    // survey menu: option 2 - display survey
    private static void display(Survey survey) {
        if (survey == null || survey.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to display it.");
            return;
        }
        System.out.println();
        System.out.print(survey.displayQuestions());
    }

    // survey menu: option 3 - load Survey
    public static Survey loadSurvey(Scanner sc) {
        File surveyDir = new File(String.valueOf(SURVEY_DEFIN_DIR));
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
    public static void saveSurvey(Scanner sc, Survey survey) {
        if (survey == null || survey.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to save it.");
            return;
        }

        System.out.println("Enter the name of the survey: ");
        String surveyName = sc.nextLine().trim();
        if (surveyName.isEmpty()) {
            System.out.println("Survey name cannot be empty!");
            return;
        }

        String filename = SURVEY_DEFIN_DIR + File.separator + surveyName + ".ser";
        survey.setTitle(surveyName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(survey);
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

        current.getResponses().clear();
        System.out.println("-------Start Survey-------");

        for (int i = 0; i < current.getQuestions().size(); i++) {
            Question question = current.getQuestions().get(i);
            System.out.println((i + 1) + ". " + question.displayQuestion());
            Response response = question.collectResponse(sc);
            current.getResponses().add(response);
            System.out.println();
        }

        String surveyName = current.getTitle()
                .trim()
                .replaceAll("\\s+", "_");

        File surveyDir = SURVEY_RESP_DIR.resolve(surveyName).toFile();
        File[] surveyFiles = surveyDir.listFiles(
                (dir, name) -> name.endsWith(".ser")
        );
        int surveyLength = surveyFiles == null ? 1 : surveyFiles.length + 1;
        String attemptName = String.format("attempt_%04d", surveyLength);

        System.out.println("Response saved!");
        saveSurveyResponses(current, attemptName);
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
        try {
            Files.createDirectories(SURVEY_DEFIN_DIR);
            Files.createDirectories(SURVEY_RESP_DIR);
        } catch (IOException e) {
            System.out.println("Failed to initialize storage directories.");
            System.exit(1); // fatal
        }
    }

    // save user responses method
    private static void saveSurveyResponses(Survey survey, String responseTitle) {
        // create a response directories if not exist
        String surveyName = survey.getTitle()
                .trim()
                .replaceAll("\\s+", "_");

        Path surveyRespFolder = SURVEY_RESP_DIR.resolve(surveyName);
        try {
            Files.createDirectories(surveyRespFolder);
        } catch (IOException e) {
            System.out.println("Error creating survey response folder: " + e.getMessage());
        }

        // Normalize attempt file name
        String safeResponseTitle = responseTitle
                .trim()
                .replaceAll("\\s+", "_");

        Path filePath = surveyRespFolder.resolve(safeResponseTitle + ".ser");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(survey.getResponses());
            System.out.println("Responses saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving responses: " + e.getMessage());
        }
    }

    private static void tabulateAllStoredResponses(Survey survey) {
        if (survey == null || survey.getQuestions() == null || survey.getQuestions().isEmpty()) {
            System.out.println("You must have a survey loaded in order to tabulate it.");
            return;
        }

        // Clear any previously loaded responses from questions
        for (Question q : survey.getQuestions()) {
            q.getUserResponse().clear();
        }

        String surveyName = survey.getTitle()
                .trim()
                .replaceAll("\\s+", "_");

        Path surveyRespFolder = SURVEY_RESP_DIR.resolve(surveyName);

        if (!Files.exists(surveyRespFolder) || !Files.isDirectory(surveyRespFolder)) {
            System.out.println("No saved responses found for: " + survey.getTitle());
            return;
        }

        File[] responseFiles = surveyRespFolder.toFile().listFiles(
                (dir, name) -> name.endsWith(".ser")
        );

        if (responseFiles == null || responseFiles.length == 0) {
            System.out.println("No saved responses found for: " + survey.getTitle());
            return;
        }

        // Sort attempts by filename so attempt_0001, attempt_0002, ... are in order
        java.util.Arrays.sort(responseFiles, java.util.Comparator.comparing(File::getName));

        int attemptCountLoaded = 0;

        for (File f : responseFiles) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Object obj = ois.readObject();

                if (!(obj instanceof java.util.List<?>)) {
                    System.out.println("Skipping invalid response file: " + f.getName());
                    continue;
                }

                @SuppressWarnings("unchecked")
                java.util.List<Response> attemptResponses = (java.util.List<Response>) obj;

                // Merge attempt responses into question response lists
                int limit = Math.min(survey.getQuestions().size(), attemptResponses.size());
                for (int i = 0; i < limit; i++) {
                    Response r = attemptResponses.get(i);
                    if (r != null) {
                        survey.getQuestions().get(i).addResponse(r);
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
        System.out.println("Survey: " + survey.getTitle());
        System.out.println("Attempts loaded: " + attemptCountLoaded);
        System.out.print(survey.tabulateSurvey());
    }
}
