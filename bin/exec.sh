#!/bin/bash

MAIN_CLASS="com.example.App"

usage() {
    echo "Usage: $0 -o OUTPUT_DIR -r REPOSITORY_PATH [-c] [REV_NAME_1 [REV_NAME_2]]"
    exit 1
}

while getopts ":o:r:c" opt
do
    case $opt in
        o) OUTPUT_DIR="$OPTARG" ;;
        r) REPOSITORY_PATH="$OPTARG" ;;
        c) CLEAN="true" ;;
        *)
            echo -e "Invalid option was specified -$OPTARG"
            usage
            ;; # TODO echo -e "Unsupported parameter -{$opt}"
    esac
done

if [ -z "$OUTPUT_DIR" ] ; then
    echo -e "Parameter -o is not supplied"
    FAILED="true"
fi

if [ -z "$REPOSITORY_PATH" ] ; then
    echo -e "Parameter -r is not supplied"
    FAILED="true"
fi

if [[ $FAILED == "true" ]] ; then
    echo
    usage
fi

REV_NAME_1=${@:$OPTIND:1}
REV_NAME_2=${@:$OPTIND+1:1}

OPTIONS=""
# OPTIONS="-Dorg.slf4j.simpleLogger.logFile=gitometer.log" 
if [ -n "$REV_NAME_1" ] ; then
    OPTIONS="$OPTIONS -DrevName1=$REV_NAME_1"
fi
if [ -n "$REV_NAME_2" ] ; then
    OPTIONS="$OPTIONS -DrevName2=$REV_NAME_2"
fi

if [[ $CLEAN == "true" ]] ; then
    mvn clean package || FAILED="true"
fi
if [[ $FAILED != "true" ]] ; then
    java -cp target/gitometer-1.0-SNAPSHOT-jar-with-dependencies.jar \
         -DoutputDir="$OUTPUT_DIR" -DrepositoryPath="$REPOSITORY_PATH" \
         $OPTIONS \
         "$MAIN_CLASS"
fi 
