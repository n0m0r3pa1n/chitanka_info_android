package info.chitanka.app.ui.fragments.books;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.chitanka.app.Constants;
import info.chitanka.app.R;
import info.chitanka.app.di.presenters.PresenterComponent;
import info.chitanka.app.events.SearchBookEvent;
import info.chitanka.app.mvp.presenters.books.BooksPresenter;
import info.chitanka.app.mvp.views.BooksView;
import info.chitanka.app.utils.RxBus;

/**
 * A placeholder fragment containing a simple view.
 */
public class BooksFragment extends BaseBooksFragment implements BooksView {
    public static final String TAG = BooksFragment.class.getSimpleName();
    private static final String KEY_QUERY = "query";

    @Inject
    BooksPresenter booksPresenter;

    @Inject
    RxBus rxBus;

    private String query;
    private Unbinder unbinder;

    public BooksFragment() {
    }

    public static BooksFragment newInstance(String searchTerm) {

        Bundle args = new Bundle();
        args.putString(KEY_QUERY, searchTerm);
        BooksFragment fragment = new BooksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        query = getArgument(KEY_QUERY, savedInstanceState);
        if (TextUtils.isEmpty(query)) {
            query = Constants.INITIAL_SEARCH_BOOK_NAME;
        }

        getComponent(PresenterComponent.class).inject(this);

        rxBus.toObserverable()
                .compose(bindToLifecycle())
                .subscribe((event) -> {
                    if (event instanceof SearchBookEvent) {
                        containerEmpty.setVisibility(View.GONE);
                        rvBooks.setVisibility(View.GONE);
                        query = ((SearchBookEvent) event).getName();
                        booksPresenter.searchBooks(query);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        booksPresenter.startPresenting();
        booksPresenter.setView(this);
        booksPresenter.searchBooks(query);
    }

    @Override
    public void onPause() {
        super.onPause();
        booksPresenter.stopPresenting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            rvBooks.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_QUERY, query);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public String getTitle() {
        return TAG;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
