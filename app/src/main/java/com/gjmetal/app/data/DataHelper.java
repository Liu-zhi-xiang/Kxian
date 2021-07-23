package com.gjmetal.app.data;


import com.gjmetal.app.model.market.kline.KLine;
import com.gjmetal.star.log.XLog;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 数据辅助类 计算macd rsi等
 */

public class DataHelper {
    /**
     * 计算SAR
     *
     * @param datas
     */
    static void calculateSAR(List<KLine> datas) {
        DecimalFormat df = new DecimalFormat("0.0000");//保留3位小数
        df.setRoundingMode(RoundingMode.HALF_UP);
        float step = 0.02f;
        float maxStep = 0.2f;
        //记录是否初始化过
        float INIT_VALUE = -100f;
        //加速因子
        float af = 0.00f;
        //极值
        float ep = INIT_VALUE;
        //判断是上涨还是下跌  false：下跌
        boolean lastTrend = false;
        float SAR = 0.000f;
        float fourMaxValue = 0.000f;
        float fourMinValue = 0.000f;
        boolean startUp = false;
        boolean startDown = false;
        int weekDay = 4;//一个周期4天
        try {
            for (int i = 0; i < datas.size(); i++) {
                if (i > weekDay + 1) {
                    break;
                }
                if (i == 0) {
                    fourMaxValue = datas.get(i).getHighPrice();
                    fourMinValue = datas.get(i).getLowPrice();
                }
                if (i > 0 && i <= weekDay + 1) {
                    if (fourMaxValue < datas.get(i - 1).getHighPrice()) {
                        fourMaxValue = datas.get(i - 1).getHighPrice();
                    }
                    if (datas.get(i - 1).getLowPrice() < fourMinValue) {
                        fourMinValue = datas.get(i - 1).getLowPrice();
                    }
                }
            }
            if (datas.size() > weekDay - 1) {
                if (datas.get(0).getOpenPrice() != datas.get(weekDay - 1).getClosePrice()) {
                    //如果第4天收盘价不等于第1天开盘价
                    lastTrend = datas.get(0).getOpenPrice() < datas.get(weekDay - 1).getClosePrice();
                } else if (Math.abs(fourMaxValue - datas.get(0).getOpenPrice()) - Math.abs(fourMinValue - datas.get(0).getOpenPrice()) != 0) {
                    //对比最高价-开盘价的绝对值和最低价-开盘价的绝对值
                    lastTrend = Math.abs(fourMaxValue - datas.get(0).getOpenPrice()) - Math.abs(fourMinValue - datas.get(0).getOpenPrice()) > 0;
                }
            }
            for (int i = weekDay; i < datas.size(); i++) {
                //上一个周期的sar
                float priorSAR = SAR;
                KLine point = datas.get(i);
                if (lastTrend) {
                    startDown = false;
                    if (i == weekDay) {
                        SAR = fourMinValue;
                        priorSAR = fourMinValue;
                    } else {
                        //上涨
                        if (ep == INIT_VALUE || datas.get(i - 2).getHighPrice() < datas.get(i - 1).getHighPrice()) {
                            if (ep == INIT_VALUE) {
                                af = 0.00f;
                            }
                            af = Math.min(af + step, maxStep);
                            af=Float.parseFloat(df.format(af));
                        }
                        ep = datas.get(i - 1).getHighPrice();
                        //ep 为前一天最高价
                        SAR = priorSAR + af * (ep - priorSAR);
                        if (i + 1 < datas.size() && SAR > datas.get(i).getLowPrice() || startUp) {
                            if (startUp || SAR > point.getLowPrice()) {
                                float maxValue = datas.get(i).getHighPrice();//前4天包括当天的最高价
                                for (int j = 1; j <= weekDay; j++) {
                                    if (i - j > weekDay) {
                                        maxValue = Math.max(maxValue, datas.get(i - j).getHighPrice());
                                    }
                                }
                                SAR = maxValue;
                                //重新初始化值
                                af = 0.00f;
                                ep = INIT_VALUE;
                                priorSAR = maxValue;
                                lastTrend = !lastTrend;
                            }
                            startUp = true;
                        }
                    }
                } else {
                    startUp = false;
                    if (i == weekDay) {
                        SAR = fourMaxValue;
                        priorSAR = fourMaxValue;
                    } else {
                        if (ep == INIT_VALUE || datas.get(i - 2).getLowPrice() > datas.get(i - 1).getLowPrice()) {
                            //重新初始化值
                            af = Math.min(af + step, maxStep);
                            af=Float.parseFloat(df.format(af));
                        }
                        ep = datas.get(i - 1).getLowPrice();
                        //ep 为前一天最低价
                        SAR = priorSAR + af * (ep - priorSAR);
                        if (i + 1 < datas.size() && SAR < datas.get(i).getHighPrice() || startDown) {
                            if (startDown || SAR < point.getHighPrice()) {
                                float minValue = datas.get(i).getLowPrice();//前4天包括当天的最低价
                                for (int j = 1; j <= weekDay; j++) {
                                    if (i - j > weekDay) {
                                        minValue = Math.min(minValue, datas.get(i - j).getLowPrice());
                                    }
                                }
                                SAR = minValue;
                                priorSAR = minValue;
                                af = 0.00f;
                                ep = INIT_VALUE;
                                lastTrend = !lastTrend;
                            }
                            startDown = true;
                        }
                    }
                }
                point.sarValueUp = lastTrend;
                point.sar = Float.parseFloat(df.format(SAR));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算RSI
     *
     * @param datas
     */
    static void calculateRSI(List<KLine> datas) {
        float rsi1 = 0;
        float rsi2 = 0;
        float rsi3 = 0;
        float rsi1ABSEma = 0;
        float rsi2ABSEma = 0;
        float rsi3ABSEma = 0;
        float rsi1MaxEma = 0;
        float rsi2MaxEma = 0;
        float rsi3MaxEma = 0;
        for (int i = 0; i < datas.size(); i++) {
            KLine point = datas.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                rsi1 = 0;
                rsi2 = 0;
                rsi3 = 0;
                rsi1ABSEma = 0;
                rsi2ABSEma = 0;
                rsi3ABSEma = 0;
                rsi1MaxEma = 0;
                rsi2MaxEma = 0;
                rsi3MaxEma = 0;
            } else {
                float Rmax = Math.max(0, closePrice - datas.get(i - 1).getClosePrice());
                float RAbs = Math.abs(closePrice - datas.get(i - 1).getClosePrice());
                rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f;
                rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f;

                rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f;
                rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f;

                rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f;
                rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f;

                rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
                rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
                rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
            }
            point.rsi1 = rsi1;
            point.rsi2 = rsi2;
            point.rsi3 = rsi3;
        }
    }

    /**
     * 计算kdj
     *
     * @param datas
     */
    static void calculateKDJ(List<KLine> datas) {
        float k = 0;
        float d = 0;

        for (int i = 0; i < datas.size(); i++) {
            KLine point = datas.get(i);
            final float closePrice = point.getClosePrice();
            int startIndex = i - 8;
            if (startIndex < 0) {
                startIndex = 0;
            }
            float max9 = Float.MIN_VALUE;
            float min9 = Float.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max9 = Math.max(max9, datas.get(index).getHighPrice());
                min9 = Math.min(min9, datas.get(index).getLowPrice());
            }
            float rsv = 0;
            if (max9 - min9 != 0) {
                rsv = 100f * (closePrice - min9) / (max9 - min9);
            }

            if (i == 0) {
                k = rsv;
                d = rsv;
            } else {
                k = (rsv + 2f * k) / 3f;
                d = (k + 2f * d) / 3f;
            }
            point.k = Float.isNaN(k) ? 0 : k;
            point.d = Float.isNaN(k) ? 0 : d;
            float valueD = 3f * k - 2 * d;
            point.j = Float.isNaN(valueD) ? 0 : valueD;
        }

    }

    /**
     * 计算macd
     *
     * @param datas
     */
    static void calculateMACD(List<KLine> datas) {
        float ema12 = 0;
        float ema26 = 0;
        float dif = 0;
        float dea = 0;
        float macd = 0;

        for (int i = 0; i < datas.size(); i++) {
            KLine point = datas.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                ema12 = closePrice;
                ema26 = closePrice;
            } else {
//                EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
//                EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema12 = ema12 * 11f / 13f + closePrice * 2f / 13f;
                ema26 = ema26 * 25f / 27f + closePrice * 2f / 27f;
            }
//            DIF = EMA（12） - EMA（26） 。
//            今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
//            用（DIF-DEA）*2即为MACD柱状图。
            dif = ema12 - ema26;
            dea = dea * 8f / 10f + dif * 2f / 10f;
            macd = (dif - dea) * 2f;
            point.dif = dif;
            point.dea = dea;
            point.macd = macd;
        }

    }

