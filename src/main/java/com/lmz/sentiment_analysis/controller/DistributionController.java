package com.lmz.sentiment_analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
//This controller handles requests for displaying the distribution chart page.
//When a GET request is made to "/distribution", it returns the view name for the chart page.
public class DistributionController {
    //Handles GET requests for the distribution page.
    @GetMapping("/distribution")
    public String distributionPage() {
        // Return the view name for the distribution chart page.
        return "chart";
    }
}

