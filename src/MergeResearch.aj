import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeResearch {

private boolean constructingA = false;
private boolean constructingA2 = false;

private boolean constructingB = false;
private boolean constructingB2 = false;

private final Map<personnel.Research, payroll.Research> personnelTopayrollMapping = new WeakHashMap<>();
private final Map<payroll.Research, personnel.Research> payrollTopersonnelMapping = new WeakHashMap<>();

before(): execution(personnel.Research.new(..)) {
    constructingA = true;
}

after(personnel.Research newlyCreatedObject) returning: this(newlyCreatedObject) && execution(personnel.Research.new(..)) {
    if (!constructingA2 && !constructingB) {
        constructingA2 = true;
        personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
        payroll.Research payrollResearch = new payroll.Research(personnelResearch);
        assert payrollResearch != null; assert personnelResearch != null;
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
//        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        constructingA2 = false;
    }
    constructingA = false;
}

before(): execution(payroll.Research.new(..)) {
    constructingB = true;
}

after(payroll.Research newlyCreatedObject) returning: this(newlyCreatedObject) && execution(payroll.Research.new(..)) {
    if (!constructingB2 && !constructingA) {
        constructingB2 = true;
        payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
        personnel.Research personnelResearch = new personnel.Research(payrollResearch);
        assert payrollResearch != null; assert personnelResearch != null;
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
//        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        constructingB2 = false;
    }
    constructingB = false;
}

// Merge personnel.Research.age and payroll.Research.age
void around(int newval): set(int personnel.Research.age) && args(newval) && !within(MergeResearch) {
    if (constructingA) {
        return;
    }
    
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.age = newval;
    
    if (!constructingB) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        
        payrollResearch.age = newval;
    }
}

void around(int newval): set(int payroll.Research.age) && args(newval) && !within(MergeResearch) {
    if (constructingB) {
        return;
    }
    
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.age = newval;
    
    if (!constructingA) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    
        personnelResearch.age = newval;
    }
}

after(): call(void personnel.Research.check(..)) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.check();
}

after(): call(void payroll.Research.check(..)) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.check();
}

after(): call(void payroll.Research.print(..)) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.print();
    
    System.out.println(personnelTopayrollMapping);
    System.out.println(payrollTopersonnelMapping);
}

after(): call(void personnel.Research.print(..)) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.print();
}

}