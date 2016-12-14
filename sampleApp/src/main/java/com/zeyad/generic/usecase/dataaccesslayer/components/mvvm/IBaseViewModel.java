package com.zeyad.generic.usecase.dataaccesslayer.components.mvvm;

import android.os.Bundle;

/**
 * @author zeyad on 11/28/16.
 */
public interface IBaseViewModel<V> {
    Bundle getState();

    void restoreState(Bundle state);

    void onViewAttached(V view, boolean isNew);

    void onViewDetached();

    int getItemId();

    void setItemId(int itemId);
}