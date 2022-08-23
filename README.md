# TwitchBot
Wir bauen einen Twitch Chatbot! ([VOD](https://www.twitch.tv/videos/1569480000))

## Übersicht
Twitch benutzt IRC für chat bots (https://dev.twitch.tv/docs/irc).
Wir fangen vermutlich mit einem proof of concept an, der einfache Befehle senden kann (z.B. Chat leeren).
Dann geht es hoffentlich weiter mit einer halbwegs sinvollen IRC implementierung und einer schönen Abstrahierung des IRC Layers in einen Chat Bot.
Falls wir so weit kommen, werden noch ein paar Befehle etc. in den Bot eingebaut und direkt im Stream getestet :)

### Update nach dem Stream
Man braucht nicht wirklich ein IRC Layer, also ist das ganze ein bisschen mit der Bot implementierung vermischt.
Der Stream war tatsächlich recht produktiv und der Bot kann "die Basics" (Nachrichten schreiben, etc.).
Nach dem Stream habe ich den Code noch etwas refactored und kommentiert.
Er ist nicht production ready, aber für Hobby Projekte kann schon darauf aufbauen :-).
Beispiele folgen, wenn ich etwas mehr Zeit habe, bis dahin kann man sich die `main` Methode anschauen (in `src/Main.java`) und den JavaDoc lesen.

## Anforderungen
In den Streams programmiere ich in einer Ubuntu 22.04 VM.
Alle Dinge, die ich zusätzlich für dieses Projekt installieren musste (außer git), findet man hier: [pastebin.com/c73DXFZH](https://pastebin.com/c73DXFZH).
Außerdem benötigt der Bot einen Access Token, um mit dem Twitch Chat interagieren zu können. Wie man einen Acces Token bekommt, ist [hier](access_token) beschrieben.

