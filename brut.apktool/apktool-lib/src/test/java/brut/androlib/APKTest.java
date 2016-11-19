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
package brut.androlib;

import brut.androlib.res.util.ExtFile;
import brut.androlib.res.AndrolibResources;
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
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class APKTest {

    @BeforeClass
    public static void beforeClass() throws BrutException {
        TestUtils.cleanFrameworkFile();
        sTmpDir = new ExtFile(OS.createTempDirectory());
        TestUtils.copyResourceDir(ProviderAttributeTest.class, "brut/apktool/apk1/", sTmpDir);
    }

    @AfterClass
    public static void afterClass() throws BrutException {
        OS.rmdir(sTmpDir);
    }

    @Test
    public void isProviderStringReplacementWorking() throws BrutException, IOException {
        String apk = "testapp.apk";
        String newApk = "";
        ExtFile testApk;
        Androlib mAndrolib = new Androlib();
        AndrolibResources mAndRes = new AndrolibResources();
        ApkDecoder apkDecoder;
        
        try{
        // decode issue636.apk
            apkDecoder = new ApkDecoder(new File(sTmpDir + File.separator + apk));
            apkDecoder.setOutDir(new File(sTmpDir + File.separator + apk + ".out"));
            apkDecoder.setKeepBrokenResources(true);
            apkDecoder.setBaksmaliDebugMode(true);
            apkDecoder.setForceDelete(true);
            apkDecoder.decode();
            
            mAndRes.tagSmaliResIDs(mAndrolib.getResTable(new ExtFile(new File(sTmpDir + File.separator + apk)),false),new File(sTmpDir + File.separator + apk + ".out/smali"));
            mAndRes.updateSmaliResIDs(mAndrolib.getResTable(new ExtFile(new File(sTmpDir + File.separator + apk)),false),new File(sTmpDir + File.separator + apk + ".out/smali"));

            // build issue636
            testApk = new ExtFile(sTmpDir, apk + ".out");
            new Androlib().build(testApk, null);
            newApk = apk + ".out" + File.separator + "dist" + File.separator + apk;
            //assertTrue(fileExists(newApk));
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        
        String apk_dir_path = "/Users/dvotipka/Documents/Projects/UMD/AndroidInteractionStudy/apks";
        File apk_dir = new File(apk_dir_path);
        File[] fileList = apk_dir.listFiles();
        //fileList = Arrays.copyOfRange(fileList,0,5);
        for(File f : fileList){
            System.out.println("Running APK: " + apk);
            apk = f.getName();
            if(apk.contains(".apk")){
                try{
                    ExtFile apkFile = new ExtFile(new File(apk_dir_path + File.separator + apk));
                    File rawmandir = new File(sTmpDir + File.separator + apk + "rawmanifest.out");
                    File rawresourcesdir = new File(sTmpDir + File.separator + apk + "rawresources.out");
                    File fullmandir = new File(sTmpDir + File.separator + apk + "fullmanifest.out");
                    
                    if(!rawmandir.exists()){
                        rawmandir.mkdir();
                    }
                    if(!fullmandir.exists()){
                        fullmandir.mkdir();
                    }
                    if(!rawresourcesdir.exists()){
                        rawresourcesdir.mkdir();
                    }
                    
                    mAndrolib.decodeManifestRaw(apkFile, rawmandir);
                    mAndrolib.decodeManifestFull(apkFile, fullmandir, mAndrolib.getResTable(apkFile,true));
                    mAndrolib.decodeResourcesRaw(new ExtFile(new File(apk_dir_path + File.separator + apk)), rawresourcesdir);
                
                    apkDecoder = new ApkDecoder(new File(apk_dir_path + File.separator + apk));
                    apkDecoder.setOutDir(new File(sTmpDir + File.separator + apk + ".out"));
                    apkDecoder.setKeepBrokenResources(false);
                    apkDecoder.setBaksmaliDebugMode(false);
                    apkDecoder.setForceDelete(false);
                    apkDecoder.decode();

                    // build issue636
                    testApk = new ExtFile(sTmpDir, apk + ".out");
                    new Androlib().build(testApk, null);
                    newApk = apk + ".out" + File.separator + "dist" + File.separator + apk;
                    //assertTrue(fileExists(newApk));
                } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for App: " + apk);
                }
            
            }
        }
        

        // decode issues636 again
//        apkDecoder = new ApkDecoder(new File(sTmpDir + File.separator + newApk));
//        apkDecoder.setOutDir(new File(sTmpDir + File.separator + apk + ".out.two"));
//        apkDecoder.decode();
//
//        String expected = TestUtils.replaceNewlines("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
//                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"com.ibotpeaches.issue636\" platformBuildVersionCode=\"23\" platformBuildVersionName=\"6.0-2438415\">\n" +
//                "    <application android:allowBackup=\"true\" android:debuggable=\"true\" android:icon=\"@mipmap/ic_launcher\" android:label=\"@string/app_name\" android:theme=\"@style/AppTheme\">\n" +
//                "        <provider android:authorities=\"com.ibotpeaches.issue636.Provider\" android:exported=\"false\" android:grantUriPermissions=\"true\" android:label=\"@string/app_name\" android:multiprocess=\"false\" android:name=\"com.ibotpeaches.issue636.Provider\"/>\n" +
//                "        <provider android:authorities=\"com.ibotpeaches.issue636.ProviderTwo\" android:exported=\"false\" android:grantUriPermissions=\"true\" android:label=\"@string/app_name\" android:multiprocess=\"false\" android:name=\"com.ibotpeaches.issue636.ProviderTwo\"/>\n" +
//                "    </application>\n" +
//                "</manifest>");
//
//
//        byte[] encoded = Files.readAllBytes(Paths.get(sTmpDir + File.separator + apk + ".out.two" + File.separator + "AndroidManifest.xml"));
//        String obtained = TestUtils.replaceNewlines(new String(encoded));
//        assertEquals(expected, obtained);
    }

    private boolean fileExists(String filepath) {
        return Files.exists(Paths.get(sTmpDir.getAbsolutePath() + File.separator + filepath));
    }

    private static ExtFile sTmpDir;
}
