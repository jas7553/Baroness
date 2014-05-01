import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import personnel.Research;

public class Report {
    
    private final List<Research> researchers;
    
    public Report() {
        researchers = readDatabase("src/employee_database.txt");
        
        for (Research r : researchers) {
            System.out.println(r);
        }
        
        for (Research r : researchers) {
            r.pay();
        }
    }
    
    public void generateReport() {
        
    }
    
    public void printReport() {
        
    }
    
    private static List<Research> readDatabase(String databaseFilename) {
        List<Research> researchers = new ArrayList<>();
        
        Scanner inFile1 = null;
        
        try {
            inFile1 = new Scanner(new File(databaseFilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        String line = new String();
        while (inFile1.hasNext()) {
            line = inFile1.nextLine();
            Research r = new Research(line);
            researchers.add(r);
        }
        
        inFile1.close();
        
        return researchers;
    }
    
    public static void main(String[] args) {
        Report r = new Report();
        r.generateReport();
        r.printReport();
    }
    
}
