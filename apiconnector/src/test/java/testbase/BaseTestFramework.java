package testbase;

import org.junit.Before;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

// important that name does not start with test
public class BaseTestFramework {
	
	protected static final String url_test = "https://test.openml.org/";
	protected static final String url_live = "https://www.openml.org/";
	protected static final OpenmlConnector client_admin_test = new OpenmlConnector(url_test,"d488d8afd93b32331cf6ea9d7003d4c3"); 
	protected static final OpenmlConnector client_write_test = new OpenmlConnector(url_test, "8baa83ecddfe44b561fd3d92442e3319");
	protected static final OpenmlConnector client_read_test = new OpenmlConnector(url_test, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f"); 
	protected static final OpenmlConnector client_read_live = new OpenmlConnector(url_live, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f"); 
	
	protected static final XStream xstream = XstreamXmlMapping.getInstance();

	protected static final boolean VERBOSE = false;
	
	@Before
    public void setup() {
		Settings.CACHE_ALLOWED = false;
		// for the functions that do use cache
		Settings.CACHE_DIRECTORY = System.getProperty("user.home") + "/.openml_test/cache";
		
		if (VERBOSE) {
			client_admin_test.setVerboseLevel(1);
			client_write_test.setVerboseLevel(1);
			client_read_test.setVerboseLevel(1);
			client_read_live.setVerboseLevel(1);
		}
    }
}
