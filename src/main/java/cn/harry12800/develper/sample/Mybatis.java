package cn.harry12800.develper.sample;

import cn.harry12800.developer.DeveloperUtils;

public class Mybatis {

	public static void main(String[] args) {
		String[] packageNames= new String[]{"com.hnlens.net.auth","com.hnlens.net.auth.mapper"};
		String url="jdbc:mysql://172.16.6.218:3306/onlineauth_privilege?useSSL=false&allowMultiQueries=true";
		String user="root";
		String tableName = "feige_user";
		String pwd = "root@123456";
		DeveloperUtils.generateDbEntityByTableName(packageNames, url, user, pwd, tableName);
	}
}
