package com.mina.engine;

import java.io.Serial;
import java.util.*;

public class TrueFalseQuestion extends MultipleChoiceQuestion {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor
    public TrueFalseQuestion(String questionPrompt) {
        super(questionPrompt, false, 1, new ArrayList<>(), 2);
        setOptions(Arrays.asList("True", "False"));
        setAllowsMultiple(false);
        setExpectedResponseCount(1);
        setChoiceNum(2);
    }

    //create Question method
    @Override
    public void createQuestion(Scanner sc) {
        System.out.println("Enter the prompt for your True/False question:");
        String prompt = sc.nextLine().trim();
        setQuestionPrompt(prompt);

        List<String> tfOptions = Arrays.asList("True", "False");
        setOptions(tfOptions);
        setAllowsMultiple(false);
        setExpectedResponseCount(1);
        setChoiceNum(tfOptions.size());
    }

    //collect Answer method
    @Override
    public Response collectResponse(Scanner sc) {
        clearResponses();

        System.out.println("Enter your response (True/False):");
        while (true) {
            String response = sc.nextLine().trim();
            if (!validateResponse(response)) {
                continue;
            }

            addResponse(new Response(response));
            break;
        }
        return getUserResponse().isEmpty() ? null : getUserResponse().getFirst();
    }

    // validate Response method
    @Override
    protected boolean validateResponse(String answer) {
        return Input.checkTrueFalse(answer);
    }

    // modify Question method
    @Override
    public String modifyQuestion(Scanner sc) {
        return modifyPrompt(sc);
    }

    // tabulate Question method
    @Override
    public String tabulateQuestion() {
        Map<String, Integer> count = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder();

        // initialize counts for true and false
        count.put("True", 0);
        count.put("False", 0);

        // count all answers
        for (Response r : getUserResponse()) {
            for (String ans : r.getAnswers()) {
                if (ans == null) {
                    continue;
                }

                String trimmed = ans.trim();

                String normalized;
                if (trimmed.equalsIgnoreCase("t") || trimmed.equalsIgnoreCase("true")) {
                    normalized = "True";
                } else if (trimmed.equalsIgnoreCase("f") || trimmed.equalsIgnoreCase("false")) {
                    normalized = "False";
                } else {
                    // unexpected, but don't crash
                    normalized = trimmed;
                }

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


    //grading method override (for true and false we only have one response and one answer)
    @Override
    public boolean checkCorrect(Response response) {
        if (response == null || response.getAnswers().isEmpty()) {
            return false;
        }

        String user = response.getAnswers().getFirst();
        String correct = answerKey.getFirst();

        return user.equalsIgnoreCase(correct);
    }

    // set Question with answer method
    public void setQuestionAnswer(Scanner sc) {
        answerKey.clear();

        while (true) {
            System.out.println("Enter the correct answer (True/False):");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("t") || input.equalsIgnoreCase("true")) {
                answerKey.add("True");
                break;
            } else if (input.equalsIgnoreCase("f") || input.equalsIgnoreCase("false")) {
                answerKey.add("False");
                break;
            } else {
                System.out.println("Invalid input. Please enter T or F.");
            }
        }
    }
}