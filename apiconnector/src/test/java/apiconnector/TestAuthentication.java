package apiconnector;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.Authenticate;

public class TestAuthentication {
	
	private static final String username = "jvrijn@liacs.nl";
	private static final String password = "secretpassword";
	
	@Test
	public void loginSucces() {
		try {
			Authenticate auth = new OpenmlConnector().openmlAuthenticate(username, password);
			long validUntil = DateParser.mysqlDateToTimeStamp(auth.getValidUntil(),auth.getTimezone());
			Assert.assertTrue( validUntil > new Date().getTime() + Constants.DEFAULT_TIME_MARGIN );
		} catch (Exception e) {
			Assert.fail("Login failed: " + e.getMessage() );
		}
	}
}
