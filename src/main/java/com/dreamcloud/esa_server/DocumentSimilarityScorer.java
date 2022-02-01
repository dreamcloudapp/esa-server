package com.dreamcloud.esa_server;

import com.dreamcloud.esa_core.similarity.DocumentSimilarity;
import com.dreamcloud.esa_core.similarity.SimilarityInfo;
import com.dreamcloud.esa_core.vectorizer.TextVectorizer;
import com.dreamcloud.esa_score.score.DocumentNameResolver;

import java.util.ArrayList;

public class DocumentSimilarityScorer {
    TextVectorizer vectorizer;

    //todo: fix API stuff so it's all nice and separate from command line
    public DocumentSimilarityScorer(TextVectorizer vectorizer) {
        this.vectorizer = vectorizer;
    }

    public DocumentScore score(DocumentSimilarityRequestBody request) throws Exception {
        DocumentSimilarity similarity = new DocumentSimilarity(vectorizer);
        SimilarityInfo similarityInfo = similarity.score(request.documentText1, request.documentText2, true);
        ArrayList<String> parsedTopConcepts = new ArrayList<>();
        for(String topConcept: similarityInfo.getTopConcepts()) {
            parsedTopConcepts.add(DocumentNameResolver.getTitle(Integer.parseInt(topConcept)));
        }
        return new DocumentScore("success", similarityInfo.getScore(), parsedTopConcepts);
    }
}
