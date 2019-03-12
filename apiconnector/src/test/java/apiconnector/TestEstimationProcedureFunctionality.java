package apiconnector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openml.apiconnector.xml.EstimationProcedure;
import org.openml.apiconnector.xml.EstimationProcedures;

import testbase.BaseTestFramework;

public class TestEstimationProcedureFunctionality extends BaseTestFramework {
	
	@Test
	public void testApiEstimationProcedureGet() throws Exception {
		EstimationProcedure ep = client_read_test.estimationProcedureGet(1);
		assertEquals(ep.getId(), 1);
		assertEquals(ep.getTtid(), 1);
	}

	@Test
	public void testApiEstimationProcedureList() throws Exception {
		EstimationProcedures ep = client_read_test.estimationProcedureList();
		assertTrue(ep.getEstimationProcedure().length > 10);
	}
}
