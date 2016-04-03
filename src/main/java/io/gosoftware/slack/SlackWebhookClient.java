package io.gosoftware.slack;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;

import io.gosoftware.slack.api.SlackMessage;

/**
 * Simple HTTP client for the Slack Webhook API.
 * 
 * @see https://api.slack.com/incoming-webhooks
 */
public class SlackWebhookClient {

  private static final Logger log = LoggerFactory.getLogger(SlackWebhookClient.class);

  private final GenericUrl incomingWebhookUrl;

  private final HttpRequestFactory requestFactory;

  private final JsonFactory jsonFactory;

  public SlackWebhookClient(HttpRequestFactory requestFactory, JsonFactory jsonFactory,
      String incomingWebhookUrl) {
    this.requestFactory = checkNotNull(requestFactory);
    this.jsonFactory = checkNotNull(jsonFactory);
    checkArgument(StringUtils.isNotBlank(incomingWebhookUrl));
    this.incomingWebhookUrl = new GenericUrl(incomingWebhookUrl);
  }

  public SlackWebhookClient post(SlackMessage message) {
    checkNotNull(message);
    
    // build the POST
    final HttpRequest request;
    try {
      request = requestFactory.buildPostRequest(incomingWebhookUrl,
          new JsonHttpContent(jsonFactory, message));
      request.setLoggingEnabled(true);
    } catch (IOException e) {
      log.error("Failed to build request: message={}", message, e);
      // TODO handle?
      return this;
    }

    // POST the message
    try {
      HttpResponse response = request.execute();
      if (!response.isSuccessStatusCode()) {
        // TODO handle?
      }
    } catch (Exception e) {
      log.error("Failed to send request: message={}", message, e);
      // TODO handle?
    }
    return this;
  }

}
