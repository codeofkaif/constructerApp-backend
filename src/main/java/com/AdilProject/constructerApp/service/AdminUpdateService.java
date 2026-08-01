package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.AdminPostUpdateRequest;
import com.AdilProject.constructerApp.entity.Notification;
import com.AdilProject.constructerApp.entity.Project;
import com.AdilProject.constructerApp.entity.Update;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.NotificationRepository;
import com.AdilProject.constructerApp.repository.ProjectRepository;
import com.AdilProject.constructerApp.repository.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUpdateService {

    private final ProjectRepository projectRepository;
    private final UpdateRepository updateRepository;
    private final NotificationRepository notificationRepository;

    // Do cheezein ek saath honi chahiye: Update row banna + Notification generate hona.
    // @Transactional ensures dono ek saath commit ya rollback honge.
    @Transactional
    public void postUpdate(AdminPostUpdateRequest req) {
        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Update update = Update.builder()
                .title(req.title())
                .description(req.description())
                .thumbnailUrl(req.thumbnailUrl())
                .project(project)
                .build();
        updateRepository.save(update);

        Notification notification = Notification.builder()
                .message("New update on your project: " + req.title())
                .user(project.getOwner())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
}
