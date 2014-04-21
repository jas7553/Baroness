package mergetool;

import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeclarationConverter {
    
    public static List<String> methodDeclarationToParameterTypeList(MethodDeclaration methodDeclaration) {
        List<String> parameterTypeList = new ArrayList<>();
        
        List<Parameter> parameters = methodDeclaration.getParameters();
        for (Parameter parameter : parameters) {
            parameterTypeList.add(parameter.getType().toString());
        }
        
        return parameterTypeList;
    }
    
    public static List<String> methodDeclarationToParameterNameList(MethodDeclaration methodDeclaration) {
        List<String> parameterNameList = new ArrayList<>();
        
        List<Parameter> parameters = methodDeclaration.getParameters();
        for (Parameter parameter : parameters) {
            parameterNameList.add(parameter.getId().getName());
        }
        
        return parameterNameList;
    }
    
    public static List<FieldDeclaration> unionFieldDeclarations(List<FieldDeclaration> fieldsA, List<FieldDeclaration> fieldsB) {
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
    
    public static String parameterNamesAndTypesFromParameterList(List<Parameter> parameters) {
        String advice = new String();
        Iterator<Parameter> parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            advice += parameter.getType().toString() + " " + parameter.getId().getName();
            if (parameterIterator.hasNext()) {
                advice += ", ";
            }
        }
        return advice;
    }
    
    public static String parameterNamesFromParameterList(List<Parameter> parameters) {
        String advice = new String();
        Iterator<Parameter> parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            advice += parameter.getId().getName();
            if (parameterIterator.hasNext()) {
                advice += ", ";
            }
        }
        return advice;
    }
    
    public static String parameterTypesFromParameterList(List<Parameter> parameters) {
        String advice = new String();
        Iterator<Parameter> parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            Parameter parameter = parameterIterator.next();
            advice += parameter.getType().toString();
            if (parameterIterator.hasNext()) {
                advice += ", ";
            }
        }
        return advice;
    }
    
}
