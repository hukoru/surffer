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
import com.martmoa.common.StringUtil;

public class SamplesMigration3 {
    public static void main(String[] args) {
        try {
            createSampleData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StringUtil stringUtil = new StringUtil();

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
                        .put("cluster.name", "autocompletion").build();

                // ElasticSearch IP 세팅
                Client client = new TransportClient()
                        .addTransportAddress(new InetSocketTransportAddress(
                                "112.175.47.183",
                                9300));


                BulkRequestBuilder bulkRequest = client.prepareBulk();
                int startIndex = 0;
                int endIndex   = 0; // 마이그레이션 갯수 설정SELECT cat_id, cat_nm FROM category
          //      String countQuery = "select count(*) ct from site_product";


                //65835
                String countQuery = "SELECT count(*) ct " +
                        " FROM site_product a " +
                        " WHERE A.IS_MIN = 1";
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

                    String query = "SELECT A.SITE_PROD_ID as keyword_id" +
                            "  , A.SITE_PROD_NM as keyword" +
                            "  , A.SITE_PROD_NM as display_keyword" +
                            "  , 0 as keyword_ranking" +
                            "  FROM site_product A "+
                            "WHERE A.IS_MIN = 1" +
                            " limit " + startIndex + ", 65835";

                    try
                    {
                        stmt = (Statement) conn.createStatement();
                        rs = (ResultSet) stmt.executeQuery(query);

                        while (rs.next() == true)
                        {
                            // AAA는 RDBMS에서 PK 로 사용중
                            String parameter1 = rs.getString("keyword_id");
                            String parameter2 = rs.getString("keyword");
                            String parameter3 = rs.getString("display_keyword");
                            Double parameter4 = rs.getDouble("keyword_ranking");

                            XContentBuilder obj = XContentFactory.jsonBuilder()
                                    .startObject()
                                    .field("keyword_id", parameter1)
                                    .field("keyword", stringUtil.convertKeyByKeyword(parameter2))
                                    .field("display_keyword", parameter3)
                                    .field("keyword_ranking", parameter4)
                                    .endObject();

/*
                            .startObject("facet")
                                .field("type", "string")
                                .field("store", "yes")
                                .field("index", "not_analyzed")
                                .endObject();*/

                            // 인덱싱 쿼리 생성
                            IndexRequest irb = client.prepareIndex("autocompletion", "autocompletion", parameter1)
                                    .setOperationThreaded(false)
                                    .setSource(obj
                                    ).request();

                            // 인덱싱 Bulk Request 생성
                            // 단순 Insert가 아닌 Upsert 명령어 ( 없을경우 Insert , 있을 경우 Update )
                            bulkRequest.add(client.prepareUpdate("autocompletion", "autocompletion", parameter1)
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
