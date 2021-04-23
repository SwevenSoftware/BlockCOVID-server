package it.sweven.blockcovid.configurations;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;

@Configuration
public class LocalDateTimeConfiguration {
  @Bean
  public Formatter<LocalDateTime> localDateTimeFormatter() {
    return new Formatter<>() {
      private final String PATTERN = "yyyy-MM-dd'T'HH:mm";

      @Override
      public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(PATTERN));
      }

      @Override
      public String print(LocalDateTime object, Locale locale) {
        return DateTimeFormatter.ofPattern(PATTERN).format(object);
      }
    };
  }
}
