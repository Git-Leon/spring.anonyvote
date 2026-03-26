package com.github.curriculeon;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class FileReaderErrorBranchTest {

    @Test
    public void testToStringThrowsWrappedErrorWhenIOFails() throws IOException {
        // Create a temporary file and obtain its URL
        File temp = File.createTempFile("fr-test", ".txt");
        String name = temp.getName();

        // Custom ClassLoader that returns a URL pointing to the temp file for getResource
        final URL resourceUrl = temp.toURI().toURL();
        ClassLoader loader = new ClassLoader() {
            @Override
            public URL getResource(String resourceName) {
                if (resourceName.equals(name)) return resourceUrl;
                return null;
            }
        };

        // Delete the file so opening it will fail with FileNotFoundException (an IOException)
        assertTrue(temp.delete());

        FileReader fr = new FileReader(name, loader);

        try {
            fr.toString();
            fail("Expected Error to be thrown when underlying IO fails");
        } catch (Error e){
            // Expected: FileReader wraps IOExceptions into Error
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IOException);
        }
    }
}
