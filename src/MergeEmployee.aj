import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeEmployee {

private int constructingA = 0;
private int constructingA2 = 0;

private int constructingB = 0;
private int constructingB2 = 0;

private final Map<personnel.Employee, payroll.Employee> personnelTopayrollMapping = new WeakHashMap<>();
private final Map<payroll.Employee, personnel.Employee> payrollTopersonnelMapping = new WeakHashMap<>();

// Merge personnel.Employee.name and payroll.Employee.name
void around(String name): set(String personnel.Employee.name) && args(name) && !within(MergeEmployee) {
    personnel.Employee personnelEmployee = (personnel.Employee) thisJoinPoint.getTarget();
    personnelEmployee.name = name;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelEmployee);
        payroll.Employee payrollEmployee = personnelTopayrollMapping.get(personnelEmployee);
        payrollEmployee.name = name;
    }
}
void around(String name): set(String payroll.Employee.name) && args(name) && !within(MergeEmployee) {
    payroll.Employee payrollEmployee = (payroll.Employee) thisJoinPoint.getTarget();
    payrollEmployee.name = name;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollEmployee);
        personnel.Employee personnelEmployee = payrollTopersonnelMapping.get(payrollEmployee);
        personnelEmployee.name = name;
    }
}

}
