package com.example.testingbatch.config;

import com.example.testingbatch.entity.Plainte;
import org.springframework.batch.item.ItemProcessor;

public class PlainteProcessor implements ItemProcessor<Plainte,Plainte> {
    @Override
    public Plainte process(Plainte plainte) throws Exception {
        return plainte;
    }
}
