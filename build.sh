#!/bin/sh
if [ $1 -eq 1 ]
   then 
	sed -i.bak 's/commandLine\.hasOption("version")/!commandLine\.hasOption("version")/g' brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
	cp -R brut.apktool/apktool-lib/build/classes results/original/
	cp -R brut.apktool/apktool-lib/build/customJacocoReportDir results/original/
	cp -R brut.apktool/apktool-lib/build/jacoco results/original/
	cp -R brut.apktool/apktool-lib/build/reports results/original/
	cp -R brut.apktool/apktool-lib/build/test-results results/original/
elif [ $1 -eq 2 ]
   then 
	mv brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java.bak brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
        sed -i.bak 's/return (id == 0) \? pkgs\[0\] : pkgs\[1\];/return (id == 0) \? pkgs\[1\] : pkgs\[0\];/g' brut.apktool/apktool-lib/src/main/java/brut/androlib/res/AndrolibResources.java
        cp -R brut.apktool/apktool-lib/build/classes results/mutant1/
        cp -R brut.apktool/apktool-lib/build/customJacocoReportDir results/mutant1/
        cp -R brut.apktool/apktool-lib/build/jacoco results/mutant1/
        cp -R brut.apktool/apktool-lib/build/reports results/mutant1/
        cp -R brut.apktool/apktool-lib/build/test-results results/mutant1/
else
   cp -R brut.apktool/apktool-lib/build/classes results/mutant2/
   cp -R brut.apktool/apktool-lib/build/customJacocoReportDir results/mutant2/
   cp -R brut.apktool/apktool-lib/build/jacoco results/mutant2/
   cp -R brut.apktool/apktool-lib/build/reports results/mutant2/
   cp -R brut.apktool/apktool-lib/build/test-results results/mutant2/
fi
