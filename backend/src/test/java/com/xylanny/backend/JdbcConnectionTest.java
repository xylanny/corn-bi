package com.xylanny.backend;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 简单的 JDBC 连接测试
 */
public class JdbcConnectionTest {

    @Test
    void testJdbcConnection() {
        String url = "jdbc:mysql://localhost:3306/corn_bi_db";
        String username = "root";
        String password = "123456";

        // 尝试获取一个数据库连接
        try (
                // 若获取成功，无论后续代码执行成功还是抛出异常，连接都会被自动关闭，无需手动调用 close()
                Connection conn = DriverManager.getConnection(url, username, password);
                // 预编译SQL语句并发送给数据库，生成PreparedStatement对象
                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO chart (chart_name, chart_goal, chart_type, chart_conclusion) VALUES  (?, ?, ?, ?)");
                PreparedStatement queryStmt = conn.prepareStatement("SELECT id, chart_name, chart_goal, chart_type, chart_conclusion FROM chart WHERE chart_name = ?");
        ) {
            // 设置占位符参数
            insertStmt.setString(1, "测试JDBCDriver");
            insertStmt.setString(2, "JDBC Driver到底是干什么的？");
            insertStmt.setString(3, "test");
            insertStmt.setString(4, "~~~");

            // 执行SQL，接收受影响的行数
            int affectedRows = insertStmt.executeUpdate();

            // 设置占位符参数
            queryStmt.setString(1, "测试JDBCDriver");

            try(ResultSet rs = queryStmt.executeQuery()) {
                System.out.println("查询结果：");
                while(rs.next()){
                    long id = rs.getLong("id");
                    String chartName = rs.getString("chart_name");
                    String chartGoal = rs.getString("chart_goal");
                    String chartType = rs.getString("chart_type");
                    String chartConclusion = rs.getString("chart_conclusion");

                    System.out.println("id: " + id);
                    System.out.println("name:" + chartName);
                    System.out.println("goal: " + chartGoal);
                    System.out.println("type: " + chartType);
                    System.out.println("conclusion: " + chartConclusion);
                }
            }
        } catch (SQLException e) { // 捕获连接失败、SQL 语法错而抛出的异常
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
/**
 * JDBC Driver是什么？
 * Java应用程序与数据库之间的双向传信者
 * JDBC Driver工作流程：
 * 1.驱动注册：
 * Spring Boot应用启动时，mysql-connector-j依赖中的java.sql.DriverManager会通过SPI机制，
 * 自动加载并注册 com.mysql.cj.jdbc.Driver类，建立了Java应用与MySQL数据库通信的“协议栈”。
 *
 * 2.获取数据库连接：
 * 首次需要操作数据库时，需要调用DriverManager.getConnection();
 * 此时，JDBC Driver会根据你提供的 URL、用户名和密码，与MySQL服务器建立一条底层的Socket网络连接;
 * 连接成功后，会返回一个Connection对象，代表与数据库的一次会话。
 *
 * 3.执行SQL：
 * 通过Connection对象创建Statement或PreparedStatement对象，并传入SQL语句；
 * JDBC Driver会将这个SQL语句（以及参数）按照MySQL的网络协议打包成二进制数据包，通过Socket连接发送给MySQL服务器。
 *
 * 4.等待并接收结果：
 * JDBC Driver会阻塞等待MySQL服务器返回执行结果；
 * 4.1 对于查询语句，MySQL会将结果集以二进制流的形式返回；
 * 4.2 对于非查询语句，MySQL返回一个更新计数。
 *
 * 5.处理结果
 * 5.1 对于查询结果
 * 调用rs.next()和rs.getInt("id")等方法时，
 * JDBC Driver会根据MySQL返回的数据类型元数据；
 * 将二进制数据转换为对应的Java类型，这个过程是逐行、按需进行的，不会一次性将所有数据加载到内存。
 *
 * 6.资源释放
 * 调用rs.close()、stmt.close()或conn.close()时，
 * JDBC Driver会分别释放 ResultSet、Statement和Connection 所持有的资源；
 * 其中，Connection的关闭通常意味着将底层的 Socket 连接断开或归还给连接池（如 HikariCP）,以备后续复用；
 * 最终，JDBC Driver 将结果集数据流解析完毕，并将 Java 对象返回给调用方。
 */