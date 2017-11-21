package com.xiaoyu.db;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {

	public static final String GENERATED_KEY_NAME = "ID";
	
	private String tableName; // 表名

	private String tableNameFt; // 格式化后表名

	private String tableNameFtUpcase; // 格式化后表名(第一个字母大写)

	private List<String> importPack = new ArrayList<String>(); // import数据类型

	private String packageName; // 包名

	private List<ColumInfo> pkColList = new ArrayList<ColumInfo>(); // 主键

	private List<ColumInfo> npkColList = new ArrayList<ColumInfo>(); // 非主键

	private ColumInfo uuidColum; // UUID列,有才存在

	private String author; // 作者

	private String since; // 生成日期

	private String tableDesc; // 表描述

	private String fileDir; // 路径

	private String fileName; //	页面和js包名称

	private String charset; //编码格式

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableNameFt() {
		return tableNameFt;
	}

	public void setTableNameFt(String tableNameFt) {
		this.tableNameFt = tableNameFt;
	}

	public List<String> getImportPack() {
		return importPack;
	}

	public void setImportPack(List<String> importPack) {
		this.importPack = importPack;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<ColumInfo> getPkColList() {
		return pkColList;
	}

	public void setPkColList(List<ColumInfo> pkColList) {
		this.pkColList = pkColList;
	}

	public List<ColumInfo> getNpkColList() {
		return npkColList;
	}

	public void setNpkColList(List<ColumInfo> npkColList) {
		this.npkColList = npkColList;
	}

	public boolean getShouldUseGeneratedKeys() {
		return pkColList.size() == 1 && GENERATED_KEY_NAME.equalsIgnoreCase(pkColList.get(0).getColumName());
	}

	public String getTableNameFtUpcase() {
		return tableNameFtUpcase;
	}

	public void setTableNameFtUpcase(String tableNameFtUpcase) {
		this.tableNameFtUpcase = tableNameFtUpcase;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSince() {
		return since;
	}

	public void setSince(String since) {
		this.since = since;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public ColumInfo getUuidColum() {
		return uuidColum;
	}

	public void setUuidColum(ColumInfo uuidColum) {
		this.uuidColum = uuidColum;
	}
}
