package com.xiaoyu.util;

import com.xiaoyu.db.ColumInfo;
import com.xiaoyu.db.TableInfo;
import org.apache.velocity.tools.generic.DateTool;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.text.*;
import java.util.Calendar;

public class TableStructUtil {

	private static final String[] types = { "TABLE" };

	public static List<TableInfo> getTableList() {
		Connection connection = null;
		DatabaseMetaData dbmd = null;
		List<TableInfo> result = new ArrayList<TableInfo>();
		TableInfo tableInfo;
		try {
			JdbcByPropertiesUtil instance = JdbcByPropertiesUtil.getInstance();
			connection = instance.getConnection();
			dbmd = connection.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, "%", types);
			while (rs.next()) {
				tableInfo = new TableInfo();
				tableInfo.setTableName(rs.getString("TABLE_NAME"));
				tableInfo.setTableDesc(rs.getString("REMARKS")); //解释性的备注
				result.add(tableInfo);
			}
			rs.close();
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 获取数据表中所有列的列名、数据类型和类型名称
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static TableInfo getConnAndTableStruct(String tableName) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		DatabaseMetaData dbmd = null;
		JdbcByPropertiesUtil instance = JdbcByPropertiesUtil.getInstance();
		connection = instance.getConnection();
		Properties pros = instance.getConfigProperties();
		TableInfo tableinfo = null;
		try {
			dbmd = connection.getMetaData();
			ResultSet rst = dbmd.getPrimaryKeys(null, null, tableName);
			List<String> pkmap = new ArrayList<>();
			while (rst.next()) {
				String key = rst.getString("COLUMN_NAME");
				pkmap.add(key);
			}
			tableinfo = new TableInfo();
			tableinfo.setSince(getNewDate());
			tableinfo.setAuthor(pros.getProperty("author"));
			tableinfo.setTableName(tableName);
			tableinfo.setTableNameFt(StringUtil.nameFormat(tableName));
			tableinfo.setTableNameFtUpcase(StringUtil.firstCharUpcase(tableinfo.getTableNameFt()));
			tableinfo.setPackageName(pros.getProperty("package"));
			List<String> importpack = new ArrayList<String>();
			ResultSet columnSet = dbmd.getColumns(null, "%", tableName, "%");
			while (columnSet.next()) {
				ColumInfo col = new ColumInfo();
				String colname = columnSet.getString("COLUMN_NAME");
				col.setColumName(colname);
				col.setComment(columnSet.getString("REMARKS"));
				String dbtype = columnSet.getString("TYPE_NAME");
				String javaandjdbcType = JdbcByPropertiesUtil.getJavaTypeByDBType(dbtype);
				String[] types = javaandjdbcType.split(",");
				col.setJavaType(types[0]);
				col.setJdbcType(types[1]);
				col.setDbType(dbtype);
				col.setJavaName(StringUtil.nameFormat(colname));
				col.setJavaNameUpcase(StringUtil.firstCharUpcase(col.getJavaName()));
				col.setJavaTypeShort(StringUtil.javaShortTypeName(types[0]));
				if (pkmap.contains(colname)) {
					tableinfo.getPkColList().add(col);
				} else {
					tableinfo.getNpkColList().add(col);
				}
				if (!importpack.contains(types[0]) && !types[0].startsWith("java.lang") /* java.lang开头的不需要import */)
					importpack.add(types[0]);
			}
			tableinfo.setImportPack(importpack);
		} catch (SQLException e) {
			e.printStackTrace();
			instance.close(null, pstmt, connection);
		}
		return tableinfo;
	}

	/**
	 * 获取当前时间
	 *
	 * @return yyyy年MM月dd日 HH:mm:ss
	 */
	public static String getNewDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		return formatter.format(new Date());
	}
}
