package com.example.ocr2;

public class HistoryItem {
    private String problem;
    private String result;

    public HistoryItem(String problem, String result) {
        this.problem = problem;
        this.result = result;
    }

    public String getProblem() {
        return problem;
    }

    public String getResult() {
        return result;
    }
}
