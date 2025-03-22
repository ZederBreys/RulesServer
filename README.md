# RulesServer
A simple Minecraft plugin for displaying useful messages.
I created the plugin for myself for my tasks.
For now it is just a plugin for displaying messages maybe later it will be something more.
## Settings
All settings in config.yml and alerts.yml
**config.yml**
```yml
# Default configuration file
enabled: true

rules-text: "Simple Rules..." #Server rules "/rules"
alerts: False #on/off alerts.yml
```
**alerts.yml**
```java
- text: "test1"
  delay: 15
- text: "test2"
  delay: 20
- text: "test3"
  delay: 5
  data: dd.MM.yyyy #Optional parameter: Set the message expiration date. The message will be displayed until and including the specified date, but will not be shown after that date.
```
Displayed message after a player logs in to the server. 