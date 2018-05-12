package com.cy.obdproject.net;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;

public class CyfMsgClient extends WebSocketClient {

    public CyfMsgClient(URI serverURI) {
        super(serverURI, new Draft_17());
    }

    public CyfMsgClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
    }

    public void sendMsg(String text) throws NotYetConnectedException {
        //write a message
        send(text);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("CyfMsgClient", "onOpen");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }


    @Override
    public void onMessage(String message) {
        Log.e("CyfMsgClient", "onMessage " + message);
        message.matches("/^id: (.*)\n(content-type: (.*)\n)?\n/m");
    }

   /* @Override
    public void onFragment( Framedata fragment ) {
        System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
    }*/

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("CyfMsgClient", "onClose " + reason);
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("cyf Connection closed by " + (remote ? "remote peer" : "us"));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        Log.e("CyfMsgClient", "onError " + ex.getMessage());
        // if the error is fatal then onClose will be called additionally
    }
}