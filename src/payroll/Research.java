package payroll;

public class Research extends Employee {
    
    private int age;
    
    public Research(String name) {
        this(name, 20);
        System.out.println("[payroll] Constructor 1");
    }
    
    public Research(String name, int age) {
        super(name);
        System.out.println("[payroll] Constructor 2");
        
        this.age = age;
    }
    
    public Research(personnel.Research personnelResearch) {
        super(personnelResearch.name);
        System.out.println("[payroll] Copy constructor");
        
        this.age = personnelResearch.getAge();
    }

    @Override
    public void check() {
        System.out.println("[payroll] Checking...");
    }
    
    @Override
    public void print() {
        System.out.println("[payroll] Printing...");
    }
    
    @Override
    public String toString() {
        return "[payroll] Name: " + name() + ", Age: " + age;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
}
