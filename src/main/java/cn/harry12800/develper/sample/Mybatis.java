package cn.harry12800.develper.sample;

import cn.harry12800.developer.DeveloperUtils;

public class Mybatis {

	public static void main(String[] args) {
		String url = "jdbc:mysql://10.3.9.142:3306/fingerchat_dev_docs?useSSL=false&allowMultiQueries=true";
		String user = "root";
		String pwd = "Lenovo,,123";
		String tableName = "api,application,auto_api_markdown,directory,markdown,resource,resource_transfer,user";
		DeveloperUtils.createBuilder().setBasePackage("com.hnlens")
				.setModuleName("doc")
				.setUrl(url)
				.setUser(user)
				.setPwd(pwd)
				.setTableName(tableName)
				.build();
	}
}
