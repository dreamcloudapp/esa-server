package com.dreamcloud.esa_server;

import com.dreamcloud.esa_core.vectorizer.TextVectorizer;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.core.util.Header;

import java.io.IOException;
import java.sql.SQLException;

public class EsaHttpServer {
    private final TextVectorizer vectorizer;

    public static boolean nonEmpty(String s) {
        return s != null && !s.equals("");
    }

    public EsaHttpServer(TextVectorizer vectorizer) {
        this.vectorizer = vectorizer;
    }
    public void start(int port) throws IOException, SQLException {
        Gson gson = new Gson();
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(port);

        //Scores two documents relatedness via their texts, supporting all options
        app.post("/similarity", ctx -> {
            ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            DocumentSimilarityRequestBody requestBody = gson.fromJson(ctx.body(), DocumentSimilarityRequestBody.class);
            DocumentSimilarityScorer scorer = new DocumentSimilarityScorer(vectorizer);
            ctx.json(scorer.score(requestBody));
        });
    }
}
