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
//	ava.lang.NullPointerException
//	at javax.swing.ImageIcon.<init>(ImageIcon.java:217)
//	at cn.harry12800.j2se.component.imageview.ImageViewerFrame.initComponents(ImageViewerFrame.java:108)
//	at cn.harry12800.j2se.component.imageview.ImageViewerFrame.<init>(ImageViewerFrame.java:76)
//	at cn.harry12800.j2se.component.imageview.ImageViewerFrame.<init>(ImageViewerFrame.java:66)
//	at cn.harry12800.vchat.adapter.message.MessageAdapter$4$1.onSuccess(MessageAdapter.java:407)
//	at cn.harry12800.vchat.utils.ImageCache$1.run(ImageCache.java:113)
//	at java.lang.Thread.run(Thread.java:748)
}
