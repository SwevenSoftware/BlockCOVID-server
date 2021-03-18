![Build status](https://github.com/SwevenSoftware/BlockCOVID-server/actions/workflows/build-server.yml/badge.svg?branch=develop)
[![codecov](https://codecov.io/gh/SwevenSoftware/BlockCOVID-server/branch/develop/graph/badge.svg?token=LD3HF3EPRD)](https://codecov.io/gh/SwevenSoftware/BlockCOVID-server)
# BlockCOVID-server
Server module for the project BlockCOVID (SWE 2020-2021)

## Contributing
### pre-commit hook
Run the following command in order to save the git-hook:
``` bash
ln -s pre-commit.sh .git/hooks/pre-commit
```

## Building the server test unit
### gradle build
Builds the project into a single `.jar` file in the build/libs folder
```sh
./gradlew build
```

### docker-compose build
This step is required in order to build and/or retrive all the necessary containers from the docker-hub
```sh
sudo docker-compose build
```

## Start test unit
Starts three containers, one with the database daemon `mongod`, one with `ganache-cli` in order to simulate a blockchain network, one with our application server which implements api calls
```sh
sudo docker-compose up
```
