package com.lmz.sentiment_analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DistributionController {

    @GetMapping("/distribution")
    public String distributionPage() {
        // 返回名为 "chart" 的模板（即 chart.html）
        return "chart";
    }
}

