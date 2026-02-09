package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository pr;

    @Autowired
    NotificationService ns;

    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        pr.save(p);
        ns.sendDelayNotification(leadTime, p.getName());
    }

    public void handleSeasonalProduct(Product p) {
        LocalDate today = LocalDate.now();

        // Règle métier : On est dans la période de vente
        boolean isWithinSeason = !today.isBefore(p.getSeasonStartDate()) && !today.isAfter(p.getSeasonEndDate());

        // Cas nominal : En saison et en stock
        if (isWithinSeason && p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
            return;
        }

        // Cas de rupture ou hors saison : calcul du réapprovisionnement
        LocalDate arrivalDate = today.plusDays(p.getLeadTime());
        boolean willArriveAfterSeason = arrivalDate.isAfter(p.getSeasonEndDate());

        if (willArriveAfterSeason) {
            ns.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            pr.save(p);
        } else {
            notifyDelay(p.getLeadTime(), p);
        }
    }

    public void handleExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            ns.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
            pr.save(p);
        }
    }

    public void processProduct(Product p) {
        if (p == null || p.getType() == null) return;
        switch (p.getType()) {
            case "NORMAL" -> handleNormalProduct(p);
            case "SEASONAL" -> handleSeasonalProduct(p);
            case "EXPIRABLE" -> handleExpiredProduct(p);
            default -> System.out.println("Type inconnu : " + p.getType());
        }
    }

    private void handleNormalProduct(Product p) {
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else if (p.getLeadTime() > 0) {
            notifyDelay(p.getLeadTime(), p);
        }
    }
}