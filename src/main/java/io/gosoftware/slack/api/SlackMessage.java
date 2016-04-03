package io.gosoftware.slack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class SlackMessage extends GenericData {

  @JsonProperty
  @Key
  private String text;

  @JsonProperty
  @Key
  private String channel;

  @JsonProperty
  @Key
  private String username;

  @JsonProperty
  @Key("icon_url")
  private String iconUrl;

  @JsonProperty
  @Key("icon_emoji")
  private String iconEmoji;

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
