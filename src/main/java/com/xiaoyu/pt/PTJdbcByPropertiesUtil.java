package com.xiaoyu.pt;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PTJdbcByPropertiesUtil {
	private static String filePath = "pt/ptjdbc.properties";
	private static PTJdbcByPropertiesUtil instance = null;

	private static Map<String, String> typeMapper = new HashMap<String, String>();
	// 数据库类型--------------java类型;jdbc类型
	static {
		typeMapper.put("CHAR", "java.lang.String,CHAR");
		typeMapper.put("VARCHAR", "java.lang.String,VARCHAR");
		typeMapper.put("LONGVARCHAR", "java.lang.String,LONGVARCHAR");
		typeMapper.put("TEXT", "java.lang.String,VARCHAR");
		typeMapper.put("MEDIUMTEXT", "java.lang.String,VARCHAR");
		typeMapper.put("LONGTEXT", "java.lang.String,VARCHAR");
		typeMapper.put("BINARY", "java.lang.String,VARCHAR");
		typeMapper.put("VARBINARY", "java.lang.String,VARCHAR");
		typeMapper.put("LONGVARBINARY", "java.lang.String,VARCHAR");
		typeMapper.put("BIT", "java.lang.Boolean,BOOLEAN");
		typeMapper.put("BOOLEAN", "java.lang.Boolean,BOOLEAN");
		typeMapper.put("DATE", "java.util.Date,TIMESTAMP");
		typeMapper.put("TIME", "java.util.Date,TIMESTAMP");
		typeMapper.put("TIMESTAMP", "java.util.Date,TIMESTAMP");
		typeMapper.put("DATETIME", "java.util.Date,TIMESTAMP");

		Map<String, String> numberTypeMap = new HashMap<>();
		numberTypeMap.put("TINYINT", "java.lang.Byte,BOOLEAN");
		numberTypeMap.put("SMALLINT", "java.lang.Short,NUMERIC");
		numberTypeMap.put("INTEGER", "java.lang.Integer,NUMERIC");
		numberTypeMap.put("INT", "java.lang.Integer,NUMERIC");
		numberTypeMap.put("BIGINT", "java.lang.Long,BIGINT");
		numberTypeMap.put("REAL", "java.lang.Float,REAL");
		numberTypeMap.put("FLOAT", "java.lang.Float,FLOAT");
		numberTypeMap.put("DOUBLE", "java.lang.Double,DOUBLE");
		numberTypeMap.put("NUMERIC", "java.math.BigDecimal,NUMERIC");
		numberTypeMap.put("DECIMAL", "java.math.BigDecimal,DECIMAL");
		typeMapper.putAll(numberTypeMap);

		// add unsigned for number type
		for (Map.Entry<String, String> entry : numberTypeMap.entrySet()) {
			typeMapper.put(entry.getKey() + " UNSIGNED", entry.getValue());
		}
	}

	public static String getJavaTypeByDBType(String javatype) {
		return typeMapper.get(javatype.toUpperCase());
	}

	private static Properties pros = null;

	public PTJdbcByPropertiesUtil() {
		super();
	}

	/**
	 * 单例方式创建对象
	 * 
	 * @return
	 */
	public static PTJdbcByPropertiesUtil getInstance() {
		if (instance == null) {
			synchronized (PTJdbcByPropertiesUtil.class) {
				if (instance == null) {
					instance = new PTJdbcByPropertiesUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 读取properties文件中 数据库连接信息
	 */
	private static Properties readPropertiesFile() {
		Properties pros = new Properties();
		try {
			String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			InputStreamReader is = new InputStreamReader(new FileInputStream(path + filePath), "UTF-8");
			pros.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pros;
	}

	/**
	 * 注册驱动 静态代码块 用于启动web服务器时加载驱动
	 */
	static {
		pros = readPropertiesFile();
		String className = (String) pros.get("driver-class");
		try {
			Class.forName(className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库连接
	 *
	 * @return
	 */
	public Connection getConnection() {
		String url = (String) pros.get("driver-url");
		String user = (String) pros.get("user");
		String password = (String) pros.get("password");

		Properties props =new Properties();
		props.put("remarksReporting","true");
		props.put("user", user);
		props.put("password", password);

		Connection conn = null;
		try {
			//conn = DriverManager.getConnection(url, user, password);
			conn = DriverManager.getConnection(url, props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Properties getConfigProperties() {
		return pros;
	}

	/**
	 * 依次关闭ResultSet、Statement、Connection 若对象不存在则创建一个空对象
	 * 
	 * @param rs
	 * @param st
	 * @param conn
	 */
	public void close(ResultSet rs, Statement st, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (conn != null) {
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 新增、修改、删除、查询记录(也可以改为有结果集ResultSet返回的查询方法)
	 * 
	 * @param sql
	 * @throws
	 */
	public void execute(String sql) {
		PTJdbcByPropertiesUtil jbpu = getInstance();
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = jbpu.getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.execute();
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			ResultSet rs = null;
			jbpu.close(rs, pst, conn);
		}
	}

	/**
	 * 新增、修改、删除记录
	 * 
	 * @param sql
	 * @throws
	 */
	public void executeUpdate(String sql) {
		PTJdbcByPropertiesUtil jbpu = getInstance();
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = jbpu.getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			ResultSet rs = null;
			jbpu.close(rs, pst, conn);
		}
	}
}