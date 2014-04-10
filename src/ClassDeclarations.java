import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclarations {
    private List<MethodDeclaration> classAMethodDeclarations;
    private List<FieldDeclaration> classAFieldDeclarations;
    private List<ConstructorDeclaration> classAConstructorDeclarations;
    
    public ClassDeclarations() {
        classAMethodDeclarations = new ArrayList<>();
        classAFieldDeclarations = new ArrayList<>();
        classAConstructorDeclarations = new ArrayList<>();
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

    public List<MethodDeclaration> getMethodDeclaration() {
        return classAMethodDeclarations;
    }

    public List<FieldDeclaration> getFieldDeclaration() {
        return classAFieldDeclarations;
    }

    public List<ConstructorDeclaration> getConstructorDeclaration() {
        return classAConstructorDeclarations;
    }
}
