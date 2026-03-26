package com.github.curriculeon;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileReaderHappyPathTest {

    @Test
    public void testToStringReadsResource() {
        FileReader fr = new FileReader("sample.txt");
        String content = fr.toString();
        assertNotNull(content);
        assertTrue(content.contains("This is a sample file for FileReader test."));
    }
}
