package hello;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    private final Counter counter;

    public HelloController(MeterRegistry registry) {
        this.counter = registry.counter("received.hellos");
    }

    @RequestMapping("/")
    public String index() {
        counter.count();
        return "Greetings from Spring Boot!";
    }

}
