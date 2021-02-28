# BlockCOVID-server
Modulo Server per il progetto di SWE 2020/2021 - BlockCOVID

## Pre-Commit
Eseguire il seguente comando per salvare il git hook:
``` bash
ln -s pre-commit.sh .git/hooks/pre-commit
```

## Dipendenze
Per la corretta esecuzione del server la macchina ospite deve avere:
- `mongod` disponibile, e un demone deve essere avviato prima dell'avvio del server (sezione successiva per come utilizzare gli script nella cartella)
- `ganache-cli` disponibile.

## Prima dell'avvio del server
### Ganache
```sh
ganache-cli
```
`ganache-cli` è un'utility di test per programmi su blockchain ed è l'impostazione di default per il server. In alternativa è possibile avviare il server impostando la rete sulla quale eseguire i test (come può essere [Ropsten](https://faucet.ropsten.be/)) col parametro `NETWORK`.

### Mongo
Sono inclusi degli script per utilizzare mongo per testare come si comporta:
- **Avvio**: `./spawnMongo.sh`, spawna un'istanza di `mongod` che lavora sulla cartella locale `./db` e raccoglie i log nel file `log`. Fallisce se ci sono altre istanze già attive.
- **Stop**: `./killMongo.sh`, termina l'istanza di `mongod` avviata con `spawnMongo.sh`. Attenzione che fallisce con tutte le altre istanze di mongodb.
- **Una volta avviato**: è possibile utilizzare la shell tramite il comando `mongo`.

## Avvio del server
```sh
ACCOUNT=[account private key] ./gradlew bootRun
```
`ACCOUNT` è necessario in quanto deteremina quale è l'account della rete sul quale fare il deploy del contratto. È possibile avviare il server con altri due parametri:
- `NETWORK`, indica l'indirizzo della rete con la quale il modulo blockchain si interfaccia
- `CONTRACT_ADDRESS` indirizzo del contratto se è già stato fatto un deploy. In caso assente il server farà un deploy ad ogni avvio.
