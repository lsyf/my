package com.loushuiyifan;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @author 漏水亦凡
 * @date 2017/10/24
 */
public class SqlTest {

    String driver = "oracle.jdbc.driver.OracleDriver";
    String jdbcurl = "jdbc:oracle:thin:@//134.96.83.50:1521/pgbb";
    String username = "borpt";
    String password = "borpt_1004";


    @Test
    public void test() {
        String sql = "select a.latn_id as \"id\",\n" +
                "       b.c3_name as \"name\",\n" +
                "       sum(case\n" +
                "             when substr(a.BILLING_CYCLE, 5, 2) =\n" +
                "                  to_char(add_months(to_date(201708, 'yyyymm'), -2), 'mm') then\n" +
                "              NEW_DISCOUNT\n" +
                "             else\n" +
                "              0\n" +
                "           end) as \"amount1\",\n" +
                "       sum(case\n" +
                "             when substr(a.BILLING_CYCLE, 5, 2) =\n" +
                "                  to_char(add_months(to_date(201708, 'yyyymm'), -2), 'mm') then\n" +
                "              sl\n" +
                "             else\n" +
                "              0\n" +
                "           end) as \"count1\",\n" +
                "       sum(case\n" +
                "             when substr(a.BILLING_CYCLE, 5, 2) =\n" +
                "                  to_char(add_months(to_date(201708, 'yyyymm'), -1), 'mm') then\n" +
                "              NEW_DISCOUNT\n" +
                "             else\n" +
                "              0\n" +
                "           end) as \"amount2\",\n" +
                "       sum(case\n" +
                "             when substr(a.BILLING_CYCLE, 5, 2) =\n" +
                "                  to_char(add_months(to_date(201708, 'yyyymm'), -1), 'mm') then\n" +
                "              sl\n" +
                "             else\n" +
                "              0\n" +
                "           end) as \"count2\",\n" +
                "       sum(case\n" +
                "             when substr(a.BILLING_CYCLE, 5, 2) =\n" +
                "                  to_char(to_date(201708, 'yyyymm'), 'mm') then\n" +
                "              NEW_DISCOUNT\n" +
                "             else\n" +
                "              0\n" +
                "           end) as \"amount3\",\n" +
                "       sum(case\n" +
                "             when substr(a.BILLING_CYCLE, 5, 2) =\n" +
                "                  to_char(to_date(201708, 'yyyymm'), 'mm') then\n" +
                "              sl\n" +
                "             else\n" +
                "              0\n" +
                "           end) as \"count3\"\n" +
                "  from Tcft_Share_Summary_Log_fx_2 a, v_dim_c3_map b\n" +
                " where a.latn_id = b.latn_id_ocs\n" +
                "   and a.BILLING_CYCLE >=\n" +
                "       to_char(add_months(to_date(201708, 'yyyymm'), -2), 'yyyymm')\n" +
                "   and a.BILLING_CYCLE <= 201708\n" +
                " group by a.latn_id, b.c3_name, b.c3_order_id\n" +
                " order by b.c3_order_id\n";
        try {
            List<Map<String, Object>> list = executeQueryBySQLForMap(sql, conn1());
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection conn1() throws Exception {
        Map<String, String> conf = new HashedMap();
        conf.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, driver);
        conf.put(DruidDataSourceFactory.PROP_URL, jdbcurl);
        conf.put(DruidDataSourceFactory.PROP_USERNAME, username);
        conf.put(DruidDataSourceFactory.PROP_PASSWORD, password);
        conf.put(DruidDataSourceFactory.PROP_INITIALSIZE, "3");

        DataSource ds = DruidDataSourceFactory.createDataSource(conf);
        Connection conn = ds.getConnection();
        return conn;
    }

    public Connection conn2() throws Exception {

        Class.forName(driver);
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        Connection conn = DriverManager.getConnection(jdbcurl, props);
        return conn;
    }

    public List<Map<String, Object>> executeQueryBySQLForMap(String sql, Connection conn) throws Exception {

        List<Map<String, Object>> list = new ArrayList<>();
        List<String> cols = new ArrayList<>();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                cols.add(metaData.getColumnLabel(i + 1));
            }
            int resultCount = 0;
            while (rs.next()) {
                resultCount++;

                Map<String, Object> map = new HashMap<>();
                for (int j = 0; j < columnCount; j++) {
                    map.put(cols.get(j), rs.getString(j + 1));
                }
                list.add(map);
            }

            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }


    public String[][] executeQueryBySQL(String sql, Connection conn) throws Exception {

        List<String[]> list = new ArrayList<>();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            list = new LinkedList<>();
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = metaData.getColumnLabel(i + 1);
            }
            list.add(row);
            int resultCount = 0;
            while (rs.next()) {
                resultCount++;

                row = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    row[j] = rs.getString(j + 1);
                }
                list.add(row);
            }

            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list.toArray(new String[][]{});
    }

}
