#!/bin/sh
set -e;

mongo <<EOF
use voc

db.createCollection("properties")
db.originTable.createIndex("@id")

db.createCollection("classes")
db.originTable.createIndex("@id")

quit()
EOF
