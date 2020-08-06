DIRNAME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIRNAME/lib"
SELENIUM=$(find . -name selenium-server*.jar)
java -jar $SELENIUM -role node -nodeConfig ../nodeconfig.json