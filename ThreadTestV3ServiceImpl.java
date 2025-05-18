package com.services.billingservice.service.placement.impl;

import com.services.billingservice.dto.placement.thread.CreateThreadDTO;
import com.services.billingservice.exception.placement.TooManyRequestException;
import com.services.billingservice.model.placement.ThreadTest;
import com.services.billingservice.repository.placement.ThreadTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThreadTestV3ServiceImpl {

    private final Semaphore semaphore = new Semaphore(1, true);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ThreadTestRepository threadTestRepository;


    public CompletableFuture<String> testThread(List<CreateThreadDTO> createThreadDTOList) {
        // try to acquire semaphore (non-blocking)
        if (!semaphore.tryAcquire()) {
            log.warn("Request rejected - system busy");
            CompletableFuture<String> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new TooManyRequestException("Request ditolak karena sistem sedang memproses request lain")
            );
            return failed;
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Simulating long process for 20 second");
                Thread.sleep(20000);

                // Process
                log.info("Processing {} requests", createThreadDTOList.size());

                List<ThreadTest> threadTestList = createThreadDTOList.stream()
                        .map(dto -> ThreadTest.builder().build())
                        .collect(Collectors.toList());

                // Batch save to database
                List<ThreadTest> savedEntity = threadTestRepository.saveAll(threadTestList);

                log.info("Successfully saved");

                return "Successfully saved entities " + savedEntity.size();
            } catch (Exception e) {
                log.error("Error processing request", e);
                throw new CompletionException(e);
            } finally {
                semaphore.release(); // Always release semaphore
                log.debug("Semaphore released");
            }
        }, executor);
    }

}
