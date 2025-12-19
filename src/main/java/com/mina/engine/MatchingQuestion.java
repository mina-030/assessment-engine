package com.mina.engine;

import java.util.*;
import java.io.Serial;

public class MatchingQuestion extends Question {
    @Serial
    private static final long serialVersionUID = 1L;

    private int choiceNum;
    private final List<String> leftOptions;
    private final List<String> rightOptions;
    private final List<Character> leftResponse;
    private final List<Integer> rightResponse;

    // constructor
    public MatchingQuestion(
            String questionPrompt,
            boolean allowsMultiple,
            int expectedAnswerCount,
            List<Response> responses,
            int choiceNum,
            List<String> leftOptions,
            List<String> rightOptions,
            List<Character> leftAnswer,
            List<Integer> rightAnswer
    ) {
        super(questionPrompt, allowsMultiple, expectedAnswerCount, responses);
        this.choiceNum = choiceNum;
        this.leftOptions = leftOptions;
        this.rightOptions = rightOptions;
        this.leftResponse = leftAnswer;
        this.rightResponse = rightAnswer;
    }

    // simple constructor
    public MatchingQuestion(
            String questionPrompt,
            List<String> leftOptions,
            List<String> rightOptions
    ) {
        this(questionPrompt, true, 1, new ArrayList<>(),
                (leftOptions != null ? leftOptions.size() : 0),
                (leftOptions != null ? new ArrayList<>(leftOptions) : new ArrayList<>()),
                (rightOptions != null ? new ArrayList<>(rightOptions) : new ArrayList<>()),
                new ArrayList<>(), new ArrayList<>());
    }

    // setter
    public void setChoiceNum(int num) {
        this.choiceNum = num;
    }

    public void setLeftOptions(int index, String option) {
        leftOptions.set(index, option);
    }

    public void setRightOptions(int index, String option) {
        rightOptions.set(index, option);
    }

    // getter
    public int getChoiceNum() {
        return choiceNum;
    }

    public String getLeftOption(int index) {
        return leftOptions.get(index);
    }

    public String getRightOption(int index) {
        return rightOptions.get(index);
    }

    public List<String> getAllLeftOptions() {
        return leftOptions;
    }

    public List<String> getAllRightOptions() {
        return rightOptions;
    }

    // create question method
    @Override
    public void createQuestion(Scanner sc) {
        String prompt = Input.readPromptUntilValid(sc, "matching");
        setQuestionPrompt(prompt);

        int choiceNum;
        System.out.println("Enter the number of matching choices for your Matching question(options for one side):");
        choiceNum = Input.checkInt(sc, 1, 1000);
        setChoiceNum(choiceNum);
        setExpectedResponseCount(choiceNum);

        String choice;
        char letter = 'A';
        for (int i = 0; i < choiceNum; i++) {
            while (true) {
                System.out.println("Enter left choice #" + letter + ":");
                choice = sc.nextLine().trim();

                if (!Input.validator(choice)) {
                    Output.printErrorInvalidInputString();
                } else if (leftOptions.contains(choice)) {
                    System.out.println("Choice " + choice + " already exists.");
                } else {
                    leftOptions.add(choice);
                    letter++;
                    break;
                }
            }
        }

        for (int i = 0; i < choiceNum; i++) {
            while (true) {
                System.out.println("Enter right choice #" + (i + 1) + ":");
                choice = sc.nextLine().trim();
                if (!Input.validator(choice)) {
                    Output.printErrorInvalidInputString();
                } else if (rightOptions.contains(choice)) {
                    System.out.println("Choice " + choice + " already exists.");
                } else {
                    rightOptions.add(choice);
                    break;
                }
            }
        }

    }

    // validate response method
    @Override
    protected boolean validateResponse(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return false;
        }

        String cleanResponse = answer.replaceAll("\\s+", "").toUpperCase();

        if (cleanResponse.length() < 2) {
            return false;
        }

        char letter = cleanResponse.charAt(0);
        if (letter < 'A' || letter >= ('A' + getChoiceNum())) {
            return false;
        }

