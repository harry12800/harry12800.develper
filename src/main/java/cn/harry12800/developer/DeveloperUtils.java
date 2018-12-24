package cn.harry12800.developer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import cn.harry12800.dbhelper.DBType;
import cn.harry12800.dbhelper.Db;
import cn.harry12800.dbhelper.MysqlHelper;
import cn.harry12800.dbhelper.OracleHelper;
import cn.harry12800.dbhelper.entity.DBTable;
import cn.harry12800.tools.EntityMent;
import cn.harry12800.tools.FileUtils;
import cn.harry12800.tools.MachineUtils;
import cn.harry12800.tools.StringUtils;
import cn.harry12800.tools.XmlUtils;

/**
 * 
 * @author harry12800
 * 
 */
public class DeveloperUtils {
	public static void main1(String[] args) throws Exception {
		//DeveloperUtils.clearAnnotation("F:/Workspaces/pssm/src");
		//		String url = "jdbc:mysql://127.0.0.1:3306/nytm";
		//		String user = "root";
		//		String password = "admin";
		String url = "jdbc:oracle:thin:@192.168.10.110:1521:orcl";
		String user = "nytm";
		String password = "nytm";
		List<DBTable> dbTable = DeveloperUtils.getDBTable(url, user, password);
		for (DBTable table : dbTable) {
			System.out.println(table.getCreateDDL(DBType.MYSQL));
			//	System.out.println(table.getCreateCommentDDL(DBType.MYSQL));
		}
	}

	/**
	 * @throws Exception 
	 */
	public static List<DBTable> getDBTable(String url, String userName, String pwd) throws Exception {
		OracleHelper oracleHelper = new OracleHelper();
		MysqlHelper mysqlHelper = new MysqlHelper();
		List<DBTable> tableDetail = null;
		if (url.contains("mysql")) {
			tableDetail = mysqlHelper.getTableDetail(url, userName, pwd);
		} else {
			tableDetail = oracleHelper.getTableDetail(url, userName, pwd);
		}
		return tableDetail;
	}

	/**
	 * 作者名称
	 */
	static final String author = "周国柱";
	public static String workSpacePath = "";
	public static String projectPath = "";
	static String projectName = "";
	static {
		workSpacePath = MachineUtils.getWorkSpacePath();
		projectPath = MachineUtils.getProjectPath();
		projectName = projectPath.substring(workSpacePath.length() + 1,
				projectPath.length());
	}

	public static String getProjectName() {
		return projectName;
	}

