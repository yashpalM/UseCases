package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */

public interface RepoListView {

    Observable<List> getRepoList();
}
