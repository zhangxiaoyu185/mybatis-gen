package com.xiaoyu.sql;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * poi实现对word操作
 * @author 张小鱼
 */
public class WordUtilByPoi {

	/**
	 * 从数据库中读取数据插入到word相应位置
	 * @param filePath word模板路径和名称
	 * @param outPath 保存后的路径
	 * @param map 数据库中的数据
	 */
	public static void readwriteWord(String filePath, String outPath, Map<String, String> map) {
		// 读取word模板
		FileInputStream in = null;
		try {
			in = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		HWPFDocument hdt = null;
		try {
			hdt = new HWPFDocument(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 读取word文本内容
		Range range = hdt.getRange();
		System.out.println(range.text());
		// 替换文本内容
		for (Map.Entry<String, String> entry : map.entrySet()) {
			range.replaceText("$" + entry.getKey() + "$", entry.getValue());
		}
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		String fileName = "" + System.currentTimeMillis();
		fileName += ".doc";
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outPath + fileName, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			hdt.write(ostream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 输出字节流
		try {
			out.write(ostream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库中读取数据插入到word相应位置
	 * @param response 响应,设置生成的文件类型,文件头编码方式和文件名,以及输出
	 * @param filePath word模板路径和名称
	 * @param map 数据库中的数据
	 */
	public static void readwriteWord(HttpServletResponse response,
			String filePath, Map<String, String> map) {
		// 读取word模板文件
		FileInputStream in;
		HWPFDocument hdt = null;
		try {
			in = new FileInputStream(new File(filePath));
			hdt = new HWPFDocument(in);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 替换读取到的word模板内容的指定字段
		Range range = hdt.getRange();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			range.replaceText("$" + entry.getKey() + "$", entry.getValue());
		}
		// 输出word内容文件流，提供下载
		response.reset();
		response.setContentType("application/x-msdownload");
		String fileName = "" + System.currentTimeMillis() + ".doc";
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		OutputStream servletOS = null;
		try {
			servletOS = response.getOutputStream();
			hdt.write(ostream);
			servletOS.write(ostream.toByteArray());
			servletOS.flush();
			servletOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读word
	 * @param filePath word模板路径和名称
	 * @param outPath 保存后的路径
	 */
	public static List<Map<String, Object>> readWord(String filePath) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		// 读取word模板
		FileInputStream in = null;
		try {
			in = new FileInputStream(new File(filePath)); // 载入文档
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}		
		HWPFDocument hdt = null;
		try {
			POIFSFileSystem pfs = new POIFSFileSystem(in);
			hdt = new HWPFDocument(pfs);// 得到文档的读取范围
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 读取word文本内容
		Range range = hdt.getRange();
		String strText = range.text();
		while(strText.endsWith("\r")){
			strText = strText.substring(0, strText.length()-2);
		}
		//System.out.println(strText);
		String[] rangeList = strText.split("\r");
		String[] strName = new String[rangeList.length];
		String[] tableName = new String[rangeList.length];
		for(int y =0;y<rangeList.length;y++){
			strName[y] = rangeList[y].substring(0, rangeList[y].indexOf("-")).replaceAll(" ", "");
			tableName[y] = rangeList[y].substring(rangeList[y].indexOf("-")+1,rangeList[y].indexOf("表")+1).trim();
			//System.out.println(strName[y]);
			//System.out.println(tableName[y]);
		}
		//读word中表格
		try {
			TableIterator it = new TableIterator(range);
			String[][] strList = null;
			int x = 0;
			// 迭代文档中的表格
			while (it.hasNext()) {
				Table tb = (Table) it.next();
				strList = new String[tb.numRows()-1][5];
				// 迭代行，默认从0开始
				for (int i = 1; i < tb.numRows(); i++) {
					TableRow tr = tb.getRow(i);
					// 迭代列，默认从0开始
					for (int j = 0; j < tr.numCells(); j++) {
						TableCell td = tr.getCell(j);// 取得单元格
						// 取得单元格的内容
						for (int k = 0; k < td.numParagraphs(); k++) {
							Paragraph para = td.getParagraph(k);
							String s = para.text().replace("", "");
							//System.out.print("\t"+s.replaceAll(" ", ""));
							strList[i-1][j] = s.replaceAll(" ", "");
						}
					}
					//System.out.println();
				}
				map = new HashMap<String, Object>();
				map.put("tableName", strName[x]);
				map.put("name", tableName[x]);
				//strList去最后的空处理
				int cleanspace = 0;
				for(int i = 0; i<strList.length; i++) {
					if(strList[i][0] == null) {
						cleanspace++;
					}
					//System.out.println(strList[i][0]);
				}
				//System.out.println(cleanspace);
				if(cleanspace > 0 ) {
					String[][] newStrList = new String[strList.length - cleanspace][strList[0].length];
					int ij = 0;
					for(int j = 0; j<strList.length; j++) {
						if(strList[j][0] == null) {
							continue;
						}else {
							newStrList[ij] = strList[j];
							ij++;
						}
					}
					map.put("data", newStrList);
				}else {
					map.put("data", strList);
				}
				listMap.add(map);
				x++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listMap;
	}
	
	/**
	 * 写word
	 * @param outPath 保存后的路径
	 */
	public static void writeWord(String outPath, String strText, String strName) {
		try { 
			if (!"".equals(outPath)) { 
				File fileDir = new File(outPath); 
				if (fileDir.exists()) { 
					byte b[] = strText.getBytes("UTF-8"); 
					ByteArrayInputStream bais = new ByteArrayInputStream(b); 
					POIFSFileSystem poifs = new POIFSFileSystem(); 
					DirectoryEntry directory = poifs.getRoot(); 
					directory.createDocument("WordDocument", bais); 
					FileOutputStream ostream = new FileOutputStream(outPath + strName + ".doc"); 
					poifs.writeFilesystem(ostream); 
					bais.close(); 
					ostream.close(); 
				} 
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	
	public static void main(String[] args) {
		readWord("D:\\suspense.doc");
		//writeWord("D:\\", readWord("D:\\1.doc").toString(), "1");
	}
}
