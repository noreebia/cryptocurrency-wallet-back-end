package wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
    private SimpMessageSendingOperations messagingTemplate;

	
	@RequestMapping("/sockettest/{yolo}")
	public void testWebSocket(@PathVariable String yolo) {
		messagingTemplate.convertAndSend("/topic/"+yolo, "hi!");
	}
	
	@RequestMapping("/sockettest")
	public void testWebSocket() {
		messagingTemplate.convertAndSend("/topic/deposits", "hi!");
	}
}
