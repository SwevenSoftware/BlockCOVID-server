#!/bin/sh

echo `pwd`
[ ! -d db/ ] && mkdir db/
mongod --fork --dbpath db/ --logpath log
