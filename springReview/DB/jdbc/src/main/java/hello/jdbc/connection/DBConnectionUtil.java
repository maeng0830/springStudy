package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

	public static Connection getConnection() {
		Connection con = null;
		try {
			// JDBC가 제공하는 DriverManager.getConnection()
			// 라이브러리에 등록되어있는 DB 드라이버를 찾는다.
			// 그리고 해당 드라이버가 작동하여 DB와 커넥션을 맺고, 그 커넥션을 반환한다.
			// JDBC는 Connection이라는 인터페이스를 정의해두었다.
			// 각 DB 드라이버들은 Connection 인터페이스의 구현체를 반환한다.
			con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return con;
	}
}
