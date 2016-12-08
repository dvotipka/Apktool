#!/bin/sh
if [$1 -eq 1]
   then 
	sed -i.bak 's/commandLine\.hasOption("version")/!commandLine\.hasOption("version")/g' brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
	cp brut.apktool/apktool-lib/build/ results/original/
elif [$1 -eq 2]
   then 
	mv brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java.bak brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
        sed -i.bak 's/return (id == 0) \? pkgs\[0\] : pkgs\[1\];/return (id == 0) \? pkgs\[1\] : pkgs\[0\];/g' brut.apktool/apktool-lib/src/main/java/brut/androlib/res/AndrolibResources.java
        cp brut.apktool/apktool-lib/build/ results/mutant1/
else
   cp brut.apktool/apktool-lib/build/ results/mutant2/
fi
