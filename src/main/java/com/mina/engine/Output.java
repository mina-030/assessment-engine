package com.mina.engine;

import java.util.List;

public final class Output {
    private static final String INVALID_INPUT_STRING =
            "Invalid input. Please try again.";
    private static final String INVALID_INPUT_INT =
            "Invalid number. Please try again.";
    private static final String INVALID_ENTRY_INPUT =
            "Input cannot be empty. Please try again.";
    private static final String INVALID_INPUT_FORMAT =
            "Invalid input format. Please try again."
                    + "The input format should be: ";
    private static final String INVALID_INPUT_RANGE =
            "Input out of range. Please try again."
                    + "Number should be around: ";
    private static final String[] MENU_1_CHOOSE_SURVEY_TEST = {
            "",
            "1) Survey",
            "2) Test",
            "3) Exit",
            "Enter your choice:"
    };
    private static final String[] MENU_2_TAKE_SURVEY = {
            "",
            "1) Create a new Survey",
            "2) Display an existing Survey",
            "3) Load an existing Survey",
            "4) Save the current Survey",
            "5) Take the current Survey",
            "6) Modifying the current Survey",
            "7) Tabulate a survey",
            "8) Return to previous menu",
            "Enter your choice: "
    };

    private static final String[] MENU_3_TAKE_TEST = {
            "",
            "1) Create a new Test",
            "2) Display an existing Test without correct answers",
            "3) Display an existing Test with correct answers",
            "4) Load an existing Test",
            "5) Save the current Test",
            "6) Take the current Test",
            "7) Modify the current Test",
            "8) Tabulate a Test",
            "9) Grade a Test",
            "10) Return to the previous test",
            "Enter your choice: "
    };


    private static final String[] MENU_4_ADD_QUESTIONS = {
            "",
            "1) Add a new T/F Survey",
            "2) Add a new multiple-choice question",
            "3) Add a new short answer question",
            "4) Add a new essay question",
            "5) Add a new date question",
            "6) Add a new matching question",
            "7) Return to previous menu",
            "Enter your choice: "
    };


    private Output() {
    } // prevent object class

    public static void printMenu1ChooseSurveyTest() {
        for (String s : MENU_1_CHOOSE_SURVEY_TEST) {
            System.out.println(s);
        }
    }

    public static void printMenu2TakeSurvey() {
        for (String s : MENU_2_TAKE_SURVEY) {
            System.out.println(s);
        }
    }

    public static void printMenu3TakeTest() {
        for (String s : MENU_3_TAKE_TEST) {
            System.out.println(s);
        }
    }

    public static void printMenu4AddQuestions() {
        for (String s : MENU_4_ADD_QUESTIONS) {
            System.out.println(s);
        }
    }

    private static void printMessage(String message) {
        System.out.println(message);
    }

    public static void printPrompt(String s) {
        System.out.println(s);
    }

    public static void printError(String s) {
        System.out.println("Error: " + s);
    }

    public static void printErrorInvalidInputString() {
        System.out.println(INVALID_INPUT_STRING);
    }

    public static void printErrorInvalidInputInt() {
        System.out.println(INVALID_INPUT_INT);
    }

    public static void printErrorEmptyInput() {
        System.out.println(INVALID_ENTRY_INPUT);
    }

    public static void printErrorInvalidInputFormat(String inputFormat) {
        System.out.println(INVALID_INPUT_FORMAT + inputFormat);
    }

    public static void printErrorOutOfRange(String range) {
        System.out.println(INVALID_INPUT_RANGE + range);
    }

    public static void showOptionsAlpha(int count, List<String> options) {
        for (int i = 0; i < count; i++) {
            System.out.println("\t" + (char) ('A' + i) + ") " + options.get(i));
        }
    }

    public static void showOptionsInt(int count, List<String> options) {
        for (int i = 0; i < count; i++) {
            System.out.println("\t" + (i + 1) + ") " + options.get(i));
        }
    }

    public static void showMatchPairs(
            int count,
            List<String> leftOptions,
            List<String> rightOptions
    ) {
        for (int i = 0; i < count; i++) {
            System.out.println(
                    (char) ('A' + i) + ") " + leftOptions.get(i)
                            + "\t\t"
                            + i + ") " + rightOptions.get(i)
                            + "\n");
        }
    }

    public static void printAnswerQuestion(String questionType) {
        System.out.println("Please enter answer for this " + questionType + " question: ");
    }
}
