package cn.harry12800.develper.sample;

import java.util.List;

import cn.harry12800.dbhelper.DBType;
import cn.harry12800.dbhelper.Db;
import cn.harry12800.dbhelper.MysqlHelper;
import cn.harry12800.dbhelper.OracleHelper;
import cn.harry12800.dbhelper.entity.DBTable;
import cn.harry12800.developer.DeveloperUtils;

public class Sample {

	public static void main(String[] args) throws Exception {
		/**
		 * Mysql的数据库生成数据字典工具
		 */
		Db db = new MysqlHelper();
		/**
		 * 设置为null 将使用代码里默认的链接
		 */

		String url = "jdbc:mysql://120.78.177.24:3306/fxchat";
		String user = "root";
		String pwd = "Zhouguozhu@123";
		//203.110.160.90:33899
//		url = "jdbc:oracle:thin:@203.110.160.90:1521:orcl";
//		user = "pharm";
//		pwd = "pharm";
//		url = "jdbc:oracle:thin:@192.168.0.70:1521:testdb";
//		user = "pharm19";
//		pwd = "pharm19";
		/**
		 * oracle 的数据库生成数据字典工具
		 */
		db = new MysqlHelper();
		db.generateDescFile(url, user, pwd);
		//db.generateDescFile(url, user, pwd);
		//		Map<String, List<String>> a = db.getTableAndColumns(url, user, pwd);
		//		Set<Entry<String,List<String>>> entrySet = a.entrySet();
		//		for (Entry<String, List<String>> entry : entrySet) {
		//			
		//			System.out.println(entry.getKey());
		//			List<String> value = entry.getValue();
		//			for (String string : value) {
		//				System.out.println("	"+string); 
		//			}
		//		}
		List<DBTable> dbTable = DeveloperUtils.getDBTable(url, user, pwd);
		for (DBTable table : dbTable) {
			//			System.out.println(table.getCreateDDL(DBType.MYSQL));
			System.out.println(table.getCreateCommentDDL(DBType.ORACLE));
		}
	}
}
