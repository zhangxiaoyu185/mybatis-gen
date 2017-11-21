package com.xiaoyu.tool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private String url;
	private String driverClassName;
	private String username;
	private String password;
	private String fileDir; // 路径
	private String companyName; // 公司名称
	private String appName; // 业务名称
	private String tableName; // 表名称
	private String backageName; // 包名称
	private String fileName; //	页面和js包名称
	private int minIdle; // 最小空闲数
	private int maxIdle; // 最大空闲数
	private Long maxWait; // 最大等待时间
	private int maxActive; // 最大连接数
	private String validationQuery; //验证SQL
	private String testOnBorrow; //验证断开时数据是否有效
	private String testOnReturn; //验证断开时数据是否有效
	private String testWhileIdle; //验证断开时数据是否有效
	private String charset; //编码格式

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public Long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Long maxWait) {
		this.maxWait = maxWait;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public String getTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(String testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public String getTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(String testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public String getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(String testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public String getCharset(){
		return charset;
	}
	
	public String getBackageName() {
		return backageName;
	}

	public void setBackageName(String backageName) {
		this.backageName = backageName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Configuration() {
	}
	
	public Configuration(String url, String driverClassName, String username,
			String password) {
		super();
		this.url = url;
		this.driverClassName = driverClassName;
		this.username = username;
		this.password = password;
	}

	public Configuration(String url, String driverClassName, String username,
			String password, String fileDir, String companyName, String appName, 
			String tableName, String backageName, String fileName, int minIdle,
			int maxIdle, Long maxWait, int maxActive, String validationQuery,
			String testOnBorrow, String testOnReturn, String testWhileIdle, String charset) {
		super();
		this.url = url;
		this.driverClassName = driverClassName;
		this.username = username;
		this.password = password;
		this.companyName = companyName;
		this.fileDir = fileDir;
		this.appName = appName;
		this.tableName = tableName;
		this.backageName = backageName;
		this.fileName = fileName;
		this.minIdle = minIdle;
		this.maxIdle = maxIdle;
		this.maxWait = maxWait;
		this.maxActive = maxActive;
		this.validationQuery = validationQuery;
		this.testOnBorrow = testOnBorrow;
		this.testOnReturn = testOnReturn;
		this.testWhileIdle = testWhileIdle;
		this.charset = charset;
	}

	/**
	 * 默认读取配置文件Properties
	 */
	public static Configuration configure() {
		try {
			String configFile = Configuration.class.getResource("").getPath()+ "pt/ptjdbc.properties";
			if(configFile.startsWith("/"))
				configFile = configFile.substring(1, configFile.length());
			InputStream in = new FileInputStream(configFile);
			Configuration cfg = load(in);
			return cfg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;// 无法加载配置信息，返回null
		}
	}

	/**
	 * 读取配置文件Properties
	 * @param in
	 * @return
	 * @throws DocumentException
	 */
	private static Configuration load(InputStream in) {
		Properties pro = new Properties(); //pro是一个键值对对象
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//加载文件
		String url = pro.getProperty("url");
		String driverClassName = pro.getProperty("driverClassName");
		String username = pro.getProperty("username");
		String password = pro.getProperty("password");
		String appName = pro.getProperty("appName");
		String tableName = pro.getProperty("tableName").toUpperCase();
		String backageName = pro.getProperty("backageName");
		String charset = pro.getProperty("charset");
		String fileName = pro.getProperty("fileName");
		String fileDir = pro.getProperty("fileDir");
		String companyName = pro.getProperty("companyName");
		Integer minIdle = Integer.parseInt(pro.getProperty("minIdle"));
		Integer maxIdle = Integer.parseInt(pro.getProperty("maxIdle"));
		Integer maxActive = Integer.parseInt(pro.getProperty("maxActive"));
		Long maxWait = Long.parseLong(pro.getProperty("maxWait"));
		String validationQuery = pro.getProperty("validationQuery");
		String testOnBorrow = pro.getProperty("testOnBorrow");
		String testOnReturn = pro.getProperty("testOnReturn");
		String testWhileIdle = pro.getProperty("testWhileIdle");

		Configuration cfg = new Configuration(url, driverClassName, username, password, 
				fileDir, companyName, appName, tableName, backageName, fileName, minIdle, 
				maxIdle, maxWait, maxActive, validationQuery, testOnBorrow, testOnReturn, 
				testWhileIdle, charset);
		return cfg;
	}
	
}