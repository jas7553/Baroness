import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeResearch {

private int constructingA = 0;
private int constructingA2 = 0;

private int constructingB = 0;
private int constructingB2 = 0;

private final Map<personnel.Research, payroll.Research> personnelTopayrollMapping = new WeakHashMap<>();
private final Map<payroll.Research, personnel.Research> payrollTopersonnelMapping = new WeakHashMap<>();

before(String databaseRow): execution(personnel.Research.new(String)) && args(databaseRow) {
    constructingA++;
}

after(String databaseRow) returning: execution(personnel.Research.new(String)) && args(databaseRow) {
    personnel.Research personnelResearch = null;
    payroll.Research payrollResearch = null;
    if ((constructingA2 == 0) && (constructingB == 0)) {
        constructingA2++;
        personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
        payrollResearch = new payroll.Research(databaseRow);
        assert personnelResearch != null; assert payrollResearch != null;
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        constructingA2--;
    }
    constructingA--;
    
    if (personnelResearch != null) personnelResearch.__init__(databaseRow);
    if (payrollResearch != null) payrollResearch.__init__(databaseRow);
}

before(String databaseRow): execution(payroll.Research.new(String)) && args(databaseRow) {
    constructingB++;
}

after(String databaseRow) returning: execution(payroll.Research.new(String)) && args(databaseRow) {
    payroll.Research payrollResearch = null;
    personnel.Research personnelResearch = null;
    if ((constructingB2 == 0) && (constructingA == 0)) {
        constructingB2++;
        payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
        personnelResearch = new personnel.Research(databaseRow);
        assert payrollResearch != null; assert personnelResearch != null;
        payrollTopersonnelMapping.put(payrollResearch, personnelResearch);
        personnelTopayrollMapping.put(personnelResearch, payrollResearch);
        constructingB2--;
    }
    constructingB--;
    
    if (payrollResearch != null) payrollResearch.__init__(databaseRow);
    if (personnelResearch != null) personnelResearch.__init__(databaseRow);
}

// Merge personnel.Research.firstName and payroll.Research.firstName
void around(String firstName): set(String personnel.Research.firstName) && args(firstName) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.firstName = firstName;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.firstName = firstName;
    }
}
void around(String firstName): set(String payroll.Research.firstName) && args(firstName) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.firstName = firstName;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.firstName = firstName;
    }
}

// Merge personnel.Research.lastName and payroll.Research.lastName
void around(String lastName): set(String personnel.Research.lastName) && args(lastName) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.lastName = lastName;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.lastName = lastName;
    }
}
void around(String lastName): set(String payroll.Research.lastName) && args(lastName) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.lastName = lastName;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.lastName = lastName;
    }
}

// Merge personnel.Research.ssn and payroll.Research.ssn
void around(long ssn): set(long personnel.Research.ssn) && args(ssn) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.ssn = ssn;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.ssn = ssn;
    }
}
void around(long ssn): set(long payroll.Research.ssn) && args(ssn) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.ssn = ssn;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.ssn = ssn;
    }
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

// Merge personnel.Research.id and payroll.Research.id
void around(long id): set(long personnel.Research.id) && args(id) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.id = id;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.id = id;
    }
}
void around(long id): set(long payroll.Research.id) && args(id) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.id = id;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.id = id;
    }
}

// Merge personnel.Research.title and payroll.Research.title
void around(String title): set(String personnel.Research.title) && args(title) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.title = title;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.title = title;
    }
}
void around(String title): set(String payroll.Research.title) && args(title) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.title = title;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.title = title;
    }
}

// Merge personnel.Research.basePay and payroll.Research.basePay
void around(float basePay): set(float personnel.Research.basePay) && args(basePay) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.basePay = basePay;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.basePay = basePay;
    }
}
void around(float basePay): set(float payroll.Research.basePay) && args(basePay) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.basePay = basePay;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.basePay = basePay;
    }
}

// Merge personnel.Research.yearsOfService and payroll.Research.yearsOfService
void around(int yearsOfService): set(int personnel.Research.yearsOfService) && args(yearsOfService) && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    personnelResearch.yearsOfService = yearsOfService;
    
    if (constructingA == 0) {
        assert personnelTopayrollMapping.containsKey(personnelResearch);
        payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
        payrollResearch.yearsOfService = yearsOfService;
    }
}
void around(int yearsOfService): set(int payroll.Research.yearsOfService) && args(yearsOfService) && !within(MergeResearch) {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    payrollResearch.yearsOfService = yearsOfService;
    
    if (constructingB == 0) {
        assert payrollTopersonnelMapping.containsKey(payrollResearch);
        personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
        personnelResearch.yearsOfService = yearsOfService;
    }
}

// override payroll.Research.name with personnel.Research.name
String around(): call(String payroll.Research.name()) && args() {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    return personnelResearch.name();
}

// override personnel.Research.pay with payroll.Research.pay
void around(): call(void personnel.Research.pay()) && args() {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.pay();
}

// override personnel.Research.position with payroll.Research.position
void around(): call(void personnel.Research.position()) && args() {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.position();
}

// override payroll.Research.toString with personnel.Research.toString
String around(): call(String payroll.Research.toString()) && args() {
    payroll.Research payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
    personnel.Research personnelResearch = payrollTopersonnelMapping.get(payrollResearch);
    return personnelResearch.toString();
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
