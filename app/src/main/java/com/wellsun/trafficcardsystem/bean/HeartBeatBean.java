package com.wellsun.trafficcardsystem.bean;


/**
 * date     : 2022-09-08
 * author   : ZhaoZheng
 * describe : 心跳包
 */
public class HeartBeatBean {
    String xieYiType1 = "01"; //协议类型            1
    String xieYiCode2 = "0002"; //协议代码            2
    String posId3 = "11223344"; //设备编号     4
    String liuLiangCardNumber4 = "0000000000000000000000000000000000000000"; //流量卡号  20
    String dataLength5 = "00ae"; //数据长度   2
    //上面29位
    String PosDateTime6 = "00000000000000";  //POS机时间  7字节
    String CorpId7 = "000000000000";         //商户好     6字节
    String PSAM1_8 = "000000000000";         //pasm1     6字节
    String PSAM2_9 = "000000000000";         //pasm2     6字节
    String PSAM3_10 = "000000000000";         //pasm3    6字节
    String PSAM4_11 = "000000000000";         //pasm4     6字节
    String PosStatus12 = "1001";             //pos状态     2字节
    String LineNum13 = "000000";             //线路号     3字节
    String BusNum14 = "30303030303030303030";  //车号       10字节
    String BlackListVersion15 = "00000000";  //黑名单版本       4字节
    String AddBlackListVersion16 = "00000000";  //增量黑名单版本       4字节
    String VoiceVersion17 = "00000000000000";  //语音版本       7字节
    String PosType18 = "00";                   //语音版本    1字节    POS类型，00老POS 01新POS 02二代机 03二维码机器 04主控板 05手持机，03二维码机器，04主控板，05手持机
    String PosProgramVersion19 = "00000000000000";   //POS程序版本  7字节
    String ParameterVersion20 = "00000000";          //参数版本  4字节
    String PriceVersion21 = "00000000";              //票价版本  4字节
    String MOTWhiteListVersion22 = "00000000";       //预留默认全0  4字节
    String Latitude23 = "0000000000000000";          //经度 8字节
    String Longitude24 = "0000000000000000";          //纬度 8字节
    String StationId25 = "00000000";                //站点编号 4字节
    String ExitStationFlag26 = "00";                //进出站标志 1字节
    String UpDownFlag27 = "00";                    //上下行标志 1字节
    String BinVersion28 = "00000000000000";          //预留默认全0  7字节
    String AidVersion29 = "00000000";                //预留默认全0  4字节
    String CaVersion30 = "00000000";                //预留默认全0  4字节
    String TrackBlackListVersion31 = "00000000";    //预留默认全0  4字节
    String MainBoardVersion32 = "00000000000000";    //主控板版本  7字节
    String ChargeByTheMileVersion33 = "00000000";     //分段计价版本  4字节
    String Places34 = "00";                                   //位置  1字节
    String LockState35 = "00";                                //手工锁的状态  1字节
    String PurseNumber36 = "0000000000";                      //钱袋编号  5字节
    String CommunicationType37 = "00";                         //通讯类型,0为有线,1为无线  1字节
    String FareId38 = "00000000000000000000000000000000";     //票价ID  16字节
    String MoHURDWhiteListVersion39 = "00000000";             //预留默认全0  4字节
    String SDKVersion40 = "00000000000000";                   //SDKVersion  7字节


