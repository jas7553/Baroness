package personnel;

public abstract class Employee {
    private String name;
    
    public Employee(String name) {
        this.name = name;
    }
    
    public String name() {
        return name;
    }
    
    public abstract void check();
    
    public abstract void print();
}
