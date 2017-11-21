package com.xiaoyu;

import com.mysql.jdbc.StringUtils;
import com.xiaoyu.db.TableInfo;
import com.xiaoyu.pt.*;
import com.xiaoyu.util.FileIOUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PTGeneratorMain {

	public static void main(String args[]) throws InstantiationException, IllegalAccessException {
		List<TableInfo> tableInfos = new ArrayList<>();
		String tablename = (String) PTJdbcByPropertiesUtil.getInstance().getConfigProperties().get("tableName");
		if (StringUtils.isNullOrEmpty(tablename)) {
			List<TableInfo> tables = PTTableStructUtil.getTableList();
			for (TableInfo table : tables)
				tableInfos.add(generateFiles(table.getTableName(), table.getTableDesc(), "common"));
		} else {
			List<String> tables = StringUtils.split(tablename, ",", true);
			String tableDesc = (String) PTJdbcByPropertiesUtil.getInstance().getConfigProperties().get("tableDesc");
			List<String> descs = StringUtils.split(tableDesc, ",", true);
			String fileName = (String) PTJdbcByPropertiesUtil.getInstance().getConfigProperties().get("fileName");
			List<String> fileNames = StringUtils.split(fileName, ",", true);
			if(tables.size() != descs.size() || tables.size() != fileNames.size()) {
				System.out.println("表名备注和表名长度和页面路径不匹配");
				return;
			}
			for (int i = 0; i < tables.size(); i++) {
				if (!StringUtils.isNullOrEmpty(tables.get(i))) {
					tableInfos.add(generateFiles(tables.get(i), descs.get(i), fileNames.get(i)));
				}
			}
		}

		System.out.println("generating mapper list file...");
		try {
			Writer writer = FileIOUtil.ptGetWriter("sql_map_service_config.xml", "");
			for (TableInfo tableInfo : tableInfos) {
				writer.write("<typeAlias type=\"" + tableInfo.getPackageName() + ".entity." + tableInfo.getTableNameFtUpcase() + "\" alias=\"" + tableInfo.getTableNameFtUpcase() + "\" />\n");
			}
			writer.write("\n");
			for (TableInfo tableInfo : tableInfos) {
				writer.write("<mapper resource=\"" + tableInfo.getPackageName().replace(".", "/") + "/mapper/" + tableInfo.getTableNameFtUpcase() + "Mapper.xml\" />\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("end...");
	}

	public static TableInfo generateFiles(String tablename, String tableDesc, String fileName) {
		TableInfo tableinfo = PTTableStructUtil.getConnAndTableStruct(tablename, tableDesc, fileName);
		Class[] clzzs = {
			PTMapperCreator.class,
			PTDomainCreator.class,
			PTVoCreator.class,
			PTControllerCreator.class,
			PTServiceCreator.class,
			PTServiceImplCreator.class
		};
		for (Class clzz : clzzs) {
			PTAbstractCreator exc;
			try {
				exc = (PTAbstractCreator) clzz.newInstance();
				exc.createJavaBean(tableinfo);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		Class[] clzzsHtml = {
			HtmlListCreator.class,
			HtmlAddCreator.class,
			HtmlModifyCreator.class,
			HtmlDetailCreator.class
		};
		for (Class clzz : clzzsHtml) {
			PTAbstractCreator exc;
			try {
				exc = (PTAbstractCreator) clzz.newInstance();
				exc.createHtml(tableinfo);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		Class[] clzzsJs = {
			JsListCreator.class,
			JsAddCreator.class,
			JsModifyCreator.class,
			JsDetailCreator.class
		};
		for (Class clzz : clzzsJs) {
			PTAbstractCreator exc;
			try {
				exc = (PTAbstractCreator) clzz.newInstance();
				exc.createJs(tableinfo);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return tableinfo;
	}
}
