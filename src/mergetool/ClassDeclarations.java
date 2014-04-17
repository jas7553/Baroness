package mergetool;
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

    private final List<MethodDeclaration> methodDeclarations;
    private final List<FieldDeclaration> fieldDeclarations;
    private final List<ConstructorDeclaration> constructorDeclarations;

    public ClassDeclarations(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;

        methodDeclarations = new ArrayList<>();
        fieldDeclarations = new ArrayList<>();
        constructorDeclarations = new ArrayList<>();

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
        methodDeclarations.add(declaration);
    }

    public void addFieldDeclaration(FieldDeclaration declaration) {
        declaration.setJavaDoc(null);
        fieldDeclarations.add(declaration);
    }

    public void addConstructorDeclaration(ConstructorDeclaration declaration) {
        declaration.setJavaDoc(null);
        constructorDeclarations.add(declaration);
    }

    public List<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    public List<FieldDeclaration> getFieldDeclarations() {
        return fieldDeclarations;
    }

    public List<ConstructorDeclaration> getConstructorDeclarations() {
        return constructorDeclarations;
    }

    public MethodDeclaration getMethodDeclarationForName(String name) {
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getName().equals(name)) {
                return methodDeclaration;
            }
        }

        return null;
    }

    public FieldDeclaration getFieldDeclarationForName(String name) {
        for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
            if (fieldDeclaration.getVariables().get(0).getId().toString().equals(name)) {
                return fieldDeclaration;
            }
        }

        return null;
    }
}
