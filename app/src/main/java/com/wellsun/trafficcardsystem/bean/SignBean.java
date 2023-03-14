package com.wellsun.trafficcardsystem.bean;

/**
 * date     : 2023-03-13
 * author   : ZhaoZheng
 * describe :
 */
public class SignBean {
    //头  29字节
    String xieYiType1 = "01"; //协议类型  1
    String xieYiCode2 = "0002";//协议代码 2
    String posId3 = "11223344";//pos编号 4
    String liuLiangKaHao4 = "0000000000000000000000000000000000000000";//流量卡号 20
    String length5 = "0091";  //长度 2
    //数据内容  145字节
    String PosDateTime6 = "20230308112233";   //时间 7
    String CorpId7 = "000000000000";   //商户号 6
    String psam1_8 = "000000000000";   //psam1 6
    String psam2_9 = "000000000000";   //psam2 6
    String psam3_10 = "000000000000";   //psam3 6
    String psam4_11 = "000000000000";   //psam4 6
    String PosStatus12 = "0000";          //pos状态 2
    String LineNum13 = "000000";         //线路号 3
    String BusNum14 = "00000000000000000000";//车号 10
    String BlackListVersion15 = "00000000";//黑名单版本 4
    String AddBlackListVersion16 = "00000000";//增量黑名单版本 4
    String VoiceVersion17 = "00000000000000";//语音版本 7
    String PosType18 = "02";//pos类型 1 POS类型，00老POS 01新POS 02二代机 03二维码机器 04主控板 05手持机，03二维码机器，04主控板，05手持机
    String PosProgramVersion19 = "20230308112233"; //pos程序版本 7
    String ParameterVersion20 = "00000001"; //参数版本 4
    String PriceVersion21 = "00000001"; //票价版本 4
    String MOTWhiteListVersion22 = "00000000"; //默认 4
    String BinVersion23 = "00000000"; //默认 4
    String AidVersion24 = "00000000"; //默认 4
    String CaVersion25 = "00000000"; //默认 4
    String TrackBlackListVersion26 = "00000000"; //默认 4
    String MainBoardVersion27 = "00000000"; //MainBoardVersion 7
    String ChargeByTheMileVersion28 = "00000000"; //分段计价 4
    String Places29 = "00";//位置 1
    String LockState30 = "00";//预留 1
    String PurseNumber31 = "0000000000";// 预留 5
    String FareId32 = "00000000000000000000000000000000";// 票价id 16
    String MoHURDWhiteListVersion33 = "00000000"; //默认 4
    String SDKVersion34 = "00000000000000"; //默认 4


    public  String getString() {
        String pin = xieYiType1+xieYiCode2+posId3+liuLiangKaHao4+length5+PosDateTime6+CorpId7+psam1_8+psam2_9+psam3_10+psam4_11
                +PosStatus12+LineNum13+BusNum14+BlackListVersion15+AddBlackListVersion16+VoiceVersion17+PosType18+PosProgramVersion19
                +ParameterVersion20+PriceVersion21+MOTWhiteListVersion22+BinVersion23+AidVersion24+CaVersion25+TrackBlackListVersion26
                +MainBoardVersion27+ChargeByTheMileVersion28+Places29+LockState30+PurseNumber31+FareId32+MoHURDWhiteListVersion33+SDKVersion34;
        return pin;
    }

    public String getXieYiType1() {
        return xieYiType1;
    }

    public void setXieYiType1(String xieYiType1) {
        this.xieYiType1 = xieYiType1;
    }

    public String getXieYiCode2() {
        return xieYiCode2;
    }

    public void setXieYiCode2(String xieYiCode2) {
        this.xieYiCode2 = xieYiCode2;
    }

    public String getPosId3() {
        return posId3;
    }

    public void setPosId3(String posId3) {
        this.posId3 = posId3;
    }

    public String getLiuLiangKaHao4() {
        return liuLiangKaHao4;
    }

    public void setLiuLiangKaHao4(String liuLiangKaHao4) {
        this.liuLiangKaHao4 = liuLiangKaHao4;
    }

    public String getLength5() {
        return length5;
    }

    public void setLength5(String length5) {
        this.length5 = length5;
    }

    public String getPosDateTime6() {
        return PosDateTime6;
    }

    public void setPosDateTime6(String posDateTime6) {
        PosDateTime6 = posDateTime6;
    }

    public String getCorpId7() {
        return CorpId7;
    }

    public void setCorpId7(String corpId7) {
        CorpId7 = corpId7;
    }

    public String getPsam1_8() {
        return psam1_8;
    }

    public void setPsam1_8(String psam1_8) {
        this.psam1_8 = psam1_8;
    }

    public String getPsam2_9() {
        return psam2_9;
    }

