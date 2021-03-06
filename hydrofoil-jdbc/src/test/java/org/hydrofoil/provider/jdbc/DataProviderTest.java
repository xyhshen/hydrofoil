package org.hydrofoil.provider.jdbc;

import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hydrofoil.provider.jdbc.JdbcDatasourceSchema.DatasourceItem.*;

/**
 * DataProviderTest
 * <p>
 * package org.hydrofoil.provider.jdbc
 *
 * @author xie_yh
 * @date 2018/7/6 11:38
 */
public final class DataProviderTest {

    private IDataConnector dataSource;

    @Before
    public void init(){
        DataSourceSchema dataSourceSchema = new DataSourceSchema();
        Map<String,String> map = new HashMap<>();
        map.put(ConnectUrl.getName(),"jdbc:jdbc://10.110.18.53:3306/gongan?useUnicode=true&characterEncoding=utf-8&useOldAliasMetadataBehavior=true&allowMultiQueries=true");
        map.put(Username.getName(),"root");
        map.put(Password.getName(),"123456a?");
        dataSourceSchema.putItem("configitem",map);
        dataSource = new DataProvider().connect(null);
    }

    @After
    public void destroy(){
        try {
            dataSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendQuery(){
        RowQueryScan query = new RowQueryScan();
        query.setName("person");
        query.getFields().add("idnumber");
        query.getFields().add("name");
        query.getFields().add("gender");
        query.getFields().add("telphone");
        query.getFields().add("spouseName");
        query.getFields().add("workunit");
        query.getFields().add("address");
        query.getFields().add("utime");
        query.getUniqueField().add("address");
        query.getMatch().add(QMatch.like("address","济南"));
        {
            RowQueryScan.AssociateRowQuery associateRowQuery = new RowQueryScan.AssociateRowQuery();
            associateRowQuery.setName("airpanl_link");
            associateRowQuery.getFields().add("idnumber");
            associateRowQuery.getFields().add("key");
            associateRowQuery.getFields().add("id");
            associateRowQuery.getFields().add("name");
            associateRowQuery.getFields().add("startTime");
            associateRowQuery.getFields().add("startSite");
            associateRowQuery.getMatch().add(new RowQueryScan.AssociateMatch(
                    QMatch.eq("idnumber",null),
                    "person",
                    "idnumber"
            ));
            query.getAssociateQuery().add(associateRowQuery);
        }
        query.setOffset(0L).setLimit(100L);
        RowQueryResponse response = dataSource.scanRow(query);
        System.out.println(response);
    }

}
