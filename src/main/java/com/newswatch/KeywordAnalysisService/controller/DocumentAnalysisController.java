package com.newswatch.KeywordAnalysisService.controller;

import com.newswatch.KeywordAnalysisService.model.DocumentData;
import com.newswatch.KeywordAnalysisService.services.DocumentService;
import com.newswatch.KeywordAnalysisService.services.ElasticsearchUtilityService;
import com.newswatch.KeywordAnalysisService.services.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class DocumentAnalysisController {

    private final DocumentService documentService;


    private final SentimentAnalysisService sentimentAnalysisService;

    private final ElasticsearchUtilityService elasticsearchUtilityService;
    @Autowired
    public DocumentAnalysisController(SentimentAnalysisService sentimentAnalysisService,DocumentService documentService,ElasticsearchUtilityService elasticsearchUtilityService ){

        this.documentService = documentService;
        this.sentimentAnalysisService = sentimentAnalysisService;
        this.elasticsearchUtilityService =  elasticsearchUtilityService;
    }



    @GetMapping("/count")
    public ResponseEntity<Long> executeCountQuery(
            @RequestParam String term1,
            @RequestParam String term2,
            @RequestParam String indexName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {


        try {

            System.out.println("Term1: " + term1);
            System.out.println("Term2: " + term2);
            System.out.println("IndexName: " + indexName);
            System.out.println("StartDate: " + startDate);
            System.out.println("EndDate: " + endDate);
            long count = elasticsearchUtilityService.executeCountQuery(term1, term2, indexName, startDate, endDate);
            System.out.println("Count: " + count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/by-topic")
    public ResponseEntity<List<DocumentData>> getDocumentsByTopic(


            @RequestParam int topicId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String index,
            @RequestParam(required = false) String searchTerm) {
        try {
//            System.out.println("Topicindex: " + index);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<DocumentData> documents;

            // Check if searchTerm is present and call the appropriate method
            if (searchTerm != null && !searchTerm.isEmpty()) {
                documents = documentService.fetchDocumentsByTopic(start, end, topicId, index, searchTerm);
            } else {
                documents = documentService.fetchDocumentsByTopic(start, end, topicId, index);
            }

            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger instead of printStackTrace in a real application
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
