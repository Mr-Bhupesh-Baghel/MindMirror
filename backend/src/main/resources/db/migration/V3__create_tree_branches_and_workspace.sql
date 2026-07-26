CREATE TABLE tree_branch (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    parent_id UUID REFERENCES tree_branch(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(16) NOT NULL,
    color VARCHAR(16) NOT NULL DEFAULT '#6f9e63',
    position INTEGER NOT NULL DEFAULT 0,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX tree_branch_user_parent_idx ON tree_branch(user_id, parent_id, position);

CREATE TABLE branch_item (
    id UUID PRIMARY KEY,
    branch_id UUID NOT NULL REFERENCES tree_branch(id) ON DELETE CASCADE,
    item_type VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    due_on DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX branch_item_branch_type_idx ON branch_item(branch_id, item_type);
