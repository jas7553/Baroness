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
        String overriddenMethod = new String();
        
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        String parameterTypes = DeclarationConverter.parameterTypesFromParameterList(parameters);
        
        overriddenMethod = "// override " + overrideMethodName + " with " + overrideWithMethodName + "\n";
        overriddenMethod += returnType + " around(" + parameterNamesAndTypes + "):";
        overriddenMethod += " call(" + returnType + " " + overrideMethodName + "(" + parameterTypes + "))";
        overriddenMethod += " && args(" + parameterNames + ") {\n";
        overriddenMethod += "    " + (returnType.equals("void") ? "" : "return ") + aspectFieldName + "(" + parameterNames + ");\n";
        overriddenMethod += "}\n";
        return overriddenMethod;
    }
    
    public static String generateMergedMethod(MergeConfiguration config, MethodDeclaration methodDeclaration, boolean order) {
        String mergedMethod = new String();
        
        String methodName = methodDeclaration.getName();
        String class1Type = order ? config.classAType : config.classBType;
        String class1Name = order ? config.classAName : config.classBName;
        String class2Type = order ? config.classBType : config.classAType;
        String class2Name = order ? config.classBName : config.classAName;
        String mappingVariableName = order ? config.classAToClassBMappingVariableName : config.classBToClassAMappingVariableName;
        
        List<Parameter> parameters = methodDeclaration.getParameters();
        
        String parameterNamesAndTypes = (parameters != null) ? DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters) : "";
        String parameterNames = (parameters != null) ? DeclarationConverter.parameterNamesFromParameterList(parameters) : "";
        
        mergedMethod += "after(" + parameterNamesAndTypes + "):";
        mergedMethod += " execution(" + methodDeclaration.getType().toString() + " " + config.classAType + "." + methodName + "(" + ".." + "))";
        mergedMethod += " && args(" + parameterNames + ")";
        mergedMethod += " && !within(" + config.aspectName + ") {\n";
        mergedMethod += "    " + class1Type + " " + class1Name + " = (" + class1Type + ") thisJoinPoint.getTarget();\n";
        mergedMethod += "    " + class2Type + " " + class2Name + " = " + mappingVariableName + ".get(" + class1Name + ");\n";
        mergedMethod += "    " + class2Name + "." + methodName + "();\n";
        mergedMethod += "}\n";
        
        return mergedMethod;
    }
}
