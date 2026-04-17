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

    @Bean
    public Job chatBackupJob() {
        return new JobBuilder("chatBackupJob", jobRepository)
                .start(chatBackupStep())
                .build();
    }

    @Bean
    public Step chatBackupStep() {
        return new StepBuilder("chatBackupStep", jobRepository)
                .<String, ChatMessage>chunk(100, transactionManager)
                .reader(redisItemReader())
                .processor(chatProcessor())
                .writer(mysqlItemWriter())
                .build();
    }

    @Bean
    public ItemReader<String> redisItemReader() {
        return () -> {
            Object item = redisTemplate.opsForList().leftPop("chat_messages_buffer");
            return item != null ? (String) item : null;
        };
    }

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

    @Bean
    public ItemWriter<ChatMessage> mysqlItemWriter() {
        return chunk -> {
            chatMessageRepository.saveAll(chunk.getItems());
            log.info("채팅 메시지 {}건 한 방에 DB 저장 완료!", chunk.getItems().size());
        };
    }
}
