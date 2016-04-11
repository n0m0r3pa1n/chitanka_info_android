package info.chitanka.android.mvp.presenters.books;

import info.chitanka.android.api.ChitankaApi;
import info.chitanka.android.mvp.presenters.BasePresenter;
import info.chitanka.android.mvp.views.BooksView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by nmp on 16-3-8.
 */
public class BooksPresenterImpl extends BasePresenter implements BooksPresenter {
    private ChitankaApi chitankaApi;
    private WeakReference<BooksView> view;

    public BooksPresenterImpl(ChitankaApi chitankaApi) {
        this.chitankaApi = chitankaApi;
    }

    @Override
    public void searchBooks(String q) {
        if (!viewExists(view))
            return;

        view.get().showLoading();
        chitankaApi.searchBooks(q).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe((books) -> {
            if (!viewExists(view))
                return;
            view.get().hideLoading();
            view.get().presentAuthorBooks(books.getBooks());
        }, (error) -> {
            Timber.e(error, "Error receiving books!");
            if (!viewExists(view))
                return;
            view.get().hideLoading();
            view.get().presentAuthorBooks(new ArrayList<>());
        });
    }


    @Override
    public void setView(BooksView view) {
        this.view = new WeakReference<>(view);
    }
}