package tw.pago.pagobackend.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.MessageResponseDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ChatService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@AllArgsConstructor
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final ChatService chatService;
  private final ModelMapper modelMapper;
  private final UserService userService;

  @PostMapping("/send-message")
  public ResponseEntity<Void> sendMessage(@RequestBody SendMessageRequestDto sendMessageRequestDto) {
    User sender = currentUserInfoProvider.getCurrentLoginUser();
    sendMessageRequestDto.setSenderId(sender.getUserId());

    Message message = chatService.createMessage(sendMessageRequestDto);

    MessageResponseDto messageResponseDto = modelMapper.map(message, MessageResponseDto.class);
    String senderName = sender.getFullName();
    messageResponseDto.setSenderName(senderName);

    messagingTemplate.convertAndSend("/topic/message", messageResponseDto);



    return ResponseEntity.status(HttpStatus.OK).build();

  }

  @MessageMapping("/send-message")
  public void receiveMessage(@Payload SendMessageRequestDto sendMessageRequestDto) {

  }

  @SendTo("/topic/message")
  public MessageResponseDto broadcastMessage(@Payload MessageResponseDto messageResponseDto) {

    return messageResponseDto;
  }


}
