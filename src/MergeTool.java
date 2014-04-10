import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MergeTool {

    private String aspectName;

    private CompilationUnit file1;
    private CompilationUnit file2;

    private ClassDeclarations classADeclarations = new ClassDeclarations();
    private ClassDeclarations classBDeclarations = new ClassDeclarations();

    public MergeTool() {
        this.aspectName = "MergeSolitaire";
    }
    
    public MergeTool(String file1, String file2) {
        this.file1 = constructCompilationUnit(file1);
        this.file2 = constructCompilationUnit(file2);

        extractClassDeclarations(this.file1, classADeclarations);
        extractClassDeclarations(this.file2, classBDeclarations);
        
        for (FieldDeclaration field : classADeclarations.getFieldDeclaration()) {
            System.out.println(field);
        }
    }
    
    private static CompilationUnit constructCompilationUnit(String filename) {
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

    private static void extractClassDeclarations(CompilationUnit cu, ClassDeclarations declarations) {
        for (TypeDeclaration type : cu.getTypes()) {
            for (BodyDeclaration member : type.getMembers()) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    declarations.addMethodDeclaration(method);
                }
                else if (member instanceof FieldDeclaration) {
                    FieldDeclaration field = (FieldDeclaration) member;
                    declarations.addFieldDeclaration(field);
                }
                else if (member instanceof ConstructorDeclaration) {
                    ConstructorDeclaration constructor = (ConstructorDeclaration) member;
                    declarations.addConstructorDeclaration(constructor);
                }
            }
        }
    }

    public String generateAspect() {
        StringBuilder aspect = new StringBuilder();
        aspect.append("public privileged aspect " + aspectName + " {\n");
        aspect.append(generateMergedConstructors());
        aspect.append("}");
        return aspect.toString();
    }

    private String generateMergedConstructors() {
        StringBuilder constructors = new StringBuilder();

        return constructors.toString();
    }
    
    private String generateMergedFields(String replaceWithType, String replaceFieldName, String replaceWithFieldName, String aspectFieldName) {
        String mergedField = new String();
        mergedField = "// Replace " + replaceFieldName + " with " + replaceWithFieldName + "\n";
        mergedField += replaceWithType + " around(): get(" + replaceWithType + " " + replaceFieldName + ") && !within(" + aspectName + ") {\n";
        mergedField += "    return " + aspectFieldName + ";\n";
        mergedField += "}\n";
        mergedField += "void around (" + replaceWithType + " newval): set(" + replaceWithType + " " + replaceFieldName + ") && args(newval) && !within(" + aspectName + ") {\n";
        mergedField += "    " + aspectFieldName + " = newval;\n";
        mergedField += "}\n";
        return mergedField;
    }

    private String generateOverriddenMethods(String returnType, String overrideMethodName, String overrideWithMethodName, List<String> methodVariableTypes, List<String> methodVariableNames, String aspectFieldName) {
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
    }
    
    private static void test() {
        MergeTool tool = new MergeTool();
        String expected, actual;
        
        expected = "// Replace rules.Solitaire.table with basic.Solitaire.table\n";
        expected += "basic.CardTable around(): get(basic.CardTable rules.Solitaire.table) && !within(MergeSolitaire) {\n";
        expected += "    return this.basicSolitaire.table;\n";
        expected += "}\n";
        expected += "void around (basic.CardTable newval): set(basic.CardTable rules.Solitaire.table) && args(newval) && !within(MergeSolitaire) {\n";
        expected += "    this.basicSolitaire.table = newval;\n";
        expected += "}\n";
        actual = tool.generateMergedFields("basic.CardTable", "rules.Solitaire.table", "basic.Solitaire.table", "this.basicSolitaire.table");
        assert expected.equals(actual);

        expected = "// override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt\n";
        expected += "void around(int i): call(void basic.Solitaire.pickCardAt(int)) && args(i) {\n";
        expected += "    this.rulesSolitaire.pickCardAt(i);\n";
        expected += "}\n";
        actual = tool.generateOverriddenMethods("void", "basic.Solitaire.pickCardAt", "rules.Solitaire.pickCardAt", Arrays.asList("int"), Arrays.asList("i"), "this.rulesSolitaire.pickCardAt");
        assert expected.equals(actual);

        expected = "// override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty\n";
        expected += "boolean around(basic.CardTable t, int start, int count): call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {\n";
        expected += "    return this.basicSolitaire.allEmpty(t, start, count);\n";
        expected += "}\n";
        actual = tool.generateOverriddenMethods("boolean", "rules.Solitaire.allEmpty", "basic.Solitaire.allEmpty", Arrays.asList("basic.CardTable", "int", "int"), Arrays.asList("t", "start", "count"), "this.basicSolitaire.allEmpty");
        assert expected.equals(actual);
    }
}
