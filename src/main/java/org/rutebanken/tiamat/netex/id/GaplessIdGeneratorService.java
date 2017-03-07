package org.rutebanken.tiamat.netex.id;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GaplessIdGeneratorService {
    private final ExecutorService executorService;

    private final GaplessIdGenerator gaplessIdGenerator;

    @Autowired
    public GaplessIdGeneratorService(GaplessIdGenerator gaplessIdGenerator) {
        this.gaplessIdGenerator = gaplessIdGenerator;
        executorService = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("id-generator-%d").build());
    }

    @PostConstruct
    public void startExecutorService() {
        executorService.submit(() -> gaplessIdGenerator.run());
    }

}
