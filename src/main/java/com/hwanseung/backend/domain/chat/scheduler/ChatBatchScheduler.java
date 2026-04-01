package com.hwanseung.backend.domain.chat.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job chatBackupJob;

    // cron: 초 분 시 일 월 요일 -> "0 * * * * *" 은 '매 분 0초마다' 실행하라는 뜻!
    @Scheduled(cron = "0 * * * * *")
    public void runChatBackupJob() {
        try {
            log.info("⏰ 1분 경과! 채팅 백업 배치를 시작합니다...");

            // 배치는 파라미터가 달라야 매번 새로운 작업으로 인식합니다.
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(chatBackupJob, parameters);

        } catch (Exception e) {
            log.error("배치 실행 중 에러 발생: {}", e.getMessage());
        }
    }
}
