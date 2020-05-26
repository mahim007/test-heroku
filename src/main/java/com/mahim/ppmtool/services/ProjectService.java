package com.mahim.ppmtool.services;

import com.mahim.ppmtool.domain.Backlog;
import com.mahim.ppmtool.domain.Project;
import com.mahim.ppmtool.domain.User;
import com.mahim.ppmtool.exceptions.ProjectIdException;
import com.mahim.ppmtool.exceptions.ProjectNotFoundException;
import com.mahim.ppmtool.repositories.BacklogRepository;
import com.mahim.ppmtool.repositories.ProjectRepository;
import com.mahim.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String userName) {
        if (project.getId() != null) {
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
            if (existingProject != null && (!existingProject.getProjectLeader().equals(userName))) {
                throw new ProjectNotFoundException("Project '" + project.getProjectIdentifier() + "' not found in your account!");
            } else if (existingProject == null) {
                throw new ProjectNotFoundException("Project '" + project.getProjectIdentifier() + "' cannot be updated because it doesn't exist");
            }
        }

        try {
            User user = userRepository.findByUsername(userName);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            if (project.getId() == null) {
                Backlog backlog = new Backlog();
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
                project.setBacklog(backlog);
            } else {
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }
            return projectRepository.save(project);
        } catch (Exception ex) {
            throw new ProjectIdException("PROJECT ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists!");
        }
    }

    public Project findProjectByIdentifier(String projectId, String userName) {
        Project project = projectRepository.findByProjectIdentifier(projectId);
        if (project == null) {
            throw new ProjectIdException("PROJECT ID '" + projectId + "' does not exist!");
        }

        if (!project.getProjectLeader().equals(userName)) {
            throw new ProjectNotFoundException("Project '" + projectId + "' not found in your account!");
        }
        return project;
    }

    public Iterable<Project> findAllProjects(String userName) {
        return projectRepository.findAllByProjectLeader(userName);
    }

    public void deleteProjectByIdentifier(String projectId, String userName) {
        projectRepository.delete(findProjectByIdentifier(projectId, userName));
    }
}
