package com.xiaoyu;

import com.xiaoyu.creator.*;
import com.xiaoyu.db.TableInfo;
import com.xiaoyu.util.FileIOUtil;
import com.xiaoyu.util.JdbcByPropertiesUtil;
import com.xiaoyu.util.TableStructUtil;
import com.mysql.jdbc.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class GeneratorMain {

	public static void main(String args[]) throws InstantiationException, IllegalAccessException {
		List<TableInfo> tableInfos = new ArrayList<>();
		String tablename = (String) JdbcByPropertiesUtil.getInstance().getConfigProperties().get("tableName");
		if (StringUtils.isNullOrEmpty(tablename)) {
			List<TableInfo> tables = TableStructUtil.getTableList();
			for (TableInfo table : tables)
				tableInfos.add(generateFiles(table.getTableName()));
		} else {
			List<String> tables = StringUtils.split(tablename, ",", true);
			for (String t : tables) {
				if (!StringUtils.isNullOrEmpty(t)) {
					tableInfos.add(generateFiles(t));
				}
			}
		}

		System.out.println("generating mapper list file...");
		try {
			Writer writer = FileIOUtil.getWriter("sql_map_service_config.xml", "");
			for (TableInfo tableInfo : tableInfos) {
				writer.write("<typeAlias type=\"" + tableInfo.getPackageName() + ".entity." + tableInfo.getTableNameFtUpcase() + "\" alias=\"" + tableInfo.getTableNameFtUpcase() + "\" />\n");
			}
			writer.write("\n");
			for (TableInfo tableInfo : tableInfos) {
				writer.write("<mapper resource=\"" + tableInfo.getPackageName().replace(".", "/") + "/" + tableInfo.getTableNameFtUpcase() + "Mapper.xml\" />\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("end...");
	}

	public static TableInfo generateFiles(String tablename) {
		TableInfo tableinfo = TableStructUtil.getConnAndTableStruct(tablename);
		Class[] clzzs = {
			MapperCreator.class,
			DomainCreator.class,
			ServiceIfaceCreator.class,
			ServiceImplCreator.class,
			VoCreator.class
		};
		for (Class clzz : clzzs) {
			AbstractCreator exc;
			try {
				exc = (AbstractCreator) clzz.newInstance();
				exc.createJavaBean(tableinfo);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return tableinfo;
	}
}
