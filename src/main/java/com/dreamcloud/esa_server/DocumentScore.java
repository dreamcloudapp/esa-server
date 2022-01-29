package com.dreamcloud.esa_server;

import java.util.ArrayList;

public class DocumentScore {
    public String status;
    public float score;
    public ArrayList<String> topConcepts;

    public DocumentScore(String status, float score, ArrayList<String> topConcepts) {
        this.status = status;
        this.score = score;
        this.topConcepts = topConcepts;
    }

    public DocumentScore(String status, float score) {
        this(status, score, new ArrayList<>());
    }
}
