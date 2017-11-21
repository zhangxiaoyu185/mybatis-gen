package com.xiaoyu.db;

public class ColumInfo {

	private String columName; // 列名

	private String jdbcType; // jdbc类型

	private String javaName; // java字段名

	private String javaNameUpcase; // java字段名(第一个字母大写)

	private String javaType; // java类型

	private String javaTypeShort; // java类型简称

	private String dbType; // 数据库类型

	private String comment; // 注释

	public String getColumName() {
		return columName;
	}

	public void setColumName(String columName) {
		this.columName = columName;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getJavaTypeShort() {
		return javaTypeShort;
	}

	public void setJavaTypeShort(String javaTypeShort) {
		this.javaTypeShort = javaTypeShort;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getJavaNameUpcase() {
		return javaNameUpcase;
	}

	public void setJavaNameUpcase(String javaNameUpcase) {
		this.javaNameUpcase = javaNameUpcase;
	}
}
