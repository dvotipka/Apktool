/**
 *  Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright 2014 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.apktool;

import brut.androlib.res.util.ExtFile;
import brut.androlib.*;
import brut.common.BrutException;
import brut.directory.DirectoryException;
import brut.util.OS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CLITest {

    @BeforeClass
    public static void beforeClass() throws BrutException {
        TestUtils.cleanFrameworkFile();
        sTmpDir = new ExtFile(OS.createTempDirectory());
        TestUtils.copyResourceDir(CLITest.class, "brut/apktool/apk1/", sTmpDir);
    }

    @AfterClass
    public static void afterClass() throws BrutException {
        OS.rmdir(sTmpDir);
    }

    @Test
    public void isProviderStringReplacementWorking() throws BrutException, IOException, InterruptedException {
        String apkpath = "/Users/dvotipka/Documents/Projects/UMD/CMSC737/ApkToolFork/Apktool/brut.apktool/apktool-lib/src/test/resources/brut/apktool/apk1/";
        String apk = "com.amazon.mShop.android.shopping.apk";
        // decode apk
        System.out.println(sTmpDir.getAbsolutePath() + File.separator + apk + ".out");
        String[] arguments = new String[]{"-v","d",apkpath + apk};
        Main.main(arguments);
        
        arguments = new String[]{"-v","d",apkpath + apk,"-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
        Main.main(arguments);
//        File f = new File(sTmpDir.getAbsolutePath() + File.separator + apk + ".out");
//        if(f.exists() && f.isDirectory()){
//            System.out.println("true");
//        }
//        else{
//            System.out.println("false");
//        }
        //assertTrue(f.exists() && f.isDirectory());

        // build apk
        String aapt_path = "/Users/dvotipka/Library/Android/sdk/build-tools/19.1.0/aapt";
        arguments = new String[]{"-q","-a",aapt_path,"-f","b",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
        Main.main(arguments);
//        String newApk = sTmpDir.getAbsolutePath() + File.separator + apk + ".out" + File.separator + "dist" + File.separator + apk;
        //assertTrue(fileExists(newApk));
        
        String frameworkpath = "/Users/dvotipka/Documents/Projects/UMD/CMSC737/ApkToolFork/Apktool/brut.apktool/apktool-lib/src/test/resources/brut/apktool/framework/";
        String framework = "1.apk";
        arguments = new String[]{"if",frameworkpath + framework, "-t","htc","-p",frameworkpath};
        Main.main(arguments);
        
        arguments = new String[]{""};
        Main.main(arguments);
        arguments = new String[]{"version"};
        Main.main(arguments);
        
//        String resourcepath = "/Users/dvotipka/Downloads/test/test2/";
//        String resource = "resources.arsc";
//        arguments = new String[]{"publicize-resources",resourcepath + resource};
//        Main.main(arguments);
        
        
    }

    private boolean fileExists(String filepath) {
        return Files.exists(Paths.get(sTmpDir.getAbsolutePath() + File.separator + filepath));
    }

    private static ExtFile sTmpDir;
}
