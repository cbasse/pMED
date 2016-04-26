package com.example.pmed.formparser;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



/**
 * Created by calebbasse on 4/25/16.
 */
public class AudioSync extends AsyncTask<String, Void, Void> {
    public static String tabletPath;

    @Override
    protected Void doInBackground(String... params) {
        connnectingwithFTP("ftp.meagherlab.co", "audio@meagherlab.co", "pancakes", params[0]);
        return null;
    }

    /**
     *
     * @param ip
     * @param userName
     * @param pass
     */
    private void connnectingwithFTP(String ip, String userName, String pass, String path) {
        boolean status = false;
        try {
            FTPClient mFtpClient = new FTPClient();
            //mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.e("isFTPConnected", String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();
                String[] mFileArray = mFtpClient.listNames();
                File f = new File(tabletPath);
                if (!f.exists()) {
                    f.mkdir();
                }
                //f = new File(path + "/AudioInterventions/" + "test");
                //f.createNewFile();
                f = new File(Environment.getExternalStorageDirectory().getPath() + "/Experiments/TestStudy/pt2.mp3");
                uploadSingleFile(mFtpClient, f, "/hellocaleb.mp3");

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ftpClient FTPclient object
     * @param remoteFilePath  FTP server file path
     * @param downloadFile   local file path where you want to save after download
     * @return status of downloaded file
     */
    public boolean downloadSingleFile(FTPClient ftpClient,
                                      String remoteFilePath, File downloadFile) {
        //File parentDir = downloadFile.getParentFile();
       // if (!parentDir.exists())
        //    parentDir.mkdir();
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    downloadFile));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     *
     * @param ftpClient FTPclient object
     * @param downloadFile local file which need to be uploaded.
     */
    public void uploadSingleFile(FTPClient ftpClient, File downloadFile,String serverFileName) {
        try {
            FileInputStream srcFileStream = new FileInputStream(downloadFile);
            boolean status = ftpClient.storeFile(serverFileName,
                    srcFileStream);
            Log.e("Status", String.valueOf(status));
            srcFileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkForAudioFileOnTablet(String filename) {
        if (new File(tabletPath + filename).exists()) {
            return true;
        } else {
            return false;
        }
    }
}
