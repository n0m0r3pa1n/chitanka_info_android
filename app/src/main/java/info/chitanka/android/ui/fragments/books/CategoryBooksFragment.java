package info.chitanka.android.ui.fragments.books;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import info.chitanka.android.R;
import info.chitanka.android.di.presenters.PresenterComponent;
import info.chitanka.android.mvp.models.Book;
import info.chitanka.android.mvp.presenters.category_books.CategoryBooksPresenter;
import info.chitanka.android.mvp.views.CategoryBooksView;
import info.chitanka.android.ui.adapters.BooksAdapter;
import info.chitanka.android.ui.fragments.BaseFragment;
import info.chitanka.android.ui.views.containers.ScrollRecyclerView;

/**
 * Created by joro on 16-3-20.
 */
public class CategoryBooksFragment extends BaseFragment implements CategoryBooksView {
    public static final String TAG = CategoryBooksFragment.class.getSimpleName();
    private static final String KEY_SLUG = "slug";

    private int page=1;

    private BooksAdapter adapter;

    @Inject
    CategoryBooksPresenter booksPresenter;

    @Bind(R.id.rv_books)
    ScrollRecyclerView rvBooks;

    @Bind(R.id.loading)
    CircularProgressBar loadingPb;

    @Bind(R.id.container_empty)
    RelativeLayout containerEmpty;


    private String slug;

    public CategoryBooksFragment() {
    }

    public static CategoryBooksFragment newInstance(String slug) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SLUG, slug);
        CategoryBooksFragment fragment = new CategoryBooksFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getComponent(PresenterComponent.class).inject(this);
        booksPresenter.setView(this);
        booksPresenter.onStart();

        slug = getArgument(KEY_SLUG, savedInstanceState);
        booksPresenter.getBooksForCategory(slug, page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_books, container, false);
        ButterKnife.bind(this, view);

        rvBooks.setOnEndReachedListener(() -> {
            page++;
            booksPresenter.getBooksForCategory(slug, page);
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SLUG, slug);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        booksPresenter.setView(null);
        booksPresenter.onDestroy();
    }

    @Override
    public void presentCategoryBooks(List<Book> books, int totalItemCount) {
        if(books.size() == 0) {
            rvBooks.setVisibility(View.GONE);
            containerEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            rvBooks.setVisibility(View.VISIBLE);
            containerEmpty.setVisibility(View.GONE);
        }

        if(adapter == null) {
            adapter= new BooksAdapter(getActivity(), books, getActivity().getSupportFragmentManager());
            rvBooks.setAdapter(adapter, totalItemCount, 20);
        } else {
            adapter.addAll(books);
            rvBooks.getScrollListener().setLoading(false);
        }

        if(adapter.getItemCount() >= totalItemCount) {
            Snackbar.make(rvBooks, getString(R.string.list_loaded), Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void hideLoading() {
        loadingPb.progressiveStop();
        loadingPb.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        loadingPb.setVisibility(View.VISIBLE);
    }

    @Override
    public String getTitle() {
        return TAG;
    }

    public void setSlug(String categorySlug) {
        if (!slug.equals(categorySlug)) {
            slug = categorySlug;
            page = 1;
            adapter = null;
            booksPresenter.getBooksForCategory(categorySlug, page);
        }
    }
}
