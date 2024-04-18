package com.newswatch.KeywordAnalysisService.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

import com.newswatch.KeywordAnalysisService.model.Document;
import com.newswatch.KeywordAnalysisService.model.DocumentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
public class DocumentService {
    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public DocumentService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public  List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index, String searchTerm) throws IOException {

        List<DocumentData> documents = new ArrayList<>();
        HashSet<String> uniqueUrls = new HashSet<>();

        try {

            SearchResponse<Document> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("topic")
                                                            .value(topicId) // Corrected line
                                                    )
                                            )
                                            .must(m -> m
                                                    .match(t -> t
                                                                    .field("document")
                                                                    .query(searchTerm)
                                                            // Corrected line
                                                    )
                                            )
                                            .must(m -> m
                                                    .range(r -> r
                                                            .field("published_date")
                                                            .gte(JsonData.of(startDate.toString())) // Keep using JsonData.of() with LocalDate.toString()
                                                            .lte(JsonData.of(endDate.toString()))
                                                    )
                                            )
                                    )
                            )
                            .size(10000), // Adjust size as needed
                    Document.class
            );


            // Parsing the response
            for (Hit<Document> hit : response.hits().hits()) {
                Document doc = hit.source();
                String title = doc.getTitle();
                String date = doc.getPublishedDate();
                String url = doc.getUrl();

                if (uniqueUrls.add(title)) {
                    documents.add(new DocumentData(title, date, url));
//                    System.out.println("Document url:"+ doc.getUrl());

                }


            }

            return documents;


        } finally {

        }
    }


    public  List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index) throws IOException {

        List<DocumentData> documents = new ArrayList<>();
        HashSet<String> uniqueUrls = new HashSet<>();
        try {

            SearchResponse<Document> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("topic")
                                                            .value(topicId) // Corrected line
                                                    )
                                            )
                                            .must(m -> m
                                                    .range(r -> r
                                                            .field("published_date")
                                                            .gte(JsonData.of(startDate.toString())) // Keep using JsonData.of() with LocalDate.toString()
                                                            .lte(JsonData.of(endDate.toString()))
                                                    )
                                            )
                                    )
                            )
                            .size(10000), // Adjust size as needed
                    Document.class
            );


            // Parsing the response
            for (Hit<Document> hit : response.hits().hits()) {
                Document doc = hit.source();
                String title = doc.getTitle();
                String date = doc.getPublishedDate();
                String url = doc.getUrl();

                if (uniqueUrls.add(url)) {
                    documents.add(new DocumentData(title, date, url));
//                                    System.out.println("Document url:"+ doc.getUrl());

                }


            }

            return documents;


        } finally {

        }
    }

}
