import java.util.ArrayList;
import java.util.List;

public class EmployeeReport {
    public static void main(String[] args) {
        
        List<personnel.Employee> employees = new ArrayList<>();
        employees.add(new personnel.Research("Jason", 21));
        
        for (personnel.Employee employee : employees) {
            System.out.println(employee);
        }
        
        personnel.Research e1 = new personnel.Research("Jason");
        System.out.println();
        
        payroll.Research e2 = new payroll.Research("Smith");
        System.out.println();
        
        System.out.println(e1);
        System.out.println(e2);
        System.out.println();
        
        e1.check();
        System.out.println();
        
        e2.check();
        System.out.println();
        
        e1.print();
        System.out.println();
        
        e2.print();
        System.out.println();
        
        System.out.println(e1);
        System.out.println(e2);
        System.out.println();
        
//        System.out.println(e1.test("s", 1));
//        System.out.println(e2.test("t", 2));
    }
}
