package com.example.taskmanager.consumer;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.BatchOutbox;
import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.TaskStatusEnum;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;


@Service
public class TaskMessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(TaskMessageConsumer.class);

	private final TaskService taskService;
	
	private final ObjectMapper objectMapper;

	@Autowired
	public TaskMessageConsumer(TaskService taskService, ObjectMapper objectMapper) {
		this.taskService = taskService;
		this.objectMapper = objectMapper;
	}

	/**
	 * 監聽 taskCreated 訊息佇列 使用 containerFactory =
	 * "rabbitListenerContainerFactory"，代表使用手動確認模式（MANUAL） 若處理成功會手動 ack，若處理失敗則手動
	 * nack，訊息會被送往 DLQ
	 * 
	 * ackMode="MANUAL" 在這裡是裝飾的，因已由 containerFactory 指定，保留可以更加易讀
	 */
	@RabbitListener(queues = "${rabbitmq.queue.task.created}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskCreatedMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		processMessage(event, channel, message);
	}

	@RabbitListener(queues = "${rabbitmq.queue.task.assigned}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskAssignedMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		processMessage(event, channel, message);
	}

	@RabbitListener(queues = "${rabbitmq.queue.task.overdue}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskOverdueMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		processMessage(event, channel, message);
	}
	
	@RabbitListener(queues = "${rabbitmq.queue.task.batch.created}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskBatchMessage(BatchOutbox batchOutbox, Channel channel, Message message) throws IOException {
        processBatchTask(batchOutbox, channel, message);
    }

	private void processMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		// 訊息的唯一編號
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		try {
			log.info("Received event: {}", event);

			switch (event.getEventType()) {
			case TASK_CREATED -> taskService.updateTaskStatus(event.getEntityId(), TaskStatusEnum.PROCESSINGS);
			case TASK_ASSIGNED -> log.info("Event Type : TASK_ASSIGNED");
			case TASK_OVERDUE -> log.info("Event Type : TASK_OVERDUE");
			default -> log.warn("Unhandled event type: {}", event.getEventType());
			}

			// 手動確認訊息處理成功（ACK）
			// deliveryTag：確認哪一筆訊息
			// multiple = false：只確認這一筆訊息（不批量確認）
			channel.basicAck(deliveryTag, false);

		} catch (Exception e) {
			log.error("Failed to process event: {}", event, e);
			
			// 手動拒絕訊息處理（NACK）
			// multiple = false：只拒絕這一筆
			// requeue = false：不重新放回原本隊列，會送去 DLQ
			channel.basicNack(deliveryTag, false, false);
		}
	}
	
	/*
	 * 從 RabbitMQ 接收批次任務資料（JSON 格式）
	 * 將 payload 轉換成 List<Task>
	 * 成功則手動 ack，失敗則 nack 並重新入隊
	 * */
	private void processBatchTask(BatchOutbox batchOutbox, Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("Received batch outbox: {} bytes, first title: {}", batchOutbox.getPayloadSize(), batchOutbox.getFirstTitle());
            
            if (batchOutbox.getPayload() == null || batchOutbox.getPayload().isEmpty()) {
                throw new IllegalArgumentException("BatchOutbox payload is empty");
            }
            
            // new TypeReference<List<Task>>() {} 是 Jackson 反序列化時用來保留泛型型別資訊的匿名子類別寫法
            // Java 的泛型在編譯後會發生「型別擦除 (Type Erasure)」，導致執行期無法知道 List 裡的實際元素型別
            // 例如如果直接寫 new TypeReference<List<Task>>()，在執行期只剩下 List，無法得知裡面是 Task
            // TypeReference 是一個抽象類別，內部會透過反射取得子類別宣告的泛型資訊：
            //     this.type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            // 因此當我們用匿名子類別 new TypeReference<List<Task>>() {} 時
            // 編譯器會為這個匿名類別生成一個真實 class，並在其中保留 List<Task> 這個泛型資訊
            // 這樣 ObjectMapper 就能從反射拿到正確的目標型別，將 JSON 轉成 List<Task>，而不是 List<Map>
            List<Task> tasks = objectMapper.readValue(batchOutbox.getPayload(), new TypeReference<List<Task>>() {});
            
            taskService.batchInsert(tasks, batchOutbox.getCreatedBy());
            channel.basicAck(deliveryTag, false);
            log.info("BatchOutbox processed successfully, {} tasks inserted", tasks.size());
        } catch (Exception e) {
            log.error("Failed to process batch outbox: {}", batchOutbox, e);
            channel.basicNack(deliveryTag, false, true); // 失敗重新入隊
        }
    }
}