    public String getHeartBeat() {
        String parameter = xieYiType1 + xieYiCode2 + posId3 + liuLiangCardNumber4 + dataLength5
                + PosDateTime6 +
                CorpId7 +
                PSAM1_8 +
                PSAM2_9 +
                PSAM3_10 +
                PSAM4_11 +
                PosStatus12 +
                LineNum13 +
                BusNum14 +
                BlackListVersion15 +
                AddBlackListVersion16 +
                VoiceVersion17 +
                PosType18 +
                PosProgramVersion19 +
                ParameterVersion20 +
                PriceVersion21 +
                MOTWhiteListVersion22 +
                Latitude23 +
                Longitude24 +
                StationId25 +
                ExitStationFlag26 +
                UpDownFlag27 +
                BinVersion28 +
                AidVersion29 +
                CaVersion30 +
                TrackBlackListVersion31 +
                MainBoardVersion32 +
                ChargeByTheMileVersion33 +
                Places34 +
                LockState35 +
                PurseNumber36 +
                CommunicationType37 +
                FareId38 +
                MoHURDWhiteListVersion39 +
                SDKVersion40;
        return parameter;
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

    public String getLiuLiangCardNumber4() {
        return liuLiangCardNumber4;
    }

    public void setLiuLiangCardNumber4(String liuLiangCardNumber4) {
        this.liuLiangCardNumber4 = liuLiangCardNumber4;
    }

    public String getDataLength5() {
        return dataLength5;
    }

    public void setDataLength5(String dataLength5) {
        this.dataLength5 = dataLength5;
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

    public String getPSAM1_8() {
        return PSAM1_8;
    }

    public void setPSAM1_8(String PSAM1_8) {
        this.PSAM1_8 = PSAM1_8;
    }

    public String getPSAM2_9() {
        return PSAM2_9;
    }

    public void setPSAM2_9(String PSAM2_9) {
        this.PSAM2_9 = PSAM2_9;
    }

    public String getPSAM3_10() {
        return PSAM3_10;
    }

    public void setPSAM3_10(String PSAM3_10) {
        this.PSAM3_10 = PSAM3_10;
    }

    public String getPSAM4_11() {
        return PSAM4_11;
    }

    public void setPSAM4_11(String PSAM4_11) {
        this.PSAM4_11 = PSAM4_11;
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

    public String getBinVersion28() {
        return BinVersion28;
    }

    public void setBinVersion28(String binVersion28) {
        BinVersion28 = binVersion28;
    }

    public String getAidVersion29() {
        return AidVersion29;
    }

    public void setAidVersion29(String aidVersion29) {
        AidVersion29 = aidVersion29;
    }

    public String getCaVersion30() {
        return CaVersion30;
    }

    public void setCaVersion30(String caVersion30) {
        CaVersion30 = caVersion30;
    }

    public String getTrackBlackListVersion31() {
        return TrackBlackListVersion31;
    }

    public void setTrackBlackListVersion31(String trackBlackListVersion31) {
        TrackBlackListVersion31 = trackBlackListVersion31;
    }

    public String getMainBoardVersion32() {
        return MainBoardVersion32;
    }

    public void setMainBoardVersion32(String mainBoardVersion32) {
        MainBoardVersion32 = mainBoardVersion32;
    }

    public String getChargeByTheMileVersion33() {
        return ChargeByTheMileVersion33;
    }

    public void setChargeByTheMileVersion33(String chargeByTheMileVersion33) {
        ChargeByTheMileVersion33 = chargeByTheMileVersion33;
    }

    public String getPlaces34() {
        return Places34;
    }

    public void setPlaces34(String places34) {
        Places34 = places34;
    }

    public String getLockState35() {
        return LockState35;
    }

    public void setLockState35(String lockState35) {
        LockState35 = lockState35;
    }

    public String getPurseNumber36() {
        return PurseNumber36;
    }

    public void setPurseNumber36(String purseNumber36) {
        PurseNumber36 = purseNumber36;
    }

    public String getFareId38() {
        return FareId38;
    }

    public void setFareId38(String fareId38) {
        FareId38 = fareId38;
    }

    public String getMoHURDWhiteListVersion39() {
        return MoHURDWhiteListVersion39;
    }

    public void setMoHURDWhiteListVersion39(String moHURDWhiteListVersion39) {
        MoHURDWhiteListVersion39 = moHURDWhiteListVersion39;
    }

    public String getSDKVersion40() {
        return SDKVersion40;
    }

    public void setSDKVersion40(String SDKVersion40) {
        this.SDKVersion40 = SDKVersion40;
    }

    public String getLatitude23() {
        return Latitude23;
    }

    public void setLatitude23(String latitude23) {
        Latitude23 = latitude23;
    }

    public String getLongitude24() {
        return Longitude24;
    }

    public void setLongitude24(String longitude24) {
        Longitude24 = longitude24;
    }

    public String getStationId25() {
        return StationId25;
    }

    public void setStationId25(String stationId25) {
        StationId25 = stationId25;
    }

    public String getExitStationFlag26() {
        return ExitStationFlag26;
    }

    public void setExitStationFlag26(String exitStationFlag26) {
        ExitStationFlag26 = exitStationFlag26;
    }

    public String getUpDownFlag27() {
        return UpDownFlag27;
    }

    public void setUpDownFlag27(String upDownFlag27) {
        UpDownFlag27 = upDownFlag27;
    }

    public String getCommunicationType37() {
        return CommunicationType37;
    }

    public void setCommunicationType37(String communicationType37) {
        CommunicationType37 = communicationType37;
    }
}
