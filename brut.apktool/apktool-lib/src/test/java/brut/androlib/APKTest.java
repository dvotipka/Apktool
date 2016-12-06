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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.util.List;

public class APKTest {

    @BeforeClass
    public static void beforeClass() throws BrutException {
        TestUtils.cleanFrameworkFile();
        sTmpDir = new ExtFile(OS.createTempDirectory());
        TestUtils.copyResourceDir(ProviderAttributeTest.class, "brut/apktool/apk1/", sTmpDir);
        File dir = new File(sTmpDir + File.separator + "apks");
    
        // attempt to create the directory here
        dir.mkdir();
    }

    @AfterClass
    public static void afterClass() throws BrutException {
        OS.rmdir(sTmpDir);
    }
    
    // Code to pull all the files from the UMD Objbox
    public void downloadFiles(String install_path){
        final AmazonS3 s3 = new AmazonS3Client();
        s3.setEndpoint("https://obj.umiacs.umd.edu/");
        String bucket_name = "apks";
        ObjectListing ol = s3.listObjects(bucket_name);
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os: objects) {
            System.err.println("* " + os.getKey());
            if(os.getKey().contains(".apk")){
                try {
                    S3Object o = s3.getObject(bucket_name, os.getKey());
                    S3ObjectInputStream s3is = o.getObjectContent();
                    FileOutputStream fos = new FileOutputStream(new File(install_path + File.separator + os.getKey().split("apks/")[1]));
                    byte[] read_buf = new byte[1024];
                    int read_len = 0;
                    while ((read_len = s3is.read(read_buf)) > 0) {
                        fos.write(read_buf, 0, read_len);
                    }
                    s3is.close();
                    fos.close();
                } catch (AmazonServiceException e) {
                    System.err.println(e.getErrorMessage());
                    System.exit(1);
                } catch (FileNotFoundException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
    
    public void uploadApks(String apk_path){
        final AmazonS3 s3 = new AmazonS3Client();
        s3.setEndpoint("https://obj.umiacs.umd.edu/");
        String bucket_name = "apks";
        File apk_dir = new File(apk_path);
        File[] fileList = apk_dir.listFiles();
        String apk = "";
        System.out.println("gets into the upload");
        for(File f : fileList){
            apk = f.getName();
            System.out.println("uploading file: " + apk);
            if(apk.contains(".apk")){
                try {
                    s3.putObject(new PutObjectRequest(bucket_name, apk, f));
                } catch (AmazonServiceException e) {
                    System.err.println(e.getErrorMessage());
                    System.exit(1);
                }
            }
        }
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
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        try{
            mAndRes.tagSmaliResIDs(mAndrolib.getResTable(new ExtFile(new File(sTmpDir + File.separator + apk)),false),new File(sTmpDir + File.separator + apk + ".out/smali"));
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        try{
            mAndRes.updateSmaliResIDs(mAndrolib.getResTable(new ExtFile(new File(sTmpDir + File.separator + apk)),false),new File(sTmpDir + File.separator + apk + ".out/smali"));
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        try{
            // build issue636
            testApk = new ExtFile(sTmpDir, apk + ".out");
            new Androlib().build(testApk, null);
            newApk = apk + ".out" + File.separator + "dist" + File.separator + apk;
            //assertTrue(fileExists(newApk));
            
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        try{
            String arsc_path = "/Users/dvotipka/Documents/Projects/UMD/CMSC737/ApkToolFork/Apktool/brut.apktool/apktool-lib/src/test/resources/brut/apktool/apks/app-debug/resources.arsc";
            mAndRes.publicizeResources(new File(arsc_path));
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        try{
            mAndRes.getAndroidResourcesFile();
        } catch(Exception e){
            System.err.println("Caught Exception: " + e.getMessage());
            System.err.println("Failed for App: " + apk);
        }
        
        
//        String orig_apk_dir_path = "/Users/dvotipka/Documents/Projects/UMD/AndroidInteractionStudy/apks";
        //uploadApks(orig_apk_dir_path);
        String test_apk_dir_path = "/Users/dvotipka/Documents/Projects/UMD/CMSC737/ApkToolFork/Apktool/brut.apktool/apktool-lib/src/test/resources/brut/apktool/apks";
        String apk_dir_path = sTmpDir + File.separator + "apks";
//        String apk_dir_path = orig_apk_dir_path;
        File apk_dir = new File(apk_dir_path);
        File test_apk_dir = new File(test_apk_dir_path);
        downloadFiles(apk_dir_path);
        File[] fileList = test_apk_dir.listFiles();
        //fileList = Arrays.copyOfRange(fileList,0,5);
        for(File f : fileList){
            System.out.println("Running APK: " + apk);
            apk = f.getName();
            if(apk.contains(".apk")){
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
                try{
                    mAndrolib.decodeManifestRaw(apkFile, rawmandir);
                } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for App: " + apk);
                }
                try{
                    mAndrolib.decodeManifestFull(apkFile, fullmandir, mAndrolib.getResTable(apkFile,true));
                } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for App: " + apk);
                }
                try{
                    mAndrolib.decodeResourcesRaw(new ExtFile(new File(apk_dir_path + File.separator + apk)), rawresourcesdir);
                } catch(Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    System.err.println("Failed for App: " + apk);
                }
                try{
                    apkDecoder = new ApkDecoder(new File(apk_dir_path + File.separator + apk));
                    apkDecoder.setOutDir(new File(sTmpDir + File.separator + apk + ".out"));
                    apkDecoder.setKeepBrokenResources(false);
                    apkDecoder.setBaksmaliDebugMode(false);
                    apkDecoder.setForceDelete(false);
                    apkDecoder.decode();

                    // build issue636
                    testApk = new ExtFile(sTmpDir, apk + ".out");
                    mAndRes.detectWhetherAppIsFramework(new File(sTmpDir + apk + ".out"));
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
