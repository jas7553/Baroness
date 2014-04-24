package payroll;

public class Research extends Employee {
    
    public Research(String name) {
        super(name);
    }
    
    @Override
    public void check() {
        System.out.println("Checking payroll.Research");
    }
    
    @Override
    public void print() {
        System.out.println("Printing payroll.Research");
    }
    
}
