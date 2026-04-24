package orionpay.interchange.visa.incoming.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/visa/jobs")
@RequiredArgsConstructor
public class JobLauncherController {

    private final JobLauncher jobLauncher;
    private final Job visaSettlementJob;

    @PostMapping("/settlement/start")
    public ResponseEntity<String> startJob(@RequestParam(value = "fileName", defaultValue = "visa_settlement_test.txt") String fileName) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileName", fileName)
                    .addLong("time", System.currentTimeMillis()) // Ensures uniqueness
                    .toJobParameters();

            log.info("Iniciando Job do Visa Settlement para o arquivo: {}", fileName);
            jobLauncher.run(visaSettlementJob, jobParameters);

            return ResponseEntity.ok("Job iniciado com sucesso para o arquivo: " + fileName);
        } catch (Exception e) {
            log.error("Erro ao iniciar o Job do Visa Settlement", e);
            return ResponseEntity.internalServerError().body("Erro ao iniciar o Job: " + e.getMessage());
        }
    }
}
