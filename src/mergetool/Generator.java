package mergetool;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.util.List;

public class Generator {
    
    public static String generateMergedConstructorPointcut(String className, String classType, List<Parameter> parameters, String aspectName) {
        String constructorPointcuts = new String();
        
        constructorPointcuts += "pointcut " + className + "Constructor(";
        constructorPointcuts += DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        constructorPointcuts += ") :\n";
        constructorPointcuts += "    call(" + classType + ".new(";
        constructorPointcuts += DeclarationConverter.parameterTypesFromParameterList(parameters);
        constructorPointcuts += ")) &&\n";
        constructorPointcuts += "    args(";
        constructorPointcuts += DeclarationConverter.parameterNamesFromParameterList(parameters);
        constructorPointcuts += ") &&\n";
        constructorPointcuts += "    !within(" + aspectName + ");\n";
        
        return constructorPointcuts;
    }
    
    public static String generateMergedConstructorJoinPoint(String classType, String className, List<Parameter> parameters) {
        String constructorJoinPoints = new String();
        
        constructorJoinPoints += classType + " around() : " + className + "Constructor(";
        constructorJoinPoints += DeclarationConverter.parameterTypesFromParameterList(parameters);
        constructorJoinPoints += ") {\n";
        constructorJoinPoints += "    proceed();\n";
        constructorJoinPoints += "    return this." + className + ";\n";
        constructorJoinPoints += "}\n";
        
        return constructorJoinPoints;
    }
    
    public static String generateMergedConstructorAdvice(String className, String classAName, String classAPackage, String classBName, String classBPackage, List<Parameter> parameters) {
        String constructorAdvice = new String();
        
        constructorAdvice += "before(";
        constructorAdvice += DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        constructorAdvice += ") : " + classAName + "Constructor(";
        constructorAdvice += DeclarationConverter.parameterNamesFromParameterList(parameters);
        constructorAdvice += ") {\n";
        constructorAdvice += "    if (this." + classAName + " == null) {\n";
        constructorAdvice += "        this." + classAName + " = new " + classAPackage + "." + className + "(";
        constructorAdvice += DeclarationConverter.parameterNamesFromParameterList(parameters);
        constructorAdvice += ");\n";
        constructorAdvice += "        this." + classBName + " = new " + classBPackage + "." + className + "(";
        constructorAdvice += DeclarationConverter.parameterNamesFromParameterList(parameters);
        constructorAdvice += ");\n";
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
    
    public static String generateOverriddenMethod(String returnType, String overrideMethodName, String overrideWithMethodName, List<Parameter> parameters, String aspectFieldName) {
        assert returnType != null;
        assert overrideMethodName != null;
        assert overrideWithMethodName != null;
        
        String overriddenMethod = new String();
        
        overriddenMethod = "// override " + overrideMethodName + " with " + overrideWithMethodName + "\n";
        overriddenMethod += returnType + " around(";
        overriddenMethod += DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        overriddenMethod += "): call(" + returnType + " " + overrideMethodName + "(";
        overriddenMethod += DeclarationConverter.parameterTypesFromParameterList(parameters);
        overriddenMethod += ")) && args(";
        overriddenMethod += DeclarationConverter.parameterNamesFromParameterList(parameters);
        overriddenMethod += ") {\n";
        overriddenMethod += "    " + (returnType.equals("void") ? "" : "return ") + aspectFieldName + "(";
        overriddenMethod += DeclarationConverter.parameterNamesFromParameterList(parameters);
        overriddenMethod += ");\n";
        overriddenMethod += "}\n";
        return overriddenMethod;
    }
    
    public static String generateMergedMethod(MethodDeclaration methodDeclaration, String classAType, String classBName, String aspectName) {
        String mergedMethod = new String();
        
        String methodName = methodDeclaration.getName();
        List<Parameter> parameters = methodDeclaration.getParameters();
        String returnType = "void";
        mergedMethod += "after(" + DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters) + "):";
        mergedMethod += " execution(" + returnType + " " + classAType + "." + methodName + "(" + ".." + "))";
        mergedMethod += " && args(" + DeclarationConverter.parameterNamesFromParameterList(parameters) + ")";
        mergedMethod += " && !within(" + aspectName + ") {\n";
        mergedMethod += "    " + (returnType.equals("void") ? "" : "return ") + "this." + classBName + "." + methodName + "(";
        mergedMethod += DeclarationConverter.parameterNamesFromParameterList(parameters);
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
    }
    
}
