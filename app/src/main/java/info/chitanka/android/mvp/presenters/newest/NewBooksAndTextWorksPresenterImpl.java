package info.chitanka.android.mvp.presenters.newest;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import info.chitanka.android.api.ChitankaApi;
import info.chitanka.android.mvp.models.NewBooksResult;
import info.chitanka.android.mvp.models.NewTextWorksResult;
import info.chitanka.android.mvp.presenters.BasePresenter;
import info.chitanka.android.mvp.views.NewBooksAndTextWorksView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by joro on 23.01.17.
 */
public class NewBooksAndTextWorksPresenterImpl extends BasePresenter<NewBooksAndTextWorksView> implements NewBooksAndTextWorksPresenter {
    private final ChitankaApi chitankaApi;
    private final Gson gson;

    public NewBooksAndTextWorksPresenterImpl(ChitankaApi chitankaApi, Gson gson) {
        this.chitankaApi = chitankaApi;
        this.gson = gson;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setView(NewBooksAndTextWorksView view) {
        this.view = new WeakReference<>(view);
    }


    @Override
    public void loadNewBooksAndTextworks() {
        chitankaApi.getNewBooksAndTextworks()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                        Type typeOfNewBooks = new TypeToken<LinkedTreeMap<String, List<NewBooksResult>>>() {
                        }.getType();
                        LinkedTreeMap<String, List<NewBooksResult>> booksMap = gson.fromJson(result.getAsJsonObject("book_revisions_by_date"), typeOfNewBooks);

                        Type typeOfNewTexts = new TypeToken<LinkedTreeMap<String, List<NewTextWorksResult>>>() {
                        }.getType();
                        LinkedTreeMap<String, List<NewTextWorksResult>> textsMap = gson.fromJson(result.getAsJsonObject("text_revisions_by_date"), typeOfNewTexts);

                        getView().presentNewBooksAndTextWorks(booksMap, textsMap);
                }, err -> {
                   Timber.e(err);
                });
    }
}
