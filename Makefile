OUTPUT = bin
SOURCES := $(shell find src -type f -name \*.java)
LIBS = libs/jsch-0.1.44.jar:libs/vnc.jar:$(JAVA_HOME)/jre/lib/plugin.jar:libs/rdp.jar
FLAGS = -target 1.5 -classpath $(LIBS) -d $(OUTPUT)
KEYSTORE_ALIAS = "steam"
KEYSTORE_PASS = "abraxas"

all: deploy

build:
	@(javac $(FLAGS) src/*.java)

jar: build
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
	@(cp libs/* examples/applet/)

runserver: deploy
	@(./examples/dev_server.py)
