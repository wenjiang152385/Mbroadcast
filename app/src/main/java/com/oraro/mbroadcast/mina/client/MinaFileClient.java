package com.oraro.mbroadcast.mina.client;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.mina.server.IoStreamThreadWork;
import com.oraro.mbroadcast.model.MinaFileParam;
import com.oraro.mbroadcast.utils.LogUtils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.handler.stream.StreamIoHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * @author dongyu 文件传输客户端
 */
public class MinaFileClient extends StreamIoHandler {
	private  final  String TAG=this.getClass().getSimpleName();

	private MyThread mMyThread;

	public MinaFileClient(MyThread thread){
		mMyThread = thread;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		LogUtils.e(TAG,"建立链接");
	}

	@Override
	public void messageReceived(IoSession session, Object buf) {
		super.messageReceived(session, buf);
	}

	@Override
	protected void processStreamIo(IoSession session, InputStream in, OutputStream out) {
		// 客户端发送文件
		LogUtils.e(TAG,session.getId()+"  ");
		LogUtils.e(TAG, "this = " + this);
		LogUtils.e(TAG, "processStreamIo sendFile = " + mMyThread.getSendFile());
		//File sendFile = new File(Environment.getExternalStorageDirectory().toString()+"/1.xlsx");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(mMyThread.getSendFile());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtils.e(TAG,e.toString());
		}
		// 放入线程让其执行
		// 客户端一般都用一个线程实现即可 不用线程池
		IoStreamThreadWork mIoStreamThreadWork = new IoStreamThreadWork(fis,out);

		MinaFileParam mMinaFileParam = new MinaFileParam();
		mMinaFileParam.setFileSession(session);
		mIoStreamThreadWork.setMinaFileParam(mMinaFileParam);

		mIoStreamThreadWork.start();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		super.exceptionCaught(session, cause);

		LogUtils.e(TAG,cause+"  ");
		LogUtils.disposeThrowable(TAG,cause);
	}

	public void createClienStream(String ip) {
		NioSocketConnector connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
		factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);
		factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
		chain.addLast("logging", new LoggingFilter());//用于打印日志可以不写
		connector.setHandler(this);
		ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, Constants.WEB_FILE_MATCH_PORT));
		connectFuture.awaitUninterruptibly();//写上这句为了得到下面的session 意思是等待连接创建完成 让创建连接由异步变同步
		//后来表明我开始的想法不行 动态依旧不能做到
//      @SuppressWarnings("unused")
//      IoSession session = connectFuture.getSession();
//      setSession(session);
	}
}
