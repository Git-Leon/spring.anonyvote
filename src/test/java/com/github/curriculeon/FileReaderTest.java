package com.github.curriculeon;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FileReaderTest {

    @Test
    public void toString_readsResourceContent() {
        FileReader reader = new FileReader("testfile.txt");
        String content = reader.toString();
        assertTrue(content.contains("Hello from test file"));
    }
}
