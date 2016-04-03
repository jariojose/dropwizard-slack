package io.gosoftware.slack.logback;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.slf4j.MDC;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.annotations.VisibleForTesting;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import io.gosoftware.slack.SlackWebhookClient;
import io.gosoftware.slack.api.SlackMessage;

public class SlackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private static final String APPENDER_NAME = "slack-appender";

  public static final String PREFIX = "slack";

  private static final Joiner JOINER = Joiner.on('.');

  private final SlackWebhookClient slack;

  private final Layout<ILoggingEvent> layout;

  private final Optional<String> defaultChannel;

  private final Optional<String> defaultUsername;

  private final Optional<String> defaultIconUrl;

  private final Optional<String> defaultIconEmoji;

  public SlackAppender(Layout<ILoggingEvent> layout, SlackWebhookClient slack) {
    this(layout, slack, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  public SlackAppender(Layout<ILoggingEvent> layout, SlackWebhookClient slack,
      Optional<String> defaultIconChannel, Optional<String> defaultUsername,
      Optional<String> defaultIconEmoji, Optional<String> defaultIconUrl) {
    this.layout = checkNotNull(layout);
    this.slack = checkNotNull(slack);
    this.defaultChannel = checkNotNull(defaultIconChannel);
    this.defaultUsername = checkNotNull(defaultUsername);
    this.defaultIconUrl = checkNotNull(defaultIconUrl);
    this.defaultIconEmoji = checkNotNull(defaultIconEmoji);
    setName(APPENDER_NAME);
  }

  @VisibleForTesting
  protected static String key(String... parts) {
    return JOINER.join(parts);
  }

  /**
   * Each customization can be overridden via the {@link MDC}.
   */
  @VisibleForTesting
  protected Optional<String> mdc(ILoggingEvent eventObject, String key) {
    return Optional.ofNullable(eventObject.getMDCPropertyMap().get(key(PREFIX, key)));
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    // build the message
    final SlackMessage message = new SlackMessage();
    message.setText(text(eventObject));
    message.setChannel(orDefault(mdc(eventObject, "channel"), defaultChannel));
    message.setUsername(orDefault(mdc(eventObject, "username"), defaultUsername));
    message.setIconEmoji(orDefault(mdc(eventObject, "iconEmoji"), defaultIconEmoji));
    message.setIconUrl(orDefault(mdc(eventObject, "iconUrl"), defaultIconUrl));
    // post to slack
    slack.post(message);
  }

  /**
   * Returns:
   * <ol>
   * <li>the given {@code value}, if present
   * <li>the given {@code defaultValue}, if present
   * <li>{@code null} if neither {@code value nor {@code defaultValue} is present
   * </ol>
   */
  protected <T> T orDefault(Optional<T> value, Optional<T> defaultValue) {
    if (value.isPresent()) {
      return value.get();
    }
    return defaultValue.orElse(null);
  }

  @VisibleForTesting
  protected String text(ILoggingEvent eventObject) {
    return layout.doLayout(eventObject);
  }

}
