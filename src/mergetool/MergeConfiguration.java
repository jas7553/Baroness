package mergetool;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MergeConfiguration {
    
    @SuppressWarnings("serial")
    public static class InputException extends Exception {
        public InputException(Exception e) {
            super(e);
        }
    }
    
    public final CompilationUnit classACompilationUnit;
    public final CompilationUnit classBCompilationUnit;
    
    public final boolean mergeAllFieldsByName;
    public final List<String> fieldNamesToMerge;
    public final List<String> methodNamesToMerge;
    public final List<String> methodNamesToOverride;
    public final List<Boolean> methodNamesToOverrideOrder;
    
    public final ClassDeclarations classADeclarations;
    public final ClassDeclarations classBDeclarations;
    
    public final String classAPackage;
    public final String classBPackage;
    
    public final String aName;
    public final String bName;
    
    public final String classAType;
    public final String classBType;
    
    public final String classAName;
    public final String classBName;
    
    public final String className;

    public final String classAToClassBMappingVariableType;
    public final String classAToClassBMappingVariableName;
    
    public final String classBToClassAMappingVariableType;
    public final String classBToClassAMappingVariableName;
    
    public final String aspectName;
    
    @SuppressWarnings("unchecked")
    public MergeConfiguration(JSONObject inputDictionary) throws InputException {
        classACompilationUnit = extractCompilationUnit(inputDictionary, "ClassA");
        classBCompilationUnit = extractCompilationUnit(inputDictionary, "ClassB");
        
        mergeAllFieldsByName = extractBooleanForKeyWithDefaultValue(inputDictionary, "MergeAllFieldsByName", false);
        
        // FieldNamesToMerge
        if (inputDictionary.containsKey("FieldNamesToMerge")) {
            JSONArray lst;
            try {
                lst = (JSONArray) inputDictionary.get("FieldNamesToMerge");
            } catch (ClassCastException e) {
                throw new RuntimeException("\"FieldNamesToMerge\" must be a valid JSON array");
            }
            fieldNamesToMerge = listFromIterator(lst.iterator());
        } else {
            fieldNamesToMerge = Collections.EMPTY_LIST;
        }
        
        // MethodNamesToMerge
        if (inputDictionary.containsKey("MethodNamesToMerge")) {
            methodNamesToMerge = new ArrayList<>();
            
            JSONArray lst;
            try {
                lst = (JSONArray) inputDictionary.get("MethodNamesToMerge");
            } catch (ClassCastException e) {
                throw new RuntimeException("\"MethodNamesToMerge\" must be a valid JSON array");
            }
            List<JSONObject> lsts = listFromIterator(lst.iterator());
            for (Object object : lsts) {
                String methodName;
                try {
                    methodName = (String) object;
                } catch (ClassCastException e) {
                    throw new RuntimeException("\"Method name\" must be a string");
                }
                
                methodNamesToMerge.add(methodName);
            }
        } else {
            methodNamesToMerge = Collections.EMPTY_LIST;
        }
        
        // MethodNamesToOverride
        if (inputDictionary.containsKey("MethodNamesToOverride")) {
            methodNamesToOverride = new ArrayList<>();
            methodNamesToOverrideOrder = new ArrayList<>();
            JSONArray lst;
            try {
                lst = (JSONArray) inputDictionary.get("MethodNamesToOverride");
            } catch (ClassCastException e) {
                throw new RuntimeException("\"MethodNamesToOverride\" must be a valid JSON array");
            }
            List<JSONObject> methodBooleanPairs = listFromIterator(lst.iterator());
            for (JSONObject jsonObject : methodBooleanPairs) {
                for (Object s : jsonObject.keySet()) {
                    String methodName;
                    try {
                        methodName = (String) s;
                    } catch (ClassCastException e) {
                        throw new RuntimeException("\"Method name\" must be a string");
                    }
                    
                    boolean overrideClassAWithClassB;
                    try {
                        String classAOrClassB = (String) jsonObject.get(methodName);
                        if (classAOrClassB.equals("ClassA")) {
                            overrideClassAWithClassB = false;
                        } else if (classAOrClassB.equals("ClassB")) {
                            overrideClassAWithClassB = true;
                        } else {
                            throw new RuntimeException("\"Method name\" value must be either \"ClassA\" or \"ClassB\"");
                        }
                    } catch (ClassCastException e) {
                        throw new RuntimeException("\"Method name\" value must be a String");
                    }
                    
                    methodNamesToOverride.add(methodName);
                    methodNamesToOverrideOrder.add(overrideClassAWithClassB);
                }
            }
        } else {
            methodNamesToOverride = Collections.EMPTY_LIST;
            methodNamesToOverrideOrder = Collections.EMPTY_LIST;
        }
        
        assert methodNamesToOverride.size() == methodNamesToOverrideOrder.size();
        
        classADeclarations = new ClassDeclarations(classACompilationUnit);
        classBDeclarations = new ClassDeclarations(classBCompilationUnit);
        
        classAPackage = classACompilationUnit.getPackage().getName().toString();
        classBPackage = classBCompilationUnit.getPackage().getName().toString();
        
        aName = classACompilationUnit.getTypes().get(0).getName();
        bName = classBCompilationUnit.getTypes().get(0).getName();
        
        classAType = classAPackage + "." + aName;
        classBType = classBPackage + "." + bName;
        
        classAName = classAPackage + aName;
        classBName = classBPackage + bName;
        
        className = aName;
        
        classAToClassBMappingVariableType = "Map<" + classAType + ", " + classBType + ">";
        classAToClassBMappingVariableName = classAPackage + "To" + classBPackage + "Mapping";
        
        classBToClassAMappingVariableType = "Map<" + classBType + ", " + classAType + ">";
        classBToClassAMappingVariableName = classBPackage + "To" + classAPackage + "Mapping";
        
        aspectName = "Merge" + className;
    }
    
    private CompilationUnit extractCompilationUnit(JSONObject inputDictionary, String className) throws InputException {
        String classAFilename;
        if (inputDictionary.containsKey(className)) {
            try {
                classAFilename = (String) inputDictionary.get(className);
            } catch (ClassCastException e) {
                throw new RuntimeException("\"" + className + "\" key must contain string value");
            }
        } else {
            throw new RuntimeException("Input file must contain \"" + className + "\" key");
        }
        
        CompilationUnit compilationUnit;
        try {
            compilationUnit = compilationUnitFromFilename(classAFilename);
        } catch (FileNotFoundException | ParseException e) {
            throw new InputException(e);
        }
        
        return compilationUnit;
    }
    
    private static Boolean extractBooleanForKeyWithDefaultValue(JSONObject inputDictionary, String key, Boolean defaultValue) {
        if (inputDictionary.containsKey(key)) {
            Boolean bool;
            try {
                bool = (Boolean) inputDictionary.get(key);
            } catch (ClassCastException e) {
                throw new RuntimeException("\"" + key + "\" must be a boolean");
            }
            return bool;
        } else {
            return defaultValue;
        }
    }
    
    private static <T> List<T> listFromIterator(Iterator<T> iter) {
        List<T> lst = new ArrayList<T>();
        while (iter.hasNext())
            lst.add(iter.next());
        return lst;
    }
    
    private static CompilationUnit compilationUnitFromFilename(String filename) throws FileNotFoundException, ParseException {
        FileInputStream in = new FileInputStream(filename);
        CompilationUnit compilationUnit = JavaParser.parse(in);
        return compilationUnit;
    }
}
