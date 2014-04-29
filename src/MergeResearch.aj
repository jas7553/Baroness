import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeResearch {

private int constructingA = 0;
private int constructingA2 = 0;

private int constructingB = 0;
private int constructingB2 = 0;

private final Map<personnel.Research, payroll.Research> personnelTopayrollMapping = new WeakHashMap<>();
private final Map<payroll.Research, personnel.Research> payrollTopersonnelMapping = new WeakHashMap<>();

before(String name): execution(personnel.Research.new(String)) && args(name) {
    constructingA++;
}

after(String name) returning: execution(personnel.Research.new(String)) && args(name) {
    personnel.Research personnelResearch = null;
    payroll.Research payrollResearch = null;
    if ((constructingA2 == 0) && (constructingB == 0)) {
        constructingA2++;
        personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
        payrollResearch = new payroll.Research(name);
        assert personnelResearch != null; assert payrollResearch != null;
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        constructingA2--;
    }
    constructingA--;
    
    if (personnelResearch != null) personnelResearch.__init__(name);
    if (payrollResearch != null) payrollResearch.__init__(name);
}

before(String name): execution(payroll.Research.new(String)) && args(name) {
    constructingB++;
}

after(String name) returning: execution(payroll.Research.new(String)) && args(name) {
    payroll.Research payrollResearch = null;
    personnel.Research personnelResearch = null;
    if ((constructingB2 == 0) && (constructingA == 0)) {
        constructingB2++;
        payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
        personnelResearch = new personnel.Research(name);
        assert payrollResearch != null; assert personnelResearch != null;
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        constructingB2--;
    }
    constructingB--;
    
    if (payrollResearch != null) payrollResearch.__init__(name);
    if (personnelResearch != null) personnelResearch.__init__(name);
}
before(String name, int age): execution(personnel.Research.new(String, int)) && args(name, age) {
    constructingA++;
}

after(String name, int age) returning: execution(personnel.Research.new(String, int)) && args(name, age) {
    personnel.Research personnelResearch = null;
    payroll.Research payrollResearch = null;
    if ((constructingA2 == 0) && (constructingB == 0)) {
        constructingA2++;
        personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
        payrollResearch = new payroll.Research(name, age);
        assert personnelResearch != null; assert payrollResearch != null;
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        constructingA2--;
    }
    constructingA--;
    
    if (personnelResearch != null) personnelResearch.__init__(name, age);
    if (payrollResearch != null) payrollResearch.__init__(name, age);
}

before(String name, int age): execution(payroll.Research.new(String, int)) && args(name, age) {
    constructingB++;
}

after(String name, int age) returning: execution(payroll.Research.new(String, int)) && args(name, age) {
    payroll.Research payrollResearch = null;
    personnel.Research personnelResearch = null;
    if ((constructingB2 == 0) && (constructingA == 0)) {
        constructingB2++;
        payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
        personnelResearch = new personnel.Research(name, age);
        assert payrollResearch != null; assert personnelResearch != null;
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        constructingB2--;
    }
    constructingB--;
    
    if (payrollResearch != null) payrollResearch.__init__(name, age);
    if (personnelResearch != null) personnelResearch.__init__(name, age);
}

// Merge personnel.Research.age and payroll.Research.age
void around(int age): set(int personnel.Research.age) && args(age) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.age = age;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.age = age;
    }
}
void around(int age): set(int payroll.Research.age) && args(age) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.age = age;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.age = age;
    }
}

// Merge personnel.Research.check and payroll.Research.check
after(): call(void personnel.Research.check()) && args() && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.check();
}
after(): call(void payroll.Research.check()) && args() && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.check();
}

// Merge personnel.Research.print and payroll.Research.print
after(): call(void personnel.Research.print()) && args() && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.print();
}
after(): call(void payroll.Research.print()) && args() && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.print();
}

// Merge personnel.Research.test and payroll.Research.test
after(String s, int i): call(double personnel.Research.test(String, int)) && args(s, i) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.test(s, i);
}
after(String s, int i): call(double payroll.Research.test(String, int)) && args(s, i) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    personnelResearch.test(s, i);
}

}
