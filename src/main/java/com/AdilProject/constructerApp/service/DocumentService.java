package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.DocumentResponse;
import com.AdilProject.constructerApp.entity.Project;
import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.DocumentRepository;
import com.AdilProject.constructerApp.repository.ProjectRepository;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;

    public List<DocumentResponse> getDocuments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return documentRepository.findByProjectId(project.getId())
                .stream()
                .map(d -> new DocumentResponse(d.getFileName(), d.getFileUrl(), d.getUploadedAt()))
                .toList();
    }
}
