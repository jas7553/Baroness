import java.util.Iterator;
import java.util.List;

public class Generator {
    
    public static String generateMergedField(String aspectName, String replaceWithType, String replaceFieldName, String replaceWithFieldName, String aspectFieldName) {
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

    public static String generateOverriddenMethod(String returnType, String overrideMethodName, String overrideWithMethodName, List<String> methodVariableTypes, List<String> methodVariableNames, String aspectFieldName) {
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

}
