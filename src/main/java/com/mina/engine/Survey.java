package com.mina.engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Survey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected String title;
    protected List<Question> questions;
    protected List<Response> responses;
    protected List<String> tables;
    Scanner sc;

    // constructor
    public Survey(String title, List<Question> questions, List<Response> responses, List<String> tables) {
        this.title = title;
        this.questions = questions;
        this.responses = responses;
        this.tables = tables;
    }

    // simple constructor
    public Survey() {
        this.title = "";
        this.questions = new ArrayList<Question>();
        this.responses = new ArrayList<Response>();
        this.tables = new ArrayList<String>();
    }

    // getter
    public List<Question> getQuestions() {
        return questions;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public List<String> getTables() {
        return tables;
    }

    public String getTitle() {
        return title;
    }

    // setter
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // display question method
    public String displayQuestions() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Question question : questions) {
            sb.append(i++)
                    .append(". ")
                    .append(question.displayQuestion())
                    .append("\n");
        }
        return sb.toString();
    }

    //display response method
    public String displayResponse() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Response response : responses) {
            sb.append(i++)
                    .append(". ")
                    .append(response.display())
                    .append("\n");
        }
        return sb.toString();
    }

    // modify method
    public boolean modify(int index) {
        if (index < 0 || index >= questions.size()) {
            return false;
        }
        Question question = questions.get(index);
        question.modifyPrompt(sc);
        return true;
    }

    // take survey method
    public boolean takeSurvey(Scanner sc) {
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.println((i + 1) + ". " + question.displayQuestion());
            Response r = question.collectResponse(sc);
            responses.add(r);
        }
        return true;
    }

    // tabulate survey method
    public String tabulateSurvey() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            sb.append("\n─────────────────────────────\n");
            sb.append("Question ").append(i + 1).append(":\n");
            sb.append(question.displayQuestion()).append("\n");
            sb.append("Responses Summary:\n");
            sb.append(question.tabulateQuestion()).append("\n");
        }
        return sb.toString();
    }

    //create true false question method
    public static Question createTrueFalse(Scanner sc) {
        TrueFalseQuestion tf = new TrueFalseQuestion("");
        tf.createQuestion(sc);
        return tf;
    }

    //create multiple choice question method
    public static Question createMultipleChoice(Scanner sc) {
        MultipleChoiceQuestion mc = new MultipleChoiceQuestion("");
        mc.createQuestion(sc);
        return mc;
    }

    //create short answer question method
    public static Question createShortAnswer(Scanner sc) {
        ShortAnswerQuestion sa = new ShortAnswerQuestion("", 50);
        sa.createQuestion(sc);
        return sa;
    }

    //create essay question method
    public static Question createEssay(Scanner sc) {
        EssayQuestion essay = new EssayQuestion("");
        essay.createQuestion(sc);
        return essay;
    }

    //create date question method
    public static Question createDate(Scanner sc) {
        DateQuestion date = new DateQuestion("", 2024, 1, 1);
        date.createQuestion(sc);
        return date;
    }

    //create matching question method
    public static Question createMatching(Scanner sc) {
        MatchingQuestion matching = new MatchingQuestion("", new ArrayList<>(), new ArrayList<>());
        matching.createQuestion(sc);
        return matching;
    }

}
