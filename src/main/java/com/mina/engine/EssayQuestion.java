package com.mina.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.Serial;

public class EssayQuestion extends Question {
    @Serial
    private static final long serialVersionUID = 1L;

    // constructor
    public EssayQuestion(String questionPrompt, boolean allowsMultiple, int responseCount, List<Response> responses) {
        super(questionPrompt, allowsMultiple, responseCount, responses);
    }

    public EssayQuestion(String questionPrompt) {
        this(questionPrompt, false, 1, new ArrayList<>());
    }

    // create question method
    @Override
    public void createQuestion(Scanner sc) {
        String prompt = Input.readPromptUntilValid(sc, "Essay");
        setQuestionPrompt(prompt);
        setAllowsMultiple(false);
        setExpectedResponseCount(1);
    }

    // getter
    public int getExpectedResponseCount() {
        return expectedResponseCount;
    }

    // validate response method
    @Override
    protected boolean validateResponse(String response) {
        return Input.validator(response);
    }

    // collect answer method
    @Override
    public Response collectResponse(Scanner sc) {
        clearResponses();

        int count = getExpectedResponseCount();
        Response lastResponse = null;

        for (int i = 0; i < count; i++) {
            while (true) {
                System.out.println("Enter your response:");
                String response = sc.nextLine().trim();

                if (!Input.validator(response)) {
                    continue;
                }

                Response r = new Response(response);
                addResponse(r);
                lastResponse = r;

                break;
            }
        }
        return lastResponse;
    }

    // display question method
    @Override
    public String displayQuestion() {
        return displayPrompt();
    }

    // modify question method
    @Override
    public void modifyQuestion(Scanner sc) {
        modifyPrompt(sc);
    }

    // tabulate question method
    @Override
    public String tabulateQuestion() {
        StringBuilder sb = new StringBuilder();

        for (Response r : getUserResponse()) {
            sb.append(r.getAnswers().getFirst()).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public void setAnswerKeyFromInput(Scanner sc) {
        throw new UnsupportedOperationException(
                "This question type is not automatically gradable"
        );
    }

    @Override
    public boolean checkAnswer(Response response) {
        throw new UnsupportedOperationException(
                "This question type is not automatically gradable"
        );
    }
}
