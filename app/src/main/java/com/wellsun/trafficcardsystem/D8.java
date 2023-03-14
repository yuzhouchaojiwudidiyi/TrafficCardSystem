package com.wellsun.trafficcardsystem;

import android.content.Context;

import com.decard.NDKMethod.BasicOper;

/**
 * date     : 2023-03-10
 * author   : ZhaoZheng
 * describe :
 */
public class D8 {
    public static String cmd_choose_3f01 = "00A40000023F01";  //选择3f01电子钱包应用
    public static String chooseWallet = "00A40000020002";     // 选择电子钱包
    public static String readWallet = "805C000204";           // 选择电子钱包


    //读卡器连接
    public static boolean connectD8(Context context) {
        //向系统申请使用USB权限,此过程为异步,建议放在程序启动时调用。 返回0请求权限
        int iReqPermission = BasicOper.dc_AUSB_ReqPermission(context);
        //打开端口，usb模式，打开之前必须确保已经获取到USB权限，返回值为设备句柄号。 //成功返回180
        int devHandle = BasicOper.dc_open("AUSB", context, "", 0);
        if (devHandle > 0) {  //sam卡异常
            Common.readCardState = true;
            return true;
        }
        return false;
    }

    //读取余额
    public static String readBalance() {
        String r_xunKa = BasicOper.dc_card_hex(0x01);
        String r_fuWei = BasicOper.dc_pro_resethex();
        String r_choose_3f01 = BasicOper.dc_pro_commandhex(cmd_choose_3f01, 7);
        String r_choose_wallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
        String r_read_wallet = BasicOper.dc_pro_commandhex(readWallet, 7);
        String[] rA_readWallet = r_read_wallet.split("\\|", -1);
        String cardBalanceHex = rA_readWallet[1].substring(0, 8);
        int cardBalanceInt = Integer.parseInt(cardBalanceHex, 16);
        return cardBalanceInt + "";
    }


}
