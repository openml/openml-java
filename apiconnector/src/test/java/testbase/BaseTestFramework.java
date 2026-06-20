package testbase;

import org.junit.Before;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;


// important that name does not start with test
public class BaseTestFramework {
	private static final ServerConfig test_server = new StagingServerConfig();

	protected static final String subdomain_test = test_server.subdomain();
	protected static final String url_test = test_server.url();
	private static final String url_live = "https://www.openml.org/";

	protected static final OpenmlConnector client_admin_test = new OpenmlConnector(url_test, test_server.apiKeyAdmin());
	protected static final OpenmlConnector client_write_test = new OpenmlConnector(url_test, test_server.apiKeyWrite());
	protected static final OpenmlConnector client_read_test = new OpenmlConnector(url_test, test_server.apiKeyRead());
	protected static final OpenmlConnector client_read_live = new OpenmlConnector(url_live, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f");

	protected static final XStream xstream = XstreamXmlMapping.getInstance();

	protected static final boolean VERBOSE = false;

	@Before
	public void setup() {
		Settings.CACHE_ALLOWED = false;
		// for the functions that do use cache
		Settings.CACHE_DIRECTORY = System.getProperty("user.home") + "/.openml_" + subdomain_test + "/cache";

		if (VERBOSE) {
			client_admin_test.setVerboseLevel(1);
			client_write_test.setVerboseLevel(1);
			client_read_test.setVerboseLevel(1);
			client_read_live.setVerboseLevel(1);
		}
	}
}


interface ServerConfig {
	String subdomain();
	String url();
	String apiKeyWrite();
	String apiKeyRead();
	String apiKeyAdmin();
}
class TestServerConfig implements ServerConfig{

	@Override
	public String subdomain() {
		return "test";
	}

	@Override
	public String url() {
		return "https://test.openml.org/";
	}

	@Override
	public String apiKeyWrite() {
		return "8baa83ecddfe44b561fd3d92442e3319";
	}

	@Override
	public String apiKeyRead() {
		return "6a4e0925273c6c9e2709b8b5179755c2";  // user id 3345, vanrijn@freiburg
	}

	@Override
	public String apiKeyAdmin() {
		return "d488d8afd93b32331cf6ea9d7003d4c3";
	}
}

class StagingServerConfig implements ServerConfig{

	@Override
	public String subdomain() {
		return "staging";
	}

	@Override
	public String url() {
		return "https://staging.openml.org/";
	}

	@Override
	public String apiKeyWrite() {
		return "normaluser";
	}

	@Override
	public String apiKeyRead() {
		return "readonly";
	}

	@Override
	public String apiKeyAdmin() {
		return "abc";
	}
}

class DockerComposeServerConfig implements ServerConfig{

	@Override
	public String subdomain() {
		return null;
	}

	@Override
	public String url() {
		return "http://localhost:8080/";
	}

	@Override
	public String apiKeyWrite() {
		return "AD000000000000000000000000000000";
	}

	@Override
	public String apiKeyRead() {
		return "AD000000000000000000000000000000";
	}

	@Override
	public String apiKeyAdmin() {
		return "AD000000000000000000000000000000";
	}
}



