OUTPUT = bin
SOURCES := $(shell find src -type f -name \*.java)
LIBS = libs/plugin.jar:libs/java-getopt-1.0.13.jar:libs/log4j-java1.1.jar:libs/rdp.jar:libs/vnc.jar:libs/jsch-0.1.44.jar
FLAGS = -target 1.5 -classpath $(LIBS) -d $(OUTPUT)
KEYSTORE_ALIAS = "dev"
KEYSTORE_PASS = "123456"

all: deploy

build:
	javac $(FLAGS) src/*.java

# jar: build
# 	@(java -jar tools/autojar.jar -o evd.jar -vc "classes:libs/*.jar" -c bin -m src/manifest.mf /jre/lib/plugin.jar \
# Tunnel.class \
# Display.class \
# libs/java-getopt-1.0.13.jar \
# libs/log4j-java1.1.jar \
# libs/rdp.jar \
# libs/vnc.jar \
# libs/jsch-0.1.44.jar)

getlibs:
	cd libs; \
	wget -q -N --no-check-certificate https://github.com/jakobadam/rdp/raw/master/lib/java-getopt-1.0.13.jar & \
	wget -q -N --no-check-certificate https://github.com/jakobadam/rdp/raw/master/lib/log4j-java1.1.jar & \
	wget -q -N --no-check-certificate https://github.com/jakobadam/rdp/raw/master/rdp.jar & \
	wget -q -N --no-check-certificate https://github.com/jakobadam/vnc/raw/master/vnc.jar & \
	wget -q -N http://downloads.sourceforge.net/project/jsch/jsch.jar/0.1.44/jsch-0.1.44.jar

unjarlibs:
	cd bin; \
	find ../libs -type f -name \*.jar -not -name plugin.jar -exec jar xfv {} \;
	rm -rf bin/META-INF

jar: build unjarlibs
	@(jar cvmf src/manifest.mf evd.jar -C bin/ .)

signlibs:
	@(mkdir -p libs/temp)
	@(cd libs ; find . -maxdepth 1 -type f -exec jarsigner -storepass $(KEYSTORE_PASS) -signedjar temp/{} {} $(KEYSTORE_ALIAS) \;)
	@(mv libs/temp/* libs)
	@(rmdir libs/temp/)	

sign: jar 
	@(jarsigner -storepass $(KEYSTORE_PASS) -signedjar evds.jar evd.jar $(KEYSTORE_ALIAS))
	@(mv evds.jar evd.jar)

clean:
	@(rm bin/*.class)
	@(rm examples/applet/*.jar)

deploy: sign
	@(mkdir -p examples/applet)
	@(cp evd.jar examples/applet/)

runserver: deploy
	@(./examples/dev_server.py)
