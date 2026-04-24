package orionpay.interchange.visa.incoming.application.batch.reader;


import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import orionpay.interchange.visa.incoming.application.dto.TC50TCR0Dto;

@Configuration
public class VisaTc50ItemReader {

    @Bean
    @StepScope // ESSENCIAL para ler o fileName dos parâmetros do Job
    public FlatFileItemReader<TC50TCR0Dto> tc50ItemReader(@Value("#{jobParameters['fileName']}") String fileName) {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("transactionCode", "destinationBin", "usageCode", "reasonCode", "amount", "currencyCode", "settlementDate", "rawLine");

        // Ranges oficiais conforme sua lógica
        tokenizer.setColumns(
                new Range(1, 2),     // transactionCode
                new Range(5, 10),    // destinationBin
                new Range(15, 15),   // usageCode
                new Range(16, 19),   // reasonCode
                new Range(31, 42),   // amount
                new Range(72, 74),   // currencyCode
                new Range(75, 78),   // settlementDate
                new Range(1, 168)    // rawLine
        );
        tokenizer.setStrict(false);

        DefaultLineMapper<TC50TCR0Dto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(TC50TCR0Dto.class);
        }});

        return new FlatFileItemReaderBuilder<TC50TCR0Dto>()
                .name("tc50ItemReader")
                .resource(new FileSystemResource(fileName)) // Agora ele lê o arquivo real
                .linesToSkip(1) // PULA A LINHA 90 (HEADER)
                .lineMapper(lineMapper)
                .build();
    }
}