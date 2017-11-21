package com.xiaoyu.tool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class TableComment {
	
	public static Map<String, String> getCommentByTableName(Connection conn, List<String> tableName) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < tableName.size(); i++) {
			String table = (String) tableName.get(i);
			ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + table);
			if (rs != null && rs.next()) {
				String create = rs.getString(2);
				String comment = parse(create);
				map.put(table.toUpperCase(), comment);
			}
			rs.close();
		}
		stmt.close();
		conn.close();
		return map;
	}

//	--MySQL数据库 show tables;  
//	--Oracle数据库（当前登录用户下的表） select uo.object_name from user_objects uo where uo.object_type = 'TABLE';  
//	--sql server 2000/2005数据库 select name from sysobjects  
//	--sybase数据库 select name from sysobjects where type='U';  
//	--DB2数据库(获取当前模式下面的所有用户表) select tabname from syscat.tables where tabschema = current schema;  
	
	public static List<String> getAllTableName(Connection conn) throws Exception {
		List<String> tables = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SHOW TABLES ");
		while (rs.next()) {
			String tableName = rs.getString(1);
			tables.add(tableName);
		}
		rs.close();
		stmt.close();
		conn.close();
		return tables;
	}

	public static String parse(String all) {
		String comment = null;
		int index = all.indexOf("COMMENT='");
		if (index < 0) {
			return "";
		}
		comment = all.substring(index + 9);
		comment = comment.substring(0, comment.length() - 1);
		return comment;
	}

	public static void main(String[] args) throws Exception {
		List<String> tables = getAllTableName(DBUtil.getConnection());
		Map<String, String> tablesComment = getCommentByTableName(DBUtil.getConnection(), tables);
		Set<String> names = tablesComment.keySet();
		Iterator<String> iter = names.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			System.out.println(name + ":" + tablesComment.get(name));
		}
	}

}
