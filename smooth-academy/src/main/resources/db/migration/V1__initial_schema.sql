CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    password_hash VARCHAR(255) NOT NULL,
    is_premium BOOLEAN NOT NULL DEFAULT false,
    premium_subscribed_at TIMESTAMPTZ,
    premium_expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE levels (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(20) NOT NULL,
    label VARCHAR(20) NOT NULL,
    min_range INT NOT NULL,
    max_range INT NOT NULL,
    required_stars INT NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE quiz_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    level_id INT NOT NULL REFERENCES levels(id),
    mode VARCHAR(20) NOT NULL CHECK (mode IN ('practice', 'challenge')),
    total_questions INT NOT NULL,
    correct_count INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    stars_earned INT NOT NULL CHECK (stars_earned BETWEEN 0 AND 3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE quiz_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_result_id UUID NOT NULL REFERENCES quiz_results(id) ON DELETE CASCADE,
    question_index INT NOT NULL,
    num_a INT NOT NULL,
    num_b INT NOT NULL,
    correct_answer INT NOT NULL,
    user_answer INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    response_time_ms INT
);

CREATE INDEX idx_quiz_results_user_id ON quiz_results(user_id);
CREATE INDEX idx_quiz_results_level_id ON quiz_results(level_id);
CREATE INDEX idx_quiz_answers_quiz_result_id ON quiz_answers(quiz_result_id);

INSERT INTO levels (id, name, icon, label, min_range, max_range, required_stars, sort_order) VALUES
    (1, 'Ones Star', '🌟', '1–9', 1, 9, 0, 1),
    (2, 'Tens Star', '⭐🌟', '10–99', 10, 99, 3, 2),
    (3, 'Hundreds Star', '💫', '100–999', 100, 999, 3, 3),
    (4, 'Thousands Star', '✨', '1000–9999', 1000, 9999, 3, 4);
