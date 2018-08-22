package sunnn.sunsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sunnn.sunsite.service.PoolService;

@Controller
@RequestMapping("/pool")
public class PoolController {

    private final PoolService poolService;

    @Autowired
    public PoolController(PoolService poolService) {
        this.poolService = poolService;
    }


}
