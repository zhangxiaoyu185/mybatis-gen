package com.xiaoyu.creator;

import com.xiaoyu.db.TableInfo;
import com.xiaoyu.util.FileIOUtil;
import com.xiaoyu.util.StringUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCreator {
	public Map<String, Object> params = new HashMap<>();

	public void createJavaBean(TableInfo tableinfo) {
		params.put("tableinfo", tableinfo);
		JavaBeanParam param = configBeanParam();
		createJavaBean(param.getTemplateName(), param.getSuffix(), param.getFileNameSuffix(),
				param.isUseCamelCaseForFileName(), param.getPackageSuffix());
	}

	private void createJavaBean(String templateName, String suffix, String fileNameSuffix,
			boolean useCamelCaseForFileName, String packageSuffix) {
		TableInfo tableinfo = (TableInfo) params.get("tableinfo");
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("input.encoding", "utf-8");
		ve.setProperty("output.encoding", "utf-8");
		ve.init();
		StringBuffer path = new StringBuffer();
		path.append("src/main/resources/" + templateName);
		Template t = ve.getTemplate(path.toString());
		VelocityContext ctx = new VelocityContext();
		ctx.put("tableinfo", tableinfo);
		ctx.put("stringUtil", new StringUtil());
		StringWriter tmp = new StringWriter();
		// 转换输出, 去除velocity代码缩进的空格符
		//System.out.println(t.getData().toString());
		//ve.evaluate(ctx, tmp, "", t.getData().toString().replaceAll("[( )*]", ""));
		//模板合并，得到期望文件
		t.merge(ctx, tmp);
		FileIOUtil.createFile(
				(useCamelCaseForFileName ? StringUtil.firstCharUpcase(tableinfo.getTableNameFt()) : tableinfo.getTableName()) + fileNameSuffix + suffix,
				packageSuffix, tmp.toString());
	}

	public abstract AbstractCreator.JavaBeanParam configBeanParam();

	public static class JavaBeanParam {

		private String templateName;

		private String suffix = ".java";

		private String fileNameSuffix = "";

		// 默认使用CamelCase的文件命名
		private boolean useCamelCaseForFileName = true;

		private String packageSuffix = "";

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		public String getFileNameSuffix() {
			return fileNameSuffix;
		}

		public void setFileNameSuffix(String javaBeanSuffix) {
			this.fileNameSuffix = javaBeanSuffix;
		}

		public boolean isUseCamelCaseForFileName() {
			return useCamelCaseForFileName;
		}

		public void setUseCamelCaseForFileName(boolean useCamelCaseForFileName) {
			this.useCamelCaseForFileName = useCamelCaseForFileName;
		}

		public String getPackageSuffix() {
			return packageSuffix;
		}

		public void setPackageSuffix(String packageSuffix) {
			this.packageSuffix = packageSuffix;
		}
	}
}
