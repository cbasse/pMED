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
    private FTPClient mFtpClient;
    @Override
    protected Void doInBackground(String... params) {
        connnectingwithFTP("ftp.meagherlab.co", "audio@meagherlab.co", "pancakes");
        if (params[0].equals("download")) {
            downloadSingleFile("/" + params[1],new File(tabletPath + params[2]));
        } else if (params[0].equals("upload")) {
            uploadSingleFile(new File(params[1]), "/" + params[2]);

        }
        return null;
    }

    private void connnectingwithFTP(String ip, String userName, String pass) {
        boolean status = false;
        try {
            mFtpClient = new FTPClient();
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
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean downloadSingleFile(String remoteFilePath, File downloadFile) {
        OutputStream outputStream = null;
        try {
            downloadFile.createNewFile();
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    downloadFile));
            mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return mFtpClient.retrieveFile(remoteFilePath, outputStream);
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

    public void uploadSingleFile(File uploadFile,String serverFileName) {
        try {
            FileInputStream srcFileStream = new FileInputStream(uploadFile);
            boolean status = mFtpClient.storeFile(serverFileName,
                    srcFileStream);
            Log.e("Status", String.valueOf(status));
            srcFileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        System.out.println("done downloading!");
    }

    public boolean checkForAudioFileOnTablet(String filename) {
        if (new File(tabletPath + filename).exists()) {
            return true;
        } else {
            return false;
        }
    }
}
