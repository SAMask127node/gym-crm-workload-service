package com.example.workload.messaging;


import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


import static com.example.workload.messaging.JmsConfig.TRAINING_EVENTS_QUEUE;


@Component
@RequiredArgsConstructor
@Log4j2
public class TrainingEventListener {
    private final WorkloadService workloadService;


    @JmsListener(destination = TRAINING_EVENTS_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void onMessage(TrainingEventMessage msg,
                          @Header(name = JmsHeaders.CORRELATION_ID, required = false) String txId,
                          @Header(name = JmsHeaders.MESSAGE_ID, required = false) String messageId) {
        log.info("JMS message received: eventId={}, txId={}, mid={}, user={}", msg.eventId(), txId, messageId, msg.trainerUsername());

        var req = new WorkloadEventRequest(
                msg.trainerUsername(),
                msg.trainerFirstName(),
                msg.trainerLastName(),
                msg.isActive(),
                msg.trainingDate(),
                msg.trainingDurationMinutes(),
                msg.actionType() == TrainingEventMessage.ActionType.ADD
                        ? WorkloadEventRequest.ActionType.ADD
                        : WorkloadEventRequest.ActionType.DELETE
        );
        workloadService.applyEvent(req);
    }
}