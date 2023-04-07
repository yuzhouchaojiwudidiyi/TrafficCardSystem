package com.wellsun.trafficcardsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.decard.NDKMethod.BasicOper;
import com.wellsun.trafficcardsystem.bean.ConsumeTipBean;
import com.wellsun.trafficcardsystem.util.CRC16;
import com.wellsun.trafficcardsystem.util.L;
import com.wellsun.trafficcardsystem.util.ToastPrint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumeActivity extends BaseActivity {
    ExecutorService esRecevie = Executors.newSingleThreadExecutor();             //接收线程池
    private Socket socket;
    private InputStream is;
    private static OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;
    String ServerIP = "192.168.1.195";
    String ServerIPPort = "20000";
    private String csn;
    public static String chooseWallet = "00A40000020002";     // 选择电子钱包
    public static String readWallet = "805C000204";           // 选择电子钱包

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ConsumeServie(ConsumeTipBean consumeTipBean){
        ToastPrint.showView(consumeTipBean.getMoney());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_consume;
    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
//        ReadCardThread readCardThread = new ReadCardThread();
//        readCardThread.start();

        Intent intent = new Intent(ConsumeActivity.this, ConsumeService.class);
        startService(intent);
    }

    @Override
    public void onClick(View view) {

    }

    class ReadCardThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    //处理逻辑
                    Thread.sleep(200);
                    consume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void consume() {
        //射频复位
//        String result = BasicOper.dc_reset();
        //1.寻卡
        String r_search_card = BasicOper.dc_card_n_hex(0x01);
        L.v("寻卡=" + r_search_card);
        if (!r_search_card.startsWith("0000")) {
            ToastPrint.showText("请重新放置卡片");
            return;
        }
        csn = r_search_card.split("\\|", -1)[1].substring(0, 8);
        csn = String.format("%16s", csn).replace(" ", "0"); //前面补零到16位
        //2.卡片复位
        String r_fuWei = BasicOper.dc_pro_resethex();
        //1.判断卡是否有3f01应用
        String r_cmd_choose_3f01 = BasicOper.dc_pro_commandhex("00A40000023F01", 7);
        L.v("选中3f01=" + r_cmd_choose_3f01);
        if (r_cmd_choose_3f01.endsWith("9000")) {    //读取卡片内容
            String cardState = r_cmd_choose_3f01.split("\\|", -1)[1].substring(60, 62);
            if (cardState.equals("00")) {
                ToastPrint.showText("未初始化");
            } else if (cardState.equals("01")) {
                String r_choose_wallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
                String r_read_wallet = BasicOper.dc_pro_commandhex(readWallet, 7);
                String[] rA_readWallet = r_read_wallet.split("\\|", -1);
                String cardBalanceHex = rA_readWallet[1].substring(0, 8);
                int cardBalanceInt = Integer.parseInt(cardBalanceHex, 16);
                if (cardBalanceInt < 2) {
                    App.tts.speakText("余额不足");
                    return;
                }
                //消费
                consumeAmount();
            }

        } else {
            ToastPrint.showText("空卡");
        }

    }

    //终端机编号
    String posid = "112233445566";
    String cmdps_choose2f01 = "00A40000022f01";

    private void consumeAmount() {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString();

        //消费金额
        String amountHex = String.format("%08X", Integer.parseInt("2"));
        //2.消费初始化
        String consumeCommand = "805001020B" + "01" + amountHex + posid; //01密匙标识符
        String r_consume_init = BasicOper.dc_pro_commandhex(consumeCommand, 7);
        String r_consume_init_result = r_consume_init.split("\\|", -1)[1];
        Log.v("卡操作", "消费初始化=" + r_consume_init);
        //消费前的余额
        String balanceHex = r_consume_init_result.substring(0, 8);
        //脱机交易序号
        String cardCnt = r_consume_init_result.substring(8, 12);
        //透支额度
        String overDraw = r_consume_init_result.substring(12, 18);
        // 密钥版本号
        String keyVersion = r_consume_init_result.substring(18, 20);
        // 算法标识
        String alglndMark = r_consume_init_result.substring(20, 22);
        // 随机数
        String random = r_consume_init_result.substring(22, 30);
        Log.v("卡操作,", "balance=" + balanceHex + "  cardCnt=" + cardCnt + "  keyVersion=" + keyVersion +
                " alglndMark=" + alglndMark + "  random=" + random);

        String r_cmdps_choose2f01 = BasicOper.dc_cpuapdu_hex(cmdps_choose2f01);

        Log.v("卡操作", "r_cmdps_choose2f01=" + r_cmdps_choose2f01);
        String cmdPsamMac1 = "80700000" + "1C" + random + cardCnt + amountHex + "06" + date + keyVersion + alglndMark + csn;
        Log.v("卡操作", "psam卡求mac1指令=" + cmdPsamMac1);
        if (false) {
            return;
        }
        String r_psamMac1 = BasicOper.dc_cpuapdu_hex(cmdPsamMac1);
        Log.v("卡操作", "r_psamMac1=" + r_psamMac1);
        String r_trade_Mac1 = BasicOper.dc_cpuapdu_hex("00C0000008");
        Log.v("卡操作", "求终端交易序号和Mac1=" + r_trade_Mac1);

        String r_trade_Mac1_Rsult = r_trade_Mac1.split("\\|", -1)[1];

        String tradeNumber = r_trade_Mac1_Rsult.substring(0, 8);
        String psamMac1 = r_trade_Mac1_Rsult.substring(8, 16);
        String consumeCommand1 = "805401000F" + tradeNumber + date + psamMac1;     //tradeNumber终端序号终端机产生
        Log.v("卡操作", "消费指令=" + consumeCommand1);
        String r_consumeCommand1 = BasicOper.dc_pro_commandhex(consumeCommand1, 7);
        Log.v("卡操作", "消费r_consumeCommand1=" + r_consumeCommand1);
        String r_consumeCommand1_tac_mac2 = r_consumeCommand1.split("\\|", -1)[1].substring(0, 16);
        String tac = r_consumeCommand1_tac_mac2.substring(0, 8);
        String mac2 = r_consumeCommand1_tac_mac2.substring(8, 16);
        String veryMac2 = BasicOper.dc_cpuapdu_hex("8072000004" + mac2);

        Log.v("卡操作", "效验mac2=" + veryMac2);
        Log.v("卡操作", "消费成功");
        Log.v("卡操作", "消费成功");
        App.tts.speakText("消费成功2元");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastPrint.showView("消费金额2元");
            }
        });

    }

    private void startTcpThread() {
        esRecevie.execute(new Runnable() {
            @Override
            public void run() {
                Log.v("socket测试", "子线程socket判断" + (socket != null && socket.isConnected()) + "");

                try {
                    Log.v("socket测试", "子线程socket开始连接");
                    socket = new Socket();
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerIP, Integer.parseInt(ServerIPPort));
                    socket.connect(inetSocketAddress, 2000);
                    socket.setKeepAlive(true);
                    Log.v("socket测试", "子线程socket连接成功" + socket.isConnected());
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
//                       br = new BufferedReader(new InputStreamReader(is));
//                       bw = new BufferedWriter(new OutputStreamWriter(os));
                    reciveData();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("socket", "子线程socket连接异常=" + e.getMessage());
                    try {
                        if (socket != null)
                            socket.close();
                        if (is != null)
                            is.close();
                        if (os != null)
                            os.close();
                        if (br != null)
                            br.close();
                        if (bw != null)
                            bw.close();
                    } catch (Exception eio) {
//                            Log.v("socket测试", "流关闭异常=" + eio.getMessage());

                    }
                    socket = null;
                    is = null;
                    os = null;
                    br = null;
                    bw = null;
                    SystemClock.sleep(6000);
                    Log.v("socket", "子线程socket重新连接=" + e.getMessage());
                    startTcpThread(); //重新连接启动

                }


            }
        });
    }


    //接收tcp内容
    private byte[] bArr;

    private void reciveData() {
        boolean bReciver = true;
        while (bReciver) {
            int available = 0;
            try {
                available = is.available();
                if (available > 0) {
                    bArr = new byte[available];
                    int read = is.read(bArr);                                              //读出内容
                    String readHex = bytesToHex(bArr);                                     //字节数组转16紧着字符串
                    String CRC16Hex = CRC16.crc16(readHex.substring(0, readHex.length() - 4));  //计算crc
                    String subCrc = readHex.substring(readHex.length() - 4);
                    Log.v("数据交互", "收到数据=" + readHex);
                    if (CRC16Hex.equals(subCrc)) {
                        Log.v("数据交互", "crc验证成功");
                    } else {
                        Log.v("数据交互", "crc验证失败");
                        return;
                    }

                    String substring = readHex.substring(0, 6);   //01000d 类型
                    if (readHex.endsWith(subCrc)) {
                        Log.v("socket", "接收类型=" + substring);

                        switch (substring) {
                            case "01000D": //公钥接收
                                break;
                            case "010000": //签到
//                                sendHeartBeat();
                                break;
                            case "010002":

                                break;

                        }

                    }
                }
                SystemClock.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                if (is == null) {
                    bReciver = false;
                }
            }
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
