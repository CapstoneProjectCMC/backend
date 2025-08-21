package com.codecampus.post.config.DbConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgresFullTextSearchConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // 1. Thêm cột search_vector (nếu chưa có)
        jdbcTemplate.execute("""
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM information_schema.columns
                    WHERE table_name='post' AND column_name='search_vector'
                ) THEN
                    ALTER TABLE post ADD COLUMN search_vector tsvector;
                END IF;
            END
            $$;
        """);

        // 2. Cập nhật dữ liệu hiện tại
        jdbcTemplate.execute("""
            UPDATE post
            SET search_vector =
                setweight(to_tsvector('simple', coalesce(title, '')), 'A') ||
                setweight(to_tsvector('simple', coalesce(content, '')), 'B');
        """);

        // 3. Tạo index GIN (nếu chưa có)
        jdbcTemplate.execute("""
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_indexes
                    WHERE tablename = 'post' AND indexname = 'idx_post_search_vector'
                ) THEN
                    CREATE INDEX idx_post_search_vector
                    ON post USING GIN (search_vector);
                END IF;
            END
            $$;
        """);

        // 4. Tạo trigger function và trigger (nếu chưa có)
        jdbcTemplate.execute("""
            CREATE OR REPLACE FUNCTION post_search_vector_trigger() RETURNS trigger AS $$
            begin
              new.search_vector :=
                setweight(to_tsvector('simple', coalesce(new.title, '')), 'A') ||
                setweight(to_tsvector('simple', coalesce(new.content, '')), 'B');
              return new;
            end
            $$ LANGUAGE plpgsql;
        """);

        jdbcTemplate.execute("""
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1
                    FROM pg_trigger
                    WHERE tgname = 'tsvectorupdate'
                ) THEN
                    CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE
                    ON post FOR EACH ROW EXECUTE PROCEDURE post_search_vector_trigger();
                END IF;
            END
            $$;
        """);
    }
}
