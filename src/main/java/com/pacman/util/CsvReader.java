package com.pacman.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Utility class for reading CSV files.
 * Parses semicolon-separated values.
 */
public class CsvReader {
    
    /**
     * Parses a CSV file from a URI.
     * @param file the URI of the file to parse
     * @return a list of rows, where each row is a list of cell values
     */
    public List<List<String>> parseCsv(URI file) {
        List<List<String>> data = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(file.toURL().openStream());
            BufferedReader br = new BufferedReader(reader);

            String line = br.readLine();
            while (line != null) {
                List<String> lineData = Arrays.asList(line.split(";"));
                data.add(lineData);
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    /**
     * Parses a CSV file from a file path.
     * @param filePath the path to the file
     * @return a list of rows, where each row is a list of cell values
     */
    public List<List<String>> parseCsv(String filePath) {
        List<List<String>> data = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String line = br.readLine();
            while (line != null) {
                List<String> lineData = Arrays.asList(line.split(";"));
                data.add(lineData);
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
