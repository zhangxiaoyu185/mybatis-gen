package com.xiaoyu.tool;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateFile {

	Configuration con = Configuration.configure();

	/**
	 * 获取基础包头
	 * @return
	 */
	public String getBasePackage() {
		String basePackage = "com." + con.getCompanyName() + "." + con.getAppName();
		return basePackage;
	}
	
	/**
	 * 得到包名
	 * @return
	 */
	public String getPackageName(String type, String backageName) {
		String packageName = "com/" + con.getCompanyName() + "/" + con.getAppName() + "/" + type;
		if(!("").equals(backageName)) {
			packageName += "/" + backageName;
		}
		return packageName;
	}

	/**
	 * 得到类名
	 * @return
	 */
	public String getClassName(String type, String tableName) {
		String[] strList = tableName.split("_");
		StringBuffer className = new StringBuffer();
		StringBuffer fullClassName = new StringBuffer();
		className.append(strList[0].toLowerCase());
		for (int i = 1; i < strList.length; i++) {
			className.append(strList[i].substring(0, 1).toUpperCase()).append(strList[i].substring(1, strList[i].length()).toLowerCase());
		}
		if ("service".equals(type)) {
			fullClassName.append(className).append("Service");
		}
		if ("service/impl".equals(type)) {
			fullClassName.append(className).append("ServiceImpl");
		}
		if ("controller".equals(type)) {
			fullClassName.append(className).append("Controller");
		}
		if ("mapper".equals(type)) {
			fullClassName.append(className).append("Mapper");
		}
		if ("entity".equals(type)) {
			fullClassName.append(className);
		}
		if ("vo".equals(type)) {
			fullClassName.append(className).append("VO");
		}
		//.html.js
		if("add".equals(type) || "detail".equals(type) || "modify".equals(type) || "".equals(type)){
			fullClassName.append(className.toString().toLowerCase());
			return fullClassName.toString();
		}
		return toUpperCaseFirstOne(fullClassName.toString());
	}

	/**
	 * 首字母转大写
	 * @param s
	 * @return
	 */
    public static String toUpperCaseFirstOne(String s) {
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
	/**
	 * 首字母转小写
	 * @param s
	 * @return
	 */
    public static String toLowerCaseFirstOne(String s) {
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
	/**
	 * 得到路径
	 * @param type service/mapper等
	 * @param Filetype 文件的格式java/xml/html/js
	 * @param tableName 支持多表
	 * @param backageName 支持多表
	 * @param fileName 支持多表      
	 * @return
	 */
	public String getPath(String type, String Filetype, String tableName, String backageName, String fileName) {
		String path="";
		String typeList = (type!=""?("_"+type):(""));
		if (".html".equals(Filetype)) {
			path = con.getFileDir()+"/webapp/views/common/"+ fileName + "/" + getClassName(type, tableName) + typeList + Filetype;
		} else if (".js".equals(Filetype)) {
			path = con.getFileDir()+"/webapp/views/common/js/"+ fileName + "/" + getClassName(type, tableName) + typeList + Filetype;
		} else {
			path = con.getFileDir() + "/java/" + getPackageName(type, backageName) + "/" + getClassName(type, tableName) + Filetype;
		}
		path = path.replaceAll("\\\\", "/");
		return path;
	}

	/**
	 * 创建文件
	 * @param dirFile
	 */
	public void createFile(File dirFile) {
		File dir = dirFile.getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		try {
			dirFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件或文件夹是否存在
	 * @param path
	 */
	public static boolean isExist(String path) {
		File file = new File(path);
		return file.exists();
	}
	 
	/**
	 * 读取文件定制内容
	 * @param dirFile
	 * @param charset
	 * @param type 读取文件类型
	 * @return 返回定制内容
	 */
	public String ReadFile(File dirFile, String charset, String type) {
		StringBuffer content = new StringBuffer();
		StringBuffer myContent = new StringBuffer();
		String strContent = "";
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(dirFile), charset);
			char[] buffer = new char[1024];
			int len = 0;
			while ((len = reader.read(buffer)) != -1) {// 读取文件内容
				String str = new String(buffer, 0, len);
				content.append(str);
			}
			if ("xml".equals(type)) {
				int start = content.indexOf("<!-- 定制内容开始 -->") + "<!-- 定制内容开始 -->".length();
				int end = content.indexOf("<!-- 定制内容结束 -->");
				if (start > 0 && end > start)
					strContent = content.substring(start, end);
			} else {
				String regx = ".*定制内容开始=.*\n((.*\n)+?).*=定制内容结束=.*";
				Pattern p = Pattern.compile(regx);
				Matcher macher = p.matcher(content);
				while (macher.find()) {
					myContent.append(macher.group(1));
				}
				strContent = myContent.toString();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strContent;
	}

	/**
	 * 生成 Entity
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param backageName 支持多表
	 * @param tableRemarks 表注释
	 */
	public void writeEntityFile(File dirFile, String charset, String tableName, String backageName, String tableRemarks) {
		String myContent = ReadFile(dirFile, charset, "java"); // 读取自己的定制内容
		StringBuffer entity = new StringBuffer(); // 总文件
		StringBuffer head = new StringBuffer(); // 头文件		
		StringBuffer columnDefine = new StringBuffer(); // 字段的声明
		StringBuffer columnSetGet = new StringBuffer(); // 字段的set get方法
		StringBuffer constructor = new StringBuffer(); // 构造方法
		String columnName = ""; // 字段的名称
		String columnType = ""; // 字段的类型
		String remarks = ""; // 字段的注释
		boolean flag = true; //是否已导date包
		try {
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName); // 得到列名集合
			String packageName = getPackageName("entity", backageName);
			packageName = packageName.replaceAll("/", ".");
			head.append("package " + packageName).append(";\n\n"); // 包名
			head.append("import ").append(getBasePackage()).append(".entity.BaseEntity;\n"); // 导包
			
			columnDefine.append("\tprivate static final long serialVersionUID = 1L;\n"); // 版本控制
			
			for (Map<String, Object> map : columnList) {
				columnName = map.get("SMALL_COLUMN_NAME").toString();				
				columnType = map.get("SMALL_TYPE_NAME").toString();
				if(("int").equals(columnType)) {
					columnType = "Integer";
				}
				if(("long").equals(columnType)) {
					columnType = "Long";
				}
				if(("double").equals(columnType)) {
					columnType = "Double";
				}
				remarks = map.get("REMARKS").toString();
				if (flag && "Date".equals(columnType)) {
					head.append("import java.util.Date;\n"); // 导包
					flag = false;
				}
				columnDefine.append("\n\t/**\n\t*").append(remarks).append("\n\t*/\n").append("\tprivate ").append(columnType).append(" ").append(columnName).append(";\n"); // 声明变量
				String setgetName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
				columnSetGet.append("\tpublic void set").append(setgetName).append("(").append(columnType).append(" ").append(columnName).append(") {\n").append("\t\tthis.")
						.append(columnName).append(" = ").append(columnName).append(";\n\t}\n\n"); // 组装set方法
				columnSetGet.append("\tpublic ").append(columnType).append(" get").append(setgetName).append("( ) {\n\t\t").append("return ").append(columnName).append(";\n\t}\n\n"); // 组装get方法
			}

			constructor.append("\tpublic ").append(getClassName("entity", tableName))
					.append("( ) { \n").append("\t}\n");// 默认构造器

			entity.append(head).append("\n").append("/**\n* "+ tableRemarks +"\n*/\n").append("public class ").append(getClassName("entity", tableName)).append(
					" extends BaseEntity").append(" {\n\n").append(columnDefine).append("\n").append(columnSetGet)
					.append(constructor).append("\n").append("//<=================定制内容开始==============\n")
					.append(myContent).append("//==================定制内容结束==============>\n")
					.append("\n}");

			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(entity.toString());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成VO
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param backageName 支持多表
	 * @param tableRemarks 表注释  
	 */
	public void writeVOFile(File dirFile, String charset, String tableName, String backageName, String tableRemarks) {
		String myContent = ReadFile(dirFile, charset, "java"); // 读取自己的定制内容
		StringBuffer vo = new StringBuffer(); // 总文件
		StringBuffer head = new StringBuffer(); // 头文件		
		StringBuffer columnDefine = new StringBuffer(); // 字段的声明
		StringBuffer columnSetGet = new StringBuffer(); // 字段的set get方法
		StringBuffer constructor = new StringBuffer(); // 构造方法
		StringBuffer convertPOToVO = new StringBuffer(); // convertPOToVO方法
		String columnName = ""; // 字段的名称
		String columnType = ""; // 字段的类型
		String remarks = ""; // 字段的注释
		boolean flag = true; //是否已导date包
		try {
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName); // 得到列名集合
			String packageName = getPackageName("vo", backageName);
			packageName = packageName.replaceAll("/", ".");
			head.append("package " + packageName).append(";\n\n"); // 包名
			head.append("import ").append(getBasePackage()).append(".entity.").append(getClassName("entity", tableName)).append(";\n"); // 导包
			head.append("import ").append(getBasePackage()).append(".vo.BaseVO;\n"); // 导包
			
			convertPOToVO.append("\n\t@Override\n\tpublic void convertPOToVO(Object poObj) {\n\t\t")
			.append("if (null == poObj) {\n\t\t\treturn;\n\t\t}\n\n\t\t").append(getClassName("entity", tableName))
			.append(" po = (").append(getClassName("entity", tableName)).append(") poObj;\n");
			
			for (Map<String, Object> map : columnList) {
				columnName = map.get("SMALL_COLUMN_NAME").toString();				
				columnType = map.get("SMALL_TYPE_NAME").toString();
				if(("int").equals(columnType)) {
					columnType = "Integer";
				}
				if(("long").equals(columnType)) {
					columnType = "Long";
				}
				if(("double").equals(columnType)) {
					columnType = "Double";
				}
				remarks = map.get("REMARKS").toString();
				if (flag && "Date".equals(columnType)) {
					head.append("import ").append(getBasePackage()).append(".tool.DateUtil;\n"); // 导包
					flag = false;
				}
				if(!columnName.toUpperCase().endsWith("UNID") && !columnType.equals("Date")) {
					columnDefine.append("\n\t/**\n\t*").append(remarks).append("\n\t*/\n").append("\tprivate ").append(columnType).append(" ").append(columnName).append(";\n"); // 声明变量
					String setgetName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
					columnSetGet.append("\tpublic void set").append(setgetName).append("(").append(columnType).append(" ").append(columnName).append(") {\n").append("\t\tthis.")
							.append(columnName).append(" = ").append(columnName).append(";\n\t}\n\n"); // 组装set方法
					columnSetGet.append("\tpublic ").append(columnType).append(" get").append(setgetName).append("( ) {\n\t\t").append("return ").append(columnName).append(";\n\t}\n\n"); // 组装get方法
					convertPOToVO.append("\t\tthis.").append(columnName).append(" = po.get").append(setgetName).append("();\n");
					continue;
				}
				if(columnType.equals("Date")) {
					columnType = "String";
					columnDefine.append("\n\t/**\n\t*").append(remarks).append("\n\t*/\n").append("\tprivate ").append(columnType).append(" ").append(columnName).append(";\n"); // 声明变量
					String setgetName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
					columnSetGet.append("\tpublic void set").append(setgetName).append("(").append(columnType).append(" ").append(columnName).append(") {\n").append("\t\tthis.")
							.append(columnName).append(" = ").append(columnName).append(";\n\t}\n\n"); // 组装set方法
					columnSetGet.append("\tpublic ").append(columnType).append(" get").append(setgetName).append("( ) {\n\t\t").append("return ").append(columnName).append(";\n\t}\n\n"); // 组装get方法
					convertPOToVO.append("\t\tthis.").append(columnName).append(" = po.get").append(setgetName).append("()!=null?DateUtil.formatDefaultDate(po.get").append(setgetName).append("()):\"\";\n");
				}
			}

			constructor.append("\tpublic ").append(getClassName("vo", tableName))
					.append("( ) { \n").append("\t}\n");// 默认构造器

			vo.append(head).append("\n").append("/**\n* "+ tableRemarks +"\n*/\n").append("public class ").append(getClassName("vo", tableName)).append(
					" implements BaseVO").append(" {\n").append(columnDefine).append("\n").append(columnSetGet)
					.append(constructor).append(convertPOToVO).append("\t}\n")
					.append("//<=================定制内容开始==============\n")
					.append(myContent).append("//==================定制内容结束==============>\n")
					.append("\n}");

			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(vo.toString());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成Service接口
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param backageName 支持多表
	 * @param tableRemarks 表注释   
	 */
	public void writeServiceIFile(File dirFile, String charset, String tableName, String backageName, String tableRemarks) {
		String myContent = ReadFile(dirFile, charset, "java");// 读取定制内容
		String packageName = getPackageName("service", backageName);// 获取包名
		packageName = packageName.replaceAll("/", ".");
		String entityPackageName = getPackageName("entity", backageName);// 获取entity包名
		entityPackageName = entityPackageName.replaceAll("/", ".");
		String className = getClassName("service", tableName);// 获取service类名
		String entityName = getClassName("entity", tableName);// 获取entity类名
		String smallClassName = toLowerCaseFirstOne(entityName);
		StringBuffer serviceI = new StringBuffer();
		StringBuffer insert = new StringBuffer(); //添加
		StringBuffer update = new StringBuffer(); //修改
		StringBuffer delete = new StringBuffer(); //删除
		StringBuffer deleteBatch = new StringBuffer(); //批量删除
		StringBuffer get = new StringBuffer(); //查询查询单个
		StringBuffer getMethodList = new StringBuffer(); //查询所有(list)
		StringBuffer getMethodPage = new StringBuffer(); //查询所有(page)
		
		serviceI.append("package ").append(packageName).append(";\n\n"); // 包名
		serviceI.append("import ").append(entityPackageName).append(".").append(entityName).append(";\n");// 导包
		serviceI.append("import java.util.List;\n");// 导包
		serviceI.append("import ").append(getBasePackage()).append(".core.mybatis.page.Page;\n\n");// 导包
		serviceI.append("/**\n* "+ tableRemarks +"\n*/\n");
		serviceI.append("public interface ").append(className).append(" {\n\n");// 类的开始
		
		insert.append("\t/**\n\t* 添加\n\t* @param ").append(smallClassName).append("\n\t* @return\n\t*/\n\tpublic boolean insert")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(");\n\n");
		update.append("\t/**\n\t* 修改\n\t* @param ").append(smallClassName).append("\n\t* @return\n\t*/\n\tpublic boolean update")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(");\n\n");
		delete.append("\t/**\n\t* 删除\n\t* @param ").append(smallClassName).append("\n\t* @return\n\t*/\n\tpublic boolean delete")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(");\n\n");
		deleteBatch.append("\t/**\n\t* 批量删除\n\t* @param list\n\t* @return boolean\n\t*/\n\tpublic boolean deleteBatchByIds(List<String> list);\n\n");
		get.append("\t/**\n\t* 查询\n\t* @param ").append(smallClassName).append("\n\t* @return\n\t*/\n\tpublic ").append(entityName).append(" get")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(");\n\n");
		getMethodList.append("\t/**\n\t* 查询所有List").append("\n\t* @return List\n\t*/\n\tpublic List<").append(entityName+"> ").append("find"+entityName+"List();\n\n");
		getMethodPage.append("\t/**\n\t* 查询所有Page").append("\n\t* @return Page\n\t*/\n\tpublic Page<").append(entityName+"> ").append("find"+entityName+"Page(int pageNum, int pageSize);\n\n");
		serviceI.append(insert).append(update).append(delete).append(deleteBatch).append(get).append(getMethodList).append(getMethodPage);
		serviceI.append("//<=================定制内容开始==============\n").append(
				myContent).append(
				"//==================定制内容结束==============>\n\n");// 定制内容
		serviceI.append("}");

		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(serviceI.toString());
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成Service实现类
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param backageName 支持多表 
	 * @param tableRemarks 表注释  
	 */
	public void writeServiceFile(File dirFile, String charset, String tableName, String backageName, String tableRemarks) {
		String myContent = ReadFile(dirFile, charset, "java");// 读取定制内容
		String packageName = getPackageName("service/impl", backageName);// 获取包名
		packageName = packageName.replaceAll("/", ".");
		String entityPackageName = getPackageName("entity", backageName);// 获取entity包名
		entityPackageName = entityPackageName.replaceAll("/", ".");
		String iPackageName = getPackageName("service", backageName);// 获取接口包路径
		iPackageName = iPackageName.replaceAll("/", ".");
		String iClassName = getClassName("service", tableName);
		String className = getClassName("service/impl", tableName);// 获取类名
		String entityName = getClassName("entity", tableName);// 获取entity类名
		String smallClassName = toLowerCaseFirstOne(entityName);
		StringBuffer service = new StringBuffer();
		StringBuffer insert = new StringBuffer(); //添加
		StringBuffer update = new StringBuffer(); //修改
		StringBuffer delete = new StringBuffer(); //删除
		StringBuffer deleteBatch = new StringBuffer(); //删除
		StringBuffer get = new StringBuffer(); //查询查询单个
		StringBuffer getMethodList = new StringBuffer(); //查询所有(list)
		StringBuffer getMethodPage = new StringBuffer(); //查询所有(page)
		
		service.append("package ").append(packageName).append(";\n\n"); // 包名
		service.append("import java.util.Map;\n");// 导包
		service.append("import java.util.List;\n");// 导包
		service.append("import java.util.HashMap;\n");// 导包
		service.append("import ").append(getBasePackage()).append(".core.mybatis.page.Page;\n");// 导包
		service.append("import ").append(getBasePackage()).append(".core.mybatis.page.PageRequest;\n");// 导包
		service.append("import ").append(iPackageName).append(".").append(
				iClassName).append(";\n").append("import org.springframework.beans.factory.annotation.Autowired;\n")
				.append("import org.springframework.stereotype.Service;\n").append("import ").append(getBasePackage()).append(".core.mybatis.dao.MyBatisDAO;\n")
				.append("import ").append(entityPackageName).append(".").append(
						getClassName("entity", tableName)).append(";\n\n");// 导包;
		service.append("/**\n* "+ tableRemarks +"\n*/\n");
		service.append("@Service(\"").append(toLowerCaseFirstOne(iClassName)).append("\")\n");
		service.append("public class ").append(className).append(" implements ").append(iClassName).append(
						" {\n\n");// 类的开始
		service.append("\t@Autowired\n\tprivate MyBatisDAO myBatisDAO;\n");
		
		insert.append("\n\t@Override\n\tpublic boolean insert")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(") {\n")
		.append("\t\tmyBatisDAO.insert(").append(smallClassName).append(");\n\t\treturn true;\n\t}\n");
		update.append("\n\t@Override\n\tpublic boolean update")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(") {\n")
		.append("\t\tmyBatisDAO.update(").append(smallClassName).append(");\n\t\treturn true;\n\t}\n");
		delete.append("\n\t@Override\n\tpublic boolean delete")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(") {\n")
		.append("\t\tmyBatisDAO.delete(").append(smallClassName).append(");\n\t\treturn true;\n\t}\n");
		
		deleteBatch.append("\n\tprivate static final String DELETEBATCH_"+entityName.toUpperCase()+"_BY_IDS=\"deleteBatch"+entityName+"ByIds\";\n\n\t@Override\n\tpublic boolean deleteBatchByIds")
		.append("(List<String> list ").append(") {\n").append("\t\tMap<String, Object> hashMap = new HashMap<String, Object>();\n")
		.append("\t\thashMap.put(\"list\", list);\n")
		.append("\t\tmyBatisDAO.delete(").append("DELETEBATCH_"+entityName.toUpperCase()+"_BY_IDS").append(",hashMap);\n\t\treturn true;\n\t}\n");
		
		get.append("\n\t@Override\n\tpublic ").append(entityName).append(" get")
		.append(entityName).append("(").append(entityName).append(" ").append(smallClassName).append(") {\n")
		.append("\t\treturn (").append(entityName).append(") myBatisDAO.findForObject(").append(smallClassName).append(");\n\t}\n\n");
		
		getMethodList.append("\tprivate static final String FIND_"+entityName.toUpperCase()+"_FOR_LISTS=\"find"+entityName+"ForLists\";\n").append("\n\t@SuppressWarnings(\"unchecked\")\n\t@Override\n\tpublic List<").append(entityName).append("> find"+entityName+"List() {\n")
		.append("\t\treturn myBatisDAO.findForList(FIND_"+entityName.toUpperCase()+"_FOR_LISTS, null);\n\t}\n");		
		
		getMethodPage.append("\n\tprivate static final String FIND_"+entityName.toUpperCase()+"_FOR_PAGES=\"find"+entityName+"ForPages\";\n").append("\n\t@SuppressWarnings(\"unchecked\")\n\t@Override\n\tpublic Page<").append(entityName).append("> find"+entityName+"Page(int pageNum, int pageSize) {\n")
		.append("\t\tMap<String, Object> hashMap = new HashMap<String, Object>();\n")
		.append("\t\treturn myBatisDAO.findForPage(FIND_"+entityName.toUpperCase()+"_FOR_PAGES, new PageRequest(pageNum, pageSize, hashMap));\n\t}\n\n");
		
		service.append(insert).append(update).append(delete).append(deleteBatch).append(get).append(getMethodList).append(getMethodPage);
		service.append("//<=================定制内容开始==============\n").append(
				myContent).append(
				"//==================定制内容结束==============>\n\n");// 定制内容
		service.append("}");

		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(service.toString());
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成Mapper
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param backageName 支持多表  
	 */
	public void writeMapperFile(File dirFile, String charset, String tableName, String backageName) {
		String packageName = getPackageName("entity", backageName);// 得到包路径
		packageName = packageName.replaceAll("/", ".");
		String entityName=getClassName("entity", tableName);
		String smallClassName = toLowerCaseFirstOne(getClassName("entity", tableName)); //小写类名
		String smallColumnName = ""; // 小写字段的名称
		String bigColumnName = ""; // 大写字段的名称
		String bigColumnType = ""; // 大写字段的类型
		String remarks = ""; // 字段的注释
		String smallUuid = "";// 获取小写uuid的全名
		String bigUuid = "";// 获取大写uuid的全名
		String typeUuid = "";// 获取大写uuid的类型
		String myContent = ReadFile(dirFile, charset, "xml");// 读取定制内容
		StringBuffer resultMap = new StringBuffer();// 生成resultMap的内容
		StringBuffer fieldAsProperty = new StringBuffer();// 生成id为fieldAsProperty的sql语句
		StringBuffer fieldIf = new StringBuffer();// 生成id为fieldIf的sql语句
		StringBuffer fieldIfUUID = new StringBuffer();// fieldIf的uuid语句
		StringBuffer property = new StringBuffer();// 生成id为property的sql语句
		StringBuffer propertyUUID = new StringBuffer();// property的uuid语句
		StringBuffer fieldPropertyIf = new StringBuffer();// 生成id为fieldPropertyIf的sql语句
		StringBuffer fieldPropertyIfUUID = new StringBuffer();// fieldPropertyIf的uuid语句
		StringBuffer insertEntity = new StringBuffer(); // 生成id为insertEntity的insert语句
		StringBuffer deleteEntity = new StringBuffer(); // 生成id为deleteEntity的delete语句
		StringBuffer getEntity = new StringBuffer(); // 生成id为getEntity的select语句
		StringBuffer updateEntity = new StringBuffer();// 生成id为updateEntity的update语句
		StringBuffer deleteBatchEntity = new StringBuffer(); //删除
		StringBuffer getMethodListEntity = new StringBuffer(); //查询所有(list)
		StringBuffer getMethodPageEntity = new StringBuffer(); //查询所有(page)
		boolean boolUUID=true; //是否有UUID
		StringBuffer mapper = new StringBuffer();
		mapper.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		mapper.append("<mapper namespace=\"").append(packageName).append(".").append(getClassName("entity", tableName)).append("\">\n\t");// mapper 开始
		resultMap.append("<resultMap id=\"").append(smallClassName).append("Mapper\" type=\"").append(getClassName("entity", tableName)).append("\">\n");// resultMap 开始
		fieldAsProperty.append("\t<sql id=\"").append(smallClassName).append("AsProperty\">\n");// sql id为fieldAsProperty开始
		fieldIf.append("\t<sql id=\"").append(smallClassName).append("FieldIf\">\n");// sql id为field开始
		property.append("\t<sql id=\"").append(smallClassName).append("Property\">\n");// sql id为property开始		
		fieldPropertyIf.append("\t<sql id=\"").append(smallClassName).append("PropertyIf\">\n");// sql id为fieldPropertyIf开始
		
		try {
			Map<String, Object> pkMap = PTTools.getPrimaryKeysByOne(DBUtil.getConnection(), tableName);//得到表主键
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(
					DBUtil.getConnection(), tableName); //得到全部列名
			for (Map<String, Object> map : columnList) { //检测是否有UUID
				smallColumnName = map.get("SMALL_COLUMN_NAME").toString();
				bigColumnName = map.get("BIG_COLUMN_NAME").toString();
				bigColumnType = map.get("BIG_TYPE_NAME").toString();
				if(bigColumnName.endsWith("UUID") && boolUUID){
					boolUUID=false;
					smallUuid = smallColumnName;// 获取小写uuid的全名
					bigUuid = bigColumnName;// 获取大写uuid的全名
					typeUuid = bigColumnType;// 获取大写uuid的类型
				}
			}
			if (boolUUID) { //UUID不存在,使用主键
				for (Map<String, Object> map : columnList) { //检测是否有UUID
					smallColumnName = map.get("SMALL_COLUMN_NAME").toString();
					bigColumnName = map.get("BIG_COLUMN_NAME").toString();
					bigColumnType = map.get("BIG_TYPE_NAME").toString();
					if(bigColumnName.equals(pkMap.get("BIG_COLUMN_NAME").toString())){
						smallUuid = smallColumnName;// 获取小写uuid的全名
						bigUuid = bigColumnName;// 获取大写uuid的全名
						typeUuid = bigColumnType;// 获取大写uuid的类型
					}
				}
			}
			for (Map<String, Object> map : columnList) {
				smallColumnName = map.get("SMALL_COLUMN_NAME").toString();
				bigColumnName = map.get("BIG_COLUMN_NAME").toString();
				bigColumnType = map.get("BIG_TYPE_NAME").toString();
				remarks = map.get("REMARKS").toString()!=null?map.get("REMARKS").toString():"";
				fieldAsProperty.append("\t\t").append(bigColumnName).append(
						" AS ").append(smallColumnName).append(",\n");
				
				if (bigColumnName.equals(bigUuid)) {
					fieldPropertyIfUUID.append("\t\t").append("<if test=\"").append(
							smallColumnName).append("!=null\" >").append(
							bigColumnName).append("=#{").append(smallColumnName)
							.append(",jdbcType=").append(bigColumnType).append(
									"},</if>\n");
					fieldIfUUID.append("\t\t").append("<if test=\"").append(
							smallColumnName).append("!=null\" >").append(bigColumnName).append(",</if>\n");
					propertyUUID.append("\t\t").append("<if test=\"").append(
							smallColumnName).append("!=null\" >").append("#{").append(smallColumnName).append(
							",jdbcType=").append(bigColumnType).append("},</if>\n");
				}else {
					fieldPropertyIf.append("\t\t").append("<if test=\"").append(
							smallColumnName).append("!=null\" >").append(
							bigColumnName).append("=#{").append(smallColumnName)
							.append(",jdbcType=").append(bigColumnType).append(
									"},</if>\n");
					fieldIf.append("\t\t").append("<if test=\"").append(
							smallColumnName).append("!=null\" >").append(bigColumnName).append(",</if>\n");
					property.append("\t\t").append("<if test=\"").append(
							smallColumnName).append("!=null\" >").append("#{").append(smallColumnName).append(
							",jdbcType=").append(bigColumnType).append("},</if>\n");
				}				
				if (boolUUID) {
					if (bigColumnName.equals(bigUuid)) {
						resultMap.append("\t\t<id column=\"").append(bigColumnName)
								.append("\" jdbcType=\"").append(bigColumnType)
								.append("\" property=\"").append(smallColumnName)
								.append("\" /><!-- ").append(remarks).append(
										" -->\n");					
						continue;
					}
				}
				if (!boolUUID) {
					if (bigColumnName.endsWith("UNID")) {
						resultMap.append("\t\t<id column=\"").append(bigColumnName)
								.append("\" jdbcType=\"").append(bigColumnType)
								.append("\" property=\"").append(smallColumnName)
								.append("\" /><!-- ").append(remarks).append(
										" -->\n");					
						continue;
					}
				}
				resultMap.append("\t\t<result column=\"").append(bigColumnName)
					.append("\" jdbcType=\"").append(bigColumnType).append(
						"\" property=\"").append(smallColumnName)
						.append("\" /><!-- ").append(remarks).append(" -->\n");
												
			}
			
			insertEntity.append("\t<insert id=\"insert").append(getClassName("entity", tableName)).append("\" parameterType=\"").append(getClassName("entity", tableName)).append("\" useGeneratedKeys=\"true\" keyProperty=\"id\" >\n");// sql id为insertEntity开始
			insertEntity.append("\t\tINSERT INTO ").append(tableName).append("(<include refid=\"").append(smallClassName).append("FieldIf\" />) VALUES (<include refid=\"").append(smallClassName).append("Property\" />)\n\t</insert>\n");// sql id为insertEntity结束
			deleteEntity.append("\t<delete id=\"delete").append(getClassName("entity", tableName)).append("\" parameterType=\"").append(getClassName("entity", tableName)).append("\">\n");// sql id为deleteEntity开始
			deleteEntity.append("\t\tDELETE FROM ").append(tableName).append(" <where> ").append(bigUuid).append("=#{").append(smallUuid).append(",jdbcType=").append(typeUuid).append("} </where>\n\t</delete>\n");// sql id为deleteEntity结束
			
			deleteBatchEntity.append("\t<!--  批量删除-->\n\t<delete id=\"deleteBatch"+entityName+"ByIds\" parameterType=\"HashMap\">\n");
			deleteBatchEntity.append("\t\tDELETE FROM ").append(tableName+"\n").append("\t\t<where>\n").append("\t\t\t<if test=\"list != null and list.size>0\">\n");
			deleteBatchEntity.append("\t\t\t\t"+bigUuid+" in\n");
			deleteBatchEntity.append("\t\t\t\t<foreach item=\"item\" index=\"id\" collection=\"list\" open=\"(\" separator=\",\" close=\")\">\n");
			deleteBatchEntity.append("\t\t\t\t\t\'${item}\'\n\t\t\t\t</foreach>\n\t\t\t</if>\n\t\t</where>\n\t</delete>\n");
			
			getEntity.append("\t<select id=\"get").append(getClassName("entity", tableName)).append("\" resultMap=\"").append(smallClassName).append("Mapper\"").append(" parameterType=\"").append(getClassName("entity", tableName)).append("\">\n");// sql id为getEntity开始
			getEntity.append("\t\tSELECT <include refid=\"").append(smallClassName).append("AsProperty\" /> FROM ").append(tableName).append(" <where> ").append(bigUuid).append("=#{").append(smallUuid).append(",jdbcType=").append(typeUuid).append("} </where>\n\t</select>\n");// sql																																		// id为getEntity结束
			updateEntity.append("\t<update id=\"update").append(getClassName("entity", tableName)).append("\" parameterType=\"").append(getClassName("entity", tableName)).append("\">\n");// sql id为updateEntity开始
			updateEntity.append("\t\tUPDATE ").append(tableName).append("<set><include refid=\"").append(smallClassName).append("PropertyIf\" /></set>").append(" <where> ").append(bigUuid).append("=#{").append(smallUuid).append(",jdbcType=").append(typeUuid).append("} </where>\n\t</update>\n");// sql id为updateEntity结束
			
			getMethodListEntity.append("\t<!--  获取所有list-->\n\t<select id=\"find"+entityName+"ForLists\" resultMap=\"").append(smallClassName).append("Mapper\" ").append("parameterType=\"HashMap\">\n");
			getMethodListEntity.append("\t\tSELECT <include refid=\"").append(smallClassName).append("AsProperty\" /> \n\t\tFROM ").append(tableName+"\n\t</select>\n");
			getMethodPageEntity.append("\t<!--  获取所有page-->\n\t<select id=\"find"+entityName+"ForPages\" resultMap=\"").append(smallClassName).append("Mapper\" ").append("parameterType=\"HashMap\">\n");
			getMethodPageEntity.append("\t\tSELECT <include refid=\"").append(smallClassName).append("AsProperty\" /> \n\t\tFROM ").append(tableName+"\n\t\t<where>\n\t\t\t1=1\n\t\t</where>\n\t</select>\n");
			
			resultMap.append("\t</resultMap>\n");// resultMap结束
			fieldAsProperty.delete(fieldAsProperty.length() - 2,
					fieldAsProperty.length() - 1);// 去除fieldAsProperty最后字段的逗号
			fieldAsProperty.append("\t</sql>\n");// sql fieldAsProperty结束
			fieldIf.append(fieldIfUUID).delete(fieldIf.length() - 7, fieldIf.length() - 6);// 去除field最后字段的逗号
			fieldIf.append("\t</sql>\n");// sql field结束
			property.append(propertyUUID).delete(property.length() - 7, property.length() - 6);// 去除property最后字段的逗号
			property.append("\t</sql>\n");// sql property结束
			fieldPropertyIf.append(fieldPropertyIfUUID).delete(fieldPropertyIf.length() - 7, fieldPropertyIf.length() - 6);// 去除fieldEqPropertyIf最后字段的逗号
			fieldPropertyIf.append("\t</sql>\n");// sql fieldEqPropertyIf结束
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		// 得到列名集合
		mapper.append(resultMap).append(fieldAsProperty).append(fieldIf).append(
				property).append(fieldPropertyIf).append(insertEntity).append(deleteEntity)
				.append(getEntity).append(updateEntity).append(deleteBatchEntity).append(getMethodListEntity).append(getMethodPageEntity)
				.append("<!-- 定制内容开始 -->")
				.append(myContent).append("<!-- 定制内容结束 -->\n\n");// 定制内容
		mapper.append("</mapper>");
		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(mapper.toString());
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成Controller
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param backageName 支持多表
	 * @param tableRemarks 表注释  
	 */
	public void writeControllerFile(File dirFile, String charset, String tableName, String backageName, String tableRemarks) {
		String myContent = ReadFile(dirFile, charset, "java");// 读取定制内容
		String packageName = getPackageName("controller", backageName).replaceAll("/", ".");// 获取包名
		String entityPackageName = getPackageName("entity", backageName).replaceAll("/", ".");// 获取entity包名
		String entityClassName=getClassName("entity", tableName);// 获取entity类名
		String servicePackageName = getPackageName("service", backageName).replaceAll("/", ".");// 获取service包名
		String serviceCName = getClassName("service", tableName);// 获取service类名
		String vOPackageName = getPackageName("vo", backageName).replaceAll("/", ".");//获取vo包名
		String vOCName = getClassName("vo", tableName);// 获取vo类名
		String className = getClassName("controller", tableName);// 获取类名
		StringBuffer controller = new StringBuffer();//汇总文件
		String setGetName = ""; //每列get/set(首字母大写的)
		String setGetUUIDName = ""; //UUID列get/set(首字母大写的)
		String uuidName = "";
		String uuidType = "";
		String uuidremarks = "";
		boolean boolUUID=true; //是否有UUID
		
		StringBuffer insertMethod = new StringBuffer(); //添加
		StringBuffer updateMethod = new StringBuffer(); //修改
		StringBuffer deleteMethod = new StringBuffer(); //删除
		StringBuffer deleteBatch = new StringBuffer(); //删除
		StringBuffer viewMethod = new StringBuffer(); //查询单个
		StringBuffer getMethodList = new StringBuffer(); //查询所有(list)
		StringBuffer getMethodPage = new StringBuffer(); //查询所有(page)
		
		controller.append("package ").append(packageName).append(";\n\n"); // 包名
		controller.append("import java.util.List;\n");
		controller.append("import java.util.ArrayList;\n");
		controller.append("import ").append(getBasePackage()).append(".tool.DateUtil;\n");
		controller.append("import ").append(getBasePackage()).append(".tool.RandomUtil;\n");
		controller.append("import ").append(getBasePackage()).append(".tool.StringUtil;\n");
		controller.append("import javax.servlet.http.HttpServletResponse;\n");
		controller.append("import ").append(getBasePackage()).append(".core.mybatis.page.Page;\n");
		controller.append("import org.springframework.stereotype.Controller;\n");
		controller.append("import ").append(getBasePackage()).append(".tool.out.ResultMessageBuilder;\n");
		controller.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
		controller.append("import org.springframework.beans.factory.annotation.Autowired;\n");
		controller.append("import org.springframework.web.bind.annotation.RequestMethod;\n");
		controller.append("import ").append(getBasePackage()).append(".controller.BaseController;\n")// 导包
				.append("import ").append(entityPackageName).append(".").append(entityClassName).append(";\n");// 导包;	
		controller.append("import ").append(servicePackageName).append(".").append(serviceCName).append(";\n");//引入service
		controller.append("import ").append(vOPackageName).append(".").append(vOCName).append(";\n\n");//引入Vo	
		controller.append("@Controller\n@RequestMapping(value=\"/").append(toLowerCaseFirstOne(entityClassName)).append("\")\n");
		controller.append("public class ").append(className).append(" extends BaseController {\n\n");// 类的开始
		controller.append("\t/**\n\t* "+ tableRemarks +"\n\t*/\n\t")
					.append("@Autowired\n").append("\tprivate ").append(serviceCName).append(" "+toLowerCaseFirstOne(serviceCName)).append(";\n\t");
		try {
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			StringBuffer noteBuffer=new StringBuffer();
			StringBuffer paramBuffer=new StringBuffer();
			StringBuffer setBuffer=new StringBuffer();
			
			insertMethod.append("\n\t/**\n\t* 添加\n\t*");
			for (Map<String, Object> map : columnList) { //检测是否有UUID
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String columnType = map.get("SMALL_TYPE_NAME").toString();
				String remarks = map.get("REMARKS").toString();
				setGetName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
				if(columnName.toUpperCase().endsWith("UUID") && boolUUID){
					boolUUID=false;
					uuidName = columnName;
					uuidType = columnType;
					uuidremarks = remarks;
					setGetUUIDName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
				}
			}
			Map<String, Object> pkMap = PTTools.getPrimaryKeysByOne(DBUtil.getConnection(), tableName);//得到表主键
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String columnType = map.get("SMALL_TYPE_NAME").toString();
				String bigColumnName = map.get("BIG_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS").toString();
				boolean boolDate=false;
				if(("int").equals(columnType)) {
					columnType = "Integer";
				}
				if(("long").equals(columnType)) {
					columnType = "Long";
				}
				if(("double").equals(columnType)) {
					columnType = "Double";
				}
				if(columnType.equals("Date")) {
					columnType = "String";
					boolDate=true;
				}
				if(columnType.equals("DateTime")) {
					columnType = "String";
					boolDate=true;
				}
				if (boolUUID && bigColumnName.equals(pkMap.get("BIG_COLUMN_NAME"))) { //没有UUID,就获取主键
					uuidName = columnName;
					uuidType = columnType;
					uuidremarks = remarks;
					setGetUUIDName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
					continue;
				}
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && (!columnName.toUpperCase().endsWith("UUID"))){//将入参set对象里面
					setGetName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
					if(boolDate){//set==>DATE类型
						noteBuffer.append("\n\t* @param "+columnName+" ").append(remarks);//入参注释拼接
						paramBuffer.append(columnType).append(" "+columnName).append(", ");//方法入参拼接
						if(columnName.toUpperCase().endsWith("CDATE")||columnName.toUpperCase().endsWith("UDATE")){
							setBuffer.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetName).append("(new Date());\n");
						} else {
							setBuffer.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetName).append("(DateUtil.parseDefaultDate(").append(columnName+"));\n");
						}
					}else{
						noteBuffer.append("\n\t* @param "+columnName+" ").append(remarks);//入参注释拼接
						paramBuffer.append(columnType).append(" "+columnName).append(", ");//方法入参拼接
						setBuffer.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetName).append("(").append(columnName+");\n");
					}
				}
			}
			
			insertMethod.append(noteBuffer);
			insertMethod.append("\n\t* @return\n\t*/\n\t");
			insertMethod.append("@RequestMapping(value=\"/add/"+ toLowerCaseFirstOne(entityClassName) +"\", method=RequestMethod.POST)").append("\n\t");
			insertMethod.append("public void add").append(entityClassName+" (");
			insertMethod.append(paramBuffer);
			insertMethod.append("HttpServletResponse response) {").append("\n");
			insertMethod.append("\t\tlogger.info(\"["+className+"]:begin add"+ entityClassName +"\"").append(");\n\r");
			insertMethod.append("\t\t"+entityClassName).append(" ").append(toLowerCaseFirstOne(entityClassName)).append(" = ").append("new "+entityClassName+"()").append(";\n");
			if(!boolUUID){//有UUID
				insertMethod.append("\t\tString uuid = RandomUtil.generateString(16);\n");
				insertMethod.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetUUIDName).append("(").append("uuid);\n");
			}
			insertMethod.append(setBuffer);
			insertMethod.append("\n\t\t"+toLowerCaseFirstOne(serviceCName)).append(".insert").append(entityClassName).append("(").append(toLowerCaseFirstOne(entityClassName)).append(");\n");
			insertMethod.append("\n\t\t").append("writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"新增成功!\"),response);");
			insertMethod.append("\n\t\tlogger.info(\"["+className+"]:end add"+ entityClassName +"\"").append(");\n");
			insertMethod.append("\n\t}\n");
			
			updateMethod.append("\n\t/**\n\t* 修改\n\t*");
			updateMethod.append("\n\t* @param "+ uuidName +" ").append(uuidremarks);//uuid注释拼接
			updateMethod.append(noteBuffer);
			updateMethod.append("\n\t* @return\n\t*/\n\t");
			updateMethod.append("@RequestMapping(value=\"/update/"+ toLowerCaseFirstOne(entityClassName) +"\", method=RequestMethod.POST)").append("\n\t");
			updateMethod.append("public void update").append(entityClassName+" (");
			updateMethod.append(uuidType).append(" "+uuidName).append(", ");//uuid方法拼接
			updateMethod.append(paramBuffer);
			updateMethod.append("HttpServletResponse response) {").append("\n");
			updateMethod.append("\t\tlogger.info(\"["+className+"]:begin update"+ entityClassName +"\"").append(");\n\r");
			
			if(!boolUUID){//有UUID
				updateMethod.append("\t\tif (StringUtil.isEmpty(").append(uuidName).append(")) {\n");
				updateMethod.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"["+uuidremarks+"]不能为空!\"), response);\n");
				updateMethod.append("\t\t\treturn;\n\t\t}\n\r");
			}else{//id
				updateMethod.append("\t\tif (").append(uuidName+" == null || ").append(uuidName+" == 0").append(") {\n");
				updateMethod.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"["+uuidremarks+"]不能为空!\"), response);\n");
				updateMethod.append("\t\t\treturn;\n\t\t}\n\r");
			}
			
			updateMethod.append("\t\t"+entityClassName).append(" ").append(toLowerCaseFirstOne(entityClassName)).append(" = ").append("new "+entityClassName+"()").append(";\n");
			updateMethod.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetUUIDName).append("(").append(uuidName+");\n");
			updateMethod.append(setBuffer);
			updateMethod.append("\n\t\t"+toLowerCaseFirstOne(serviceCName)).append(".update").append(entityClassName).append("(").append(toLowerCaseFirstOne(entityClassName)).append(");\n");
			updateMethod.append("\n\t\t").append("writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"修改成功!\"),response);");
			updateMethod.append("\n\t\tlogger.info(\"["+className+"]:end update"+ entityClassName +"\"").append(");\n");
			updateMethod.append("\n\t}\n");		
			
			deleteMethod.append("\n\t/**\n\t* 删除\n\t*");
			deleteMethod.append("\n\t* @param "+ uuidName +" ").append(uuidremarks);//uuid注释拼接
			deleteMethod.append("\n\t* @return\n\t*/\n\t");
			deleteMethod.append("@RequestMapping(value=\"/delete/one\", method=RequestMethod.POST)").append("\n\t");
			deleteMethod.append("public void delete").append(entityClassName+" (");
			deleteMethod.append(uuidType).append(" "+uuidName).append(", ");//uuid方法拼接
			deleteMethod.append("HttpServletResponse response) {").append("\n");
			deleteMethod.append("\t\tlogger.info(\"["+className+"]:begin delete"+ entityClassName +"\"").append(");\n\r");
			if(!boolUUID){//有UUID
				deleteMethod.append("\t\tif (StringUtil.isEmpty(").append(uuidName).append(")) {\n");
				deleteMethod.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"["+uuidremarks+"]不能为空!\"), response);\n");
				deleteMethod.append("\t\t\treturn;\n\t\t}\n\r");
			}else{//id
				deleteMethod.append("\t\tif (").append(uuidName+" == null || ").append(uuidName+" == 0").append(") {\n");
				deleteMethod.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"["+uuidremarks+"]不能为空!\"), response);\n");
				deleteMethod.append("\t\t\treturn;\n\t\t}\n\r");
			}
			deleteMethod.append("\t\t"+entityClassName).append(" ").append(toLowerCaseFirstOne(entityClassName)).append(" = ").append("new "+entityClassName+"()").append(";\n");
			deleteMethod.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetUUIDName).append("(").append(uuidName+");\n");
			deleteMethod.append("\n\t\t"+toLowerCaseFirstOne(serviceCName)).append(".delete").append(entityClassName).append("(").append(toLowerCaseFirstOne(entityClassName)).append(");\n");
			deleteMethod.append("\n\t\t").append("writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"删除成功!\"),response);");
			deleteMethod.append("\n\t\tlogger.info(\"["+className+"]:end delete"+ entityClassName +"\"").append(");\n");
			deleteMethod.append("\n\t}\n");
			
		//	deleteBatch
			deleteBatch.append("\n\t/**\n\t* 批量删除\n\t*");
			deleteBatch.append("\n\t* @param "+ uuidName +"s ").append("UUID集合\n");//uuid注释拼接
			deleteBatch.append("\t* @return\n\t*/\n\t");
			deleteBatch.append("@RequestMapping(value=\"/delete/batch\", method=RequestMethod.POST)").append("\n\t");
			deleteBatch.append("public void deleteBatch").append(entityClassName+" (");
			deleteBatch.append("String "+uuidName).append("s, ");//uuid方法拼接
			deleteBatch.append("HttpServletResponse response) {").append("\n");
			deleteBatch.append("\t\tlogger.info(\"["+className+"]:begin deleteBatch"+ entityClassName +"\"").append(");\n\r");
			deleteBatch.append("\t\tif (StringUtil.isEmpty(").append(uuidName).append("s)) {\n");
			deleteBatch.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"[UUID集合]不能为空!\"), response);\n");
			deleteBatch.append("\t\t\treturn;\n\t\t}\n\r");
			deleteBatch.append("\t\tString[] uuids="+uuidName+"s.split(\"\\\\|\");\n");
			deleteBatch.append("\t\tList<String> list = new ArrayList<String>();\n");
			deleteBatch.append("\t\tfor (int i = 0; i < uuids.length; i++) {\n\t\t\tlist.add(uuids[i]);\n\t\t}\n");
			deleteBatch.append("\t\tif (list.size() == 0) {\n\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"请选择批量删除对象！\"), response);\n\t\t\treturn;\n\t\t}\n");
			deleteBatch.append("\t\t"+toLowerCaseFirstOne(serviceCName)+".deleteBatchByIds(list);\n");
			deleteBatch.append("\n\t\t").append("writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"批量删除成功!\"),response);");
			deleteBatch.append("\n\t\tlogger.info(\"["+className+"]:end deleteBatch"+ entityClassName +"\"").append(");\n");
			deleteBatch.append("\n\t}\n");
			
			//viewMethod
			viewMethod.append("\n\t/**\n\t* 获取单个\n\t*");
			viewMethod.append("\n\t* @param "+ uuidName +" ").append(uuidremarks);//uuid注释拼接
			viewMethod.append("\n\t* @return\n\t*/\n\t");
			viewMethod.append("@RequestMapping(value=\"/views\", method=RequestMethod.POST)").append("\n\t");
			viewMethod.append("public void views").append(entityClassName+" (");
			viewMethod.append(uuidType).append(" "+uuidName).append(", ");//uuid方法拼接
			viewMethod.append("HttpServletResponse response) {").append("\n");
			viewMethod.append("\t\tlogger.info(\"["+className+"]:begin views"+ entityClassName +"\"").append(");\n\r");
			
			if(!boolUUID){//有UUID
				viewMethod.append("\t\tif (StringUtil.isEmpty(").append(uuidName).append(")) {\n");
				viewMethod.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"["+uuidremarks+"]不能为空!\"), response);\n");
				viewMethod.append("\t\t\treturn;\n\t\t}\n\r");
			}else{//id
				viewMethod.append("\t\tif (").append(uuidName+" == null || ").append(uuidName+" == 0").append(") {\n");
				viewMethod.append("\t\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(false, -1, \"["+uuidremarks+"]不能为空!\"), response);\n");
				viewMethod.append("\t\t\treturn;\n\t\t}\n\r");
			}
			viewMethod.append("\t\t"+entityClassName).append(" ").append(toLowerCaseFirstOne(entityClassName)).append(" = ").append("new "+entityClassName+"()").append(";\n");
			viewMethod.append("\t\t"+toLowerCaseFirstOne(entityClassName)).append(".set"+setGetUUIDName).append("(").append(uuidName+");\n");
			viewMethod.append("\n\t\t"+toLowerCaseFirstOne(entityClassName)+" = "+toLowerCaseFirstOne(serviceCName)).append(".get").append(entityClassName).append("(").append(toLowerCaseFirstOne(entityClassName)).append(");\n");
			viewMethod.append("\n\t\t").append(vOCName).append(" "+toLowerCaseFirstOne(vOCName)).append(" = ").append("new "+vOCName+"();\n");
			viewMethod.append("\t\t"+toLowerCaseFirstOne(vOCName)).append(".convertPOToVO(").append(toLowerCaseFirstOne(entityClassName)+");\n\r");
			viewMethod.append("\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"获取单个信息成功\", "+toLowerCaseFirstOne(vOCName)+"), response);\n");
			viewMethod.append("\t\tlogger.info(\"["+className+"]:end views"+ entityClassName +"\"").append(");\n");
			viewMethod.append("\n\t}\n");
			
		//	getMethodList
			getMethodList.append("\n\t/**\n\t* 获取列表<List>\n\t* ");
			getMethodList.append("\n\t* @return\n\t*/\n\t");
			getMethodList.append("@RequestMapping(value=\"/find/all\", method=RequestMethod.POST)").append("\n\t");
			getMethodList.append("public void find").append(entityClassName+"List (");
			getMethodList.append("HttpServletResponse response) {").append("\n");
			getMethodList.append("\t\tlogger.info(\"["+className+"]:begin find"+ entityClassName +"List\"").append(");\n\r");
			getMethodList.append("\t\tList<").append(entityClassName+"> ").append("lists = ").append(toLowerCaseFirstOne(serviceCName)+".find").append(entityClassName+"List();\n");
			getMethodList.append("\t\tList<").append(vOCName+"> vos = new ArrayList<"+vOCName+">();\n");
			getMethodList.append("\t\t"+vOCName+" vo;\n");
			getMethodList.append("\t\tfor ("+entityClassName+" "+toLowerCaseFirstOne(entityClassName)+" : lists) {\n");
			getMethodList.append("\t\t\tvo = new ").append(vOCName+"();\n\r");
			getMethodList.append("\t\t\tvo.convertPOToVO(").append(toLowerCaseFirstOne(entityClassName)+");\n\r");
			getMethodList.append("\t\t\tvos.add(vo);\n\t\t}\n");
			getMethodList.append("\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"list列表获取成功!\", vos),response);\n");
			getMethodList.append("\t\tlogger.info(\"["+className+"]:end find"+ entityClassName +"List\"").append(");\n\r");
			getMethodList.append("\n\t}\n");
			
		//	getMethodPage
			getMethodPage.append("\n\t/**\n\t* 获取列表<Page>\n\t* ");
			getMethodPage.append("\n\t* @param pageNum 页码\n\t* @param pageSize 页数\n");
			getMethodPage.append("\t* @return\n\t*/\n\t");
			getMethodPage.append("@RequestMapping(value=\"/find/by/cnd\", method=RequestMethod.POST)").append("\n\t");
			getMethodPage.append("public void find").append(entityClassName+"Page (");
			getMethodPage.append("Integer pageNum, Integer pageSize, ");
			getMethodPage.append("HttpServletResponse response) {").append("\n");
			getMethodPage.append("\t\tlogger.info(\"["+className+"]:begin find"+ entityClassName +"Page\"").append(");\n\r");
			getMethodPage.append("\t\tif (pageNum == null || pageNum == 0) {\n");
			getMethodPage.append("\t\t\tpageNum = 1;\n\t\t}\n");
			getMethodPage.append("\t\tif (pageSize == null || pageSize == 0) {\n");
			getMethodPage.append("\t\t\tpageSize = 10;\n\t\t}\n");
			getMethodPage.append("\t\tPage<").append(entityClassName+"> ").append("page = ").append(toLowerCaseFirstOne(serviceCName)+".find").append(entityClassName+"Page(pageNum, pageSize);\n");
			getMethodPage.append("\t\tPage<").append(vOCName+"> ").append("pageVO = ").append("new Page<"+vOCName+">(page.getPageNumber(), page.getPageSize(), page.getTotalCount());\n");
			getMethodPage.append("\t\tList<").append(vOCName+"> vos = new ArrayList<"+vOCName+">();\n");
			getMethodPage.append("\t\t"+vOCName+" vo;\n");
			getMethodPage.append("\t\tfor ("+entityClassName+" "+toLowerCaseFirstOne(entityClassName)+" : page.getResult()) {\n");
			getMethodPage.append("\t\t\tvo = new ").append(vOCName+"();\n\r");
			getMethodPage.append("\t\t\tvo.convertPOToVO(").append(toLowerCaseFirstOne(entityClassName)+");\n\r");
			getMethodPage.append("\t\t\tvos.add(vo);\n\t\t}\n");
			getMethodPage.append("\t\tpageVO.setResult(vos);\n\r");
			getMethodPage.append("\t\twriteAjaxJSONResponse(ResultMessageBuilder.build(true, 1, \"page列表获取成功!\", pageVO),response);\n");
			getMethodPage.append("\t\tlogger.info(\"["+className+"]:end find"+ entityClassName +"Page\"").append(");\n\r");
			getMethodPage.append("\n\t}\n");
			
			controller.append(insertMethod);
			controller.append(updateMethod);
			controller.append(deleteMethod);
			controller.append(deleteBatch);
			controller.append(viewMethod);
			controller.append(getMethodList);
			controller.append(getMethodPage);
			controller.append("//<=================定制内容开始==============\n").append(
					myContent).append(
					"//==================定制内容结束==============>\n\n");// 定制内容
			controller.append("}");
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(controller.toString());
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成添加页面
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param fileName 支持多表
	 * @param tableRemarks 表注释  
	 */
	public void writeAddHtmlFile(File dirFile, String charset, String tableName, String fileName, String tableRemarks) {
		StringBuffer addHtml = new StringBuffer();//汇总文件
		String head="<!DOCTYPE html>\n"
				+"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+"<head>\n"
				+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
				+"<title>添加"+tableRemarks+"</title>\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/base-min.css\" media=\"all\" />\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/index.css\" media=\"all\" />\n"
				+"<script type=\"text/javascript\" src=\"../js/jquery/jquery-1.8.3.min.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckeditor/ckeditor.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckfinder/ckfinder.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery-form.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/layer/layer.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/common/common.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/"+fileName+"/"+getClassName("add", tableName)+"_add.js?ts=new Date()\"></script>\n</head>\n"
				+"\n<body>\n\t<div class=\"box\">\n\t\t<ul class=\"list\">\n";
		try {
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			StringBuffer bufferLi=new StringBuffer();
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS")!=null?map.get("REMARKS").toString():"";
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					bufferLi.append("\t\t\t<li class=\"list-li\">\n")
							.append("\t\t\t\t<label class=\"leftLi\">").append(remarks+"：</label>\n")
							.append("\t\t\t\t<div class=\"rightLi\">\n")
							.append("\t\t\t\t\t").append("<input type=\"text\" class=\"inputStyle").append(" "+columnName+"\"/>\n")
							.append("\t\t\t\t\t<span class=\"p-red\"> *</span>\n\t\t\t\t</div>\n\t\t\t</li>\n");
				}				
			}
			addHtml.append(head)
					.append(bufferLi)
					.append("\t\t\t<li class=\"list-li\">\n\t\t\t\t<input type=\"button\" class=\"buttonStyle submit\" value=\"添加\"/>\n\t\t\t</li>\n")
					.append("\t\t</ul>\n\t</div>\n</body>\n</html>\n");
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(addHtml.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成修改页面
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param fileName 支持多表
	 * @param tableRemarks 表注释  
	 */
	public void writeModifyHtmlFile(File dirFile, String charset, String tableName, String fileName, String tableRemarks) {
		StringBuffer modifyHtml = new StringBuffer();//汇总文件
		String head="<!DOCTYPE html>\n"
				+"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+"<head>\n"
				+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
				+"<title>修改"+tableRemarks+"</title>\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/base-min.css\" media=\"all\" />\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/index.css\" media=\"all\" />\n"
				+"<script type=\"text/javascript\" src=\"../js/jquery/jquery-1.8.3.min.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckeditor/ckeditor.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckfinder/ckfinder.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery-form.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/layer/layer.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/common/common.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/"+fileName+"/"+getClassName("modify", tableName)+"_modify.js?ts=new Date()\"></script>\n</head>\n"
				+"\n<body>\n\t<div class=\"box\">\n\t\t<ul class=\"list\">\n";
		try {
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			StringBuffer bufferLi=new StringBuffer();
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS")!=null?map.get("REMARKS").toString():"";
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					bufferLi.append("\t\t\t<li class=\"list-li\">\n")
							.append("\t\t\t\t<label class=\"leftLi\">").append(remarks+"：</label>\n")
							.append("\t\t\t\t<div class=\"rightLi\">\n")
							.append("\t\t\t\t\t").append("<input type=\"text\" class=\"inputStyle").append(" "+columnName+"\"/>\n")
							.append("\t\t\t\t\t<span class=\"p-red\"> *</span>\n\t\t\t\t</div>\n\t\t\t</li>\n");
				}
				
			}
			bufferLi.append("\t\t\t<li class=\"list-li\">\n\t\t\t\t<input type=\"button\" class=\"buttonStyle submit\" value=\"修改\"/>\n\t\t\t</li>\n");
        
			modifyHtml.append(head).append(bufferLi).append("\t\t</ul>\n\t</div>\n</body>\n</html>\n");
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(modifyHtml.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成列表页面
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param fileName 支持多表
	 * @param tableRemarks 表注释  
	 */
	public void writeListHtmlFile(File dirFile, String charset, String tableName, String fileName, String tableRemarks) {
		StringBuffer listHtml = new StringBuffer();//汇总文件
		String head="<!DOCTYPE html>\n"
				+"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+"<head>\n"
				+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
				+"<title>列表"+tableRemarks+"</title>\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/base-min.css\" media=\"all\" />\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/index.css\" media=\"all\" />\n"
				+"<script type=\"text/javascript\" src=\"../js/jquery/jquery-1.8.3.min.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckeditor/ckeditor.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckfinder/ckfinder.js?ts=new Date()\"></script>\n"
				+"<!-- <script type=\"text/javascript\" src=\"../../js/jquery.js?ts=new Date()\"></script> -->\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery-form.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/layer/layer.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/common/common.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/"+fileName+"/"+getClassName("", tableName)+".js?ts=new Date()\"></script>\n</head>\n"
				+"\n<body>\n" 
				+"\t<div class=\"rightTop greyBox\">\n"
				+"\t\t<span class=\"cur_postion\"></span>\n"
				+"\t\t<input type=\"button\" class=\"topBtn addit\" value=\"新增\"></input>\n"
				+"\t\t<input type=\"button\" class=\"topBtn delthese\" value=\"删除\"></input>\n"
				+"\t</div>\n"
				+"\n\t<div class=\"rightContent\">\n"
				+"\t\t<div class=\"manageBox margin-tb\">\n"
				+"\t\t\t<div class=\"searchBox margin-tb\">\n"
				+"\t\t\t\t<div style=\"background:#fff;width:100px;margin-top:-18px;\" >搜 索 框</div>\n"
				+"\t\t\t\t<div class=\"searchContent margin-tb\">\n"
				+"\t\t\t\t<!-- js生成查询条件 -->\n"
				+"\t\t\t\t</div>\n"
				+"\t\t\t\t<div class=\"margin-tb\">\n"
				+"\t\t\t\t\t<input type=\"button\" class=\"searchBtn\" value=\"查询\"></input>\n"
				+"\t\t\t\t\t<input type=\"button\" class=\"resetBtn\" value=\"重置\"></input>\n"
				+"\t\t\t\t</div>\n"
				+"\t\t\t</div>\n"
				+"\n\t\t\t<table class=\"tb\">\n"
				+"\t\t\t\t<thead class=\"tb-head\">\n"
				+"\t\t\t\t\t<tr>\n"
				+"\t\t\t\t\t\t<!-- js生成表格头部 -->\n"
				+"\t\t\t\t\t</tr>\n"
				+"\t\t\t\t</thead>\n"
				+"\t\t\t\t<tbody class=\"tb-body\">\n"
				+"\t\t\t\t\t<!-- js生成表格内容 -->\n"
				+"\t\t\t\t</tbody>\n"
				+"\t\t\t\t<tfoot class=\"tb-foot\">\n"
				+"\t\t\t\t\t<tr>\n"
				+"\t\t\t\t\t\t<!-- js生成表格底部 -->\n"
				+"\t\t\t\t\t</tr>\n"
				+"\t\t\t\t</tfoot>\n"
				+"\t\t\t</table>\n"
				+"\t\t</div>\n"
				+"\t</div>\n"
				+"</body>\n"
				+"</html>\n";
		listHtml.append(head);
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dirFile), charset);
			out.write(listHtml.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	/**
	 * 生成详情页面
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param fileName 支持多表
	 * @param tableRemarks 表注释  
	 */
	public void writeDetailHtmlFile(File dirFile, String charset, String tableName, String fileName, String tableRemarks) {
		StringBuffer detailHtml = new StringBuffer();//汇总文件
		String head="<!DOCTYPE html>\n"
				+"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+"<head>\n"
				+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
				+"<title>详情"+tableRemarks+"</title>\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/base-min.css\" media=\"all\" />\n"
				+"<link type=\"text/css\" rel=\"stylesheet\" href=\"../css/common/index.css\" media=\"all\" />\n"
				+"<script type=\"text/javascript\" src=\"../js/jquery/jquery-1.8.3.min.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckeditor/ckeditor.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../ckfinder/ckfinder.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../../js/jquery-form.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/layer/layer.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/common/common.js?ts=new Date()\"></script>\n"
				+"<script type=\"text/javascript\" src=\"../js/"+fileName+"/"+getClassName("detail", tableName)+"_detail.js?ts=new Date()\"></script>\n</head>\n"
				+"\n<body>\n\t<div class=\"box\">\n\t\t<ul class=\"list\">\n";
		try {
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			StringBuffer bufferLi=new StringBuffer();
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS")!=null?map.get("REMARKS").toString():"";
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					bufferLi.append("\t\t\t<li class=\"list-li\">\n")
							.append("\t\t\t\t<label class=\"leftLi\">").append(remarks+"：</label>\n")
							.append("\t\t\t\t<div class=\"rightLi\">\n")
							.append("\t\t\t\t\t").append("<label class=\"").append(""+columnName+"\"></label>\n")
							.append("\t\t\t\t</div>\n\t\t\t</li>\n");
				}			
			}
			
			detailHtml.append(head).append(bufferLi).append("\t\t</ul>\n\t</div>\n</body>\n</html>\n");
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(detailHtml.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成添加js
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表  
	 */
	public void writeAddJsFile(File dirFile, String charset, String tableName) {
		String entityClassName=getClassName("entity", tableName);// 获取entity类名
		StringBuffer addJs = new StringBuffer();//汇总文件
		String func="// JavaScript Document\n"
				+"$(function () {\n"
				+"\tinitAdd();\n"
				+"\t//提交\n"
				+"\t$(\".submit\").on(\"click\",function(){\n"
				+"\t\tcheckAdd();\n"
				+"\t});\n});\n";
		try {
			StringBuffer bufferInit=new StringBuffer("//初始化\nfunction initAdd(){\n");
			StringBuffer bufferCheck=new StringBuffer("\n//检查提交\nfunction checkAdd(){\n");
			StringBuffer bufferAdd=new StringBuffer("\n//提交\nfunction Add(msgObject){\n");
			StringBuffer str=new StringBuffer("\tvar str = ");
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS")!=null?map.get("REMARKS").toString():"";
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					bufferInit.append("\t$(\".").append(columnName).append("\").").append("val(\"\");\n");
					bufferCheck.append("\tif($.trim($(\".").append(columnName+"\").val()) == \"\"){\n")
								.append("\t\talert(\"").append(remarks+"不能为空，请填写完再提交！\");\n")
								.append("\t\t$(\".").append(columnName+"\").focus();\n")
								.append("\t\treturn false;\n\t}\n");
					bufferAdd.append("\tvar ").append(columnName).append(" = ").append("$(\".").append(columnName+"\")").append(".val();\n");
					str.append("'&"+columnName+"='+encodeURIComponent(").append(columnName+")+");
				}
				
			}
			//富文本框[注释]
			bufferInit.append("\t/*CKEDITOR.replace( 'crcseContent' , { height: '240px', \n")
						.append("\t\twidth: '480px',\n")
						.append("\t\ttoolbar: [[ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo', 'Bold',\n")
						.append("\t\t\t\t\t\t'Italic',\"Format\",\"FontSize\",\"TextColor\" ,\"CodeSnippet\",\"Table\"]],\n")
						.append("\t\textraPlugins: 'codesnippet',\n\t\tuiColor: '#f3f3f3'\n")
						.append("\t} );*/\n");
			bufferInit.append("}");
			
			//富文本框[注释]
			bufferCheck.append("\t/*if($.trim(CKEDITOR.instances.crcseContent.getData()) == \"\"){\n")
						.append("\t\talert(\"内容不能为空，请填写完再提交！\");\n")
						.append("\t\treturn false;\n\t}*/\n");
			bufferCheck.append("\n\tvar r=confirm(\"是否确认增加？\");\n")
						.append("\tif (r==true){\n")
						.append("\t\tvar msgObject = parent.layer.msg('处理中，请等待……', {\n")
						.append("\t\t\ticon: 16,\n")
						.append("\t\t\tshade: 0.4,\n")
						.append("\t\t\ttime: waitImgTime //（如果不配置，默认是3秒）\n")
						.append("\t\t}, function(index){\n")
						.append("\t\t\t//do something\n")
						.append("\t\t\tparent.layer.close(index);\n")
						.append("\t\t});\n")
						.append("\t\tAdd(msgObject);\n\t}\n}");
			int indxe=str.indexOf("&");
			str.replace(indxe, indxe+1, "");
			String strs=str.toString().substring(0, (str.toString().length()-1));
			bufferAdd.append(strs+";\n\t")
						.append("getOData(str,\"").append(toLowerCaseFirstOne(entityClassName)+"/add/"+toLowerCaseFirstOne(entityClassName)+"\"").append(",{\n")
						.append("\t\tfn:function(oData){\n")
						.append("\t\t\twindow.parent.refreshList();\n")
						.append("\t\t\talert(\"增加成功！\");\n")
						.append("\t\t},\n")
						.append("\t\tfnerr:function(oData){\n")
						.append("\t\t\tparent.layer.close(msgObject);\n")
						.append("\t\t}\n")
						.append("\t});\n}");
			addJs.append(func);
			addJs.append(bufferInit);
			addJs.append(bufferCheck);
			addJs.append(bufferAdd);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(addJs.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成修改js
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表  
	 */
	public void writeModifyJsFile(File dirFile, String charset, String tableName) {
		String entityClassName=getClassName("entity", tableName);// 获取entity类名
		StringBuffer modifyJs = new StringBuffer();//汇总文件
		String func="// JavaScript Document\n"
				+"$(function () {\n"
				+"\tinitModify();\n"
				+"\t//提交\n"
				+"\t$(\".submit\").on(\"click\",function(){\n"
				+"\t\tcheckModify();\n"
				+"\t});\n});\n";
		try {
			StringBuffer bufferInit=new StringBuffer("//初始化\nfunction initModify(){\n");
			StringBuffer bufferCheck=new StringBuffer("\n//检查提交\nfunction checkModify(){\n");
			StringBuffer getInfo=new StringBuffer("\n//获取详情\nfunction getInfo(id){\n");
			StringBuffer views=new StringBuffer("");
			StringBuffer bufferModify=new StringBuffer("\n//提交\nfunction Modify(msgObject){\n\t//var crcseContent = CKEDITOR.instances.crcseContent.getData();\n");
			StringBuffer str=new StringBuffer("\tvar str = ");
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			String uuid="";
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS")!=null?map.get("REMARKS").toString():"";
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					bufferInit.append("\t$(\".").append(columnName).append("\").").append("val(\"\");\n");
					views.append("\t\t\t$(\".").append(columnName+"\")").append(".val(oData.data.").append(columnName+" || \"\");\n");
					bufferCheck.append("\tif($.trim($(\".").append(columnName+"\").val()) == \"\"){\n")
								.append("\t\talert(\"").append(remarks+"不能为空，请填写完再提交！\");\n")
								.append("\t\t$(\".").append(columnName+"\").focus();\n")
								.append("\t\treturn false;\n\t}\n");
					
					bufferModify.append("\tvar ").append(columnName).append(" = ").append("$(\".").append(columnName+"\")").append(".val();\n");
					str.append("'&"+columnName+"='+encodeURIComponent(").append(columnName+")+");
				} else if (columnName.toUpperCase().endsWith("UUID")){
					bufferModify.append("\tvar ").append(columnName).append(" = ").append("getQueryString(\"id\");\n");
					str.append("'"+columnName+"='").append("+encodeURIComponent("+columnName+")+");
					uuid=columnName;
				}
			}
			getInfo.append("\tvar str = '").append(uuid+"='+encodeURIComponent(id);\n")
					.append("\tgetOData(str,\"").append(toLowerCaseFirstOne(entityClassName)+"/views\",{fn:function(oData){\n")
					.append("\t\tif(oData.code == 1) {\n")
					.append(views);
			getInfo.append("\t\t\t//CKEDITOR.instances.crcseContent.setData(oData.data.crcseContent);\n")
					.append("\t\t} else {\n")
					.append("\t\t\talert(data.errMsg);\n")
					.append("\t\t}\n\t}});\n}\n");
			
			//富文本框[注释]
			bufferInit.append("\t/*CKEDITOR.replace( 'crcseContent' , { height: '240px', \n")
						.append("\t\twidth: '480px',\n")
						.append("\t\ttoolbar: [[ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo', 'Bold',\n")
						.append("\t\t\t\t\t\t'Italic',\"Format\",\"FontSize\",\"TextColor\" ,\"CodeSnippet\",\"Table\"]],\n")
						.append("\t\textraPlugins: 'codesnippet',\n\t\tuiColor: '#f3f3f3'\n")
						.append("\t} );*/\n");
			bufferInit.append("\tgetInfo(getQueryString(\"id\"));\n");
			bufferInit.append("}");
			
			//富文本框[注释]
			bufferCheck.append("\t/*if($.trim(CKEDITOR.instances.crcseContent.getData()) == \"\"){\n")
						.append("\t\talert(\"内容不能为空，请填写完再提交！\");\n")
						.append("\t\treturn false;\n\t}*/\n");
			bufferCheck.append("\n\tvar r=confirm(\"是否确认修改？\");\n")
						.append("\tif (r==true){\n")
						.append("\t\tvar msgObject = parent.layer.msg('处理中，请等待……', {\n")
						.append("\t\t\ticon: 16,\n")
						.append("\t\t\tshade: 0.4,\n")
						.append("\t\t\ttime: waitImgTime //（如果不配置，默认是3秒）\n")
						.append("\t\t}, function(index){\n")
						.append("\t\t\t//do something\n")
						.append("\t\t\tparent.layer.close(index);\n")
						.append("\t\t});\n")
						.append("\t\tModify(msgObject);\n\t}\n}");
			
			String strs=str.toString().substring(0, (str.toString().length()-1));
			bufferModify.append(strs+";\n\t")
						.append("getOData(str,\"").append(toLowerCaseFirstOne(entityClassName)+"/update/"+toLowerCaseFirstOne(entityClassName)+"\"").append(",{\n")
						.append("\t\tfn:function(oData){\n")
						.append("\t\t\twindow.parent.refreshList();\n")
						.append("\t\t\talert(\"修改成功！\");\n")
						.append("\t\t},\n")
						.append("\t\tfnerr:function(oData){\n")
						.append("\t\t\tparent.layer.close(msgObject);\n")
						.append("\t\t}\n")
						.append("\t});\n}");
			modifyJs.append(func);
			modifyJs.append(bufferInit);
			modifyJs.append(getInfo);
			modifyJs.append(bufferCheck);
			modifyJs.append(bufferModify);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(modifyJs.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成列表js
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表
	 * @param fileName 支持多表  
	 */
	public void writeListJsFile(File dirFile, String charset, String tableName, String fileName) {
		String entityClassName=getClassName("entity", tableName);// 获取entity类名
		StringBuffer listJs = new StringBuffer();//汇总文件
		String uuid="";
		StringBuffer aData=new StringBuffer("\tvar aData =[{name:\"<input type='checkbox' name='checkboxAll' value='checkbox' />\",percent:\"5\"},\n");
		StringBuffer td=new StringBuffer();
		try {
			StringBuffer bufferInit=new StringBuffer("//初始化\nfunction initModify(){\n");
			StringBuffer bufferCheck=new StringBuffer("\n//检查提交\nfunction checkModify(){\n");
			StringBuffer views=new StringBuffer("");
			StringBuffer bufferModify=new StringBuffer("\n//提交\nfunction Modify(){\n\t//var crcseContent = CKEDITOR.instances.crcseContent.getData();\n");
			StringBuffer str=new StringBuffer("\tvar str = ");
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				String remarks = map.get("REMARKS")!=null?map.get("REMARKS").toString():"";
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					bufferInit.append("\t$(\".").append(columnName).append("\").").append("val(\"\");\n");
					views.append("\t\t\t$(\".").append(columnName+"\")").append(".val(oData.data.").append(columnName+" || \"\");\n");
					bufferCheck.append("\tif($.trim($(\".").append(columnName+"\").val()) == \"\"){\n")
								.append("\t\talert(\"").append(remarks+"不能为空，请填写完再提交！\");\n")
								.append("\t\t$(\".").append(columnName+"\").focus();\n")
								.append("\t\treturn false;\n\t}\n");
					
					bufferModify.append("\tvar ").append(columnName).append(" = ").append("$(\".").append(columnName+"\")").append(".val();\n");
					str.append("'&"+columnName+"='+encodeURIComponent(").append(columnName+")+");
					
					aData.append("\t\t\t\t{name:\"").append(remarks+"\",percent:\"10\"},\n");
					td.append("\t\t\t\t+'<td>'+(arrData[i].").append(columnName).append(" || \"\")+'</td>'\n");
				} else if (columnName.toUpperCase().endsWith("UUID")){
					bufferModify.append("\tvar ").append(columnName).append(" = ").append("getQueryString(\"id\");\n");
					str.append("'"+columnName+"='").append("encodeURIComponent("+columnName+")+");
					uuid=columnName;
				}
			}
			aData.append("\t\t\t\t{name:\"操作\",percent:\"10\"}");
			String tdCheckbox="\t\t\t\t+'<td>'+'<input type=\"checkbox\" name=\"checkbox\" value=\"checkbox\" data-id=\"'+arrData[i]."+uuid+"+'\"/>'+'</td>'\n";
			td.append("\t\t\t\t+'<td>'\n")
				.append("\t\t\t\t+'<a  class=\"p-edit detailit\" data-id=\"'+arrData[i].").append(uuid+"+'\">查看</a>'\n")
				.append("\t\t\t\t+'<a  class=\"p-edit modifyit\" data-id=\"'+arrData[i].").append(uuid+"+'\">修改</a>'\n")
				.append("\t\t\t\t+'<a  class=\"p-edit delit\" data-id=\"'+arrData[i].").append(uuid+"+'\">删除</a>'\n")
				.append("\t\t\t\t+'</td>'\n")
				.append("\t\t\t\t+'</tr>';\n");			
			String func="// JavaScript Document\n"
					+"$(function () {\n"
					+"\t$(\".cur_postion\").html(\"当前位置： [ \"+sessionStorage.getItem(\"pmenuname\")+\" ] - [ \"+sessionStorage.getItem(\"cmenuname\")+\" ]\");\n"
					+"\n\t//查询条件\n"
					+"\tvar strhtml_searchContent = '<div class=\"inline-block margin\">'\n"
					+"\t\t+'<span>标题:</span>'\n"
					+"\t\t+'<input type=\"text\" class=\"inputStyle_condition crcseTitle\"/>'\n"
					+"\t\t+'</div>';\n"
					+"\t$(\".searchContent\").html(strhtml_searchContent);\n"
					+"\t//是否显示查询条件\n"
					+"\tshowSearchBox(true);\n\n"
					+"\tvar obj = {};//查询条件对象\n"
					+"\tsearchContent(obj);\n"
					+"\tshowList(obj,1);\n\n"
					+"\t//详情\n"
					+"\t$(\"body\").delegate('.detailit','click', function(){\n"
					+"\t\tlayer.open({\n"
					+"\t\t\ttype: 2,\n"
					+"\t\t\ttitle: '详情',\n"
					+"\t\t\tscrollbar: false,\n"
					+"\t\t\tmaxmin: true,\n"
					+"\t\t\tshadeClose: false, //点击遮罩关闭层\n"
					+"\t\t\tarea : [widthLayer , heightLayer],\n"
					+"\t\t\tcontent: '../"+fileName+"/"+getClassName("detail", tableName)+"_detail.html?id='+$(this).attr(\"data-id\")+'&timestamp='+(new Date()).valueOf()\n"
					+"\t\t});\n"
					+"\t});\n"
					+"\t//新增\n"
					+"\t$('.addit').on('click', function(){\n"
					+"\t\tlayer.open({\n"
					+"\t\t\ttype: 2,\n"
					+"\t\t\ttitle: '新增',\n"
					+"\t\t\tscrollbar: false,\n"
					+"\t\t\tmaxmin: true,\n"
					+"\t\t\tshadeClose: false, //点击遮罩关闭层\n"
					+"\t\t\tarea : [widthLayer , heightLayer],\n"
					+"\t\t\tcontent: '../"+fileName+"/"+getClassName("add", tableName)+"_add.html?timestamp='+(new Date()).valueOf()\n"
					+"\t\t});\n"
					+"\t});\n"
					+"\t//修改\n"
					+"\t$(\"body\").delegate('.modifyit','click', function(){\n"
					+"\t\tlayer.open({\n"
					+"\t\t\ttype: 2,\n"
					+"\t\t\ttitle: '修改',\n"
					+"\t\t\tscrollbar: false,\n"
					+"\t\t\tmaxmin: true,\n"
					+"\t\t\tshadeClose: false, //点击遮罩关闭层\n"
					+"\t\t\tarea : [widthLayer , heightLayer],\n"
					+"\t\t\tcontent: '../"+fileName+"/"+getClassName("modify", tableName)+"_modify.html?id='+$(this).attr(\"data-id\")+'&timestamp='+(new Date()).valueOf()\n"
					+"\t\t});\n"
					+"\t});\n"
					+"\t//全选\n"
					+"\t$(\"body\").delegate(\"input[name='checkboxAll']\",\"click\",function(){\n"
					+"\t\tif($(this).attr(\"checked\")){\n"
					+"\t\t\t$(\"input[name='checkbox']\").each(function(){\n"
					+"\t\t\t\t$(this).attr(\"checked\",true);\n"
					+"\t\t\t});\n"
					+"\t\t}else{\n"
					+"\t\t\t$(\"input[name='checkbox']\").each(function(){\n"
					+"\t\t\t\t$(this).removeAttr(\"checked\");\n"
					+"\t\t\t});\n"
					+"\t\t}\n"
					+"\t});\n\n"
					+"\t//批量删除\n"
					+"\t$(\"body\").delegate(\".delthese\",\"click\",function(){\n"
					+"\t\tvar ids = '';\n"
					+"\t\t$(\"input[name='checkbox']\").each(function(){\n"
					+"\t\t\tif($(this).attr(\"checked\")){\n"
					+"\t\t\t\tids += $(this).attr(\"data-id\")+\"|\";\n"
					+"\t\t\t}\n"
					+"\t\t});\n"
					+"\t\tif(ids == \"\"){\n"
					+"\t\t\talert(\"未选择删除对象！\");\n"
					+"\t\t}else{\n"
					+"\t\t\tvar r=confirm(\"是否确认删除所选的记录？\");\n"
					+"\t\t\tif (r==true){\n"
					+"\t\t\t\tvar str = '"+uuid+"s='"+"+encodeURIComponent(ids);\n"
					+"\t\t\t\tgetOData(str,\""+toLowerCaseFirstOne(entityClassName)+"/delete/batch\",{fn:function(oData){\n"
					+"\t\t\t\t\t\tvar obj = {};//查询条件对象\n"
					+"\t\t\t\t\t\tsearchContent(obj);\n"
					+"\t\t\t\t\t\tpagenum = parseInt($(\".curpage\").text());\n"
					+"\t\t\t\t\t\tisNull(obj,pagenum);\n"
					+"\t\t\t\t\t\talert(\"删除成功！\");\n"
					+"\t\t\t\t\t}}\n"
					+"\t\t\t\t);\n"
					+"\t\t\t}\n"
					+"\t\t}\n"
					+"\t});\n"
					+"\n\t//删除\n"
					+"\t$(\"body\").delegate(\".delit\",\"click\",function(){\n"
					+"\t\tvar r=confirm(\"是否确认删除？\");\n"
					+"\t\tif (r==true){\n"
					+"\t\t\tvar str = '"+uuid+"='+encodeURIComponent($(this).attr(\"data-id\"));\n"
					+"\t\t\tgetOData(str,\""+toLowerCaseFirstOne(entityClassName)+"/delete/one\",{fn:function(oData){\n"
					+"\t\t\t\t\tvar obj = {};//查询条件对象\n"
					+"\t\t\t\t\tsearchContent(obj);\n"
					+"\t\t\t\t\tpagenum = parseInt($(\".curpage\").text());\n"
					+"\t\t\t\t\tisNull(obj,pagenum);\n"
					+"\t\t\t\t\talert(\"删除成功！\");\n"
					+"\t\t\t\t}}\n"
					+"\t\t\t);\n"
					+"\t\t}\n"
					+"\t});\n"
					+"\t//tr高亮显示\n"
					+"\t$(\"body\").delegate(\".trHighLight\",\"mouseleave\",function(){\n"
					+"\t\t$(this).find(\"td\").css(\"background-color\",\"#fff\");\n"
					+"\t});\n"
					+"\t//tr高亮显示并显示图\n"
					+"\t$(\"body\").delegate(\".trHighLight\",\"mouseenter\",function(){\n"
					+"\t\t$(this).find(\"td\").css(\"background-color\",\"#c1ebff\");\n"
					+"\t\t/*var imgurl = $(this).attr(\"data-imageurl\");\n"
					+"\t\tif(imgurl != \"#\"){\n"
					+"\t\t\tvar that = this;\n"
					+"\t\t\t$(\"<img/>\").attr(\"src\", imgurl).load(function() {\n"
					+"\n\t\t\t\tvar width = 150;\n"
					+"\t\t\t\tvar realWidth = this.width;\n"
					+"\t\t\t\tvar realHeight = this.height;\n"
					+"\t\t\t\tvar height = realHeight/realWidth*width;\n"
					+"\t\t\t\tif(realWidth>=width){\n"
					+"\t\t\t\t\tvar bannerimg = '<img id=\"bannerimg\" src=\"'+imgurl+'\" width=\"'+width+'px\" height=\"'+height+'px\" />';\n"
					+"\t\t\t\t}\n"
					+"\t\t\t\telse{//如果小于浏览器的宽度按照原尺寸显示\n"
					+"\t\t\t\t\t$(\"#bannerimg\").css(\"width\",realWidth+'px').css(\"height\",realHeight+'px');\n"
					+"\t\t\t\t\tvar bannerimg = '<img id=\"bannerimg\" src=\"'+imgurl+'\" width=\"'+realWidth+'px\" height=\"'+realHeight+'px\" />';\n"
					+"\t\t\t\t}\n"
					+"\n\t\t\t\tlayer.tips(bannerimg, that ,{\n"
					+"\t\t\t\t\ttips: 3\n"
					+"\t\t\t\t});\n"
					+"\t\t\t});\n"
					+"\t\t}*/\n"
					+"\t});\n"
					+"\t//查询\n"
					+"\t$(\"body\").delegate(\".searchBtn\",\"click\",function(){\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tshowList(obj,1);\n"
					+"\t});\n"
					+"\t//重置\n"
					+"\t$(\"body\").delegate(\".resetBtn\",\"click\",function(){\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\t$(\".crcseTitle\").val('');\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tshowList(obj,1);\n"
					+"\t});\n"
					+"\t//上一页\n"
					+"\t$('.manageBox').delegate(\".backpage\",\"click\", function() {\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tpagenum = parseInt($(this).attr(\"data-pagenum\"));\n"
					+"\t\tshowList(obj,pagenum);\n"
					+"\t});\n"
					+"\t//下一页\n"
					+"\t$('.manageBox').delegate(\".nextpage\",\"click\", function() {\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tvar pagenum = parseInt($(this).attr(\"data-pagenum\"));\n"
					+"\t\tshowList(obj,pagenum);\n"
					+"\t});\n"
					+"\t//首页\n"
					+"\t$('.manageBox').delegate(\".firstpage\",\"click\", function() {\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tvar pagenum = parseInt($(this).attr(\"data-pagenum\"));\n"
					+"\t\tshowList(obj,pagenum);\n"
					+"\t});\n"
					+"\t//末页\n"
					+"\t$('.manageBox').delegate(\".lastpage\",\"click\", function() {\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tvar pagenum = parseInt($(this).attr(\"data-pagenum\"));\n"
					+"\t\tshowList(obj,pagenum);\n"
					+"\t});\n"
					+"\t//跳转至\n"
					+"\t$('.manageBox').delegate(\".jumppage\",\"click\", function() {\n"
					+"\t\tvar obj = {};//查询条件对象\n"
					+"\t\tsearchContent(obj);\n"
					+"\t\tvar pagenum = parseInt($('.jumppagetext').val());\n"
					+"\t\tif(pagenum>0 && pagenum <=parseInt($(this).attr(\"data-pagemax\"))){\n"
					+"\t\t\tshowList(obj,pagenum);\n"
					+"\t\t}else{\n\t\t\talert(\"查无此页！\");\n\t\t}\n"
					+"\t});\n"
					+"\n});\n";
			String showList="function showList(obj,pagenum){\n"
					+aData.toString()+"];\n"
					+"\tsetTableHead(aData);\n"
					+"\tvar str = '';\n"
					+"\tif(obj.crcseTitle==\"\"){\n"
					+"\t\tstr = 'pageNum='+pagenum+'&pageSize=10';\n"
					+"\t}else{\n"
					+"\t\tstr = 'pageNum='+pagenum+'&pageSize=10&crcseTitle='+encodeURIComponent(obj.crcseTitle);\n"
					+"\t}\n"
					+"\tgetOData(str,\""+toLowerCaseFirstOne(entityClassName)+"/find/by/cnd\",{fn:function(oData){\n"
					+"\t\tvar strhtml_list = \"\";\n"
					+"\t\tvar arrData = oData.data.result;\n"
					+"\t\tvar ln = arrData.length;\n"
					+"\t\tfor(var i=0;i<ln;i++){\n"
					+"\t\t\tstrhtml_list += '<tr class=\"trHighLight\">'\n"
					+tdCheckbox
					+td.toString()
					+"\t\t}\n"
					+"\t\t$(\".tb-body\").html(strhtml_list);\n"
					+"\t\tsetTableFoot(oData.data,aData.length);\n"
					+"\t\t}}\n"
					+"\t);\n"
					+"}\n";
			String isNull="function isNull(obj,pagenum){\n"
					+"\tvar str = '';\n"
					+"\tif(obj.crcseTitle==\"\"){\n"
					+"\t\tstr = 'pageNum='+pagenum+'&pageSize=10';\n"
					+"\t}else{\n"
					+"\t\tstr = 'pageNum='+pagenum+'&pageSize=10&crcseTitle='+encodeURIComponent(obj.crcseTitle);\n"
					+"\t}\n"
					+"\tgetOData(str,\""+toLowerCaseFirstOne(entityClassName)+"/find/by/cnd\",{fn:function(oData){\n"
					+"\t\tvar arrData = oData.data.result;\n"
					+"\t\tvar ln = arrData.length;\n"
					+"\t\tif(ln == 0){\n"
					+"\t\t\tif (oData.data.totalCount != 0 && pagenum !=1){\n"
					+"\t\t\t\tshowList(obj,pagenum-1);\n"
					+"\t\t\t}else{\n"
					+"\t\t\t\tshowList(obj,1);\n"
					+"\t\t\t}\n"
					+"\t\t}else{\n"
					+"\t\t\tshowList(obj,pagenum);\n"
					+"\t\t}\n"
					+"\t\t}}\n"
					+"\t);\n"
					+"}\n";			
			String refreshList="\nfunction refreshList(){\n"
					+"\tvar obj = {};//查询条件对象\n"
					+"\tsearchContent(obj);\n"
					+"\tshowList(obj,parseInt($(\".curpage\").text()));\n"
					+"\tlayer.closeAll();\n"
					+"}\n"
					+"\nfunction searchContent(obj){\n"
					+"\tobj.crcseTitle = $(\".crcseTitle\").val();\n"
					+"}\n";			
			listJs.append(func);
			listJs.append(showList);
			listJs.append(isNull);
			listJs.append(refreshList);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(listJs.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成详情js
	 * @param dirFile
	 * @param charset
	 * @param tableName 支持多表  
	 */
	public void writeDetailJsFile(File dirFile, String charset, String tableName) {
		String entityClassName=getClassName("entity", tableName);// 获取entity类名
		StringBuffer detailJs = new StringBuffer();//汇总文件
		String func="// JavaScript Document\n"
				+"$(function () {\n"
				+"\tinitDetail();\n"
				+"});\n";
		try {
			StringBuffer bufferInit=new StringBuffer("//初始化\nfunction initDetail(){\n\tgetInfo(getQueryString(\"id\"));\n}");
			StringBuffer getInfo=new StringBuffer("\n//获取详情\nfunction getInfo(id){\n");
			StringBuffer views=new StringBuffer("");
			List<Map<String, Object>> columnList = PTTools.getColumnNameList(DBUtil.getConnection(), tableName);// 得到列名集合
			String uuid="";
			for (Map<String, Object> map : columnList) {
				String columnName = map.get("SMALL_COLUMN_NAME").toString();
				//拼接字符串
				if(!columnName.toUpperCase().endsWith("UNID") && !columnName.toUpperCase().endsWith("UUID")){//将入参set对象里面
					views.append("\t\t\t$(\".").append(columnName+"\")").append(".text(oData.data.").append(columnName+" || \"\");\n");
				} else if (columnName.toUpperCase().endsWith("UUID")){
					uuid=columnName;
				}
			}
			getInfo.append("\tvar str = '").append(uuid+"='+encodeURIComponent(id);\n")
					.append("\tgetOData(str,\"").append(toLowerCaseFirstOne(entityClassName)+"/views\",{fn:function(oData){\n")
					.append("\t\tif(oData.code == 1) {\n")
					.append(views);
			getInfo.append("\t\t\t/*var imgUrl=\"/\"+oData.data.list[0].cratmDir+\"/\"+oData.data.list[0].cratmFileName;\n")
					.append("\t\t\tgetImageWidthHeight(urlfile+\"/coreAttachment/image/get/thumb/\"+oData.data.list[0].cratmUuid,function(realWidth,realHeight){\n")
					.append("\t\t\t\tvar width = 0;\n")
					.append("\t\t\t\tvar height = 200;\n")
					.append("\t\t\t\t//如果真实的宽度大于浏览器的宽度就按照200显示\n")
					.append("\t\t\t\tif(realHeight>=height){\n")
					.append("\t\t\t\t\twidth = realWidth/realHeight*height;\n")
					.append("\t\t\t\t\t$(\".preimg\").css(\"width\",width).css(\"height\",height);\n")
					.append("\t\t\t\t}else{//如果小于浏览器的宽度按照原尺寸显示}\n")
					.append("\t\t\t\t\t$(\".preimg\").css(\"width\",realWidth+'px').css(\"height\",realHeight+'px');\n")
					.append("\t\t\t\t}\n").append("\t\t\t\t$(\".preimg\").attr(\"src\",urlfile+\"/coreAttachment/image/get/thumb/\"+oData.data.list[0].cratmUuid);\n")
					.append("\t\t\t\t$(\".preimg\").attr(\"data-filename\",imgUrl);\n")
					.append("\t\t\t});*/\n")
					.append("\t\t} else {\n")
					.append("\t\t\talert(data.errMsg);\n")
					.append("\t\t}\n\t}});\n}\n");
			detailJs.append(func);
			detailJs.append(bufferInit);
			detailJs.append(getInfo);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(dirFile), charset);
			out.write(detailJs.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CreateFile f = new CreateFile();
		//获取表集合
		List<String> tables = null;
		Map<String, String> tablesMap = null;
		try {
			tables = TableComment.getAllTableName(DBUtil.getConnection());
			tablesMap = TableComment.getCommentByTableName(DBUtil.getConnection(), tables);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String charset = f.con.getCharset();
		String tableNames = f.con.getTableName();
		String[] list = tableNames.split(",");		
		String backageName = "";
		String fileName = "";
		String tableRemarks = "";
		String[] listBackage = f.con.getBackageName().split(",");
		String[] listFile = f.con.getFileName().split(",");		
		for (int i = 0; i < list.length; i++) {
			//找出匹配的表注释名
			tableRemarks = tablesMap.get(list[i].toUpperCase());
			if (i >= listBackage.length) {
				backageName = listBackage[listBackage.length - 1];
			} 
			if (i < listBackage.length) {
				backageName = listBackage[i];
			}
			if (i >= listFile.length) {
				fileName = listFile[listBackage.length - 1];
			}
			if (i < listFile.length) {
				fileName = listFile[i];
			}
			String entityPath = f.getPath("entity", ".java", list[i], backageName, fileName);
			if(!isExist(entityPath)) {
				File dirEntityFile = new File(entityPath);
				f.createFile(dirEntityFile);
				f.writeEntityFile(dirEntityFile, charset, list[i], backageName, tableRemarks);
			}
			
			String voPath = f.getPath("vo", ".java", list[i], backageName, fileName);
			if(!isExist(voPath)) {
				File dirVOFile = new File(voPath);
				f.createFile(dirVOFile);
				f.writeVOFile(dirVOFile, charset, list[i], backageName, tableRemarks);
			}
			
			String MapperPath = f.getPath("mapper", ".xml", list[i], backageName, fileName);
			if(!isExist(MapperPath)) {
				File dirMapperFile = new File(MapperPath);
				f.createFile(dirMapperFile);
				f.writeMapperFile(dirMapperFile, charset, list[i], backageName);
			}
			
			String ControllerPath = f.getPath("controller", ".java", list[i], backageName, fileName);
			if(!isExist(ControllerPath)) {
				File dirControllerFile = new File(ControllerPath);
				f.createFile(dirControllerFile);
				f.writeControllerFile(dirControllerFile, charset, list[i], backageName, tableRemarks);
			}
			/**
			String serviceIPath = f.getPath("service", ".java", list[i], backageName, fileName);
			if(!isExist(serviceIPath)) {
				File dirServiceIFile = new File(serviceIPath);
				f.createFile(dirServiceIFile);
				f.writeServiceIFile(dirServiceIFile, charset, list[i], backageName, tableRemarks);
			}
			
			String servicePath = f.getPath("service/impl", ".java", list[i], backageName, fileName);
			if(!isExist(servicePath)) {
				File dirServiceFile = new File(servicePath);		
				f.createFile(dirServiceFile);		
				f.writeServiceFile(dirServiceFile, charset, list[i], backageName, tableRemarks);
			}
			//html 增、改、列表、详情
			String addHtmlPath = f.getPath("add", ".html", list[i], backageName, fileName);
			if(!isExist(addHtmlPath)) {
				File dirAddHtmlFile = new File(addHtmlPath);
				f.createFile(dirAddHtmlFile);
				f.writeAddHtmlFile(dirAddHtmlFile, charset, list[i], fileName, tableRemarks);
			}
			
			String modifyHtmlPath = f.getPath("modify", ".html", list[i], backageName, fileName);
			if(!isExist(modifyHtmlPath)) {
				File dirModifyHtmlFile = new File(modifyHtmlPath);
				f.createFile(dirModifyHtmlFile);
				f.writeModifyHtmlFile(dirModifyHtmlFile, charset, list[i], fileName, tableRemarks);
			}
			
			String htmlPath = f.getPath("", ".html", list[i], backageName, fileName);
			if(!isExist(htmlPath)) {
				File dirHtmlFile = new File(htmlPath);
				f.createFile(dirHtmlFile);
				f.writeListHtmlFile(dirHtmlFile, charset, list[i], fileName, tableRemarks);
			}
			
			String detailHtmlPath = f.getPath("detail", ".html", list[i], backageName, fileName);
			if(!isExist(detailHtmlPath)) {
				File dirDetailHtmlFile = new File(detailHtmlPath);
				f.createFile(dirDetailHtmlFile);
				f.writeDetailHtmlFile(dirDetailHtmlFile, charset, list[i], fileName, tableRemarks);
			}
			//js 增、改、列表、详情
			String addJsPath = f.getPath("add", ".js", list[i], backageName, fileName);
			if(!isExist(addJsPath)) {
				File dirAddJsFile = new File(addJsPath);
				f.createFile(dirAddJsFile);
				f.writeAddJsFile(dirAddJsFile, charset, list[i]);
			}
			
			String modifyJsPath = f.getPath("modify", ".js", list[i], backageName, fileName);
			if(!isExist(modifyJsPath)) {
				File dirModifyJsFile = new File(modifyJsPath);
				f.createFile(dirModifyJsFile);
				f.writeModifyJsFile(dirModifyJsFile, charset, list[i]);
			}
			
			String jsPath = f.getPath("", ".js", list[i], backageName, fileName);
			if(!isExist(jsPath)) {
				File dirJsFile = new File(jsPath);
				f.createFile(dirJsFile);
				f.writeListJsFile(dirJsFile, charset, list[i], fileName);
			}
			
			String detailJsPath = f.getPath("detail", ".js", list[i], backageName, fileName);
			if(!isExist(detailJsPath)) {
				File dirDetailJsFile = new File(detailJsPath);
				f.createFile(dirDetailJsFile);
				f.writeDetailJsFile(dirDetailJsFile, charset, list[i]);
			}
			**/
		}		
	}
	
}