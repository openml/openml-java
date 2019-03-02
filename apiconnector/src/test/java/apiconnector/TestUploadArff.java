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
import org.openml.apiconnector.xml.DataSetDescription;

public class TestUploadArff extends TestBase {
	
	private static final String DATASETPATH = "data" + File.separator + "arff_test" + File.separator;
	
	@Test
	public void testUploadDataset() throws IOException {
		// Test XML description
		Path path = Paths.get(DATASETPATH);
		// Pass through each dataset on the directory
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
			if (VERBOSE) {
				System.out.println("ARFF-check processing " + file.getFileName());
			}
			File toUpload = new File(file.toString());
			int id = -1;
			// boolean to signal the validity of a dataset
			boolean invalid = file.getFileName().toString().startsWith("invalid");
			
			try {
				DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
				id = client_write_test.dataUpload(dsd, toUpload);
				// Only reached by a dataset (ARFF file) that gets uploaded.
				client_write_test.dataDelete(id);
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
