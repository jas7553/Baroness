import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Generator {
    
    public static String generateMergedConstructorPointcut(String className, String classType, List<Parameter> parameters, String aspectName) {
        String constructorPointcuts = new String();
        
        constructorPointcuts += "pointcut " + className + "Constructor(";
        Iterator<Parameter> parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            constructorPointcuts += parameter.getType().toString() + " " + parameter.getId().getName();
            if (parameterIterator.hasNext()) {
                constructorPointcuts += ", ";
            }
        }
        constructorPointcuts += ") :\n";
        constructorPointcuts += "    call(" + classType + ".new(";
        parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            constructorPointcuts += parameter.getType().toString();
            if (parameterIterator.hasNext()) {
                constructorPointcuts += ", ";
            }
        }
        constructorPointcuts += ")) &&\n";
        constructorPointcuts += "    args(";
        parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            constructorPointcuts += parameter.getId().getName();
            if (parameterIterator.hasNext()) {
                constructorPointcuts += ", ";
            }
        }
        constructorPointcuts += ") &&\n";
        constructorPointcuts += "    !within(" + aspectName + ");\n";
        
        return constructorPointcuts;
    }
    
    public static String generateMergedConstructorJoinPoint(String classType, String className, List<Parameter> parameters) {
        String constructorJoinPoints = new String();
        
        constructorJoinPoints += classType + " around() : " + className + "Constructor(";
        Iterator<Parameter> parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            constructorJoinPoints += parameter.getType().toString();
            if (parameterIterator.hasNext()) {
                constructorJoinPoints += ", ";
            }
        }
        constructorJoinPoints += ") {\n";
        constructorJoinPoints += "    proceed();\n";
        constructorJoinPoints += "    return this." + className + ";\n";
        constructorJoinPoints += "}\n";
        
        return constructorJoinPoints;
    }
    
    public static String generateMergedConstructorAdvice(String className, String classAName, String classAPackage, String classBName, String classBPackage ) {
        String constructorAdvice = new String();
        
        constructorAdvice += "before(" + "int" + " " + "numberOfPiles" + ") : " + classAName + "Constructor(" + "numberOfPiles" + ") {\n";
        constructorAdvice += "    if (this." + classAName + " == null) {\n";
        constructorAdvice += "        this." + classAName + " = new " + classAPackage + "." + className + "(" + "numberOfPiles" + ");\n";
        constructorAdvice += "        this." + classBName + " = new " + classBPackage + "." + className + "(" + "numberOfPiles" + ");\n";
        constructorAdvice += "    }\n";
        constructorAdvice += "}\n";
        
        return constructorAdvice;
    }
    
    public static String generateMergedField(String aspectName, String replaceWithType, String replaceFieldName, String replaceWithFieldName, String aspectFieldName) {
        String mergedField = new String();
        mergedField = "// Replace " + replaceFieldName + " with " + replaceWithFieldName + "\n";
        mergedField += replaceWithType + " around(): get(" + replaceWithType + " " + replaceFieldName + ") && !within(" + aspectName + ") {\n";
        mergedField += "    return " + aspectFieldName + ";\n";
        mergedField += "}\n";
        mergedField += "void around(" + replaceWithType + " newval): set(" + replaceWithType + " " + replaceFieldName + ") && args(newval) && !within(" + aspectName + ") {\n";
        mergedField += "    " + aspectFieldName + " = newval;\n";
        mergedField += "}\n";
        return mergedField;
    }

    public static String generateOverriddenMethod(String returnType, String overrideMethodName, String overrideWithMethodName, List<String> methodVariableTypes, List<String> methodVariableNames, String aspectFieldName) {
        assert returnType != null; assert overrideMethodName != null; assert overrideWithMethodName != null;
        assert methodVariableTypes != null; assert methodVariableNames != null;
        assert methodVariableTypes.size() == methodVariableNames.size();
        
        String overriddenMethod = new String();
        
        overriddenMethod = "// override " + overrideMethodName + " with " + overrideWithMethodName + "\n";
        overriddenMethod += returnType + " around(";
        Iterator<String> methodVariableTypesIterator = methodVariableTypes.iterator();
        Iterator<String> methodVariableNamesIterator = methodVariableNames.iterator();
        while (methodVariableTypesIterator.hasNext()) {
            overriddenMethod += methodVariableTypesIterator.next() + " " + methodVariableNamesIterator.next();
            if (methodVariableTypesIterator.hasNext()) {
                overriddenMethod += ", ";
            }
        }
        overriddenMethod += "): call(" + returnType + " " + overrideMethodName + "(";
        methodVariableTypesIterator = methodVariableTypes.iterator();
        while (methodVariableTypesIterator.hasNext()) {
            overriddenMethod += methodVariableTypesIterator.next();
            if (methodVariableTypesIterator.hasNext()) {
                overriddenMethod += ", ";
            }
        }
        overriddenMethod += ")) && args(";
        methodVariableNamesIterator = methodVariableNames.iterator();
        while (methodVariableNamesIterator.hasNext()) {
            overriddenMethod += methodVariableNamesIterator.next();
            if (methodVariableNamesIterator.hasNext()) {
                overriddenMethod += ", ";
            }
        }
        overriddenMethod += ") {\n";
        overriddenMethod += "    " + (returnType.equals("void") ? "" : "return ") + aspectFieldName + "(";
        methodVariableNamesIterator = methodVariableNames.iterator();
        while (methodVariableNamesIterator.hasNext()) {
            overriddenMethod += methodVariableNamesIterator.next();
            if (methodVariableNamesIterator.hasNext()) {
                overriddenMethod += ", ";
            }
        }
        overriddenMethod += ");\n";
        overriddenMethod += "}\n";
        return overriddenMethod;
    }
    
    public static String generateMergedMethod(MethodDeclaration methodDeclaration, String classAType, String classBName, String aspectName) {
        String mergedMethod = new String();
        
        String methodName = methodDeclaration.getName();
        List<Parameter> parameters = methodDeclaration.getParameters();
        String returnType = "void";
        mergedMethod += "after(";
        Iterator<Parameter> parametersIterator = parameters.iterator();
        while (parametersIterator.hasNext()) {
            Parameter parameter = parametersIterator.next();
            mergedMethod += parameter.getType().toString() + " " + parameter.getId().getName();
            if (parametersIterator.hasNext()) {
                mergedMethod += ", ";
            }
        }
        mergedMethod += "): execution(" + returnType + " " + classAType + "." + methodName + "(" + ".." + ")) && args(";
        parametersIterator = parameters.iterator();
        while (parametersIterator.hasNext()) {
            mergedMethod += parametersIterator.next().getId().getName();
            if (parametersIterator.hasNext()) {
                mergedMethod += ", ";
            }
        }
        mergedMethod += ") && !within(" + aspectName + ") {\n";
        mergedMethod += "    " + (returnType.equals("void") ? "" : "return ") + "this." + classBName + "." + methodName + "(";
        mergedMethod += "in";
        mergedMethod += ");\n";
        mergedMethod += "}\n";
        
        return mergedMethod;
    }
    
    public static void main(String[] args) {
        String expected, actual;
        
        expected = "// Replace rules.Solitaire.table with basic.Solitaire.table\n";
        expected += "basic.CardTable around(): get(basic.CardTable rules.Solitaire.table) && !within(MergeSolitaire) {\n";
        expected += "    return this.basicSolitaire.table;\n";
        expected += "}\n";
        expected += "void around(basic.CardTable newval): set(basic.CardTable rules.Solitaire.table) && args(newval) && !within(MergeSolitaire) {\n";
        expected += "    this.basicSolitaire.table = newval;\n";
        expected += "}\n";
        actual = generateMergedField("MergeSolitaire", "basic.CardTable", "rules.Solitaire.table", "basic.Solitaire.table", "this.basicSolitaire.table");
        assert expected.equals(actual);
        
        expected = "// override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt\n";
        expected += "void around(int i): call(void basic.Solitaire.pickCardAt(int)) && args(i) {\n";
        expected += "    this.rulesSolitaire.pickCardAt(i);\n";
        expected += "}\n";
        actual = generateOverriddenMethod("void", "basic.Solitaire.pickCardAt", "rules.Solitaire.pickCardAt", Arrays.asList("int"), Arrays.asList("i"), "this.rulesSolitaire.pickCardAt");
        assert expected.equals(actual);
        
        expected = "// override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty\n";
        expected += "boolean around(basic.CardTable t, int start, int count): call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {\n";
        expected += "    return this.basicSolitaire.allEmpty(t, start, count);\n";
        expected += "}\n";
        actual = generateOverriddenMethod("boolean", "rules.Solitaire.allEmpty", "basic.Solitaire.allEmpty", Arrays.asList("basic.CardTable", "int", "int"), Arrays.asList("t", "start", "count"), "this.basicSolitaire.allEmpty");
        assert expected.equals(actual);
    }
    
}
