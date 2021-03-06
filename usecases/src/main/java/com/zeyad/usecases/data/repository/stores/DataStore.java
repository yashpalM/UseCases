package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.RealmManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    Gson gson = Config.getGson();

    @NonNull
    Observable<List> dynamicGetList(final String url, Class domainClass, Class dataClass, boolean persist,
                                    boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit a ? by its id.
     */
    @NonNull
    Observable<?> dynamicGetObject(final String url, final String idColumnName, final int itemId,
                                   Class domainClass, Class dataClass, boolean persist, boolean shouldCache);

    /**
     * Patch a JSONObject which returns an {@link Observable} that will emit a ?.
     */
    Observable<?> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                     Class domainClass, Class dataClass, boolean persist, boolean queuable);

    /**
     * Post a JSONObject which returns an {@link Observable} that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicPostObject(final String url, String idColumnName, final JSONObject keyValuePairs,
                                    Class domainClass, Class dataClass, boolean persist, boolean queuable);

    /**
     * Post a HashMap<String, Object> which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<?> dynamicPostList(final String url, String idColumnName, final JSONArray jsonArray,
                                  Class domainClass, Class dataClass, boolean persist, boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicPutObject(final String url, String idColumnName, final JSONObject keyValuePairs,
                                   Class domainClass, Class dataClass, boolean persist, boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a list of ?.
     */
    @NonNull
    Observable<?> dynamicPutList(final String url, String idColumnName, final JSONArray jsonArray,
                                 Class domainClass, Class dataClass, boolean persist, boolean queuable);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link Observable} that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicDeleteCollection(final String url, String idColumnName, final JSONArray jsonArray,
                                          Class dataClass, boolean persist, boolean queuable);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link Observable}
     * that will emit a list of ?.
     */
    @NonNull
    Observable<Boolean> dynamicDeleteAll(Class dataClass);

    /**
     * Search disk with a RealmQuery which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<List> queryDisk(RealmManager.RealmQueryProvider queryFactory, Class domainClass);

    @NonNull
    Observable<?> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                      boolean queuable);

    @NonNull
    Observable<?> dynamicUploadFile(final String url, final File file, String key, HashMap<String, Object> parameter,
                                    boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass);
}
