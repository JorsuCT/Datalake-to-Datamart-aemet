package main;

import java.time.LocalDateTime;

public class Controller {
    public void run(String apikey, String table_1, String table_2){
        while (true){
            DataCrawler crawlwer = new DataCrawler(apikey);
            crawlwer.crawl();
            LocalDateTime delta = LocalDateTime.now();
            DataMart_create datamart_max = new DataMart_create();
            datamart_max.run(table_1);
            DataMart_create datamart_min = new DataMart_create();
            datamart_min.run(table_2);
            delta = delta.plusHours(1);
            while (delta.isAfter(LocalDateTime.now()));
        }
    }
}
