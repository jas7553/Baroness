package personnel;

public class Research extends Employee {
    
    public Research(String name) {
        super("New Name");
    }
    
    @Override
    public void check() {
        System.out.println("Checking personnel.Employee");
    }
    
    @Override
    public void print() {
        System.out.println("Printing personnel.Employee");
    }
    
}
