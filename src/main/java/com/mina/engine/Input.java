package com.mina.engine;

import java.util.Scanner;

public class Input {
    private Input() {
    }

    // general validator method
    public static boolean validator(String answer) {
        return answer != null && !answer.trim().isEmpty();
    }

    // check if prompt is valid
    public static String readPromptUntilValid(Scanner sc, String questionType) {
        while (true) {
            System.out.println("Enter the prompt for your " + questionType + " question:");
            String prompt = sc.nextLine().trim();

            if (!validator(prompt)) {
                Output.printErrorEmptyInput();
                continue;
            }

            return prompt;
        }
    }

    // check if number is within range
    public static int checkInt(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    Output.printErrorOutOfRange(min + "-" + max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                Output.printErrorEmptyInput();
            }
        }
    }

    //check if valid response for true/false question
    public static boolean checkTrueFalse(String answer) {
        return validator(answer) && answer.equalsIgnoreCase("true") || answer.equalsIgnoreCase("false");
    }

    //check if the answer of multiple choice is in character + number format
    public static boolean checkAnswerFormatOfMatching(String response) {
        if (!validator(response)) {
            Output.printErrorEmptyInput();
            return false;
        }
        if (response.length() != 1) {
            System.out.println("Invalid response length. Please try again.");
            return false;
        }
        if (!response.matches("[A-Z]\\d")) {
            Output.printErrorInvalidInputFormat("A1, B2, C3");
            return false;
        }
        return true;
    }


    // check if the first character within the response's size
    public static boolean checkMatchingResponseChar(String response, int responseSize) {
        char responseChar = response.charAt(0);
        int index = responseChar - 'A';
        return index >= 0 && index < responseSize;

    }

    // check if the second number within the response's size
    public static boolean checkMatchingResponseInt(String response, int responseSize) {
        int responseInt = Integer.parseInt(String.valueOf(response.charAt(1)));
        return responseInt >= 0 && responseInt <= responseSize;
    }

    // integrate all the methods for checking the format of response of multiple choice question
    public static boolean checkMatchingResponse(String response, int responseSize) {
        return (checkAnswerFormatOfMatching(response)
                && checkMatchingResponseChar(response, responseSize)
                && checkMatchingResponseInt(response, responseSize));
    }

    // check multiple choice format
    public static boolean checkMultiResponse(String response, int responseSize) {
        if (!validator(response) || response.length() != 1) {
            return false;
        }

        char c = response.toUpperCase().charAt(0);
        int index = c - 'A';
        return index >= 0 && index < responseSize;
    }

    // check if the date format is valid(YYYY/MM/DD)
    public static boolean checkValidDate(String answer) {
        if (!validator(answer)) {
            Output.printErrorEmptyInput();
            return false;
        }

        String response = answer.trim();
        String[] parts = response.split("/");

        if (parts.length != 3) {
            return false;
        }

        try {
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            if (month >= 1 && month <= 12 &&
                    day >= 1 && day <= 31 &&
                    year >= 1900 && year <= 2100) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        Output.printErrorInvalidInputFormat("MM/DD/YYYY");
        return false;
    }


}