package com.mina.engine;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test extends Survey {
    @Serial
    private static final long serialVersionUID = 1L;

    private String title;
    private List<String> correctAnswer;
    private int score;
    Scanner sc;

    //constructor
    public Test(String title, List<Question> questions, List<Response> responses, List<String> correctAnswer, int score) {
        this.title = title;
        this.questions = questions;
        this.responses = responses;
        this.correctAnswer = correctAnswer;
        this.score = score;
    }

    // simple constructor
    public Test() {
        this.title = "";
        this.questions = new ArrayList<Question>();
        this.responses = new ArrayList<Response>();
        this.correctAnswer = new ArrayList<String>();
        this.score = 0;
    }

    // setter
    public void setCorrectAnswer(List<String> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // getter
    public List<String> getCorrectAnswer() {
        return correctAnswer;
    }

    public int getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    // display a test without correct answers
    public String displayTestWithoutAnswer() {
        return displayQuestions();
    }

    // display a test with correct answers
    public String displayTestWithAnswer() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < getQuestions().size(); i++) {
            Question q = getQuestions().get(i);
            sb.append(i + 1)
                    .append(". ")
                    .append(q.displayQuestion())
                    .append("\n");

            List<String> key = q.getAnswer();
            if (key != null && !key.isEmpty()) {
                sb.append("Answer: ").append(String.join(", ", key));
            } else {
                sb.append("Answer: (not auto-gradable)");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }

    // count how many correct question for one test
    public int countCorrectAnswer() {
        int correct = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Response r = responses.get(i);

            if (!(q instanceof Gradable)) {
                continue;
            }

            if (((Gradable) q).checkAnswer(r)) {
                correct++;
            }
        }
        return correct;
    }

    // grade the test based on the count of correct Answer and the total question we have
    public int grade() {
        int totalQuestions = questions.size();
        int totalCorrectAnswers = countCorrectAnswer();
        // count how many essay
        int essayCount = 0;
        for (Question q : questions) {
            if (q instanceof EssayQuestion) {
                essayCount++;
            }
        }

        //if autoGrable <= 0 means all essay questions
        int autoGradable = totalQuestions - essayCount;
        if (autoGradable <= 0) {
            return 0;
        }

        // calculate the auto-gradable questions
        double maxAutoGradable = (double) (100 * autoGradable) / totalQuestions;
        double pointsPerQuestion = maxAutoGradable / totalQuestions;
        double earnedPoints = pointsPerQuestion * totalCorrectAnswers;

        // round to nearest int
        return (int) Math.round(earnedPoints);
    }
}
