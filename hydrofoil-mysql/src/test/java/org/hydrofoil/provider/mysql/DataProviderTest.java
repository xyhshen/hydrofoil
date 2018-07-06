package org.hydrofoil.provider.mysql;

import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataProvider;
import org.hydrofoil.common.provider.IDataSource;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hydrofoil.provider.mysql.MysqlDatasourceSchema.DatasourceItem.ConnectUrl;
import static org.hydrofoil.provider.mysql.MysqlDatasourceSchema.DatasourceItem.Password;
import static org.hydrofoil.provider.mysql.MysqlDatasourceSchema.DatasourceItem.Username;
import static org.junit.Assert.assertTrue;

/**
 * DataProviderTest
 * <p>
 * package org.hydrofoil.provider.mysql
 *
 * @author xie_yh
 * @date 2018/7/6 11:38
 */
public final class DataProviderTest {

    private IDataSource dataSource;

    @Before
    public void init(){
        DataSourceSchema dataSourceSchema = new DataSourceSchema();
        Map<String,String> map = new HashMap<>();
        map.put(ConnectUrl.getName(),"jdbc:mysql://10.110.18.53:3306/gongan?useUnicode=true&characterEncoding=utf-8&useOldAliasMetadataBehavior=true&allowMultiQueries=true");
        map.put(Username.getName(),"root");
        map.put(Password.getName(),"123456a?");
        dataSourceSchema.putItem("configitem",map);
        dataSource = new DataProvider().connect(dataSourceSchema);
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
        RowQueryRequest query = new RowQueryRequest();
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
            RowQueryRequest.AssociateRowQuery associateRowQuery = new RowQueryRequest.AssociateRowQuery();
            associateRowQuery.setName("airpanl_link");
            associateRowQuery.getFields().add("idnumber");
            associateRowQuery.getFields().add("key");
            associateRowQuery.getFields().add("id");
            associateRowQuery.getFields().add("name");
            associateRowQuery.getFields().add("startTime");
            associateRowQuery.getFields().add("startSite");
            associateRowQuery.getMatch().add(new RowQueryRequest.AssociateMatch(
                    QMatch.eq("idnumber",null),
                    "person",
                    "idnumber"
            ));
            query.getAssociateQuery().add(associateRowQuery);
        }
        query.setStart(0L).setLimit(100L);
        RowQueryResponse response = dataSource.sendQuery(query);
        System.out.println(response);
    }

}
