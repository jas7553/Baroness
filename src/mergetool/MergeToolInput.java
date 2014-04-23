package mergetool;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class MergeToolInput {
    
    @SuppressWarnings("serial")
    public class InputException extends Exception {
        public InputException(Exception e) {
            super(e);
        }
    }
    
    public final CompilationUnit classA;
    public final CompilationUnit classB;
    
    public final boolean mergeAllFieldsByName;
    public final List<String> fieldNamesToMerge;
    public final List<String> methodNamesToMerge;
    public final List<Boolean> methodNamesToMergeOrder;
    public final List<String> methodNamesToOverride;
    public final List<Boolean> methodNamesToOverrideOrder;
    
    @SuppressWarnings("unchecked")
    public MergeToolInput(String filename) throws InputException {
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
        
        JSONObject inputDictionary;
        try {
            inputDictionary = (JSONObject) obj;
        } catch (ClassCastException e) {
            throw new RuntimeException("Input file must be a valid JSON object");
        }
        
        String classAFilename;
        if (inputDictionary.containsKey("ClassA")) {
            try {
                classAFilename = (String) inputDictionary.get("ClassA");
            } catch (ClassCastException e) {
                throw new RuntimeException("\"ClassA\" key must contain string value");
            }
        } else {
            throw new RuntimeException("Input file must contain \"ClassA\" key");
        }
        
        try {
            classA = compilationUnitFromFilename(classAFilename);
        } catch (FileNotFoundException | ParseException e) {
            throw new InputException(e);
        }
        
        String classBFilename;
        if (inputDictionary.containsKey("ClassB")) {
            try {
                classBFilename = (String) inputDictionary.get("ClassB");
            } catch (ClassCastException e) {
                throw new RuntimeException("Input file must contain \"ClassB\" key");
            }
        } else {
            throw new RuntimeException("Input file must contain \"ClassB\" key");
        }
        
        try {
            classB = compilationUnitFromFilename(classBFilename);
        } catch (FileNotFoundException | ParseException e) {
            throw new InputException(e);
        }
        
        // MergeAllFieldsByName
        if (inputDictionary.containsKey("MergeAllFieldsByName")) {
            Boolean bool;
            try {
                bool = (Boolean) inputDictionary.get("MergeAllFieldsByName");
            } catch (ClassCastException e) {
                throw new RuntimeException("\"MergeAllFieldsByName\" must be a boolean");
            }
            mergeAllFieldsByName = bool;
        } else {
            mergeAllFieldsByName = false;
        }
        
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
            methodNamesToMergeOrder = new ArrayList<>();
            
            JSONArray lst;
            try {
                lst = (JSONArray) inputDictionary.get("MethodNamesToMerge");
            } catch (ClassCastException e) {
                throw new RuntimeException("\"MethodNamesToMerge\" must be a valid JSON array");
            }
            List<JSONObject> lsts = listFromIterator(lst.iterator());
            for (JSONObject jsonObject : lsts) {
                for (Object s : jsonObject.keySet()) {
                    String methodName;
                    try {
                        methodName = (String) s;
                    } catch (ClassCastException e) {
                        throw new RuntimeException("\"Method name\" must be a string");
                    }
                    
                    boolean classAOrClassBFirst;
                    try {
                        classAOrClassBFirst = (Boolean) jsonObject.get(methodName);
                    } catch (ClassCastException e) {
                        throw new RuntimeException("\"Method name\" value must be a boolean");
                    }
                    
                    methodNamesToMerge.add(methodName);
                    methodNamesToMergeOrder.add(classAOrClassBFirst);
                }
            }
        } else {
            methodNamesToMerge = Collections.EMPTY_LIST;
            methodNamesToMergeOrder = Collections.EMPTY_LIST;
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
                        overrideClassAWithClassB = (Boolean) jsonObject.get(methodName);
                    } catch (ClassCastException e) {
                        throw new RuntimeException("\"Method name\" value must be a boolean");
                    }
                    
                    methodNamesToOverride.add(methodName);
                    methodNamesToOverrideOrder.add(overrideClassAWithClassB);
                }
            }
        } else {
            methodNamesToOverride = Collections.EMPTY_LIST;
            methodNamesToOverrideOrder = Collections.EMPTY_LIST;
        }
        
        assert methodNamesToMerge.size() == methodNamesToMergeOrder.size();
        assert methodNamesToOverride.size() == methodNamesToOverrideOrder.size();
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
    
    public static void main(String[] args) {
        try {
            new MergeToolInput("src/input.json");
        } catch (InputException e) {
            e.printStackTrace();
        }
    }
}
