package cn.harry12800.develper.sample;

import cn.harry12800.developer.DeveloperUtils;

public class Mybatis {

	public static void main(String[] args) {
		String url = "jdbc:mysql://120.78.177.24:3306/scan?useSSL=false&allowMultiQueries=true";
		String user = "root";
		String pwd = "Zhouguozhu@123";

		DeveloperUtils.createBuilder().setBasePackage("cn.harry12800").setModuleName("db").setDatabaseName("scan")
				.setUrl(url).setUser(user).setPwd(pwd).setAllTable(false).setTableName("file_server").build();
	}
}
