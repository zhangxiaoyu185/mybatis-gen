package com.xiaoyu.util;

import com.xiaoyu.pt.PTJdbcByPropertiesUtil;

import java.io.*;
import java.util.Properties;

public class FileIOUtil {

	public static Writer getWriter(String name, String packageSuffix) throws IOException {
		Properties pros = JdbcByPropertiesUtil.getInstance().getConfigProperties();
		String packageName = pros.getProperty("package");
		StringBuffer path = new StringBuffer();
		packageName = packageName.replace(".", File.separator);
		packageSuffix = packageSuffix.replace(".", File.separator);
		path.append("src/main/java/").append(packageName).append(File.separator);
		if (packageSuffix.length() > 0)
			path.append(packageSuffix).append(File.separator);
		path.append(File.separator).append(name);
		File file = new File(path.toString());
		File dir = new File(file.getParent());
		if (!dir.exists())
			dir.mkdirs();
		if (file.exists())
			file.delete();
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}

	public static Writer ptGetWriter(String name, String packageSuffix) throws IOException {
		Properties pros = PTJdbcByPropertiesUtil.getInstance().getConfigProperties();
		String packageName = pros.getProperty("package");
		StringBuffer path = new StringBuffer();
		packageName = packageName.replace(".", File.separator);
		packageSuffix = packageSuffix.replace(".", File.separator);
		path.append(pros.getProperty("fileDir")).append("src/main/java/");
		path.append(packageName).append(File.separator);
		if (packageSuffix.length() > 0)
			path.append(packageSuffix).append(File.separator);
		path.append(File.separator).append(name);
		File file = new File(path.toString());
		File dir = new File(file.getParent());
		if (!dir.exists())
			dir.mkdirs();
		if (file.exists())
			file.delete();
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}

	public static Writer htmlGetWriter(String fileName, String name, String packageSuffix) throws IOException {
		Properties pros = PTJdbcByPropertiesUtil.getInstance().getConfigProperties();
		StringBuffer path = new StringBuffer();
		packageSuffix = packageSuffix.replace(".", File.separator);
		path.append(pros.getProperty("fileDir")).append("src/main/webapp/views/common/");
		path.append(fileName).append(File.separator);
		if (packageSuffix.length() > 0)
			path.append(packageSuffix).append(File.separator);
		path.append(name);
		File file = new File(path.toString());
		File dir = new File(file.getParent());
		if (!dir.exists())
			dir.mkdirs();
		if (file.exists())
			file.delete();
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}

	public static Writer jsGetWriter(String fileName, String name, String packageSuffix) throws IOException {
		Properties pros = PTJdbcByPropertiesUtil.getInstance().getConfigProperties();
		StringBuffer path = new StringBuffer();
		packageSuffix = packageSuffix.replace(".", File.separator);
		path.append(pros.getProperty("fileDir")).append("src/main/webapp/views/common/js/");
		path.append(fileName).append(File.separator);
		if (packageSuffix.length() > 0)
			path.append(packageSuffix).append(File.separator);
		path.append(name);
		File file = new File(path.toString());
		File dir = new File(file.getParent());
		if (!dir.exists())
			dir.mkdirs();
		if (file.exists())
			file.delete();
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}

	public static void createFile(String name, String packageSuffix, String content) {
		try {
			Writer writer = getWriter(name, packageSuffix);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ptCreateFile(String name, String packageSuffix, String content) {
		try {
			Writer writer = ptGetWriter(name, packageSuffix);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void htmlCreateFile(String fileName, String name, String packageSuffix, String content) {
		try {
			Writer writer = htmlGetWriter(fileName, name, packageSuffix);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void jsCreateFile(String fileName, String name, String packageSuffix, String content) {
		try {
			Writer writer = jsGetWriter(fileName, name, packageSuffix);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readTemplate(String name) {
		StringBuffer path = new StringBuffer();
		path.append(Thread.currentThread().getContextClassLoader().getResource("").getPath());
		path.append(name);// 打包时设置
		BufferedReader reader = null;
		StringBuffer content = new StringBuffer();
		try {
			String str = null;
			// 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"));
			while ((str = reader.readLine()) != null)
				content.append(str).append("\n");
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
		return content.toString();
	}

}
