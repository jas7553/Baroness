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
    }
    
    public void __init__(String name) {
    }
    
    public void __init__(String name, int age) {
        this.age = age;
    }
    
    @Override
    public void check() {
    }
    
    @Override
    public void print() {
    }
    
    @Override
    public void position() {
        System.out.println("[payroll]: Positioning...");
    }
    
    @Override
    public void pay() {
        System.out.println("[payroll]: Paying " + name());
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
    
    public double test(String s, int i) {
        return 2.0;
    }
    
}
