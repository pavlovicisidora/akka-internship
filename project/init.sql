DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS workspaces;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
    id UUID PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    passwordHash TEXT NOT NULL
);

CREATE TABLE workspaces (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE projects (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    due_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO workspaces (id, name, description, created_by, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Test Workspace', 'Sample workspace for development', '33333333-3333-3333-3333-333333333333', NOW());

INSERT INTO projects (id, workspace_id, name, description, status, created_by, created_at) VALUES
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Project A', 'Test project A', 'Active', '33333333-3333-3333-3333-333333333333', NOW());

INSERT INTO jobs (id, project_id, name, description, status, due_date, created_by, created_at) VALUES
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'Job 1', 'First job', 'Pending', NOW(), '33333333-3333-3333-3333-333333333333', NOW());

INSERT INTO users (id, email, passwordHash) VALUES
('33333333-3333-3333-3333-333333333333', 'isidora@gmail.com', '$2y$10$0QrqGhnN3xLa9FqVmw0AgeLcJu1lxdenHUqPI2fFcZ9k0grCIci5u');

