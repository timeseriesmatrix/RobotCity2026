/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.model.Currency;
import com.floreantpos.model.dao.CurrencyDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CurrencyUtil {
    private static Currency mainCurrency;
    private static List<Currency> auxiliaryCurrencyList;

    public static void populateCurrency() {
        auxiliaryCurrencyList = new ArrayList<Currency>();
        List<Currency> currencyList = CurrencyDAO.getInstance().findAll();
        if (currencyList != null) {
            for (Currency currency : currencyList) {
                if (currency.isMain().booleanValue()) {
                    mainCurrency = currency;
                    continue;
                }
                auxiliaryCurrencyList.add(currency);
            }
        }
    }

    public static Currency getMainCurrency() {
        return mainCurrency;
    }

    public static List<Currency> getAuxiliaryCurrencyList() {
        return auxiliaryCurrencyList;
    }

    public static List<Currency> getAllCurrency() {
        ArrayList<Currency> currencyList = new ArrayList<Currency>();
        currencyList.add(mainCurrency);
        Collections.sort(auxiliaryCurrencyList, new Comparator<Currency>(){

            @Override
            public int compare(Currency curr1, Currency curr2) {
                return curr1.getName().compareTo(curr2.getName());
            }
        });
        currencyList.addAll(auxiliaryCurrencyList);
        return currencyList;
    }

    public static String getCurrencyName() {
        String currencyName = null;
        currencyName = mainCurrency != null ? mainCurrency.getName() : "USD";
        return currencyName;
    }

    public static String getCurrencySymbol() {
        String currencySymbol = null;
        currencySymbol = mainCurrency != null ? mainCurrency.getSymbol() : "$";
        return currencySymbol;
    }
}

