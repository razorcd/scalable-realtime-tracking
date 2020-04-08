#!/usr/bin/env bash


#https://tracker-api.scoober.com/v1/trackings/5f61ff2a5bedab9e3c3b4da94efddf31eaa0d548b413b2c5ce866e33f259fa9f

START_ORDERID=299060
END_ORDERID=299000
DOMAIN=locahost:9090

COMMAND=$1
echo Flag: $COMMAND


case $COMMAND in

  *"-k"*)
    kill $(ps aux | egrep "curl -v" | cut -d ' ' -f 2-3)
    echo "Curl processes running:" $(ps aux | grep [c]url | wc -l)
    echo "-k: Kill all Curl processes done."
    ;;

  *"-e"*)
    for i in $(eval echo "{$START_ORDERID..$END_ORDERID}")
    do
       echo "executing: curl -v $DOMAIN/simple/location/$i &>/dev/null &"
#       curl localhost:8080/v1/trackings &>/dev/null &
       curl -v localhost:9090/simple/location/$i &>/dev/null &
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






















