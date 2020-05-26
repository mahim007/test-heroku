package com.mahim.ppmtool.services;

import com.mahim.ppmtool.domain.Backlog;
import com.mahim.ppmtool.domain.ProjectTask;
import com.mahim.ppmtool.exceptions.ProjectNotFoundException;
import com.mahim.ppmtool.repositories.BacklogRepository;
import com.mahim.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String userName) {

        Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, userName).getBacklog();
        projectTask.setBacklog(backlog);
        Integer BacklogSequence = backlog.getPTSequence();
        BacklogSequence++;
        backlog.setPTSequence(BacklogSequence);
        projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + BacklogSequence);
        projectTask.setProjectIdentifier(projectIdentifier.toUpperCase());
        if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
            projectTask.setPriority(3);
        }

        if (projectTask.getStatus() == "" || projectTask.getStatus() ==  null) {
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);

    }

    public Iterable<ProjectTask> findBacklogById(String id, String userName) {

        projectService.findProjectByIdentifier(id, userName);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String userName) {
        projectService.findProjectByIdentifier(backlog_id, userName);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Project task: '" + pt_id + "' not found.");
        }

        if (!projectTask.getBacklog().getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project task :'" + pt_id + "' does not exist in project '" + backlog_id + "'");
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedProjectTask, String backlog_id, String pt_id, String userName) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, userName);
        projectTask = updatedProjectTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String userName) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, userName);
        projectTaskRepository.delete(projectTask);
    }
}
