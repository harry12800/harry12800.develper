package cn.harry12800.develper.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.harry12800.developer.DeveloperUtils;
import cn.harry12800.tools.FileUtils;

/**
 * 代码行数统计
 */
public class CodeCounter {

	/**
	 * 统计项目代码行数
	 */
	public static void counter(String projectName) {

		Set<String> projectSrcPath = DeveloperUtils.getProjectSrcPath(projectName, true);

		for (String path : projectSrcPath) {
			System.out.println(path);
			ArrayList<File> al = getFile(new File(path));
			for (File f : al) {
				if (f.getName().matches(".*\\.java$")) { // 匹配java格式的文件
					count(f);
					System.out.println(f);
				}
			}
			System.out.println("统计文件：" + files);
			System.out.println("代码行数：" + codeLines);
			System.out.println("注释行数：" + commentLines);
			System.out.println("空白行数：" + blankLines);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// code("harry12800.j2se");
		// code("gznytm.serial");
		// Set<String> projectSrcPath =
		// DeveloperUtils.getProjectSrcPath("gznytm.serial",false);
		// for (String string : projectSrcPath) {
		// //DeveloperUtils.notifyAddAnnotation(string);
		// }
		codePath("D:\\Workspaces\\IM\\Server");
	}

	public static void codePath(String path) {
		Set<String> sets = new HashSet<>();
		ArrayList<File> al = getFile(new File(path));
		for (File f : al) {
			if (f.getName().matches(".*\\.java$")) { // 匹配java格式的文件
				if (!sets.contains(f.getName())) {
					sets.add(f.getName());
					count1(f);
				}
				System.out.println(f);
			}
		}
		System.out.println("统计文件：" + files);
		System.out.println("代码行数：" + codeLines);
		System.out.println("注释行数：" + commentLines);
		System.out.println("空白行数：" + blankLines);
	}

	/**
	 * 代码行数统计
	 */
	public static void code(String projectName) {

		Set<String> projectSrcPath = DeveloperUtils.getProjectSrcPath(projectName, false);

		for (String path : projectSrcPath) {
			System.out.println("1:" + path);
			ArrayList<File> al = getFile(new File(path));
			for (File f : al) {
				if (f.getName().matches(".*\\.java$")) { // 匹配java格式的文件
					count1(f);
					System.out.println(f);
				}
			}
			System.out.println("统计文件：" + files);
			System.out.println("代码行数：" + codeLines);
			System.out.println("注释行数：" + commentLines);
			System.out.println("空白行数：" + blankLines);
		}
	}

	static long files = 0;
	static long codeLines = 0;
	static long commentLines = 0;
	static long blankLines = 0;
	static ArrayList<File> fileArray = new ArrayList<File>();

	/**
	 * 获得目录下的文件和子目录下的文件
	 * 
	 * @param f
	 * @return
	 */
	public static ArrayList<File> getFile(File f) {
		File[] ff = f.listFiles();
		for (File child : ff) {
			if (child.isDirectory()) {
				getFile(child);
			} else
				fileArray.add(child);
		}
		return fileArray;

	}

	/**
	 * 统计方法
	 * 
	 * @param f
	 */
	private static void count(File f) {
		BufferedReader br = null;
		boolean flag = false;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = br.readLine()) != null) {
				line = line.trim(); // 除去注释前的空格
				if (line.matches("^[ ]*$")) { // 匹配空行
					blankLines++;
				} else if (line.startsWith("//")) {
					commentLines++;
				} else if (line.startsWith("/*") && !line.endsWith("*/")) {
					commentLines++;
					flag = true;
				} else if (line.startsWith("/*") && line.endsWith("*/")) {
					commentLines++;
				} else if (flag == true) {
					commentLines++;
					if (line.endsWith("*/")) {
						flag = false;
					}
				} else {
					codeLines++;
				}
			}
			files++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 统计方法
	 * 
	 * @param f
	 */
	private static void count1(File f) {
		BufferedReader br = null;
		boolean flag = false;
		try {
			StringBuffer sbBuffer = new StringBuffer();
			br = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = br.readLine()) != null) {
				String string = line;
				line = line.trim(); // 除去注释前的空格
				if (line.matches("^[ ]*$")) { // 匹配空行
					blankLines++;
				} else if (line.startsWith("//")) {
					// sbBuffer.append(string+"\r\n");
					commentLines++;
				} else if (line.startsWith("/*") && !line.endsWith("*/")) {
					// sbBuffer.append(string+"\r\n");
					commentLines++;
					flag = true;
				} else if (line.startsWith("/*") && line.endsWith("*/")) {
					// sbBuffer.append(string+"\r\n");
					commentLines++;
				} else if (flag == true) {
					commentLines++;
					// sbBuffer.append(string+"\r\n");
					if (line.endsWith("*/")) {
						flag = false;
					}
				} else {
					sbBuffer.append(string + "\r\n");
					codeLines++;
				}
			}
			files++;
			FileUtils.appendContent("z:/d.txt", sbBuffer.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}