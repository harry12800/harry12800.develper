package cn.harry12800.developer;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cn.harry12800.tools.FileUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class GenController {

	public final static String ftl = "controller.xml";
	public static boolean jarOrFile = true;

	@SuppressWarnings("deprecation")
	public static void gen(CurdData curdData) throws Exception {

		Configuration cfg = Configuration.getDefaultConfiguration();
		if (jarOrFile)
			cfg.setClassForTemplateLoading(GenEntity.class, "/template/gen/curd");
		else {
			cfg.setDirectoryForTemplateLoading(new File("src/main/resources/template/gen/curd"));
		}
		Template t1 = cfg.getTemplate(ftl);
		StringWriter out = new StringWriter();
		t1.process(curdData.toMap(), out);
		out.flush();
		out.close();
		StringBuffer buffer = out.getBuffer();

		StringReader sr = new StringReader(buffer.toString());
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		NodeList childNodes = doc.getFirstChild().getChildNodes();
		int length = childNodes.getLength();
		XmlTemplate template = new XmlTemplate();
		Field[] f = XmlTemplate.class.getDeclaredFields();
		for (int i = 0; i < length; i++) {
			System.out.println(childNodes.item(i).getTextContent());
			for (Field field : f) {
				if (field.getName().equals(childNodes.item(i).getNodeName())) {
					field.setAccessible(true);
					field.set(template, childNodes.item(i).getTextContent());
				}
			}
		}
		System.out.println(template);
		FileUtils.createFile(DeveloperUtils.projectPath + File.separator + template.filePath + template.fileName);
		FileUtils.writeContent(DeveloperUtils.projectPath + File.separator + template.filePath + template.fileName, template.content);
		System.out.println(DeveloperUtils.projectPath + File.separator + template.filePath + template.fileName);
	}
}