    /**
     * 计算 BOLL 需要在计算ma之后进行
     *
     * @param datas
     */
    static void calculateBOLL(List<KLine> datas) {
        for (int i = 0; i < datas.size(); i++) {
            KLine point = datas.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                point.mb = closePrice;
                point.up = Float.NaN;//Float.NaN
                point.dn = Float.NaN;
            } else {
                int n = 26;
                if (i < 26) {
                    n = i + 1;
                }
                float md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    float c = datas.get(j).getClosePrice();
                    float m = point.getMA26Price();
                    float value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                point.mb = point.getMA26Price();
                point.up = point.mb + 2f * md;
                point.dn = point.mb - 2f * md;
            }
        }

    }

    /**
     * 计算ma
     *
     * @param datas
     */
    static void calculateMA(List<KLine> datas) {
        float ma5 = 0;
        float ma10 = 0;
        float ma20 = 0;
        float ma26 = 0;
        float ma40 = 0;
        float ma60 = 0;
        for (int i = 0; i < datas.size(); i++) {
            KLine point = datas.get(i);
            final float closePrice = point.getClosePrice();
            ma5 += closePrice;
            ma10 += closePrice;
            ma20 += closePrice;
            ma26 += closePrice;
            ma40 += closePrice;
            ma60 += closePrice;
            if (i >= 5) {
                ma5 -= datas.get(i - 5).getClosePrice();
                point.MA5Price = ma5 / 5f;
            } else {
                point.MA5Price = ma5 / (i + 1f);
            }
            if (i >= 10) {
                ma10 -= datas.get(i - 10).getClosePrice();
                point.MA10Price = ma10 / 10f;
            } else {
                point.MA10Price = ma10 / (i + 1f);
            }
            if (i >= 20) {
                ma20 -= datas.get(i - 20).getClosePrice();
                point.MA20Price = ma20 / 20f;
            } else {
                point.MA20Price = ma20 / (i + 1f);
            }
            if (i >= 26) {
                ma26 -= datas.get(i - 26).getClosePrice();
                point.MA26Price = ma26 / 26f;
            } else {
                point.MA26Price = ma26 / (i + 1f);
            }
            if (i >= 40) {
                ma40 -= datas.get(i - 40).getClosePrice();
                point.MA40Price = ma40 / 40f;
            } else {
                point.MA40Price = ma40 / (i + 1f);
            }
            if (i >= 60) {
                ma60 -= datas.get(i - 60).getClosePrice();
                point.MA60Price = ma60 / 60f;
            } else {
                point.MA60Price = ma60 / (i + 1f);
            }
        }
    }

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param datas
     */
    static void calculate(List<KLine> datas) {
        calculateMA(datas);
        calculateMACD(datas);
        calculateBOLL(datas);
        calculateSAR(datas);
        calculateRSI(datas);
        calculateKDJ(datas);
        calculateVolumeMA(datas);
    }

    private static void calculateVolumeMA(List<KLine> entries) {
        float volumeMa5 = 0;
        float volumeMa10 = 0;

        for (int i = 0; i < entries.size(); i++) {
            KLine entry = entries.get(i);

            volumeMa5 += entry.getVolume();
            volumeMa10 += entry.getVolume();

            if (i >= 5) {

                volumeMa5 -= entries.get(i - 5).getVolume();
                entry.MA5Volume = (volumeMa5 / 5f);
            } else {

                entry.MA5Volume = (volumeMa5 / (i + 1f));
            }

            if (i >= 10) {
                volumeMa10 -= entries.get(i - 10).getVolume();
                entry.MA10Volume = (volumeMa10 / 5f);
            } else {
                entry.MA10Volume = (volumeMa10 / (i + 1f));
            }
        }
    }
}
