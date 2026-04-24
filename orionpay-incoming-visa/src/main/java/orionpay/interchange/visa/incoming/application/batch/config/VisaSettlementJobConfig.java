package orionpay.interchange.visa.incoming.application.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.incoming.application.batch.listener.VisaJobListener;
import orionpay.interchange.visa.incoming.application.batch.processor.VisaTc50ItemProcessor;
import orionpay.interchange.visa.incoming.application.batch.tasklet.ReadVisaHeaderTasklet;
import orionpay.interchange.visa.incoming.application.batch.tasklet.ReadVisaTrailerTasklet;
import orionpay.interchange.visa.incoming.application.batch.tasklet.ReconciliationTasklet;
import orionpay.interchange.visa.incoming.application.batch.writer.NoOpItemWriter;
import orionpay.interchange.visa.incoming.application.dto.TC50TCR0Dto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class VisaSettlementJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ReadVisaHeaderTasklet readVisaHeaderTasklet;
    private final ReadVisaTrailerTasklet readVisaTrailerTasklet;
    private final ReconciliationTasklet reconciliationTasklet;
    private final VisaJobListener visaJobListener;

    private final VisaTc50ItemProcessor itemProcessor;
    private final NoOpItemWriter itemWriter;

    @Bean
    public Job visaSettlementJob() {
        return new JobBuilder("visaSettlementJob", jobRepository)
                .listener(visaJobListener)
                .start(readVisaHeaderStep())
                .next(visaSettlementStep()) // Transformado para Chunk
                .next(readVisaTrailerStep()) // Lê o trailer (TCR92) após o processamento das linhas e valida integridade
                .next(reconciliationStep())
                .build();
    }

    @Bean
    public Step readVisaHeaderStep() {
        return new StepBuilder("readVisaHeaderStep", jobRepository)
                .tasklet(readVisaHeaderTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step readVisaTrailerStep() {
        return new StepBuilder("readVisaTrailerStep", jobRepository)
                .tasklet(readVisaTrailerTasklet, transactionManager)
                .build();
    }

    /**
     * Define o ItemReader dinâmico, recuperando o parâmetro 'fileName' do contexto do Job.
     */
    @Bean
    @StepScope
    public FlatFileItemReader<TC50TCR0Dto> dynamicTc50ItemReader(@Value("#{jobParameters['fileName']}") String fileName) {
        // 1. Criamos o Tokenizer específico para a transação (TC50 / TCR0)
        FixedLengthTokenizer tc50Tokenizer = new FixedLengthTokenizer();
        tc50Tokenizer.setNames("transactionCode", "destinationBin", "usageCode", "reasonCode", "amount", "currencyCode", "settlementDate", "rawLine");
        tc50Tokenizer.setColumns(
                new Range(1, 2),   new Range(3, 8),   new Range(9, 9), new Range(10, 13),
                new Range(31, 42), new Range(72, 74), new Range(75, 78), new Range(1, 168)
        );
        tc50Tokenizer.setStrict(false);

        // 2. Criamos um Tokenizer "Vazio" para o Header e Trailer (para o Reader não quebrar ao passar por eles)
        FixedLengthTokenizer ignoreTokenizer = new FixedLengthTokenizer();
        ignoreTokenizer.setColumns(new Range(1, 2));
        ignoreTokenizer.setNames("transactionCode");
        ignoreTokenizer.setStrict(false);

        // 3. Mapeamos qual Tokenizer usar dependendo do início da linha
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("50*", tc50Tokenizer); // Se começar com 50, usa o real
        tokenizers.put("00*", tc50Tokenizer); // Se começar com 00, usa o real
        tokenizers.put("90*", ignoreTokenizer); // Header: Ignorar
        tokenizers.put("92*", ignoreTokenizer); // Trailer: Ignorar

        PatternMatchingCompositeLineTokenizer compositeTokenizer = new PatternMatchingCompositeLineTokenizer();
        compositeTokenizer.setTokenizers(tokenizers);

        return new FlatFileItemReaderBuilder<TC50TCR0Dto>()
                .name("tc50ItemReader")
                .resource(new FileSystemResource(fileName))
                .lineMapper(new DefaultLineMapper<TC50TCR0Dto>() {{
                    setLineTokenizer(compositeTokenizer);
                    setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                        setTargetType(TC50TCR0Dto.class);
                    }});
                }})
                .build();
    }

    /**
     * O coração do processamento de Batch (Chunk-Oriented).
     * Lê pacotes de N linhas, processa com o Caso de Uso, e "grava" (o log do batch, pois o banco já foi gravado pelo Processor).
     * Permite saltar erros no processamento (falhas de cast numérico/parsing).
     */
    @Bean
    public Step visaSettlementStep() {
        return new StepBuilder("visaSettlementStep", jobRepository)
                .<TC50TCR0Dto, VisaSettlementMaster>chunk(1000, transactionManager) // Chunk Size de 1.000 transações
                .reader(dynamicTc50ItemReader(null)) // Spring Batch injeta o parâmetro real via reflexão
                .processor(itemProcessor)
                .writer(itemWriter)
                .faultTolerant()
                .skip(Exception.class) // Pula a linha se o Processor lançar exceção
                .skipLimit(100) // Se o arquivo tiver mais de 100 linhas erradas, ele aborta
                .build();
    }

    @Bean
    public Step reconciliationStep() {
        return new StepBuilder("reconciliationStep", jobRepository)
                .tasklet(reconciliationTasklet, transactionManager)
                .build();
    }
}