    public void setPsam2_9(String psam2_9) {
        this.psam2_9 = psam2_9;
    }

    public String getPsam3_10() {
        return psam3_10;
    }

    public void setPsam3_10(String psam3_10) {
        this.psam3_10 = psam3_10;
    }

    public String getPsam4_11() {
        return psam4_11;
    }

    public void setPsam4_11(String psam4_11) {
        this.psam4_11 = psam4_11;
    }

    public String getPosStatus12() {
        return PosStatus12;
    }

    public void setPosStatus12(String posStatus12) {
        PosStatus12 = posStatus12;
    }

    public String getLineNum13() {
        return LineNum13;
    }

    public void setLineNum13(String lineNum13) {
        LineNum13 = lineNum13;
    }

    public String getBusNum14() {
        return BusNum14;
    }

    public void setBusNum14(String busNum14) {
        BusNum14 = busNum14;
    }

    public String getBlackListVersion15() {
        return BlackListVersion15;
    }

    public void setBlackListVersion15(String blackListVersion15) {
        BlackListVersion15 = blackListVersion15;
    }

    public String getAddBlackListVersion16() {
        return AddBlackListVersion16;
    }

    public void setAddBlackListVersion16(String addBlackListVersion16) {
        AddBlackListVersion16 = addBlackListVersion16;
    }

    public String getVoiceVersion17() {
        return VoiceVersion17;
    }

    public void setVoiceVersion17(String voiceVersion17) {
        VoiceVersion17 = voiceVersion17;
    }

    public String getPosType18() {
        return PosType18;
    }

    public void setPosType18(String posType18) {
        PosType18 = posType18;
    }

    public String getPosProgramVersion19() {
        return PosProgramVersion19;
    }

    public void setPosProgramVersion19(String posProgramVersion19) {
        PosProgramVersion19 = posProgramVersion19;
    }

    public String getParameterVersion20() {
        return ParameterVersion20;
    }

    public void setParameterVersion20(String parameterVersion20) {
        ParameterVersion20 = parameterVersion20;
    }

    public String getPriceVersion21() {
        return PriceVersion21;
    }

    public void setPriceVersion21(String priceVersion21) {
        PriceVersion21 = priceVersion21;
    }

    public String getMOTWhiteListVersion22() {
        return MOTWhiteListVersion22;
    }

    public void setMOTWhiteListVersion22(String MOTWhiteListVersion22) {
        this.MOTWhiteListVersion22 = MOTWhiteListVersion22;
    }

    public String getBinVersion23() {
        return BinVersion23;
    }

    public void setBinVersion23(String binVersion23) {
        BinVersion23 = binVersion23;
    }

    public String getAidVersion24() {
        return AidVersion24;
    }

    public void setAidVersion24(String aidVersion24) {
        AidVersion24 = aidVersion24;
    }

    public String getCaVersion25() {
        return CaVersion25;
    }

    public void setCaVersion25(String caVersion25) {
        CaVersion25 = caVersion25;
    }

    public String getTrackBlackListVersion26() {
        return TrackBlackListVersion26;
    }

    public void setTrackBlackListVersion26(String trackBlackListVersion26) {
        TrackBlackListVersion26 = trackBlackListVersion26;
    }

    public String getMainBoardVersion27() {
        return MainBoardVersion27;
    }

    public void setMainBoardVersion27(String mainBoardVersion27) {
        MainBoardVersion27 = mainBoardVersion27;
    }

    public String getChargeByTheMileVersion28() {
        return ChargeByTheMileVersion28;
    }

    public void setChargeByTheMileVersion28(String chargeByTheMileVersion28) {
        ChargeByTheMileVersion28 = chargeByTheMileVersion28;
    }

    public String getPlaces29() {
        return Places29;
    }

    public void setPlaces29(String places29) {
        Places29 = places29;
    }

    public String getLockState30() {
        return LockState30;
    }

    public void setLockState30(String lockState30) {
        LockState30 = lockState30;
    }

    public String getPurseNumber31() {
        return PurseNumber31;
    }

    public void setPurseNumber31(String purseNumber31) {
        PurseNumber31 = purseNumber31;
    }

    public String getFareId32() {
        return FareId32;
    }

    public void setFareId32(String fareId32) {
        FareId32 = fareId32;
    }

    public String getMoHURDWhiteListVersion33() {
        return MoHURDWhiteListVersion33;
    }

    public void setMoHURDWhiteListVersion33(String moHURDWhiteListVersion33) {
        MoHURDWhiteListVersion33 = moHURDWhiteListVersion33;
    }

    public String getSDKVersion34() {
        return SDKVersion34;
    }

    public void setSDKVersion34(String SDKVersion34) {
        this.SDKVersion34 = SDKVersion34;
    }

}
