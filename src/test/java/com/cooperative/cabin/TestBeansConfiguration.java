package com.cooperative.cabin;

import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.application.service.PricingApplicationService;
import com.cooperative.cabin.application.service.AvailabilityApplicationService;
import com.cooperative.cabin.application.service.AdminUserApplicationService;
import com.cooperative.cabin.application.service.AvailabilityBlocksAdminService;
import com.cooperative.cabin.application.service.CabinApplicationService;
import com.cooperative.cabin.application.service.AdminDashboardService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestBeansConfiguration {

    @Bean
    public ReservationApplicationService reservationApplicationService() {
        return Mockito.mock(ReservationApplicationService.class);
    }

    @Bean
    public PricingApplicationService pricingApplicationService() {
        return Mockito.mock(PricingApplicationService.class);
    }

    @Bean
    public AvailabilityApplicationService availabilityApplicationService() {
        return Mockito.mock(AvailabilityApplicationService.class);
    }

    @Bean
    public AdminUserApplicationService adminUserApplicationService() {
        return Mockito.mock(AdminUserApplicationService.class);
    }

    @Bean
    public AvailabilityBlocksAdminService availabilityBlocksAdminService() {
        return Mockito.mock(AvailabilityBlocksAdminService.class);
    }

    @Bean
    public CabinApplicationService cabinApplicationService() {
        return Mockito.mock(CabinApplicationService.class);
    }

    @Bean
    public AdminDashboardService adminDashboardService() {
        return Mockito.mock(AdminDashboardService.class);
    }
}
