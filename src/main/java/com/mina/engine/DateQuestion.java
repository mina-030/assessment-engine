package com.mina.engine;

import java.util.*;
import java.io.Serial;

public class DateQuestion extends Question {
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
        this(questionPrompt, false, 1, new ArrayList<Response>(), year, month, day);
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

    //getter
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    //general setter
    public void setValidDate(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    // create question method
    @Override
    public void createQuestion(Scanner sc) {
        String prompt = Input.readPromptUntilValid(sc, "Date");
        setqPrompt(prompt);
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
    public void setQuestionAnswer(Scanner sc) {
        while (true) {
            Output.printAnswerQuestion("Date");
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
            List<String> key = new ArrayList<>();
            key.add(input);
            super.setAnswerKey(key);
            break;
        }
    }

    // check correct method for date question
    @Override
    public boolean checkCorrect(Response response) {
        if (response == null || response.getAnswers().isEmpty()) {
            return false;
        }

        return response.getAnswers().equals(answerKey);
    }
}
