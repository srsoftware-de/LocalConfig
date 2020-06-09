package de.keawe.examples.localconfig;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.keawe.localconfig.Configuration;
import de.keawe.tools.translations.Translation;

public class App {
	private static Logger log = LoggerFactory.getLogger(App.class);
	
	private static String t(String txt,Object...fills) {
		return Translation.get(App.class, txt, fills);
	}
	
	public static void main(String[] args) throws IOException {
		String filename = Configuration.dir("ConfigTest.config");
		log.debug(t("Storing configuration in {}."),filename);
		
		Configuration config = new Configuration(filename);
		config.put("my_long", 1234);
		config.put("my_String", t("This is a test"));
		log.debug("{}",config);
		config.save();
		
		Configuration cf2 = new Configuration(filename);
		log.debug("{}",cf2);
	}
}
