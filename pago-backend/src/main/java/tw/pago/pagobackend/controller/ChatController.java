package tw.pago.pagobackend.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.JoinRoomRequest;
import tw.pago.pagobackend.dto.MessageResponseDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ChatService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Controller
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

    System.out.println("Frontend send Message");
    User sender = User.builder().build();
    sender.setUserId("cc0fc75a5a854b1e9980d4acbe82086d");
    sender.setFirstName("LILY ");
    sender.setLastName("LILY");
    sender.setEmail("a0923183408@gmail.com");
    sender.setAvatarUrl("https://lh3.googleusercontent.com/a/AGNmyxYCn5ZTzVOK_r0TIN829tKCiI1zxd7e84okgIpI_TA=s96-c");


    sendMessageRequestDto.setSenderId(sender.getUserId());
    sendMessageRequestDto.setChatRoomId("a10fc75a5a854b1e9980d4aaaa82086d");

    Message message = chatService.createMessage(sendMessageRequestDto);

    MessageResponseDto messageResponseDto = modelMapper.map(message, MessageResponseDto.class);
    String senderName = sender.getFullName();
    messageResponseDto.setSenderName(senderName);

    messagingTemplate.convertAndSend("/chatroom/message", messageResponseDto);

  }

  @SendTo("/chatroom/message")
  public MessageResponseDto broadcastMessage(@Payload MessageResponseDto messageResponseDto) {

//    System.out.println("Broadcast Message!");

    return messageResponseDto;
  }

  @MessageMapping("/join")
  @SendTo("/topic/{roomName}/join")
  public String joinRoom(@Payload JoinRoomRequest JoinRoomRequest, @DestinationVariable String roomName) {
    // ... 處理加入聊天室的邏輯，如保存用戶信息等
//    User currentLoginUser = currentUserInfoProvider.getCurrentLoginUser();
//    String currentLoginUserName = currentLoginUser.getFullName();
    System.out.println("Join");


    return JoinRoomRequest.getUserName() + " 加入了 " + roomName + " 聊天室";
  }


}
