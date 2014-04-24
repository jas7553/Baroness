package mergetool;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import mergetool.MergeConfiguration.InputException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class MergeTool {
    
    public static void merge(String filename) throws InputException {
        Scanner inFile1 = null;
        
        try {
            inFile1 = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new InputException(e);
        }
        
        String json = new String();
        while (inFile1.hasNext()) {
            json += inFile1.next();
        }
        
        inFile1.close();
        
        Object obj = JSONValue.parse(json);
        
        List<JSONObject> inputDictionaries = null;
        if (obj instanceof JSONObject) {
            inputDictionaries = Arrays.asList((JSONObject) obj);
        } else if (obj instanceof JSONArray) {
            JSONArray jsonDictionaries = (JSONArray) obj;
            inputDictionaries = new ArrayList<>();
            for (Object object : jsonDictionaries) {
                try {
                    inputDictionaries.add((JSONObject) object);
                } catch (ClassCastException e) {
                    throw new RuntimeException("Input file must be either a valid JSON object or valid JSON array");
                }
            }
        } else {
            throw new RuntimeException("Input file must be either a valid JSON object or valid JSON array");
        }
        
        for (JSONObject inputDictionary : inputDictionaries) {
            MergeConfiguration input = new MergeConfiguration(inputDictionary);
            MergeTool tool = new MergeTool(input);
            tool.writeAspectToFile();
        }
    }
    
    private final MergeConfiguration config;
    
    public MergeTool(MergeConfiguration input) {
        config = input;
    }
    
    public void writeAspectToFile() {
        String aspect = generateAspect();
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(new File("src/" + config.aspectName + ".aj")));
            out.write(aspect);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String generateAspect() {
        String aspect = new String();
        
        aspect += generateImports();
        aspect += generateMergedImports();
        aspect += "public privileged aspect " + config.aspectName + " {\n\n";
        aspect += generateMergedConstructor();
        aspect += generateMergedFields();
        aspect += generateOverriddenMethods();
        aspect += generateMergedMethods();
        aspect += "}\n";
        
        return aspect;
    }
    
    private String generateImports() {
        String imports = new String();
        
        imports += "import java.util.Map;\n";
        imports += "import java.util.WeakHashMap;\n";
        
        return imports;
    }
    
    private String generateMergedImports() {
        Set<ImportDeclaration> importDeclarationSet = new HashSet<>();
        
        if (config.classACompilationUnit.getImports() != null) {
            for (ImportDeclaration importDeclaration : config.classACompilationUnit.getImports()) {
                importDeclarationSet.add(importDeclaration);
            }
        }
        
        if (config.classBCompilationUnit.getImports() != null) {
            for (ImportDeclaration importDeclaration : config.classBCompilationUnit.getImports()) {
                importDeclarationSet.add(importDeclaration);
            }
        }
        
        String imports = new String();
        
        for (ImportDeclaration importDeclaration : importDeclarationSet) {
            imports += importDeclaration + "\n";
        }
        
        imports += "\n";
        
        return imports;
    }
    
    private String generateMergedConstructor() {
        String constructors = new String();
        
        constructors += generateMergedConstructorTemps();
        constructors += "\n";
        constructors += generateMergedConstructorMaps();
        constructors += "\n";
        
        int modifiers = config.classACompilationUnit.getTypes().get(0).getModifiers();
        boolean isAbstractClass = ModifierSet.hasModifier(modifiers, ModifierSet.ABSTRACT);
        if (!isAbstractClass) {
            constructors += generateMergedConstructorPointcuts();
            constructors += "\n";
            constructors += generateMergedConstructorAdvices();
            constructors += "\n";
        }
        
        return constructors;
    }
    
    private String generateMergedConstructorTemps() {
        String constructorTemps = new String();
        
        constructorTemps += "private " + config.classAType + " " + config.classAName + ";\n";
        constructorTemps += "private " + config.classBType + " " + config.classBName + ";\n";
        
        return constructorTemps;
    }
    
    private String generateMergedConstructorMaps() {
        String constructorMaps = new String();
        
        constructorMaps += "private " + config.classAToClassBMappingVariableType + " " + config.classAToClassBMappingVariableName + " = new WeakHashMap<>();\n";
        constructorMaps += "private " + config.classBToClassAMappingVariableType + " " + config.classBToClassAMappingVariableName + " = new WeakHashMap<>();\n";
        
        return constructorMaps;
    }
    
    private String generateMergedConstructorPointcuts() {
        String constructorPointcuts = new String();
        
        List<Parameter> classAConstructorParameters = config.classADeclarations.getConstructorDeclarations().get(0).getParameters();
        List<Parameter> classBConstructorParameters = config.classBDeclarations.getConstructorDeclarations().get(0).getParameters();
        
        if (classAConstructorParameters != null) {
            constructorPointcuts += Generator.generateMergedConstructorPointcut(config.classAName, config.classAType, classAConstructorParameters, config.aspectName);
            constructorPointcuts += "\n";
        }
        
        if (classBConstructorParameters != null) {
            constructorPointcuts += Generator.generateMergedConstructorPointcut(config.classBName, config.classBType, classBConstructorParameters, config.aspectName);
        }
        
        return constructorPointcuts;
    }
    
    private String generateMergedConstructorAdvices() {
        String constructorsAdvice = new String();
        
        for (ConstructorDeclaration constructor : config.classADeclarations.getConstructorDeclarations()) {
            constructorsAdvice += generateMergedConstructorAdvice(constructor);
        }
        
        return constructorsAdvice;
    }
    
    private String generateMergedConstructorAdvice(ConstructorDeclaration constructor) {
        String constructorAdvice = new String();
        
        List<Parameter> parameters = constructor.getParameters();
        constructorAdvice += Generator.generateMergedConstructorAdvice(config, parameters);
        
        return constructorAdvice;
    }
    
    private String generateMergedFields() {
        String mergedFields = new String();
        
        List<FieldDeclaration> fieldDeclarations;
        
        if (config.mergeAllFieldsByName) {
            fieldDeclarations = DeclarationConverter.unionFieldDeclarations(config.classADeclarations.getFieldDeclarations(), config.classBDeclarations.getFieldDeclarations());
        } else {
            fieldDeclarations = new ArrayList<>();
            for (String fieldNameToMerge : config.fieldNamesToMerge) {
                fieldDeclarations.add(config.classADeclarations.getFieldDeclarationForName(fieldNameToMerge));
            }
        }
        
        for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
            String fieldName = fieldDeclaration.getVariables().get(0).toString();
            String replaceWithType = fieldDeclaration.getType().toString();
            
            mergedFields += Generator.generateMergedField(config, replaceWithType, fieldName, config.classBType, config.classBName, config.classAType, config.classAName, config.classBToClassAMappingVariableName) + "\n";
        }
        
        return mergedFields;
    }
    
    private String generateOverriddenMethods() {
        String overriddenMethods = new String();
        
        for (int i = 0; i < config.methodNamesToOverride.size(); i++) {
            String methodName = config.methodNamesToOverride.get(i);
            boolean aOrB = config.methodNamesToOverrideOrder.get(i).booleanValue();
            MethodDeclaration md = config.classADeclarations.getMethodDeclarationForName(methodName);
            String returnType = md.getType().toString();
            String overrideMethodName = (aOrB ? config.classBType : config.classAType) + "." + methodName;
            String overrideWithMethodName = (aOrB ? config.classAType : config.classBType) + "." + methodName;
            List<Parameter> parameters = md.getParameters();
            String aspectMethodName = "this." + (aOrB ? config.classAName : config.classBName) + "." + methodName;
            overriddenMethods += Generator.generateOverriddenMethod(returnType, overrideMethodName, overrideWithMethodName, parameters, aspectMethodName) + "\n";
        }
        
        return overriddenMethods;
    }
    
    private String generateMergedMethods() {
        String mergedMethods = new String();
        
        List<MethodDeclaration> methodsToMerge = new ArrayList<>();
        for (String methodNameToMerge : config.methodNamesToMerge) {
            methodsToMerge.add(config.classADeclarations.getMethodDeclarationForName(methodNameToMerge));
        }
        
        for (int i = 0; i < methodsToMerge.size(); i++) {
            MethodDeclaration methodDeclaration = methodsToMerge.get(i);
            boolean order = config.methodNamesToMergeOrder.get(i);
            String classType = (order ? config.classAType : config.classBType);
            String className = (order ? config.classBName : config.classAName);
            mergedMethods += Generator.generateMergedMethod(methodDeclaration, classType, className, config.aspectName);
            mergedMethods += "\n";
        }
        
        return mergedMethods;
    }
    
    public static void main(String[] args) throws InputException {
        // MergeTool.merge("src/input.json");
        
        MergeTool.merge("src/report_input.json");
        
        System.out.println("done");
    }
    
}
