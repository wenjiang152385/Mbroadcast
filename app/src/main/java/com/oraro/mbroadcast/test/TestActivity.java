package com.oraro.mbroadcast.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.ui.activity.ExcelActivity;

import org.apache.mina.core.service.IoAcceptor;

public class TestActivity extends Activity {
    // 端口号，要求客户端与服务器端一致
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ((Button)findViewById(R.id.wy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this,ExcelActivity.class));
//                Log.e("wy","onclick");
//                PlayEntry playEntry = new PlayEntry();
//                playEntry.setTextDesc("feijiasjdjajflsjsjgl");
//                MinaThread mThread = new MinaThread(Constants.instructionPlay);
//                mThread.start();
//                TTSInterface tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
//                //执行TTS逻辑
//                tts.TTSStartPlay("jfgjlk.fff.fff.fff.", null, null, null, null, null, null, null);

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        int port = Constants.WEB_MATCH_PORT;
//                        String local = "192.168.1.102";
//                        NioSocketConnector connector = new NioSocketConnector();
//                        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
//                        ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
//                        factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);
//                        factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
//                        chain.addLast("logging", new LoggingFilter());// 用于打印日志可以不写
//                        connector.setHandler(new MinaFileClient());
//                        ConnectFuture connectFuture = connector.connect(new InetSocketAddress(local, port));
//                        connectFuture.awaitUninterruptibly();// 写上这句为了得到下面的session 意思是等待连接创建完成
//                        Log.e("exceptionCaught","createClienStream  ");
//                    }
//                }).start();
            }
        });
        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("wy","onclick");
                IoAcceptor acceptor = null;
//                try {
//                    // 创建一个非阻塞的server端的Socket
//                    if(null==acceptor){
//                        acceptor = new NioSocketAcceptor();
//                        // 设置过滤器（使用mina提供的文本换行符编解码器）
//                        acceptor.getFilterChain().addLast("codec",
//                                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
//                                        LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));
//                        // 自定义的编解码器
//                        // acceptor.getFilterChain().addLast("codec", new
//                        // ProtocolCodecFilter(new CharsetCodecFactory()));
//                        // 设置读取数据的换从区大小
//                        acceptor.getSessionConfig().setReadBufferSize(2048);
//                        // 读写通道10秒内无操作进入空闲状态
//                        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
//                        // 为接收器设置管理服务
//                        acceptor.setHandler(new MinaStringServerHandler());
//                        // 绑定端口
//                        acceptor.bind(new InetSocketAddress(PORT));
//                        System.out.println("服务器启动成功... 端口号未：" + PORT);
//                    }
//
//
//                } catch (Exception e) {
//                    System.out.println("服务器启动异常...");
//                    e.printStackTrace();
//                }

            }
        });
    }
}
