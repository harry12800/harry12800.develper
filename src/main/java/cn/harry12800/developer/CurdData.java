package cn.harry12800.developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.harry12800.tools.Maps;

public class CurdData {
	public String functionName = "";
	public String functionAuthor = "周国柱";
	public String functionVersion = "1.0";
	public String name = "";
	public String filePath = "";
	public String fileName = "";
	public String content = "";

	public String packagePath = "";
	public String packageName = "";
	public String moduleName = "";
	public String subModuleName = "";
	/**
	 * 类名
	 */
	public String ClassName = "";
	public String className = "";
	/**
	 * 数据库表名
	 */
	public String dbTableName;
	/**
	 * 数据库注释
	 */
	public String dbDesc;

	public List<String> importList = new ArrayList<>();
	public List<String> classDescList = new ArrayList<>();
	public Table table;

	public String toString() {
		return "Template [name=" + name + ", filePath=" + filePath
				+ ", fileName=" + fileName + ", ]";
	}

	public Map<Object, Object> toMap() {
		HashMap<Object, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("functionName", dbDesc);
		newHashMap.put("functionAuthor", functionAuthor);
		newHashMap.put("functionVersion", functionVersion);
		newHashMap.put("name", name);
		newHashMap.put("filePath", filePath);
		newHashMap.put("fileName", fileName);
		newHashMap.put("content", content);
		newHashMap.put("packageName", packageName);
		newHashMap.put("packagePath", packagePath);
		newHashMap.put("moduleName", moduleName);
		newHashMap.put("subModuleName", subModuleName);
		newHashMap.put("ClassName", ClassName);
		newHashMap.put("className", className);
		newHashMap.put("dbTableName", dbTableName);
		newHashMap.put("dbDesc", dbDesc);
		newHashMap.put("table", table);
		newHashMap.put("importList", importList);
		newHashMap.put("classDescList", classDescList);
		classDescList.add("代码自动生成!数据库的资源文件.");
		return newHashMap;
	}
}
