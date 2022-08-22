package com.kg.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pk")
public class indexController {

    @RequestMapping("/index/")
    public Map<String, String> index(){
        Map<String, String > map1 = new HashMap<>();
        map1.put("name", "fuck");
        map1.put("sex", "male");
        return map1;
    }
}
