-- V3: Add constraints and audit checks
ALTER TABLE users ADD CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'MANAGER', 'MEMBER'));
ALTER TABLE sprints ADD CONSTRAINT chk_sprint_status CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED'));
ALTER TABLE tasks ADD CONSTRAINT chk_task_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'));
ALTER TABLE tasks ADD CONSTRAINT chk_task_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'));
