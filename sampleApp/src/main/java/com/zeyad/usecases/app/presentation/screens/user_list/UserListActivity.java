package com.zeyad.usecases.app.presentation.screens.user_list;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.EmptyViewHolder;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.INITIAL;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity implements LoadDataView<UserListState> {
    public static final int PAGE_SIZE = 6;
    private static final String USER_LIST_MODEL = "userListState";
    @BindView(R.id.imageView_avatar)
    public ImageView imageViewAvatar;
    UserListView userListVM;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.user_list)
    RecyclerView userRecycler;
    GenericRecyclerViewAdapter usersAdapter;
    private boolean twoPane;
    private String currentFragTag;
    private UserListState userListState;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public Bundle saveState() {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(USER_LIST_MODEL, Parcels.wrap(userListState));
        return bundle;
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (userListVM != null)
                userListVM.setCurrentPage(userListState.getCurrentPage());
            renderState(Parcels.unwrap(savedInstanceState.getParcelable(USER_LIST_MODEL)));
        }
    }

    @Override
    public void initialize() {
        viewModel = new UserListVM();
        userListVM = ((UserListVM) viewModel);
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView();
        if (findViewById(R.id.user_detail_container) != null)
            twoPane = true;
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter(getViewContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case R.layout.empty_view:
                        return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.empty_view, parent,
                                false));
                    case R.layout.user_item_layout:
                        return new UserViewHolder(mLayoutInflater.inflate(R.layout.user_item_layout,
                                parent, false));
                    default:
                        return null;
                }
            }
        };
        usersAdapter.setAreItemsClickable(true);
        usersAdapter.setOnItemClickListener((position, itemInfo, holder) -> {
            if (itemInfo.getData() instanceof UserRealm) {
                UserRealm userModel = (UserRealm) itemInfo.getData();
                UserDetailState userDetailModel = UserDetailState.builder()
                        .setUser(userModel)
                        .setIsTwoPane(twoPane)
                        .setState(INITIAL)
                        .build();
                Pair<View, String> pair = null;
                Pair<View, String> secondPair = null;
                if (Utils.hasLollipop()) {
                    UserViewHolder userViewHolder = (UserViewHolder) holder;
                    ImageView avatar = userViewHolder.getAvatar();
                    pair = Pair.create(avatar, avatar.getTransitionName());
                    TextView textViewTitle = userViewHolder.getTextViewTitle();
                    secondPair = Pair.create(textViewTitle, textViewTitle.getTransitionName());
                }
                if (twoPane) {
                    List<Pair<View, String>> pairs = new ArrayList<>();
                    pairs.add(pair);
                    pairs.add(secondPair);
                    if (Utils.isNotEmpty(currentFragTag))
                        removeFragment(currentFragTag);
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userDetailModel);
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
                    addFragment(R.id.user_detail_container, orderDetailFragment, pairs, currentFragTag);
                } else {
                    if (Utils.hasLollipop()) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                                pair, secondPair);
                        navigator.navigateTo(getViewContext(), UserDetailActivity.getCallingIntent(getViewContext(),
                                userDetailModel), options);
                    } else
                        navigator.navigateTo(getViewContext(), UserDetailActivity.getCallingIntent(getViewContext(),
                                userDetailModel));
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getViewContext());
        userRecycler.setLayoutManager(layoutManager);
        userRecycler.setAdapter(usersAdapter);
        userRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if ((layoutManager.getChildCount() + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    userListState = new UserListState.Builder()
                            .setUsers(userListState.getUsers())
                            .setState(userListState.getState())
                            .setError(userListState.getError())
                            .setIsLoading(userListState.isLoading())
                            .setCurrentPage(userListState.getCurrentPage() + 1)
                            .setyScroll(firstVisibleItemPosition)
                            .build();
                    userListVM.incrementPage(usersAdapter.getItem(usersAdapter.getItemCount() - 1).getId());
                }
            }
        });
    }

    @Override
    public void loadData() {
        userListVM.getUsers().compose(bindToLifecycle()).subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
    }

    @Override
    public void renderState(UserListState userListModel) {
        this.userListState = userListModel;
        List<UserRealm> users = userListModel.getUsers();
        if (Utils.isNotEmpty(users)) {
            List<ItemInfo> itemInfos = new ArrayList<>(users.size());
            UserRealm userRealm;
            for (int i = 0, repoModelsSize = users.size(); i < repoModelsSize; i++) {
                userRealm = users.get(i);
                itemInfos.add(new ItemInfo<>(userRealm, R.layout.user_item_layout)
                        .setId(userRealm.getId()));
            }
            usersAdapter.animateTo(itemInfos);
            userRecycler.smoothScrollToPosition(userListModel.getyScroll());
        }
    }

    @Override
    public void showLoading() {
        runOnUiThread(() -> {
            loaderLayout.setVisibility(View.VISIBLE);
            loaderLayout.bringToFront();
        });
    }

    @Override
    public void hideLoading() {
        runOnUiThread(() -> loaderLayout.setVisibility(View.GONE));
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, userRecycler, message, R.string.retry,
                view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, userRecycler, Snackbar.LENGTH_LONG);
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public UserListState getState() {
        return userListState;
    }
}
