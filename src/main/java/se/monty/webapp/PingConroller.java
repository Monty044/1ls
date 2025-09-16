package se.monty.webapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PingController {
    @GetMapping("/")
    String root() {
        return "OK";
    }
}
