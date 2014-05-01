package mergetool;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;

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
        
        if (!DeclarationConverter.isAbstractClass(config.classACompilationUnit)) {
            constructors += generateMergedConstructorAdvices();
            constructors += "\n";
        }
        
        return constructors;
    }
    
    private String generateMergedConstructorTemps() {
        String constructorTemps = new String();

        constructorTemps += "private int constructingA = 0;\n";
        constructorTemps += "private int constructingA2 = 0;\n";
        constructorTemps += "\n";
        constructorTemps += "private int constructingB = 0;\n";
        constructorTemps += "private int constructingB2 = 0;\n";
        
        return constructorTemps;
    }
    
    private String generateMergedConstructorMaps() {
        String constructorMaps = new String();
        
        constructorMaps += "private final " + config.classAToClassBMappingVariableType + " " + config.classAToClassBMappingVariableName + " = new WeakHashMap<>();\n";
        constructorMaps += "private final " + config.classBToClassAMappingVariableType + " " + config.classBToClassAMappingVariableName + " = new WeakHashMap<>();\n";
        
        return constructorMaps;
    }
    
    private String generateMergedConstructorAdvices() {
        String constructorsAdvice = new String();
        
        for (ConstructorDeclaration constructor : config.classADeclarations.getConstructorDeclarations()) {
            constructorsAdvice += Generator.generateMergedConstructorAdvice(config, constructor);
        }
        
        return constructorsAdvice;
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
            
            mergedFields += Generator.generateMergedField(config, replaceWithType, fieldName) + "\n";
        }
        
        return mergedFields;
    }
    
    private String generateOverriddenMethods() {
        String overriddenMethods = new String();
        
        for (int i = 0; i < config.methodNamesToOverride.size(); i++) {
            String methodName = config.methodNamesToOverride.get(i);
            boolean aOrB = config.methodNamesToOverrideOrder.get(i).booleanValue();
            MethodDeclaration methodDeclaration = config.classADeclarations.getMethodDeclarationForName(methodName);
            overriddenMethods += Generator.generateOverriddenMethod(config, methodDeclaration, aOrB) + "\n";
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
            mergedMethods += Generator.generateMergedMethod(config, methodDeclaration);
            mergedMethods += "\n";
        }
        
        return mergedMethods;
    }
    public static int i;
    public static void main(String[] args) throws InputException {
        MergeTool.merge("src/input.json");
        
        MergeTool.merge("src/report_input.json");
        
        System.out.println("done");
    }
    
}
