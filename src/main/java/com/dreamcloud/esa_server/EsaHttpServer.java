package com.dreamcloud.esa_server;

import com.dreamcloud.esa_core.vectorizer.TextVectorizer;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.core.util.Header;

import java.util.concurrent.TimeUnit;

public class EsaHttpServer {
    private final TextVectorizer vectorizer;

    public EsaHttpServer(TextVectorizer vectorizer) {
        this.vectorizer = vectorizer;
    }

    public void start(int port) throws InterruptedException {
        System.out.print("Starting the server");
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.print('.');
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.print('.');
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println('.');
        System.out.println();
        TimeUnit.MILLISECONDS.sleep(500);

        this.displayInfo(port);

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

    private void displayInfo(int port) {
        System.out.println("Welcome to the ESA server!");
        System.out.println("----------------------------------------");
        System.out.println("API Usage:");
        System.out.println("----------------------------------------");
        System.out.println("---        Document Similarity       ---");
        System.out.println("POST http://localhost:" + port + "/similarity");
        System.out.println("request body {\n\tdocumentText1: string,\n\tdocumentText2: string\n}");
        System.out.println("response body {\n\tstatus: string (success|failure),\n\terror: string|null\n\tscore: float|null,\n\ttopConcepts: array[string]|null\n}");
        System.out.println("----------------------------------------");
        System.out.println();
    }
}
