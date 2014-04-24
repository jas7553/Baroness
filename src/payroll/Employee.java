package payroll;

public abstract class Employee {
    
    private String name;
    
    public Employee(String name) {
        this.name = "new name here!";
    }
    
    public abstract void check();
    
    public abstract void print();
    
}
