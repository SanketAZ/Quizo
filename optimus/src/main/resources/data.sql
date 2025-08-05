-- Enable UUID-generation functions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--------------------------------------------------------------------------------
-- 1. Seed quizzes
--------------------------------------------------------------------------------

INSERT INTO quiz (
    quiz_id,
    creator_user_id,
    title,
    description,
    question_count,
    duration_sec,
    start_time,
    status,
    created_at,
    updated_at
)
SELECT
    -- fixed quiz UUIDs
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-java-basics'),
    -- one fixed “system” creator
    '00000000-0000-0000-0000-000000000000'::uuid,
    'Java Basics',
    'Intro to Java fundamentals',
    10,
    10 * 30,      -- e.g. 30 sec per question
    NULL,
    'NOT_STARTED',
    now(),
    now()
    ON CONFLICT (quiz_id) DO NOTHING;

INSERT INTO quiz (
    quiz_id,
    creator_user_id,
    title,
    description,
    question_count,
    duration_sec,
    start_time,
    status,
    created_at,
    updated_at
)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-spring-boot'),
    '00000000-0000-0000-0000-000000000000'::uuid,
    'Spring Boot Core',
    'Deep dive into Spring Boot features',
    15,
    15 * 30,
    NULL,
    'NOT_STARTED',
    now(),
    now()
    ON CONFLICT (quiz_id) DO NOTHING;

--------------------------------------------------------------------------------
-- 2. Seed questions for “Java Basics” (10 questions)
--------------------------------------------------------------------------------

INSERT INTO question (
    question_id,
    text,
    weight,
    created_at,
    updated_at,
    quiz_id
)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-java-basics-q' || i),
    'Question ' || i || ' of 10 for Java Basics?',
    1,
    now(),
    now(),
    q.quiz_id
FROM generate_series(1,10) AS i
         JOIN quiz q
              ON q.quiz_id = uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-java-basics')
    ON CONFLICT (question_id) DO NOTHING;

--------------------------------------------------------------------------------
-- 3. Seed options for “Java Basics” questions (4 options each)
--------------------------------------------------------------------------------

INSERT INTO "option" (
    option_id,
    text,
    is_correct,
    created_at,
    updated_at,
    question_id
)
SELECT
    uuid_generate_v5(
            '00000000-0000-0000-0000-000000000000'::uuid,
            'quiz-java-basics-q' || i || '-opt' || o
    ),
    'Option ' || o || ' for Q' || i,
    (o = 1),   -- first option correct
    now(),
    now(),
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-java-basics-q' || i)
FROM generate_series(1,10) AS i
         CROSS JOIN generate_series(1,4) AS o
    ON CONFLICT (option_id) DO NOTHING;

--------------------------------------------------------------------------------
-- 4. Seed questions for “Spring Boot Core” (15 questions)
--------------------------------------------------------------------------------

INSERT INTO question (
    question_id,
    text,
    weight,
    created_at,
    updated_at,
    quiz_id
)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-spring-boot-q' || i),
    'Question ' || i || ' of 15 for Spring Boot Core?',
    1,
    now(),
    now(),
    q.quiz_id
FROM generate_series(1,15) AS i
         JOIN quiz q
              ON q.quiz_id = uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-spring-boot')
    ON CONFLICT (question_id) DO NOTHING;

--------------------------------------------------------------------------------
-- 5. Seed options for “Spring Boot Core” questions (4 options each)
--------------------------------------------------------------------------------

INSERT INTO "option" (
    option_id,
    text,
    is_correct,
    created_at,
    updated_at,
    question_id
)
SELECT
    uuid_generate_v5(
            '00000000-0000-0000-0000-000000000000'::uuid,
            'quiz-spring-boot-q' || i || '-opt' || o
    ),
    'Option ' || o || ' for Q' || i,
    (o = 1),
    now(),
    now(),
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-spring-boot-q' || i)
FROM generate_series(1,15) AS i
         CROSS JOIN generate_series(1,4) AS o
    ON CONFLICT (option_id) DO NOTHING;

--------------------------------------------------------------------------------
-- 4. Rooms
--------------------------------------------------------------------------------
INSERT INTO room (
    room_id,
    owner_user_id,
    title,
    description,
    created_at,
    updated_at
)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-alpha'),
    '00000000-0000-0000-0000-000000000000'::uuid,
    'Alpha Room',
    'First room hosting Java Basics',
    now(),
    now()
    ON CONFLICT (room_id) DO NOTHING;

INSERT INTO room (
    room_id,
    owner_user_id,
    title,
    description,
    created_at,
    updated_at
)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-beta'),
    '00000000-0000-0000-0000-000000000000'::uuid,
    'Beta Room',
    'Second room hosting both quizzes',
    now(),
    now()
    ON CONFLICT (room_id) DO NOTHING;

--------------------------------------------------------------------------------
-- 5. Room–Quiz links
--------------------------------------------------------------------------------

-- Alpha Room ↔ only Java Basics
INSERT INTO room_quiz (
    room_id,
    quiz_id
)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-alpha'),
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-java-basics')
    ON CONFLICT DO NOTHING;

-- Beta Room ↔ both quizzes
INSERT INTO room_quiz (room_id, quiz_id)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-beta'),
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-java-basics')
    ON CONFLICT DO NOTHING;

INSERT INTO room_quiz (room_id, quiz_id)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-beta'),
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'quiz-spring-boot')
    ON CONFLICT DO NOTHING;

--------------------------------------------------------------------------------
-- 6. Room–User links (non–authors)
--------------------------------------------------------------------------------

-- Define two “other” users:
--   Alice and Bob (they are not the quiz creators)
WITH
    user_alice AS (
        SELECT uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'user-alice') AS uid
    ),
    user_bob AS (
        SELECT uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'user-bob') AS uid
    )
INSERT INTO room_user (room_id, user_id)
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-alpha'),
    ua.uid
FROM user_alice ua
UNION ALL
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-beta'),
    ua.uid
FROM user_alice ua
UNION ALL
SELECT
    uuid_generate_v5('00000000-0000-0000-0000-000000000000'::uuid, 'room-beta'),
    ub.uid
FROM user_bob ub
    ON CONFLICT DO NOTHING;