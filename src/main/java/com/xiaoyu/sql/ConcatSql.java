package com.xiaoyu.sql;

import java.util.List;
import java.util.Map;

public class ConcatSql {

	public static void concatMysql(Map<String, Object> map){
		String strCreateSql = "create table "+map.get("tableName")+"("+"\n";
		String strAlter = "\n";
		String strCreateTrigger = "\n";
		String[][] strList = (String[][]) map.get("data");
		for(int i = 0;i<strList.length;i++){
			if(strList[i][0] == null) {
				continue;
			}
			for(int j = 0;j<strList[i].length;j++){
				if(j==0 || j==1){
					strCreateSql += "\t"+strList[i][j];
				}
				if(j==2){
					if(!"".equals(strList[i][j])) {
						if(!"int".equals(strList[i][1].toLowerCase()) && !"bigInt".equals(strList[i][1].toLowerCase())) {
							if("double".equals(strList[i][1].toLowerCase())) {
								//String[] str = strList[i][j].trim().split(",");
								strCreateSql += "("+strList[i][j].trim()+")";
							}else {
								strCreateSql += "("+strList[i][j]+")";
							}
						}						
					}
						
				}
				if(j==3){
					if(!"".equals(strList[i][j])){
						String[] ysList = strList[i][j].split(",");
						for(int y = 0; y < ysList.length; y++){
							if("[DF]".equals(ysList[y].substring(0, 4))){
								strCreateSql += "\tdefault\t"+ysList[y].substring(5, ysList[y].length());
							}
							if("[AK]".equals(ysList[y])){
								strCreateSql += "\tauto_increment";
							}
							if("[PK]".equals(ysList[y])){
								strCreateSql += "\tprimary key";	
							}
							if("[UK]".equals(ysList[y])){
								strCreateSql += "\tunique";	
							}
							if("[NN]".equals(ysList[y])){
								strCreateSql += "\tnot null";
							}
						}
					}
				}
				if(j==4){
					strCreateSql += "\tcomment '" + strList[i][j]+"'";
				}
			}
			if(i==strList.length-1)
				strCreateSql += "\n";
			else 
				strCreateSql += ",\n";
		}
		strCreateSql += ") auto_increment=1 comment '"+map.get("name")+"';\n"+strAlter+"\n"+strCreateTrigger;
		System.out.println(strCreateSql);
	}
	
	public static void concatOracle(Map<String, Object> map){
		String strCreateSql = "create table "+map.get("tableName")+"("+"\n";
		String strCreateComment = "\ncomment on table "+map.get("tableName")+" is '"+map.get("name")+"';";
		String strCreateTrigger = "\n";
		String[][] strList = (String[][]) map.get("data");
		for(int i = 0;i<strList.length;i++){
			if(strList[i][0] == null) {
				continue;
			}
			for(int j = 0;j<strList[i].length;j++){
				if(j==0){
					strCreateSql += "\t"+strList[i][j];
				}
				if(j==1){
					if ("int".equalsIgnoreCase(strList[i][j]) || "double".equalsIgnoreCase(strList[i][j]) || "bigInt".equalsIgnoreCase(strList[i][j])) {
						strList[i][j] = "NUMBER";
					}
					if ("varchar".equalsIgnoreCase(strList[i][j])) {
						strList[i][j] = "VARCHAR2";
					}
					if ("datetime".equalsIgnoreCase(strList[i][j])) {
						strList[i][j] = "DATE";
					}
					if ("text".equalsIgnoreCase(strList[i][j])) {
						strList[i][j] = "CLOB";
					}
					strCreateSql += "\t"+strList[i][j];
				}
				if(j==2){				
					if(!"".equals(strList[i][j]))
						strCreateSql += "("+strList[i][j]+")";	
				}
				if(j==3){
					if(!"".equals(strList[i][j])){
						String[] ysList = strList[i][j].split(",");
						for(int y = 0; y < ysList.length; y++){
							if("[DF]".equals(ysList[y].substring(0, 4))){
								strCreateSql += "\tdefault\t"+ysList[y].substring(5, ysList[y].length());
							}
							if("[AK]".equals(ysList[y])){//生成unid触发器
								strCreateTrigger += "\ncreate or replace trigger "+map.get("tableName")+"_trigger\nbefore insert on "+
								map.get("tableName")+" for each row\nbegin\n\tselect XTPT_SEQUENCE.nextval into :NEW."+
								strList[i][0]+" from dual;\nend;\n/";
							}
							if("[PK]".equals(ysList[y])){
								strCreateSql += "\tprimary key";	
							}
							if("[UK]".equals(ysList[y])){
								strCreateSql += "\tunique";	
							}
							if("[NN]".equals(ysList[y])){
								strCreateSql += "\tnot null";
							}
						}
					}
				}
				if(j==4){
					strCreateComment += "\nCOMMENT ON COLUMN " + map.get("tableName") +"."+ strList[i][0] + " IS '" + strList[i][j]+"';";
				}
			}
			if(i==strList.length-1)
				strCreateSql += "\n";
			else 
				strCreateSql += ",\n";
		}
		strCreateSql += ");\n"+strCreateComment+"\n"+strCreateTrigger;
		System.out.println(strCreateSql);
	}
	
