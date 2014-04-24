import personnel.Employee;

public class EmployeeReport {
    public static void main(String[] args) {
        Employee e1 = new Employee("Jason");
        Employee e2 = new Employee("Smith");
        
        System.out.println(e1.name());
        System.out.println(e2.name());
        
        e1.setName("Homer");
        e2.setName("Simpson");
        
        System.out.println(e1.name());
        System.out.println(e2.name());
    }
}
