JAVA_SRC := $(shell find src/main/java -name "*.java")
TEST_SRC := $(shell find src/test/java -name "*.java")

.PHONY: build test jar clean

build:
	@mkdir -p out/main
	javac --release 17 -Xlint:all -Werror -d out/main $(JAVA_SRC)

test: build
	@mkdir -p out/test
	javac --release 17 -Xlint:all -Werror -cp out/main -d out/test $(TEST_SRC)
	java -ea -cp out/main:out/test rolemining.testing.TestRunner out/test

jar: build
	jar --create --file out/rolemine.jar --main-class rolemining.cli.Main -C out/main .

clean:
	rm -rf out
