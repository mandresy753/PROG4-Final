ALTER TABLE exams
    ALTER COLUMN exam_date TYPE timestamp WITHOUT TIME ZONE
    USING exam_date::timestamp;
