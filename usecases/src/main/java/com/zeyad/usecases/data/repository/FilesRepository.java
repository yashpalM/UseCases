package com.zeyad.usecases.data.repository;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.mappers.DAOMapperFactory;
import com.zeyad.usecases.data.mappers.DefaultDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.domain.repositories.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import rx.Observable;

/**
 * @author by ZIaDo on 11/12/16.
 */
public class FilesRepository implements Files {
    private static Gson mGson;
    private final DataStoreFactory mDataStoreFactory;
    private final IDAOMapperFactory mEntityMapperUtil;

    public FilesRepository() {
        if (Config.getInstance().getDataStoreFactory() == null) {
            mDataStoreFactory = new DataStoreFactory(RestApiImpl.getInstance());
            Config.getInstance().setDataStoreFactory(mDataStoreFactory);
        } else
            mDataStoreFactory = Config.getInstance().getDataStoreFactory();
        mEntityMapperUtil = new DAOMapperFactory() {
            @Override
            public IDAOMapper getDataMapper(Class dataClass) {
                return new DefaultDAOMapper();
            }
        };
        mGson = Config.getGson();
    }

    public FilesRepository(DataStoreFactory dataStoreFactory, DAOMapperFactory daoMapperFactory, Gson gson) {
        if (Config.getInstance().getDataStoreFactory() == null) {
            mDataStoreFactory = dataStoreFactory;
            Config.getInstance().setDataStoreFactory(mDataStoreFactory);
        } else
            mDataStoreFactory = Config.getInstance().getDataStoreFactory();
        mEntityMapperUtil = daoMapperFactory;
        mGson = gson;
    }

    @Override
    public Observable<String> readFromResource(String filePath) {
        return Observable.defer(() -> {
            StringBuilder returnString = new StringBuilder();
            InputStream fIn = null;
            InputStreamReader isr = null;
            BufferedReader input = null;
            try {
                fIn = Config.getInstance().getContext().getResources().getAssets().open(filePath);
                isr = new InputStreamReader(fIn);
                input = new BufferedReader(isr);
                String line;
                while ((line = input.readLine()) != null)
                    returnString.append(line);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            } finally {
                if (isr != null)
                    try {
                        isr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (fIn != null)
                    try {
                        fIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (input != null)
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return Observable.just(returnString.toString());
        });
    }

    @Override
    public Observable<String> readFromFile(String fullFilePath) {
        try {
            return Observable.just(mGson.fromJson(new InputStreamReader(Config.getInstance()
                    .getContext().openFileInput(fullFilePath)), String.class));
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, String data) {
        return Observable.defer(() -> {
            File file = new File(fullFilePath);
            if (!file.exists()) {
                try {
                    final FileWriter writer = new FileWriter(file);
                    writer.write(data);
                    writer.close();
                    return Observable.just(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            }
            return Observable.just(true);
        });
    }

    @NonNull
    @Override
    public Observable<?> uploadFileDynamically(String url, File file, String key, HashMap<String, Object> parameters,
                                               boolean onWifi, boolean whileCharging, boolean queuable,
                                               Class domainClass, Class dataClass) {
        return mDataStoreFactory.cloud(mEntityMapperUtil.getDataMapper(dataClass))
                .dynamicUploadFile(url, file, key, parameters, onWifi, queuable, whileCharging, domainClass);
    }


    @NonNull
    @Override
    public Observable<?> downloadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                                 boolean queuable, Class domainClass, Class dataClass) {
        return mDataStoreFactory.cloud(mEntityMapperUtil.getDataMapper(dataClass))
                .dynamicDownloadFile(url, file, onWifi, whileCharging, queuable);
    }
}
