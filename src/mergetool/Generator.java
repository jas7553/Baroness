package mergetool;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.util.List;

public class Generator {
    
    public static String generateMergedConstructorPointcut(String className, String classType, List<Parameter> parameters, String aspectName) {
        String constructorPointcuts = new String();
        
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        String parameterTypes = DeclarationConverter.parameterTypesFromParameterList(parameters);
        
        constructorPointcuts += "pointcut " + className + "Constructor(" + parameterNamesAndTypes + ") :\n";
        constructorPointcuts += "    call(" + classType + ".new(" + parameterTypes + ")) &&\n";
        constructorPointcuts += "    args(" + parameterNames + ") &&\n";
        constructorPointcuts += "    !within(" + aspectName + ");\n";
        
        return constructorPointcuts;
    }
    
    public static String generateMergedConstructorAdvice(MergeConfiguration config, List<Parameter> parameters) {
        String constructorAdvice = new String();
        
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        
        constructorAdvice += String.format("before(%s) : %sConstructor(%s) {\n", parameterNamesAndTypes, config.classAName, parameterNames);
        constructorAdvice += String.format("    %s = (%s) thisJoinPoint.getTarget();\n", config.classAName, config.classAType);
        constructorAdvice += String.format("}\n");
        
        constructorAdvice += String.format("after(%s) : %sConstructor(%s) {\n", parameterNamesAndTypes, config.classAName, parameterNames);
        constructorAdvice += String.format("    %s.put(%s, new %s(%s));\n", config.classBToClassAMappingVariableName, config.classBName, config.classAType, parameterNames);
        constructorAdvice += String.format("}\n\n");
        
        constructorAdvice += String.format("before(%s) : %sConstructor(%s) {\n", parameterNamesAndTypes, config.classBName, parameterNames);
        constructorAdvice += String.format("    %s = (%s) thisJoinPoint.getTarget();\n", config.classBName, config.classBType);
        constructorAdvice += String.format("}\n");
        
        constructorAdvice += String.format("after(%s) : %sConstructor(%s) {\n", parameterNamesAndTypes, config.classBName, parameterNames);
        constructorAdvice += String.format("    %s.put(%s, new %s(%s));\n", config.classAToClassBMappingVariableName, config.classAName, config.classBType, parameterNames);
        constructorAdvice += String.format("}\n");
        
        return constructorAdvice;
    }
    
    public static String generateMergedField(MergeConfiguration config, String replaceWithType, String fieldName, String class1Type, String class1Name, String class2Type, String class2Name, String mappingVariable) {
        String mergedField = new String();
        
        mergedField = "// Replace " + class1Type + "." + fieldName + " with " + class2Type + "." + fieldName + "\n";
        mergedField += replaceWithType + " around(): get(" + replaceWithType + " " + class1Type + "." + fieldName + ") && !within(" + config.aspectName + ") {\n";
        mergedField += "    " + class1Type + " " + class1Name + " = (" + class1Type + ") thisJoinPoint.getTarget();\n";
        mergedField += "    " + class2Type + " " + class2Name + " = " + mappingVariable + ".get(" + class1Name + ");\n";
        mergedField += "    return " + class2Name + "." + fieldName + ";\n";
        mergedField += "}\n";
        mergedField += "void around(" + replaceWithType + " newval): set(" + replaceWithType + " " + class1Type + "." + fieldName + ") && args(newval) && !within(" + config.aspectName + ") {\n";
        mergedField += "    " + class1Type + " " + class1Name + " = (" + class1Type + ") thisJoinPoint.getTarget();\n";
        mergedField += "    " + class2Type + " " + class2Name + " = " + mappingVariable + ".get(" + class1Name + ");\n";
        mergedField += "    " + class2Name + "." + fieldName + " = newval;\n";
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
        
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        
        mergedMethod += "after(" + parameterNamesAndTypes + "):";
        mergedMethod += " execution(" + methodDeclaration.getType().toString() + " " + classAType + "." + methodName + "(" + ".." + "))";
        mergedMethod += " && args(" + parameterNames + ")";
        mergedMethod += " && !within(" + aspectName + ") {\n";
        mergedMethod += "    this." + classBName + "." + methodName + "(" + parameterNames + ");\n";
        mergedMethod += "}\n";
        
        return mergedMethod;
    }
}
