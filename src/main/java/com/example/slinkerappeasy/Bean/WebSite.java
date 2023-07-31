package com.example.slinkerappeasy.Bean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity
public class WebSite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 500)
    private String url;
    @Column(length = 500)
    private String libelle;
    @Column(length = 500)
    private String jsonSummary;
    @ManyToOne
    private StatutWebSite statutWebSite;
    @OneToMany()
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Client> clients;
    private int linkChecked;
    private int linkProcessed;
    private int avaibale;
    private int unvaibale;
    private long duree;

    public int getUnvaibale() {
        return unvaibale;
    }

    public void setUnvaibale(int unvaibale) {
        this.unvaibale = unvaibale;
    }

    public int getAvaibale() {
        return avaibale;
    }

    public void setAvaibale(int avaibale) {
        this.avaibale = avaibale;
    }


    public long getDuree() {
        return duree;
    }

    public void setDuree(long duree) {
        this.duree = duree;
    }

    public int getLinkProcessed() {
        return linkProcessed;
    }

    public void setLinkProcessed(int linkProcessed) {
        this.linkProcessed = linkProcessed;
    }


    public int getLinkChecked() {
        return linkChecked;
    }

    public void setLinkChecked(int linkChecked) {
        this.linkChecked = linkChecked;
    }

    @ManyToOne
    private ScrappingLink scrappingLinks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getJsonSummary() {
        return jsonSummary;
    }

    public void setJsonSummary(String jsonSummary) {
        this.jsonSummary = jsonSummary;
    }

    public StatutWebSite getStatutWebSite() {
        return statutWebSite;
    }

    public void setStatutWebSite(StatutWebSite statutWebSite) {
        this.statutWebSite = statutWebSite;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public ScrappingLink getScrappingLinks() {
        return scrappingLinks;
    }

    public void setScrappingLinks(ScrappingLink scrappingLinks) {
        this.scrappingLinks = scrappingLinks;
    }
}

