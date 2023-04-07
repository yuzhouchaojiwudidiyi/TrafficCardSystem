package com.wellsun.trafficcardsystem;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.decard.NDKMethod.BasicOper;
import com.wellsun.trafficcardsystem.bean.ConsumeTipBean;
import com.wellsun.trafficcardsystem.socket.SocketClient;
import com.wellsun.trafficcardsystem.util.BytesUtil;
import com.wellsun.trafficcardsystem.util.CRC16;
import com.wellsun.trafficcardsystem.util.L;
import com.wellsun.trafficcardsystem.util.ToastPrint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * date     : 2023-04-06
 * author   : ZhaoZheng
 * describe :
 */
public class ConsumeService extends Service implements SocketClient.SocketListener {
    ExecutorService pool = Executors.newFixedThreadPool(1);               //创建含有3个线程的线程池

    String ServerIP = "192.168.1.195";
    String ServerIPPort = "20000";
    private String csn;

    public static String chooseWallet = "00A40000020002";     // 选择电子钱包
    public static String readWallet = "805C000204";           // 选择电子钱包
    private SocketClient socketClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    @Override
    public void onCreate() {
        super.onCreate();
        socketClient = new SocketClient(ServerIP, ServerIPPort);
        socketClient.connect();
        socketClient.setOnConnectListener(this);
        ReadCardThread readCardThread = new ReadCardThread();
        readCardThread.start();

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
//            ToastPrint.showText("请重新放置卡片");
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
                consumeAmount(r_cmd_choose_3f01.split("\\|", -1)[1].substring(68, 84));
            }

        } else {
            ToastPrint.showText("空卡");
        }

    }


    //终端机编号
    String posid = "112233445566";
    String cmdps_choose2f01 = "00A40000022f01";

    private void consumeAmount(String cardNumber) {
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

        EventBus.getDefault().post(new ConsumeTipBean("消费2元"));

        String cosnsume = "010004886020913030303030303030303030303030303030303030010C214090000605000007700005F088602091C7A3A54B214000000150442809012140900006050000012023030708312600000770000027051E0000001E0000005A18000004E921400075C0A8C2B10100580100000000000000000000000000000000000000000000000000023030303030303030303000000000214001000000000000000000010000000500000000000000000000000000000002000000000000000000000000000001000000000000000000000000000004BFFFFFFD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        StringBuilder sb = new StringBuilder(cosnsume);
        sb.replace(100, 116, cardNumber); //卡号
        sb.replace(138, 152, date); //日期
        sb.replace(168, 176, amountHex); //消费金额
        sb.replace(184, 192, balanceHex); //卡交易前余额
        sb.replace(192, 196, cardCnt); //卡片交易计数器
        sb.replace(200, 208, csn.substring(8, 16)); //物理卡号
        cosnsume = sb.toString();
        String crc16 = CRC16.crc16(cosnsume);
        socketClient.send(cosnsume + crc16);


    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceived(String message) {

    }

    @Override
    public void onSent(String message) {

    }
}
