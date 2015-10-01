package com.martmoa.surffer.controller;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hukoru on 15. 9. 22.
 */
public class SearchController {



    /**
     * C01. 상품 목록
     *
     * @param searchWord  String	검색어
     *
     *
     *
     * @return product 객체
     *
     * @throws Exception http exception
     */
    @RequestMapping(value = "{version}/{device}/autocomplete/{searchWord}", method = RequestMethod.GET)
    public
    @ResponseBody
    String autocomplete(
            @PathVariable("version") String version,
            @PathVariable("device")  String device,
            @PathVariable("searchWord") String searchWord
    ) throws Exception {


        String  returnMsg = "fail";
        String  returnCode = "fail";

        BoolQueryBuilder qb = null;

        String categoryField = "";


        //List<HashMap> prod_list = productService.searchProductList(product);

        Settings settings;
        Client client;

        settings = ImmutableSettings
                .settingsBuilder()
                .build();

        client = buildClient(settings);

        SearchResponse searchResponse;

        //qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
        //qb = qb.must(QueryBuilders.queryStringQuery(URLDecoder.decode(searchWord, "UTF-8")).field("productName").analyzer("korean")); // del_yn 변수중에 N인것 적용
        qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다

        //TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName", searchWord);
        FieldSortBuilder fieldSortBuilderAsc = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
        TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields(categoryField).size(20);
        //    TermsFacetBuilder termsFacetBuilder = FacetBuilders.


        searchResponse = client.prepareSearch("product")
                .setQuery(qb)
                .addHighlightedField("productName", 0, 0)
                .setHighlighterPreTags("<span class='keyword'>")
                .setHighlighterPostTags("</span>")
                .addSort(fieldSortBuilderAsc)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .addFacet(termsFacetBuilder)
                .setTrackScores(true)
                .setFrom(0)
                .setSize(20)
                        //.setExplain(true)
                .execute()
                .actionGet();

        client.close();

        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();


        for (SearchHit item : searchResponse.getHits()) {
            results.add(item.getSource());
        }


        client.close();

        return searchResponse.toString();

    }

    /**
     * S01. 상품 목록
     *
     * @param searchWord  String	검색어
     *
     *
     *
     * @return product 객체
     *
     * @throws Exception http exception
     */
    @RequestMapping(value = "{version}/{device}/product/list/search", method = RequestMethod.GET)
    public
    @ResponseBody
    String searchProductList(
            @PathVariable("version") String version,
            @PathVariable("device")  String device,
            @RequestHeader("searchWord") String searchWord,
            @RequestHeader(value="categoryId", defaultValue="") String categoryId
    ) throws Exception {

        String  returnMsg = "fail";
        String  returnCode = "fail";

        BoolQueryBuilder qb = null;

        String categoryField = "";


        //List<HashMap> prod_list = productService.searchProductList(product);

        Settings settings;
        Client client;

        settings = ImmutableSettings
                .settingsBuilder()
                .build();

        client = buildClient(settings);

        SearchResponse searchResponse;

        //qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
        //qb = qb.must(QueryBuilders.queryStringQuery(URLDecoder.decode(searchWord, "UTF-8")).field("productName").analyzer("korean")); // del_yn 변수중에 N인것 적용
        qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
        if(categoryId.length()>0){
            qb = qb.must(QueryBuilders.queryStringQuery(URLDecoder.decode(searchWord, "UTF-8")).field("productName").analyzer("korean"))
                    .must(QueryBuilders.termQuery("groupCatId", categoryId)); // del_yn 변수중에 N인것 적용
            categoryField = "catNm";
        }else{
            qb = qb.must(QueryBuilders.queryStringQuery(URLDecoder.decode(searchWord, "UTF-8")).field("productName").analyzer("korean"));
            categoryField = "groupCatNm";
        }


        //TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName", searchWord);
        FieldSortBuilder fieldSortBuilderAsc = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
        TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields(categoryField).size(20);
        //    TermsFacetBuilder termsFacetBuilder = FacetBuilders.


        searchResponse = client.prepareSearch("product")
                .setQuery(qb)
                .addHighlightedField("productName", 0, 0)
                .setHighlighterPreTags("<span class='keyword'>")
                .setHighlighterPostTags("</span>")
                .addSort(fieldSortBuilderAsc)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .addFacet(termsFacetBuilder)
                .setTrackScores(true)
                .setFrom(0)
                .setSize(20)
                        //.setExplain(true)
                .execute()
                .actionGet();

        client.close();

        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();


        for (SearchHit item : searchResponse.getHits()) {
            results.add(item.getSource());
        }


        client.close();

        return searchResponse.toString();

    }

    protected Client buildClient(Settings settings) throws Exception {
        TransportClient client = new TransportClient(settings);
        String nodes = "112.175.47.183:9300";
        String[] nodeList = nodes.split(",");
        int nodeSize = nodeList.length;

        for (int i = 0; i < nodeSize; i++) {
            client.addTransportAddress(toAddress(nodeList[i]));
        }

        return client;
    }

    private InetSocketTransportAddress toAddress(String address) {
        if (address == null) return null;

        String[] splitted = address.split(":");
        int port = 9300;

        if (splitted.length > 1) {
            port = Integer.parseInt(splitted[1]);
        }

        return new InetSocketTransportAddress(splitted[0], port);
    }
}
