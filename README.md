# Go Game
Server
*	Start de server
*	Geef een port aan waarop je wilt hosten
*	That’s is, good job

Client
* Start de Client
*	Geef je naam
*	Geef aan of je zelf wilt spelen of dat de AI voor je moet spelen
*	Geef aan of je een TUI of een GUI wilt
*	Geef aan op welke server je wilt spelen, door zowel de Inetaddress als de port te geven

* In geval dat je als je de eerste van de 2 spelers bent:
  * Geef aan welke kleur je wilt zijn
  * Geef aan hoe groot je het board wilt hebben

* Wanneer je speelt:
  * Wacht tot het je beurt is
  * Kies een index waarop je jouw volgende steen wilt zetten (de index tel je van links boven naar rechts onder)
  * Als je wilt passen, type -1
  * Al je niet weet waar je je volgende steen wilt zetten, kijk dan naar de hint steen

* Wanneer het is afgelopen:
  * Dit kan door drie mogelijkheden
    * Iemand disconnect
  * Alleen in geval van beide passen is het mogelijk om een rematch te doen
  
*	Wanneer het is afgelopen 
  * Dit kan door drie mogelijkheden
    * Iemand disconnect
    * Iemand typt EXIT
    * Beide spelers passen
  * Alleen in geval van beide passen is het mogelijk om een rematch te doen
    * Geef aan of je nog een potje wilt spelen tegen deze person
    * Beide ja? Lees terug naar WANNEER SPELEN
