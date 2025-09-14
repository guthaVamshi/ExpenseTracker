package org.learnspring.expensetracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class KeepAliveConfig {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveConfig.class);

    @Value("${server.port:8080}")
    private String port;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Self-ping to prevent the application from sleeping on Render free tier
     * Runs every 10 minutes to keep the app warm
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void keepAlive() {
        try {
            String healthUrl = "http://localhost:" + port + "/health";
            logger.debug("Sending keep-alive ping to: {}", healthUrl);
            
            String response = restTemplate.getForObject(healthUrl, String.class);
            logger.debug("Keep-alive ping successful. Response length: {} chars", 
                        response != null ? response.length() : 0);
                        
        } catch (Exception e) {
            logger.warn("Keep-alive ping failed: {}", e.getMessage());
        }
    }

    /**
     * Memory cleanup task to prevent memory leaks
     * Runs every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void memoryCleanup() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // Suggest garbage collection
            System.gc();
            
            long afterMemory = runtime.totalMemory() - runtime.freeMemory();
            long cleaned = beforeMemory - afterMemory;
            
            logger.debug("Memory cleanup: {} bytes freed. Used memory: {} MB", 
                        cleaned, afterMemory / (1024 * 1024));
                        
        } catch (Exception e) {
            logger.warn("Memory cleanup failed: {}", e.getMessage());
        }
    }

    /**
     * Application status logger
     * Logs application status every hour for monitoring
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void logApplicationStatus() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long uptime = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            
            logger.info("Application Status - Uptime: {} ms, Memory: {}/{} MB", 
                       uptime, usedMemory, maxMemory);
                       
        } catch (Exception e) {
            logger.warn("Status logging failed: {}", e.getMessage());
        }
    }
}
