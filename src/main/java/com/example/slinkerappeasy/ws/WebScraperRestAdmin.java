package com.example.slinkerappeasy.ws;


import com.example.slinkerappeasy.Bean.Result;
import com.example.slinkerappeasy.Service.impl.admin.WebScraperSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/admin/webscraper/")
@RestController
public class WebScraperRestAdmin {

    @GetMapping("scrapeLinksWebsite/{id}")
    public List<String> scrapeLinksWebsite(@PathVariable Long id) {
        return webScraperSevice.scrapeLinksWebsite(id);
    }

    @GetMapping("scrapeAndFilterCustomAmazonLinks/{id}")
    public List<String> scrapeAndFilterCustomAmazonLinks(@PathVariable Long id,@RequestParam int nbrPage) {
        return webScraperSevice.scrapeAndFilterCustomAmazonLinks(id, nbrPage);
    }
//"/users/123?nbrPage=5".
    @GetMapping("linkChecked/{id}")
    public int linkChecked(@PathVariable Long id) {
        return webScraperSevice.linkChecked(id);
    }
    @GetMapping("CustomScrapeLinks/{id}")
    public List<String> CustomScrapeLinks(@PathVariable Long id,@RequestParam int numberOfLinks) {
        return webScraperSevice.CustomScrapeLinks(id, numberOfLinks);
    }

    @GetMapping("scrapeAndFilterAmazonLinks/{id}")
    public List<String> scrapeAndFilterAmazonLinks(@PathVariable Long id) {
        return webScraperSevice.scrapeAndFilterAmazonLinks(id);
    }
//    @GetMapping("id/{id}")
//    public List<Result> AmazonProducts(@PathVariable Long id) {
//        return webScraperSevice.AmazonProducts(id);
//    }
//    @GetMapping("AmazonCustomProducts/{id}")
//    public List<Result> AmazonCustomProducts(@PathVariable Long id,@RequestParam int nbrPage) {
//        return webScraperSevice.AmazonCustomProducts(id, nbrPage);
//    }

    @GetMapping("id/{id}")
    public List<Result> AmazonProducts(@PathVariable Long id) {
        return webScraperSevice.AmazonProducts(id);
    }


    @Autowired
    private WebScraperSevice webScraperSevice;
}
