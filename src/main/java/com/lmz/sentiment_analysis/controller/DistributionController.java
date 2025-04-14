package com.lmz.sentiment_analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DistributionController {

    @GetMapping("/distribution")
    public String distributionPage() {

        return "chart";
    }
}

