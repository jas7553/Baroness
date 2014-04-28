import java.util.Map;
import java.util.WeakHashMap;

public privileged aspect MergeResearch {

private personnel.Research personnelResearch;
private payroll.Research payrollResearch;

private Map<personnel.Research, payroll.Research> personnelTopayrollMapping = new WeakHashMap<>();
private Map<payroll.Research, personnel.Research> payrollTopersonnelMapping = new WeakHashMap<>();

pointcut personnelResearchConstructor(String name) :
    call(personnel.Research.new(String)) &&
    args(name) &&
    !within(MergeResearch);

pointcut payrollResearchConstructor(String name) :
    call(payroll.Research.new(String)) &&
    args(name) &&
    !within(MergeResearch);

before(String name) : personnelResearchConstructor(name) {
    personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
}
after(String name) : personnelResearchConstructor(name) {
    payrollTopersonnelMapping.put(payrollResearch, new personnel.Research(name));
}

before(String name) : payrollResearchConstructor(name) {
    payrollResearch = (payroll.Research) thisJoinPoint.getTarget();
}
after(String name) : payrollResearchConstructor(name) {
    personnelTopayrollMapping.put(personnelResearch, new payroll.Research(name));
}

after(): execution(void personnel.Research.check(..)) && args() && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.check();
}

after(): execution(void personnel.Research.print(..)) && args() && !within(MergeResearch) {
    personnel.Research personnelResearch = (personnel.Research) thisJoinPoint.getTarget();
    payroll.Research payrollResearch = personnelTopayrollMapping.get(personnelResearch);
    payrollResearch.print();
}

}
