package payroll;

public class Research extends Employee {
    
    // From personnel.Research
    public int personnelId;
    private String firstName;
    private String lastName;
    private long ssn;
    private int age;
    private long id;
    private String title;
    private float basePay;
    private int yearsOfService;
    
    // For payroll.Research
    private double hourlyRate;
    
    public Research(String databaseRow) {
    }
    
    public void __init__(String databaseRow) {
        // A $5 bonus for every year of service to the company
        hourlyRate = basePay + ((yearsOfService/5) * 5);
    }
    
    @Override
    public String name() {
        return super.name();
    }
    
    @Override
    public void check() {
    }
    
    @Override
    public void print() {
    }
    
    @Override
    public void position() {
    }
    
    @Override
    public void pay() {
        System.out.println("[payroll]: Paying " + name() + " $" + hourlyRate);
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    public double test(String s, int i) {
        return 2.0;
    }
    
}
