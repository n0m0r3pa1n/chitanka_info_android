package info.chitanka.app.ui.fragments.books;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import info.chitanka.app.R;
import info.chitanka.app.TrackingConstants;
import info.chitanka.app.components.AnalyticsService;
import info.chitanka.app.mvp.models.Book;
import info.chitanka.app.mvp.views.BooksView;
import info.chitanka.app.ui.adapters.BooksAdapter;
import info.chitanka.app.ui.fragments.BaseFragment;
import info.chitanka.app.utils.IntentUtils;

/**
 * Created by joro on 16-3-20.
 */

public abstract class BaseBooksFragment extends BaseFragment implements BooksView {

    @BindView(R.id.loading)
    CircularProgressBar loading;

    @BindView(R.id.rv_books)
    RecyclerView rvBooks;

    @BindView(R.id.container_empty)
    RelativeLayout containerEmpty;

    @Inject
    AnalyticsService analyticsService;

    @Override
    public void presentAuthorBooks(List<Book> books) {
        if (books == null || books.size() == 0) {
            rvBooks.setVisibility(View.GONE);
            containerEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBooks.setVisibility(View.VISIBLE);
            containerEmpty.setVisibility(View.GONE);
        }

        BooksAdapter adapter = new BooksAdapter(getActivity(), books);
        adapter.getOnWebClick()
                .compose(bindToLifecycle())
                .subscribe(book -> {
                    IntentUtils.openWebUrl(book.getWebChitankaUrl(), getActivity());
                    analyticsService.logEvent(TrackingConstants.CLICK_WEB_BOOKS, new HashMap<String, String>() {{
                        put("bookTitle", book.getTitle());
                    }});
                });

        rvBooks.setAdapter(adapter);
    }

    @Override
    public void hideLoading() {
        loading.progressiveStop();
        loading.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }
}
