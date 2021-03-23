package it.sweven.blockcovid.configurations;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationConfiguration {
  @Bean
  GroupedOpenApi userApis() {
    return GroupedOpenApi.builder().group("User operations").pathsToMatch("/**/user/**").build();
  }

  @Bean
  GroupedOpenApi adminApis() {
    return GroupedOpenApi.builder().group("Admin operations").pathsToMatch("/**/admin/**").build();
  }

  @Bean
  GroupedOpenApi roomApis() {
    return GroupedOpenApi.builder().group("Room operations").pathsToMatch("/**/room/**").build();
  }

  @Bean
  public GroupedOpenApi AllApis() {
    return GroupedOpenApi.builder().group("All operations").pathsToMatch("/**").build();
  }
}
