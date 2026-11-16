package com.homeservices.service;

import com.homeservices.repository.ServiceCategoryRepository;
import com.homeservices.repository.ServiceOfferingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class MarketplaceServiceTest {
    @Mock ServiceCategoryRepository categories;
    @Mock ServiceOfferingRepository offerings;
    @InjectMocks MarketplaceService service;

    @Test
    void rejectsOverlongSearchFilterBeforeDatabaseQuery() {
        assertThrows(IllegalArgumentException.class,
                () -> service.search("A".repeat(101), null, null));
        verifyNoInteractions(offerings);
    }
}
