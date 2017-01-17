package info.chitanka.android.mvp.presenters.authors;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import info.chitanka.android.api.ChitankaApi;
import info.chitanka.android.mvp.models.Authors;
import info.chitanka.android.mvp.models.Pagination;
import info.chitanka.android.mvp.presenters.BasePresenter;
import info.chitanka.android.mvp.views.AuthorsView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by nmp on 16-3-11.
 */
public class AuthorsPresenterImpl extends BasePresenter<AuthorsView> implements AuthorsPresenter {
    private final ChitankaApi chitankaApi;
    private CompositeSubscription compositeSubscription;

    public AuthorsPresenterImpl(ChitankaApi chitankaApi) {
        this.chitankaApi = chitankaApi;
    }

    @Override
    public void onStart() {
        this.compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    @Override
    public void setView(AuthorsView authorsViewWeakReference) {
        this.view = new WeakReference<>(authorsViewWeakReference);
    }

    @Override
    public void searchAuthors(String name) {
        if (viewExists()) {
            getView().showLoading();
            compositeSubscription.add(
                    chitankaApi.searchAuthors(name)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((authors) -> {
                                if (!viewExists())
                                    return;
                                getView().hideLoading();
                                getView().presentSearch(new Authors(new Pagination(), authors));
                            }, (error) -> {
                                Timber.e(error, "Error printing authors!");
                                if (!viewExists())
                                    return;
                                getView().hideLoading();
                                getView().presentSearch(new Authors(new Pagination(), new ArrayList<>()));
                            })
            );
        }
    }

    @Override
    public void loadAuthors(int page, int pageSize) {
        if (viewExists()) {
            getView().showLoading();
            compositeSubscription.add(chitankaApi.getAuthors(page, pageSize)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((authors) -> {
                        if (!viewExists())
                            return;
                        getView().hideLoading();
                        getView().presentAuthors(authors);
                    }, (error) -> {
                        Timber.e(error, "Error printing authors!");
                        if (!viewExists())
                            return;
                        getView().hideLoading();
                        getView().presentAuthors(new Authors());
                    })
            );
        }
    }
}
