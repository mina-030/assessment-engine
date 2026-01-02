package com.mina.engine;

import java.io.Serial;
import java.util.*;

public class TrueFalseQuestion extends MultipleChoiceQuestion implements Gradable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor
    public TrueFalseQuestion(String questionPrompt) {
        super(questionPrompt, false, 1, new ArrayList<>(), 2);
        setOptions(Arrays.asList("True", "False"));
        setAllowsMultiple(false);
        setExpectedResponseCount(1);
        setChoiceSize(2);
    }

    //create Question method
    @Override
    public void createQuestion(Scanner sc) {
        System.out.println("Enter the prompt for your True/False question:");
        String prompt = sc.nextLine().trim();
        setQuestionPrompt(prompt);
        setOptions(Arrays.asList("True", "False"));
        setAllowsMultiple(false);
        setExpectedResponseCount(1);
        setChoiceSize(2);
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
    protected boolean validateResponse(String response) {
        return Input.checkTrueFalse(response);
    }

    // modify Question method
    @Override
    public void modifyQuestion(Scanner sc) {
        modifyPrompt(sc);
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
    public boolean checkAnswer(Response response) {
        if (!Input.validator(String.valueOf(response))) {
            return false;
        }

        String user = response.getAnswers().getFirst();
        String correct = answerKey.getFirst();

        return user.equalsIgnoreCase(correct);
    }

    // set Question with answer method
    public void setAnswerKeyFromInput(Scanner sc) {
        int answerNum = getAnswerNum(sc, "True False");
        List<String> key = collectionAnswer(
                sc, answerNum,
                "True False",
                Input::checkTrueFalse);
        super.setAnswer(key);
    }
}