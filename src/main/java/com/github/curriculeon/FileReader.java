package com.github.curriculeon;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileReader {

    private final String filename;
    private final ClassLoader classLoader;

    public FileReader(String filename){
        this.filename = filename;
        this.classLoader = getClass().getClassLoader();
    }

    /**
     * Test-friendly constructor that accepts an explicit ClassLoader.
     */
    public FileReader(String filename, ClassLoader classLoader){
        this.filename = filename;
        this.classLoader = classLoader == null ? getClass().getClassLoader() : classLoader;
    }

    @Override
    public String toString(){
    File file = new File(classLoader.getResource(filename).getFile());
        StringBuilder result = new StringBuilder();
        try(Scanner scanner = new Scanner(file)){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }catch(IOException e){
            throw new Error(e);
        }
        return result.toString();
    }

}

