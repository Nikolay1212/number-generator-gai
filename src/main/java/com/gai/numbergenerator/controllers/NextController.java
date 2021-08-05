package com.gai.numbergenerator.controllers;

import com.gai.numbergenerator.services.NumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NextController {

    @Autowired
    private NumberService numberService;

    @GetMapping("/number/next")
    public String getNextNumber(Model model) {
        String number = numberService.generateNextNumber();
        model.addAttribute("number", number);
        return "number";
    }
}
