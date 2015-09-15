package net.file;

import java.io.FileWriter;
import java.io.IOException;

public class RegexpFileWriter {

	private String path;

	private String regexp;

	public RegexpFileWriter(String path, String regexp) {
		super();
		this.path = path;
		this.regexp = regexp;
	}

	public RegexpFileWriter() {
		super();
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// To optimize to use map and single write-out
	public void write(String line) {
		try {
			FileWriter fw = new FileWriter(path, true);
			fw.write(line + ";\r\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

}
