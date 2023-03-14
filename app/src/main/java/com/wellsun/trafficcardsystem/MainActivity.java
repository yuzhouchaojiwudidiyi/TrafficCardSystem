package com.wellsun.trafficcardsystem;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.decard.NDKMethod.BasicOper;
import com.wellsun.trafficcardsystem.bean.SignBean;
import com.wellsun.trafficcardsystem.pboc.PBOCUtil;
import com.wellsun.trafficcardsystem.util.BytesUtil;
import com.wellsun.trafficcardsystem.util.CRC16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

    private android.widget.TextView tvReadcardState;
    private android.widget.TextView tvBalance;
    private android.widget.EditText etAmount;
    private android.widget.Button btReadBalance;
    private android.widget.Button btRecharge;
    private android.widget.Button btConsume;
    private android.widget.TextView tvContent;
    ExecutorService esRecevie = Executors.newSingleThreadExecutor();             //接收线程池
    ExecutorService esSend = Executors.newSingleThreadExecutor();                //上传发送线程池
    private Socket socket;
    private InputStream is;
    private static OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;
    String ServerIP = "192.168.1.59";
    String ServerIPPort = "20000";


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        boolean bReadCardState = D8.connectD8(mContext);
        tvReadcardState = (TextView) findViewById(R.id.tv_readcard_state);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
        etAmount = (EditText) findViewById(R.id.et_amount);
        btReadBalance = (Button) findViewById(R.id.bt_read_balance);
        btRecharge = (Button) findViewById(R.id.bt_recharge);
        btConsume = (Button) findViewById(R.id.bt_consume);
        tvReadcardState.setText("读卡器状态: " + bReadCardState);

        try {
            String des = PBOCUtil.getDisperseKeyOnce("1234567890123456", "3F013F013F013F013F013F013F013F01");
            System.out.println("分散密匙是"+des);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        startTcpThread();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_read_balance:
                Log.v("卡操作", "读取余额");
                String balance = D8.readBalance();
                tvBalance.setText(balance);
                App.tts.speakText("余额:" + balance + "元");
                break;
            case R.id.bt_recharge:
                //读取余额
                tvBalance.setText(D8.readBalance());
                try {
                    reacharge();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("卡操作", "异常=" + e.getMessage());
                }
                break;
            case R.id.bt_consume:
                //读取余额
                tvBalance.setText(D8.readBalance());
                try {
                    consume();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("卡操作", "异常=" + e.getMessage());
                }
                break;
            case R.id.bt_socket:
                startTcpThread();
                break;
            case R.id.bt_sign:
                sign();
                break;
        }
    }

    private void sign() {
        SignBean signBean = new SignBean();
        String yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        signBean.setPosDateTime6(yyyyMMddHHmmss); //时间
        String signBeanStr = signBean.getString();
        String crc16 = CRC16.crc16(signBeanStr);

        if (socket == null || !socket.isConnected()) { //socket没连接不往下执行
            Log.v("数据交互", "socket连接失败");
            return;
        }
          String data="20230314092427000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000022023030811223300000001000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        esSend.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("数据交互", "上传签到数据="+signBeanStr+crc16);
                    byte[] qrByte = BytesUtil.hexString2Bytes(signBeanStr+crc16);
                    os.write(qrByte);
                    os.flush();
                } catch (Exception e) {
                    Log.v("socket", "上传数据失败=" + e + e.getMessage());

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
                        Log.v("socket", "main网络提示流关闭异常=" + eio.getMessage());

                    }
                    Log.v("socket", "main网络提示流关闭成功 并重新连接");
                    socket = null;
                    is = null;
                    os = null;
                    br = null;
                    bw = null;
                    startTcpThread();


                }
            }
        });


    }

    private void startTcpThread() {
        esRecevie.execute(new Runnable() {
            @Override
            public void run() {
                Log.v("socket测试", "子线程socket判断" + (socket != null && socket.isConnected()) + "");
                if (socket == null || socket.isConnected()) {
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

                } else {
                    Log.v("socket", "socket判断已连接");
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
                    String CRC16Hex = CRC16.crc16(readHex.substring(0,readHex.length()-4));  //计算crc
                    String subCrc = readHex.substring(readHex.length() - 4);
                    if (CRC16Hex.equals(subCrc)){
                        Log.v("数据交互","crc验证成功");
                    }else {
                        Log.v("数据交互","crc验证失败");
                        return;
                    }

                    String substring = readHex.substring(0, 6);   //01000d 类型
                    if (readHex.endsWith(subCrc)) {
                        Log.v("socket", "接收类型=" + substring);

                        switch (substring) {
                            case "01000D": //公钥接收

                            case "010000": //签到
                                //01000001886020910019000000000000000000000000000000000000000000000000002611
                                Log.v("socket签到结果", readHex);
                                //开启心跳模式
//                                sendHeartBeat();
                                break;
                            case "010002":
//                                Log.v("心跳ocket","接收到心跳返回内容="+readHex);
                                break;

                        }

                    }
                }
                SystemClock.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                bReciver = false;
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















    private void consume() throws Exception {
        //读取余额
        tvBalance.setText(D8.readBalance());
        //1.选择电子钱包
        String r_choose_wallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
        String amountString = etAmount.getText().toString().trim();
        App.tts.speakText("消费金额:" + amountString + "元");
        //消费金额
        String amountHex = String.format("%08X", Integer.parseInt(amountString));
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

        //求过程密匙sessionKey
        String inputData = random + cardCnt + "0001";   //0001为终端机交易序号后四位
        String sessionKey = PBOCUtil.getDes(inputData, consumeKey);
        Log.v("卡操作", "inputData=" + inputData);
        Log.v("卡操作", "sessionKey=" + sessionKey);
        String input1_mac1 = amountHex + consumeType + posid + date;
        Log.v("卡操作", "求mac1指令=" + input1_mac1);
        String computer_mac1 = PBOCUtil.getMac2(input1_mac1, sessionKey, "0000000000000000").substring(0, 8);
        Log.v("卡操作", "computer_mac1=" + computer_mac1);
        String consumeCommand1 = "805401000F" + "00000001" + date + computer_mac1;  //00000001终端序号终端机产生
        Log.v("卡操作", "消费指令=" + consumeCommand1);
        String r_consumeCommand1 = BasicOper.dc_pro_commandhex(consumeCommand1, 7);
        Log.v("卡操作", "消费r_consumeCommand1=" + r_consumeCommand1);
        String r_consumeCommand1_tac_mac2 = r_consumeCommand1.split("\\|", -1)[1].substring(0, 16);
        String tac = r_consumeCommand1_tac_mac2.substring(0, 8);
        String mac2 = r_consumeCommand1_tac_mac2.substring(8, 16);
        //效验mac2
        String inputData1 = amountHex;
        String veryMac2 = PBOCUtil.getMac2(inputData1, sessionKey, "0000000000000000").substring(0, 8);
        if (veryMac2.equals(mac2)) {
            Log.v("卡操作", "mac2效验成功");
        } else {
            Log.v("卡操作", "mac2效验失败");
        }

        //效验tac码
        //计算xor
        byte[] bytesConsume1 = PBOCUtil.hexString2byte(consumeKey.substring(0, 16));
        byte[] bytesConsume2 = PBOCUtil.hexString2byte(consumeKey.substring(16, 32));
        byte[] bytesXor = PBOCUtil.xOr(bytesConsume1, bytesConsume2);
        String stringXor = PBOCUtil.byte2hexString(bytesXor);

        String veryTacCommand = amountHex + consumeType + posid + "00000001" + date; //0001为终端机交易序号后四位 前面补齐共8位
        String veryTac = PBOCUtil.getMac2(veryTacCommand, stringXor, "0000000000000000").substring(0, 8);
        if (tac.equals(veryTac)) {
            Log.v("卡操作", "tac2效验成功");
            App.tts.speakText("消费成功:");
        } else {
            Log.v("卡操作", "tac2效验失败");
            App.tts.speakText("消费失败:");
        }
    }


    // 圈存的key
    String rechargeKey = "3F013F013F013F013F013F013F013F01";
    // 消费密匙
    String consumeKey = "3E013E013E013E013E013E013E013E01";
    // 验证tac的key 内部密匙
    String tacKey = "34343434343434343434343434343434";
    //终端机编号
    String posid = "112233445566";
    // 交易类型
    String tradeType = "02";
    String consumeType = "06";//06普通消费 09复合消费
    // 指令
    public String chooseWallet = "00A40000020002";     // 选择电子钱包
    public String pin = "002000000812345FFFFFFFFFFF";  // 验证口令
    String date = "20230308112233";

    private void reacharge() throws Exception {
        //1.选择电子钱包
        String r_choose_wallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
        //2.验证口令
        String r_pin = BasicOper.dc_pro_commandhex(pin, 7);
        //3.圈存初始化
        String amountString = etAmount.getText().toString().trim();
        App.tts.speakText("充值金额:" + amountString + "元");
        //充值金额
        String amountHex = String.format("%08X", Integer.parseInt(amountString));
        Log.v("卡操作", "充值16进制金额:" + amountHex);
        String recharge_init = "805000020b01" + amountHex + posid;     // 充值初始化
        Log.v("卡操作", "预充值指令:" + recharge_init);
        String r_recharge_init = BasicOper.dc_pro_commandhex(recharge_init, 7);
        String r_recharge_init_result = r_recharge_init.split("\\|", -1)[1];
        // 卡余额
        String balance = r_recharge_init_result.substring(0, 8);
        // 联机计数器
        String cardCnt = r_recharge_init_result.substring(8, 12);
        // 密钥版本
        String keyVersion = r_recharge_init_result.substring(12, 14);
        // 算法标识
        String alglndMark = r_recharge_init_result.substring(14, 16);
        // 随机数
        String random = r_recharge_init_result.substring(16, 24);
        // mac1
        String mac1 = r_recharge_init_result.substring(24, 32);//mac1
        Log.v("卡操作,", "balance=" + balance + "  cardCnt=" + cardCnt + "  keyVersion=" + keyVersion +
                " alglndMark=" + alglndMark + "  random=" + random + "  mac1=" + mac1);

        String inputData = random + cardCnt + "8000";
        String sessionKey = PBOCUtil.getDes(inputData, rechargeKey);
        Log.v("卡操作", "inputData:" + inputData + "   sessionKey=" + sessionKey);

        String input1_mac1 = balance + amountHex + tradeType + posid;
        String computer_mac1 = PBOCUtil.getMac2(input1_mac1, sessionKey, "0000000000000000").substring(0, 8);
        Log.v("卡操作", "计算mac1指令:" + input1_mac1);
        Log.v("卡操作", "计算mac1=:" + computer_mac1);

        if (computer_mac1.equals(mac1)) {
            Log.v("卡操作", "验证mac1成功:");
        } else {
            Log.v("卡操作", "验证mac1失败:");
            return;
        }

        String input2 = amountHex + tradeType + posid + date;
        String mac2 = PBOCUtil.getMac2(input2, sessionKey, "0000000000000000").substring(0, 8);
        Log.v("卡操作", "sessionKey=" + sessionKey + "   input2=" + input2 + "   mac2=" + mac2);
        String recharge = "805200000B" + date + mac2;
        Log.v("卡操作", "预充值指令recharge:" + recharge);

        String r_recharge_tag = BasicOper.dc_pro_commandhex(recharge, 7);
        Log.v("卡操作", "充值结果:" + r_recharge_tag);
        String r_recharge_tag_result = r_recharge_tag.split("\\|", -1)[1].substring(0, 8);

        byte[] bytesTac1 = PBOCUtil.hexString2byte(tacKey.substring(0, 16));
        byte[] bytesTac2 = PBOCUtil.hexString2byte(tacKey.substring(16, 32));
        byte[] bytesXor = PBOCUtil.xOr(bytesTac1, bytesTac2);
        String stringXor = PBOCUtil.byte2hexString(bytesXor);
        Log.v("卡操作", "stringXor:" + stringXor);

        int balanceInt = Integer.parseInt(balance, 16); //余额
        int amountInt = Integer.parseInt(amountString);
        String totalHex = String.format("%08X", balanceInt + amountInt);

        Log.v("卡操作", "金额:" + "balanceInt=" + balanceInt + "  amountInt=" + amountInt +
                "  totalHex:" + totalHex);

        //充值余额  交易序号 充值金额  终端机号 交易日期
        String comandVerTac = totalHex + cardCnt + amountHex + tradeType + posid + date;
        Log.v("卡操作", "验证tac指令:" + comandVerTac);
        String veryTac = PBOCUtil.getMac2(comandVerTac, stringXor, "0000000000000000").substring(0, 8);
        Log.v("卡操作", "验证tac结果:" + veryTac);
        if (r_recharge_tag_result.equals(veryTac)) {
            Log.v("卡操作", "验证tac成功");
            App.tts.speakText("充值成功:");
        } else {
            Log.v("卡操作", "验证tac失败");
            App.tts.speakText("充值失败:");
        }
    }
}
