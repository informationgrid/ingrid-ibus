package de.ingrid.ibus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import de.ingrid.admin.elasticsearch.IPlugHeartbeatElasticsearch;
import de.ingrid.admin.elasticsearch.IndexRunnable;
import de.ingrid.admin.elasticsearch.IndexScheduler;
import de.ingrid.admin.service.CacheService;
import de.ingrid.admin.service.CommunicationService;
import de.ingrid.admin.service.PlugDescriptionService;

@SpringBootApplication
@ComponentScan(basePackages = {"de.ingrid.ibus", "de.ingrid.admin.service", "de.ingrid.admin.elasticsearch"}, excludeFilters = {
        @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=PlugDescriptionService.class),
        @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=CacheService.class),
        @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=CommunicationService.class),
        @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=IndexRunnable.class),
        @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=IndexScheduler.class),
        @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=IPlugHeartbeatElasticsearch.class)
})
public class IBusApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(IBusApplication.class, args);
	}
	
}
