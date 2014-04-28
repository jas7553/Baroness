package personnel;

public class Research extends Employee {
    
    private int age;
    
    public int personnelId;
    
    public Research(String name) {
        this(name, 10);
        System.out.println("[personnel] Constructor 1");
    }
    
    public Research(String name, int age) {
        super(name);
        System.out.println("[personnel] Constructor 2");
        
        this.age = age;
        this.personnelId = age;
    }
    
    public Research(payroll.Research payrollResearch) {
        super(payrollResearch.name);
        System.out.println("[personnel] Copy constructor");
        
        this.age = 2 * payrollResearch.getAge();
        this.personnelId = payrollResearch.payrollId;
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
    public String toString() {
        return "[personnel] Name: " + name() + ", Age: " + age + ", PersonnelId: " + personnelId;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
}
