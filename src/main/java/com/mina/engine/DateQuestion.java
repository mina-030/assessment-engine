package com.mina.engine;

import java.util.*;
import java.io.Serial;

public class DateQuestion extends Question implements Gradable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected int year;
    protected int month;
    protected int day;

    // Constructor
    public DateQuestion(
            String questionPrompt,
            boolean allowsMultiple,
            int expectedResponseCount,
            List<Response> responses,
            int year,
            int month,
            int day
    ) {
        super(questionPrompt, allowsMultiple, expectedResponseCount, responses);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // Simpler Constructor
    public DateQuestion(
            String questionPrompt,
            int year,
            int month,
            int day
    ) {
        this(questionPrompt, false, 1, new ArrayList<>(), year, month, day);
    }

    //setter
    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    // create question method
    @Override
    public void createQuestion(Scanner sc) {
        String prompt = Input.readPromptUntilValid(sc, "Date");
        setQuestionPrompt(prompt);
        setAllowsMultiple(false);
        setExpectedResponseCount(1);
    }

    // validate response method
    @Override
    protected boolean validateResponse(String answer) {
        return Input.checkValidDate(answer);
    }

    // collect answer method
    @Override
    public Response collectResponse(Scanner sc) {
        clearResponses();

        while (true) {
            System.out.println("Enter your response (MM/DD/YYYY):");
            String response = sc.nextLine().trim();

            if (!validateResponse(response)) {
                continue;
            }

            String[] parts = response.split("/");
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            setMonth(month);
            setDay(day);
            setYear(year);

            Response r = new Response(response);
            addResponse(r);
            return r;
        }
    }

    // display question method
    @Override
    public String displayQuestion() {
        return displayPrompt();
    }

    // modify question method
    @Override
    public String modifyQuestion(Scanner sc) {
        return modifyPrompt(sc);
    }

    // tabulate question method
    @Override
    public String tabulateQuestion() {
        return tabulateOneOption();
    }

    // ----------------- For Test -------------------------
    @Override
    public void setAnswerKeyFromInput(Scanner sc) {
        int answerNum = getAnswerNum(sc, "Date");
        List<String> key = collectionAnswer(
                sc, answerNum,
                "Date",
                Input::checkValidDate);
        super.setAnswer(key);
    }

    // check correct method for date question
    @Override
    public boolean checkAnswer(Response response) {
        if (!Input.validator(String.valueOf(response))) {
            return false;
        }

        return response.getAnswers().equals(answerKey);
    }
}
