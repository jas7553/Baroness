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
        /*
        String parameterNamesAndTypes = DeclarationConverter.parameterNamesAndTypesFromParameterList(parameters);
        String parameterNames = DeclarationConverter.parameterNamesFromParameterList(parameters);
        */
        constructorAdvice += String.format("before(): execution(%s.new(..)) {\n", config.classAType);
        constructorAdvice += String.format("    constructingA = true;\n");
        constructorAdvice += String.format("}\n");
        constructorAdvice += String.format("\n");
        constructorAdvice += String.format("after(%s newlyCreatedObject) returning: this(newlyCreatedObject) && execution(%s.new(..)) {\n", config.classAType, config.classAType);
        constructorAdvice += String.format("    if (!constructingA2 && !constructingB) {\n");
        constructorAdvice += String.format("        constructingA2 = true;\n");
        constructorAdvice += String.format("        %s %s = (%s) thisJoinPoint.getTarget();\n", config.classAType, config.classAName, config.classAType);
        constructorAdvice += String.format("        %s %s = new %s(%s);\n", config.classBType, config.classBName, config.classBType, config.classAName);
        constructorAdvice += String.format("        assert %s != null; assert %s != null;\n", config.classAName, config.classBName);
        constructorAdvice += String.format("        %s.put(%s, %s);\n", config.classAToClassBMappingVariableName, config.classAName, config.classBName);
        constructorAdvice += String.format("        constructingA2 = false;\n");
        constructorAdvice += String.format("    }\n");
        constructorAdvice += String.format("    constructingA = false;\n");
        constructorAdvice += String.format("}\n");
        constructorAdvice += String.format("\n");
        constructorAdvice += String.format("before(): execution(%s.new(..)) {\n", config.classBType);
        constructorAdvice += String.format("    constructingB = true;\n");
        constructorAdvice += String.format("}\n");
        constructorAdvice += String.format("\n");
        constructorAdvice += String.format("after(%s newlyCreatedObject) returning: this(newlyCreatedObject) && execution(%s.new(..)) {\n", config.classBType, config.classBType);
        constructorAdvice += String.format("    if (!constructingB2 && !constructingA) {\n");
        constructorAdvice += String.format("        constructingB2 = true;\n");
        constructorAdvice += String.format("        %s %s = (%s) thisJoinPoint.getTarget();\n", config.classBType, config.classBName, config.classBType);
        constructorAdvice += String.format("        %s %s = new %s(%s);\n", config.classAType, config.classAName, config.classAType, config.classBName);
        constructorAdvice += String.format("        assert %s != null; assert %s != null;\n", config.classBName, config.classAName);
        constructorAdvice += String.format("        %s.put(%s, %s);\n", config.classBToClassAMappingVariableName, config.classBName, config.classAName);
        constructorAdvice += String.format("        constructingB2 = false;\n");
        constructorAdvice += String.format("    }\n");
        constructorAdvice += String.format("    constructingB = false;\n");
        constructorAdvice += String.format("}\n");
        /*
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
        */
        return constructorAdvice;
    }
    
    public static String generateMergedField(MergeConfiguration config, String fieldType, String fieldName) {
        String mergedField = new String();
        
        mergedField += String.format("// Merge %s.%s and %s.%s\n", config.classAType, fieldName, config.classBType, fieldName);
        mergedField += String.format("void around(%s %s): set(%s %s.%s) && args(%s) && !within(%s) {\n", fieldType, fieldName, fieldType, config.classAType, fieldName, fieldName, config.aspectName);
        mergedField += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classAType, config.classAName, config.classAType);
        mergedField += String.format("    %s.%s = %s;\n", config.classAName, fieldName, fieldName);
        mergedField += String.format("    \n");
        mergedField += String.format("    if (!constructingA) {\n");
        mergedField += String.format("        assert %s.containsKey(%s);\n", config.classAToClassBMappingVariableName, config.classAName);
        mergedField += String.format("        %s %s = %s.get(%s);\n", config.classBType, config.classBName, config.classAToClassBMappingVariableName, config.classAName);
        mergedField += String.format("        %s.%s = %s;\n", config.classBName, fieldName, fieldName);
        mergedField += String.format("    }\n");
        mergedField += String.format("}\n");
        mergedField += String.format("\n");
        mergedField += String.format("void around(%s %s): set(%s %s.%s) && args(%s) && !within(%s) {\n", fieldType, fieldName, fieldType, config.classBType, fieldName, fieldName, config.aspectName);
        mergedField += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classBType, config.classBName, config.classBType);
        mergedField += String.format("    %s.%s = %s;\n", config.classBName, fieldName, fieldName);
        mergedField += String.format("    \n");
        mergedField += String.format("    if (!constructingB) {\n");
        mergedField += String.format("        assert %s.containsKey(%s);\n", config.classBToClassAMappingVariableName, config.classBName);
        mergedField += String.format("        %s %s = %s.get(%s);\n", config.classAType, config.classAName, config.classBToClassAMappingVariableName, config.classBName);
        mergedField += String.format("        %s.%s = %s;\n", config.classAName, fieldName, fieldName);
        mergedField += String.format("    }\n");
        mergedField += String.format("}\n");
        
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
    
    public static String generateMergedMethod(MergeConfiguration config, MethodDeclaration methodDeclaration) {
        String mergedMethod = new String();
        
        String methodName = methodDeclaration.getName();

        mergedMethod += String.format("after(): call(void %s.%s(..)) && !within(%s) {\n", config.classAType, methodName, config.aspectName);
        mergedMethod += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classAType, config.classAName, config.classAType);
        mergedMethod += String.format("    %s %s = %s.get(%s);\n", config.classBType, config.classBName, config.classAToClassBMappingVariableName, config.classAName);
        mergedMethod += String.format("    %s.%s();\n", config.classBName, methodName);
        mergedMethod += String.format("}\n");
        mergedMethod += String.format("\n");
        mergedMethod += String.format("after(): call(void %s.%s(..)) && !within(%s) {\n", config.classBType, methodName, config.aspectName);
        mergedMethod += String.format("    %s %s = (%s) thisJoinPoint.getTarget();\n", config.classBType, config.classBName, config.classBType);
        mergedMethod += String.format("    %s %s = %s.get(%s);\n", config.classAType, config.classAName, config.classBToClassAMappingVariableName, config.classBName);
        mergedMethod += String.format("    %s.%s();\n", config.classAName, methodName);
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
