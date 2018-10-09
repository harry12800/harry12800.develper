package cn.harry12800.develper.sample;

import java.util.List;

import cn.harry12800.dbhelper.entity.DBTable;
import cn.harry12800.developer.DeveloperUtils;

public class Mybatis {

	public static void main(String[] args) {
		String url = "jdbc:mysql://10.3.9.231:3306/lens_dorm_power?useSSL=false&allowMultiQueries=true";
		String user = "root";
		String pwd = "Lenovo,,123";
		String tableName = "bed,area_building,water_operate_log,dorm_out_person,dorm_transfer,asset_assign,employee,house,water_electricity_gas_standard,dorn_assign,dorm_assign_log,balance,evection,gas_month_data,asset_encode,unusual,asset_log,carport,login_log,asset_destroy,carport_apply,dorm_out_log,self_leave_log,asset_register,history_data_count,asset_borrow,dept,asset_level_standard,room,self_leave,water_month_data,dictionary,asset_repository,electric_operate_log,person,carport_assign,carport_log,gas_operate_log,balance_scheme,asset,dorm_quit,electric_month_data,family,person_import_log,user,customer";
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
//		DeveloperUtils.generateDescFile(url, user, pwd);
//		DeveloperUtils.createBuilder().setBasePackage("com.hnlens")
//				.setModuleName("dorm")
//				.setDatabaseName("lens_dorm")
//				.setUrl(url)
//				.setUser(user)
//				.setPwd(pwd)
//				.setTableName(tableName)
//				.build();
		try {
			List<DBTable> dbTable = DeveloperUtils.getDBTable(url, user, pwd);
			for (DBTable dbTable2 : dbTable) {
				System.out.print(dbTable2.getName()+",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
