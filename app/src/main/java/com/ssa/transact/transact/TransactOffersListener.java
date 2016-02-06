package com.ssa.transact.transact;

import java.util.HashMap;
import java.util.List;

public interface TransactOffersListener {
    void updateOffers(List<String> offers);
    void showOffer(HashMap<String, String> offer);
}
