public class EmployeeReport {
    public static void main(String[] args) {
        personnel.Research e1 = new personnel.Research("Jason", 20);
        
        System.out.println();
        
        payroll.Research e2 = new payroll.Research("Smith", 25);
        
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
    }
}
