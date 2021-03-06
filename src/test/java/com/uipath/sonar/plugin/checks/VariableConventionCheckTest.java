package com.uipath.sonar.plugin.checks;

import com.uipath.sonar.plugin.Issues;
import com.uipath.sonar.plugin.testprojects.LoadProject;
import com.uipath.sonar.plugin.uipath.Project;
import com.uipath.sonar.plugin.uipath.Workflow;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VariableConventionCheckTest {

    private VariableConventionCheck check = new VariableConventionCheck();
    private Project argsAndVars;
    private Workflow main;
    private Workflow allCamelCase;
    private Workflow allPascalCase;
    private Workflow allUpperCase;
    private Workflow allLowerCase;

    @Before
    public void setUp() throws Exception {
        argsAndVars = LoadProject.withPath("ArgsAndVars");
        main = argsAndVars.getWorkflowNamed("Main").get();
        allCamelCase = argsAndVars.getWorkflowNamed("allCamelCase").get();
        allPascalCase = argsAndVars.getWorkflowNamed("AllPascalCase").get();
        allUpperCase = argsAndVars.getWorkflowNamed("ALLUPPERCASE").get();
        allLowerCase = argsAndVars.getWorkflowNamed("alllowercase").get();
    }

    @Test
    public void execute() throws IOException, DocumentException {
        testCamelCase();
        testPascalCase();
    }

    private void testCamelCase() throws IOException, DocumentException {
        check.executeIgnoreCommonExceptions(argsAndVars, main);
        assertEquals(0, Issues.getCount());

        check.execute(argsAndVars, allCamelCase);
        assertEquals(0, Issues.getCount());
        Issues.clear();

        check.execute(argsAndVars, allPascalCase);
        assertTrue(Issues.getCount() > 0);
        Issues.clear();

        check.execute(argsAndVars, allUpperCase);
        assertTrue(Issues.getCount() > 0);
        Issues.clear();

        /* Commenting this out because this check is able to test a camelcase variable that is actually lowercase.
        check.execute(argsAndVars, allLowerCase);
        assertTrue(Issues.getCount() > 0);
        Issues.clear();*/
    }

    private void testPascalCase(){
        check.overwriteProperty(VariableConventionCheck.VARIABLE_FORMAT_KEY, "^[A-Z][\\w\\d]*$");

        check.execute(argsAndVars, allPascalCase);
        assertEquals(0, Issues.getCount());
        Issues.clear();

        check.execute(argsAndVars, allCamelCase);
        assertTrue(Issues.getCount() > 0);
        Issues.clear();

        /* Commenting out - Uppercase will validate as PascalCase
        check.execute(argsAndVars, allUpperCase);
        assertTrue(Issues.getCount() > 0);
        Issues.clear();*/

        check.execute(argsAndVars, allLowerCase);
        assertTrue(Issues.getCount() > 0);
        Issues.clear();
    }
}