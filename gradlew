#!/usr/bin/env sh
# gradlew (standard Gradle wrapper script)
# DO NOT EDIT — this is the standard Gradle wrapper script.

# Wrapper for executing Gradle
# Resolve the path of the script
PRG="$0"

# Need to resolve symlinks for the script
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

SAVED="`pwd`"
cd "`dirname \"$PRG\"`/.." >&-
BASEDIR=`pwd -P`
cd "$SAVED" >&-

CLASSPATH="$BASEDIR/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$CLASSPATH" ]; then
  exec "$JAVA_HOME/bin/java" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
else
  # fallback to system gradle
  exec gradle "$@"
fi
