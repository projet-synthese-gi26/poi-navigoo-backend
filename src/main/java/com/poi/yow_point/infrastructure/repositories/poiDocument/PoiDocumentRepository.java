package com.poi.yow_point.infrastructure.repositories.poiDocument;

import com.poi.yow_point.infrastructure.document.PoiDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoiDocumentRepository extends ReactiveElasticsearchRepository<PoiDocument, String> {
}
