package cn.zhuoqianmingyue.es.query;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.zhuoqianmingyue.es.Application;
import cn.zhuoqianmingyue.es.index.RestHighLevelClientIndexApiTest;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * RestHightLevelClientQueryApiTest
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.x/java-rest-high.html
 * @author lijk
 * @date 2021/07/07
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RestHighLevelClientDslQueryApiTest {

    private static final Logger log = LoggerFactory.getLogger(RestHighLevelClientIndexApiTest.class);
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * ???????????? matchAll
     * <p>
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-all-query.html
     *{
     *   "query": {
     *     "match_all": {
     *       "boost": 1.0
     *     }
     *   },
     *   "_source": {
     *     "includes": [
     *       "name",
     *       "studymodel",
     *       "price",
     *       "create_date"
     *     ],
     *     "excludes": [ ]
     *   },
     *   "sort": [
     *     {
     *       "_id": {
     *         "order": "desc"
     *       }
     *     }
     *   ]
     * }
     * @throws IOException
     */
    @Test
    public void matchAllQueryTest() throws IOException {

        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort("_id", SortOrder.DESC);
        //????????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "create_date"}, new String[]{});
        searchRequest.source(searchSourceBuilder);

        //ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // ????????????
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            log.info(hit.getSourceAsString());
        }
    }

    /**
     * ??????+ ?????? ??????
     * <p>
     * ???????????????https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html
     * ???????????????https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html
     *
     * {
     *   "from": 2,
     *   "size": 2,
     *   "query": {
     *     "match_all": {
     *       "boost": 1.0
     *     }
     *   },
     *   "_source": {
     *     "includes": [
     *       "name",
     *       "studymodel",
     *       "price",
     *       "create_date"
     *     ],
     *     "excludes": [ ]
     *   },
     *   "sort": [
     *     {
     *       "_id": {
     *         "order": "desc"
     *       }
     *     }
     *   ]
     * }
     * @throws IOException
     */
    @Test
    public void pageQueryTest() throws IOException {
        // ??????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        //????????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "create_date"}, new String[]{});

        //???????????????from to
        // ??????
        int page = 2;
        // ?????????????????????
        int size = 2;
        int index = (page - 1) * size;
        searchSourceBuilder.from(index);
        searchSourceBuilder.size(size);
        searchSourceBuilder.sort("_id", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // ????????????
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    /**
     * ????????????
     * ?????? https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-term-query.html
     *
     * @throws IOException
     */
    @Test
    public void termQueryTest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("studymodel", "online"));

        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    /**
     * ??????????????????????????????
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html
     */
    @Test
    public void matchQueryTest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name", "Java"));

        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }


    /**
     * ???????????????????????? Operator ??????
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html
     */
    @Test
    public void matchQueryOperatorTest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //Java ???????????????????????????
        searchSourceBuilder.query(QueryBuilders.matchQuery("name", "Java ??????").operator(Operator.AND));
        //searchSourceBuilder.query(QueryBuilders.matchQuery("name", "Java ??????").operator(Operator.OR));
        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }


    }

    /**
     * ????????????????????????
     */
    @Test
    public void matchPhraseQueryTest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //Java ??????????????????
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("name", "Java ??????"));
        //searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("name", "Java ??????").slop(1));
        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    /**
     * ????????????????????????
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
     */
    @Test
    public void multiMatchQueryTest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("Java", "name", "description"));

        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    /**
     * ???????????????
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/filter-search-results.html
     */
    @Test
    public void filterQueryTest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("Java", "name", "description");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchSourceBuilder.postFilter(QueryBuilders.termQuery("studymodel", "online"));


        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    /**
     * ???????????? ????????????
     * must?????????????????????must???????????????????????????????????? ???AND???
     * should?????????????????????should???????????????????????????????????????????????????????????? "OR"
     * must_not?????????????????????must_not??????????????????????????????????????????NOT???
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html
     */
    @Test
    public void boolQueryTest() throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("??????", "name", "description");
        TermQueryBuilder price = QueryBuilders.termQuery("price", "29");
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(price);

        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "online"));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    /**
     * ????????????
     * ?????????https://www.elastic.co/guide/en/elasticsearch/reference/current/highlighting.html
     */
    @Test
    public void highlightQueryTest() throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("??????", "name", "description");
        TermQueryBuilder price = QueryBuilders.termQuery("price", "29");
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(price);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        // ????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<em>"); // ????????????
        highlightBuilder.postTags("</em>"); // ????????????
        highlightBuilder.fields().add(new HighlightBuilder.Field("name")); // ????????????
        searchSourceBuilder.highlighter(highlightBuilder);

        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            highlightFields.forEach((k, v) -> {
                log.info("HighlightValueKey:{} ,HighlightValue:{}", k, v);
            });
            log.info("id:{} ,sourceAsString:{}", id, hit.getSourceAsString());
        }
    }

    @Test
    public void initTestDate() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest request1 = new IndexRequest("imooc_test");
        request1.id("1");
        List<String> teachers = Arrays.asList("7??????", "??????");
        Map<Object, Object> build1 = MapUtil.builder()
                .put("name", "0???1????????????Java?????????????????????????????????")
                .put("price", "348.00")
                .put("studymodel", "easy-level")
                .put("teachers", teachers)
                .put("create_date", "2012-09-10 00:00:00")
                .put("description", "???????????????+?????????+???????????????????????????SCRM??????").build();
        String jsonString1 = JSON.toJSONString(build1);
        request1.source(jsonString1, XContentType.JSON);
        bulkRequest.add(request1);

        IndexRequest request2 = new IndexRequest("imooc_test");
        request2.id("2");
        Map<Object, Object> build2 = MapUtil.builder()
                .put("name", "?????????????????????????????? ???????????????")
                .put("price", "99.00")
                .put("studymodel", "middle-level")
                .put("teachers", "??????")
                .put("create_date", "2012-08-01 00:00:00")
                .put("description", "ES5+ES6+?????????API+???????????????+??????????????????????????????").build();
        String jsonString2 = JSON.toJSONString(build2);
        request2.source(jsonString2, XContentType.JSON);
        bulkRequest.add(request2);

        IndexRequest request3 = new IndexRequest("imooc_test");
        request3.id("3");
        Map<Object, Object> build3 = MapUtil.builder()
                .put("name", "three.js-????????????????????????????????????")
                .put("price", "366.00")
                .put("studymodel", "high-level")
                .put("teachers", "??????")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "?????????????????????three.js+WebGL ????????????????????????3D??????").build();
        String jsonString3 = JSON.toJSONString(build3);
        request3.source(jsonString3, XContentType.JSON);
        bulkRequest.add(request3);

        IndexRequest request4 = new IndexRequest("imooc_test");
        request4.id("4");
        Map<Object, Object> build4 = MapUtil.builder()
                .put("name", "?????????ZooKeeper??????????????????Dubbo???????????????????????????????????????")
                .put("price", "199.00")
                .put("studymodel", "high-level")
                .put("teachers", "????????????")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "???????????????????????????????????????").build();
        String jsonString4 = JSON.toJSONString(build4);
        request4.source(jsonString4, XContentType.JSON);
        bulkRequest.add(request4);

        IndexRequest request5 = new IndexRequest("imooc_test");
        request5.id("5");
        Map<Object, Object> build5 = MapUtil.builder()
                .put("name", "????????????????????? ???Dubbo????????????????????????")
                .put("price", "366.00")
                .put("studymodel", "high-level")
                .put("teachers", "Allen")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "Dubbo???????????????+?????????????????????+?????????????????????????????????").build();
        String jsonString5 = JSON.toJSONString(build5);
        request5.source(jsonString5, XContentType.JSON);
        bulkRequest.add(request5);


        IndexRequest request6 = new IndexRequest("imooc_test");
        request6.id("6");
        Map<Object, Object> build6 = MapUtil.builder()
                .put("name", "?????????2????????????Apache????????????-RPC??????Dubbo?????????????????????")
                .put("price", "00.00")
                .put("studymodel", "easy-level")
                .put("teachers", "Debug_SteadyJack")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "?????????2????????????Apache????????????-RPC??????Dubbo?????????????????????").build();
        String jsonString6 = JSON.toJSONString(build6);
        request6.source(jsonString6, XContentType.JSON);
        bulkRequest.add(request6);

        IndexRequest request7 = new IndexRequest("imooc_test");
        request7.id("7");
        Map<Object, Object> build7 = MapUtil.builder()
                .put("name", "Spring Cloud Alibaba ??????????????????????????????????????????")
                .put("price", "368.00")
                .put("studymodel", "easy-level")
                .put("teachers", "????????????")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "??????????????????????????????+?????????????????????????????????????????????????????????????????????").build();
        String jsonString7 = JSON.toJSONString(build7);
        request7.source(jsonString7, XContentType.JSON);
        bulkRequest.add(request7);


        IndexRequest request8 = new IndexRequest("imooc_test");
        request8.id("8");
        Map<Object, Object> build8 = MapUtil.builder()
                .put("name", "????????????SpringCloud?????????????????????????????????????????????")
                .put("price", "366.00")
                .put("studymodel", "middle-level")
                .put("teachers", "Allen")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "??????????????? SpringCloud????????????????????????????????????????????????????????????????????????").build();
        String jsonString8 = JSON.toJSONString(build8);
        request8.source(jsonString8, XContentType.JSON);
        bulkRequest.add(request8);

        IndexRequest request9 = new IndexRequest("imooc_test");
        request9.id("9");
        Map<Object, Object> build9 = MapUtil.builder()
                .put("name", "Spring Cloud Alibaba??????????????????")
                .put("price", "399.00")
                .put("studymodel", "high-level")
                .put("teachers", "??????")
                .put("create_date", "2012-09-01 00:00:00")
                .put("description", "????????? ???????????????Alibaba?????????????????????").build();
        String jsonString9 = JSON.toJSONString(build9);
        request9.source(jsonString9, XContentType.JSON);
        bulkRequest.add(request9);


        IndexRequest request10 = new IndexRequest("imooc_test");
        request10.id("10");
        Map<Object, Object> build10 = MapUtil.builder()
                .put("name", "Java????????????????????? Spring Boot+Kafka+HBase")
                .put("price", "299.00")
                .put("studymodel", "middle-level")
                .put("teachers", "?????????")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "???????????????+???????????????").build();
        String jsonString10 = JSON.toJSONString(build10);
        request10.source(jsonString10, XContentType.JSON);
        bulkRequest.add(request10);


        IndexRequest request11 = new IndexRequest("imooc_test");
        request11.id("11");
        Map<Object, Object> build11 = MapUtil.builder()
                .put("name", "Spring Security + OAuth2 ?????? ???????????????????????????????????????")
                .put("price", "348.00")
                .put("studymodel", "middle-level")
                .put("teachers", "?????????????????????")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "???????????????????????????????????????????????????????????????????????????????????????").build();
        String jsonString11 = JSON.toJSONString(build11);
        request11.source(jsonString11, XContentType.JSON);
        bulkRequest.add(request11);

        IndexRequest request12 = new IndexRequest("imooc_test");
        request12.id("12");
        Map<Object, Object> build12 = MapUtil.builder()
                .put("name", "Spring Cloud???????????????????????? ?????????????????????3???????????????")
                .put("price", "468.00")
                .put("studymodel", "middle-level")
                .put("teachers", "????????????")
                .put("create_date", "2011-09-01 00:00:00")
                .put("description", "?????????/???????????????/?????????????????? ?????????????????????????????????????????????").build();
        String jsonString12 = JSON.toJSONString(build12);
        request12.source(jsonString12, XContentType.JSON);
        bulkRequest.add(request12);

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * searchAfter
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html
     * @throws IOException
     */
    @Test
    public void searchAfterQueryTest() throws IOException {

        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(2);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort("_id", SortOrder.DESC);
        //????????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "create_date"}, new String[]{});
        searchRequest.source(searchSourceBuilder);

        //ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // ????????????
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsArray = hits.getHits();
        while (ArrayUtil.isNotEmpty(hitsArray)){
            for (SearchHit hit : hitsArray) {
                log.info(hit.getSourceAsString());
            }
            Object[] sortValues = hitsArray[hitsArray.length - 1].getSortValues();
            SearchHits searchHits = searchAfter(sortValues);
            hitsArray = searchHits.getHits();
        }
    }

    /**
     * searchAfter ?????????
     * @param sortValues ????????????????????????
     * @return SearchHits
     * @throws IOException
     */
    public SearchHits searchAfter(Object[] sortValues) throws IOException {
        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("zhuoqianmingyue_test");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(2);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort("_id", SortOrder.DESC);
        searchSourceBuilder.searchAfter(sortValues);
        //????????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "create_date"}, new String[]{});
        searchRequest.source(searchSourceBuilder);

        //ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits();
    }

    @Test
    public void scrollQueryTest() throws IOException {

        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("abc","zhuoqianmingyue_test");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(2);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //????????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "create_date"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMillis(500L));
        //ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // ????????????
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsArray = hits.getHits();
        while (ArrayUtil.isNotEmpty(hitsArray)){
            for (SearchHit hit : hitsArray) {
                log.info(hit.getSourceAsString());
            }

            searchResponse = scroll(searchResponse.getScrollId());
            hitsArray = searchResponse.getHits().getHits();
        }
    }

    /**
     * scroll ?????????
     * @param scrollId ????????????????????????
     * @return SearchHits
     * @throws IOException
     */
    public SearchResponse scroll(String scrollId) throws IOException {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueMillis(500L));

        SearchResponse searchScrollResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);

        return searchScrollResponse;
    }
}