	/**
	 * 获得项目的输出路径
	 * @param projectPath 项目路径
	 * @return
	 * @throws Exception 
	 */
	static String getProjectOutputPath(String projectPath) throws Exception {
		try {
			List<String> output = XmlUtils.getNodeAttrValues(projectPath
					+ File.separator + ".classpath",
					"//classpathentry[@kind='output']", "path");
			for (String string : output) {
				return projectPath + File.separator + string;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new Exception("未找到项目的输出路径");
	}

	/**
	 * 将class文件移动到指定位置，按照class文件包的path路径层次
	 * 
	 * @param clas
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static boolean deployClass(String path, Class<?>... clazz)
			throws Exception {
		List<String> filename = DeveloperUtils.findProjectOutputPath(clazz);
		for (int i = 0; i < clazz.length; i++) {
			String packageName = clazz[i].getPackage().getName()
					.replaceAll("[.]", "/");// 把类名换成路径名
			// System.out.println(packageName);
			String dirPath = new File(path).getAbsolutePath() + File.separator
					+ packageName;
			System.out.println(dirPath);
			FileUtils.moveFile(filename.get(i), dirPath);
		}
		return true;
	}

	/**
	 * 移动代码
	 * 
	 * @param src
	 * @param des
	 * @param startRow
	 * @param endRow
	 * @param row
	 * @return
	 */
	public static boolean shiftCode(Class<?> src, Class<?> des, int startRow,
			int endRow, int row) {
		System.out.println(Thread.currentThread().getStackTrace()[1]);
		String srcName = src.getName();
		srcName = srcName.replaceAll("[.]", "/");// 把类名换成路径名
		String desName = des.getName();
		desName = desName.replaceAll("[.]", "/");// 把类名换成路径名

		Set<String> projectSourcePath = getProjectSrcPath();
		StringBuilder rowByFile = null;
		for (String path : projectSourcePath) {
			File file = new File(path + srcName + ".java");
			if (file.exists()) {
				rowByFile = FileUtils.getRowByFile(file, startRow, endRow);
				System.out.println("转移的代码：");
				System.out.println(rowByFile.toString());
				break;
			}
		}
		if (rowByFile == null)
			return false;
		for (String path : projectSourcePath) {
			File file = new File(path + desName + ".java");
			if (file.exists()) {
				FileUtils.appendContent(file.getAbsolutePath(),
						rowByFile.toString(), row);
				return true;
			}
		}
		return false;
	}

	/**
	 * 把src的java代码从startRow行到endrow 放到desjava文件的结尾
	 * 
	 * @param src
	 * @param des
	 * @param startRow
	 * @param endRow
	 * @return
	 */
	public static boolean shiftCode(Class<?> src, Class<?> des, int startRow,
			int endRow) {
		System.out.println(Thread.currentThread().getStackTrace()[1]);
		String srcName = src.getName();
		srcName = srcName.replaceAll("[.]", "/");// 把类名换成路径名
		String desName = des.getName();
		desName = desName.replaceAll("[.]", "/");// 把类名换成路径名

		Set<String> projectSourcePath = getProjectSrcPath();
		StringBuilder rowByFile = null;
		for (String path : projectSourcePath) {
			File file = new File(path + srcName + ".java");
			if (file.exists()) {
				rowByFile = FileUtils.getRowByFile(file, startRow, endRow);
				System.out.println("转移的代码：");
				System.out.println(rowByFile.toString());
				break;
			}
		}
		if (rowByFile == null)
			return false;
		for (String path : projectSourcePath) {
			File file = new File(path + desName + ".java");
			if (file.exists()) {
				String str = FileUtils.getSrcByFilePath(file.getAbsolutePath());
				int index = str.lastIndexOf("}");
				StringBuilder sd = new StringBuilder(str.substring(0, index));
				sd.append("\r\n").append(rowByFile.toString()).append("}");
				FileUtils.writeContent(file.getAbsolutePath(), sd.toString());
				return true;
			}
		}
		return false;
	}

	/**
	 * 将数据库的内容写入到c文件的第row行
	 * 
	 * @param c
	 * @param row
	 * @param sql
	 * @param isEllipsis
	 *            时候过长省略
	 */
	public static Set<String> getProjectSrcPath() {
		Set<String> path = new HashSet<>(0);
		path.addAll(findProjectSrcPath(".classpath"));
		return path;
	}

	/**
	 * 
	 * @param c
	 * @param row
	 * @param sql
	 * @param isEllipsis
	 * @throws Exception 
	 */
	public static void generateSqlDataCode(Class<?> c, int row, String sql,
			boolean isEllipsis) throws Exception {
		// 得到全类名
		String name = c.getName();
		System.out.println(name);
		System.out.println(Thread.currentThread().getStackTrace()[1]);
		// 把类名换成路径名
		name = name.replaceAll("[.]", "/");
		StringBuilder sb = new StringBuilder();
		List<?> list = OracleHelper.query(sql);
		for (int i = 0; i < list.size(); i++) {
			Object[] obj = (Object[]) list.get(i);
			for (int j = 0; j < obj.length; j++) {
				String str = obj[j].toString();
				if (isEllipsis)
					str = StringUtils.ellipsisMid(str, 64);
				sb.append("//").append(str).append("\t");
			}
			sb.append("\r\n");
		}
		Set<String> projectSourcePath = getProjectSrcPath();
		for (String path : projectSourcePath) {
			File file = new File(path + name + ".java");
			if (file.exists()) {
				FileUtils.appendContent(file.getAbsolutePath(), sb.toString(),
						row);
			}
		}
	}

	/**
	 * 给某一个java文件中的<code>startRow至endRow<code>行,首尾分别添加prefix，suffix
	 * 
	 * @param c
	 * @param prefix
	 * @param suffix
	 * @param startRow
	 * @param endRow
	 */
	public static void generateCodeSuffixPrefix(Class<?> c, String prefix,
			String suffix, int startRow, int endRow) {
		// 得到全类名
		String name = c.getName();
		System.out.println(name);
		System.out.println(Thread.currentThread().getStackTrace()[1]);
		// 把类名换成路径名
		name = name.replaceAll("[.]", "/");
		Set<String> projectSourcePath = getProjectSrcPath();
		for (String path : projectSourcePath) {
			File file = new File(path + name + ".java");
			if (file.exists()) {
				FileUtils.modifyLineContent(file.getAbsolutePath(), prefix,
						suffix, startRow, endRow);
			}
		}
	}

	/**
	 * <p>
	 * 给某一个java文件中的<code>pageRow<code>行,增加代码段
	 * <p>传入的map进行枚举,生成的代码如下
	 * <p><code> diyMap.put("122", 12.0);
	 * <p><code> diyMap.put("122",12.0);
	 * 
	 * @param map
	 *            需要被枚举的Map
	 * @param c
	 *            那个class文件
	 * @param mapName
	 *            实际的map名称 diyMap
	 * @param pageRow
	 *            第几行
	 */
	public static void generateMapCode(Map<?, ?> map, Class<?> c,
			String mapName, int pageRow) {
		// 得到全类名
		String name = c.getName();
		System.out.println(Thread.currentThread().getStackTrace()[1]);
		// 把类名换成路径名
		name = name.replaceAll("[.]", "/");
		StringBuffer sb = new StringBuffer();
		for (Entry<?, ?> e : map.entrySet()) {
			sb.append("\t\t");
			sb.append(mapName).append(".put(");
			if (e.getKey() instanceof Number) { // key是数字就不要加“”；
				sb.append(e.getKey());
			} else {
				sb.append("\"" + e.getKey() + "\"");
			}
			sb.append(",");
			if (e.getValue() instanceof Number) {// value是数字就不要加“”
				sb.append(e.getValue());
			} else {
				sb.append("\"" + e.getValue() + "\"");
			}
			sb.append(");\r\n");
		}
		if (!StringUtils.isEmpty(sb.toString().trim())) {
			Set<String> projectSourcePath = getProjectSrcPath();
			for (String path : projectSourcePath) {
				File file = new File(path + name + ".java");
				if (file.exists()) {
					FileUtils.appendContent(file.getAbsolutePath(),
							sb.toString(), pageRow);
				}
			}
		}

	}

	/**
	 * 移除该目录下的java文件中的注解/* 和//
	 * @param dirPath   文件夹路径
	 */
	public static void clearAnnotation(String dirPath) {
		File file = new File(dirPath);
		List<File> f = new ArrayList<File>(0);
		f.add(file);
		while (!f.isEmpty()) {
			File[] s = f.get(0).listFiles();
			for (File fi : s) {
				if (fi.isDirectory())
					f.add(fi);
				else if (fi.isFile()) {
					if (fi.getName().endsWith(".java")) {
						String content = FileUtils.getSrcByFilePath(fi
								.getAbsolutePath());
						if (content.contains("native")) {
							System.out.println(fi.getName());
							continue;
						}
						content = content.replaceAll("/\\*{1,2}[\\s\\S]*?\\*/",
								"").replaceAll("//[\\s\\S]*?\n", "");
						FileUtils.writeText(fi.getAbsolutePath(), content);
						// System.out.println(content);
					}
				}
			}
			f.remove(0);
		}
	}

	/**
	 * 查看哪些类没有注解自己的身份
	 * @param dirPath
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void notifyAddAnnotation(String dirPath)
			throws ClassNotFoundException, IOException {
		File file = new File(dirPath);
		List<File> f = new ArrayList<File>(0);
		f.add(file);
		ArrayList<String> fileList = new ArrayList<String>(0);
		while (!f.isEmpty()) {
			File[] s = f.get(0).listFiles();
			for (File fi : s) {
				if (fi.isDirectory())
					f.add(fi);
				else if (fi.isFile()) {
					if (fi.getName().endsWith(".java")) {
						String content = FileUtils.getSrcByFilePath(fi
								.getAbsolutePath());
						Pattern p = Pattern.compile("@author ");
						Matcher m = p.matcher(content);
						if (!m.find()) {
							fileList.add(fi.getName());
						}
					}
				}
			}
			f.remove(0);
		}
		System.out.println(fileList);
	}

	/**
	 * 给类生成getSet方法
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void generateGetSet(String dirPath)
			throws ClassNotFoundException, IOException {
		File file = new File(dirPath);
		List<File> f = new ArrayList<File>(0);
		f.add(file);
		while (!f.isEmpty()) {
			File[] s = f.get(0).listFiles();
			for (File fi : s) {
				if (fi.isDirectory())
					f.add(fi);
				else if (fi.isFile()) {
					if (fi.getName().endsWith(".java")) {
						List<String> tt = resolveJavaFile(fi);
						String content = modifyGetSet(tt, fi);
						FileUtils.writeText(fi.getAbsolutePath(), content);
					}
				}
			}
			f.remove(0);
		}
	}

	/**
	 * 给java 文件添加getSet方法
	 * 
	 * @return 加了getset方法的 java文件字符串
	 */
	private static String modifyGetSet(List<String> tt, File fi) {
		String content = FileUtils.getSrcByFilePath(fi.getAbsolutePath());
		for (int i = 0; i < tt.size(); i++) {
			String[] str = tt.get(i).split("-");
			Pattern p = Pattern.compile("(/\\*{1,2}[\\s\\S]*?\\*/)[\\s\\S]*?"
					+ str[0] + " " + str[1]);
			Matcher m = p.matcher(content);
			if (m.find()) {
			}
		}
		return content;
	}

	/**
	 * 得到这个java文件的所有属性，通过动态加载类的方式
	 * 
	 * @param fi
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static List<String> resolveJavaFile(File fi)
			throws ClassNotFoundException, IOException {
		String content = FileUtils.getSrcByFilePath(fi.getAbsolutePath());
		String simpleName = fi.getName().replace(".java", "");
		String className = getClassNameBySrc(content) + "." + simpleName;
		System.out.println("分析类:" + simpleName);
		JavaCompiler jc = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager manager = jc.getStandardFileManager(null, null,
				null);
		Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(fi
				.getAbsoluteFile());
		CompilationTask t = jc.getTask(null, manager, null, null, null, it);
		t.call();
		manager.close();
		URL[] urls = new URL[] { new URL("file:/E:/java/workspace/db_desc/src") };

		@SuppressWarnings("resource")
		URLClassLoader loader = new URLClassLoader(urls);
		Class<?> c = loader.loadClass(className);
		Field[] fs = c.getDeclaredFields();
		List<String> tt = new ArrayList<String>(0);
		for (Field field : fs) {
			// System.out.println(field.getName());
			int s = field.getType().toString().lastIndexOf('.');
			String type = field.getType().toString().substring(s + 1);
			tt.add(type + "-" + field.getName());
			// System.out.println(field.getType());
			// System.out.println(field.getGenericType());
		}
		return tt;
	}

	/***
	 * 得到类全名
	 * @param javaCode
	 *            java文件的字符串内容
	 * @return
	 */
	private static String getClassNameBySrc(String javaCode) {
		Pattern p = Pattern.compile("package(.*?);");
		Matcher m = p.matcher(javaCode);
		if (m.find()) {
			return m.group(1).trim();
		}
		return null;
	}

	/**
	 * 在包名下生成一个D版Resource的Java文件。
	 * 
	 * @param packageName
	 *            包名
	 * @param url
	 *            数据库的url
	 * @param user
	 *            数据库实例
	 * @param pwd
	 *            数据库实例密码
	 */
	public static void generateDbUtils(String packageName, String url,
			String user, String pwd) {
		Db db = new OracleHelper();
		Map<String, List<String>> map = null;
		Map<String, String> comments = null;
		try {
			map = db.getTableAndColumns(url, user, pwd);
			comments = db.getTableComments(url, user, pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuilder content = new StringBuilder();

		content.append("package " + packageName + "; \r\n\r\n");
		addClassAnnotation(content, "代码自动生成!数据库的资源文件.", url, user, pwd);
		content.append("public class DBResource {\r\n\r\n");
		for (String str : map.keySet()) {
			content.append("\t//");
			for (int i = 0; i < map.get(str).size(); i++) {
				content.append("?,");
			}
			content.deleteCharAt(content.length() - 1).append("\";\r\n");
			content = addAttrAnnotation(content, comments.get(str));

			content.append("\tpublic static final String ").append(str)
					.append("=\"");
			for (String col : map.get(str)) {
				content.append(col).append(",");
			}
			content.deleteCharAt(content.length() - 1).append("\";\r\n");

		}
		content.append("}");
		packageName = packageName.replaceAll("[.]", "/");
		FileUtils.writeContent(System.getProperty("user.dir") + "/src/"
				+ packageName + "/DBResource.java", content.toString());
	}

	/**
	 * 给方法或者属性上加注释
	 * 
	 * @param StringBuilder
	 *            给这个StringBuilder中加入一个完整的注释
	 * @param text
	 * @return StringBuilder
	 */
	public static StringBuilder addAttrAnnotation(StringBuilder str, String text) {

		if (StringUtils.isNull(text)) {
			str.append("\r\n");
			return str;
		}
		str.append("\t/**\r\n");
		String[] t = StringUtils.getStringList(text);
		for (String t1 : t) {
			if (!StringUtils.isNull(t1))
				str.append("\t * ").append(t1).append("\r\n");
		}
		str.append("\t */\r\n");
		return str;
	}

	/**
	 * 给类上加注解
	 * 
	 * @param str
	 * @param text
	 * @return
	 */
	public static StringBuilder addClassAnnotation(StringBuilder str,
			String... text) {
		if (StringUtils.isNull(text)) {
			str.append("\r\n");
			return str;
		}
		str.append("/**\r\n");
		str.append(" * ").append("@author:" + author).append("\r\n");
		for (String string : text) {
			String[] t = StringUtils.getStringList(string);
			for (String t1 : t) {
				if (!StringUtils.isNull(t1))
					str.append(" * <p>").append(t1).append("\r\n");
			}
		}
		str.append(" */\r\n");
		return str;
	}

	public static Builder createBuilder() {
		return new Builder();
	}

	public static class Builder {
		String basePackage;
		String moduleName;
		String url;
		String user;
		String pwd;
		String tableName;
		String databaseName;

		public Builder setBasePackage(String basePackage) {
			this.basePackage = basePackage;
			return this;
		}

		public Builder setModuleName(String moduleName) {
			this.moduleName = moduleName;
			return this;
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setUser(String user) {
			this.user = user;
			return this;
		}

		public Builder setPwd(String pwd) {
			this.pwd = pwd;
			return this;
		}

		public Builder setTableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public void build() {
			String[] split = tableName.split(",");
			for (String tableName : split) {
				generateDbEntityByTableNameUseFreemarker(basePackage, moduleName, url, user, pwd,databaseName, tableName);
			}
		}

		public Builder setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
			return this;
		}
	}
	public static void generateDescFile(
			String url,
			String user,
			String pwd
			) {
		Db db;
		if (url.contains("mysql")) {
			db = new MysqlHelper();
		} else
			db = new OracleHelper();
		try {
			db.generateDescFile(url, user, pwd, getProjectPath()+"/src/main/resources/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getProjectPath() {
		String workspace = System.getProperty("user.dir");
		workspace = workspace.replaceAll("\\\\", "/");
		return workspace;
	}
	/**
	 * 根据模板生成java文件。entity，dao，service，web。mapper.xml;
	 * @param url
	 * @param user
	 * @param pwd
	 * @param tableName
	 */
	public static void generateDbEntityByTableNameUseFreemarker(String basePackage,
			String moduleName,
			String url,
			String user,
			String pwd,
			String databaseName,
			String tableName) {
		Db db;
		if (url.contains("mysql")) {
			db = new MysqlHelper();
		} else
			db = new OracleHelper();
		List<DBTable> tableDetail = null;
		try {
			tableDetail = db.getTableDetail(url, user, pwd);
			for (DBTable table : tableDetail) {
				if (!table.getName().equalsIgnoreCase(tableName))
					continue;
				CurdData curdData = createCurdData(table);
				curdData.packageName = basePackage;
				curdData.packagePath = basePackage.replaceAll("[.]", "/");
				curdData.moduleName = moduleName;
				curdData.databaseName = databaseName;
				curdData.classDescList.add(url);
				curdData.classDescList.add(user);
				curdData.classDescList.add(pwd);
				curdData.classDescList.add("代码自动生成!数据库的资源文件.");
				GenEntity.gen(curdData);
				GenDao.gen(curdData);
				GenController.gen(curdData);
				GenService.gen(curdData);
				GenMybatisXml.gen(curdData);
				GenView.gen(curdData);
				GenWebDto.gen(curdData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据模板生成java文件。entity，dao，service，web。mapper.xml;
	 * @param url
	 * @param user
	 * @param pwd
	 * @param tableName
	 */
	public static void generateAllDbEntityByTableNameUseFreemarker(String basePackage,
			String moduleName,
			String url,
			String user,
			String pwd,
			String databaseName) {
		Db db;
		if (url.contains("mysql")) {
			db = new MysqlHelper();
		} else
			db = new OracleHelper();
		List<DBTable> tableDetail = null;
		try {
			tableDetail = db.getTableDetail(url, user, pwd);
			for (DBTable table : tableDetail) {
				CurdData curdData = createCurdData(table);
				curdData.packageName = basePackage;
				curdData.packagePath = basePackage.replaceAll("[.]", "/");
				curdData.moduleName = moduleName;
				curdData.databaseName = databaseName;
				curdData.classDescList.add(url);
				curdData.classDescList.add(user);
				curdData.classDescList.add(pwd);
				curdData.classDescList.add("代码自动生成!数据库的资源文件.");
				GenEntity.gen(curdData);
				if(curdData.table.keyFields.size() == 1){
					GenDao.gen(curdData);
					GenController.gen(curdData);
					GenService.gen(curdData);
					GenMybatisXml.gen(curdData);
					GenView.gen(curdData);
					GenWebDto.gen(curdData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CurdData createCurdData(DBTable table) {
		CurdData curdData = new CurdData();
		curdData.dbTableName = table.getName();
		curdData.ClassName = EntityMent.columnName2EntityClassName(table.getName());
		char[] cs = curdData.ClassName.toCharArray();
		cs[0] += 32;
		curdData.className = String.valueOf(cs);
		curdData.dbDesc = table.getComment();
		curdData.fileName = curdData.ClassName + ".java";
		curdData.table = new Table(table);
		return curdData;
	}

	private static Set<String> findProjectSrcPath(String classPath,
			String projectPath) {
		Set<String> path = new HashSet<>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String string : nodeAttrValues) {
				if (string.startsWith("/")) {
					path.addAll(findProjectSrcPath(workSpacePath + string
							+ "/.classpath", workSpacePath + string));
				} else {
					path.add(projectPath + "/" + string + "/");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	private static Set<String> findProjectSrcPath(String classPath) {
		Set<String> path = new HashSet<>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String string : nodeAttrValues) {
				if (string.startsWith("/")) {
					path.addAll(findProjectSrcPath(workSpacePath + string
							+ "/.classpath", workSpacePath + string));
				} else {
					path.add(projectPath + "/" + string + "/");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 
	 * @param classPath
	 * @param recursion 递归
	 * @return
	 */
	private static Set<String> findProjectSrcPath(String projectPath, String classPath, boolean recursion) {
		Set<String> path = new HashSet<>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String srcpath : nodeAttrValues) {
				if (srcpath.startsWith("/") && recursion) {
					path.addAll(findProjectSrcPath(workSpacePath + srcpath
							+ "/.classpath", workSpacePath + srcpath));
				}
				if (!srcpath.startsWith("/")) {
					path.add(projectPath + "/" + srcpath + "/");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public static List<String> findProjectOutputPath(Class<?>... clazz)
			throws Exception {
		System.out.println(Thread.currentThread().getStackTrace()[1]);
		Set<String> findProjectOutputPath = findProjectOutputPath();
		System.out.println(findProjectOutputPath);
		List<String> classpath = new ArrayList<String>(0);
		for (Class<?> cla : clazz) {
			String srcName = cla.getName();
			srcName = srcName.replaceAll("[.]", "/");// 把类名换成路径名
			boolean mark = false;
			for (String path : findProjectOutputPath) {
				File file = new File(path + srcName + ".class");
				if (file.exists()) {
					classpath.add(file.getAbsolutePath());
					mark = true;
					break;
				}
			}
			if (!mark) {
				throw new Exception(cla.getName() + "：这个Class文件未找到！");
			}
		}
		return classpath;
	}

	/**
	 * 获取项目的class文件输出路径，以及关联项目的class文件输出路径
	 * 
	 * @return
	 */
	public static Set<String> findProjectOutputPath() {
		Set<String> path = new HashSet<>(0);
		path.addAll(findProjectOutputPath(".classpath"));
		return path;
	}

	/**
	 * 获取项目的class文件输出路径
	 * 
	 * @param classPath
	 * @return
	 */
	private static Set<String> findProjectOutputPath(String classPath) {
		Set<String> path = new HashSet<>(0);
		try {
			List<String> output = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='output']", "path");
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String string : nodeAttrValues) {
				if (string.startsWith("/")) {
					path.addAll(findProjectOutputPath(workSpacePath + string
							+ "/.classpath", workSpacePath + string));
				}
			}
			for (String string : output) {
				path.add(projectPath + "/" + string + "/");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 获取项目的class文件输出路径
	 * 不递归
	 * @param classPath
	 * @return
	 */
	public static String findProjectOutputPathByName(String projectName) {
		String classPath = workSpacePath + File.separator + projectName + File.separator + ".classpath";
		try {
			List<String> output = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='output']", "path");
			for (String string : output) {
				return workSpacePath + File.separator + projectName + "/" + string + "/";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取项目的class文件输出路径
	 * 不递归
	 * @param classPath
	 * @return
	 */
	private static String findProjectOutputPathByName(String projectPath, String classPath) {
		try {
			List<String> output = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='output']", "path");
			for (String string : output) {
				return projectPath + "/" + string;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param classPath
	 * @return
	 */
	public static List<String> findAssociateProjectName(String classPath) {
		List<String> path = new ArrayList<String>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String projectName : nodeAttrValues) {
				if (projectName.startsWith("/")) {
					path.add(projectName.substring(1));
					path.addAll(findAssociateProjectName(workSpacePath + projectName
							+ "/.classpath"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public static List<String> findProjectLibPathByName(String projectPath, String classPath) {
		List<String> libPath = new ArrayList<String>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='lib']", "path");
			for (String path : nodeAttrValues) {
				libPath.add(projectPath + File.separator + path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return libPath;
	}

	/**
	 * 获取项目的class文件输出路径
	 * 
	 * @param classPath
	 * @param projectPath
	 * @return
	 */
	private static Set<String> findProjectOutputPath(String classPath,
			String projectPath) {
		Set<String> path = new HashSet<String>(0);
		try {
			List<String> output = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='output']", "path");
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String string : nodeAttrValues) {
				if (string.startsWith("/")) {
					path.addAll(findProjectOutputPath(workSpacePath + string
							+ "/.classpath", workSpacePath + string));
				}
			}
			for (String string : output) {
				path.add(projectPath + "/" + string + "/");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	static class Project {
		public String name;
		public String projectPath;
		public String classpath;
		public boolean isLoad = false;
		public Set<String> srcPathList = new HashSet<String>(0);
		public List<String> libPathList;
		public String outputPath;
		public List<Project> associateProtectList = new ArrayList<Project>(0);

		public Project(String name) {
			this.name = name;
			initDetails();
		}

		private void initDetails() {
			projectPath = DeveloperUtils.workSpacePath + File.separator + name;
			classpath = projectPath + File.separator + ".classpath";
			srcPathList = findProjectSrcPath(projectPath, classpath, false);
			outputPath = findProjectOutputPathByName(projectPath, classpath);
			System.out.println("outputPath:" + classpath);
			libPathList = findProjectLibPathByName(projectPath, classpath);
			List<String> associateProjectName = findAssociateProjectName(classpath);
			for (String name : associateProjectName) {
				Project project = ProjectFactory.GetInstance().createProject(name);
				if (!associateProtectList.contains(project))
					associateProtectList.add(project);
			}

		}

		@Override
		public String toString() {
			return projectPath;
		}
	}

	static class ProjectFactory {
		static ProjectFactory instance = null;

		private ProjectFactory() {
		}

		static public synchronized ProjectFactory GetInstance() {
			if (instance == null)
				instance = new ProjectFactory();
			return instance;
		}

		Map<String, Project> map = new HashMap<String, DeveloperUtils.Project>(0);

		Project createProject(String name) {
			if (map.get(name) != null) {
				return map.get(name);
			} else {
				Project project = new Project(name);
				map.put(name, project);
				return project;
			}
		}
	}

	/**
	 * 获取一个项目的src路径
	 * @param projectName
	 * @return
	 */
	public static Set<String> getProjectSrcPath(String projectName) {
		String projectPath = workSpacePath + File.separator + projectName;
		String claspath = projectPath + File.separator + ".classpath";
		return findProjectSrcPath(projectPath, claspath, false);
	}

	/**
	 * 获取一个项目的src路径
	 * @param projectName
	 * @return
	 */
	public static Set<String> getProjectSrcPath(String projectName, boolean recursion) {
		String projectPath = workSpacePath + File.separator + projectName;
		String claspath = projectPath + File.separator + ".classpath";
		return findProjectSrcPath(projectPath, claspath, recursion);
	}
}
