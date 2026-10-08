package com.taskflow.config;

import com.taskflow.common.enums.Role;
import com.taskflow.common.enums.SprintStatus;
import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.entity.Project;
import com.taskflow.entity.Sprint;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.SprintRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ProjectRepository projectRepository,
                           SprintRepository sprintRepository,
                           TaskRepository taskRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded with data. Skipping initialization.");
            return;
        }

        log.info("Seeding initial Taskflow users, projects, sprints, and tasks...");

        // 1. Users
        User admin = userRepository.save(new User(
                "System Administrator",
                "admin@taskflow.com",
                passwordEncoder.encode("password123"),
                Role.ADMIN
        ));

        User manager = userRepository.save(new User(
                "Sarah Manager",
                "manager@taskflow.com",
                passwordEncoder.encode("password123"),
                Role.MANAGER
        ));

        User dev = userRepository.save(new User(
                "Alex Developer",
                "member@taskflow.com",
                passwordEncoder.encode("password123"),
                Role.MEMBER
        ));

        // 2. Project
        Project project = projectRepository.save(new Project(
                "Taskflow Core Platform",
                "Enterprise agile task and project management ecosystem with Kanban workflow"
        ));

        // 3. Sprint
        Sprint sprint = sprintRepository.save(new Sprint(
                "Sprint 1 - Foundation & Architecture",
                "Implement full stack CRUD, security, task filtering, and responsive board",
                LocalDate.now().minusDays(3),
                LocalDate.now().plusDays(11),
                SprintStatus.ACTIVE,
                project.getId()
        ));

        // 4. Sample Tasks across statuses
        taskRepository.save(new Task(
                "Implement JWT Authentication",
                "Configure Spring Security filter chain, token generator, and login endpoint",
                TaskStatus.DONE,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(2),
                dev.getId(),
                project.getId(),
                sprint.getId()
        ));

        taskRepository.save(new Task(
                "Design Kanban Board UI",
                "Build interactive drag/click status columns for TODO, IN_PROGRESS, IN_REVIEW, DONE",
                TaskStatus.IN_PROGRESS,
                TaskPriority.CRITICAL,
                LocalDate.now().plusDays(4),
                dev.getId(),
                project.getId(),
                sprint.getId()
        ));

        taskRepository.save(new Task(
                "Setup Jira-style Filter Bar",
                "Enable search query, priority filter, assignee filter, and pagination",
                TaskStatus.IN_REVIEW,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(5),
                manager.getId(),
                project.getId(),
                sprint.getId()
        ));

        taskRepository.save(new Task(
                "Prepare Kafka Notification Infrastructure",
                "Configure producer topics and consumer listeners for task events",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(8),
                admin.getId(),
                project.getId(),
                sprint.getId()
        ));

        log.info("Database seeding completed successfully!");
    }
}
