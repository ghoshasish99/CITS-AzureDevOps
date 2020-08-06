DIRNAME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIRNAME"
#class paths
APP_CLASSPATH=lib/*:lib/clib/*
java -Xms128m -Xmx1024m -Dfile.encoding=UTF-8 -cp cognizant-intelligent-test-scripter-ide-1.1.jar:$APP_CLASSPATH com.cognizant.cognizantits.ide.main.Main "$@"