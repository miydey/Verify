package com.chuangber.verify.net;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WSTest {
	private static final String TAG = WSTest.class.getSimpleName();
	public WebSocketClient client;
	public WSTest() throws URISyntaxException {
		client = new WebSocketClient(new URI(
				"ws://www.chuangber.cn/machine_online/"
						+ "22c2fb208b9ad1e555ca3edaa6aad156/8ac94e2d32d9cf65a74de5033919244c")) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {

			}

			@Override
			public void onMessage(String s) {
				Log.e(TAG, s );
			}

			@Override
			public void onClose(int i, String s, boolean b) {

			}

			@Override
			public void onError(Exception e) {

			}
		};
	}


//	public static void main(String[] args) throws URISyntaxException {
//		WebSocketClient client = new WebSocketClient(
//				new URI(
//						"ws://www.chuangber.cn/machine_online/"
//						+ "22c2fb208b9ad1e555ca3edaa6aad156/8ac94e2d32d9cf65a74de5033919244c")) {
////						"ws://192.168.0.234/machine_online/"
////								+ "739543935d3908542dcb82c664718724/44d3df8331a5b2d269f64fbd7f9ad08c")) {
//
//			/**
//			 * 打开连接
//			 */
//			@Override
//			public void onOpen(ServerHandshake arg0) {
//				System.out.println("open");
//			}
//
//			/**
//			 * 收到消息
//			 */
//			@Override
//			public void onMessage(String arg0) {
//				System.out.println(arg0);
//			}
//
//			/**
//			 * 发生异常
//			 */
//			@Override
//			public void onError(Exception arg0) {
//				arg0.printStackTrace();
//
//			}
//
//			/**
//			 * 关闭连接
//			 */
//			@Override
//			public void onClose(int arg0, String arg1, boolean arg2) {
//				System.out.println("close");
//			}
//		};
//		client.connect();
//		while (!client.getReadyState().equals(READYSTATE.OPEN)) {
//			System.out.println("还没有打开");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//		}
//		System.out.println("打开了连接");
//		client.send("739543935d3908542dcb82c66471872444d3df8331a5b2d269f64fbd7f9ad08c");
//		System.out.println("after send");
//	}
}
