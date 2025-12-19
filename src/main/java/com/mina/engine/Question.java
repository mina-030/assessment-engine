package com.mina.engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;


public abstract class Question implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String qPrompt;
    protected boolean allowsMultiple;
    protected int expectedResponseCount;
    private List<Response> responses;
    protected List<String> answerKey = new ArrayList<>();

    //    Constructor
    public Question(String questionPrompt, boolean allowsMultiple, int expectedAnswerCount, List<Response> responses) {
        this.qPrompt = questionPrompt;
        this.allowsMultiple = allowsMultiple;
        this.expectedResponseCount = expectedAnswerCount;
        this.responses = (responses != null) ? responses : new ArrayList<>();

    }

    //    Getter
    public String getQuestionPrompt() {
        return qPrompt;
    }

    public boolean isMultipleAllowed() {
        return allowsMultiple;
    }

    public int getExpectedResponseCount() {
        return expectedResponseCount;
    }

    public List<Response> getUserResponse() {
        return responses;
    }

    public List<String> getAnswer() {
        return answerKey;
    }

    //    Setter
    public void setQuestionPrompt(String questionPrompt) {
        this.qPrompt = questionPrompt;
    }

    public void setAllowsMultiple(boolean allowsMultiple) {
        this.allowsMultiple = allowsMultiple;
    }

    public void setExpectedResponseCount(int expectedResponseCount) {
        this.expectedResponseCount = expectedResponseCount;
    }

    public void setResponses(List<Response> responses) {
        if (responses != null) {
            this.responses = responses;
        } else {
            this.responses = new ArrayList<>();
        }
    }

    public void setAnswer(List<String> answerKey) {
        this.answerKey = answerKey;
    }

    //    Add Response Method
    public void addResponse(Response response) {
        this.responses.add(response);
    }

    protected void clearResponses() {
        this.responses.clear();
    }


    public String modifyPrompt(Scanner sc) {
        // modify prompt
        System.out.println(getQuestionPrompt() + "\n");
        System.out.println("Do you wish to modify the prompt? (yes/no)");
        String answer = sc.nextLine().trim();
        if (answer.equalsIgnoreCase("yes")) {
            System.out.println(getQuestionPrompt() + "\n");

            while (true) {
                System.out.println("Enter a new prompt: ");
                String prompt = sc.nextLine().trim();
                if (!Input.validator(prompt)) {
                    Output.printErrorInvalidInputString();
                    continue;
                }
                setQuestionPrompt(prompt);
                break;
            }
        }
        return "Question prompt updated successfully";
    }

    // general tabulate option method
    public String tabulateOneOption() {
        Map<String, Integer> count = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder();

        for (Response r : getUserResponse()) {
            String ans = r.getAnswers().getFirst();
            count.put(ans, count.getOrDefault(ans, 0) + 1);
        }

        for (String response : count.keySet()) {
            sb.append(response).append(": ").append(count.get(response)).append("\n");
        }

        return sb.toString();
    }


    // Display method
    public String displayPrompt() {
        return getQuestionPrompt();
    }


    // Abstract Methods for the subclass (can be modified) (display, modify, collectResponse, validateResponse)
    public abstract void createQuestion(Scanner sc);

    protected abstract boolean validateResponse(String answer);

    public abstract Response collectResponse(Scanner sc);

    public abstract String displayQuestion();

    public abstract String modifyQuestion(Scanner sc);

    public abstract String tabulateQuestion();

    // --------------------------- For test---------------------------------
    // needed to be fixed and delete later
    // have an interface already doing it
    public void setAnswerKey(List<String> answerKey) {
        this.answerKey = answerKey;
    }

    public List<String> getAnswerKey() {
        return answerKey;
    }

    // checkCorrect method
    public boolean checkCorrect(Response response) {
        if (response == null || response.getAnswers().isEmpty()) {
            return false;
        }

        String user = response.getAnswers().getFirst();
        String correct = answerKey.getFirst();

        return user.equalsIgnoreCase(correct);
    }

    public abstract void setQuestionAnswer(Scanner sc);

    // set question with answer for test
    public void setQuestionWithAnswer(Scanner sc) {
        createQuestion(sc);
        setQuestionAnswer(sc);
    }
}
