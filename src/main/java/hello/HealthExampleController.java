package hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class HealthExampleController {

    private static final AtomicBoolean healthy = new AtomicBoolean(true);

    public static boolean check() {
        return healthy.get();
    }

    @RequestMapping(path = "/health/conf", method = RequestMethod.POST)
    public boolean healthConf() {
        //Toggle health
        healthy.set(!healthy.get());
        return healthy.get();
    }
}
