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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CLITest {

    @BeforeClass
    public static void beforeClass() throws BrutException {
        TestUtils.cleanFrameworkFile();
        sTmpDir = new ExtFile(OS.createTempDirectory());
        TestUtils.copyResourceDir(CLITest.class, "../../../../apktool-lib/src/test/resources/brut/apktool/apk1/", sTmpDir);
    }

    @AfterClass
    public static void afterClass() throws BrutException {
        //OS.rmdir(sTmpDir);
        OS.rmdir("com.amazon.mShop.android.shopping");
    }

    @Test
    public void isProviderStringReplacementWorking() throws BrutException, IOException, InterruptedException {
        String base_path = CLITest.class.getProtectionDomain().getCodeSource().getLocation().getFile() + "../../../../apktool-lib/src/test/resources/brut/apktool";
        String apkpath = base_path + File.separator + "apk1/";
        String apk = "com.amazon.mShop.android.shopping.apk";
        // decode apk
        System.out.println(sTmpDir.getAbsolutePath() + File.separator + apk + ".out");
        System.out.println("Test1");
        String[] arguments = new String[]{"-v","d",apkpath + apk,"-f"};
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test2");
        arguments = new String[]{"-v","d",apkpath + apk,"-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test3");
        try{
            arguments = new String[]{"","d",apkpath + apk,"-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out","-f"};
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
//        File f = new File(sTmpDir.getAbsolutePath() + File.separator + apk + ".out");
//        if(f.exists() && f.isDirectory()){
//            System.out.println("true");
//        }
//        else{
//            System.out.println("false");
//        }
        //assertTrue(f.exists() && f.isDirectory());

        // build apk
        System.out.println("Test4");
        String aapt_path = "/Users/dvotipka/Library/Android/sdk/build-tools/19.1.0/aapt";
        arguments = new String[]{"-q","-a",aapt_path,"-f","b",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
        
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test5");
        arguments = new String[]{"-v","-a","/aapt","--max-sdk-version","21","-f","b",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
//        String newApk = sTmpDir.getAbsolutePath() + File.separator + apk + ".out" + File.separator + "dist" + File.separator + apk;
        //assertTrue(fileExists(newApk));
        
        String frameworkpath = "/Users/dvotipka/Documents/Projects/UMD/CMSC737/ApkToolFork/Apktool/brut.apktool/apktool-lib/src/test/resources/brut/apktool/framework/";
        String framework = "1.apk";
        System.out.println("Test5");
        arguments = new String[]{"if",frameworkpath + framework, "-t","htc","-p",frameworkpath};
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        
        
        System.out.println("Test7");
        try{
            arguments = new String[]{"--version"};
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test8");
        try{
            arguments = new String[]{"--advanced"};
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test9");
        try{
            arguments = new String[]{"publicize-resources",apkpath + apk};
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test10");
        arguments = new String[]{"-v","d",apkpath + apk,"-s","-f","-b","-t","test","-r","-k","-p",sTmpDir.getAbsolutePath(),"-m","--api","14","-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
//        System.out.println("Test11");
//        arguments = new String[]{"-v","d",apkpath + apk,"-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
//        try{
//            Main.main(arguments);
//        } catch(Exception e){
//                    System.err.println("Caught Exception: " + e.getMessage());
//                    System.err.println("Failed for arguments: " + arguments.toString());
//        }
//        System.out.println("Test12");
//        arguments = new String[]{"-v","d",apkpath + "null.apk","-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
//        try{
//            Main.main(arguments);
//        } catch(Exception e){
//                    System.err.println("Caught Exception: " + e.getMessage());
//                    System.err.println("Failed for arguments: " + arguments.toString());
//        }
//        System.out.println("Test12");
//        arguments = new String[]{"-v","d",apkpath + "null.apk","-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
//        try{
//            Main.main(arguments);
//        } catch(Exception e){
//                    System.err.println("Caught Exception: " + e.getMessage());
//                    System.err.println("Failed for arguments: " + arguments.toString());
//        }
//        System.out.println("Test13");
//        arguments = new String[]{"-v","d",apkpath + "immutable.apk","-o",sTmpDir.getAbsolutePath() + File.separator + apk + ".out"};
//        try{
//            Main.main(arguments);
//        } catch(Exception e){
//                    System.err.println("Caught Exception: " + e.getMessage());
//                    System.err.println("Failed for arguments: " + arguments.toString());
//        }
        System.out.println("Test14");
        arguments = new String[]{"-v","b",sTmpDir.getAbsolutePath() + File.separator + apk + ".out","-d","-v","-c","-p",sTmpDir.getAbsolutePath(),"-o",sTmpDir.getAbsolutePath() + File.separator + apk + "_new.apk"};
        try{
            Main.main(arguments);
        } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for arguments: " + arguments.toString());
        }
        System.out.println("Test6");
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    
        System.setOut(new PrintStream(outContent));
        
        arguments = new String[]{""};
        Main.main(arguments);
        
        String sysOut = outContent.toString();
        assertTrue(sysOut.contains("a tool for reengineering Android apk files"));
        
        System.setOut(null);
        
        
    }

    private boolean fileExists(String filepath) {
        return Files.exists(Paths.get(sTmpDir.getAbsolutePath() + File.separator + filepath));
    }

    private static ExtFile sTmpDir;
}
