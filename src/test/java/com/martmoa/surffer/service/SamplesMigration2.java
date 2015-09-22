package com.martmoa.surffer.service;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SamplesMigration2 {
    public static void main(String[] args) {
        try {
            createSampleData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void createSampleData() throws Exception
    {
        try
        {
            Connection conn = null;

            try
            {
                Class.forName("org.mariadb.jdbc.Driver").newInstance();

                // ElasticSearch 접속을 위한 기본 세팅 추가, 클러스트 이름 매칭
                Settings settings = ImmutableSettings.settingsBuilder()
                        .put("cluster.name", "product").build();

                // ElasticSearch IP 세팅
                Client client = new TransportClient()
                        .addTransportAddress(new InetSocketTransportAddress(
                                "112.175.47.183",
                                9300));


                BulkRequestBuilder bulkRequest = client.prepareBulk();
                int startIndex = 0;
                int endIndex   = 0; // 마이그레이션 갯수 설정SELECT cat_id, cat_nm FROM category
          //      String countQuery = "select count(*) ct from site_product";
                String countQuery = "SELECT count(*) ct " +
                        " FROM site_product a " +
                        "  inner join product b on a.prod_id = b.prod_id " +
                        "  inner join product_grp_attr c on b.prod_grp_id = c.prod_grp_id " +
                        "  INNER JOIN category g ON c.cat4_id = g.cat_id " +
                        " WHERE c.cat4_id like 'A001%'";
                Statement countStmt = null;
                ResultSet countRs = null;

                conn = (Connection) DriverManager.getConnection("jdbc:mysql://112.175.47.184:3306/martmoa_dev", "martmoa_dev", "martmoa20!!");
                countStmt = (Statement) conn.createStatement();
                countRs = (ResultSet) countStmt.executeQuery(countQuery);
                countRs.next();
                endIndex = countRs.getInt("ct");

             //   System.out.println("Count query result : " + endIndex);

                for(startIndex = 0; startIndex < endIndex; startIndex += 2500) {

                    System.out.println("INSERT startIndex : " + startIndex);
                    long startTime = System.currentTimeMillis(); // Get the start Time
                    long endTime = 0;
                    Statement stmt = null;
                    ResultSet rs = null;

                    String query = "SELECT A.SITE_PROD_ID as siteProductId" +
                            "  , A.SITE_PROD_NM as productName" +
                            "  , B.PROD_IMG_URL as productImg" +
                            "  , A.PRICE as productPrice" +
                            "  , getUnitPrice(a.price,E.qty,E.unit) as productUnitPrice" +
                            "  , ifnull(A.MAX_PRICE,0)-ifnull(A.MIN_PRICE,0) as productDiffPrice" +
                            "  , CASE A.OPTION_FLAG WHEN 1 THEN 'Y' ELSE 'N' END as prodOptionYn" +
                            "  , '1' as productOrder" +
                            "  , a.MAX_PRICE as productMaxPrice" +
                            "  , a.MAX_PRICE_SITE as productMaxPriceSite" +
                            "  , a.MIN_PRICE as productMinPrice" +
                            "  , a.MIN_PRICE_SITE as productMinPriceSite" +
                            "  , a.SITE_ID as siteId " +
                            "  , g.CAT_ID   AS catId " +
                            "  , g.CAT_NM  catNm  " +
                            "  , substring(g.CAT_ID , 1,8)   AS groupCatId " +
                            "  ,(SELECT cat_nm FROM category where cat_id = substring(g.CAT_ID , 1,8))  groupCatNm  " +
                            "FROM site_product A "+
                            "  INNER JOIN site_product_img B ON A.SITE_PROD_ID = B.SITE_PROD_ID "+
                            "  INNER JOIN product e          ON A.prod_id = e.prod_id  "+
                            "  INNER JOIN product_grp_attr f ON e.prod_grp_id = f.prod_grp_id  "+
                            "  INNER JOIN category g         ON f.cat4_id = g.cat_id  "+
                            "  INNER JOIN site h ON a.site_id = h.site_id AND h.is_use = 1  "+
                            "where  f.cat4_id like 'A001%' " +
                                    " limit " + startIndex + ", 20418";

                    try
                    {
                        stmt = (Statement) conn.createStatement();
                        rs = (ResultSet) stmt.executeQuery(query);

                        while (rs.next() == true)
                        {
                            // AAA는 RDBMS에서 PK 로 사용중
                            String parameter1 = rs.getString("siteProductId");
                            String parameter2 = rs.getString("productName");
                            String parameter3 = rs.getString("productImg");
                            Double parameter4 = rs.getDouble("productPrice");
                            String parameter5 = rs.getString("productUnitPrice");
                            Double parameter6 = rs.getDouble("productDiffPrice");
                            String parameter7 = rs.getString("prodOptionYn");
                            String parameter8 = rs.getString("productOrder");
                            Double parameter9 = rs.getDouble("productMaxPrice");
                            String parameter10 = rs.getString("productMaxPriceSite");
                            Double parameter11 = rs.getDouble("productMinPrice");
                            String parameter12 = rs.getString("productMinPriceSite");
                            String parameter13 = rs.getString("siteId");
                            String parameter14 = rs.getString("catId");
                            String parameter15 = rs.getString("catNm");
                            String parameter16 = rs.getString("groupCatId");
                            String parameter17 = rs.getString("groupCatNm");


                            XContentBuilder obj = XContentFactory.jsonBuilder()
                                    .startObject()
                                    .field("siteProductId", parameter1)
                                    .field("productName", parameter2)
                                    .field("productImg", parameter3)
                                    .field("productPrice", parameter4)
                                    .field("productUnitPrice", parameter5)
                                    .field("productDiffPrice", parameter6)
                                    .field("prodOptionYn", parameter7)
                                    .field("productOrder", parameter8)
                                    .field("productMaxPrice", parameter9)
                                    .field("productMaxPriceSite", parameter10)
                                    .field("productMinPrice", parameter11)
                                    .field("productMinPriceSite", parameter12)
                                    .field("siteId", parameter13)
                                    .field("catId", parameter14)
                                    .field("catNm", parameter15)
                                    .field("groupCatId", parameter16)
                                    .field("groupCatNm", parameter17)
                                    .endObject();

/*
                            .startObject("facet")
                                .field("type", "string")
                                .field("store", "yes")
                                .field("index", "not_analyzed")
                                .endObject();*/

                            // 인덱싱 쿼리 생성
                            IndexRequest irb = client.prepareIndex("product", "product", parameter1)
                                    .setOperationThreaded(false)
                                    .setSource(obj
                                    ).request();

                            // 인덱싱 Bulk Request 생성
                            // 단순 Insert가 아닌 Upsert 명령어 ( 없을경우 Insert , 있을 경우 Update )
                            bulkRequest.add(client.prepareUpdate("product", "product", parameter1)
                                            .setConsistencyLevel(WriteConsistencyLevel.DEFAULT)
                                            .setDoc(irb)
                                            .setUpsert(irb)
                            )
                            .setReplicationType(ReplicationType.ASYNC)
                                    .setConsistencyLevel(WriteConsistencyLevel.QUORUM)
                                    .setRefresh(false);
                        }

                        //Bulk 요청
                        bulkRequest.execute().actionGet(25000);
                        bulkRequest = client.prepareBulk();
                        endTime = System.currentTimeMillis();

                        System.out.println("Use Time " +  startIndex + " : " + (endTime-startTime)/1000);

                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    finally
                    {
                        if (rs != null)
                        {
                            try
                            {
                                rs.close();
                            }
                            catch (SQLException sqlEx)
                            {
                                rs = null;
                            }
                        }

                        if (stmt != null)
                        {
                            try
                            {
                                stmt.close();
                            }
                            catch (SQLException sqlEx)
                            {
                                stmt = null;
                            }
                        }
                    }


                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.out.println("SQLException: " + ex.getMessage());
            }

            conn.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
