package net.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.file.RegexpFileWriter;

@Controller
public class FileProcessor implements Runnable {

	private static ConcurrentMap<String, HashMap<String, Integer>> result = new ConcurrentHashMap<String, HashMap<String, Integer>>();

	private static HashMap<String, Integer> finalResult = new HashMap<String, Integer>();

	private static ArrayList<RegexpFileWriter> fileWriters;
	
	public static String fileWorkingDirectory;

	public static String getFileWorkingDirectory() {
		return fileWorkingDirectory;
	}

	public static void setFileWorkingDirectory(String fileWorkingDirectory) {
		FileProcessor.fileWorkingDirectory = fileWorkingDirectory;
	}

	private String filePath = null;

	public FileProcessor() {
		super();
	}

	public FileProcessor(String filePath, String fileWorkingDirectory) {
		super();
		this.filePath = filePath;
		FileProcessor.fileWorkingDirectory = fileWorkingDirectory;
	}
	
	public FileProcessor(String filePath) {
		super();
		this.filePath = filePath;
	}
	

	public ArrayList<RegexpFileWriter> getFileWriters() {
		return fileWriters;
	}

	public void setFileWriters(ArrayList<RegexpFileWriter> fileWriters) {
		this.fileWriters = fileWriters;
	}

	public static Map sortByValue(Map unsortMap) {
		List list = new LinkedList(unsortMap.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static void writeToFiles() {
		for (Map.Entry entry : finalResult.entrySet()) {
			String key = (String) entry.getKey();
			for (RegexpFileWriter writer : fileWriters) {
				if (key.matches(writer.getRegexp())) {
					writer.write(entry.getKey() + " " + entry.getValue());
				}
			}
		}
	}

	public static List<String> getFileList() {
		List<String> fileNames = new ArrayList<String>();

		File[] files = new File(fileWorkingDirectory).listFiles();

		for (File file : files) {
			if (file.isFile()) {
				fileNames.add(file.getName());
			}
		}
		return fileNames;
	}

	public HashMap<String, Integer> countWordsInFile(String path) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		System.out.println("Reading file: " + path);
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(fileWorkingDirectory+"\\" + path));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] splitted = sCurrentLine.replaceAll("[^A-Za-z\\s]+", " ").replaceAll("\\s+|\\t+|\\n+|\\r+", " ")
						.split(" ");

				for (int i = 0; i < splitted.length; i++) {
					if (hm.containsKey(splitted[i])) {
						int cont = hm.get(splitted[i]);
						hm.put(splitted[i], cont + 1);
					} else {
						hm.put(splitted[i], 1);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return hm;
	}

	@RequestMapping("/countwords")
	public ModelAndView countWords() {
		String message = "All files processed!";

		ArrayList<Thread> threads = new ArrayList<Thread>();

		List<String> fileNames = getFileList();

		for (String fileName : fileNames) {
			Thread tobj = new Thread(new FileProcessor(fileName));
			threads.add(tobj);
			tobj.start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (HashMap<String, Integer> e : result.values()) {
			for (Map.Entry entry : e.entrySet()) {
				String key = (String) entry.getKey();
				if (FileProcessor.finalResult.containsKey(key)) {
					int count = FileProcessor.finalResult.remove(key);
					FileProcessor.finalResult.put(key, (count + (Integer) entry.getValue()));
				} else {
					FileProcessor.finalResult.put((String) entry.getKey(), (Integer) entry.getValue());
				}
			}
		}

		FileProcessor.finalResult = (HashMap<String, Integer>) sortByValue(FileProcessor.finalResult);
		writeToFiles();

		return new ModelAndView("countwords", "message", message);
	}

	@Override
	public void run() {
		result.putIfAbsent(filePath, countWordsInFile(filePath));
	}

	
}
