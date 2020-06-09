package de.keawe.examples.localconfig;

import java.io.IOException;

import de.keawe.localconfig.Configuration;

public class App {
	public static void main(String[] args) throws IOException {
		String filename = Configuration.dir("ConfigTest.config");
		System.out.println("Storing configuration in "+filename);
		
		Configuration config = new Configuration(filename);
		config.put("my_long", 1234);
		config.put("my_String", "Dies ist ein Test");
		System.out.println(config);
		config.save();
		
		Configuration cf2 = new Configuration(filename);
		System.out.println(cf2);
	}
}
