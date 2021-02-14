# BlockCOVID-server
Modulo Server per il progetto di SWE 2020/2021 - BlockCOVID

## Come usare mongo
Sono inclusi degli script per utilizzare mongo per testare come si comporta:
- **Avvio**: `./spawnMongo.sh`, spawna un'istanza di `mongod` che lavora sulla cartella locale `./db`. Fallisce se ci sono altre istanze già attive.
- **Stop**: `./killMongo.sh`, termina l'istanza di `mongod` avviata con `spawnMongo.sh`. Attenzione che fallisce con tutte le altre istanze di mongodb.
- **Una volta avviato**: è possibile utilizzare la shell solita tramite il comando `mongo`.
