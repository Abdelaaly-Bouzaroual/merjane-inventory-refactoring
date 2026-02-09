package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks 
    private ProductService productService;

    @Test
    public void test() {
        // GIVEN
        Product product =new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product);

        // THEN
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }


    @Test
    public void handleSeasonalProduct_WhenReorderIsAfterSeason_ShouldNotifyOutOfStock() {
        // GIVEN : Un produit qui n'est plus en stock
        // Fin de saison dans 5 jours, mais délai de réappro de 10 jours
        LocalDate seasonEnd = LocalDate.now().plusDays(5);
        Product product = new Product();
        product.setName("Pastèque");
        product.setType("SEASONAL");
        product.setAvailable(0);
        product.setLeadTime(10);
        product.setSeasonStartDate(LocalDate.now().minusDays(10));
        product.setSeasonEndDate(seasonEnd);

        // WHEN
        productService.handleSeasonalProduct(product);

        // THEN : Doit être marqué indisponible et notifier le client
        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService).sendOutOfStockNotification("Pastèque");
        Mockito.verify(productRepository).save(product);
    }
}