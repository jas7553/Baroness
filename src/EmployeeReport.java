import personnel.Employee;
import personnel.Research;

public class EmployeeReport {
    public static void main(String[] args) {
        Employee e1 = new Research("Jason");
        Employee e2 = new Research("Smith");
        
        System.out.println(e1.name());
        System.out.println(e2.name());
    }
}
