package com.wellsun.trafficcardsystem;

import android.content.Intent;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.decard.NDKMethod.BasicOper;
import com.wellsun.trafficcardsystem.bean.HeartBeatBean;
import com.wellsun.trafficcardsystem.bean.SignBean;
import com.wellsun.trafficcardsystem.pboc.PBOCUtil;
import com.wellsun.trafficcardsystem.util.BytesUtil;
import com.wellsun.trafficcardsystem.util.CRC16;
import com.wellsun.trafficcardsystem.util.L;
import com.wellsun.trafficcardsystem.util.ToastPrint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    ScheduledExecutorService esTime = Executors.newScheduledThreadPool(3); //定时线程池

    ExecutorService pool = Executors.newFixedThreadPool(2);               //创建含有3个线程的线程池
    private Socket socket;
    private InputStream is;
    private static OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;
    String ServerIP = "192.168.1.195";
    String ServerIPPort = "20000";
    String result = "开始:";
    private TextView tvResult;

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
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvReadcardState.setText("读卡器状态: " + bReadCardState);

        Intent intent = new Intent(mContext,ConsumeActivity.class);
        startActivity(intent);

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
//        startTcpThread();

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
                try {
                    try {
                        consumePsam();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            case R.id.bt_send_cpucard:
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendCpuCard();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            case R.id.bt_change_bin_file:
                changeBinFile();

                break;
            case R.id.bt_send_psamcard:
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendPsamCard();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                break;
            case R.id.bt_heart:
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
//                        sendHeartBeat();
                        HeartBeatBean heartBeatBean = new HeartBeatBean();
                        String yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        heartBeatBean.setPosDateTime6(yyyyMMddHHmmss); //时间
                        String heartBeat = heartBeatBean.getHeartBeat();
                        String crc16 = CRC16.crc16(heartBeat);
                        String data = heartBeat + crc16;
                        byte[] keyByte = BytesUtil.hexString2Bytes(data);
                        Log.v("数据交互", "心跳=" + data);

                        try {
                            os.write(keyByte);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.v("socket", "异常=" + e.getMessage());
                        }
                    }

                });
                break;
            case R.id.bt_upload:
                String data1 = "010004886020913030303030303030303030303030303030303030010C214090000605000007700005F088602091C7A3A54B214000000150442809012140900006050000012023030708312600000770000027051E0000001E0000005A18000004E921400075C0A8C2B10100580100000000000000000000000000000000000000000000000000023030303030303030303000000000214001000000000000000000010000000500000000000000000000000000000002000000000000000000000000000001000000000000000000000000000004BFFFFFFD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
                String data2 = "010004886020913030303030303030303030303030303030303030010C214090000605000007710005F088602091053CFBA9214000000150352409012140900006050000012023031615154800000771000027061E0000001E0000005C1C0000026821400075C0E1C2B10100580100000000000000000000000000000000000000000000000000023030303030303030303000000000214001000000000000000000010000000500000000000000000000000000000002000000000000000000000000000001000000000000000000000000000004BFFFFFFD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
                String crc16 = CRC16.crc16(data1);

                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        byte[] qrByte = BytesUtil.hexString2Bytes(data1 + crc16);
                        try {
                            os.write(qrByte);
                            os.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                break;
            case R.id.bt_other:
                String r_xunKa = BasicOper.dc_card_hex(0x01);
                String r_fuWei = BasicOper.dc_pro_resethex();
                String r_choose_3f01 = BasicOper.dc_pro_commandhex("00B0850000", 7);
               Log.v("读00005文件",r_choose_3f01);
                break;
        }
    }

    String cmd_read_0005 = "00B0850000";  //1e h=30字节大小
    String cmd_des_init = "801A260408"; //des初始化
    String cmd_des_ep = "801A0000"; //des加密
    String cmd_random4 = "0084000004"; //取随机数四个字节
    String cmd_request_4byte = "00C0000004"; //去返回的8字节数
    String cmd_write_0005 = "04D6850022"; //去写0005文件

    private void changeBinFile() {
        String r_search_card = BasicOper.dc_card_n_hex(0x01);
        Log.v("卡操作", r_search_card);
        String[] rA_r_xunKa = r_search_card.split("\\|", -1);
        csn = rA_r_xunKa[1].substring(0, 8);
        csn = String.format("%16s", csn).replace(" ", "0"); //前面补零到16位
        Log.v("查错", "寻卡结果=" + r_search_card + "     csn=" + csn);
        result = "物理卡号:" + r_search_card + "\n" + result;
        String r_fuWei = BasicOper.dc_pro_resethex();

        //1.选择CPU卡根目录
        String r_cmd_choose_3f00 = BasicOper.dc_pro_commandhex(cmd_choose_3f00, 7);
        String r_cmd_read_0005 = BasicOper.dc_pro_commandhex(cmd_read_0005, 7);
        result = "读0015文件=" + r_cmd_read_0005 + "\n" + result;
        Log.v("卡操作", "0015=" + r_cmd_read_0005);
        tvResult.setText(result);
        String data0015 = r_cmd_read_0005.split("\\|", -1)[1];
        data0015 = data0015.substring(0, data0015.length() - 4);
        result = "读0005结果" + data0015 + "\n" + result;
        //2.取随机数
        String random4 = BasicOper.dc_pro_commandhex(cmd_random4, 7).split("\\|", -1)[1].substring(0, 8);
        //3.psam卡计算mac  进入psam应用
        String r_cmdps_choose2f01 = BasicOper.dc_cpuapdu_hex(cmdps_choose2f01);
        //4.DES初始化
        String r_cmd_des_init = BasicOper.dc_cpuapdu_hex(cmd_des_init + csn);
        //5.des计算
        String content = "123111111111111111112222222222222222222233333333333333333333";
        String cmd_request_mac = "80FA0500" + "30" + random4 + "00000000" + "04D68500" + "22" + content + "8000000000";
        String r_cmd_request_mac = BasicOper.dc_cpuapdu_hex(cmd_request_mac);
        Log.v("卡操作", "求mac结果:" + r_cmd_request_mac);
        result = "求mac结果" + r_cmd_request_mac + "\n" + result;
        String r_cmd_request_4byte = BasicOper.dc_cpuapdu_hex(cmd_request_4byte);
        Log.v("卡操作", "求mac:" + r_cmd_request_4byte);
        String lineMac = r_cmd_request_4byte.split("\\|", -1)[1].substring(0, 8);
        //6.去修改线路内容
        Log.v("卡操作","写0005文件内容"+cmd_write_0005+content+lineMac);
        String r_cmd_write_0005 = BasicOper.dc_pro_commandhex(cmd_write_0005+content+lineMac, 7);
        Log.v("卡操作","写0005文件结果"+r_cmd_write_0005);
        result = "写0005文件结果" + r_cmd_write_0005 + "\n" + result;
        tvResult.setText(result);
        App.tts.speakText("线路保护文件0005更新成功");

    }


    //发psam卡
    public String cmd_getpasmid = "112233445566";                    //获取终端机号
    public String cmdPs_reset = "800E000008FFFFFFFFFFFFFFFF";          //清空psam卡
    public String cmdPs_creadMf = "80E0000018FFFFFFFFFFFFFFFF0F01315041592E5359532E4444463031";          //创建mf文件
    public String cmdPs_cread0016 = "80E00200070016000F0F0006";          //创建16文件
    public String cmdPs_write0016 = "00D6960006112233445566";              //写0016文件 终端机编号
    public String cmdPs_creat2f01 = "80E00100092F010F002222222222";      //创建2f01文件应用
    public String cmdPs_creatkey = "80E00200070000050F003018";           //创建0000密匙文件
    public String cmdPs_addcosumekey = "80D40000170001220F030FFF3E013E013E013E013E013E013E013E01"; //添加消费密匙  (00版本号  01算法标识  22密匙用途)
    public String cmdPs_addlineKey = "80D40000170400260F030FFF36363636363636363636363636363636"; //添加线路保护密匙  (04版本号 00算法标识 26密匙用途)
    public String cmdPs_addlineEpKey = "80D40000170400280F030FFF36363636363636363636363636363636";//添加线路保护密匙加密密匙  (04版本号  00算法标识  28密匙用途) 添加08 MAC、加密密钥
    public String cmdPs_creat0018 = "80E00200070018000F0F0004";//创建0018二进制文件
    public String cmdPs_creat2f01_finish = "80E00101022F01";//创建2f01结束
    public String cmdPs_creat3f00_finish = "80E00101023F00";//创建3f01结束

    private void sendPsamCard() {
        String r_cmdPs_reset = BasicOper.dc_cpuapdu_hex(cmdPs_reset);
        result = "清除psam" + r_cmdPs_reset + "\n" + result;
        String r_cmdPs_creadMf = BasicOper.dc_cpuapdu_hex(cmdPs_creadMf);
        result = "创建mf" + r_cmdPs_creadMf + "\n" + result;
        String r_cmdPs_cread0016 = BasicOper.dc_cpuapdu_hex(cmdPs_cread0016);
        result = "创建0016" + r_cmdPs_cread0016 + "\n" + result;
        String r_cmdPs_write0016 = BasicOper.dc_cpuapdu_hex(cmdPs_write0016);
        result = "更新0016" + r_cmdPs_write0016 + "\n" + result;
        String r_cmdPs_creat2f01 = BasicOper.dc_cpuapdu_hex(cmdPs_creat2f01);
        result = "创建2f01" + r_cmdPs_creat2f01 + "\n" + result;
        String r_cmdPs_creatkey = BasicOper.dc_cpuapdu_hex(cmdPs_creatkey);
        result = "创建key文件" + r_cmdPs_creatkey + "\n" + result;
        String r_cmdPs_addcosumekey = BasicOper.dc_cpuapdu_hex(cmdPs_addcosumekey);
        result = "添加消费密匙" + r_cmdPs_addcosumekey + "\n" + result;
        String r_cmdPs_addlineKey = BasicOper.dc_cpuapdu_hex(cmdPs_addlineKey);
        result = "添加线路保护密匙" + r_cmdPs_addlineKey + "\n" + result;
        String r_cmdPs_addlineEpKey = BasicOper.dc_cpuapdu_hex(cmdPs_addlineEpKey);
        result = "添加线路保护密匙和加密" + r_cmdPs_addlineEpKey + "\n" + result;
        String r_cmdPs_creat0018 = BasicOper.dc_cpuapdu_hex(cmdPs_creat0018);
        result = "创建0018" + r_cmdPs_creat0018 + "\n" + result;
        String r_cmdPs_creat2f01_finish = BasicOper.dc_cpuapdu_hex(cmdPs_creat2f01_finish);
        result = "创建结束2f01" + r_cmdPs_creat2f01_finish + "\n" + result;
        String r_cmdPs_creat3f00_finish = BasicOper.dc_cpuapdu_hex(cmdPs_creat3f00_finish);
        result = "创建结束3f01" + r_cmdPs_creat3f00_finish + "\n" + result;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(result);
            }
        });
        App.tts.speakText(" psam卡发卡完成");
    }

    //发卡
    String cmd_random = "0084000008"; //取随机数
    String outsideKey = "FFFFFFFFFFFFFFFF"; //默认外部认证秘钥
    String cmd_outsideVertiy = "0082000008"; //外部认证
    String cmd_erase = "800E000000"; //擦除卡片
    String cmd_choose_3f00 = "00A40000023F00"; //选中根目录
    String cmd_create_mf0000 = "80E00000073F005001F0FFFF";//创建根目录密匙文件0000
    //    String cmd_add_mfLineProtectKey = "80D401000D36F0F0FF33FFFFFFFFFFFFFFFF";//添加线路保护密匙
    String cmd_add_mfLineProtectKey = "80D401000D36F0F0FF3336363636363636363636363636363636";//添加线路保护密匙
    String cmd_add_mfoutsideKey = "80D401001539F0F0AA33FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";//添加外部保护密匙
    String cmd_create_mf0001 = "80E00001072A0213F000FFFF";//创建0001定长文件
    String cmd_add_mf0001_data = "00E2000C1361114F09A00000000386980701500450424F43";//添加定长记录文件 一条数据
    String cmd_create_mf0005 = "80E0000507A80030F0F0FFFF";//创建0005线路保护文件
    String cmd_create_3f01 = "80E03F011138036FF0F095FFFFA00000000386980701";//创建3f01文件 应用
    String cmd_choose_3f01 = "00A4040009A00000000386980701";//选中3f01文件 应用

    String cmd_creat_0000 = "80E00000073F018F95F0FFFF";//创建密匙文件
    String cmd_add_insideKey = "80D401001534F002000134343434343434343434343434343434";//添加内部密匙 tac key
    String cmd_add_lineProtectKey = "80D401001536F002FF3336363636363636363636363636363636";//添加线路保护密匙
    String cmd_add_pinUnlockKey = "80D401001537F002FF3337373737373737373737373737373737";//添加口令解锁密匙
    String cmd_add_pinResetKey = "80D401001538F002FF3338383838383838383838383838383838";//添加口令重装密匙
    String cmd_add_outsideKey = "80D401001539F002443339393939393939393939393939393939";//添加外部认证密匙
    String cmd_add_consumeKey = "80D40101153EF00200013E013E013E013E013E013E013E013E01";//添加消费密匙
    String cmd_add_rechargeKey = "80D40101153FF00200013F013F013F013F013F013F013F013F01";//添加充值密匙
    String cmd_add_takeOutKey = "80D40101153DF00201003D013D013D013D013D013D013D013D01";//添加圈提密匙
    String cmd_add_overDrawKey = "80D40101153CF00201003C013C013C013C013C013C013C013C01";//添加修改透支额度密匙
    String cmd_add_pinKey = "80D401000D3AF0EF013312345FFFFFFFFFFF";//添加Pin

    String cmd_creat_0015 = "80E0001507A8001EF0F0FFFF";//创建0015二进制文件  mac线路保护
    String cmd_creat_0017 = "80E0001707280007F0F0FFFF";//创建0017二进制文件
    String cmd_creat_0018 = "80E00018072E0A17F0EFFFFF";//创建0018循环记录文件文件
    String cmd_creat_0002 = "80E00002072F0208F000FF18";//创建电子钱包应用
    String cmd_creat_001A = "80E0001A072C0018F0F0FFFF";//创建复合交易文件
    String cmd_creat_001A_data = "00E200D405BB03112233";//添加复合交易文件数据
    String cmd_creat_0001 = "80E00001072F0208F100FF18";//创建电子存折 0001
    String csn = "";

    private void sendCpuCard() throws Exception {
        String r_search_card = BasicOper.dc_card_n_hex(0x01);
        Log.v("卡操作", r_search_card);
        String[] rA_r_xunKa = r_search_card.split("\\|", -1);
        csn = rA_r_xunKa[1].substring(0, 8);
        csn = String.format("%16s", csn).replace(" ", "0"); //前面补零到16位
        Log.v("查错", "寻卡结果=" + r_search_card + "     csn=" + csn);
        result = "物理卡号:" + r_search_card + "\n" + result;
        String r_fuWei = BasicOper.dc_pro_resethex();
        //1.取随机数
        String r_randomCommand = BasicOper.dc_pro_commandhex(cmd_random, 7);
        String[] rA_randomCommand = r_randomCommand.split("\\|", -1);
        String rA_randomCommand1 = rA_randomCommand[1].substring(0, 16);
        //2.随机数加密
        String outsideVerify = PBOCUtil.getDes(rA_randomCommand1, outsideKey);
        //3.外部认证
        String r_outsideVertiyCommand = BasicOper.dc_pro_commandhex(cmd_outsideVertiy + outsideVerify, 7);
        result = "外部认证:" + r_outsideVertiyCommand + "\n" + result;
        //4.擦除卡片
        String r_eraseCommand = BasicOper.dc_pro_commandhex(cmd_erase, 7);
        result = "卡片擦除:" + r_eraseCommand + "\n" + result;
        //5.选中根目录
        String r_cmd_choose_3f00 = BasicOper.dc_pro_commandhex(cmd_choose_3f00, 7);
        //6.创建密匙文件
        String r_cmd_create_mf0000 = BasicOper.dc_pro_commandhex(cmd_create_mf0000, 7);

        //添加mf线路保护密匙
        //7.添加线路保护密匙
        String cmd_add_mFlineProtectKey = "80D401001536F002FF33" + PBOCUtil.getDisperseKeyOnce(csn, "36363636363636363636363636363636");
        String r_cmd_add_mfLineProtectKey = BasicOper.dc_pro_commandhex(cmd_add_mFlineProtectKey, 7);
        result = "MF线路保护密匙:" + cmd_add_mFlineProtectKey + "\n" + result;


        //8.添加外部保护密匙
        String r_cmd_add_mfoutsideKey = BasicOper.dc_pro_commandhex(cmd_add_mfoutsideKey, 7);
        //9.创建定长文件
        String r_cmd_create_mf0001 = BasicOper.dc_pro_commandhex(cmd_create_mf0001, 7);
        //10.添加定长文件 第一,二条数据
        String r_cmd_add_mf0001_data1 = BasicOper.dc_pro_commandhex(cmd_add_mf0001_data, 7);
        String r_cmd_add_mf0001_data2 = BasicOper.dc_pro_commandhex(cmd_add_mf0001_data, 7);
        //11.创建0005文件
        String r_cmd_create_mf0005 = BasicOper.dc_pro_commandhex(cmd_create_mf0005, 7);
        //12.创建3f01应用
        String r_cmd_create_3f01 = BasicOper.dc_pro_commandhex(cmd_create_3f01, 7);
        result = "创建3f01:" + r_eraseCommand + "\n" + result;
        //13.选中3f01应用
        String r_cmd_choose_3f01 = BasicOper.dc_pro_commandhex(cmd_choose_3f01, 7);
        //14.创建密匙文件
        String r_cmd_creat_0000 = BasicOper.dc_pro_commandhex(cmd_creat_0000, 7);
        //15.添加内部密匙
        String r_cmd_add_insideKey = BasicOper.dc_pro_commandhex(cmd_add_insideKey, 7);

        //添加线路保护密匙
        String cmd_add_lineProtectKey = "80D401001536F002FF33" + PBOCUtil.getDisperseKeyOnce(csn, "36363636363636363636363636363636");
        result = "线路保护秘钥:" + cmd_add_lineProtectKey + "\n" + result;
        //16.添加线路保护密匙
        String r_cmd_add_lineProtectKey = BasicOper.dc_pro_commandhex(cmd_add_lineProtectKey, 7);

        //17.添加口令解锁密匙
        String r_cmd_add_pinUnlockKey = BasicOper.dc_pro_commandhex(cmd_add_pinUnlockKey, 7);
        //18.添加口令重装密匙
        String r_cmd_add_pinResetKey = BasicOper.dc_pro_commandhex(cmd_add_pinResetKey, 7);
        //19.添加外部认证密匙
        String r_cmd_add_outsideKey = BasicOper.dc_pro_commandhex(cmd_add_outsideKey, 7);

        //添加消费密匙 添加充值密匙
        String cmd_add_consumeKey = "80D40101153EF0020001" + PBOCUtil.getDisperseKeyOnce(csn, "3E013E013E013E013E013E013E013E01");
//        String cmd_add_consumeKey = "80D40101153EF0020001" + "3E013E013E013E013E013E013E013E01";
        String cmd_add_rechargeKey = "80D40101153FF0020001" + PBOCUtil.getDisperseKeyOnce(csn, "3F013F013F013F013F013F013F013F01");
//        String cmd_add_rechargeKey = "80D40101153FF0020001" + "3F013F013F013F013F013F013F013F01";
        result = "充值秘钥:" + cmd_add_consumeKey + "\n" + result;
        result = "消费秘钥:" + cmd_add_consumeKey + "\n" + result;
        Log.v("卡操作,","发卡分散密匙指令="+cmd_add_rechargeKey);
        Log.v("卡操作,","发卡物理卡号="+csn);
        //20.添加消费密匙
        String r_cmd_add_consumeKey = BasicOper.dc_pro_commandhex(cmd_add_consumeKey, 7);
        //21.添加充值密匙
        String r_cmd_add_rechargeKey = BasicOper.dc_pro_commandhex(cmd_add_rechargeKey, 7);
        //22.添加圈提密匙
        String r_cmd_add_takeOutKey = BasicOper.dc_pro_commandhex(cmd_add_takeOutKey, 7);
        //23.添加修改透支密匙
        String r_cmd_add_overDrawKey = BasicOper.dc_pro_commandhex(cmd_add_overDrawKey, 7);
        //24.添加pin口令
        String r_cmd_add_pinKey = BasicOper.dc_pro_commandhex(cmd_add_pinKey, 7);
        //25.创建0015二进制文件
        String r_cmd_creat_0015 = BasicOper.dc_pro_commandhex(cmd_creat_0015, 7);
        result = "创建0015:" + r_cmd_creat_0015 + "\n" + result;
        //26.创建0017二进制文件
        String r_cmd_creat_0017 = BasicOper.dc_pro_commandhex(cmd_creat_0017, 7);
        //27.创建0018循环记录文件
        String r_cmd_creat_0018 = BasicOper.dc_pro_commandhex(cmd_creat_0018, 7);
        //28.创建电子钱包应用 0002
        String r_cmd_creat_0002 = BasicOper.dc_pro_commandhex(cmd_creat_0002, 7);
        result = "创建电子钱包:" + r_cmd_creat_0002 + "\n" + result;
        //29.创建复合交易文件001A
        String r_cmd_creat_001A = BasicOper.dc_pro_commandhex(cmd_creat_001A, 7);
        //30.添加复合交易文件 标志为BB 3个字节数据
        String r_cmd_creat_001A_data = BasicOper.dc_pro_commandhex(cmd_creat_001A_data, 7);
        //31.创建电子存折文件 0001
        String r_cmd_creat_0001 = BasicOper.dc_pro_commandhex(cmd_creat_0001, 7);
        result = "CPU卡发卡成功:" + "\n" + result;
        result = "结果:" + "\n" + result;
        //发卡完毕
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(result);
            }
        });
        App.tts.speakText("CPU卡发卡成功");

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

    String cmdps_choose2f01 = "00A40000022f01";

    private void consumePsam() throws Exception {

        //获取物理卡号
        String r_search_card = BasicOper.dc_card_n_hex(0x01);
        Log.v("卡操作", r_search_card);
        String[] rA_r_xunKa = r_search_card.split("\\|", -1);
        csn = rA_r_xunKa[1].substring(0, 8);
        csn = String.format("%16s", csn).replace(" ", "0"); //前面补零到16位
        Log.v("卡操作", "寻卡结果=" + r_search_card + "     csn=" + csn);
        result = "物理卡号:" + r_search_card + "\n" + result;
        //卡片复位
        String r_fuWei = BasicOper.dc_pro_resethex();
        String r_choose_3f01 = BasicOper.dc_pro_commandhex(cmd_choose_3f01, 7);
        Log.v("卡操作", "选中3f01=" + r_choose_3f01);
        //1.选择电子钱包
        String r_choose_wallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
        Log.v("卡操作", "选择电子钱包结果=" + r_choose_wallet);
        String amountString = etAmount.getText().toString().trim();
//        App.tts.speakText("消费金额:" + amountString + "元");
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

        String r_cmdps_choose2f01 = BasicOper.dc_cpuapdu_hex(cmdps_choose2f01);
        result = "选中2f01" + cmdps_choose2f01 + "\n" + result;
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
        result = "求终端交易序号和Mac1" + r_psamMac1 + "\n" + result;
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
        App.tts.speakText("消费成功");

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
        //射频复位
        String result = BasicOper.dc_reset();
        //寻卡
        String r_search_card = BasicOper.dc_card_n_hex(0x01);
        L.v("寻卡=" + r_search_card);
        if (!r_search_card.startsWith("0000")) {
            ToastPrint.showText("请重新放置卡片");
            return;
        }
        csn = r_search_card.split("\\|", -1)[1].substring(0, 8);
        csn = String.format("%16s", csn).replace(" ", "0"); //前面补零到16位
        //卡片复位
        String r_fuWei = BasicOper.dc_pro_resethex();
       String choos3f01= BasicOper.dc_pro_commandhex("00A40000023F01", 7);
        Log.v("卡操作", "选择电子钱包:" + choos3f01);

        //1.选择电子钱包
        String r_choose_wallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
        Log.v("卡操作", "选择电子钱包:" + r_choose_wallet);
        //2.验证口令
        String r_pin = BasicOper.dc_pro_commandhex(pin, 7);
        Log.v("卡操作", "验证pin:" + r_pin);
        //3.圈存初始化
        String amountString = etAmount.getText().toString().trim();
        App.tts.speakText("充值金额:" + amountString + "元");
        //充值金额
        String amountHex = String.format("%08X", Integer.parseInt(amountString));
        Log.v("卡操作", "充值16进制金额:" + amountHex);
        String recharge_init = "805000020b01" + amountHex + posid;     // 充值初始化
        Log.v("卡操作", "预充值指令:" + recharge_init);
        String r_recharge_init = BasicOper.dc_pro_commandhex(recharge_init, 7);
        Log.v("卡操作", "圈存初始化:" + r_recharge_init);
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
        rechargeKey = PBOCUtil.getDisperseKeyOnce(csn, "3F013F013F013F013F013F013F013F01");
        Log.v("卡操作,","分散的密匙="+rechargeKey);
        Log.v("卡操作,","发卡物理卡号="+csn);
        String sessionKey = PBOCUtil.encryptECB3Des(inputData, rechargeKey);
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


    private void sendHeartBeat() {
        esTime.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HeartBeatBean heartBeatBean = new HeartBeatBean();
                String yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                heartBeatBean.setPosDateTime6(yyyyMMddHHmmss); //时间
                String heartBeat = heartBeatBean.getHeartBeat();
                String crc16 = CRC16.crc16(heartBeat);
                String data = heartBeat + crc16;
                byte[] keyByte = BytesUtil.hexString2Bytes(data);
                Log.v("数据交互", "心跳=" + data);

                try {
                    os.write(keyByte);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("socket", "异常=" + e.getMessage());
                }

            }
        }, 2, 10, TimeUnit.SECONDS);//延迟2s后，每10s执行一次
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
                bReciver = false;
            }
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
        esTime.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("数据交互", "上传签到数据=" + signBeanStr + crc16);
                    byte[] qrByte = BytesUtil.hexString2Bytes(signBeanStr + crc16);
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

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
