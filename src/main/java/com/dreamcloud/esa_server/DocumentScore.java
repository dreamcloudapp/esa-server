package com.dreamcloud.esa_server;

import java.util.ArrayList;

public class DocumentScore {
    public String status;
    public String error;
    public float score;
    public String[] topConcepts;

    public DocumentScore(String status, float score, String[] topConcepts) {
        this.status = status;
        this.score = score;
        this.topConcepts = topConcepts;
    }

    public DocumentScore(String status, float score) {
        this.status = status;
        this.score = score;
    }

    public DocumentScore(String status, String error) {
        this.status = status;
        this.error = error;
    }
}
