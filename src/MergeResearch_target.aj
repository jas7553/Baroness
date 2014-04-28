import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeResearch {

private boolean constructing = false;

private Map<personnel.Research, payroll.Research> personnelTopayrollMapping = new WeakHashMap<>();
private Map<payroll.Research, personnel.Research> payrollTopersonnelMapping = new WeakHashMap<>();

after(personnel.Research newlyCreatedObject) returning: this(newlyCreatedObject) && execution(personnel.Research.new(..)) {
    if (!constructing) {
        constructing = true;
        personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
        payroll.Research payrollResearch = new payroll.Research(personnelResearch);
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        constructing = false;
    }
}

after(payroll.Research newlyCreatedObject) returning: this(newlyCreatedObject) && execution(payroll.Research.new(..)) {
    if (!constructing) {
        constructing = true;
        payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
        personnel.Research personnelResearch = new personnel.Research(payrollResearch);
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        constructing = false;
    }
}

// Replace personnel.Research.age with payroll.Research.age
int around(): get(int age) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    return payrollResearch.age;
}
void around(int newval): set(int personnel.Research.age) && args(newval) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.age = newval;
}

after(): call(void personnel.Research.check(..)) && args() && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.check();
}

after(): call(void payroll.Research.check(..)) && args() && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.check();
}

after(): call(void personnel.Research.print(..)) && args() && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.print();
}

after(): call(void payroll.Research.print(..)) && args() && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.print();
}

}
