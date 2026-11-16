package com.homeservices.service;

import com.homeservices.entity.ServiceCategory;
import com.homeservices.entity.ServiceOffering;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.exception.ResourceNotFoundException;
import com.homeservices.repository.ServiceCategoryRepository;
import com.homeservices.repository.ServiceOfferingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class MarketplaceService {
    private final ServiceCategoryRepository categories;
    private final ServiceOfferingRepository offerings;

    public MarketplaceService(ServiceCategoryRepository categories, ServiceOfferingRepository offerings) {
        this.categories = categories;
        this.offerings = offerings;
    }

    public List<ServiceCategory> getCategories() {
        return categories.findByActiveTrueOrderByNameAsc();
    }

    public List<ServiceOffering> search(String city, String category, String query) {
        validateFilterLength(city, "City");
        validateFilterLength(category, "Category");
        validateFilterLength(query, "Search text");
        String cityFilter = normalize(city);
        String categoryFilter = normalize(category);
        String textFilter = normalize(query);

        return offerings.findApprovedActiveServices(ProviderStatus.APPROVED).stream()
                .filter(service -> cityFilter == null || contains(service.getProvider().getCity(), cityFilter))
                .filter(service -> categoryFilter == null || equalsIgnoreCase(service.getCategory().getName(), categoryFilter))
                .filter(service -> textFilter == null
                        || contains(service.getTitle(), textFilter)
                        || contains(service.getProvider().getBusinessName(), textFilter)
                        || contains(service.getCategory().getName(), textFilter))
                .toList();
    }

    public ServiceOffering getPublicService(Long id) {
        return offerings.findPublicById(id, ProviderStatus.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));
    }

    private void validateFilterLength(String value, String label) {
        if (value != null && value.length() > 100) {
            throw new IllegalArgumentException(label + " filter must be 100 characters or fewer.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean contains(String value, String filter) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(filter);
    }

    private boolean equalsIgnoreCase(String value, String filter) {
        return value != null && value.toLowerCase(Locale.ROOT).equals(filter);
    }
}
