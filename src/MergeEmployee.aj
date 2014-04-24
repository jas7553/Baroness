public privileged aspect MergeEmployee {

private personnel.Employee personnelEmployee;
private payroll.Employee payrollEmployee;

pointcut personnelEmployeeConstructor(String name) :
    call(personnel.Employee.new(String)) &&
    args(name) &&
    !within(MergeEmployee);

pointcut payrollEmployeeConstructor(String name) :
    call(payroll.Employee.new(String)) &&
    args(name) &&
    !within(MergeEmployee);

personnel.Employee around() : personnelEmployeeConstructor(String) {
    proceed();
    return this.personnelEmployee;
}

payroll.Employee around() : payrollEmployeeConstructor(String) {
    proceed();
    return this.payrollEmployee;
}

before(String name) : personnelEmployeeConstructor(name) {
    if (this.personnelEmployee == null) {
        this.personnelEmployee = new personnel.Employee(name);
        this.payrollEmployee = new payroll.Employee(name);
    }
}

before(String name) : payrollEmployeeConstructor(name) {
    if (this.payrollEmployee == null) {
        this.payrollEmployee = new payroll.Employee(name);
        this.personnelEmployee = new personnel.Employee(name);
    }
}

// Replace payroll.Employee.name with personnel.Employee.name
String around(): get(String payroll.Employee.name) && !within(MergeEmployee) {
    return this.personnelEmployee.name;
}
void around(String newval): set(String payroll.Employee.name) && args(newval) && !within(MergeEmployee) {
    this.personnelEmployee.name = newval;
}

}
