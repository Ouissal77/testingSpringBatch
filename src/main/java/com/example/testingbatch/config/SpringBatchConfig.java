package com.example.testingbatch.config;

import com.example.testingbatch.entity.Plainte;
import com.example.testingbatch.repository.PlainteRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
public class SpringBatchConfig {

    private PlainteRepository plainteRepository;
    @Bean
    public FlatFileItemReader<Plainte> reader(){

    FlatFileItemReader<Plainte> itemReader= new FlatFileItemReader<>();
    itemReader.setResource(new FileSystemResource("src/main/resources/Fournisseurs.csv"));
    itemReader.setName("csvReader");
    itemReader.setLinesToSkip(1);
    itemReader.setLineMapper(lineMapper());

    return itemReader;
    }

    // this line mapper will read tehe csv file using the ","
    // separator from the headers set at setNames (extract the infro from csv)
    private LineMapper<Plainte> lineMapper() {
        DefaultLineMapper<Plainte> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer= new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(
                "codeFournisseur",
                "nomFournisseur",
                "prenomFournisseur",
                "raisonSociale",
                "adresseFournisseur",
                "emailFournisseur",
                "telephoneFournisseur",
                "nomDuBanque",
                "numeroDuCompte"
        );

        //will map the information to the DB , to the class palinte
        BeanWrapperFieldSetMapper<Plainte> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Plainte.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public PlainteProcessor processor(){
        return new PlainteProcessor();
    }


    @Bean

    public JpaItemWriter<Plainte> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Plainte> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
//    public RepositoryItemWriter<Plainte> writer(){
//        RepositoryItemWriter<Plainte> writer= new RepositoryItemWriter<>();
//        writer.setRepository(plainteRepository);
//        writer.setMethodName("save");
//        return writer;
//    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,EntityManagerFactory entityManagerFactory){
        return new StepBuilder("csv-step",jobRepository).<Plainte,Plainte>chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer(entityManagerFactory))
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,EntityManagerFactory entityManagerFactory){
        return new JobBuilder("importPlaintes",jobRepository)
                .flow(step1(jobRepository,transactionManager,entityManagerFactory))
                .end().build();
    }


}