        try {
            int number = Integer.parseInt(cleanResponse.substring(1));
            return number >= 1 && number <= getChoiceNum();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // collect answer method
    @Override
    public Response collectResponse(Scanner sc) {
        clearResponses();
        int count = getExpectedResponseCount();

        List<String> matchingPairs = new ArrayList<>();

        System.out.println("Enter " + count + " matching pair(s) in format like A1, B2, etc.:");

        for (int i = 0; i < count; i++) {
            while (true) {
                System.out.println("Enter matching pair " + (i + 1) + ":");
                String response = sc.nextLine().trim();

                if (!Input.validator(response)) {
                    Output.printErrorEmptyInput();
                    continue;
                }

                // Clean and validate the response
                String cleanResponse = response.replaceAll("\\s+", "").toUpperCase();

                if (!validateResponse(cleanResponse)) {
                    Output.printErrorInvalidInputFormat("A1, B1, C3");
                    System.out.println("Available left choices: A-" + (char) ('A' + getChoiceNum() - 1));
                    System.out.println("Available right choices: 1-" + getChoiceNum());
                    continue;
                }

                char letter = cleanResponse.charAt(0);
                int number = Integer.parseInt(cleanResponse.substring(1));

                // Check if this exact pair was already entered
                if (matchingPairs.contains(cleanResponse)) {
                    System.out.println("You already entered this matching pair. Please try a different one.");
                    continue;
                }

                // Check if left choice was already used
                boolean leftUsed = matchingPairs.stream()
                        .anyMatch(pair -> pair.charAt(0) == letter);
                if (leftUsed) {
                    System.out.println(
                            "Left choice '" + letter
                                    + "' was already matched. Please use a different left choice.");
                    continue;
                }

                // Check if right choice was already used
                boolean rightUsed = matchingPairs.stream()
                        .anyMatch(pair -> pair.substring(1).equals(cleanResponse.substring(1)));
                if (rightUsed) {
                    System.out.println(
                            "Right choice '" + number
                                    + "' was already matched. Please use a different right choice.");
                    continue;
                }

                // Valid response - add to our list
                matchingPairs.add(cleanResponse);
                break;
            }
        }

        // Create and return the actual Response object
        Response responseObj = new Response();
        for (String pair : matchingPairs) {
            responseObj.addAnswer(pair);
        }
        addResponse(responseObj);

        return responseObj;
    }

    // display prompt and options method
    @Override
    public String displayQuestion() {
        StringBuilder sb = new StringBuilder();

        sb.append(getQuestionPrompt()).append("\n");
        int count = getChoiceNum();
        char letter = 'A';
        for (int i = 0; i < count; i++) {
            sb.append(letter).append(") ")
                    .append(getLeftOption(i)).append("\t\t")
                    .append(i + 1).append(") ")
                    .append(getRightOption(i)).append("\n");
            letter++;
        }

        return sb.toString();
    }

    // modify question method
    public String modifyQuestion(Scanner sc) {
        // modify prompt
        String message = modifyPrompt(sc);
        System.out.println(message);

        // modify choices
        System.out.println("Do you wish to modify the choices? (yes/no)");
        String answer = sc.nextLine().trim();

        if (!answer.equalsIgnoreCase("yes")) {
            return "No changes to choices.";
        }

        int choicesCount = getChoiceNum();
        Output.showMatchPairs(choicesCount, getAllLeftOptions(), getAllRightOptions());

        // Modify left choices
        System.out.println("--- Modify Left Choices ---");
        modifyChoiceList(sc, "left", leftOptions);

        // Modify right choices
        System.out.println("--- Modify Right Choices ---");
        modifyChoiceList(sc, "right", rightOptions);

        return "Modified successfully!";
    }

    // modify choice method
    private void modifyChoiceList(Scanner sc, String side, List<String> options) {
        while (true) {
            System.out.println(
                    "Select the " + side
                            + " option to modify (1-" + options.size()
                            + ") or 'done' to finish: ");
            String token = sc.nextLine().trim();

            if (token.equalsIgnoreCase("done")) {
                break;
            }

            if (!token.matches("\\d+")) {
                System.out.println("Please enter a valid number or 'done'.");
                continue;
            }

            int index = Integer.parseInt(token) - 1;
            if (index < 0 || index >= options.size()) {
                Output.printErrorOutOfRange(0 + "-" + options.size());
                continue;
            }

            System.out.println("Current option: " + options.get(index));
            System.out.println("Enter new option text: ");
            String newOption = sc.nextLine().trim();

            if (!Input.validator(newOption)) {
                Output.printErrorEmptyInput();
                continue;
            }

            if (side.equals("left")) {
                setLeftOptions(index, newOption);
            } else {
                setRightOptions(index, newOption);
            }

            System.out.println("Option updated successfully!");
            int choicesCount = getChoiceNum();
            Output.showMatchPairs(choicesCount, getAllLeftOptions(), getAllRightOptions()); // Show updated view
        }
    }

    // tabulate question method
    public String tabulateQuestion() {
        Map<String, Integer> count = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder();

        sb.append(getQuestionPrompt()).append("\n");

        // Display the options
        int choiceCount = getChoiceNum();
        for (int i = 0; i < choiceCount; i++) {
            char letter = (char) ('A' + i);
            sb.append(letter).append(") ").append(getLeftOption(i)).append("\n");
        }
        for (int i = 0; i < choiceCount; i++) {
            sb.append(i + 1).append(") ").append(getRightOption(i)).append("\n");
        }
        sb.append("\n");

        // Count responses
        for (Response r : getUserResponse()) {
            // Build a string representation of this matching set
            List<String> sortedPairs = new ArrayList<>();
            for (String pair : r.getAnswers()) {
                sortedPairs.add(pair);
            }
            Collections.sort(sortedPairs);
            String key = String.join(" ", sortedPairs);

            count.put(key, count.getOrDefault(key, 0) + 1);
        }

        // Display counts
        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            sb.append(entry.getValue()).append("\n");
            String[] pairs = entry.getKey().split(" ");
            for (String pair : pairs) {
                sb.append(pair).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // ------------------------------------ For Test ----------------------------------------
    public void setAnswerKey(List<String> answerKeys) {
        this.answerKey = answerKeys;
    }

    // set answer key method
    public void setQuestionAnswer(Scanner sc) {
        Output.printAnswerQuestion("Matching (e.g. A1, B2, C3)");
        List<String> key = new ArrayList<>();

        for (int i = 0; i < getChoiceNum(); i++) {
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

    // checkCorrect method for matching class
    @Override
    public boolean checkCorrect(Response response) {
        if (response == null || response.getAnswers().isEmpty()) {
            return false;
        }
        List<String> user = new ArrayList<>();
        for (String s : response.getAnswers()) {
            user.add(s.replaceAll("\\s+", "").toUpperCase());
        }

        List<String> key = new ArrayList<>();
        for (String s : answerKey) {
            key.add(s.replaceAll("\\s+", "").toUpperCase());
        }

        if (user.size() != key.size()) {
            return false;
        }

        for (int i = 0; i < key.size(); i++) {
            if (!user.get(i).equals(key.get(i))) {
                return false;
            }
        }
        return true;
    }
}
