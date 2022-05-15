package main;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import main.dto.DtoMessage;
import main.dto.MessageMapper;
import main.model.Message;
import main.model.MessageRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

@RestController
public class ChatController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private MessageRepository messageRepository;


  @GetMapping("/init")
  public HashMap<String, Boolean> init() {
    HashMap<String, Boolean> response = new HashMap<>();
    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
    Optional<User> userOpt = userRepository.findBySessionId(sessionId);
    response.put("result", userOpt.isPresent());
    return response;
  }

  @PostMapping("/auth")
  public HashMap<String, Boolean> auth(@RequestParam String name){
    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
    User user = new User();
    user.setName(name);
    user.setSessionId(sessionId);
    userRepository.save(user);
    HashMap<String, Boolean> response = new HashMap<>();
    response.put("result", true);
    return response;
  }

  @PostMapping("/message")
  public Map<String, Boolean> sendMessage(@RequestParam String message){
    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
    User user= userRepository.findBySessionId(sessionId).get();
    if(message.isEmpty()){
      return Map.of("result", false);
    }
    Message msg = new Message();
    msg.setDateTime(LocalDateTime.now());
    msg.setMessage(message);
    msg.setUser(user);
    messageRepository.save(msg);
    return Map.of("result", true);
  }

  @GetMapping("/message")
  public List<DtoMessage> getMessagesList(){
    return messageRepository
        .findAll(Sort.by(Sort.Direction.ASC, "dateTime"))
        .stream()
        .map(message -> MessageMapper.map(message))
        .collect(Collectors.toList());
  }

  @GetMapping("/user")
  public HashMap<Integer, String> getUsersList(){
    return null;
  }

}
