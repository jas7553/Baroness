import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.FieldDeclaration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MergeTool {

    private CompilationUnit classA;
    private CompilationUnit classB;

    private ClassDeclarations classADeclarations;
    private ClassDeclarations classBDeclarations;

    private String classAPackage;
    private String classBPackage;

    private String classAType;
    private String classBType;
    
    private String classAName;
    private String classBName;

    private String className;
    private String aspectName;

    public MergeTool(String file1, String file2) {
        classA = compilationUnitFromFilename(file1);
        classB = compilationUnitFromFilename(file2);

        classADeclarations = new ClassDeclarations(classA);
        classBDeclarations = new ClassDeclarations(classB);
        
        classAPackage = classA.getPackage().getName().toString();
        String aName = classA.getTypes().get(0).getName();
        
        classBPackage = classB.getPackage().getName().toString();
        String bName = classB.getTypes().get(0).getName();
        
        classAType = classAPackage + "." + aName;
        classAName = classAPackage + aName;
        
        classBType = classBPackage + "." + bName;
        classBName = classBPackage + bName;
        
        className = aName;
        aspectName = "Merge" + className;
    }

    public String generateAspect() {
        StringBuilder aspect = new StringBuilder();
        aspect.append(generateMergedImports());
        aspect.append("public privileged aspect " + aspectName + " {\n");
        aspect.append("\n");
        aspect.append(generateInstanceFields());
        aspect.append(generateMergedConstructor());
        aspect.append("\n");
        aspect.append(generateMergedFields());
        aspect.append("}");
        return aspect.toString();
    }
    
    private String generateMergedImports() {
        Set<ImportDeclaration> importDeclarationSet = new HashSet<>();
        
        for (ImportDeclaration importDeclaration : classA.getImports()) {
            importDeclarationSet.add(importDeclaration);
        }
        
        for (ImportDeclaration importDeclaration : classB.getImports()) {
            importDeclarationSet.add(importDeclaration);
        }
        
        String imports = new String();
        
        for (ImportDeclaration importDeclaration : importDeclarationSet) {
            imports += importDeclaration + "\n";
        }
        
        return imports;
    }
    
    private String generateInstanceFields() {
        String instanceFields = new String();
        
        instanceFields += "private " + classAType + " " + classAName + ";\n";
        instanceFields += "private " + classBType + " " + classBName + ";\n";
        
        return instanceFields;
    }

    private String generateMergedConstructor() {
        String constructors = new String();

        return constructors;
    }
    
    private String generateMergedFields() {
        String mergedFields = new String();
        
        List<FieldDeclaration> sharedFields = unionFieldDeclarations(classADeclarations.getFieldDeclarations(), classBDeclarations.getFieldDeclarations());
        
        for (FieldDeclaration fieldDeclaration : sharedFields) {
            String fieldName = fieldDeclaration.getVariables().get(0).toString();
            String replaceWithType = fieldDeclaration.getType().toString();
            String replaceFieldName = classBPackage + "." + className + "." + fieldName;
            String replaceWithFieldName = classAPackage + "." + className + "." + fieldName;
            String aspectFieldName = "this." + classAName + "." + fieldName;
            mergedFields += generateMergedField(replaceWithType, replaceFieldName, replaceWithFieldName, aspectFieldName) + "\n";
        }
        
        return mergedFields;
    }
    
    private static CompilationUnit compilationUnitFromFilename(String filename) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(in);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return cu;
    }

    private static List<FieldDeclaration> unionFieldDeclarations(List<FieldDeclaration> fieldsA, List<FieldDeclaration> fieldsB) {
        List<FieldDeclaration> unionFields = new ArrayList<>();

        for (FieldDeclaration fieldA : fieldsA) {
            for (FieldDeclaration fieldB : fieldsB) {
                String fieldAName = fieldA.getVariables().get(0).toString();
                String fieldBName = fieldB.getVariables().get(0).toString();
                if (fieldA.equals(fieldB) || fieldAName.equals(fieldBName)) {
                    unionFields.add(fieldA);
                }
            }
        }

        return unionFields;
    }
    
    private String generateMergedField(String replaceWithType, String replaceFieldName, String replaceWithFieldName, String aspectFieldName) {
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

    private String generateOverriddenMethod(String returnType, String overrideMethodName, String overrideWithMethodName, List<String> methodVariableTypes, List<String> methodVariableNames, String aspectFieldName) {
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

    public static void main(String[] args) {
        MergeTool.test();

        MergeTool tool = new MergeTool("src/basic/Solitaire.java", "src/rules/Solitaire.java");
        String aspect = tool.generateAspect();
        
        System.out.println(aspect);
    }
    
    private static void test() {
        MergeTool tool = new MergeTool("src/basic/Solitaire.java", "src/rules/Solitaire.java");
        String expected, actual;
        
        expected = "// Replace rules.Solitaire.table with basic.Solitaire.table\n";
        expected += "basic.CardTable around(): get(basic.CardTable rules.Solitaire.table) && !within(MergeSolitaire) {\n";
        expected += "    return this.basicSolitaire.table;\n";
        expected += "}\n";
        expected += "void around(basic.CardTable newval): set(basic.CardTable rules.Solitaire.table) && args(newval) && !within(MergeSolitaire) {\n";
        expected += "    this.basicSolitaire.table = newval;\n";
        expected += "}\n";
        actual = tool.generateMergedField("basic.CardTable", "rules.Solitaire.table", "basic.Solitaire.table", "this.basicSolitaire.table");
        assert expected.equals(actual);

        expected = "// override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt\n";
        expected += "void around(int i): call(void basic.Solitaire.pickCardAt(int)) && args(i) {\n";
        expected += "    this.rulesSolitaire.pickCardAt(i);\n";
        expected += "}\n";
        actual = tool.generateOverriddenMethod("void", "basic.Solitaire.pickCardAt", "rules.Solitaire.pickCardAt", Arrays.asList("int"), Arrays.asList("i"), "this.rulesSolitaire.pickCardAt");
        assert expected.equals(actual);

        expected = "// override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty\n";
        expected += "boolean around(basic.CardTable t, int start, int count): call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {\n";
        expected += "    return this.basicSolitaire.allEmpty(t, start, count);\n";
        expected += "}\n";
        actual = tool.generateOverriddenMethod("boolean", "rules.Solitaire.allEmpty", "basic.Solitaire.allEmpty", Arrays.asList("basic.CardTable", "int", "int"), Arrays.asList("t", "start", "count"), "this.basicSolitaire.allEmpty");
        assert expected.equals(actual);
    }
}
