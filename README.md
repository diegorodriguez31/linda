# IR-Linda-Thomas_Nadal_Elizalde-Diego_Rodriguez

## Lancer l'application

Les URI de nos serveurs (principaux et backup) sont fixes, nous n'avons pas eu le temps de les gérer dynamiquement.

Si vous voulez changer les ports, il faut modifier la valeur des constantes de début de classe.
_(linda.server.LindaClient, linda.server.BackupLindaServer et linda.server.LindaServer)_

### 1. Compiler l'application + se placer avant le répertoire "linda" contenant les .class
Vous pouvez compiler en ligne de commande ou via votre IDE.

### 2. Lancer le serveur de Backup (localhost:1098)
Il est **obligatoire** de lancer le serveur de backup avant le serveur principal.

    java linda.server.BackupLindaServer

### 3. Lancer le serveur principal (localhost:1099)
    java linda.server.LindaServer

### 4. Lancer une application client (Whiteboard par exemple)
    java linda.whiteboard.Whiteboard localhost:1099
