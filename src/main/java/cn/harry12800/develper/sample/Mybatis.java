package cn.harry12800.develper.sample;

import cn.harry12800.developer.DeveloperUtils;

public class Mybatis {

	public static void main(String[] args) {
		String url = "jdbc:mysql://172.16.6.218:3306/fingerchat_dev_docs?useSSL=false&allowMultiQueries=true";
		String user = "root";
		String pwd = "root@123456";
		String tableName = "api,application,auto_api_markdown,directory,markdown,resource,resource_transfer,user";
		//		String url = "jdbc:mysql://127.0.0.1/scan?useSSL=false&useUnicode=true&characterEncoding=utf8&characterSetResults=utf8";
		//		String user = "root";
		//		String pwd = "admin";
		//		String tableName = "chat_msg";
		//		DeveloperUtils.createBuilder().setBasePackage("cn.harry12800.vchat.server")
		//		.setModuleName("module")
		//		.setUrl(url)
		//		.setUser(user)
		//		.setPwd(pwd)
		//		.setTableName(tableName)
		//		.build();
		DeveloperUtils.createBuilder().setBasePackage("com.hnlens")
				.setModuleName("doc")
				.setUrl(url)
				.setUser(user)
				.setPwd(pwd)
				.setTableName(tableName)
				.build();
	}
}
