package com.example.slinkerappeasy.Service.impl.admin;


import com.example.slinkerappeasy.Bean.Result;
import com.example.slinkerappeasy.Bean.WebSite;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebScraperSevice {
    private Set<String> visitedLinks;
    private List<String> extractedLinks;
    private static final int THREAD_POOL_SIZE = 10;

    public List<String> scrapeLinksWebsite(Long id) {
        WebSite webSite = webSiteAdminService.findById(id);
        extractedLinks = new ArrayList<>();
        visitedLinks = new HashSet<>();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            scrapePage(webSite.getUrl(), executor);
            executor.shutdown();
            while (!executor.isTerminated()) {
                // Attente de la fin de tous les threads
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception selon vos besoins
        }
        return extractedLinks;
    }

    public List<String> CustomScrapeLinks(Long id, int numberOfLinks) {
        List<String> links = new ArrayList<>();
        WebSite webSite = webSiteAdminService.findById(id);

        String url = webSite.getUrl(); // Construisez l'URL avec l'ID en tant que paramètre
        try {
            Document document = Jsoup.connect(url).get(); // Récupérez le document HTML de la page
            Elements anchorTags = document.select("a[href]"); // Sélectionnez tous les éléments <a> contenant un attribut href

            int count = 0;
            for (Element anchorTag : anchorTags) {
                if (count >= numberOfLinks) {
                    break;
                }

                String subLink = anchorTag.attr("abs:href"); // Récupérez l'URL absolue du sous-lien
                links.add(subLink);

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return links;
    }


    public int linkChecked(Long id) {
        List<String> links = scrapeLinksWebsite(id);
        int linkschecked = links.size();
        return linkschecked;
    }

    private void scrapePage(String pageUrl, ExecutorService executor) throws IOException {
        if (!visitedLinks.contains(pageUrl)) {
            visitedLinks.add(pageUrl);
            Document document = Jsoup.connect(pageUrl).get();
            Elements links = document.select("a[href]");

            List<String> batchLinks = new ArrayList<>();

            for (Element link : links) {
                String href = link.absUrl("href");
                if (isValidLink(href) && !visitedLinks.contains(href)) {
                    batchLinks.add(href);
                }
            }

            synchronized (extractedLinks) {
                extractedLinks.addAll(batchLinks);
            }

            for (String link : batchLinks) {
                executor.execute(() -> {
                    try {
                        scrapePage(link, executor);
                    } catch (IOException e) {
                        // Gérer l'exception
                    }
                });
            }
        }
    }

    // Reste du code inchangé

    public List<String> scrapeAndFilterAmazonLinks(Long id) {
        List<String> links = scrapeLinksWebsite(id);
        Set<String> visitedLinks = new HashSet<>(); // Ensemble des liens visités
        List<String> amazonLinks = new ArrayList<>(); // Ensemble des liens Amazon identifiés

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (String link : links) {
            if (visitedLinks.contains(link)) {
                continue; // Si le lien a déjà été visité, passer à l'itération suivante
            }
            visitedLinks.add(link); // Ajouter le lien visité à l'ensemble des liens visités

            executor.execute(() -> {
                try {
                    Connection connection = Jsoup.connect(link);
                    Document document = connection.get();
                    Elements pageLinks = document.select("a[href]"); // Récupérer tous les liens de la page

                    List<String> batchLinks = new ArrayList<>();

                    for (Element pageLink : pageLinks) {
                        String linkUrl = pageLink.absUrl("href");
                        if (linkUrl.isEmpty() || visitedLinks.contains(linkUrl)) {
                            continue; // Si le lien est vide ou a déjà été visité, passer à l'itération suivante
                        }
                        if (linkUrl.contains("amazon") || linkUrl.contains("amzn")) {
                            batchLinks.add(linkUrl); // Si le lien pointe vers Amazon, l'ajouter à l'ensemble des liens Amazon identifiés
                        }
                    }

                    synchronized (amazonLinks) {
                        amazonLinks.addAll(batchLinks);
                    }
                } catch (IOException e) {
                    // Gérer l'exception
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Attente de la fin de tous les threads
        }

        return amazonLinks; // Convertir l'ensemble des liens Amazon identifiés en liste et la renvoyer
    }

    public List<String> scrapeAndFilterCustomAmazonLinks(Long id, int numberOfLinks) {
        List<String> customlinks = CustomScrapeLinks(id, numberOfLinks);
        HashSet<String> visitedCustomLinks = new HashSet<>();
        List<String> CustomamazonLinks = new ArrayList<>(); // Ensemble des liens Amazon identifiés
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        for (String link : customlinks) {
            if (visitedCustomLinks.contains(link)) {
                continue; // Si le lien a déjà été visité, passer à l'itération suivante
            }
            visitedCustomLinks.add(link); // Ajouter le lien visité à l'ensemble des liens visités
            executor.execute(() -> {
                try {
                    Connection connection = Jsoup.connect(link);
                    Document document = connection.get();
                    Elements pageLinks = document.select("a[href]"); // Récupérer tous les liens de la page
                    List<String> batchCustomLinks = new ArrayList<>();
                    for (Element CustomPageLink : pageLinks) {
                        String linkUrl = CustomPageLink.absUrl("href");
                        if (linkUrl.isEmpty() || visitedCustomLinks.contains(linkUrl)) {
                            continue; // Si le lien est vide ou a déjà été visité, passer à l'itération suivante
                        }
                        if (linkUrl.contains("amazon") || linkUrl.contains("amzn")) {
                            batchCustomLinks.add(linkUrl); // Si le lien pointe vers Amazon, l'ajouter à l'ensemble des liens Amazon identifiés
                        }
                    }
                    synchronized (CustomamazonLinks) {
                        CustomamazonLinks.addAll(batchCustomLinks);
                    }
                } catch (IOException e) {
                    // Gérer l'exception
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Attente de la fin de tous les threads
        }

        return CustomamazonLinks; // Convertir l'ensemble des liens Amazon identifiés en liste et la renvoyer
    }

    // Amazon products
    //    public List<Result> AmazonProducts(Long id) {
//        List<String> amazonLinks = scrapeAndFilterAmazonLinks(id);
//        List<Result> items = new ArrayList<>(); // Liste des objets ScrapingOperationItem
//        WebSite webSite = webSiteAdminService.findById(id);
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//
//        for (String link : amazonLinks) {
//            executor.execute(() -> {
//                try {
//                    Connection connection = Jsoup.connect(link);
//                    Document document = connection.get();
//                    Element product = document.selectFirst("#ppd");
//                    String title = product.select("#productTitle").text(); // Récupérer le titre du produit
//                    String stock = product.select("#availability").text(); // Récupérer le stock du produit
//                    String rating = product.select(".a-icon-alt").text(); // Récupérer le rating du produit
//                    String imageUrl = product.select("img").attr("src");
//                    String prix = product.select("div#corePrice_feature_div span.a-price-whole").text();
//                    BigDecimal Prixdecimal = new BigDecimal(prix);
//                    Result item = new Result();// Créer un objet ScrapingOperationItem avec les informations récupérées
//                    item.setDescription(title);
//                    item.setStock(stock);
//                    item.setReview(rating);
//                    item.setImage(imageUrl);
//                    item.setPrix(Prixdecimal );
//                    synchronized (items) {
//                        items.add(item);
//                    }
//                    /*int availbaleNbr= 0 ;
//                    int unavailbaleNbr= 0 ;
//                    for (Result result : items){
//                        if (result.getStock().contains("in Stock")){
//                            availbaleNbr++;
//                        }
//                        else if (result.getStock().contains("unvaibale")){
//                            unavailbaleNbr++;
//                        }
//                        else {
//
//                        }
//                    }
//                    int produitScrapper = items.size();
//                    long duree = executerEtChronometrer(() -> {
//                        // Appeler la méthode que je souhaite chronométrer ici
//                        AmazonProducts(id);
//                    });
//                //affectation des attribues au website
//                    webSite.setLinkProcessed(produitScrapper);
//                    webSite.setDuree(duree);*/
//                } catch (IOException e) {
//                    // Gérer l'exception
//                }
//            });
//        }
//
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//            // Attente de la fin de tous les threads
//        }
//
//        return  items; // Renvoyer la liste des objets ScrapingOperationItem
//    }

    public List<Result> AmazonProducts(Long id) {
        List<String> amazonLinks = scrapeAndFilterAmazonLinks(id);
        List<Result> items = new ArrayList<>(); // Liste des objets ScrapingOperationItem
        WebSite webSite = webSiteAdminService.findById(id);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (String link : amazonLinks) {
            executor.execute(() -> {
                try {
                    Connection connection = Jsoup.connect(link);
                    Document document = connection.get();
                    Element product = document.selectFirst("#ppd");
                    String title = product.select("#productTitle").text(); // Récupérer le titre du produit
                    String stock = product.select("#availability").text(); // Récupérer le stock du produit
                    String rating = product.select(".a-icon-alt").text(); // Récupérer le rating du produit
                    String imageUrl = product.select("img").attr("src");
                    String prix = product.select("div#corePrice_feature_div span.a-price-whole").text();
                    BigDecimal Prixdecimal = new BigDecimal(prix);
                    Result item = new Result();// Créer un objet ScrapingOperationItem avec les informations récupérées
                    item.setDescription(title);
                    item.setStock(stock);
                    item.setReview(rating);
                    item.setImage(imageUrl);
                    item.setPrix(Prixdecimal);
                    synchronized (items) {
                        items.add(item);
                    }
                } catch (IOException e) {
                    // Gérer l'exception
                }
            });
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
            // Attente de la fin de tous les threads
        }

        webSite.setLinkChecked(linkChecked(id));

        System.out.println("Finish");
        return items; // Renvoyer la liste des objets ScrapingOperationItem
    }

    public List<Result> AmazonCustomProducts(Long id, int nbrPage) {
        List<String> amazonLinks = scrapeAndFilterCustomAmazonLinks(id, nbrPage);
        List<Result> items = new ArrayList<>(); // Liste des objets ScrapingOperationItem
        WebSite webSite = webSiteAdminService.findById(id);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (String link : amazonLinks) {
            executor.execute(() -> {
                try {
                    Connection connection = Jsoup.connect(link);
                    Document document = connection.get();
                    Element product = document.selectFirst("#ppd");
                    String title = product.select("#productTitle").text(); // Récupérer le titre du produit
                    String stock = product.select("#availability").text(); // Récupérer le stock du produit
                    String rating = product.select(".a-icon-alt").text(); // Récupérer le rating du produit
                    String imageUrl = product.select("img").attr("src");
                    String prix = product.select("div#corePrice_feature_div span.a-price-whole").text();
                    BigDecimal Prixdecimal = new BigDecimal(prix);
                    Result item = new Result();// Créer un objet ScrapingOperationItem avec les informations récupérées
                    item.setDescription(title);
                    item.setStock(stock);
                    item.setReview(rating);
                    item.setImage(imageUrl);
                    item.setPrix(Prixdecimal);
                    synchronized (items) {
                        items.add(item);
                    }
                   /* int availbaleNbr= 0 ;
                    int unavailbaleNbr= 0 ;
                    for (Result result : items){
                        if (result.getStock().contains("in Stock")){
                            availbaleNbr++;
                        }
                        else if (result.getStock().contains("unvaibale")){
                            unavailbaleNbr++;
                        }
                        else {

                        }
                    }
                    int produitScrapper = items.size();
                    long duree = executerEtChronometrer(() -> {
                        // Appeler la méthode que je souhaite chronométrer ici
                        AmazonProducts(id);
                    });
                    //affectation des attribues au website
                    webSite.setLinkProcessed(produitScrapper);
                    webSite.setDuree(duree);*/
                } catch (IOException e) {
                    // Gérer l'exception
                }
            });
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
            // Attente de la fin de tous les threads
        }

        System.out.println("Finish");
        return items; // Renvoyer la liste des objets ScrapingOperationItem
    }

    private boolean isValidLink(String link) {
        // Ajoutez ici des conditions supplémentaires pour ignorer les liens erronés
        return link != null && !link.isEmpty() && !link.startsWith("mailto:") && !link.contains("pinterest") && !link.contains("instagram") && !link.contains("youtube") && !link.contains("tiktok") && !link.contains("twiter") && !link.contains("facebook") && !link.contains("menu");
    }

    public long executerEtChronometrer(Runnable AmazonProducts) {
        long debut = System.nanoTime();

        // Appeler la méthode que vous souhaitez chronométrer
        AmazonProducts.run();

        long fin = System.nanoTime();
        long duree = fin - debut;
        return duree;

    }

    @Autowired
    private WebSiteAdminServiceImpl webSiteAdminService;
}