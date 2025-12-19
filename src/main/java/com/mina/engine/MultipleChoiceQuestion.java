package com.mina.engine;

import java.util.*;
import java.io.Serial;

public class MultipleChoiceQuestion extends Question {
    @Serial
    private static final long serialVersionUID = 1L;

    private int choiceNum;
    private List<String> options = new ArrayList<>();

    public MultipleChoiceQuestion(
            String questionPrompt,
            boolean allowsMultiple,
            int expectedAnswerCount,
            List<Response> responses,
            int choiceNum
    ) {
        super(questionPrompt, allowsMultiple, expectedAnswerCount, responses);
        this.choiceNum = choiceNum;
    }

    //    convenience constructor
    public MultipleChoiceQuestion(String questionPrompt) {
        this(questionPrompt, true, 2, new ArrayList<>(), 0);
    }

    //    setter
    public void setChoiceNum(int choiceNum) {
        this.choiceNum = choiceNum;
    }

    public void setOptions(List<String> options) {
        this.options = (options != null) ? new ArrayList<>(options) : new ArrayList<>();
    }

    //    getter
    public int getChoiceNum() {
        return choiceNum;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    //    Add options method
    public void addOption(String option) {
        options.add(option);
    }

//    inheritance override methods from superclass (Question)

    // create question method
    @Override
    public void createQuestion(Scanner sc) {
        String prompt = Input.readPromptUntilValid(sc, "Multiple Choice");
        setQuestionPrompt(prompt);

        int choiceNum;
        while (true) {
            System.out.println("Enter the number of choices for your multiple-choice question:");
            try {
                choiceNum = Integer.parseInt(sc.nextLine().trim());
                if (choiceNum > 1) {
                    break;
                }
                System.out.println("Number must be at least 2.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        setChoiceNum(choiceNum);

        char letter = 'A';
        for (int i = 0; i < choiceNum; i++) {
            while (true) {
                System.out.println("Enter choice #" + letter + ":");
                String choice = sc.nextLine().trim();

                if (!Input.validator(choice)) {
                    System.out.println("Invalid Input. Please try again.");
                } else if (options.contains(choice)) {
                    System.out.println("Choice " + choice + " already exists.");
                } else {
                    options.add(choice);
                    letter++;
                    break;
                }
            }
        }
    }

    // display question method
    @Override
    public String displayQuestion() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQuestionPrompt()).append("\n");

        List<String> opts = getOptions();
        for (int i = 0; i < opts.size(); i++) {
            char letter = (char) ('A' + i);
            sb.append(letter)
                    .append(") ")
                    .append(opts.get(i))
                    .append("\n");
        }

        // For multiple answers, show hint
        if (isMultipleAllowed()) {
            sb.append("Select ")
                    .append(getExpectedResponseCount())
                    .append(" option(s):\n");
        }

        return sb.toString();
    }

    // modify question method
    public String modifyQuestion(Scanner sc) {
        // modify prompt
        String message = modifyPrompt(sc);
        System.out.println(message);

        // modify choices
        System.out.println("Do you wish to modify choices? (yes/no)");
        String answer2 = sc.nextLine().trim();
        if (answer2.equalsIgnoreCase("yes")) {

            int modifyNum;
            while (true) {
                System.out.println("Which choice do you want to modify?");
                Output.showOptionsInt(getOptions().size(), getOptions());
                try {
                    modifyNum = Integer.parseInt(sc.nextLine().trim());
                    if (modifyNum > options.size() || modifyNum < 1) {
                        System.out.println("Out of range. Please try again.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }

            while (true) {
                System.out.println("Enter a new choice: \n");
                String newChoice = sc.nextLine().trim();
                if (!Input.validator(newChoice)) {
                    System.out.println("Invalid input. Please try again.");
                    continue;
                }
                options.set(modifyNum - 1, newChoice);
                break;
            }
        }

        return "Question updated successfully";
    }

    // validate response method
    @Override
    protected boolean validateResponse(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            System.out.println("Response cannot be empty.");
            return false;
        }

        answer = answer.trim().toUpperCase();
        if (answer.length() != 1) {
            System.out.println("Please enter a single letter (A, B, C, etc.).");
            return false;
        }

        char c = answer.charAt(0);
        if (c < 'A' || c >= ('A' + getOptions().size())) {
            System.out.println("Please enter a letter between A and " +
                    (char) ('A' + getOptions().size() - 1));
            return false;
        }

        return true;
    }

    // collect answer method
    @Override
    public Response collectResponse(Scanner sc) {
        clearResponses();

        int count = getExpectedResponseCount();
        List<String> selectedAnswers = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            while (true) {
                System.out.println("Enter your response " + (i + 1) + " (A, B, C, etc.):");
                String response = sc.nextLine().trim().toUpperCase();

                if (!validateResponse(response)) {
                    continue;
                }

                // Check for duplicate selection if not allowing multiple
                if (!isMultipleAllowed() && selectedAnswers.contains(response)) {
                    System.out.println("You already selected this option. Please choose a different one.");
                    continue;
                }

                selectedAnswers.add(response);
                break;
            }
        }

        // Create a single Response object with all answers
        Response responseObj = new Response(selectedAnswers);
        addResponse(responseObj);
        return responseObj;
    }

    // tabulate question method
    @Override
    public String tabulateQuestion() {
        Map<String, Integer> count = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder();


        // initialize counts for A, B, C, D...
        for (int i = 0; i < getChoiceNum(); i++) {
            String option = Character.toString((char) ('A' + i));
            count.put(option, 0);
        }

        // count all answers
        for (Response r : getUserResponse()) {
            for (String ans : r.getAnswers()) {
                if (ans == null) {
                    continue;
                }

                String normalized = ans.trim().toUpperCase();

                int current = count.getOrDefault(normalized, 0);
                count.put(normalized, current + 1);
            }
        }

        // build output
        for (String response : count.keySet()) {
            sb.append(response)
                    .append(": ")
                    .append(count.get(response))
                    .append("\n");
        }

        return sb.toString();
    }


    // set answer key method
    public void setQuestionAnswer(Scanner sc) {
        Output.printAnswerQuestion("Multiple choice");
        System.out.println("Enter the number of answers for your multiple-choice question:");
        int answerNum = sc.nextInt();
        List<String> key = new ArrayList<>();

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

    @Override
    public boolean checkCorrect
            (Response response) {
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
