package com.gai.numbergenerator.controllers;

import com.gai.numbergenerator.services.NumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RandomController {

    @Autowired
    private NumberService numberService;

    @GetMapping("/number/random")
    public String getRandomNumber(Model model) {
        String number = numberService.generateRandomNumber();
        model.addAttribute("number", number);
        return "number";
    }
}
