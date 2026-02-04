package org.microservice.productserver;

// /home/sium/all source code/projects/E_Commerce_Microservice-main/project/product/productServer/src/main/java/org/microservice/productserver/ProductServerApplication.java

package org.microservice.productserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServerApplication {

    public static void main(String[] args) {
        // Fix for Render's DATABASE_URL format:
        // Render provides: postgres://user:pass@host:port/db
        // Java needs:      jdbc:postgresql://host:port/db
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isBlank()) {
            if (databaseUrl.startsWith("postgres://")) {
                String jdbcUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
                // Set the property that application.yml expects
                System.setProperty("SPRING_DATASOURCE_URL", jdbcUrl);
            }
        }

        SpringApplication.run(ProductServerApplication.class, args);
    }

}