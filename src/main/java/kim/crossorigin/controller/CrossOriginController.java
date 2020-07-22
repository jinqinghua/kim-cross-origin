package kim.crossorigin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cors")
@Slf4j
public class CrossOriginController {

    @GetMapping({"", "/", "/index"})
    public String index() {
        return "/index";
    }

    //@CrossOrigin
    @GetMapping("/json")
    @ResponseBody
    public Map<String, Object> json() {
        Map<String, Object> map = new HashMap<>();
        map.put("objective", "test cors");
        return map;
    }

}
