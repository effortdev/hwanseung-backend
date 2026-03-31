package com.hwanseung.backend.domain.chat.config;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChatBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. Job (배치 작업의 단위)
    @Bean
    public Job chatBackupJob() {
        return new JobBuilder("chatBackupJob", jobRepository)
                .start(chatBackupStep())
                .build();
    }

    // 2. Step (읽기 -> 가공 -> 쓰기)
    @Bean
    public Step chatBackupStep() {
        return new StepBuilder("chatBackupStep", jobRepository)
                // 100개씩 묶어서 처리하겠다는 뜻입니다 (Chunk 크기)
                .<String, ChatMessage>chunk(100, transactionManager)
                .reader(redisItemReader())
                .processor(chatProcessor())
                .writer(mysqlItemWriter())
                .build();
    }

    // 3. Reader: Redis의 'chat_messages_buffer'에서 하나씩 쏙쏙 빼옵니다 (Left Pop)
    @Bean
    public ItemReader<String> redisItemReader() {
        return () -> {
            Object item = redisTemplate.opsForList().leftPop("chat_messages_buffer");
            return item != null ? (String) item : null;
        };
    }

    // 4. Processor: Redis에서 꺼낸 JSON 문자열을 ChatMessage 엔티티로 예쁘게 변환합니다.
    @Bean
    public ItemProcessor<String, ChatMessage> chatProcessor() {
        return item -> {
            JsonNode jsonNode = objectMapper.readTree(item);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(jsonNode.get("roomId").asText());
            chatMessage.setSenderId(jsonNode.get("sender").asText());
            chatMessage.setContent(jsonNode.get("content").asText());
            return chatMessage;
        };
    }

    // 5. Writer: 변환된 엔티티 100개를 모아서 MySQL에 한 방에(saveAll) 쾅! 저장합니다.
    @Bean
    public ItemWriter<ChatMessage> mysqlItemWriter() {
        return chunk -> {
            chatMessageRepository.saveAll(chunk.getItems());
            log.info("채팅 메시지 {}건 한 방에 DB 저장 완료!", chunk.getItems().size());
        };
    }
}
