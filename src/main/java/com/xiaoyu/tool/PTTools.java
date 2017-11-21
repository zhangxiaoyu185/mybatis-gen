package com.xiaoyu.tool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PTTools {

	/**
	 * 获取数据库中所有表的表名
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getTableNameList(Connection conn) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		// 如果使用%，则表示需要访问的是数据库下的所有表
		ResultSet rs = dbmd.getTables(null, null, "%", new String[] { "TABLE" });
		List<Map<String, String>> tableNameList = new ArrayList<Map<String, String>>();
		Map<String, String> tableMap = null;
		while (rs.next()) {
			tableMap = new HashMap<String, String>();
			tableMap.put("TABLE_NAME", rs.getString("TABLE_NAME")); //表的名称
			tableMap.put("TABLE_CAT", rs.getString("TABLE_CAT")); //表所在的编目(可能为空)
			tableMap.put("TABLE_SCHEM", rs.getString("TABLE_SCHEM")); //表所在的模式(可能为空) 
			//表的类型(典型的有 "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL  TEMPORARY", "ALIAS", "SYNONYM")
			tableMap.put("TABLE_TYPE", rs.getString("TABLE_TYPE")); 
			tableMap.put("REMARKS", rs.getString("REMARKS")); //解释性的备注
			tableNameList.add(tableMap);
		}
		if(rs!=null) rs.close();
		return tableNameList;
	}

	/**
	 * 获取数据表中所有列的列名、数据类型和类型名称
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> getColumnNameList(Connection conn, String tableName) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getColumns(null, dbmd.getUserName(), tableName, "%");
		List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> columnMap;
		while (rs.next()) {
			String smallModelName = "";
			String bigModelName = "";
			columnMap = new HashMap<String, Object>();
			// 转换字段名
			String columnName = rs.getString("COLUMN_NAME");
			bigModelName = columnName.toUpperCase();
			while (columnName.indexOf('_') != -1) {
				int y = columnName.indexOf('_');
				if (!"".equals(smallModelName))
					smallModelName += columnName.substring(0, 1) + columnName.substring(1, y).toLowerCase();
				else
					smallModelName += columnName.substring(0, y).toLowerCase();
				columnName = columnName.substring(y + 1, columnName.length());
			}
			smallModelName = smallModelName + columnName.substring(0, 1) + columnName.substring(1, columnName.length()).toLowerCase();

			// 转化成类型
			String typeName = rs.getString("TYPE_NAME").toLowerCase();
			String smallModelType = "";
			String bigModelType = "";
			if ("varchar2".equalsIgnoreCase(typeName) || "varchar".equalsIgnoreCase(typeName) || "text".equalsIgnoreCase(typeName) || "blob".equalsIgnoreCase(typeName)) {
				smallModelType = "String";
				bigModelType = "VARCHAR";
			}
			if ("bigInt".equalsIgnoreCase(typeName)) {
				smallModelType = "long";
				bigModelType = "NUMERIC";
			}
			if ("number".equalsIgnoreCase(typeName) || "int".equalsIgnoreCase(typeName)) {
				smallModelType = "int";
				bigModelType = "NUMERIC";
			}
			if ("double".equalsIgnoreCase(typeName)) {
				smallModelType = "double";
				bigModelType = "NUMERIC";
			}
			if ("date".equalsIgnoreCase(typeName) || "datetime".equalsIgnoreCase(typeName)) {
				smallModelType = "Date";
				bigModelType = "TIMESTAMP";
			}

			columnMap.put("REMARKS", rs.getString("REMARKS"));
			columnMap.put("DATA_TYPE", rs.getString("DATA_TYPE"));
			columnMap.put("SMALL_COLUMN_NAME", smallModelName);
			columnMap.put("BIG_COLUMN_NAME", bigModelName);
			columnMap.put("SMALL_TYPE_NAME", smallModelType);
			columnMap.put("BIG_TYPE_NAME", bigModelType);
			columnList.add(columnMap);
		}
		return columnList;
	}
	
	/**
	 * 获取数据表中的主键(联合主键),暂不用
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> getPrimaryKeys(Connection conn, String tableName) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();		
		ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName);
		List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> columnMap = null;
		while(rs.next()) {
			String smallModelName = "";
			String bigModelName = "";
			columnMap = new HashMap<String, Object>();
			// 转换字段名
			String columnName = rs.getString("COLUMN_NAME");
			bigModelName = columnName.toUpperCase();
			while (columnName.indexOf('_') != -1) {
				int y = columnName.indexOf('_');
				if (!"".equals(smallModelName))
					smallModelName += columnName.substring(0, 1) + columnName.substring(1, y).toLowerCase();
				else
					smallModelName += columnName.substring(0, y).toLowerCase();
				columnName = columnName.substring(y + 1, columnName.length());
			}
			smallModelName = smallModelName + columnName.substring(0, 1) + columnName.substring(1, columnName.length()).toLowerCase();

			columnMap.put("SMALL_COLUMN_NAME", smallModelName);
			columnMap.put("BIG_COLUMN_NAME", bigModelName);
			columnMap.put("TABLE_CAT", rs.getObject("TABLE_CAT")+"");
			columnMap.put("TABLE_NAME", rs.getObject("TABLE_NAME")+"");
			columnMap.put("KEY_SEQ", rs.getObject("KEY_SEQ")+"");
			columnMap.put("PK_NAME", rs.getObject("PK_NAME")+"");
			columnList.add(columnMap);
		}
		return columnList;
	}
	
	/**
	 * 获取数据表中的主键(单个主键)
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, Object> getPrimaryKeysByOne(Connection conn, String tableName) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();		
		ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName);
		Map<String, Object> columnMap = null;
		while(rs.next()) {
			String smallModelName = "";
			String bigModelName = "";
			columnMap = new HashMap<String, Object>();
			// 转换字段名
			String columnName = rs.getString("COLUMN_NAME");
			bigModelName = columnName.toUpperCase();
			while (columnName.indexOf('_') != -1) {
				int y = columnName.indexOf('_');
				if (!"".equals(smallModelName))
					smallModelName += columnName.substring(0, 1) + columnName.substring(1, y).toLowerCase();
				else
					smallModelName += columnName.substring(0, y).toLowerCase();
				columnName = columnName.substring(y + 1, columnName.length());
			}
			smallModelName = smallModelName + columnName.substring(0, 1) + columnName.substring(1, columnName.length()).toLowerCase();

			columnMap.put("SMALL_COLUMN_NAME", smallModelName);
			columnMap.put("BIG_COLUMN_NAME", bigModelName);
			columnMap.put("TABLE_CAT", rs.getObject("TABLE_CAT")+"");
			columnMap.put("TABLE_NAME", rs.getObject("TABLE_NAME")+"");
			columnMap.put("KEY_SEQ", rs.getObject("KEY_SEQ")+"");
			String pkName = rs.getObject("PK_NAME").toString();
			columnMap.put("PK_NAME", pkName);
			if (!("PRIMARY").equals(pkName)) {
				return columnMap;
			}
		}
		return columnMap;
	}
	
	public static void main(String[] args) throws SQLException, Exception{		
		Configuration con = Configuration.configure();
		String tableNames = con.getTableName();
		String[] list = tableNames.split(",");
//		List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), list[0]); // 得到列名集合
//		for (Map<String, Object> map : columnList) {
//			String columnName = map.get("SMALL_COLUMN_NAME").toString();
//			String columnType = map.get("SMALL_TYPE_NAME").toString();
//			String columnRemarks = map.get("Remarks").toString();
//			System.out.println(columnName+"     "+columnType+"      "+columnRemarks);
//		}
		List<Map<String, Object>> columnList = PTTools.getPrimaryKeys(DBUtil.getConnection(), list[0]); // 得到列名集合
		for (Map<String, Object> map : columnList) {
			String b = map.get("SMALL_COLUMN_NAME").toString();
			String a = map.get("BIG_COLUMN_NAME").toString();
			String c = map.get("TABLE_CAT").toString();
			String d = map.get("TABLE_NAME").toString();
			String e = map.get("KEY_SEQ").toString();
			String f = map.get("PK_NAME").toString();
			System.out.println(a+"     "+b+"      "+c+"      "+d+"      "+e+"      "+f);
		}
	}
	
}