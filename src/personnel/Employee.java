package personnel;

public class Employee {
    private String name;
    
    public Employee(String name) {
        this.name = name;
    }
    
    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
