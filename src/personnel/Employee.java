package personnel;

public abstract class Employee {
    public String name;
    
    public Employee() {
    }
    
    public Employee(String name) {
        this.name = name;
    }
    
    public String name() {
        return name;
    }
    
    public abstract void check();
    
    public abstract void print();
    
    public abstract void position();
    
    public abstract void pay();
}
