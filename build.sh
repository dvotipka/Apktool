#!/bin/sh
if [ $1 -eq 1 ]
   then 
	sed -i.bak 's/commandLine\.hasOption("version")/!commandLine\.hasOption("version")/g' brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
	cp -R brut.apktool/apktool-lib/build/classes results/original/classes
	cp -R brut.apktool/apktool-lib/build/customJacocoReportDir results/original/customJacocoReportDir
	cp -R brut.apktool/apktool-lib/build/jacoco results/original/jacoco
	cp -R brut.apktool/apktool-lib/build/reports results/original/reports
	cp -R brut.apktool/apktool-lib/build/test-results results/original/test-results
elif [ $1 -eq 2 ]
   then 
	mv brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java.bak brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
        sed -i.bak 's/return (id == 0) \? pkgs\[0\] : pkgs\[1\];/return (id == 0) \? pkgs\[1\] : pkgs\[0\];/g' brut.apktool/apktool-lib/src/main/java/brut/androlib/res/AndrolibResources.java
        cp -R brut.apktool/apktool-lib/build/classes results/mutant1/classes
        cp -R brut.apktool/apktool-lib/build/customJacocoReportDir results/mutant1/customJacocoReportDir
        cp -R brut.apktool/apktool-lib/build/jacoco results/mutant1/jacoco
        cp -R brut.apktool/apktool-lib/build/reports results/mutant1/reports
        cp -R brut.apktool/apktool-lib/build/test-results results/mutant1/test-results
else
   cp -R brut.apktool/apktool-lib/build/classes results/mutant2/classes
   cp -R brut.apktool/apktool-lib/build/customJacocoReportDir results/mutant2/customJacocoReportDir
   cp -R brut.apktool/apktool-lib/build/jacoco results/mutant2/jacoco
   cp -R brut.apktool/apktool-lib/build/reports results/mutant2/reports
   cp -R brut.apktool/apktool-lib/build/test-results results/mutant2/test-results
fi
