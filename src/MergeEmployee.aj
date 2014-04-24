import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeEmployee {

private personnel.Employee personnelEmployee;
private payroll.Employee payrollEmployee;

private Map<personnel.Employee, payroll.Employee> personnelTopayrollMapping = new WeakHashMap<>();
private Map<payroll.Employee, personnel.Employee> payrollTopersonnelMapping = new WeakHashMap<>();

// Replace payroll.Employee.name with personnel.Employee.name
String around(): get(String payroll.Employee.name) && !within(MergeEmployee) {
    payroll.Employee payrollEmployee = (payroll.Employee) thisJoinPoint.getTarget();
    personnel.Employee personnelEmployee = payrollTopersonnelMapping.get(payrollEmployee);
    return personnelEmployee.name;
}
void around(String newval): set(String payroll.Employee.name) && args(newval) && !within(MergeEmployee) {
    payroll.Employee payrollEmployee = (payroll.Employee) thisJoinPoint.getTarget();
    personnel.Employee personnelEmployee = payrollTopersonnelMapping.get(payrollEmployee);
    personnelEmployee.name = newval;
}

}
