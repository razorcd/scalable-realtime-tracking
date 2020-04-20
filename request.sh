#!/usr/bin/env bash



MULT=1
START_ORDERID=600000
END_ORDERID=609000

COMMAND=$1
echo Flag: $COMMAND


case $COMMAND in

  *"-k"*)
    kill $(ps aux | egrep "curl htt" | cut -d ' ' -f 2-3)
    echo "Curl processes running:" $(ps aux | grep [c]url | wc -l)
    echo "-k: Kill all Curl processes done."
    ;;

  *"-e"*)
    for l in $(eval echo "{1..$MULT}")
    do
        for i in $(eval echo "{$START_ORDERID..$END_ORDERID}")
        do
           echo "executing: curl .../location/$i &>/dev/null &"
#           curl https://xxxxxxxxx/location/$i &>/dev/null &
           curl localhost:8081/location/$i &>/dev/null &
        done
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





















