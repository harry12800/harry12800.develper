package cn.harry12800.developer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import cn.harry12800.developer.DeveloperUtils.Project;
import cn.harry12800.tools.MachineUtils;
import cn.harry12800.tools.StringUtils;
@SuppressWarnings("resource")
public class JarUtils {
	public static String workSpacePath = "";
	public static String localProjectPath = "";
	static String localProjectName = "";
	static {
		workSpacePath = MachineUtils.getWorkSpacePath();
		localProjectPath = MachineUtils.getProjectPath();
		localProjectName = localProjectPath.substring(workSpacePath.length() + 1, localProjectPath.length());
	}

	public static void main(String[] args) throws Exception {
		onejar("nytm.business", null, false, true);
		System.out.println("完成");
	}

	/**
	 * 通过项目名称来打jar包。只要是本工作空间的java项目,不打包.开头的文件或文件夹
	 * 
	 * @param projectName
	 *            项目名称
	 * @param mainClassName
	 *            打包运行的main类
	 * @param isSimple
	 *            是否是单项目打包
	 * @param requireSrcCode
	 *            是否需要源码
	 * @throws Exception
	 */
	public static void onejar(String projectName, String mainClassName, boolean isSimple, boolean requireSrcCode)
			throws Exception {
		// 获得项目的输出路径
		String jarPath = localProjectPath + File.separator + projectName + ".jar";

		File targetDir = new File(new File(jarPath).getParent());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		if (!isSimple) {
			mainClassName = "cn.harry12800.main.Boot";
			jarPath = localProjectPath + File.separator + "main.jar";
		}
		System.out.println("*** --> 开始生成" + jarPath + "包...");
		/**
		 * jar 文件记录。因为同名文件不能写入
		 */
		HashSet<String> jarMemorySet = new HashSet<String>();
		JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath), getManifest(mainClassName));
		if (!isSimple) {
			if (!new File(workSpacePath + File.separator + "newtec.encrypt.javaclassloader").exists()) {
				throw new Exception("可执行的多项目必须有newtec.encrypt.javaclassloader这个项目");
			}
			appendOutputToJar(jar, "newtec.encrypt.javaclassloader", jarMemorySet);
			if (requireSrcCode) {
				appendSrcToJar(jar, "newtec.encrypt.javaclassloader", jarMemorySet);
			}
			Project project = DeveloperUtils.ProjectFactory.GetInstance().createProject(projectName);
			for (Project pro : project.associateProtectList) {
				System.out.println("相关联的项目：" + pro);
				String childjarpath = localProjectPath + File.separator + pro.name + ".jar";
				generateProJar(pro.name, requireSrcCode, childjarpath);
				mergeJarFile(childjarpath, jar, jarMemorySet);
				// generateJar(pro.outputPath,projectPath+File.separator+pro.name+".jar",
				// null);
				new File(childjarpath).delete();
			}
			String childjarpath = localProjectPath + File.separator + projectName + ".jar";
			generateProJar(projectName, requireSrcCode, childjarpath);
			mergeJarFile(childjarpath, jar, jarMemorySet);
			new File(childjarpath).delete();
			mergeJarFile(project, jar, jarMemorySet);
		} else {
			appendOutputToJar(jar, projectName, jarMemorySet);
			if (requireSrcCode) {
				appendSrcToJar(jar, projectName, jarMemorySet);
			}

		}
		jar.close();
		System.out.println("*** --> jar包生成完毕。jar文件输出路径：" + jarPath);
	}

	private static void mergeJarFile(String childjarpath, JarOutputStream target, HashSet<String> set)
			throws IOException {
		File source = new File(childjarpath);
		JarInputStream jarInputStream = new JarInputStream(new FileInputStream(source));
		JarEntry entry;
		URL url = new URL("file:///" + childjarpath);
		URLClassLoader myClassLoader = new URLClassLoader(new URL[] { url }, null);
		while ((entry = jarInputStream.getNextJarEntry()) != null) {
			InputStream is = myClassLoader.getResourceAsStream(entry.getName());
			if (set.contains(entry.getName())) {
				System.out.println("重复文件：" + entry.getName());
				if (is != null)
					is.close();
				continue;
			}
			JarEntry newEntry = new JarEntry(entry.getName());
			entry.setTime(source.lastModified());
			target.putNextEntry(newEntry);
			set.add(newEntry.getName());
			if (entry.isDirectory()) {
				if (is != null)
					is.close();
				continue;
			}
			try (BufferedInputStream in = new BufferedInputStream(is);) {
				byte[] buffer = new byte[1024];
				int len = -1;
				while (-1 != (len = in.read(buffer))) {
					target.write(buffer, 0, len);
				}
				target.closeEntry();
			}

		}
	}

	/**
	 * 通过项目名称来打jar包。只要是本工作空间的java项目,不打包开头的文件或文件夹
	 * 
	 * @param projectName
	 *            项目名称
	 * @param mainClassName
	 *            打包运行的main类
	 * @param isSimple
	 *            是否是单项目打包
	 * @param requireSrcCode
	 *            是否需要源码
	 * @throws Exception
	 */
	public static void jarProtectByName(String projectName, String mainClassName, boolean isSimple,
			boolean requireSrcCode) throws Exception {
		// 获得项目的输出路径
		String jarPath = localProjectPath + File.separator + projectName + ".jar";

		File targetDir = new File(new File(jarPath).getParent());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		if (!isSimple) {
			mainClassName = "cn.harry12800.main.Boot";
			jarPath = localProjectPath + File.separator + "main.jar";
		}
		System.out.println("*** --> 开始生成" + jarPath + "包...");
		/**
		 * jar 文件记录。因为同名文件不能写入
		 */
		HashSet<String> jarMemorySet = new HashSet<String>();
		JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath), getManifest(mainClassName));
		if (!isSimple) {
			if (!new File(workSpacePath + File.separator + "harry12800.main").exists()) {
				throw new Exception("可执行的多项目必须有harry12800.mainr这个项目");
			}
			appendOutputToJar(jar, "harry12800.main", jarMemorySet);
			if (requireSrcCode) {
				appendSrcToJar(jar, "harry12800.main", jarMemorySet);
			}
			Project project = DeveloperUtils.ProjectFactory.GetInstance().createProject(projectName);
			for (Project pro : project.associateProtectList) {
				System.out.println("相关联的项目：" + pro);
				String childjarpath = localProjectPath + File.separator + pro.name + ".jar";
				generateProJar(pro.name, requireSrcCode, childjarpath);
				writeFileForJar(jar, new File(childjarpath), "lib/" + pro.name + ".jar", jarMemorySet);
				new File(childjarpath).delete();
			}
			String childjarpath = localProjectPath + File.separator + projectName + ".jar";
			generateProJar(projectName, requireSrcCode, childjarpath);
			writeFileForJar(jar, new File(childjarpath), "lib/" + projectName + ".jar", jarMemorySet);
			new File(childjarpath).delete();
			writeLibFile(project, jar, jarMemorySet);
		} else {
			appendOutputToJar(jar, projectName, jarMemorySet);
			if (requireSrcCode) {
				appendSrcToJar(jar, projectName, jarMemorySet);
			}

		}
		jar.close();
		System.out.println("*** --> jar包生成完毕。jar文件输出路径：" + jarPath);
	}

	/**
	 * 通过项目名称来打jar包。只要是本工作空间的java项目,不打包.开头的文件或文件夹
	 * 
	 * @param projectName
	 *            项目名称
	 * @param mainClassName
	 *            打包运行的main类
	 * @param isSimple
	 *            是否是单项目打包
	 * @param requireSrcCode
	 *            是否需要源码
	 * @throws Exception
	 */
	public static void jarProtectByName(String fileName, String projectName, String mainClassName, boolean isSimple,
			boolean requireSrcCode) throws Exception {
		// 获得项目的输出路径
		String jarPath = localProjectPath + File.separator + projectName + ".jar";
		String namePath = localProjectPath + File.separator + fileName;

		File targetDir = new File(new File(jarPath).getParent());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		if (!isSimple) {
			mainClassName = "cn.harry12800.main.Boot";
			jarPath = localProjectPath + File.separator + "main.jar";
		}
		System.out.println("*** --> 开始生成" + jarPath + "包...");
		/**
		 * jar 文件记录。因为同名文件不能写入
		 */
		HashSet<String> jarMemorySet = new HashSet<String>();
		JarOutputStream jar = new JarOutputStream(new FileOutputStream(namePath), getManifest(mainClassName));
		if (!isSimple) {
			if (!new File(workSpacePath + File.separator + "newtec.encrypt.javaclassloader").exists()) {
				throw new Exception("可执行的多项目必须有newtec.encrypt.javaclassloader这个项目");
			}
			appendOutputToJar(jar, "newtec.encrypt.javaclassloader", jarMemorySet);
			if (requireSrcCode) {
				appendSrcToJar(jar, "newtec.encrypt.javaclassloader", jarMemorySet);
			}
			Project project = DeveloperUtils.ProjectFactory.GetInstance().createProject(projectName);
			for (Project pro : project.associateProtectList) {
				System.out.println("相关联的项目：" + pro);
				String childjarpath = localProjectPath + File.separator + pro.name + ".jar";
				generateProJar(pro.name, requireSrcCode, childjarpath);
				writeFileForJar(jar, new File(childjarpath), "lib/" + pro.name + ".jar", jarMemorySet);
				new File(childjarpath).delete();
			}
			String childjarpath = localProjectPath + File.separator + projectName + ".jar";
			generateProJar(projectName, requireSrcCode, childjarpath);
			writeFileForJar(jar, new File(childjarpath), "lib/" + projectName + ".jar", jarMemorySet);
			new File(childjarpath).delete();
			writeLibFile(project, jar, jarMemorySet);
		} else {
			appendOutputToJar(jar, projectName, jarMemorySet);
			if (requireSrcCode) {
				appendSrcToJar(jar, projectName, jarMemorySet);
			}

		}
		jar.close();
		System.out.println("*** --> jar包生成完毕。jar文件输出路径：" + jarPath);
	}

	
	private static void mergeJarFile(Project project, JarOutputStream target, Set<String> set) throws IOException {
		List<String> libPathList = project.libPathList;
		for (Project pro : project.associateProtectList) {
			libPathList.addAll(pro.libPathList);
		}
		for (int i = 0; i < libPathList.size(); i++) {
			File source = new File(libPathList.get(i));
			JarInputStream jarInputStream = new JarInputStream(new FileInputStream(source));
			JarEntry entry;
			URL url = new URL("file:///" + libPathList.get(i));
			URLClassLoader myClassLoader = new URLClassLoader(new URL[] { url }, null);
			while ((entry = jarInputStream.getNextJarEntry()) != null) {
				InputStream is = myClassLoader.getResourceAsStream(entry.getName());
				if (set.contains(entry.getName())) {
					System.out.println("重复文件：" + entry.getName());
					if (is != null)
						is.close();
					continue;
				}
				JarEntry newEntry = new JarEntry(entry.getName());
				entry.setTime(source.lastModified());
				target.putNextEntry(newEntry);
				set.add(newEntry.getName());
				if (entry.isDirectory()) {
					if (is != null)
						is.close();
					target.closeEntry();
					continue;
				}
				try (BufferedInputStream in = new BufferedInputStream(is);) {
					byte[] buffer = new byte[1024];
					int len = -1;
					while (-1 != (len = in.read(buffer))) {
						target.write(buffer, 0, len);
					}
					target.closeEntry();
				}

			}
		}
	}

	private static void writeLibFile(Project project, JarOutputStream target, Set<String> set) throws IOException {
		List<String> libPathList = project.libPathList;
		for (Project pro : project.associateProtectList) {
			libPathList.addAll(pro.libPathList);
		}
		for (int i = 0; i < libPathList.size(); i++) {
			File source = new File(libPathList.get(i));
			if (!set.contains("lib/" + source.getName())) {
				JarEntry entry = new JarEntry("lib/" + source.getName());
				entry.setTime(source.lastModified());
				target.putNextEntry(entry);
				try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));) {
					byte[] buffer = new byte[1024];
					int len = -1;
					while (-1 != (len = in.read(buffer))) {
						target.write(buffer, 0, len);
					}
					target.closeEntry();
				}
				set.add("lib/" + source.getName());
			}
		}
	}

	/**
	 * 递归将文件写入jar包中
	 * 
	 * @param jar
	 * @param source
	 * @param outputPath
	 * @param set
	 * @throws IOException
	 */
	private static void writeFileForJar(JarOutputStream jar, File source, String path, Set<String> set)
			throws IOException {
		if (!set.contains(path)) {
			JarEntry entry = new JarEntry(path);
			entry.setTime(source.lastModified());
			jar.putNextEntry(entry);
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));) {
				byte[] buffer = new byte[1024];
				int len = -1;
				while (-1 != (len = in.read(buffer))) {
					jar.write(buffer, 0, len);
				}
				jar.closeEntry();
			}
			set.add(path);
		}
	}

	private static void generateProJar(String name, boolean requireSrcCode, String childjarpath) throws Exception {
		/**
		 * jar 文件记录。因为同名文件不能写入
		 */
		HashSet<String> jarMemorySet = new HashSet<String>();
		JarOutputStream jar = new JarOutputStream(new FileOutputStream(childjarpath), getManifest(null));
		appendOutputToJar(jar, name, jarMemorySet);
		if (requireSrcCode) {
			appendSrcToJar(jar, name, jarMemorySet);
		}
		jar.close();
	}

	/**
	 * 简单的获取Manifest对象
	 * 
	 * @param mainClassName
	 * @return
	 */
	private static Manifest getManifest(String mainClassName) {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(Attributes.Name.SPECIFICATION_VENDOR, DeveloperUtils.author);
		if (!StringUtils.isEmpty(mainClassName))
			manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClassName.trim());
		return manifest;
	}

	/**
	 * 单个项目打包
	 * 
	 * @param outputPath
	 *            项目的输出路径
	 * @param jarPath
	 *            生成的jar文件路径
	 * @param mainClassName
	 *            main方法 所在的类，如果没有设置为空
	 * @throws Exception
	 */
	public static void appendOutputToJar(JarOutputStream jar, String projectName, Set<String> jarMemorySet)
			throws Exception {
		String outputPath = DeveloperUtils.getProjectOutputPath(workSpacePath + File.separator + projectName);
		System.out.println("《" + projectName + "》项目的输出路径:" + outputPath);
		writeDirtoJar(jar, new File(outputPath), new File(outputPath).getAbsolutePath(), jarMemorySet);
	}

	/**
	 * 单个项目打包
	 * 
	 * @param srcPathList
	 *            项目的输出路径
	 * @param jarPath
	 *            生成的jar文件路径
	 * @param mainClassName
	 *            main方法 所在的类，如果没有设置为空
	 * @throws Exception
	 */
	public static void appendSrcToJar(JarOutputStream jar, String projectName, Set<String> jarMemorySet)
			throws Exception {
		Set<String> srcPathList = DeveloperUtils.getProjectSrcPath(projectName);
		for (String srcpath : srcPathList) {
			System.out.println("src路径：" + srcpath);
			writeDirtoJar(jar, new File(srcpath), new File(srcpath).getAbsolutePath(), jarMemorySet);
		}
	}

	private static void writeDirtoJar(JarOutputStream jar, File source, String outputPath, Set<String> set)
			throws IOException {
		if (source.isDirectory()) {
			String name = source.getPath().replace("\\", "/");
			if (!name.isEmpty()) {
				if (!name.endsWith("/")) {
					name += "/";
				}
				name = name.substring(outputPath.length() + 1);

				if (name.startsWith(".svn"))
					return;
				if (name.isEmpty())
					name = "/";
				if (!set.contains(name) && !name.equals("/")) {
					// System.out.println("目录："+name);
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					jar.putNextEntry(entry);
					jar.closeEntry();
					set.add(name);
				}
			}
			for (File nestedFile : source.listFiles())
				writeDirtoJar(jar, nestedFile, outputPath, set);
			return;
		} else {
			String middleName = source.getPath().replace("\\\\", "/").substring(outputPath.length() + 1);
			// System.out.println("文件："+middleName);
			if (!set.contains(middleName)) {
				JarEntry entry = new JarEntry(middleName);
				entry.setTime(source.lastModified());
				jar.putNextEntry(entry);
				try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));) {
					byte[] buffer = new byte[1024];
					int len = -1;
					while (-1 != (len = in.read(buffer))) {
						jar.write(buffer, 0, len);
					}
					jar.closeEntry();
				}
				set.add(middleName);
			}
		}
	}

	public static void ListJarFile(String path) throws Exception {
		JarFile jarFile = new JarFile(path);
		Enumeration<JarEntry> jarEntrys = jarFile.entries();
		while (jarEntrys.hasMoreElements()) {
			JarEntry jarEntry = jarEntrys.nextElement();
			System.out.println(jarEntry.getName());
		}
	}

	public static void JarFileTest(String path) throws Exception {
		JarFile jarFile = new JarFile(path);
		JarEntry jarEntry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
		InputStream in = jarFile.getInputStream(jarEntry);
		int c = -1;
		while ((c = in.read()) != -1) {
			System.out.print((char) (c & 0XFF));
		}
		if (in != null) {
			in.close();
			in = null;
		}
		System.out.println();
	}

	public static String Jar(String path, File file) throws IOException {
		if (file == null) {
			return null;
		}
		String jarFileName = "";
		if (file.getName().indexOf(".") > -1) {
			jarFileName = file.getName().substring(0, file.getName().indexOf(".")) + ".jar";
		} else {
			jarFileName = file.getName() + ".jar";
		}
		String jarFullName = path + File.separator + jarFileName;
		byte[] data = new byte[1024 * 2];
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(jarFullName);
		JarOutputStream jarOS = new JarOutputStream(fos);
		jarOS.setMethod(JarOutputStream.DEFLATED);
		jarOS.putNextEntry(new JarEntry(file.getName()));
		int length = 0;
		while ((length = fis.read(data)) != -1) {
			jarOS.write(data, 0, length);
		}
		jarOS.finish();
		jarOS.close();
		fos.close();
		fis.close();
		return jarFileName;
	}

	public static String Jar(String path, String jarName, File[] files) throws IOException {
		if (files == null) {
			return null;
		}
		String jarFullName = path + File.separator + jarName;
		byte[] data = new byte[1024];
		FileOutputStream fos = new FileOutputStream(jarFullName);
		for (File file : files) {
			FileInputStream fis = new FileInputStream(file);
			JarOutputStream jarOS = new JarOutputStream(fos);
			jarOS.setMethod(JarOutputStream.DEFLATED);
			jarOS.putNextEntry(new JarEntry(file.getName()));
			int length = 0;
			while ((length = fis.read(data)) != -1) {
				jarOS.write(data, 0, length);
			}
			jarOS.finish();
			jarOS.close();
			fis.close();
		}
		fos.close();
		return jarFullName;
	}

}