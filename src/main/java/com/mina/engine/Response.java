package com.mina.engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected List<String> response;

    public Response() {
        this.response = new ArrayList<>();
    }

    public Response(String input) {
        this();
        this.response.add(input);
    }

    public Response(List<String> response) {
        this.response = response;
    }

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public void addResponse(String input) {
        this.response.add(input);
    }

    public String display() {
        if (response.isEmpty()) {
            return "No Response";
        }

        if (response.size() == 1) {
            return response.getFirst();
        }

        return String.join(", ", response);
    }
}
