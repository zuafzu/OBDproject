package com.cy.obdproject.socket;

public class WebSocketConfig {

    /**
     * 设置wss
     *
     * @param webSocketClient
     */
    public static void wssConfig(MyWebSocketClient webSocketClient) {
//        SSLContext sslContext = null;
//        try {
//            sslContext = SSLContext.getInstance("TLS");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            sslContext.init(null, new TrustManager[]{
//                    new X509TrustManager() {
//
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return new java.security.cert.X509Certificate[0];
//                        }
//
//
//                    }
//            }, new SecureRandom());
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//        SSLSocketFactory factory = sslContext.getSocketFactory();
//        try {
//            webSocketClient.setSocket(factory.createSocket());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
