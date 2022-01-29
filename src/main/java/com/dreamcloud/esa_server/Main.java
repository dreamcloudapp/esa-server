package com.dreamcloud.esa_server;

import com.dreamcloud.esa_core.analyzer.AnalyzerOptions;
import com.dreamcloud.esa_core.analyzer.EsaAnalyzer;
import com.dreamcloud.esa_core.analyzer.TokenizerFactory;
import com.dreamcloud.esa_core.cli.AnalyzerOptionsReader;
import com.dreamcloud.esa_core.cli.VectorizationOptionsReader;
import com.dreamcloud.esa_core.vectorizer.TextVectorizer;
import com.dreamcloud.esa_core.vectorizer.VectorBuilder;
import com.dreamcloud.esa_core.vectorizer.VectorizationOptions;
import com.dreamcloud.esa_core.vectorizer.Vectorizer;
import com.dreamcloud.esa_score.analysis.TfIdfAnalyzer;
import com.dreamcloud.esa_score.analysis.TfIdfOptions;
import com.dreamcloud.esa_score.analysis.TfIdfStrategyFactory;
import com.dreamcloud.esa_score.analysis.strategy.TfIdfStrategy;
import com.dreamcloud.esa_score.cli.FileSystemScoringReader;
import com.dreamcloud.esa_score.cli.TfIdfOptionsReader;
import org.apache.commons.cli.*;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        //Server options
        Option portOption = new Option(null, "port", true, "--port (integer)");
        portOption.setRequired(true);
        options.addOption(portOption);

        AnalyzerOptionsReader analyzerOptionsReader = new AnalyzerOptionsReader();
        VectorizationOptionsReader vectorizationOptionsReader = new VectorizationOptionsReader();
        TfIdfOptionsReader tfIdfOptionsReader = new TfIdfOptionsReader();
        FileSystemScoringReader fileSystemScoringReader = new FileSystemScoringReader();

        analyzerOptionsReader.addOptions(options);
        vectorizationOptionsReader.addOptions(options);
        tfIdfOptionsReader.addOptions(options);
        fileSystemScoringReader.addOptions(options);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cli = parser.parse(options, args);
            int port = Integer.parseInt(cli.getOptionValue("port"));

            VectorizationOptions vectorOptions = vectorizationOptionsReader.getOptions(cli);
            AnalyzerOptions analyzerOptions = analyzerOptionsReader.getOptions(cli);
            TfIdfOptions tfIdfOptions = tfIdfOptionsReader.getOptions(cli);
            fileSystemScoringReader.parseOptions(cli);

            //Normal documents
            analyzerOptions.setTokenizerFactory(new TokenizerFactory() {
                public Tokenizer getTokenizer() {
                    return new StandardTokenizer();
                }
            });

            TfIdfStrategyFactory tfIdfFactory = new TfIdfStrategyFactory();
            TfIdfStrategy tfIdfStrategy = tfIdfFactory.getStrategy(tfIdfOptions);
            TfIdfAnalyzer tfIdfAnalyzer = new TfIdfAnalyzer(tfIdfStrategy, new EsaAnalyzer(analyzerOptions), fileSystemScoringReader.getCollectionInfo());

            VectorBuilder vectorBuilder = new VectorBuilder(fileSystemScoringReader.getScoreReader(), fileSystemScoringReader.getCollectionInfo(), tfIdfAnalyzer, analyzerOptions.getPreprocessor(), vectorOptions);
            TextVectorizer vectorizer = new Vectorizer(vectorBuilder);
            EsaHttpServer server = new EsaHttpServer(vectorizer);
            server.start(port);
        } catch (ParseException e) {
            formatter.printHelp("esa-server", options);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
