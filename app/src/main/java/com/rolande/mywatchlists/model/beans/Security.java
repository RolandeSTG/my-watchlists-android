package com.rolande.mywatchlists.model.beans;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * A class/bean to define a security. A security (here) refers to a
 * financial instrument that investors may buy/sell on trading markets. Stocks,
 * bonds and options are common instances of it.
 *
 * @author Rolande St-Gelais
 */
public class Security implements Serializable {

    @SerializedName("_id")
    private long id;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("name")
    private String name;

    @SerializedName("exchange")
    private String exchange;

    @SerializedName("quote")
    private Quote quote;

    // Default no-arg constructor
    public Security() {
    }

    public Security(long id, String symbol, String name, String exchange, Quote quote) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
        this.quote = quote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    @Override
    public String toString() {
        return "Security [id=" + id + ", symbol=" + symbol + ", name=" + name + ", exchange=" + exchange + ", quote="
                + quote + "]";
    }

}


