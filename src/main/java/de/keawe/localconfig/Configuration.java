package de.keawe.localconfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;

import de.keawe.tools.translations.Translation;

public class Configuration extends TreeMap<String,String>{
	private File configFile = null;
	
	public Configuration(String filename) {
		this(new File(filename));
	}
	
	public Configuration(File f) {
		super();
		configFile = f;
		try {
			load();
		} catch (IOException e) {
			System.out.println(Translation.get(this,"Trying to load configuration from non-existing file (#).",f));
			System.out.println(Translation.get(this,"New configuration file will be created on save."));
		}
	}

	public void copy(Configuration source, String field) {
		put(field,source.get(field));
	}
	
	/**
	 * returns the directory path, where config files should be stored.
	 * @param programName the name of the program, for which the configuration is managed.
	 * @return
	 */
	public static String dir(String programName) {
		String filename = System.getProperty("user.home")+"/.config/"+programName;
		return filename;
	}
	
	public File file() {
		return configFile;
	}

	public String getOrAdd(String key, String defaultValue) throws IOException {
		String s = get(key);
		if (s != null) return s;
		put(key,defaultValue);
		save();
		return defaultValue;
	}
	
	public int getOrAddInt(String key, int defaultValue) throws IOException  {
		Integer i = getInt(key);
		if (i != null) return i;
		put(key,""+defaultValue);
		save();
		return defaultValue;
	}
	
	public Integer getInt(String key) {
		String s = get(key);
		if (s==null) return null;
		return Integer.parseInt(s);
	}
	
	public static SimpleEntry<String, String> keyValue(String line) throws UnexpectedException {
		String [] parts = line.split("=");
		if (parts.length<2) throw new UnexpectedException("Not a key-value pair: "+line);
		SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<String,String>(parts[0].trim(),parts[1].trim());
		return entry;
	}
	
	private void load() throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(configFile));
		
		String line = null;
		while ((line = file.readLine()) != null) {
			line = uncomment(line);
			if (line.trim().isEmpty()) continue;
			SimpleEntry<String, String> entry = keyValue(line);
			put(entry.getKey(),entry.getValue());
		}

		file.close();
	}
	
	@Override
	public String put(String key, String value) {
		return super.put(key.trim(), value.trim());
	}
	
	public String put(String key, long value) {
		return put(key,""+value);
	}
	
	public void save() throws IOException {
		configFile.getParentFile().mkdirs(); // try to create parent path
		FileWriter file = new FileWriter(configFile);
		for (Map.Entry<String, String> entry : this.entrySet()) file.write(entry.getKey()+" = "+entry.getValue()+"\n");
		file.close();
	}

	public void saveAs(File file) throws IOException {
		configFile = file;
		save();		
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName()+":\n");
		for (Map.Entry<String, String> entry : entrySet()) sb.append("\t"+entry.getKey()+" = "+entry.getValue()+"\n");
		return sb.toString();
	}

	public static String uncomment(String line) {
		String[] parts = line.split("#",2); // remove everything behind a sharp
		return parts[0];
	}
}
