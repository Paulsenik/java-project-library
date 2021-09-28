package com.paulsen.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Class for easier file-access and handling filedata
 * 
 * @author Paul Seidel
 * @version 1.0.4
 * @since 2019-01-01
 * @see Last Updated 2021/08/07
 */
public class PFolder {

	public static File createFolder(String path) {
		if (isFile(path))
			return null;
		File f = new File(path);
		f.mkdirs();
		return f;
	}

	public static boolean isFolder(String path) {
		return (!isFile(path));
	}

	public static boolean isFolder(File f) {
		return ((!isFile(f.getAbsolutePath())));
	}

	public static boolean isFile(String path) {
//		System.out.println("   asdfasdf " + ((char) 92));
		for (int i = path.length() - 1; i >= 0; i--) {
			if (path.charAt(i) == '/' || path.charAt(i) == ((char) 92))
				return false;
			if (path.charAt(i) == '.')
				return true;
		}
		if (new File(path).isFile())
			return true;
		return false;
	}

	public static String getName(String path) {
		String s = "";
		for (int i = path.length() - 1; i >= 0; i--) {
			if (path.charAt(i) == '/' || path.charAt(i) == ((char) 92))
				break;
			s = path.charAt(i) + s;
		}
		return s;
	}

	public static boolean deleteFolder(String path) {
		if (isFile(path))
			return false;
		File f = new File(path);
		f.delete();
		return true;
	}

	/**
	 * 
	 * @param path of Folder
	 * @param Type of Files (if is null => Filetypes will be ignored)
	 * @return
	 */
	public static String[] getFiles(String path, String fileType) {
		String[] s = new File(path).list();
		if (s == null || s.length == 0)
			return null;

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < s.length; i++)
			if (isFile(s[i]))
				if (fileType == null || s[i].endsWith(fileType))
					list.add(path + "/" + s[i]);

		String sN[] = new String[list.size()];
		for (int i = 0; i < sN.length; i++)
			sN[i] = list.get(i);
		return sN;
	}

	public static String[] getSubFolders(String path) {
		String s[] = new File(path).list();
		if (s == null || s.length == 0)
			return null;

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < s.length; i++)
			if (!isFile(s[i]))
				list.add(path + "/" + s[i]);

		String sN[] = new String[list.size()];
		for (int i = 0; i < sN.length; i++) {
			sN[i] = list.get(i);
		}
		return sN;
	}

	/**
	 * 
	 * @param
	 * @return All folders and their childfolders recursively inside of it
	 */
	public static ArrayList<File> getAllFoldersOfRoot(File folder) {

		if (folder != null) {
			File[] children = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return PFolder.isFolder(name);
				}
			});
			if (children != null || (children != null && children.length == 0)) {
				ArrayList<File> sum = new ArrayList<File>();

				for (File f : children) {
					sum.add(f);
				}

				for (int i = 0; i < children.length; i++) {
					ArrayList<File> tempFiles = getAllFoldersOfRoot(children[i]); // recursion
					if (sum != null && tempFiles != null) {
						sum.addAll(tempFiles);
					}
				}
				return sum;
			}
		}
		return null;
	}
}
