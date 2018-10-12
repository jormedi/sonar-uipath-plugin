package com.uipath.sonar.plugin.checks;

import com.uipath.sonar.plugin.uipath.Project;
import com.uipath.sonar.plugin.AbstractProjectCheck;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(
    key = "ValidateMainWorkflowCheck",
    name = "Validate Main Workflow.",
    description =  "Verifies that the Main workflow in project.json is valid.",
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
            project.reportIssue(getRuleKey(), "Main workflow '" + mainPath + "' does not exist!");
        }
    }
}