package com.uipath.sonar.plugin.checks;

import com.uipath.sonar.plugin.AbstractWorkflowCheck;
import com.uipath.sonar.plugin.uipath.Project;
import com.uipath.sonar.plugin.uipath.Workflow;
import org.dom4j.Element;
import org.dom4j.Node;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import java.util.List;

@Rule(
    key = "EmptyCatchCheck",
    name = "Catch blocks should not be empty",
    description =  "Checks that Catch blocks of Try Catch activities are not empty.",
    status = "BETA",
    priority = Priority.MAJOR,
    tags = {"workflow"}
)
public class EmptyCatchCheck extends AbstractWorkflowCheck {

    public EmptyCatchCheck(){
        super();
    }

    public void execute(Project project, Workflow workflow){
        List<Node> nodes = workflow.getXamlDocument().selectNodes("//xa:TryCatch");
        for(Node tryCatchNode : nodes){
            Element tryCatchElement = (Element)tryCatchNode;

            String displayName = tryCatchElement.attributeValue("DisplayName");

            for(Node activityActionNode : tryCatchElement.selectNodes("//xa:TryCatch.Catches/xa:Catch/xa:ActivityAction")){
                Element activityActionElement = (Element)activityActionNode;

                String exceptionType = activityActionElement.attributeValue("TypeArguments").split(":")[1];

                if(activityActionElement.elements().size() < 2){
                    reportIssue(workflow, "Catch block of '" + displayName + "', catch block for exception type '" + exceptionType +"' should not be empty.");
                }
            }
        }
    }
}
