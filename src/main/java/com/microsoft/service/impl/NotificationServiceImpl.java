package com.microsoft.service.impl;

import com.microsoft.service.NotificationService;
import com.microsoft.domain.Notification;
import com.microsoft.domain.enumeration.Channel;
import com.microsoft.repository.NotificationRepository;
import com.microsoft.service.dto.NotificationDTO;
import com.microsoft.service.mapper.NotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Service Implementation for managing Notification.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Value("${TWILIO_SID}")
    private String twilioSid;

    @Value("${TWILIO_KEY}")
    private String twilioKey;

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);

        switch( notification.getChannel() ) {
            case SMS: {
                Twilio.init(twilioSid, twilioKey);
                Message message = Message.creator(new PhoneNumber(notification.getTo()),
                    new PhoneNumber(notification.getFrom()), 
                    notification.getMsgUri()).create();

                log.debug("Sending Notification :{}", message.getSid());
                break;
            }
            case EMAIL:
            case MOBILE:
            default: {
                log.debug("Channel is not implemented yet");
            }
        }
        
        return notificationMapper.toDto(notification);
    }

    /**
     * Get all the notifications.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> findAll() {
        log.debug("Request to get all Notifications");
        return notificationRepository.findAll().stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one notification by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public NotificationDTO findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        Notification notification = notificationRepository.findOne(id);
        return notificationMapper.toDto(notification);
    }

    /**
     * Delete the notification by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        notificationRepository.delete(id);
    }
}
