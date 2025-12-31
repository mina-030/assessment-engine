package com.mina.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public interface Gradable {
    void setAnswerKeyFromInput(Scanner sc);
    boolean checkAnswer(Response response);

    default int getAnswerNum(Scanner sc, String questionType) {
        while (true) {
            System.out.println("Enter the number of answers for your " + questionType + " question: ");
            String line = sc.nextLine();

            try {
                int n = Integer.parseInt(line);
                if (n <= 0) {
                    Output.printErrorInvalidInputInt();
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                Output.printError(e.getMessage());
            }
        }
    }

    default List<String> collectionAnswer(
            Scanner sc,
            int answerNum,
            String questionType,
            java.util.function.Predicate<String> validator
    ) {
        Output.printAnswerQuestion(questionType);
        List<String> answer = new ArrayList<>();

        for (int i = 0; i < answerNum; i++) {
            while (true) {
                System.out.println("Enter your answer " + (i + 1) + ": ");
                String input = sc.nextLine().trim();

                // check if the user's input is empty
                if (!Input.validator(input)) {
                    Output.printErrorEmptyInput();
                    continue;
                }

                // check if the user's input is valid
                if (!validator.test(input)) {
                    Output.printErrorInvalidInputString();
                    continue;
                }
                // valid - now store it
                answer.add(input);
                break;
            }
        }
        return answer;
    }

    static boolean generalCheckAnswer(Response response, Question question) {
        if (!Input.validator(String.valueOf(response))) {
            return false;
        }

        // make a safe copy for checking the answer
        List<String> tempKey = new ArrayList<>(question.answerKey);
        for (String userAnswer : response.getAnswers()) {
            userAnswer = userAnswer.toLowerCase();

            if (!tempKey.contains(userAnswer)) {
                return false;
            }
            tempKey.remove(userAnswer);
        }
        return tempKey.isEmpty();
    }
}
