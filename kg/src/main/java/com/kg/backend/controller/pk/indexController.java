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
    public List<Map<String, String>> index(){
        List<Map<String, String>> list = new LinkedList<>();
        Map<String, String > map1 = new HashMap<>();
        map1.put("name", "fuck");
        map1.put("sex", "male");

        Map<String, String> map2 = new HashMap<>();
        map2.put("name", "shti");
        map2.put("sex", "female");

        list.add(map1);
        list.add(map2);
        return list;
    }
}
