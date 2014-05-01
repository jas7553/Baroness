package personnel;

public class Research extends Employee {
    
    private String firstName;
    private String lastName;
    private long ssn;
    private int age;
    private long id;
    private String title;
    private float basePay;
    private int yearsOfService;
    
    public Research(String databaseRow) {
    }
    
    public void __init__(String databaseRow) {
        String[] row = databaseRow.split(" ");
        firstName = row[0];
        lastName = row[1];
        ssn = Long.parseLong(row[2]);
        age = Integer.parseInt(row[3]);
        id = Integer.parseInt(row[4]);
        title = row[5];
        basePay = Float.parseFloat(row[6]);
        yearsOfService = Integer.parseInt(row[7]);
        
        name = firstName + " " + lastName;
    }
    
    @Override
    public String name() {
        return super.name();
    }

    @Override
    public void check() {
        System.out.println("[personnel] Checking...");
    }

    @Override
    public void print() {
        System.out.println("[personnel] Printing...");
    }

    @Override
    public void position() {
    }

    @Override
    public void pay() {
    }
    
    @Override
    public String toString() {
        return "[personnel] Name: " + name() + ", Age: " + age + ", PersonnelId: " + id;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public double test(String s, int i) {
        return 1.0;
    }
    
}
