package com.sfmap.map.demo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sfmap.api.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpUtil {

    private static boolean isSSL = true;

    public static String getJson(InputStream inputStream) {
        if (inputStream == null) return "";
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String temp = null;
        try {
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static InputStream getInputStream(String urlString, Context context) {
        if (urlString == null || "".equals(urlString)) return null;
        if(!isNetworkConnected(context))return null;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            SSLContext sslContext = null;
            if (isSSL) {
                sslContext = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                sslContext.init(null, new TrustManager[]{tm}, new SecureRandom());
            }
            if (url != null) {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (isSSL) {
                    ((HttpsURLConnection)httpURLConnection).setSSLSocketFactory(sslContext.getSocketFactory());
                }

                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 下载文件
     *
     * @param httpUrl  下载文件url
     * @param saveFile 保存文件路径
     * @return 是否下载成功
     */
    public static boolean httpDownload(String httpUrl, String saveFile, Context context, DownListener listener) {
        // 下载网络文件
        long bytesum = 0;
        int byteread = 0;
        long totalLegth=0;
        if (httpUrl == null || "".equals(httpUrl)) return false;
        if(!isNetworkConnected(context)){if(listener!=null)listener.error("网络错误");return false;}
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            SSLContext sslContext = null;
            if (isSSL) {
                sslContext = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                sslContext.init(null, new TrustManager[]{tm}, new SecureRandom());
            }
            if (url != null) {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (isSSL) {
                    ((HttpsURLConnection)httpURLConnection).setSSLSocketFactory(sslContext.getSocketFactory());
                }
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    totalLegth = httpURLConnection.getContentLength();
                    InputStream inStream  = httpURLConnection.getInputStream();

                    if (inStream != null) {
                        FileOutputStream fs = new FileOutputStream(saveFile);

                        byte[] buffer = new byte[1204];
                        if(listener!=null)listener.start();
                        while ((byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread;
                            fs.write(buffer, 0, byteread);
                            if(listener!=null)listener.down(bytesum,totalLegth);
                        }
                        if(listener!=null)listener.done(new File(saveFile).getName());

                        return true;
                    }
                }else{
                    if(listener!=null) listener.error("服务连接失败");
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(listener!=null)listener.error("文件读写错误");
        } catch (IOException e) {
            e.printStackTrace();
            if(listener!=null)listener.error("文件读写错误");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            listener.error("服务连接失败");
        } catch (KeyManagementException e) {
            e.printStackTrace();
            listener.error("服务连接失败");
        }
        return false;
    }

    /**
     * 解压zip文件
     * @param sZipPathFile
     * @param sDestPath
     * @return
     */
    public static ArrayList Ectract(String sZipPathFile, String sDestPath) {
        ArrayList<String> allFileName = new ArrayList<String>();
        try {
            // 先指定压缩档的位置和档名，建立FileInputStream对象
            FileInputStream fins = new FileInputStream(sZipPathFile);
            // 将fins传入ZipInputStream中
            ZipInputStream zins = new ZipInputStream(fins);
            ZipEntry ze = null;
            byte[] ch = new byte[256];
            while ((ze = zins.getNextEntry()) != null) {
                File zfile = new File(sDestPath + ze.getName());
                File fpath = new File(zfile.getParentFile().getPath());
                if (ze.isDirectory()) {
                    if (!zfile.exists())
                        zfile.mkdirs();
                    zins.closeEntry();
                } else {
                    if (!fpath.exists())
                        fpath.mkdirs();
                    FileOutputStream fouts = new FileOutputStream(zfile);
                    int i;
                    allFileName.add(zfile.getAbsolutePath());
                    while ((i = zins.read(ch)) != -1)
                        fouts.write(ch, 0, i);
                    zins.closeEntry();
                    fouts.close();
                }
            }
            fins.close();
            zins.close();
        } catch (Exception e) {
            System.err.println("Extract error:" + e.getMessage());
        }
        return allFileName;
    }

    public interface DownListener {
        public void start();
        public void down(long completeCode, long total);
        public void done(String fileName);
        public void error(String message);
    }
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
      * 从assets中读取txt
      */
    public static List<LatLng> readFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            List<LatLng> text = readTextFromSDcard(is);
//            textView.setText(text);
            return text;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按行读取txt
     *
     * @param is
     * @return
     * @throws Exception
     */
    private static List<LatLng> readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
//        StringBuffer buffer = new StringBuffer("");
        String str;
        List<LatLng> latLngs = new ArrayList<>();
        while ((str = bufferedReader.readLine()) != null) {
//            buffer.append(str);
//            buffer.append("\n");
            double lat = Double.valueOf(str.substring(0, str.indexOf(",")));
            double lon = Double.valueOf(str.substring(str.indexOf(",")+1, str.length()));
            LatLng latLng = new LatLng(lat, lon);
            latLngs.add(latLng);
        }
        return latLngs;
    }
}
