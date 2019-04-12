package com.uipath.sonar.plugin.checks;

import com.uipath.sonar.plugin.Issues;
import com.uipath.sonar.plugin.uipath.Project;
import com.uipath.sonar.plugin.AbstractProjectCheck;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(
    key = "ValidateMainWorkflowCheck",
    name = "Check that the 'Main' workflow exists",
    description =  "Verifies that the Main workflow in project.json exists and is valid.",
    status = "BETA",
    priority = Priority.BLOCKER,
    tags = {"project"}
)
public class ValidateMainWorkflowCheck extends AbstractProjectCheck {

    public ValidateMainWorkflowCheck(){
        super();
    }

    @Override
    public void execute(Project project){

        String mainPath = project.getProjectJson().main;

        if(!project.getSensorContext().fileSystem().resolvePath(mainPath).exists()){
            Issues.report(project, getRuleKey(), "Main workflow '" + mainPath + "' does not exist!");
        }
    }
}
