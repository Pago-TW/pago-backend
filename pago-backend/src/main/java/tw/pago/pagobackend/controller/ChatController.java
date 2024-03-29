package tw.pago.pagobackend.controller;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tw.pago.pagobackend.dto.ChatroomResponseDto;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.MessageResponseDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ChatService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Controller
@AllArgsConstructor
@Validated
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final ChatService chatService;
  private final ModelMapper modelMapper;
  private final UserService userService;

  @PostMapping("/send-message")
  @Deprecated
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
    System.out.println("收到的消息: " + sendMessageRequestDto);
    String senderId =  sendMessageRequestDto.getSenderId();
    User sender = userService.getUserById(senderId);


    sendMessageRequestDto.setSenderId(sender.getUserId());
    String destinationChatroomId =  sendMessageRequestDto.getChatroomId();

    Message message = chatService.createMessage(sendMessageRequestDto);

    MessageResponseDto messageResponseDto = modelMapper.map(message, MessageResponseDto.class);
    String senderName = sender.getFullName();
    messageResponseDto.setSenderName(senderName);

    messagingTemplate.convertAndSend("/chatrooms/" + destinationChatroomId + "/message", messageResponseDto);

  }


  @GetMapping("/chatrooms")
  public ResponseEntity<Object> getCharoomList(
      @RequestParam(required = false) String chatWith,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "update_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    // Get the current logged-in user's ID
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    // Get the current logged-in user object
    User currentLoginUser = currentUserInfoProvider.getCurrentLoginUser();

    // If the request is to chat with a specific user
    if (chatWith != null) {
      // Find the existing chatroom for the two users
      Optional<Chatroom> optionalChatroom = chatService.findChatroomByUserIds(currentLoginUserId, chatWith);

      // If the chatroom doesn't exist, create a new one
      Chatroom chatroom = optionalChatroom.orElseGet(() -> {
        CreateChatRoomRequestDto createChatRoomRequestDto = new CreateChatRoomRequestDto();
        System.out.println("Create a new chatroom...!");
        return chatService.createChatroom(createChatRoomRequestDto, currentLoginUserId, chatWith);
      });

      // Get chatroom details with the ChatroomResponseDto object
      ChatroomResponseDto chatroomResponseDto = chatService.getChatroomResponseDtoByChatroomAndUser(chatroom, currentLoginUser);

      return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDto);
    }

    // If the request is to get the chatroom list
    // Create a list query parameters DTO with the given parameters
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(currentLoginUserId)
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    // Get the chatroom list based on the query parameters
    List<Chatroom> chatroomList = chatService.getChatroomList(listQueryParametersDto);
    // Get the chatroom response DTO list for the chatroomList and current user
    List<ChatroomResponseDto> chatroomResponseDtoList = chatService.getChatroomResponseDtoListByChatroomListAndUser(chatroomList, currentLoginUser);

    // Count the total number of chatroomList
    Integer total = chatService.countChatroom(listQueryParametersDto);

    // Create a list response DTO with the chatroom response DTOs
    ListResponseDto<ChatroomResponseDto> chatroomListResponseDto = ListResponseDto.<ChatroomResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(chatroomResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(chatroomListResponseDto);
  }

  @GetMapping("/chatrooms/{chatroomId}/messages")
  public ResponseEntity<?> getChatHistory(
      @PathVariable String chatroomId,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "25") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "send_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort
  ) {
    // Build the query parameters DTO
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .chatroomId(chatroomId)
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    // Get the chatroom list based on the query parameters
    List<Message> messageList = chatService.getChatHistory(listQueryParametersDto);
    List<MessageResponseDto> messageResponseDtoList = chatService.getMessageResponseDtoListByMessageList(messageList);

    // Count the total number of chatroomList
    Integer total = chatService.countMessage(listQueryParametersDto);

    // Create a list response DTO with the chatroom response DTOs
    ListResponseDto<MessageResponseDto> messageResponseDtoListResponseDto = ListResponseDto.<MessageResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(messageResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(messageResponseDtoListResponseDto);
  }

  @GetMapping("/messages")
  public ResponseEntity<?> getChatHistoryByChatWithQueryParams(
      @RequestParam String chatWith,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "25") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "send_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort
  ) {

    // Get the current logged-in user's ID
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    // Get the current logged-in user object
    User currentLoginUser = currentUserInfoProvider.getCurrentLoginUser();


    // Find the existing chatroom for the two users
    Optional<Chatroom> optionalChatroom = chatService.findChatroomByUserIds(currentLoginUserId, chatWith);

    // If the chatroom doesn't exist, create a new one
    Chatroom chatroom = optionalChatroom.orElseGet(() -> {
      CreateChatRoomRequestDto createChatRoomRequestDto = new CreateChatRoomRequestDto();
      System.out.println("Create a new chatroom...!");
      return chatService.createChatroom(createChatRoomRequestDto, currentLoginUserId, chatWith);
    });

    // Get chatroom details with the ChatroomResponseDto object
    String chatroomId = chatroom.getChatroomId();


    // Build the query parameters DTO
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .chatroomId(chatroomId)
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    // Get the chatroom list based on the query parameters
    List<Message> messageList = chatService.getChatHistory(listQueryParametersDto);
    List<MessageResponseDto> messageResponseDtoList = chatService.getMessageResponseDtoListByMessageList(messageList);

    // Count the total number of chatroomList
    Integer total = chatService.countMessage(listQueryParametersDto);

    // Create a list response DTO with the chatroom response DTOs
    ListResponseDto<MessageResponseDto> messageResponseDtoListResponseDto = ListResponseDto.<MessageResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(messageResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(messageResponseDtoListResponseDto);
  }


}
