package com.martmoa.surffer.service;

import com.martmoa.thor.config.DataConfig;
import com.martmoa.thor.config.PropertiesConfig;
import com.martmoa.thor.constant.ResponseAttribute;
import com.martmoa.thor.constant.ResponseCode;
import com.martmoa.thor.dao.CategoryMapper;
import com.martmoa.thor.dao.ProductMapper;
import com.martmoa.thor.domain.Category;
import com.martmoa.thor.domain.Product;
//import org.bitbucket.eunjeon.seunjeon.Analyzer;
//import org.bitbucket.eunjeon.seunjeon.Term;
import org.bitbucket.eunjeon.seunjeon.Analyzer;
import org.bitbucket.eunjeon.seunjeon.Term;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.*;

import static java.lang.String.format;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PropertiesConfig.class, DataConfig.class})
public class SearchServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceTest.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;


    private Product product = new Product();

    private Category category = new Category();

    @Test
    public void searchArrayTest(){
        String searchWord = "사과";
        Settings settings;
        Client client;

        String searchText[] ;

        BoolQueryBuilder qb = null;
        QueryStringQueryBuilder qS = null;
        SearchRequestBuilder srb = null;
        SearchResponse response  = null;

        settings = ImmutableSettings
                .settingsBuilder()
                .build();

        try {

            client = buildClient(settings);

            SearchResponse searchResponse;
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName",searchWord);

            qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다

            // 스트링쿼리 초기화
            qS = QueryBuilders.queryString(searchWord);
            qS.field("productName");

            qb.should(qS.field("productName").analyzer("korean").analyzeWildcard(true));

            FieldSortBuilder fieldSortBuilderAsc = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
            //MatchQueryBuilder.Operator.AND

            if (searchWord.equalsIgnoreCase(""))
                searchWord = "*";

            qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
            qb = qb.must(QueryBuilders.queryStringQuery(searchWord).field("productName").analyzer("korean")); // del_yn 변수중에 N인것 적용

            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields("groupCatNm").size(10);
            TermsFacetBuilder termsFacetBuilder2 = FacetBuilders.termsFacet("cat_term_service").fields("catNm").size(10);


            int size = 20;
            int from = 0;

            //   List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

            //<span class="keyword">

            searchResponse = client.prepareSearch("product")
                    .setQuery(qb)
                    .addHighlightedField("productName")
                    .setHighlighterFragmentSize(2000)
                    .setHighlighterNumOfFragments(2)
                    .setHighlighterPreTags("<span class='keyword'>")
                    .setHighlighterPostTags("</span>")
                    .addSort(fieldSortBuilderAsc)
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .addFacet(termsFacetBuilder)
                            //       .addAggregation(aggsBuilder)
                    .setTrackScores(true)
                    .setFrom(from)
                    .setSize(size)
                    .setExplain(false)
                    .execute()
                    .actionGet();

            client.close();


            List<String> productList = new ArrayList<>();

            SearchHits results = searchResponse.getHits();
            Map<String, Object> json = new HashMap<String, Object>();

            productList.add(results.toString());

            SearchHit[] source = results.getHits();

           // System.out.println(SearchHits);




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchFacetTest(){
        String searchWord = "apple";
        Settings settings;
        Client client;

        String searchText[] ;

        BoolQueryBuilder qb = null;
        QueryStringQueryBuilder qS = null;
        SearchRequestBuilder srb = null;
        SearchResponse response  = null;

        settings = ImmutableSettings
                .settingsBuilder()
                .build();

        try {


            searchText = searchWord.split("%20");
         //   searchText = QueryParser.escape(searchText);

            client = buildClient(settings);
            String categoryId = "A0100006";

            categoryId = "A0100006";

            SearchResponse searchResponse;
            // MatchAllQueryBuilder matchAllQueryBuilder = new MatchAllQueryBuilder();
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName",searchWord);

            //termQueryBuilder. minimumMatch(1);
            //MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("test");

            qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
            //qb = qb.must(QueryBuilders.termQuery("del_yn", "N")); // del_yn 변수중에 N인것 적용

            //searchWord = QueryParser.escape(searchWord);


            // 스트링쿼리 초기화
            qS = QueryBuilders.queryString(searchWord);
            qS.field("productName", 2);
            qS.field("ptype");

          //  qb.should(qS.field("productName").analyzer("korean").analyzeWildcard(true));

            FieldSortBuilder fieldSortBuilderAsc = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
            //MatchQueryBuilder.Operator.AND("productName")

            if (searchWord.equalsIgnoreCase(""))
                searchWord = "*";

            qb = QueryBuilders.boolQuery() // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
                .must(qS.analyzer("korean")); // del_yn 변수중에 N인것 적용
            //qb = qb.must(QueryBuilders.queryStringQuery(searchWord).field("productName").analyzer("korean")); // del_yn 변수중에 N인것 적용


            /*
            example
            QueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("address",address))
                    .mustNot(QueryBuilders.termQuery("address", "10.203.238.140"))
                    .should(QueryBuilders.termQuery("client", ""));
*/
            // TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName", searchWord);
            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields("groupCatId").size(10);

            //searchWord = QueryParser.escape(searchWord);

            int size = 20;
            int from = 0;

            //   List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

            //<span class="keyword">
/*
            FilterBuilder filter = boolFilter()
                    .must(termFilter("tag", "wow"))
                    .mustNot(rangeFilter("age").from("10").to("20"))
                    .should(termFilter("tag", "sometag"))
                    .should(termFilter("tag", "sometagtag"));*/


            FilterBuilder categoryFilter = FilterBuilders.boolFilter()
                    .must(FilterBuilders.termFilter("groupCatId", categoryId));

                    searchResponse = client.prepareSearch("product")
                    .setQuery(qb)
                    .addHighlightedField("productName")
                    .setHighlighterFragmentSize(2000)
                    .setHighlighterNumOfFragments(2)
                    .setPostFilter(categoryFilter)
                    //.setFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
                    .setHighlighterPreTags("<span class='keyword'>")
                    .setHighlighterPostTags("</span>")
                    .addSort(fieldSortBuilderAsc)
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .addFacet(termsFacetBuilder)
                            //       .addAggregation(aggsBuilder)
                    .setTrackScores(true)
                    .setFrom(from)
                    .setSize(size)
                    .setExplain(false)
                    .execute()
                    .actionGet();

            client.close();

            List<Map<String, Object>> productList = new ArrayList<>();

            SearchHit[] results = searchResponse.getHits().getHits();
            Map<String, Object> json = new HashMap<String, Object>();
            for (SearchHit hit : results) {
                productList.add(hit.getSource());
                //productList.add(hit.getHighlightFields(""));
            }


            System.out.println(searchResponse.toString());




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchUtilTest2() {

        String searchWord = "dkfkqldksskdlxm";
        String searchWord2 = "한글테스트";

     //   searchWord =  stringUtil.convertKeyByLanguage(searchWord);
     //   searchWord2 =  stringUtil.convertKeyByKeyword(searchWord2);

        System.out.println(searchWord2);

    }


    @Test
    public void searchAnalyzeTest2() {

        List<Term> result = Analyzer.parseJava("형태소분석기입니다. 사랑합니다.");

        String[] analyzeTerm;

        for (Term term: result) {
            System.out.println(term.surface());
            analyzeTerm = term.feature().split(",");
            if(analyzeTerm.equals("NNG")){
                System.out.println(term.toString());
            }

        }

    }

    @Test
    public void searchAnalyzeTest() {

        try {

            /*
            A001,농산 308189
            A002,정육 143771
            A003,수산 107809
            A004,유제품/냉장 161991
            A005,간편식 134484
            A006,소스/반찬 114855
            A007,음료/간식 223826
            A008,생활용품 320725
            A009,육아 199426
            A010,뷰티/여성 658866
            A011,건강기능식품 116909
            A050,선물세트 9
            A099,미취급 제외
            */
            String categoryId = "A001";
            String categoryName = "농산";

            category.setUpCategoryId(categoryId);
            List fileContent = new ArrayList<>();
            FileOutputStream fos = new FileOutputStream(categoryId+"-"+categoryName+".csv");

            //List<Category> categoryList = categoryMapper.searchCategoryKeywordList(category);

            List<Product> prodList = null;


            OutputStreamWriter osw = new OutputStreamWriter(fos, "MS949");
            BufferedWriter bw = new BufferedWriter(osw);

            String[] analyzeTerm;
/*

            for(Category cateogy: categoryList) {
                product.setCatId(cateogy.getCategoryId());
                prodList = productMapper.searchKeywordList(product);

                for(Product product: prodList){
                    List<Term> result = Analyzer.parseJava(product.getProductName());
                    for (Term term: result) {
                        analyzeTerm = term.feature().split(",");
                        System.out.println(analyzeTerm);

                        if(analyzeTerm.equals("NNG")){
                            //System.out.println(term.toString());
                            bw.write(product.getProductName() + "," + term.toString()+"\r\n");
                        }
                        // fileContent.add(product.getProductName() +","+ term.toString());


                    }
                }
            }
*/



            bw.flush();
            osw.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void searchKeywordListTest() {

        String searchWord = "cj";
        Settings settings;
        Client client;

        BoolQueryBuilder qb = null;
        QueryStringQueryBuilder qS = null;
        SearchRequestBuilder srb = null;
        SearchResponse response  = null;

        settings = ImmutableSettings
                .settingsBuilder()
                .build();

        try {
            client = buildClient(settings);
            SearchResponse searchResponse;
           // MatchAllQueryBuilder matchAllQueryBuilder = new MatchAllQueryBuilder();

            //MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("test");

            /*

            "query": {
                "bool": {
                    "must": [
                    {
                        "query_string": {
                            "default_operator": "AND",
                                    "fields": [
                            "productName^5",
                                    "catNm"
                            ],
                            "query": "닭알 구운"
                        }
                    }
                    ]
                }
            },
*/

            qb = QueryBuilders.boolQuery(); // Bool 쿼리빌더 초기화. 이놈이 제일 중요하다
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName", URLDecoder.decode(searchWord, "UTF-8"));
            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields("groupCatNm").size(10);
            qb = qb.must(QueryBuilders.queryStringQuery(searchWord).field("productName").analyzer("korean")); // del_yn 변수중에 N인것 적용

          //  TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productName", URLDecoder.decode(searchWord, "UTF-8"));
             FieldSortBuilder fieldSortBuilderAsc = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
            //TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields("groupCatNm").size(10);
            //MatchQueryBuilder.Operator.AND


         //   List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            //TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("term_service").fields("groupCatNm").size(100);

          /*  srb = client.prepareSearch("product")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(qb)
                    .addSort("_score", SortOrder.ASC) // reg_dt기반 정렬 적용
                    .setSize(size) // 몇개를 가져올 것인가?
                    .setFrom(from) // 어디서 부터?
                    .setExplain(false);
            response = srb.execute().actionGet(10000);*/
         //   ValueCountBuilder aggsBuilder = AggregationBuilders.count("catNm");
    //CommonTermsQueryBuilder.Operator = "";


         //   ValueCountBuilder aggsBuilder = AggregationBuilders.count("aggs_result");
           // aggsBuilder.field("buyer_country"); // 20개 로 null value가 하나 존재 함.
            searchResponse = client.prepareSearch("product")
                    .setQuery(qb)
                    .addSort(fieldSortBuilderAsc)
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .addFacet(termsFacetBuilder)
                    .setTrackScores(true)
                    .setSize(50)
                    .setExplain(true)
                    .execute()
                    .actionGet();

            client.close();

            List<Map<String, Object>> productList = new ArrayList<>();

            SearchHit[] results = searchResponse.getHits().getHits();
            Map<String, Object> json = new HashMap<String, Object>();
            for (SearchHit hit : results) {
                productList.add(hit.getSource());
            }

            TermsFacet facet = (TermsFacet) searchResponse.getFacets().facetsAsMap().get("term_service");

            List<HashMap> result = new ArrayList<>();

            for (TermsFacet.Entry entry : facet) {
       /*     System.out.println(entry.getTerm().toString());
            System.out.println(entry.getCount());*/
                HashMap<String, Object> map = new HashMap<>();
                map.put("catNm", entry.getTerm().toString());
                map.put("catCnt", entry.getCount());
                result.add(map);
            }

            Map responseResult = new HashMap();
            responseResult.put(ResponseAttribute.RETURN_CODE, ResponseCode.OK.getCode());
            responseResult.put(ResponseAttribute.RETURN_MESSAGE, ResponseCode.OK.getMessage());
            responseResult.put("searchWord", URLDecoder.decode(searchWord, "UTF-8"));
            responseResult.put("results", productList);
            responseResult.put("categoryGroupList", result);
            responseResult.put("totalHits", searchResponse.getHits().getTotalHits());


            String resultJson = mapper.writeValueAsString(responseResult);
            System.out.println(format("resultArrayJson: %s", resultJson));

            //System.out.println(mapper.toString());







        } catch (Exception e) {
            e.printStackTrace();
        }
     /*   List<Term> result = Analyzer.parseJava("형태소분석기입니다. 사랑합니다.");
        for (Term term: result) {
            System.out.println(term);
        }*/

    }


    public Client buildClient(Settings settings) throws Exception {
        TransportClient client = new TransportClient(settings);
        String nodes = "112.175.47.183:9300";
        String[] nodeList = nodes.split(",");
        int nodeSize = nodeList.length;

        for (int i = 0; i < nodeSize; i++) {
            client.addTransportAddress(toAddress(nodeList[i]));
        }

        return client;
    }

    public InetSocketTransportAddress toAddress(String address) {
        if (address == null) return null;

        String[] splitted = address.split(":");
        int port = 9300;

        if (splitted.length > 1) {
            port = Integer.parseInt(splitted[1]);
        }

        return new InetSocketTransportAddress(splitted[0], port);
    }

}