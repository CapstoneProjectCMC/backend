package com.codecampus.search.configuration.elasticsearch;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig
        extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(System.getenv().getOrDefault(
                        "ELASTICSEARCH_HOST", "localhost:9200"
                ))
                .build();
    }
}
