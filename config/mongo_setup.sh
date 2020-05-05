#!/bin/sh

set -e;
uname=`grep -oP '(?<="vocserver.database.username": ")[^"]*' /docker-entrypoint-initdb.d/vocserver.json`
passwd=`grep -oP '(?<="vocserver.database.password": ")[^"]*' /docker-entrypoint-initdb.d/vocserver.json`

mongo <<EOF
use voc
db.createUser({user: "$uname", pwd: "$passwd", roles: [ { role: "readWrite", db: "voc" }]})

db.createCollection("master")

db.createCollection("properties")
db.properties.createIndex({"@id": 1})

db.createCollection("classes")
db.classes.createIndex({"@id": 1})

quit()
EOF
