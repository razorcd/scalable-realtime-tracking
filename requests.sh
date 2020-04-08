#!/usr/bin/env bash


#https://tracker-api.scoober.com/v1/trackings/5f61ff2a5bedab9e3c3b4da94efddf31eaa0d548b413b2c5ce866e33f259fa9f

START_ORDERID=1
END_ORDERID=3

COMMAND=$1
echo Flag: $COMMAND


case $COMMAND in

  *"-k"*)
    kill $(ps aux | egrep "curl -v" | cut -d ' ' -f 3)
    echo "Curl processes running:" $(ps aux | grep [c]url | wc -l)
    echo "-k: Kill all Curl processes done."
    ;;

  *"-e"*)
    for i in $(eval echo "{$START_ORDERID..$END_ORDERID}")
    do
       echo "executing: curl -v localhost:8080/location/$i &>/dev/null &"
       curl -v localhost:8080/location/$i &>/dev/null &
    done
    echo "Curl processes running:" $(ps aux | grep [c]url | wc -l)
    echo "-e: done."
    ;;

  *"-c"*)
    echo "Curl processes running:" $(ps aux | grep [c]url | wc -l)
    echo "-c done"
    ;;

  *)
    echo "Can not understand flag."
    ;;
esac






















