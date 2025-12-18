package com.mina.engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected List<String> answer;

    public Response() {
        this.answer = new ArrayList<String>();
    }

    public Response(String answer) {
        this();
        this.answer.add(answer);
    }

    public Response(List<String> answer) {
        this.answer = answer;
    }

    public List<String> getAnswers() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public void addAnswer(String answer) {
        this.answer.add(answer);
    }

    public String display() {
        if (answer.isEmpty()) {
            return "No answer";
        }

        if (answer.size() == 1) {
            return answer.get(0);
        }

        return String.join(", ", answer);
    }

    public void saveAnswer(String answer) {
        addAnswer(answer);
    }

    public void loadAnswer(List<String> savedAnswer) {
        this.answer = savedAnswer;
    }

    public void modifyAnswer(int index, String newAnswer) {
        if (index >= 0 && index < answer.size()) {
            answer.set(index, newAnswer);
        }
    }
}
