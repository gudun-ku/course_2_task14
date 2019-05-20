package com.elegion.myfirstapplication.comments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.myfirstapplication.ApiUtils;
import com.elegion.myfirstapplication.App;
import com.elegion.myfirstapplication.R;
import com.elegion.myfirstapplication.model.Album;
import com.elegion.myfirstapplication.model.Comment;
import com.elegion.myfirstapplication.model.User;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import io.reactivex.schedulers.Schedulers;


public class CommentsAlbumFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String ALBUM_KEY = "ALBUM_KEY";

    private CompositeDisposable mSubscriptions;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefresher;
    private View mErrorView;
    private View mDataView;
    private View mNoDataView;
    private Album mAlbum;
    private EditText mPostEdit;
    private Button mPostButton;
    private Toast mToast;
    private User loggedInUser;

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(getContext(),message,Toast.LENGTH_SHORT);
        mToast.show();
    }

    @NonNull
    private final CommentsAdapter mCommmentsAdapter = new CommentsAdapter();

    public static CommentsAlbumFragment newInstance(Album album) {
        Bundle args = new Bundle();
        args.putSerializable(ALBUM_KEY, album);

        CommentsAlbumFragment fragment = new CommentsAlbumFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.recycler);
        mErrorView = view.findViewById(R.id.errorView);

        mPostEdit = view.findViewById(R.id.etComment);
        mPostButton = view.findViewById(R.id.buttonPost);
        mPostButton.setOnClickListener(this);

        mNoDataView = view.findViewById(R.id.no_data);
        mDataView = view.findViewById(R.id.comments_data);

        mRefresher = view.findViewById(R.id.refresher);
        mRefresher.setOnRefreshListener(this);


        //current user
        loggedInUser = ((App) getActivity().getApplication()).getLoggedUser();

        //on enter in edit text
        mPostEdit.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                sendNewComment();
                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAlbum = (Album) getArguments().getSerializable(ALBUM_KEY);

        getActivity().setTitle(mAlbum.getName());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mCommmentsAdapter);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mRefresher.post(new Runnable() {
                            @Override
                            public void run() {
                                mRefresher.setRefreshing(true);
                                getAlbumComments();
                            }
                        }
        );
    }

    private void getAlbumComments() {

        mSubscriptions.add(
                ApiUtils.getApiService()
                .getAlbumComments(mAlbum.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mRefresher.setRefreshing(true);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mRefresher.setRefreshing(false);
                    }
                })
                .subscribe(new Consumer<List<Comment>>() {
                               @Override
                               public void accept(List<Comment> comments) throws Exception {

                                   addCommentsData(comments, true);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mErrorView.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        }
                )
        );


    }

    private void addCommentsData(List<Comment> comments, boolean isRefreshed) {
        if (comments.isEmpty()) {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mDataView.setVisibility(View.GONE);
            mNoDataView.setVisibility(View.VISIBLE);

        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mDataView.setVisibility(View.VISIBLE);
            mNoDataView.setVisibility(View.GONE);
        }

        boolean commentsAdded = mCommmentsAdapter.addData(comments, true);
        if (commentsAdded) {
            showToast("Комментарии обновлены");
        } else {
            showToast("Новых комментариев нет");
        }
    }

    private void getAlbumComment(int commentId) {

        mSubscriptions.add(
                ApiUtils.getApiService()
                .getComment(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mRefresher.setRefreshing(true);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mRefresher.setRefreshing(false);
                    }
                })
                .subscribe(new Consumer<Comment>() {
                               @Override
                               public void accept(Comment comment) throws Exception {
                                   mErrorView.setVisibility(View.GONE);
                                   mRecyclerView.setVisibility(View.VISIBLE);
                                   mCommmentsAdapter.addComment(comment);
                                   mRecyclerView.smoothScrollToPosition(mCommmentsAdapter.getItemCount() - 1);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mErrorView.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        }
                )
        );

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonPost) {
            sendNewComment();
        }
    }

    private void sendNewComment() {
        String newCommentText = mPostEdit.getText().toString();
        if (newCommentText.isEmpty()) {
            showToast(getResources().getString(R.string.no_comment_text));
            return;
        }

        Comment newComment = new Comment(mAlbum.getId(), newCommentText,loggedInUser.getName());


        ApiUtils.getApiService()
        .postComment(newComment)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                mRefresher.setRefreshing(true);
            }
        })
        .doFinally(new Action() {
            @Override
            public void run() throws Exception {
                mRefresher.setRefreshing(false);
                mPostEdit.setText(null);
                mPostEdit.clearFocus();
                hideKeyboardOnEditText(mPostEdit);
            }
        })
        .subscribe(
                new SingleObserver<Comment>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mSubscriptions.add(d);
                    }

                    @Override
                    public void onSuccess(Comment comment) {
                        getAlbumComment(comment.getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mErrorView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
        );



    }

    private void hideKeyboardOnEditText(EditText mEditText) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // Hide the soft keyboard
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(),0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onDestroy() {
        mSubscriptions.clear();
        super.onDestroy();
    }
}

