package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricLogDto;
import ru.t1.java.demo.kafka.MetricProducer;
import ru.t1.java.demo.model.MetricErrorType;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    
    @Value("${track.time-limit-exceed}")
    private Long executionTimeLimit;

    private final MetricProducer metricProducer;

    @Around("@annotation(ru.t1.java.demo.aop.annotation.Metric)")
    public Object logExecTime(ProceedingJoinPoint pJoinPoint) throws Throwable {
        log.info("Invoke method: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage());
            throw throwable;
        }finally {
            long executionTime = System.currentTimeMillis() - beforeTime;
            log.info("Execution time: {} ms", executionTime);

            if(executionTime > executionTimeLimit) {
                MetricLogDto metricStatisticDto = MetricLogDto.builder()
                        .methodSignature(pJoinPoint.getSignature().getName())
                        .params(Arrays.toString(pJoinPoint.getArgs()))
                        .executionTime(executionTime)
                        .build();
                metricProducer.sendMetricLog(metricStatisticDto, MetricErrorType.METRICS);
            }
        }

        return result;
    }
}
