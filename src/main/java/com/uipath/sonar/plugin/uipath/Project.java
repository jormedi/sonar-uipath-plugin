package com.uipath.sonar.plugin.uipath;

import com.google.gson.Gson;
import com.uipath.sonar.plugin.HasInputFile;
import com.uipath.sonar.plugin.UiPathSensor;
import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Project represents a UiPath project, built from a project.json file.
 *
 * It contains a List of Workflow objects that belong to the project.
 * Issues must be created on a SensorContext and reported on InputFile objects. This class wraps an InputFile
 * for project.json. Any calls to reportIssue will be created on the project.json file.
 */
public class Project implements HasInputFile {

    private File directory;
    private ArrayList<Workflow> workflows;
    private ProjectJson projectJson;
    private UiPathSensor sensor;
    private SensorContext sensorContext;
    private InputFile inputFile;

    public Project(File directory) throws DocumentException{

        if(!directory.exists()){
            throw new IllegalArgumentException("The specified directory does not exist.\n" + directory);
        }

        if(!directory.isDirectory()){
            throw new IllegalArgumentException("The File argument passed to the Project constructor should be the root directory of UiPath project. Received a file.");
        }

        this.directory = directory;

        try{
            File projectJsonFile = Arrays.stream(directory.listFiles()).filter(f -> f.getName().equals("project.json")).findFirst().get();
            Gson gson = new Gson();
            projectJson = gson.fromJson(new FileReader(projectJsonFile), ProjectJson.class);
        }
        catch(Exception ex){
            throw new IllegalArgumentException("Could not find project.json in the given directory '" + directory.toString() + "'.", ex);
        }

        this.workflows = new ArrayList<>();

        for(File xamlFile : FileUtils.listFiles(directory, new String[] {"xaml"}, true)){
            workflows.add(new Workflow(this, xamlFile));
        }
    }

    public Project(File directory, UiPathSensor sensor, SensorContext sensorContext) throws DocumentException {
        this(directory);
        this.sensor = sensor;
        this.sensorContext = sensorContext;
        inputFile = sensor.getProjectJson();

        for(Workflow workflow : getWorkflows()){
            workflow.setInputFile(sensor.getInputFileForWorkflow(workflow.getFile()).get());
        }
    }

    public ArrayList<Workflow> getWorkflows(){
        return workflows;
    }

    public ProjectJson getProjectJson(){
        return projectJson;
    }

    public Optional<Workflow> getWorkflowNamed(String name){
        return workflows.stream().filter(wf -> wf.getName().equals(name) || wf.getFileName().equals(name)).findAny();
    }

    public Optional<Workflow> getWorkflowWithPath(String path) {
        return workflows.stream().filter(wf -> wf.getPath().endsWith(Paths.get(path))).findAny();
    }

    public String getName(){
        return projectJson.name;
    }

    public File getDirectory(){
        return directory;
    }

    public boolean hasInputFile(){
        return inputFile != null;
    }

    public InputFile getInputFile(){
        return inputFile;
    }
}