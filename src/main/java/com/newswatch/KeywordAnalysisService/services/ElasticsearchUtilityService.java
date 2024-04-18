package com.newswatch.KeywordAnalysisService.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.SpanTermQuery;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.json.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class ElasticsearchUtilityService {


    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public ElasticsearchUtilityService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }


    /**
     * Executes a count query for a given pair of terms from a specified index.
     *
     * @param term1     The first term.
     * @param term2     The second term.
     * @param indexName The name of the Elasticsearch index.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return The count of documents containing the given pair of terms.
     * @throws IOException If there's an issue communicating with Elasticsearch.
     */

    //Create a test for this method below



    public long executeCountQuery(String term1, String term2, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        SpanTermQuery termQuery1 = SpanTermQuery.of(st -> st.field("text").value(term1));
        SpanTermQuery termQuery2 = SpanTermQuery.of(st -> st.field("text").value(term2));
        System.out.println("End  " );
        CountResponse countResponse = elasticsearchClient.count(c -> c
                .index(indexName)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .spanNear(sn -> sn
                                                .inOrder(false)
                                                .slop(50)
                                                .clauses(cl -> cl.spanTerm(termQuery1))
                                                .clauses(cl -> cl.spanTerm(termQuery2))
                                        )
                                )
                                .filter(f -> f
                                        .range(r -> r
                                                .field("published_date")
                                                .gte(JsonData.of(startDate.toString()))
                                                .lte(JsonData.of(endDate.toString()))
                                        )
                                )
                        )
                )
        );
        System.out.println("Count: " + countResponse.count());
        return countResponse.count();
    }//


    public long executeCountQuery(String keyword, String indexName) throws IOException {
        // Building the query
        var response = elasticsearchClient.count(c -> c
                .index(indexName)
                .query(q -> q
                        .match(m -> m
                                .field("text")
                                .query(keyword)
                        )
                )
        );

        return response.count();
    }

}
