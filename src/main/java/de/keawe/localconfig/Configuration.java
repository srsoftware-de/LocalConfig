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

/**
 * This class provides means to save program configuration
 * @author Stephan Richter
 *
 */
public class Configuration extends TreeMap<String,String>{
	private File configFile = null;
	
	/**
	 * Load configuration from file
	 * @param filename the path to the configuration file
	 */
	public Configuration(String filename) {
		this(new File(filename));
	}
	
	/**
	 * Load configuration from file
	 * @param f the configuration file
	 */
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
	
	public boolean getOrAdd(String key, boolean defaultValue) throws IOException {
		Boolean b = getBool(key);
		if (b != null) return b;
		put(key,defaultValue?"true":"false");
		save();
		return defaultValue;
	}
	
	public double getOrAdd(String key, double defaultValue) throws IOException {
		Double d = getDouble(key);
		if (d != null) return d;
		put(key,""+defaultValue);
		save();
		return defaultValue;
	}
	
	public int getOrAdd(String key, int defaultValue) throws IOException  {
		Integer i = getInt(key);
		if (i != null) return i;
		put(key,""+defaultValue);
		save();
		return defaultValue;
	}
	
	/**
	 * will evaluate 1 or "true" (ignoring case) as true, every other value as false
	 * @param key
	 * @return
	 */
	public Boolean getBool(String key) {
		String s = get(key);
		if (s == null) return false;
		s = s.trim().toLowerCase();
		return s.equals("true") || s.equals("1");
	}
	
	public Double getDouble(String key) {
		String s = get(key);
		if (s==null) return null;
		return Double.parseDouble(s);
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
	
	public String put(String key, Number value) {
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
