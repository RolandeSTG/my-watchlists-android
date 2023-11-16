package com.rolande.mywatchlists.model.beans;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
/**
 * A class/bean to define a security's quote, which refers to a security's symbol
 * along with a number of pricing and volume information.
 *
 * @author Rolande St-Gelais
 */
public class Quote implements Serializable {

    //    @SerializedName("_id")
    private transient Long id;                  // not included in serialize/deserialize process

    @SerializedName("symbol")
    private String symbol;
    @SerializedName("previous_close_price")
    private double previousClosePrice;

    @SerializedName("open_price")
    private double openPrice;

    @SerializedName("last_trade_timestamp")
    private Timestamp lastTradeTimestamp;

    @SerializedName("last_trade_price")
    private double lastTradePrice;

    @SerializedName("last_trade_volume")
    private int lastTradeVolume;

    @SerializedName("bid_price")
    private double bidPrice;

    @SerializedName("bid_volume")
    private int bidVolume;

    @SerializedName("ask_price")
    private double askPrice;

    @SerializedName("ask_volume")
    private int askVolume;

    @SerializedName("environment")
    private String environment;

    public Quote() {}

    public Quote(Long id, String symbol, double previousClosePrice, double openPrice, Timestamp lastTradeTimestamp, double lastTradePrice, int lastTradeVolume, double bidPrice, int bidVolume, double askPrice, int askVolume, String environment) {
        this.id = id;
        this.symbol = symbol;
        this.previousClosePrice = previousClosePrice;
        this.openPrice = openPrice;
        this.lastTradeTimestamp = lastTradeTimestamp;
        this.lastTradePrice = lastTradePrice;
        this.lastTradeVolume = lastTradeVolume;
        this.bidPrice = bidPrice;
        this.bidVolume = bidVolume;
        this.askPrice = askPrice;
        this.askVolume = askVolume;
        this.environment = environment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public double getPreviousClosePrice() {
        return previousClosePrice;
    }
    public void setPreviousClosePrice(double previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }
    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }
    public Timestamp getLastTradeTimestamp() {
        return lastTradeTimestamp;
    }

    public void setLastTradeTimestamp(Timestamp lastTradeTimestamp) {
        this.lastTradeTimestamp = lastTradeTimestamp;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }
    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }
    public int getLastTradeVolume() {
        return lastTradeVolume;
    }
    public void setLastTradeVolume(int lastTradeVolume) {
        this.lastTradeVolume = lastTradeVolume;
    }
    public double getBidPrice() {
        return bidPrice;
    }
    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(int bidVolume) {
        this.bidVolume = bidVolume;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(int askVolume) {
        this.askVolume = askVolume;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", previousClosePrice=" + previousClosePrice +
                ", openPrice=" + openPrice +
                ", lastTradeTimestamp=" + lastTradeTimestamp +
                ", lastTradePrice=" + lastTradePrice +
                ", lastTradeVolume=" + lastTradeVolume +
                ", bidPrice=" + bidPrice +
                ", bidVolume=" + bidVolume +
                ", askPrice=" + askPrice +
                ", askVolume=" + askVolume +
                ", environment='" + environment + '\'' +
                '}';
    }
}
