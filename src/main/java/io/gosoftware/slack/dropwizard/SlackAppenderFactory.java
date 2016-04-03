package io.gosoftware.slack.dropwizard;

import java.util.Optional;
import java.util.TimeZone;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.gosoftware.slack.SlackWebhookClient;
import io.gosoftware.slack.logback.SlackAppender;

@JsonTypeName("slack")
public class SlackAppenderFactory extends AbstractAppenderFactory {

  @NotBlank
  @JsonProperty
  private String incomingWebhookUrl;

  @NotBlank
  @Pattern(regexp = "^#.*")
  @JsonProperty
  private String channel;

  @JsonProperty
  private String username;

  @Pattern(regexp = "^:.*:")
  @JsonProperty
  private String iconEmoji;

  @JsonProperty
  private String iconUrl;

  @NotNull
  private TimeZone timeZone;

  public SlackAppenderFactory() {
    timeZone = TimeZone.getTimeZone(DateTimeZone.UTC.getID());
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  public String getIconEmoji() {
    return iconEmoji;
  }

  public void setIconEmoji(String iconEmoji) {
    this.iconEmoji = iconEmoji;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public String getIncomingWebhookUrl() {
    return incomingWebhookUrl;
  }

  public void setIncomingWebhookUrl(String incomingWebhookUrl) {
    this.incomingWebhookUrl = incomingWebhookUrl;
  }

  @Override
  public Appender<ILoggingEvent> build(LoggerContext context, String applicationName,
      Layout<ILoggingEvent> layout) {

    if (layout == null) {
      layout = buildLayout(context, timeZone);
    }

    final HttpTransport transport = new NetHttpTransport();
    final HttpRequestFactory requestFactory = transport.createRequestFactory();
    final JsonFactory jsonFactory = new JacksonFactory();

    final SlackWebhookClient slack =
        new SlackWebhookClient(requestFactory, jsonFactory, incomingWebhookUrl);

    if (StringUtils.isBlank(username)) {
      username = applicationName;
    }

    final SlackAppender appender = new SlackAppender(layout, slack, Optional.ofNullable(channel),
        Optional.ofNullable(username), Optional.ofNullable(iconEmoji),
        Optional.ofNullable(iconUrl));
    appender.setContext(context);

    addThresholdFilter(appender, threshold);
    appender.stop();
    appender.start();

    return wrapAsync(appender);
  }

}
