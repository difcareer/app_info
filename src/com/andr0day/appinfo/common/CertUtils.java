package com.andr0day.appinfo.common;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Log;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertUtils {

    private static final String DEMO = "8323820D85B99152EEFAA06A111AF02D";

    private static final String MOBILE_SAFE = "DC6DBD6E49682A57A8B82889043B93A8";

    public static final String PUB_KEY = "pubKey";

    public static final String SIGN_ALG_NAME = "signAlgName";

    public static final String SIGN_NUMBER = "signNumber";

    public static final String SUBJECT_DN = "subjectDN";

    private static final ArrayList<String> SIGNATURES = new ArrayList<String>();

    public static List<String> getSigMd5s(PackageInfo packageInfo) {
        List<String> sigs = new ArrayList<String>();
        for (Signature signature : packageInfo.signatures) {
            StringBuilder sb = new StringBuilder();
            sb.append("> md5 : \n   " + StringUtils.toHexString(md5NonE(signature.toByteArray())) + "\n");
            Map<String, String> sigInfo = getSigInfo(signature.toByteArray());
            for (String k : sigInfo.keySet()) {
                if (!PUB_KEY.equals(k) && !SIGN_NUMBER.equals(k)) {
                    sb.append("> " + k + " : \n   " + sigInfo.get(k).trim() + "\n");
                }
            }
            sigs.add(sb.toString());
        }
        return sigs;
    }

    public static boolean isDebugable(PackageInfo packageInfo) {
        boolean debuggable = false;
        X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

        try {
            Signature signatures[] = packageInfo.signatures;

            for (int i = 0; i < signatures.length; i++) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }

        } catch (Exception e) {
            //debuggable variable will remain false
        }

        return debuggable;
    }

    public static Map<String, String> getSigInfo(byte[] signature) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            map.put(PUB_KEY, cert.getPublicKey().toString());
            map.put(SIGN_ALG_NAME, cert.getSigAlgName());
            map.put(SIGN_NUMBER, cert.getSerialNumber().toString());
            map.put(SUBJECT_DN, cert.getSubjectDN().toString());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            Log.e("SIG", "signName:" + cert.getSigAlgName());
            Log.e("SIG", "pubKey:" + pubKey);
            Log.e("SIG", "signNumber:" + signNumber);
            Log.e("SIG", "subjectDN:" + cert.getSubjectDN().toString());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }


    public static final byte[] md5(byte buffer[]) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(buffer, 0, buffer.length);
        return digest.digest();
    }

    public static final byte[] md5NonE(byte buffer[]) {
        try {
            return md5(buffer);
        } catch (NoSuchAlgorithmException e) {

        }
        return new byte[0];
    }
}
