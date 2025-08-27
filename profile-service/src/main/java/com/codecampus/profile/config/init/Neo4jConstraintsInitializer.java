package com.codecampus.profile.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Neo4jConstraintsInitializer implements ApplicationRunner {
  private final Neo4jClient neo4j;

  @Override
  public void run(ApplicationArguments args) {
    // Đảm bảo Exercise.exerciseId UNIQUE
    createUniqueConstraint("uc_exercise_exerciseId", "Exercise", "exerciseId");
    createUniqueConstraint("uc_contest_contestId", "Contest", "contestId");
    createUniqueConstraint("uc_org_orgId", "Organization", "orgId");
    createUniqueConstraint("uc_post_postId", "Post", "postId");
    createUniqueConstraint("uc_file_fileId", "FileResource", "fileId");
    createUniqueConstraint("uc_package_packageId", "Package", "packageId");
  }

  private void createUniqueConstraint(String name, String label,
                                      String property) {
    // Neo4j 5+
    String v5 = "CREATE CONSTRAINT " + name
        + " IF NOT EXISTS FOR (n:" + label + ") REQUIRE n." + property +
        " IS UNIQUE";

    // Neo4j 4.4 (legacy)
    String v44 = "CREATE CONSTRAINT " + name
        + " IF NOT EXISTS ON (n:" + label + ") ASSERT n." + property +
        " IS UNIQUE";

    try {
      neo4j.query(v5).run();
      log.info("[Neo4j] Created/kept constraint (v5 syntax): {}", name);
    } catch (Exception e) {
      log.warn("[Neo4j] v5 syntax failed for {}, trying v4.4 syntax. Cause: {}",
          name, e.getMessage());
      try {
        neo4j.query(v44).run();
        log.info("[Neo4j] Created/kept constraint (v4.4 syntax): {}", name);
      } catch (Exception e2) {
        log.error("[Neo4j] Failed to create constraint {}: {}", name,
            e2.getMessage(), e2);
      }
    }
  }
}