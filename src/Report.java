import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import personnel.Research;

public class Report {
    
    private final List<Research> researchers;
    
    public Report() {
        researchers = new ArrayList<>();
        
        Scanner inFile1 = null;
        
        try {
            inFile1 = new Scanner(new File("src/employee_database.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        String line = new String();
        while (inFile1.hasNext()) {
            line = inFile1.nextLine();
            Research r = researchFromLine(line);
        }
        
        inFile1.close();
    }
    
    private static Research researchFromLine(String line) {
        String[] row = line.split(" ");
        String firstName = row[0];
        String lastName = row[1];
        System.out.println(row[2]);
        long id = Long.parseLong(row[2]);
        int age = Integer.parseInt(row[3]);
        String title = row[4];
        float basePay = Float.parseFloat(row[5]);
        Research r = new Research(firstName + " " + lastName, age);
        return r;
    }
    
    public void generateReport() {
        
    }
    
    public void printReport() {
        
    }
    
    public static void main(String[] args) {
        Report r = new Report();
        r.generateReport();
        r.printReport();
    }
    
}
