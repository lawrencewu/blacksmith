#!/bin/bash

## Load includes
if [ "x$BLACKSMITH_HOME" = "x" ]; then DIRNAME=`dirname $0`; BLACKSMITH_HOME=`cd $DIRNAME/..; pwd` ; fi; export BLACKSMITH_HOME
. ${BLACKSMITH_HOME}/bin/includes.sh

CONFIG=./conf/local-benchmark.xml

help_and_exit() {
  echo "Usage: "
  echo '  $ local.sh -[c]'
  echo ""
  echo "   -c        Path to the framework configuration XML file. Optional - if not supplied benchmark will load ${CONFIG}"
  echo ""
  echo ""

  exit 0
}

welcome "This script is used to launch local benchmarks."

### read in any command-line params
while ! [ -z $1 ]
do
  case "$1" in
    "-c")
      CONFIG=$2
      shift
      ;;
    *)
      help_and_exit
      ;;
  esac
  shift
done

add_fwk_to_classpath
set_env
${JAVA} ${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError -classpath $CP -Dbind.address=${BIND_ADDRESS} com.ctriposs.blacksmith.local.LaunchLocal -config ${CONFIG}
