# Dropwizard logs in Slack

Send your [Dropwizard](http://dropwizard.io) logs to [Slack](https://slack.com)

## Usage

Using the `SlackAppender` is as easy as adding the dependency to your Maven POM and adding the appender to your Dropwizard configuration file.

### Add via Maven

```XML
<!-- your application pom -->
<dependency>
	<groupId>io.gosoftware</groupId>
	<artifactId>dropwizard-slack</artifactId>
	<version>{current version}</version>
</dependency>
```

### Add the appender


```YAML
# add the appender in your dropwizard YAML
logging:
  level: INFO
  loggers:
    "your.package": INFO
  
  appenders:
    - type: console
      threshold: ALL
    - type: slack
      threshold: INFO
      incomingWebhookUrl: 'https://hooks.slack.com/services/your/slack/webhook/url' # required
      username: 'simeon' # defaults to '{applicationName}'
      iconEmoji: ':gem:'
      channel: '#app-logs' # required
```
#### Appender Configuration

| Property | Description | Required |
|:--- |:--- |:---:|
| incomingWebhookUrl | The full URL for your Slack webhook | Y |
| channel | The Slack channel to send each log message. The channel must begin with a `#` | Y |
| username | The Slack username used to send the logs | N |
| iconEmoji | The icon, as an emoji, which appears next to the username for each log message sent to the Slack channel. The emoji must start and end with a `:` | N |
| iconUrl | THe icon, as a URL, whic appears next to the username for each log message sent to the Slack channel | N |

