package com.mina.engine;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShortAnswerQuestion extends EssayQuestion {
    @Serial
    private static final long serialVersionUID = 1L;
    private int maxWord;

    public ShortAnswerQuestion(
            String questionPrompt,
            boolean allowsMultiple,
            int expectedAnswerCount,
            List<Response> responses,
            int maxWord
    ) {
        super(questionPrompt, allowsMultiple, expectedAnswerCount, responses);
        this.maxWord = maxWord;
    }

    public ShortAnswerQuestion(
            String questionPrompt,
            int maxWord
    ) {
        this(questionPrompt, false, 1, new ArrayList<>(), maxWord);
    }

    // getter
    public int getMaxWord() {
        return maxWord;
    }

    // setter
    public void setMaxWord(int maxWord) {
        this.maxWord = maxWord;
    }

    @Override
    public void createQuestion(Scanner sc) {
        // create prompt
        String prompt = Input.readPromptUntilValid(sc, "Short Answer");
        setqPrompt(prompt);

        // ask the maximum words of the question
        while (true) {
            System.out.println("Enter the maximum word for your Short Answer question:");
            try {
                int maxWord = Input.checkInt(sc, 1, 50000);
                setMaxWord(maxWord);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid max word. Please try again.");
            }
        }

        // ask if it allows multiple answers
        while (true) {
            System.out.println("Do you allow multiple answers? (yes/no)");
            String answer = sc.nextLine().trim().toLowerCase();
            if (answer.equals("yes")) {
                setAllowsMultiple(true);

                while (true) {
                    System.out.println("How many answer can the user provide?");
                    try {
                        int answerCount = Integer.parseInt(sc.nextLine().trim());
                        if (answerCount <= 0) {
                            System.out.println("Answer count must be positive. Please try again.");
                            continue;
                        }
                        setExpectedResponseCount(answerCount);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid answer count. Please try again.");
                    }
                }
                break;
            } else if (answer.equals("no")) {
                setAllowsMultiple(false);
                setExpectedResponseCount(1);
                break;
            } else {
                System.out.println("Please enter yes or no");
            }
        }
    }

    // modify question method
    @Override
    public String modifyQuestion(Scanner sc) {
        String message = modifyPrompt(sc);
        System.out.println(message);

        // Option to modify max word limit
        System.out.println("Do you wish to modify the maximum word limit? (yes/no)");
        String answer = sc.nextLine().trim().toLowerCase();
        if (answer.equals("yes")) {
            while (true) {
                System.out.println("Current maximum word limit: " + getMaxWord());
                System.out.println("Enter new maximum word limit:");
                try {
                    int newMaxWord = Integer.parseInt(sc.nextLine().trim());
                    if (newMaxWord <= 0) {
                        System.out.println("Maximum word limit must be positive. Please try again.");
                        continue;
                    }
                    setMaxWord(newMaxWord);
                    System.out.println("Maximum word limit updated to: " + newMaxWord);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please enter a valid integer.");
                }
            }
        }

        return "Short answer question modified successfully";
    }

    // tabulate question method
    @Override
    public String tabulateQuestion() {
        return tabulateOneOption();
    }

    // set answer ket method
    public void setQuestionAnswer(Scanner sc) {
        Output.printAnswerQuestion("Matching");
        List<String> key = new ArrayList<>();

        for (int i = 0; i < getExpectedResponseCount(); i++) {
            while (true) {
                System.out.println("Enter your " + (i + 1) + " answer: ");
                String input = sc.nextLine().trim();

                // check if the user's input is empty
                if (!Input.validator(input)) {
                    Output.printErrorEmptyInput();
                    continue;
                }

                // check if the user's input is valid
                if (!validateResponse(input)) {
                    Output.printErrorInvalidInputString();
                    continue;
                }
                // valid - now store it
                key.add(input);
                break;
            }
        }
        super.setAnswerKey(key);
    }

    // grading method
    @Override
    public boolean checkCorrect(Response response) {
        if (response == null || response.getAnswers().isEmpty()) {
            return false;
        }

        // make a safe copy for checking the answer
        List<String> tempKey = new ArrayList<>(answerKey);
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
