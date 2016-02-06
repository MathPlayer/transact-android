package com.ssa.transact.transact;

import android.graphics.Bitmap;

/**
 * Created by bogdan on 16/02/06.
 */
public interface TransactBuyListener {
    void updateMap(Bitmap map);
    void buyActionFinished(String message);
}
