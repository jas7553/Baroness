package mergetool;

import japa.parser.ast.body.ConstructorDeclaration;
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
    
    public static String generateMergedConstructorAdvice(MergeConfiguration config, ConstructorDeclaration constructorDeclaration) {
        String constructorAdvice = new String();
        
        List<Parameter> parameters = constructorDeclaration.getParameters();
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        String parameterTypes = DeclarationConverter.parameterTypesFromParameterList(parameters);
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        
        constructorAdvice += String.format("before(%s): execution(%s.new(%s)) && args(%s) {\n", parameterNamesAndTypes, config.classAType, parameterTypes, parameterNames);
        constructorAdvice += String.format("    constructingA++;\n");
        constructorAdvice += String.format("}\n");
        constructorAdvice += String.format("\n");
        constructorAdvice += String.format("after(%s) returning: execution(%s.new(%s)) && args(%s) {\n", parameterNamesAndTypes, config.classAType, parameterTypes, parameterNames);
        constructorAdvice += String.format("    %s %s = null;\n", config.classAType, config.classAName);
        constructorAdvice += String.format("    %s %s = null;\n", config.classBType, config.classBName);
        constructorAdvice += String.format("    if ((constructingA2 == 0) && (constructingB == 0)) {\n");
        constructorAdvice += String.format("        constructingA2++;\n");
        constructorAdvice += String.format("        %s = (%s) thisJoinPoint.getTarget();\n", config.classAName, config.classAType);
        constructorAdvice += String.format("        %s = new %s(%s);\n", config.classBName, config.classBType, parameterNames);
        constructorAdvice += String.format("        assert %s != null; assert %s != null;\n", config.classAName, config.classBName);
        constructorAdvice += String.format("        %s.put(%s, %s);\n", config.classAToClassBMappingVariableName, config.classAName, config.classBName);
        constructorAdvice += String.format("        %s.put(%s, %s);\n", config.classBToClassAMappingVariableName, config.classBName, config.classAName);
        constructorAdvice += String.format("        constructingA2--;\n");
        constructorAdvice += String.format("    }\n");
        constructorAdvice += String.format("    constructingA--;\n");
        constructorAdvice += String.format("    \n");
        constructorAdvice += String.format("    if (%s != null) %s.__init__(%s);\n", config.classAName, config.classAName, parameterNames);
        constructorAdvice += String.format("    if (%s != null) %s.__init__(%s);\n", config.classBName, config.classBName, parameterNames);
        constructorAdvice += String.format("}\n");
        constructorAdvice += String.format("\n");
        constructorAdvice += String.format("before(%s): execution(%s.new(%s)) && args(%s) {\n", parameterNamesAndTypes, config.classBType, parameterTypes, parameterNames);
        constructorAdvice += String.format("    constructingB++;\n");
        constructorAdvice += String.format("}\n");
        constructorAdvice += String.format("\n");
        constructorAdvice += String.format("after(%s) returning: execution(%s.new(%s)) && args(%s) {\n", parameterNamesAndTypes, config.classBType, parameterTypes, parameterNames);
        constructorAdvice += String.format("    %s %s = null;\n", config.classBType, config.classBName);
        constructorAdvice += String.format("    %s %s = null;\n", config.classAType, config.classAName);
        constructorAdvice += String.format("    if ((constructingB2 == 0) && (constructingA == 0)) {\n");
        constructorAdvice += String.format("        constructingB2++;\n");
        constructorAdvice += String.format("        %s = (%s) thisJoinPoint.getTarget();\n", config.classBName, config.classBType);
        constructorAdvice += String.format("        %s = new %s(%s);\n", config.classAName, config.classAType, parameterNames);
        constructorAdvice += String.format("        assert %s != null; assert %s != null;\n", config.classBName, config.classAName);
        constructorAdvice += String.format("        %s.put(%s, %s);\n", config.classBToClassAMappingVariableName, config.classBName, config.classAName);
        constructorAdvice += String.format("        %s.put(%s, %s);\n", config.classAToClassBMappingVariableName, config.classAName, config.classBName);
        constructorAdvice += String.format("        constructingB2--;\n");
        constructorAdvice += String.format("    }\n");
        constructorAdvice += String.format("    constructingB--;\n");
        constructorAdvice += String.format("    \n");
        constructorAdvice += String.format("    if (%s != null) %s.__init__(%s);\n", config.classBName, config.classBName, parameterNames);
        constructorAdvice += String.format("    if (%s != null) %s.__init__(%s);\n", config.classAName, config.classAName, parameterNames);
        constructorAdvice += String.format("}\n");
        
        return constructorAdvice;
    }
    
    public static String generateMergedField(MergeConfiguration config, String fieldType, String fieldName) {
        String mergedField = new String();
        
        mergedField += String.format("// Merge %s.%s and %s.%s\n", config.classAType, fieldName, config.classBType, fieldName);
        mergedField += String.format("void around(%s %s): set(%s %s.%s) && args(%s) && !within(%s) {\n", fieldType, fieldName, fieldType, config.classAType, fieldName, fieldName, config.aspectName);
        mergedField += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classAType, config.classAName, config.classAType);
        mergedField += String.format("    %s.%s = %s;\n", config.classAName, fieldName, fieldName);
        mergedField += String.format("    \n");
        mergedField += String.format("    if (constructingA == 0) {\n");
        mergedField += String.format("        assert %s.containsKey(%s);\n", config.classAToClassBMappingVariableName, config.classAName);
        mergedField += String.format("        %s %s = %s.get(%s);\n", config.classBType, config.classBName, config.classAToClassBMappingVariableName, config.classAName);
        mergedField += String.format("        %s.%s = %s;\n", config.classBName, fieldName, fieldName);
        mergedField += String.format("    }\n");
        mergedField += String.format("}\n");
        mergedField += String.format("void around(%s %s): set(%s %s.%s) && args(%s) && !within(%s) {\n", fieldType, fieldName, fieldType, config.classBType, fieldName, fieldName, config.aspectName);
        mergedField += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classBType, config.classBName, config.classBType);
        mergedField += String.format("    %s.%s = %s;\n", config.classBName, fieldName, fieldName);
        mergedField += String.format("    \n");
        mergedField += String.format("    if (constructingB == 0) {\n");
        mergedField += String.format("        assert %s.containsKey(%s);\n", config.classBToClassAMappingVariableName, config.classBName);
        mergedField += String.format("        %s %s = %s.get(%s);\n", config.classAType, config.classAName, config.classBToClassAMappingVariableName, config.classBName);
        mergedField += String.format("        %s.%s = %s;\n", config.classAName, fieldName, fieldName);
        mergedField += String.format("    }\n");
        mergedField += String.format("}\n");
        
        return mergedField;
    }
    
    public static String generateOverriddenMethod(MergeConfiguration config, MethodDeclaration methodDeclaration, boolean aOrB) {
        String overriddenMethod = new String();
        
        String methodName = methodDeclaration.getName();
        String returnType = methodDeclaration.getType().toString();
        List<Parameter> parameters = methodDeclaration.getParameters();
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        String parameterTypes = DeclarationConverter.parameterTypesFromParameterList(parameters);
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);

        aOrB = !aOrB;
        String class1Name = aOrB ? config.classAName : config.classBName;
        String class1Type = aOrB ? config.classAType : config.classBType;
        String class2Name = aOrB ? config.classBName : config.classAName;
        String class2Type = aOrB ? config.classBType : config.classAType;
        String mappingName = aOrB ? config.classAToClassBMappingVariableName : config.classBToClassAMappingVariableName;

        overriddenMethod += String.format("// override %s.%s with %s.%s\n", class1Type, methodName, class2Type, methodName);
        overriddenMethod += String.format("%s around(%s): call(%s %s.%s(%s)) && args(%s) {\n", returnType, parameterNamesAndTypes, returnType, class1Type, methodName, parameterTypes, parameterNames);
        overriddenMethod += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", class1Type, class1Name, class1Type);
        overriddenMethod += String.format("    %s %s = %s.get(%s);\n", class2Type, class2Name, mappingName, class1Name);
        overriddenMethod += String.format("    %s%s.%s(%s);\n", (returnType.equals("void") ? "" : "return "), class2Name, methodName, parameterNames);
        overriddenMethod += String.format("}\n");
        
        return overriddenMethod;
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
    
    public static String generateMergedMethod(MergeConfiguration config, MethodDeclaration methodDeclaration) {
        String mergedMethod = new String();
        
        String methodName = methodDeclaration.getName();
        String returnType = methodDeclaration.getType().toString();
        List<Parameter> parameters = methodDeclaration.getParameters();
        String parameterNames = (parameters != null) ? DeclarationConverter.parameterNamesFromParameterList(parameters) : "";
        String parameterTypes = (parameters != null) ? DeclarationConverter.parameterTypesFromParameterList(parameters) : "";
        String parameterNamesAndTypes = (parameters != null) ? DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters) : "";

        mergedMethod += String.format("// Merge %s.%s and %s.%s\n", config.classAType, methodName, config.classBType, methodName);
        mergedMethod += String.format("after(%s): call(%s %s.%s(%s)) && args(%s) && !within(%s) {\n", parameterNamesAndTypes, returnType, config.classAType, methodName, parameterTypes, parameterNames, config.aspectName);
        mergedMethod += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classAType, config.classAName, config.classAType);
        mergedMethod += String.format("    %s %s = %s.get(%s);\n", config.classBType, config.classBName, config.classAToClassBMappingVariableName, config.classAName);
        mergedMethod += String.format("    %s.%s(%s);\n", config.classBName, methodName, parameterNames);
        mergedMethod += String.format("}\n");
        mergedMethod += String.format("after(%s): call(%s %s.%s(%s)) && args(%s) && !within(%s) {\n", parameterNamesAndTypes, returnType, config.classBType, methodName, parameterTypes, parameterNames, config.aspectName);
        mergedMethod += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classBType, config.classBName, config.classBType);
        mergedMethod += String.format("    %s %s = %s.get(%s);\n", config.classAType, config.classAName, config.classBToClassAMappingVariableName, config.classBName);
        mergedMethod += String.format("    %s.%s(%s);\n", config.classAName, methodName, parameterNames);
        mergedMethod += String.format("}\n");
        
        return mergedMethod;
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
