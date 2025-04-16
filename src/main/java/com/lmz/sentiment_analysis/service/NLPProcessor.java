package com.lmz.sentiment_analysis.service;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
//This service uses Stanford CoreNLP to perform sentiment analysis on a given text.
//It initializes a CoreNLP pipeline with specific annotators and provides a method to analyze the sentiment of text by processing it with the pipeline.
public class NLPProcessor {

    private final StanfordCoreNLP pipeline;

    //Initializes the NLPProcessor with a StanfordCoreNLP pipeline configured to perform tokenization, sentence splitting, parsing, and sentiment analysis.
    public NLPProcessor() {
        Properties props = new Properties();
        // Specify the annotators needed for processing the text.
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
        // Initialize the pipeline with the given properties.
        this.pipeline = new StanfordCoreNLP(props);
    }

    //Analyzes the sentiment of the given text.
    public String analyzeSentiment(String text) {
        // Create an annotation object with the input text.
        Annotation annotation = new Annotation(text);
        // Run all the selected annotators on the text.
        pipeline.annotate(annotation);
        // Iterate over the sentences extracted from the text.
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            return sentiment;
        }
        // Default sentiment if no sentences were found.
        return "Neutral";
    }
}

