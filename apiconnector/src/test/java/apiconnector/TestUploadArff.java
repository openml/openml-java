package apiconnector;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.UploadDataSet;

public class TestUploadArff {
	
	private static final boolean VERBOSE = false;
	private static final String DATASETPATH = "data" + File.separator + "arff_test" + File.separator;
	private static final String url = "https://test.openml.org/";
	private static final OpenmlConnector client_write = new OpenmlConnector(url, "8baa83ecddfe44b561fd3d92442e3319");

	@Test
	public void testUploadDataset() throws IOException {
		if (VERBOSE) {
			client_write.setVerboseLevel(1);
		}
		
		// Test XML description
		final File description = TestDataFunctionality.createTestDatasetDescription();
		Path path = Paths.get(DATASETPATH);
		// Pass through each dataset on the directory
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
			System.out.println("ARFF-check processing " + file.getFileName());
			File toUpload = new File(file.toString());
			int id = -1;
			// boolean to signal the validity of a dataset
			boolean invalid = file.getFileName().toString().startsWith("invalid");
			
			try {
				UploadDataSet ud = client_write.dataUpload(description, toUpload);
				id = ud.getId();
				// Only reached by a dataset (ARFF file) that gets uploaded.
				client_write.dataDelete(id);
			} catch(Exception e) {
				if (VERBOSE) {
					e.printStackTrace();
				}
			} finally {
				if(invalid) {
					assertTrue(id == -1);
				} else {
					assertTrue(id != -1);
				}
				// Reset the dataset id
				id = -1;
			}
			// Keep going through datasets
			return FileVisitResult.CONTINUE;
		}});
	}
}