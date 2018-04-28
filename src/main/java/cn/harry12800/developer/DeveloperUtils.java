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
import cn.harry12800.dbhelper.entity.DBField;
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
		for (  DBTable table  : dbTable) {
			System.out.println(table.getCreateDDL(DBType.MYSQL));
		//	System.out.println(table.getCreateCommentDDL(DBType.MYSQL));
		}
	}
	/**
	 * @throws Exception 
	 */
	public static List<DBTable> getDBTable(String url,String userName,String pwd) throws Exception {
		OracleHelper oracleHelper= new OracleHelper();
		MysqlHelper mysqlHelper= new MysqlHelper();
		List<DBTable> tableDetail = null;
		if(url.contains("mysql")){
			 tableDetail = mysqlHelper.getTableDetail(url,userName,pwd);
		}else{
			 tableDetail = oracleHelper.getTableDetail(url,userName,pwd);
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
						if(content.contains("native")) {
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
	private static String getClassNameBySrc(String javaCode
			) {
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
	public static void main(String[] args) throws Exception {
		String url="jdbc:mysql://127.0.0.1:3306/nytm";
		String user="root";
		String pwd="admin";
		String tableName="Person_Application";
		DeveloperUtils.generateDbEntityByTableName( url, user, pwd, tableName);
}
	/**
	 * 根据模板生成java文件。entity，dao，service，web。mapper.xml;
	 * @param url
	 * @param user
	 * @param pwd
	 * @param tableName
	 */
	public static void generateDbEntityByTableName(String url, String user,
			String pwd, String tableName) {
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
				CurdData curdData= createCurdData(table);
				curdData.classDescList.add(url);
				curdData.classDescList.add(user);
				curdData.classDescList.add(pwd);
				GenEntity.gen(curdData);
				GenDao.gen(curdData);
				GenService.gen(curdData);
				GenMybatisXml.gen(curdData);
				GenView.gen(curdData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CurdData createCurdData(DBTable table) {
		CurdData curdData = new CurdData();
		curdData.dbTableName = table.getName();
		curdData.ClassName = EntityMent.columnName2EntityClassName(table.getName());
        char[] cs=curdData.ClassName.toCharArray();
        cs[0]+=32;
        curdData.className= String.valueOf(cs);
		curdData.dbDesc = table.getComment();
		curdData.packageName="cn/harry12800";
		curdData.moduleName="scan";
		curdData.fileName=curdData.ClassName+".java";
		curdData.table = new Table(table);
		return curdData;
	}
	/**
	 * 生成数据库的实体 bean，Mapper，xml（mybatis）
	 * @param packageName  需要被设置的包名；如“com.newtec.tree2word.test”
	 * @param url 如："jdbc:oracle:thin:@121.201.65.19:1521:mesdb"
	 * @param user  数据库实例的用户名"pharm"
	 * @param pwd  数据库实例的密码""pharm"
	 */
	public static void generateDbEntityByTableName(String[] packageNames, String url,
			String user, String pwd,String tableName) {
		Db db;
		String packageName = packageNames[0];
		String mapperPackageName = null;
		if(packageNames.length>1)
			mapperPackageName = packageNames[1];
		if(url.contains("mysql")){
			  db = new MysqlHelper();
		}
		else
		  db = new OracleHelper();
		List<DBTable> tableDetail = null;
		try {
			tableDetail = db.getTableDetail(url, user, pwd);
			for (  DBTable  table : tableDetail) {
				if(!table.getName().equalsIgnoreCase(tableName)) continue;
				String clazzName = table.getName();
				clazzName = EntityMent.columnName2EntityClassName(clazzName);
				String tbDesc = table.getComment();
				StringBuilder content = new StringBuilder();
				content.append("package " + packageName + "; \r\n\r\n");
				addClassAnnotation(content, "代码自动生成!数据库的资源文件.", url, user, pwd,
						tbDesc);
				
				content.append("public class " + clazzName + " {\r\n\r\n");
				/**
				 * 属性定义
				 */
				for (DBField field : table.getFields()) {
					String comments = field.getComment();
					content = addAttrAnnotation(content, comments);
					String name = field.getName();
					name = EntityMent.columnName2EntityAttrName(name);
					name = name.replaceAll(" ", "");
					String type =  field.getType();
					type = EntityMent.getDb2attrMap(type);
					if (type == null)
						type = "" + field.getType();
					if(field.isPrimaryKey)
						content.append("\t@TableField(value=\"主键\",isKey=true,type=0,sort=0, title = \"主键\",show=false, canAdd = false, canEdit = false, dbFieldName = \""+field.getName()+"\")\r\n");
					else{
						content.append("\t@TableField(value=\""+field.getComment()+"\",type=1,sort=1, title =\""+field.getComment()+"\", exp=true,width=100,  canAdd = true, canEdit = false, canSearch = false, dbFieldName = \""+field.getName()+"\")\r\n");
					}
					if ("String".equals(type)) {
						content.append("\tprivate " + type + " ").append(name)
								.append("= \"\";\r\n");
					} else if ("Integer".equals(type)) {
						content.append("\tprivate " + type + " ").append(name)
								.append("= 0;\r\n");
					} else if ("Double".equals(type)) {
						content.append("\tprivate " + type + " ").append(name)
								.append("= 0.0;\r\n");
					} else {
						content.append("\tprivate " + type + " ").append(name)
								.append("= null;\r\n");
					}
				}

				/**
				 * getset方法添加
				 */
				for (DBField field : table.getFields()) {
					String comments =field.getComment();
					String name =  field.getName();
					name = EntityMent.columnName2EntityAttrName(name);
					name = name.replaceAll(" ", "");
					String type = field.getType();
					type = EntityMent.getDb2attrMap(type);
					if (type == null)
						type = field.getType();
					addGetSetMethod(content, name, type, comments);
				}
				content.append("\r\n}");
				String packageNamePath = packageName.replaceAll("[.]", "/");
				String mapperpackageNamePath =null;
				if(mapperPackageName!=null)
					mapperpackageNamePath = mapperPackageName.replaceAll("[.]", "/");
				String string= System.getProperty("user.dir") + "/src/"
				+ packageNamePath + "/" + clazzName + ".java";
				System.out.println(string);
				FileUtils.writeContent(string,
						content.toString());
				/**
				 * mybatis的附加信息，解决字段名称不对应的问题
				 */
				/**
				 * Mapper的附加信息，基本Mapper方法
				 */
				if(mapperPackageName!=null){
					createMapperJavaFile( mapperPackageName,packageName,clazzName, mapperpackageNamePath);
					createMapperXmlFile(table, mapperPackageName,packageName,clazzName, mapperpackageNamePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 生成数据库的实体 bean
	 * 
	 * @param packageName
	 *            需要被设置的包名；如“com.newtec.tree2word.test”
	 * @param url
	 *            如："jdbc:oracle:thin:@121.201.65.19:1521:mesdb"
	 * @param user
	 *            数据库实例的用户名"pharm"
	 * @param pwd
	 *            数据库实例的密码""pharm"
	 */
	public static void generateDbEntity(String[] packageNames, String url,
			String user, String pwd) {
		Db db;
		String packageName = packageNames[0];
		String mapperPackageName = null;
		if(packageNames.length>1)
			mapperPackageName = packageNames[1];
		if(url.contains("mysql")){
			  db = new MysqlHelper();
		}
		else
		  db = new OracleHelper();
		List<DBTable> tableDetail = null;
		try {
			tableDetail = db.getTableDetail(url, user, pwd);
			for (  DBTable  table : tableDetail) {
				String clazzName = table.getName();
				clazzName = EntityMent.columnName2EntityClassName(clazzName);
				String tbDesc = table.getComment();
				StringBuilder content = new StringBuilder();
				content.append("package " + packageName + "; \r\n\r\n");
				addClassAnnotation(content, "代码自动生成!数据库的资源文件.", url, user, pwd,
						tbDesc);
				content.append("public class " + clazzName + " {\r\n\r\n");
				/**
				 * 属性定义
				 */
				for (DBField field : table.getFields()) {
					String comments = field.getComment();
					content = addAttrAnnotation(content, comments);
					String name = field.getName();
					name = EntityMent.columnName2EntityAttrName(name);
					name = name.replaceAll(" ", "");
					String type =  field.getType();
					type = EntityMent.getDb2attrMap(type);
					if (type == null)
						type = "" + field.getType();
					if ("String".equals(type)) {
						content.append("\tprivate " + type + " ").append(name)
								.append("= \"\";\r\n");
					} else if ("Integer".equals(type)) {
						content.append("\tprivate " + type + " ").append(name)
								.append("= 0;\r\n");
					} else if ("Double".equals(type)) {
						content.append("\tprivate " + type + " ").append(name)
								.append("= 0.0;\r\n");
					} else {
						content.append("\tprivate " + type + " ").append(name)
								.append("= null;\r\n");
					}
				}

				/**
				 * getset方法添加
				 */
				for (DBField field : table.getFields()) {
					String comments =field.getComment();
					String name =  field.getName();
					name = EntityMent.columnName2EntityAttrName(name);
					name = name.replaceAll(" ", "");
					String type = field.getType();
					type = EntityMent.getDb2attrMap(type);
					if (type == null)
						type = field.getType();
					addGetSetMethod(content, name, type, comments);
				}
				content.append("\r\n}");
				String packageNamePath = packageName.replaceAll("[.]", "/");
				String mapperpackageNamePath =null;
				if(mapperPackageName!=null)
					mapperpackageNamePath = mapperPackageName.replaceAll("[.]", "/");
				FileUtils.writeContent(System.getProperty("user.dir") + "/src/"
						+ packageNamePath + "/" + clazzName + ".java",
						content.toString());
				/**
				 * mybatis的附加信息，解决字段名称不对应的问题
				 */
				/**
				 * Mapper的附加信息，基本Mapper方法
				 */
				if(mapperPackageName!=null){
					createMapperJavaFile( mapperPackageName,packageName,clazzName, mapperpackageNamePath);
					createMapperXmlFile(table, mapperPackageName,packageName,clazzName, mapperpackageNamePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * mybatis 完整信息
	 */
	private static void createMapperXmlFile(DBTable table,
			String mapperPackageName, String packageName, String clazzName,
			String mapperpackageNamePath) {
		StringBuilder mybatis = new StringBuilder();
		mybatis.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n")
			.append("<!DOCTYPE mapper\r\n")
			.append("PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\r\n")
					.append("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n")
		.append("<mapper namespace=\""+mapperPackageName+"."+clazzName+"Mapper\">\r\n");
		addFindById(mybatis,table,packageName,clazzName);
		addFindBySql(mybatis,table,packageName,clazzName);
		addFindAll(mybatis,table,packageName,clazzName);
		addSave(mybatis,table,packageName,clazzName);
		addUpdate(mybatis,table,packageName,clazzName);
		addDeleteByIds(mybatis,table,packageName,clazzName);
		mybatis.append("\t<resultMap type='" + packageName + "."
				+ clazzName + "' id='" + clazzName + "'>\r\n");
		for (DBField field : table.getFields()) {
			String dbColumnName = field.getName();
			String attrName = EntityMent
					.columnName2EntityAttrName(dbColumnName);
			attrName = attrName.replaceAll(" ", "");
			addMybatisXml(mybatis, dbColumnName, attrName);
		}
		mybatis.append("\t</resultMap>\r\n");
		mybatis.append("</mapper>");
		FileUtils.writeContent(System.getProperty("user.dir") + "/src/"
				+ mapperpackageNamePath + "/" + clazzName + "Mapper.xml",
				mybatis.toString());
	}
	private static void addFindBySql(StringBuilder mybatis, DBTable table,
			String packageName, String clazzName) {
		mybatis.append("\t<select id='findBySql' parameterType='java.lang.String'  resultMap='"+clazzName+"' >\r\n");
		mybatis.append("\t\t${value}\r\n");
		mybatis.append("\t</select>\r\n");
	}
	private static void addDeleteByIds(StringBuilder mybatis, DBTable table, String packageName, String clazzName) {
		mybatis.append("\t<delete id='deleteByIds' parameterType='java.lang.String'>\r\n");
		mybatis.append("\t\tdelete from "+table.getName()+" where  ${value}\r\n");
		mybatis.append("\t</delete>\r\n");
	}
	private static void addUpdate(StringBuilder mybatis, DBTable table,String packageName,String clazzName) {
		mybatis.append("\t<update id='update' parameterType='"+packageName+"."+clazzName+"'>\r\n");
		mybatis.append("\t\tupdate ").append( table.getName()).append(" set ").append(getMybatisSetAxies(table)).append(" where id=#{id}\r\n");
		mybatis.append("\t</update>\r\n");
	}
	private static Object getMybatisSetAxies(DBTable table) {
		List<String> allDBFieldName = table.getAllDBFieldName();
		List<String> allFieldName = table.getAllFieldName();
		List<String> list=new ArrayList<String>();
		for (int i = 0; i < allDBFieldName.size(); i++) {
			if(!"id".equals(allFieldName.get(i))){
				list.add(allDBFieldName.get(i)+"=#{"+allFieldName.get(i)+"}");
			}
		}
		return StringUtils.getCommaMerge(list);
	}
	private static void addSave(StringBuilder mybatis, DBTable table,String packageName,String clazzName) {
		mybatis.append("\t<insert id='save' parameterType='"+packageName+"."+clazzName+"'>\r\n");
		String string= StringUtils.getCommaMerge(table.getAllDBFieldName());
		mybatis.append("\t\tinsert into ").append( table.getName()).append("(").append(string).append(")\r\n"); 
		mybatis.append("\t\tvalues(").append(getMybatisAxies(table)).append(")\r\n");
		mybatis.append("\t</insert>\r\n");
	}
	
	private static Object getMybatisAxies(DBTable table) {
		List<String> allFieldName = table.getAllFieldName();
		List<String> list=new ArrayList<String>();
		for (String string : allFieldName) {
			list.add("#{"+string+"}");
		}
		return StringUtils.getCommaMerge(list);
	}
	private static void addFindAll(StringBuilder mybatis, DBTable table,String packageName,String clazzName) {
		mybatis.append("\t<select id='findAll' resultMap='"+clazzName+"' >\r\n");
		String string= StringUtils.getCommaMerge(table.getAllDBFieldName());
		mybatis.append("\t\tselect ").append(string).append(" from ").append(table.getName()).append("\r\n");
		mybatis.append("\t</select>\r\n");
	}
	private static void addFindById(StringBuilder mybatis, DBTable table,String packageName,String clazzName) {
		mybatis.append("\t<select id='findById' resultMap='"+clazzName+"'  parameterType='java.lang.String'>\r\n");
		String string= StringUtils.getCommaMerge(table.getAllDBFieldName());
		mybatis.append("\t\tselect ").append(string).append(" from ").append(table.getName()).append("  where id = #{id}\r\n");
		mybatis.append("\t</select>\r\n");
	}
	private static void createMapperJavaFile(String mapperPackageName
			,String packageName,String clazzName,String mapperpackageNamePath
			) {
		StringBuilder mapper = new StringBuilder();
		mapper.append("package "+mapperPackageName+";\r\n\r\n");
		mapper.append("import java.util.List;\r\n\r\n");
		mapper.append("import "+packageName+"."+clazzName+";\r\n\r\n");
		mapper.append("public interface ").append(clazzName+"Mapper extends  BaseMapper<"+clazzName+"> {\r\n");
		mapper.append("\tpublic "+clazzName+" findById(String id);\r\n");
		mapper.append("\tpublic List<"+clazzName+"> findAll() ;\r\n");
		mapper.append("\tpublic int save("+clazzName+" t);\r\n");
		mapper.append("\tpublic int update("+clazzName+" t);\r\n");
		mapper.append("\tpublic List<"+clazzName+"> findBySql(String sql);\r\n");
		mapper.append("\tpublic int deleteByIds(String ids);\r\n}");
		FileUtils.writeContent(System.getProperty("user.dir") + "/src/"
				+ mapperpackageNamePath + "/" + clazzName + "Mapper.java",
				mapper.toString());
	}
	/**
	 * 针对mybatis字段名与属性名对不上的情况下。
	 * 
	 * @param mybatis
	 * @param dbColumnName
	 * @param attrName
	 */
	private static void addMybatisXml(StringBuilder mybatis,
			String dbColumnName, String attrName) {
		mybatis.append("\t\t<result property='" + attrName + "' column='"
				+ dbColumnName + "'/>\r\n");
		// <resultMap type="com.nytm.entity.BusinessOrder" id="orderResultMap">
		// <!-- 用id属性来映射主键字段 -->
		// <id property="id" column="id"/>
		// <!-- 用result属性来映射非主键字段 -->
		// <result property="size" column="material_size"/>
		// <result property="number" column="req_count"/>
		// </resultMap>

	}

	/**
	 * 给StringBuilder添加getSet方法。
	 * 
	 * @param content
	 *            需要被添加的StringBuilder
	 * @param name
	 *            Java属性字段的名称
	 * @param type
	 *            Java属性字段的数据类型
	 * @param comments
	 *            提示信息
	 */
	private static void addGetSetMethod(StringBuilder content, String name,
			String type, String comments) {
		String sub = name.substring(0, 1).toUpperCase() + name.substring(1);

		if (StringUtils.isNull(comments)) {
			addAttrAnnotation(content, "获取" + name);
		} else {
			addAttrAnnotation(content, "获取" + comments);
		}
		content.append("\tpublic " + type + " get").append(sub)
				.append("() {\r\n");
		content.append("\t\treturn " + name + ";\r\n");
		content.append("\t}\r\n");
		if (StringUtils.isNull(comments)) {
			addAttrAnnotation(content, "设置" + name);
		} else {
			addAttrAnnotation(content, "设置" + comments);
		}
		content.append("\tpublic void set").append(sub)
				.append("(" + type + " " + name + ") {\r\n");
		content.append("\t\tthis." + name + "=" + name + ";\r\n");
		content.append("\t}\r\n");
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
	private static Set<String> findProjectSrcPath(String projectPath,String classPath,boolean recursion) {
		Set<String> path = new HashSet<>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='src']", "path");
			for (String srcpath : nodeAttrValues) {
				if (srcpath.startsWith("/")&&recursion) {
					path.addAll(findProjectSrcPath(workSpacePath + srcpath
							+ "/.classpath", workSpacePath + srcpath));
				}
				if(!srcpath.startsWith("/")){
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
		String classPath = workSpacePath+File.separator+projectName+File.separator+".classpath";
		try {
			List<String> output = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='output']", "path");
			for (String string : output) {
				return workSpacePath+File.separator+projectName + "/" + string + "/";
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
	private static String findProjectOutputPathByName(String projectPath,String classPath) {
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
	public static List<String> findAssociateProjectName(String classPath){
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
	public static List<String>  findProjectLibPathByName(String projectPath,String classPath){
		List<String> libPath = new ArrayList<String>(0);
		try {
			List<String> nodeAttrValues = XmlUtils.getNodeAttrValues(classPath,
					"//classpathentry[@kind='lib']", "path");
			for (String path : nodeAttrValues) {
				libPath.add(projectPath+File.separator+path);
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
		public Set<String> srcPathList =new HashSet<String>(0);
		public List<String> libPathList;
		public String outputPath;
		public List<Project> associateProtectList =new ArrayList<Project>(0);
		public Project(String name) {
			this.name = name;
			initDetails();
		}
		private void initDetails() {
			projectPath = DeveloperUtils.workSpacePath+File.separator+name;
			classpath = projectPath+File.separator+".classpath";
			srcPathList = findProjectSrcPath(projectPath,classpath,false);
			outputPath = findProjectOutputPathByName(projectPath,classpath);
			System.out.println("outputPath:"+classpath);
			libPathList = findProjectLibPathByName(projectPath,classpath);
			List<String> associateProjectName = findAssociateProjectName(classpath);
			for (String name : associateProjectName) {
				 Project project = ProjectFactory.GetInstance().createProject(name);
				 if(!associateProtectList.contains(project))
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
			if(instance == null)
				instance = new ProjectFactory();
			return instance;
		}
		Map<String,Project> map = new HashMap<String, DeveloperUtils.Project>(0);
		Project createProject(String name){
			if(map.get(name)!=null){
				return map.get(name);
			}
			else{
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
		String projectPath = workSpacePath+File.separator+projectName;
		String claspath =  projectPath +File.separator+".classpath";
		return findProjectSrcPath(projectPath, claspath, false);
	}
	/**
	 * 获取一个项目的src路径
	 * @param projectName
	 * @return
	 */
	public static Set<String> getProjectSrcPath(String projectName,boolean recursion) {
		String projectPath = workSpacePath+File.separator+projectName;
		String claspath =  projectPath +File.separator+".classpath";
		return findProjectSrcPath(projectPath, claspath, recursion);
	}
}
