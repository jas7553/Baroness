import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclarations {
    private final CompilationUnit compilationUnit;

    private final List<MethodDeclaration> classAMethodDeclarations;
    private final List<FieldDeclaration> classAFieldDeclarations;
    private final List<ConstructorDeclaration> classAConstructorDeclarations;

    public ClassDeclarations(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;

        classAMethodDeclarations = new ArrayList<>();
        classAFieldDeclarations = new ArrayList<>();
        classAConstructorDeclarations = new ArrayList<>();

        extractClassDeclarations();
    }

    private void extractClassDeclarations() {
        for (TypeDeclaration type : compilationUnit.getTypes()) {
            for (BodyDeclaration member : type.getMembers()) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    addMethodDeclaration(method);
                } else if (member instanceof FieldDeclaration) {
                    FieldDeclaration field = (FieldDeclaration) member;
                    addFieldDeclaration(field);
                } else if (member instanceof ConstructorDeclaration) {
                    ConstructorDeclaration constructor = (ConstructorDeclaration) member;
                    addConstructorDeclaration(constructor);
                }
            }
        }
    }

    public void addMethodDeclaration(MethodDeclaration declaration) {
        declaration.setJavaDoc(null);
        classAMethodDeclarations.add(declaration);
    }

    public void addFieldDeclaration(FieldDeclaration declaration) {
        declaration.setJavaDoc(null);
        classAFieldDeclarations.add(declaration);
    }

    public void addConstructorDeclaration(ConstructorDeclaration declaration) {
        declaration.setJavaDoc(null);
        classAConstructorDeclarations.add(declaration);
    }

    public List<MethodDeclaration> getMethodDeclarations() {
        return classAMethodDeclarations;
    }

    public List<FieldDeclaration> getFieldDeclarations() {
        return classAFieldDeclarations;
    }

    public List<ConstructorDeclaration> getConstructorDeclarations() {
        return classAConstructorDeclarations;
    }
}
