package com.services.billingservice.controller.placement;

import com.services.billingservice.dto.placement.thread.CreateThreadDTO;
import com.services.billingservice.exception.GeneralException;
import com.services.billingservice.exception.placement.TooManyRequestException;
import com.services.billingservice.service.placement.impl.ThreadTestServiceImpl;
import com.services.billingservice.service.placement.impl.ThreadTestV3ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/api/placement/thread-test")
@RequiredArgsConstructor
@Slf4j
public class ThreadTestController {

    private final ThreadTestServiceImpl threadTestService;
    private final ThreadTestV3ServiceImpl threadTestV3Service;

    @PostMapping
    public ResponseEntity<String> threadTest(@RequestBody List<CreateThreadDTO> createThreadDTOList) {
        CompletableFuture<String> futureResult = threadTestV3Service.testThread(createThreadDTOList);
        try {
            String result = futureResult.get();
            return ResponseEntity.ok(result);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TooManyRequestException) {
                throw (TooManyRequestException) cause;
            }
            throw new GeneralException(cause.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GeneralException("Operation interrupted");
        }
    }

}
