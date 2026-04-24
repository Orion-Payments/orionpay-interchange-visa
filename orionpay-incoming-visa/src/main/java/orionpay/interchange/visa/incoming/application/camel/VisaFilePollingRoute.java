package orionpay.interchange.visa.incoming.application.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Rota do Apache Camel responsável por monitorizar uma pasta à procura de novos arquivos de liquidação da Visa.
 * Quando um arquivo chega, ele é movido para a pasta de processamento (.processing) e o Spring Batch Job é iniciado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VisaFilePollingRoute extends RouteBuilder {

    private final JobLauncher jobLauncher;
    private final Job visaSettlementJob;

    // Configurações de diretórios (Num cenário real, poderiam vir do application.yml)
    @Value("${visa.sftp.local-dir:file:data/inbox}")
    private String inputDirectory;

    @Value("${visa.sftp.file-pattern:*.txt}")
    private String filePattern;

    @Value("${visa.sftp.polling-delay:10000}")
    private String pollingDelay;

    @Override
    public void configure() throws Exception {
        // Opções do Camel File Component:
        // antInclude=*.txt: Lê apenas arquivos txt.
        // preMove=.processing: Move o arquivo para uma subpasta ".processing" antes de começar (Evita leitura duplicada se o batch for lento).
        // move=.done: Move o arquivo para ".done" se o batch terminar com sucesso.
        // moveFailed=.error: Move o arquivo para ".error" se houver alguma Exception não tratada.
        // delay=10000: Varre a pasta a cada X segundos.

        String endpointUri = String.format("%s?antInclude=%s&preMove=.processing&move=.done&moveFailed=.error&delay=%s", 
                inputDirectory, filePattern, pollingDelay);

        from(endpointUri)
                .routeId("visaFilePollingRoute")
                .log("Novo arquivo detectado pelo Camel: ${header.CamelFileName}")
                
                // Processador Customizado para ligar o Apache Camel ao Spring Batch
                .process(exchange -> {
                    // O arquivo físico que o Camel acabou de mover para a pasta .processing
                    File processedFile = exchange.getIn().getBody(File.class);
                    String absolutePath = processedFile.getAbsolutePath();

                    log.info("Acionando o Job Spring Batch para o arquivo: {}", absolutePath);

                    // Cria os parâmetros para o Job do Spring Batch
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("fileName", absolutePath)
                            .addLong("time", System.currentTimeMillis()) // Timestamp para garantir que a execução seja única
                            .toJobParameters();

                    // Lança o Job do Batch
                    jobLauncher.run(visaSettlementJob, jobParameters);
                })
                .log("Processamento do arquivo ${header.CamelFileName} foi encaminhado ao Batch com sucesso.");
    }
}
