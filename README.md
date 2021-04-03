![Build status](https://github.com/SwevenSoftware/BlockCOVID-server/actions/workflows/build-server.yml/badge.svg)
[![codecov](https://codecov.io/gh/SwevenSoftware/BlockCOVID-server/branch/develop/graph/badge.svg)](https://codecov.io/gh/SwevenSoftware/BlockCOVID-server)
# BlockCOVID-server
## Description
Server module for the project BlockCOVID.
This module provides all the API required for the [**android**](https://github.com/SwevenSoftware/BlockCOVID-android)
and [**web**](https://github.com/SwevenSoftware/BlockCOVID-web) modules, 
together with all the interactions with an ethereum network
(for testing we recommend [ganache](https://github.com/trufflesuite/ganache-cli))


## Usage
### Prerequisites
#### MongoDB
This application uses a mongodb instance as database. You can start and stop a mongodb daemon
trough the provided scripts `spawnMongo.sh` and `killMongo.sh`. The folder `./db` will be used as
mongo database folder.

#### Https
To run the server make sure you have a PKCS12 keystore with your public key, this is important in order to ensure `https`
requests are handled properly. **Without a valid keystore the server will fail to start**. You can either create a 
[self-signed pkcs12 keystore](https://en.wikipedia.org/wiki/Self-signed_certificate) or issue one with a 
[CA](https://it.wikipedia.org/wiki/Certificate_authority) (such as [letsencrypt](https://letsencrypt.org/))

#### Blockchain
In order to save the hashes of generated reports on a blockchain netwokrk you need to:
* Select a network to deploy your contract: you can simply use the provided *containered* solution and test the server
  behavior trough *ganache*. Otherwise, there are a number of ethereum test network such as [Ropsten](https://faucet.ropsten.be/)

* Open an account on that particular network 

once you have such information compile the file `src/main/resources/aplpication.properties`.
An example could be the following
```properties
server.ssl.key-store-type=PKCS12
# Position of your keystore
server.ssl.key-store=classpath:keystore/keystore.p12
# Keystore password
server.ssl.key-store-password=keystorePassword
# Keystore alias
server.ssl.key-alias=keystoreAlias
# enabling ssl for requests and responses 
server.ssl.enabled=true
# Mongodb URI, enery mongodb instance exposes a URI for connections
spring.data.mongodb.uri=mongodb://127.0.0.1:27017/blockcovid-test
# The port of the application
server.port=8091
# This is your account private key on the network, the one provided here
# is just an example
it.sweven.blockcovid.blockchain.account=0xb43436657ed0d6b922f3e7fab75dc32c610796d374daa6d7f1878669ff79d0e5
# the contract field can be void, in this case the application will 
# deploy the contract on the provided network and save the new address 
# in the database
it.sweven.blockcovid.blockchain.contract=
# The network the application should interface with
it.sweven.blockcovid.blockchain.network=http://127.0.0.1:8545
```

### Building
```shell
./gradlew build
```
will build the single artifact based on the provided `application.properties` and will store it at `build/libs`.
This artifact can then be run on the JVM. 

### Running
Once all the prerequisites are satisfied, and the artifact is built the application can be launched in several ways:

#### Test container start
This way you can test the application with a container version of mongodb and ganache cli
```shell
sudo docker-compose build && \
sudo docker-compose up
```
this way the containers will be created and started. Eventually when the run ends you can dismantle the built containers 
with
```shell
sudo docker-compose down
```
#### Standalone Application
In this case an instance of mongodb and valid network credentials must be supplied.
```shell
./gradlew bootRun
```
will start the application, in this case you handle both the mongodb instance and the interaction with the 
blockchain network


## Contributing
We adopt a [Gitflow workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).
So in order to contribute to the application the steps are:
- start from `develop` branch
- `git flow feature start [faeture name]` (alternatively `git checkout -b feature/[feature name]`)
- Implement the new feature and the corresponding tests
- commit your changes
- `git flow feature pulish [feature name]` (alternatively `git push -u origin feature/[feature name]`)
- open a pull request describing your changes and addressing issues if necessary
eventually an administrator will review your work and merge it in the develop branch.
### pre-commit hook
Builds will fail if the code is not compiant with the [spotless](https://github.com/diffplug/spotless) formatting.
Therefore add this hook to git that prevents you from committing anything that is not correctly formatted
```shell
cp pre-commit.sh .git/hooks/pre-commit
```
text can be formatted with
```shell
./graldew spotlessApply
```
