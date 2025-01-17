package com.spmystery.episode.module.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.spmystery.episode.adapter.UserPlayGameAdapter;
import com.spmystery.episode.base.RxLazyFragment;
import com.spmystery.episode.entity.user.UserPlayGameInfo;
import com.spmystery.episode.utils.ConstantUtil;
import com.spmystery.episode.widget.CustomEmptyView;
import com.spmystery.episode.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hcc on 2016/10/12 18:19
 * 100332338@qq.com
 * <p>
 * 用户详情界面的游戏
 */

public class UserPlayGameFragment extends RxLazyFragment {
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    private UserPlayGameInfo userPlayGameInfo;
    private List<UserPlayGameInfo.DataBean.GamesBean> games = new ArrayList<>();


    public static UserPlayGameFragment newInstance(UserPlayGameInfo userPlayGameInfo) {
        UserPlayGameFragment mFragment = new UserPlayGameFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantUtil.EXTRA_DATA, userPlayGameInfo);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_user_play_game;
    }

    @Override
    public void finishCreateView(Bundle state) {
        userPlayGameInfo = getArguments().getParcelable(ConstantUtil.EXTRA_DATA);
        initRecyclerView();
    }


    @Override
    protected void initRecyclerView() {
        games.addAll(userPlayGameInfo.getData().getGames());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        UserPlayGameAdapter mAdapter = new UserPlayGameAdapter(mRecyclerView, games);
        mRecyclerView.setAdapter(mAdapter);
        if (games.isEmpty()) {
            initEmptyLayout();
        }
    }

    private void initEmptyLayout() {
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_space_no_data);
        mCustomEmptyView.setEmptyText("ㄟ( ▔, ▔ )ㄏ 再怎么找也没有啦");
    }
}