	public static void concatSqlServer(Map<String, Object> map){
		String strCreateSql = "create table "+map.get("tableName")+"("+"\n";
		String strCreateComment = "\nEXECUTE sp_addextendedproperty N'MS_Description', N'"+map.get("name")+"', N'user', N'dbo', N'table', N'"+map.get("tableName")+"', NULL, NULL;";
		String strCreateTrigger = "\n";
		String[][] strList = (String[][]) map.get("data");
		for(int i = 0;i<strList.length;i++){
			if(strList[i][0] == null) {
				continue;
			}
			for(int j = 0;j<strList[i].length;j++){				
				if(j==0){
					String[] nameList = strList[i][j].split("_");
					String strname = nameList[nameList.length-1];
					if("CDATE".equals(strname)){//生成cdate默认值
						strCreateTrigger += "\nEXEC sp_bindefault N'[dbo].[DF_GetDate]', N'["+ map.get("tableName")+"].["+strList[i][j]+"]';\ngo\n";
					}
					if("UDATE".equals(strname)){//生成udate触发器
						strCreateTrigger += "\nEXEC sp_bindefault N'[dbo].[DF_GetDate]', N'["+ map.get("tableName")+"].["+strList[i][j]+"]';\ngo\n";
						strCreateTrigger += "\ncreate trigger "+map.get("tableName")+"_udate on "+map.get("tableName")+"\nafter update\nas\nupdate "+
						map.get("tableName")+" set "+strList[i][j]+"=GETDATE()\n" +"from "+map.get("tableName")+",inserted\nwhere "+map.get("tableName")+"."+strList[i][j]+"=inserted."+strList[i][j]+"\ngo\n";
					}
					strCreateSql += "\t"+strList[i][j];
				}
				if(j==1){
					strCreateSql += "\t"+strList[i][j];
				}
				if(j==2){
					if(!"".equals(strList[i][j])) {
						if(!"int".equals(strList[i][1].toLowerCase()) && !"bigInt".equals(strList[i][1].toLowerCase())) {
							if("double".equals(strList[i][1].toLowerCase())) {
								String[] str = strList[i][j].trim().split(",");
								strCreateSql += "("+str[0]+")";
							}else {
								strCreateSql += "("+strList[i][j]+")";
							}
						}						
					}
				}
				if(j==3){
					if(!"".equals(strList[i][j])){
						String[] ysList = strList[i][j].split(",");
						for(int y = 0; y < ysList.length; y++){
							if("[DF]".equals(ysList[y].substring(0, 4))){
								strCreateSql += "\tdefault\t"+ysList[y].substring(5, ysList[y].length());
							}
							if("[AK]".equals(ysList[y])){
								strCreateSql += "\tidentity(1,1)";
							}
							if("[PK]".equals(ysList[y])){
								strCreateSql += "\tprimary key";	
							}
							if("[UK]".equals(ysList[y])){
								strCreateSql += "\tunique";	
							}
							if("[NN]".equals(ysList[y])){
								strCreateSql += "\tnot null";
							}
						}
					}
				}
				if(j==4){
					strCreateComment += "\nexec sp_addextendedproperty N'MS_Description', N'" + strList[i][j] + "', N'user', N'dbo', N'table', N'"+map.get("tableName") +"', N'column', N'"+ strList[i][0] + "';";
				}
			}
			if(i==strList.length-1)
				strCreateSql += "\n";
			else 
				strCreateSql += ",\n";
		}
		strCreateSql += ");\n";
		strCreateSql += strCreateComment+"\ngo\n";
		strCreateSql += strCreateTrigger;
		System.out.println(strCreateSql);
	}
	
	public static void main(String[] args) {
		List<Map<String, Object>> listMap = WordUtilByPoi.readWord("D://develop/1.doc");
		for(Map<String, Object> map : listMap){
			//oracle
			//创建全局序列和触发器
//			String strGlobal = "CREATE SEQUENCE xtpt_sequence INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE;"+"\n";
//			strGlobal += "CREATE OR REPLACE FUNCTION GET_DATE RETURN DATE\nIS\n\tyyyy\tvarchar2(36);\n\tmm\tvarchar2(36);" +
//					"\n\tdd\tvarchar2(36);\n\ttempdate\tvarchar2(36);\nBEGIN\ntempdate := '';\n"+
//					"select to_char(to_date(sysdate),'YYYY') into yyyy from dual;\n"+
//					"select to_char(to_date(sysdate),'MM') into mm from dual;\n"+
//					"select to_char(to_date(sysdate),'DD') into dd from dual;\n"+
//					"tempdate := substr(yyyy,1,4)|| '-'||substr(mm,1,2)|| '-'||substr(dd,1,2);\n"+
//					"return to_date(tempdate,'yyyy-mm-dd');\nEND;";
			//System.out.println(strGlobal);
			//concatOracle(map);
			//SQL Server
			//创建全局默认值
			//String strGlobal1 = "create default [DF_GetDate] as getdate(); \ngo\n";
			//System.out.println(strGlobal1);
			//concatSqlServer(map);
			//MySql
			concatMysql(map);
		}
		
		//存储过程
		/**
		     SET ANSI_NULLS OFF
		     GO
		     SET QUOTED_IDENTIFIER OFF
		     GO
		     CREATE PROCEDURE [dbo].[P_GridPage]
		     (
		     	@SQL nVARCHAR(4000),
		     	@Page int,
		     	@RecsPerPage int,
		     	@ID VARCHAR(255),
		     	@Sort VARCHAR(255)
		     )
		     AS
		     DECLARE @Str nVARCHAR(4000)
		     SET @Str='SELECT  TOP '+CAST(@RecsPerPage AS VARCHAR(20))+' * FROM ('+@SQL+') T WHERE T.'+@ID+' NOT IN 
		     (SELECT  TOP '+CAST((@RecsPerPage*(@Page-1)) AS VARCHAR(20))+' '+@ID+' FROM ('+@SQL+') T9 ORDER BY '+@Sort+') ORDER BY '+@Sort
		     PRINT @Str
		     EXEC sp_ExecuteSql @Str
		     GO
		 **/
	}
}