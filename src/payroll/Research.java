package payroll;

public class Research extends Employee {
    
    private int age;
    
    public int payrollId;
    
//    public Research(String name) {
//        this(name, 10);
//        System.out.println("[payroll] Constructor 1");
//    }
    
    public Research(String name, int age) {
        super(name);
        System.out.println("[payroll] Constructor 2");
        this.age = age;
    }
    
    public Research(personnel.Research personnelResearch) {
//        super(personnelResearch.name);
        System.out.println("[payroll] Copy constructor");
        
//        this.name = personnelResearch.name;
//        this.age = personnelResearch.getAge();
        this.payrollId = personnelResearch.getAge() * 4;
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
        return "[payroll] Name: " + name() + ", Age: " + age + ", PayrollId: " + payrollId;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
}
